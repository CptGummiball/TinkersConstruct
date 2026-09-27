package slimeknights.tconstruct.library.client.materials;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import lombok.extern.log4j.Log4j2;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.fabricmc.fabric.api.client.model.loading.v1.PreparableModelLoadingPlugin;
import slimeknights.mantle.data.listener.IEarlySafeManagerReloadListener;
import slimeknights.mantle.data.loadable.field.ContextKey;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.mantle.util.typed.TypedMap;
import slimeknights.mantle.util.typed.TypedMapBuilder;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.utils.Util;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * Loads the material render info from resource packs. Loaded independently of materials loaded in data packs, so a resource needs to exist in both lists to be used.
 * See {@link slimeknights.tconstruct.library.materials.stats.MaterialStatsManager} for stats.
 * <p>
 * The location inside resource packs is "tinkering/materials".
 * So if your mods name is "foobar", the location for your mods materials is "assets/foobar/tinkering/materials".
 */
@Log4j2
public class MaterialRenderInfoLoader implements IEarlySafeManagerReloadListener {
  public static final MaterialRenderInfoLoader INSTANCE = new MaterialRenderInfoLoader();

  /** Folder to scan for material render info JSONS */
  public static final String FOLDER = "tinkering/materials";

  /**
   * Called from the client entrypoint to hook the render infos into the resource reload.
   *
   * <p>The timing is the whole difficulty. Render infos decide which material sprite a tool part
   * bakes with, so they have to be loaded before models bake — and a listener registered the
   * ordinary way runs long after that, because vanilla's own {@code ModelManager} is registered
   * first. Forge worked around it by hanging the load off {@code ModelEvent.RegisterAdditional}.
   *
   * <p>Fabric has a hook meant for exactly this: the preparation stage of a
   * {@link PreparableModelLoadingPlugin} is handed the reload's own resource manager and completes
   * before any model is resolved. That is the same seam the geometry bridge uses, and the two are
   * order-independent — geometry parsing never reads a render info, while every read of one happens
   * at bake time, after both preparation stages are done.
   */
  public static void init()  {
    PreparableModelLoadingPlugin.register(
      (manager, executor) -> CompletableFuture.runAsync(() -> INSTANCE.onReloadSafe(manager), executor),
      (ignored, context) -> {});
  }

  /** Map of all loaded materials */
  private Map<MaterialVariantId,MaterialRenderInfo> renderInfos = ImmutableMap.of();

  private MaterialRenderInfoLoader() {}

  /**
   * Gets a list of all loaded materials render infos
   * @return  All loaded material render infos
   */
  public Collection<MaterialRenderInfo> getAllRenderInfos() {
    return renderInfos.values();
  }

  /**
   * Gets the render info for the given material
   * @param variantId  Material loaded
   * @return  Material render info
   */
  public Optional<MaterialRenderInfo> getRenderInfo(MaterialVariantId variantId) {
    // if there is a variant, try fetching for the variant
    if (variantId.hasVariant()) {
      MaterialRenderInfo info = renderInfos.get(variantId);
      if (info != null) {
        return Optional.of(info);
      }
    }
    // no variant or the variant was not found? default to the material
    return Optional.ofNullable(renderInfos.get(variantId.getId()));
  }

  /** Gets the variant for the given render info path */
  public static MaterialVariantId variant(ResourceLocation location) {
    String path = location.getPath();

    // locate variant as a subfolder, and create final ID
    String variant = "";
    int slashIndex = path.lastIndexOf('/');
    if (slashIndex >= 0) {
      variant = path.substring(slashIndex + 1);
      path = path.substring(0, slashIndex);
    }
    return MaterialVariantId.create(location.getNamespace(), path, variant);
  }

  /** Creates the context for the render info parser */
  public static TypedMap createContext(MaterialVariantId id) {
    return TypedMapBuilder.builder().put(MaterialVariantId.CONTEXT_KEY, id).put(ContextKey.DEBUG, "Material Render Info " + id).build();
  }

  @Override
  public void onReloadSafe(ResourceManager manager) {
    // first, we need to fetch all relevant JSON files
    Map<ResourceLocation,JsonElement> jsons = new HashMap<>();
    SimpleJsonResourceReloadListener.scanDirectory(manager, FOLDER, JsonHelper.DEFAULT_GSON, jsons);
    // final result map
    Map<MaterialVariantId,MaterialRenderInfo> map = new HashMap<>();

    // iterate the files, handling parenting thanks to the data map loader
    for(Entry<ResourceLocation, JsonElement> entry : jsons.entrySet()) {
      // clean up ID by trimming off the extension and folder
      ResourceLocation location = entry.getKey();
      MaterialVariantId id = variant(location);

      // read in the JSON data
      try  {
        JsonObject json = GsonHelper.convertToJsonObject(entry.getValue(), location.toString());
        // skip empty objects, its the way to disable it
        if (json.keySet().isEmpty()) {
          continue;
        }
        // parse it into material render info, folding in any parent first
        map.put(id, MaterialRenderInfo.LOADABLE.deserialize(resolveParents(jsons, location, json, new HashSet<>()), createContext(id)));
      } catch (IllegalArgumentException | JsonParseException ex) {
        log.error("Couldn't parse data file {} from {}", id, location, ex);
      }
    }

    // store the list immediately, otherwise it is not in place in time for models to load
    this.renderInfos = Map.copyOf(map);
    log.debug("Loaded material render infos: {}", Util.toIndentedStringList(map.keySet().stream().sorted(Comparator.comparing(MaterialVariantId::getId).thenComparing(MaterialVariantId::getVariant)).toList()));
    log.info("{} material render infos loaded", map.size());
  }


  /**
   * Folds a render info's {@code "parent"} chain into a single object, child keys winning.
   *
   * <p>25 of the 104 shipped render infos use it — the stone and slime variants mostly, which
   * inherit a palette and override only the texture. Upstream this was Mantle's
   * {@code RegistryDataMapLoader.parseData}, whose package was never copied into this tree; the
   * merge is shallow because the render info schema is flat (texture, fallbacks, color,
   * luminosity), so there is no nested object for a deep merge to reach.
   *
   * @param jsons     Every render info in the reload, keyed as the parent references them
   * @param location  Id of the file being resolved, for error messages
   * @param json      The file's own contents
   * @param seen      Ids already visited on this chain, guarding against a parent loop
   */
  private static JsonObject resolveParents(Map<ResourceLocation,JsonElement> jsons, ResourceLocation location, JsonObject json, Set<ResourceLocation> seen) {
    if (!json.has("parent")) {
      return json;
    }
    ResourceLocation parentId = ResourceLocation.tryParse(GsonHelper.getAsString(json, "parent"));
    if (parentId == null) {
      throw new JsonParseException("Invalid parent in material render info " + location);
    }
    if (!seen.add(parentId)) {
      throw new JsonParseException("Parent loop in material render info " + location + " at " + parentId);
    }
    JsonElement parentElement = jsons.get(parentId);
    if (parentElement == null) {
      throw new JsonParseException("Missing parent " + parentId + " for material render info " + location);
    }
    JsonObject parent = resolveParents(jsons, parentId, GsonHelper.convertToJsonObject(parentElement, parentId.toString()), seen);
    JsonObject merged = parent.deepCopy();
    merged.remove("parent");
    for (Entry<String,JsonElement> entry : json.entrySet()) {
      if (!"parent".equals(entry.getKey())) {
        merged.add(entry.getKey(), entry.getValue());
      }
    }
    return merged;
  }


  /* Helpers */

  /** Checks if the given material has any of the given fallbacks. Used by {@link slimeknights.tconstruct.library.client.armor.texture.MaterialHasFallbackTextureSupplier} and {@link slimeknights.tconstruct.library.client.modifiers.model.MaterialHasFallbackModifierModel} */
  public boolean hasFallback(MaterialVariantId material, Set<String> fallbacks) {
    MaterialRenderInfo info = MaterialRenderInfoLoader.INSTANCE.getRenderInfo(material).orElse(null);
    if (info != null) {
      for (String fallback : info.fallbacks()) {
        if (fallbacks.contains(fallback)) {
          return true;
        }
      }
    }
    return false;
  }
}
