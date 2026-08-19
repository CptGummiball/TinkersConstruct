package slimeknights.tconstruct.library.client.model;

import slimeknights.mantle.client.model.geometry.GeometryLoaderRegistry;
import slimeknights.mantle.client.model.geometry.GeometryModelLoadingPlugin;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.client.model.block.FluidTextureModel;
import slimeknights.tconstruct.library.client.model.block.TankModel;
import slimeknights.tconstruct.library.client.model.tools.MaterialBlockModel;
import slimeknights.tconstruct.library.client.model.tools.MaterialModel;
import slimeknights.tconstruct.library.client.model.tools.ToolModel;

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

    // smeltery module: SmelteryClientEvents.registerModelLoaders
    GeometryLoaderRegistry.register(TConstruct.getResource("tank"), TankModel.LOADER);
    GeometryLoaderRegistry.register(TConstruct.getResource("fluid_texture"), FluidTextureModel.LOADER);

    // fluids module: FluidClientEvents.registerModelLoaders
    GeometryLoaderRegistry.register(TConstruct.getResource("fluid_container"), FluidContainerModel.LOADER);

    // tools module: ToolClientEvents.registerModelLoaders
    GeometryLoaderRegistry.register(TConstruct.getResource("material"), MaterialModel.LOADER);
    GeometryLoaderRegistry.register(TConstruct.getResource("material_block"), MaterialBlockModel.LOADER);
    GeometryLoaderRegistry.register(TConstruct.getResource("tool"), ToolModel.LOADER);

    // Mantle's own loaders (connected, item_layer, retextured, nbt_key, colored_block) have no
    // geometry source in this tree at all — only the support classes Tinkers' models needed were
    // written — so the 135 mantle:connected and 18 mantle:item_layer models still fall through to
    // the vanilla parse of their JSON, which is what happened before this bridge existed.

    // must come last: the plugin snapshots nothing, but registering loaders after the first
    // resource reload has begun would silently miss that reload's models
    GeometryModelLoadingPlugin.init();
  }
}
