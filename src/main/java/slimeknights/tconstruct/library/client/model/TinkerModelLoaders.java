package slimeknights.tconstruct.library.client.model;

import slimeknights.mantle.client.model.geometry.GeometryLoaderRegistry;
import slimeknights.mantle.client.model.geometry.GeometryModelLoadingPlugin;
import slimeknights.tconstruct.TConstruct;

/**
 * Registers Tinkers' custom model geometry with {@link GeometryLoaderRegistry} and installs the
 * Fabric bridge that feeds it.
 *
 * <p>Replaces the {@code ModelEvent.RegisterGeometryLoaders} handlers that were spread across the
 * per-module client event classes on Forge; the ids below match those handlers exactly. Each module's
 * handler is named next to its entry so the registrations can move back out to the module classes
 * once those port.
 */
public final class TinkerModelLoaders {
  private TinkerModelLoaders() {}

  /** Registers the ported loaders, then the plugin that resolves models through them. */
  public static void init() {
    // shared module: CommonsClientEvents.registerModelLoaders
    GeometryLoaderRegistry.register(TConstruct.getResource("gui"), UniqueGuiModel.LOADER);

    // Loaders still parked, listed with what each one waits on (see unported.gradle):
    //   tconstruct:tool             ToolModel            — modifier model system + Forge composite/item-layer baking
    //   tconstruct:material         MaterialModel        — MaterialRenderInfoLoader (needs mantle.data.datamap) + MantleItemLayerModel
    //   tconstruct:material_block   MaterialBlockModel   — SimpleBlockModel retexturing + Forge ModelData
    //   tconstruct:fluid_container  FluidContainerModel  — Forge DynamicFluidContainerModel + IClientFluidTypeExtensions
    //   tconstruct:tank             TankModel            — ColoredBlockModel + Forge ModelData/IQuadTransformer
    //   tconstruct:fluid_texture    FluidTextureModel    — RetexturedModel + Forge ModelData/IQuadTransformer
    // Mantle's own loaders (connected, item_layer, retextured, nbt_key, colored_block) have no
    // source in this tree at all; their geometry classes were never copied in.

    // must come last: the plugin snapshots nothing, but registering loaders after the first
    // resource reload has begun would silently miss that reload's models
    GeometryModelLoadingPlugin.init();
  }
}
