package slimeknights.tconstruct.library.modifiers;

import com.google.common.annotations.VisibleForTesting;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.tags.TagKey;
import net.minecraft.tags.TagLoader;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.enchantment.Enchantment;
import slimeknights.mantle.event.MinecraftForge;
import slimeknights.mantle.recipe.condition.ConditionHelper;
import slimeknights.mantle.recipe.condition.ICondition;
import slimeknights.mantle.recipe.condition.ICondition.IContext;
import slimeknights.mantle.event.Event;
import slimeknights.mantle.event.EventPriority;
import slimeknights.mantle.data.loadable.field.ContextKey;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.mantle.util.RegistryHelper;
import slimeknights.mantle.util.typed.TypedMap;
import slimeknights.mantle.util.typed.TypedMapBuilder;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.json.JsonRedirect;
import slimeknights.tconstruct.library.modifiers.impl.ComposableModifier;
import slimeknights.tconstruct.library.utils.GenericTagUtil;
import slimeknights.tconstruct.library.utils.JsonUtils;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/** Modifier registry and JSON loader */
@Log4j2
public class ModifierManager extends SimpleJsonResourceReloadListener implements net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener {
  /** Location of dynamic modifiers */
  public static final String FOLDER = "tinkering/modifiers";
  /** Location of modifier tags */
  public static final String TAG_FOLDER = "tinkering/tags/modifiers";

  public static final ResourceLocation ENCHANTMENT_MAP = TConstruct.getResource("tinkering/enchantments_to_modifiers.json");
  /** Registry key to make tag keys */
  public static final ResourceKey<? extends Registry<Modifier>> REGISTRY_KEY = ResourceKey.createRegistryKey(TConstruct.getResource("modifiers"));

  /** GSON instance for loading dynamic modifiers */
  public static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();

  /** @deprecated use {@link ModifierId#EMPTY} */
  @Deprecated
  public static final ModifierId EMPTY = ModifierId.EMPTY;

  /** Singleton instance of the modifier manager */
  public static final ModifierManager INSTANCE = new ModifierManager();

  /** Default modifier to use when a modifier is not found */
  @Getter
  private final Modifier defaultValue;

  /** If true, static modifiers have been registered, so static modifiers can safely be fetched */
  @Getter
  private boolean modifiersRegistered = false;
  /** All modifiers registered directly with the manager */
  @VisibleForTesting
  final Map<ModifierId,Modifier> staticModifiers = new HashMap<>();
  /** Set all modifier types that are expected to load in datapacks */
  private final Set<ModifierId> expectedDynamicModifiers = new HashSet<>();

  /** Modifiers loaded from JSON */
  private Map<ModifierId,Modifier> dynamicModifiers = Collections.emptyMap();
  /** Modifier tags loaded from JSON */
  private Map<TagKey<Modifier>,List<Modifier>> tags = Collections.emptyMap();
  /** Map from modifier to tags on the modifier */
  private Map<ModifierId,Set<TagKey<Modifier>>> reverseTags = Collections.emptyMap();

  /** List of tag to modifier mappings to try */
  private Map<TagKey<Enchantment>, Modifier> enchantmentTagMap = Collections.emptyMap();
  /** Mapping from enchantment to modifiers, for conversions */
  private Map<Enchantment,Modifier> enchantmentMap = Collections.emptyMap();
  /** Enchantment ids parsed from JSON; 1.21 enchantments are a datapack registry, so they resolve once the server registries exist */
  private final List<PendingEnchantment> pendingEnchantments = new ArrayList<>();
  /** Registry names of resolved enchantments, kept for sorted display */
  private Map<Enchantment,ResourceLocation> enchantmentKeys = Collections.emptyMap();
  /** Marks tag expansion done for the current data load */
  private boolean enchantmentsResolved = false;

  /** If true, dynamic modifiers have been loaded from datapacks, so its safe to fetch dynamic modifiers */
  @Getter
  boolean dynamicModifiersLoaded = false;
  private IContext conditionContext = IContext.EMPTY;

  private ModifierManager() {
    super(GSON, FOLDER);
    // create the empty modifier
    defaultValue = new EmptyModifier();
    defaultValue.setId(EMPTY);
    staticModifiers.put(EMPTY, defaultValue);
  }

  /** For internal use only */
  public void init() {
    // Fabric bootstrap runs at what Forge called common setup, so the registration event
    // fires immediately; addons in this jar listen on the shim bus.
    fireRegistryEvent();
    net.fabricmc.fabric.api.resource.ResourceManagerHelper.get(net.minecraft.server.packs.PackType.SERVER_DATA).registerReloadListener(this);
    this.conditionContext = slimeknights.mantle.util.DataLoadedConditionContext.INSTANCE;
    net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
      resolveEnchantmentMappings(server.registryAccess());
      JsonUtils.syncPackets(server, handler.getPlayer(), new UpdateModifiersPacket(this.dynamicModifiers, this.tags, this.enchantmentMap, this.enchantmentTagMap));
    });
    net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents.SERVER_STARTED.register(server -> resolveEnchantmentMappings(server.registryAccess()));
    net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server, resources, success) -> {
      if (success) {
        resolveEnchantmentMappings(server.registryAccess());
        JsonUtils.syncPackets(server, null, new UpdateModifiersPacket(this.dynamicModifiers, this.tags, this.enchantmentMap, this.enchantmentTagMap));
      }
    });
  }

  /** Fires the modifier registry event */
  private void fireRegistryEvent() {
    MinecraftForge.EVENT_BUS.post(new ModifierRegistrationEvent(null));
    modifiersRegistered = true;
  }


  @SuppressWarnings("removal")
  @Override
  protected void apply(Map<ResourceLocation,JsonElement> splashList, ResourceManager pResourceManager, ProfilerFiller pProfiler) {
    long time = System.nanoTime();

    // load modifiers from JSON
    Map<ModifierId,ModifierId> redirects = new HashMap<>();
    this.dynamicModifiers = splashList.entrySet().stream()
                                      .map(entry -> loadModifier(entry.getKey(), entry.getValue().getAsJsonObject(), redirects))
                                      .filter(Objects::nonNull)
                                      .collect(Collectors.toMap(Modifier::getId, mod -> mod));

    // process redirects
    Map<ModifierId,Modifier> resolvedRedirects = new HashMap<>(); // handled as a separate map to prevent redirects depending on order (no double redirects)
    for (Entry<ModifierId, ModifierId> redirect : redirects.entrySet()) {
      ModifierId from = redirect.getKey();
      ModifierId to = redirect.getValue();
      if (!contains(to)) {
        log.error("Invalid modifier redirect {} as modifier {} does not exist", from, to);
      } else {
        resolvedRedirects.put(from, get(to));
      }
    }
    int modifierSize = this.dynamicModifiers.size();
    this.dynamicModifiers.putAll(resolvedRedirects);

    // validate required modifiers
    for (ModifierId id : expectedDynamicModifiers) {
      if (!dynamicModifiers.containsKey(id)) {
        log.error("Missing expected modifier '{}'", id);
      }
    }
    for (ModifierId id : staticModifiers.keySet()) {
      if (dynamicModifiers.containsKey(id)) {
        if (!net.fabricmc.loader.api.FabricLoader.getInstance().isDevelopmentEnvironment()) {
          log.warn("Dynamic modifier {} is replacing static modifier with the same ID. The ability to do this may be removed in a future version, so if this is intentional please open an issue report with reasoning..", id);
        } else {
          log.error("Dynamic modifier {} is replacing static modifier with the same ID. This is likely a bug with your mod, but on the chance its intentional this error does become just a warning at runtime.", id);
        }
      }
    }

    // TODO: this should be set back to false at some point
    dynamicModifiersLoaded = true;
    long timeStep = System.nanoTime();
    log.info("Loaded {} dynamic modifiers and {} modifier redirects in {} ms", modifierSize, redirects.size(), (timeStep - time) / 1000000f);
    time = timeStep;

    // load modifier tags
    TagLoader<Modifier> tagLoader = new TagLoader<>(id -> {
      Modifier modifier = ModifierManager.getValue(new ModifierId(id));
      // only allow the default modifier if it's explicitly set to empty
      if (modifier == defaultValue && !id.equals(EMPTY)) {
        return Optional.empty();
      }
      return Optional.of(modifier);
    }, TAG_FOLDER);
    this.tags = GenericTagUtil.mapLoaderResults(REGISTRY_KEY, tagLoader.loadAndBuild(pResourceManager));
    this.reverseTags = GenericTagUtil.reverseTags(Modifier::getId, tags);
    timeStep = System.nanoTime();
    log.info("Loaded {} modifier tags for {} modifiers in {} ms", tags.size(), this.reverseTags.size(), (timeStep - time) / 1000000f);

    // load modifier to enchantment mapping
    enchantmentMap = new HashMap<>();
    this.enchantmentTagMap = new LinkedHashMap<>();
    this.pendingEnchantments.clear();
    this.enchantmentsResolved = false;
    for (Resource resource : pResourceManager.getResourceStack(ENCHANTMENT_MAP)) {
      JsonObject enchantmentJson = JsonHelper.getJson(resource, ENCHANTMENT_MAP);
      if (enchantmentJson != null) {
        for (Entry<String,JsonElement> entry : enchantmentJson.entrySet()) {
          try {
            // parse the modifier first, its the same in both cases
            String key = entry.getKey();

            // if the modifier ends with a ?, its optional, so suppress errors if missing
            String modifierStr = GsonHelper.convertToString(entry.getValue(), key);
            boolean optional = modifierStr.charAt(modifierStr.length() - 1) == '?';
            if (optional) {
              modifierStr = modifierStr.substring(0, modifierStr.length() - 1);
            }
            ModifierId modifierId = ModifierId.PARSER.parseString(modifierStr, key);
            Modifier modifier = get(modifierId);
            if (modifier == defaultValue) {
              if (optional) {
                TConstruct.LOG.debug("Skipping unknown optional modifier " + modifierId + " for enchantment " + key);
                continue;
              }
              throw new JsonSyntaxException("Unknown modifier " + modifierId + " for enchantment " + key);
            }

            // if it starts with #, it's a tag
            if (key.charAt(0) == '#') {
              ResourceLocation tagId = ResourceLocation.tryParse(key.substring(1));
              if (tagId == null) {
                throw new JsonSyntaxException("Invalid enchantment tag ID " + key.substring(1));
              }
              this.enchantmentTagMap.put(TagKey.create(Registries.ENCHANTMENT, tagId), modifier);
            } else {
              // if it ends with a ?, its an optional enchantment, so suppress errors on missing
              optional = key.charAt(key.length() - 1) == '?';
              if (optional) {
                key = key.substring(0, key.length() - 1);
              }
              ResourceLocation enchantmentId = ResourceLocation.tryParse(key);
              if (enchantmentId == null) {
                throw new JsonSyntaxException("Invalid enchantment ID " + key + " for modifier " + modifierId);
              }
              // enchantments are a datapack registry in 1.21; resolve once server registries exist
              pendingEnchantments.add(new PendingEnchantment(enchantmentId, optional, modifier, key));
            }
          } catch (RuntimeException e) {
            log.info("Invalid enchantment to modifier mapping", e);
          }
        }
      }
    }
    log.info("Loaded {} enchantment to modifier mappings in {} ms", enchantmentMap.size() + enchantmentTagMap.size(), (System.nanoTime() - timeStep) / 1000000f);

    MinecraftForge.EVENT_BUS.post(new ModifiersLoadedEvent());
  }

  /** Creates context for modifier parsing */
  public static TypedMapBuilder contextBuilder(ResourceLocation modifier) {
    return TypedMapBuilder.builder().put(ContextKey.ID, modifier).put(ContextKey.DEBUG, "Modifier " + modifier);
  }

  /** @deprecated use {@link #contextBuilder(ResourceLocation)} */
  @Deprecated(forRemoval = true)
  public static TypedMap createContext(ResourceLocation modifier) {
    return contextBuilder(modifier).build();
  }

  /** Loads a modifier from JSON */
  @Nullable
  private Modifier loadModifier(ResourceLocation key, JsonElement element, Map<ModifierId, ModifierId> redirects) {
    try {
      JsonObject json = GsonHelper.convertToJsonObject(element, "modifier");

      // processed first so a modifier can both conditionally redirect and fallback to a conditional modifier
      if (json.has("redirects")) {
        for (JsonRedirect redirect : JsonHelper.parseList(json, "redirects", JsonRedirect::fromJson)) {
          ICondition redirectCondition = redirect.getCondition();
          if (redirectCondition == null || redirectCondition.test(conditionContext)) {
            ModifierId redirectTarget = new ModifierId(redirect.getId());
            log.debug("Redirecting modifier {} to {}", key, redirectTarget);
            redirects.put(new ModifierId(key), redirectTarget);
            return null;
          }
        }
      }

      // conditions
      if (json.has("condition") && !ConditionHelper.getCondition(GsonHelper.getAsJsonObject(json, "condition")).test(conditionContext)) {
        return null;
      }

      // fallback to actual modifier
      Modifier modifier = ComposableModifier.LOADER.deserialize(json, contextBuilder(key).put(ContextKey.CONDITION_CONTEXT, conditionContext).build());
      modifier.setId(new ModifierId(key));
      return modifier;
    } catch (JsonSyntaxException e) {
      log.error("Failed to load modifier {}", key, e);
      return null;
    }
  }

  /** Updates the modifiers from the server */
  void updateModifiersFromServer(Map<ModifierId,Modifier> modifiers, Map<TagKey<Modifier>,List<Modifier>> tags, Map<Enchantment,Modifier> enchantmentMap, Map<TagKey<Enchantment>,Modifier> enchantmentTagMappings) {
    this.dynamicModifiers = modifiers;
    this.dynamicModifiersLoaded = true;
    this.tags = tags;
    this.reverseTags = GenericTagUtil.reverseTags(Modifier::getId, tags);
    this.enchantmentMap = enchantmentMap;
    this.enchantmentTagMap = enchantmentTagMappings;
    MinecraftForge.EVENT_BUS.post(new ModifiersLoadedEvent());
  }


  /* Query the registry */

  /** Fetches a static modifier by ID, only use if you need access to modifiers before the world loads*/
  public Modifier getStatic(ModifierId id) {
    return staticModifiers.getOrDefault(id, defaultValue);
  }

  /** Checks if the given static modifier exists */
  public boolean containsStatic(ModifierId id) {
    return staticModifiers.containsKey(id) || expectedDynamicModifiers.contains(id);
  }

  /** Checks if the registry contains the given modifier */
  public boolean contains(ModifierId id) {
    return staticModifiers.containsKey(id) || dynamicModifiers.containsKey(id);
  }

  /** Gets the modifier for the given ID */
  public Modifier get(ModifierId id) {
    // highest priority is static modifiers, cannot be replaced
    Modifier modifier = staticModifiers.get(id);
    if (modifier != null) {
      return modifier;
    }
    // second priority is dynamic modifiers, fallback to the default
    return dynamicModifiers.getOrDefault(id, defaultValue);
  }

  /**
   * Gets the modifier for a given enchantment. Not currently synced to client side
   * @param enchantment  Enchantment
   * @return Closest modifier to the enchantment, or null if no match
   */
  @SuppressWarnings("deprecation")  // eventually it won't be if we move away from forge
  @Nullable
  public Modifier get(Enchantment enchantment) {
    // tag mappings expand into the map on resolve, so a plain lookup covers both
    return enchantmentMap.get(enchantment);
  }

  /** Resolves parsed enchantment mappings against the world enchantment registry; runs once per data load */
  private void resolveEnchantmentMappings(net.minecraft.core.RegistryAccess registryAccess) {
    if (enchantmentsResolved) {
      return;
    }
    enchantmentsResolved = true;
    Registry<Enchantment> registry = registryAccess.registryOrThrow(Registries.ENCHANTMENT);
    for (PendingEnchantment pending : pendingEnchantments) {
      Enchantment enchantment = registry.get(pending.id());
      if (enchantment == null) {
        if (pending.optional()) {
          TConstruct.LOG.debug("Skipping unknown optional enchantment {} for modifier mapping", pending.debugKey());
        } else {
          log.error("Invalid enchantment ID {} in modifier mapping", pending.debugKey());
        }
      } else {
        enchantmentMap.put(enchantment, pending.modifier());
      }
    }
    pendingEnchantments.clear();
    // expand tag mappings so gameplay queries need no registry access; explicit entries win
    for (Entry<TagKey<Enchantment>,Modifier> mapping : enchantmentTagMap.entrySet()) {
      for (net.minecraft.core.Holder<Enchantment> holder : registry.getTagOrEmpty(mapping.getKey())) {
        enchantmentMap.putIfAbsent(holder.value(), mapping.getValue());
      }
    }
    // capture names for sorted display
    Map<Enchantment,ResourceLocation> keys = new java.util.IdentityHashMap<>();
    for (Enchantment enchantment : enchantmentMap.keySet()) {
      ResourceLocation id = registry.getKey(enchantment);
      if (id != null) {
        keys.put(enchantment, id);
      }
    }
    this.enchantmentKeys = keys;
  }

  /** Enchantment mapping waiting on the datapack registry */
  private record PendingEnchantment(ResourceLocation id, boolean optional, Modifier modifier, String debugKey) {}

  /** Checks if the given modifier has an enchantment equivelent */
  public boolean hasEnchantment(Modifier modifier) {
    return enchantmentMap.containsValue(modifier) || enchantmentTagMap.containsValue(modifier);
  }

  /** Gets a stream of all enchantments that match the given modifiers */
  @SuppressWarnings("deprecation")  // eventually it won't be if we move away from forge
  public Stream<Enchantment> getEquivalentEnchantments(Predicate<ModifierId> modifiers) {
    Predicate<Entry<?,Modifier>> predicate = entry -> modifiers.test(entry.getValue().getId());
    // tag mappings are expanded into the map on resolve, so the map is the full set
    return enchantmentMap.entrySet().stream().filter(predicate).map(Entry::getKey)
      .distinct().sorted(Comparator.comparing(enchantment -> enchantmentKeys.getOrDefault(enchantment, EMPTY)));
  }

  /** Gets a list of all modifier IDs */
  public Stream<ResourceLocation> getAllLocations() {
    // filter out redirects (redirects are any modifiers where the ID does not match the key
    return Stream.concat(staticModifiers.entrySet().stream(), dynamicModifiers.entrySet().stream())
                 .filter(entry -> entry.getKey().equals(entry.getValue().getId()))
                 .map(Entry::getKey);
  }

  /** Gets a stream of all modifier values */
  public Stream<Modifier> getAllValues() {
    return Stream.concat(staticModifiers.values().stream(), dynamicModifiers.values().stream()).distinct();
  }


  /* Helpers */

  /** Gets the modifier for the given ID */
  public static Modifier getValue(ModifierId name) {
    return INSTANCE.get(name);
  }


  /* Tags */

  /** Creates a tag key for a modifier */
  public static TagKey<Modifier> getTag(ResourceLocation id) {
    return TagKey.create(REGISTRY_KEY, id);
  }

  /** Gets the set of tags on a modifier */
  public static Stream<TagKey<Modifier>> getTagKeys(ModifierId modifier) {
    return INSTANCE.reverseTags.getOrDefault(modifier, Set.of()).stream();
  }

  /**
   * Checks if the given modifier is in the given tag
   * @return  True if the modifier is in the tag
   */
  public static boolean isInTag(ModifierId modifier, TagKey<Modifier> tag) {
    return INSTANCE.reverseTags.getOrDefault(modifier, Set.of()).contains(tag);
  }

  /**
   * Gets all values contained in the given tag
   * @param tag  Tag instance
   * @return  Contained values, or null if the tag is absent
   */
  @Nullable
  public static List<Modifier> getTagOrNull(TagKey<Modifier> tag) {
    return INSTANCE.tags.get(tag);
  }

  /**
   * Gets all values contained in the given tag
   * @param tag  Tag instance
   * @return  Contained values
   */
  public static List<Modifier> getTagValues(TagKey<Modifier> tag) {
    return INSTANCE.tags.getOrDefault(tag, List.of());
  }

  /** Gets a stream of all tag ID to tag value mappings */
  public static Stream<Entry<TagKey<Modifier>,List<Modifier>>> getAllTags() {
    return INSTANCE.tags.entrySet().stream();
  }


  /* Events */

  /** Event for registering modifiers */
  @RequiredArgsConstructor(access = AccessLevel.PROTECTED)
  public class ModifierRegistrationEvent extends Event {
    /** Container receiving this event; null on Fabric where registration is a single broadcast */
    @Nullable
    private final Object container;

    /** Validates the namespace of the container registering */
    private void checkModNamespace(ResourceLocation name) {
      // check mod container, should be the active mod
      // don't want mods registering stuff in Tinkers namespace, or Minecraft
      // Fabric has no active-mod context on the shim bus, so the event may carry null
      if (container == null) {
        return;
      }
      String activeMod = container.toString();
      if (!name.getNamespace().equals(activeMod)) {
        TConstruct.LOG.warn("Potentially Dangerous alternative prefix for name `{}`, expected `{}`. This could be a intended override, but in most cases indicates a broken mod.", name, activeMod);
      }
    }

    /**
     * Registers a static modifier with the manager. Static modifiers cannot be configured by datapacks, so its generally encouraged to use dynamic modifiers
     * @param name      Modifier name
     * @param modifier  Modifier instance
     */
    public void registerStatic(ModifierId name, Modifier modifier) {
      checkModNamespace(name);

      // should not include under both types
      if (expectedDynamicModifiers.contains(name)) {
        throw new IllegalArgumentException(name + " is already expected as a dynamic modifier");
      }

      // set the name and register it
      modifier.setId(name);
      Modifier existing = staticModifiers.putIfAbsent(name, modifier);
      if (existing != null) {
        throw new IllegalArgumentException("Attempting to register a duplicate static modifier, this is not supported. Original value " + existing);
      }
    }

    /**
     * Registers that the given modifier is expected to be loaded in datapacks
     * @param name  Modifier name
     */
    public void registerExpected(ModifierId name) {
      checkModNamespace(name);

      // should not include under both types
      if (staticModifiers.containsKey(name)) {
        throw new IllegalArgumentException(name + " is already registered as a static modifier");
      }
      // register it
      expectedDynamicModifiers.add(name);
    }
  }

  /** Event fired when modifiers reload */
  public static class ModifiersLoadedEvent extends Event {}

  /** Class for the empty modifier instance, mods should not need to extend this class */
  private static class EmptyModifier extends Modifier {
    @Override
    public boolean shouldDisplay(boolean advanced) {
      return false;
    }
  }

  @Override
  public net.minecraft.resources.ResourceLocation getFabricId() {
    return slimeknights.tconstruct.TConstruct.getResource("modifiers");
  }
}
