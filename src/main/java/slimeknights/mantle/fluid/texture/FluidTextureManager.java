package slimeknights.mantle.fluid.texture;

import com.google.gson.JsonElement;
import net.fabricmc.fabric.api.client.model.loading.v1.PreparableModelLoadingPlugin;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import slimeknights.mantle.Mantle;
import slimeknights.mantle.util.JsonHelper;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Loads the textures each fluid renders with.
 *
 * <p>Forge keyed these by its fluid-type registry; that registry has no Fabric counterpart, so
 * the files are keyed by the still fluid instead — the shipped names already match, and on
 * Fabric the fluid is what the renderer is handed anyway. The flowing variant resolves through
 * the same entry, since a flowing fluid shares its source's textures.
 */
public class FluidTextureManager implements SimpleSynchronousResourceReloadListener {
  /** Folder containing the logic */
  public static final String FOLDER = "mantle/fluid_texture";

  private static final FluidTextureManager INSTANCE = new FluidTextureManager();
  /** Map of still fluid to texture */
  private Map<Fluid,FluidTexture> textures = Collections.emptyMap();
  /** Fallback texture instance */
  private static final FluidTexture FALLBACK = new FluidTexture(ResourceLocation.parse("block/water_still"), ResourceLocation.parse("block/water_flow"), null, null, -1);

  private FluidTextureManager() {}

  /**
   * Registers the loaders; call once from the client entrypoint.
   *
   * <p>Loaded twice on purpose. Item models for filled containers bake the fluid's sprite into
   * themselves, and model baking happens before an ordinary reload listener has run — the buckets
   * would bake against the missing texture and keep it until the next reload. The model-loading
   * preparation stage fills the table in time for that; the reload listener then runs on the main
   * thread, where registering render handlers is safe.
   */
  public static void init() {
    PreparableModelLoadingPlugin.register(
      (manager, executor) -> CompletableFuture.runAsync(() -> INSTANCE.loadTextures(manager), executor),
      (ignored, context) -> {});
    ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(INSTANCE);
  }

  @Override
  public ResourceLocation getFabricId() {
    return Mantle.getResource("fluid_texture");
  }

  @Override
  public void onResourceManagerReload(ResourceManager resourceManager) {
    loadTextures(resourceManager);
    // which fluids need a render handler is only known once the textures are read, and the
    // registry is global, so this registers on the first load and is a no-op afterwards
    TextureFluidRenderHandler.register();
  }

  /** Reads the fluid texture files into the lookup table */
  private void loadTextures(ResourceManager resourceManager) {
    long time = System.nanoTime();
    Map<ResourceLocation,JsonElement> jsons = new HashMap<>();
    SimpleJsonResourceReloadListener.scanDirectory(resourceManager, FOLDER, JsonHelper.DEFAULT_GSON, jsons);

    Map<Fluid,FluidTexture> map = new HashMap<>();
    for (Map.Entry<ResourceLocation,JsonElement> entry : jsons.entrySet()) {
      ResourceLocation id = entry.getKey();
      Fluid fluid = BuiltInRegistries.FLUID.get(id);
      // get returns the default (empty) entry for unknown ids, so compare rather than null-check
      if (fluid == Fluids.EMPTY) {
        Mantle.logger.debug("Ignoring fluid texture {} as no fluid exists with that name", id);
      } else {
        map.put(fluid, FluidTexture.deserialize(GsonHelper.convertToJsonObject(entry.getValue(), "fluid_texture")));
      }
    }
    this.textures = map;
    Mantle.logger.info("Loaded {} fluid textures in {} ms", map.size(), (System.nanoTime() - time) / 1000000f);
  }

  /** Gets the texture for the given fluid, falling back to water so lookups never fail */
  public static FluidTexture getData(Fluid fluid) {
    return INSTANCE.textures.getOrDefault(fluid, FALLBACK);
  }

  /** True when the fluid declared its own textures */
  public static boolean hasData(Fluid fluid) {
    return INSTANCE.textures.containsKey(fluid);
  }

  public static ResourceLocation getStillTexture(Fluid fluid) {
    return getData(fluid).still();
  }

  public static ResourceLocation getFlowingTexture(Fluid fluid) {
    return getData(fluid).flowing();
  }

  public static ResourceLocation getOverlayTexture(Fluid fluid) {
    return getData(fluid).overlay();
  }
}
