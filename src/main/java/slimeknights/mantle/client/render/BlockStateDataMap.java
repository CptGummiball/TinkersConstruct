package slimeknights.mantle.client.render;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.extern.log4j.Log4j2;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;

import javax.annotation.Nullable;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Function;

/**
 * Resource-pack driven map of block state to some rendering data, keyed by the block's registry id.
 *
 * <p>Backs {@link FluidCuboid#REGISTRY}, {@link RenderItem#STATE_REGISTRY}, {@link FaucetFluid#REGISTRY}
 * and {@link ChannelFluids#REGISTRY}; the JSON these read is already generated into
 * {@code assets/tconstruct/mantle/model/**} by the datagen providers in
 * {@code slimeknights.tconstruct.common.data.render}.
 *
 * <p>File layout, matching that data:
 * <pre>
 * {"variants": {"": &lt;value&gt;, "facing=down": &lt;value&gt;}}   // per state
 * {"parent": "namespace:other_file"}                            // whole file redirect
 * &lt;value&gt;                                                  // bare value, for template files
 * </pre>
 * A variant value may itself be the string id of another file in the same folder, which is how the
 * generated data shares one template between the seared and scorched variants of a block. Keys are
 * vanilla blockstate variant syntax: {@code ""} matches every state and is the fallback, anything
 * else is a comma separated list of {@code property=value} pairs.
 *
 * <p>Fabric port: Forge registered these maps from {@code RegisterClientReloadListenersEvent};
 * {@link #init()} registers with Fabric's {@link ResourceManagerHelper} instead and is safe to call
 * from more than one module.
 */
@Log4j2
public class BlockStateDataMap<T> implements ResourceManagerReloadListener, IdentifiableResourceReloadListener {
  /** Maximum number of redirects to follow before assuming the data loops */
  private static final int MAX_REDIRECTS = 8;

  /** Id used for reload listener ordering */
  private final ResourceLocation id;
  /** Resource folder holding the JSON, without a trailing slash */
  private final String folder;
  /** Parses a resolved JSON value into the data type */
  private final Function<JsonElement,T> parser;
  /** Writes the data type back to JSON; null for maps without a datagen provider */
  @Nullable
  private final Function<T,JsonElement> serializer;

  /** Loaded data; empty until the first resource reload */
  private Map<Block,List<Variant<T>>> entries = Map.of();
  /** Prevents registering the listener twice when several modules initialize the same map */
  private boolean registered = false;

  public BlockStateDataMap(ResourceLocation id, String folder, Function<JsonElement,T> parser) {
    this(id, folder, parser, null);
  }

  public BlockStateDataMap(ResourceLocation id, String folder, Function<JsonElement,T> parser, @Nullable Function<T,JsonElement> serializer) {
    this.id = id;
    this.folder = folder;
    this.parser = parser;
    this.serializer = serializer;
  }

  /** Serializes a value for the datagen provider */
  public JsonElement serialize(T value) {
    if (serializer == null) {
      throw new UnsupportedOperationException("No serializer for " + id);
    }
    return serializer.apply(value);
  }

  /** Gets the resource folder this map reads, used by the datagen provider */
  public String getFolder() {
    return folder;
  }

  /** Registers this map with the client resource manager; safe to call repeatedly */
  public void init() {
    if (!registered) {
      registered = true;
      ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(this);
    }
  }

  @Override
  public ResourceLocation getFabricId() {
    return id;
  }


  /* Lookups */

  /** Gets the value for the given state, or null if the block has no data */
  @Nullable
  public T getNullable(BlockState state) {
    List<Variant<T>> variants = entries.get(state.getBlock());
    if (variants != null) {
      for (Variant<T> variant : variants) {
        if (variant.matches(state)) {
          return variant.value;
        }
      }
    }
    return null;
  }

  /** Gets the value for the given state, falling back to the given value */
  public T get(BlockState state, T fallback) {
    T value = getNullable(state);
    return value != null ? value : fallback;
  }

  /** Gets the value for the block's default state, or null if the block has no data */
  @Nullable
  public T get(Block block) {
    return getNullable(block.defaultBlockState());
  }


  /* Loading */

  @Override
  public void onResourceManagerReload(ResourceManager manager) {
    // first pass: every file in the folder, keyed by its id with the folder and extension trimmed
    String prefix = folder + "/";
    int trimEnd = ".json".length();
    Map<ResourceLocation,JsonElement> files = new HashMap<>();
    for (Entry<ResourceLocation,Resource> entry : manager.listResources(folder, location -> location.getPath().endsWith(".json")).entrySet()) {
      ResourceLocation full = entry.getKey();
      String path = full.getPath();
      ResourceLocation trimmed = ResourceLocation.fromNamespaceAndPath(full.getNamespace(), path.substring(prefix.length(), path.length() - trimEnd));
      try (Reader reader = entry.getValue().openAsReader()) {
        files.put(trimmed, JsonParser.parseReader(reader));
      } catch (Exception e) {
        log.error("Failed to load {} from {}", id, full, e);
      }
    }

    // second pass: files named after a block become entries, everything else is only reachable as a template
    Map<Block,List<Variant<T>>> map = new HashMap<>();
    for (Entry<ResourceLocation,JsonElement> entry : files.entrySet()) {
      Optional<Block> block = BuiltInRegistries.BLOCK.getOptional(entry.getKey());
      if (block.isPresent()) {
        try {
          List<Variant<T>> variants = parseFile(block.get(), entry.getValue(), files);
          if (!variants.isEmpty()) {
            map.put(block.get(), variants);
          }
        } catch (Exception e) {
          log.error("Failed to parse {} for block {}", id, entry.getKey(), e);
        }
      }
    }
    entries = map;
  }

  /** Parses a single file into its list of variants, most specific first */
  private List<Variant<T>> parseFile(Block block, JsonElement element, Map<ResourceLocation,JsonElement> files) {
    JsonElement resolved = resolve(element, files);
    // a bare value applies to every state
    if (!resolved.isJsonObject() || !resolved.getAsJsonObject().has("variants")) {
      return List.of(new Variant<>(List.of(), parser.apply(resolved)));
    }
    // property predicates first so the default "" entry acts as a fallback
    List<Variant<T>> specific = new ArrayList<>();
    List<Variant<T>> fallback = new ArrayList<>();
    StateDefinition<Block,BlockState> definition = block.getStateDefinition();
    for (Entry<String,JsonElement> entry : resolved.getAsJsonObject().getAsJsonObject("variants").entrySet()) {
      T value = parser.apply(resolve(entry.getValue(), files));
      List<PropertyValue<?>> predicate = parsePredicate(definition, entry.getKey());
      (predicate.isEmpty() ? fallback : specific).add(new Variant<>(predicate, value));
    }
    specific.addAll(fallback);
    return specific;
  }

  /** Follows string and {@code parent} redirects to the value they name */
  private static JsonElement resolve(JsonElement element, Map<ResourceLocation,JsonElement> files) {
    for (int i = 0; i < MAX_REDIRECTS; i++) {
      ResourceLocation redirect = null;
      if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isString()) {
        redirect = ResourceLocation.parse(element.getAsString());
      } else if (element.isJsonObject() && element.getAsJsonObject().has("parent")) {
        redirect = ResourceLocation.parse(element.getAsJsonObject().get("parent").getAsString());
      }
      if (redirect == null) {
        return element;
      }
      JsonElement target = files.get(redirect);
      if (target == null) {
        throw new IllegalArgumentException("Missing parent " + redirect);
      }
      element = target;
    }
    throw new IllegalArgumentException("Too many parents, data likely loops");
  }

  /** Parses a vanilla style variant key into the properties it requires */
  private static List<PropertyValue<?>> parsePredicate(StateDefinition<Block,BlockState> definition, String key) {
    if (key.isEmpty()) {
      return List.of();
    }
    List<PropertyValue<?>> predicate = new ArrayList<>();
    for (String pair : key.split(",")) {
      int split = pair.indexOf('=');
      if (split == -1) {
        throw new IllegalArgumentException("Invalid variant key " + key);
      }
      String name = pair.substring(0, split);
      Property<?> property = definition.getProperty(name);
      if (property == null) {
        throw new IllegalArgumentException("Unknown property " + name + " in variant key " + key);
      }
      predicate.add(PropertyValue.of(property, pair.substring(split + 1)));
    }
    return predicate;
  }

  /** One property requirement of a variant key */
  private record PropertyValue<V extends Comparable<V>>(Property<V> property, V value) {
    public static <V extends Comparable<V>> PropertyValue<V> of(Property<V> property, String name) {
      return new PropertyValue<>(property, property.getValue(name).orElseThrow(() -> new IllegalArgumentException("Invalid value " + name + " for property " + property.getName())));
    }

    public boolean matches(BlockState state) {
      return state.getValue(property).equals(value);
    }
  }

  /** One entry of the variants map */
  private record Variant<T>(List<PropertyValue<?>> predicate, T value) {
    public boolean matches(BlockState state) {
      for (PropertyValue<?> property : predicate) {
        if (!property.matches(state)) {
          return false;
        }
      }
      return true;
    }
  }
}
