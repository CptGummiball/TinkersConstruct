package slimeknights.tconstruct.library.client.model;

import slimeknights.mantle.Mantle;
import slimeknights.mantle.client.model.RetexturedModel;
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

    // Mantle's own loaders. Written here rather than copied, since Mantle's geometry sources are
    // not in this tree; retextured is the one the tables and smeltery components need, and it is
    // what carries a block entity's chosen texture into its model.
    GeometryLoaderRegistry.register(Mantle.getResource("retextured"), RetexturedModel.Geometry.LOADER);

    // Still unwritten: connected (135 models), item_layer (18), nbt_key (2) and colored_block (1).
    // Those fall through to the vanilla parse of their JSON, which is what happened before this
    // bridge existed — a connected texture shows its base variant rather than nothing.

    // must come last: the plugin snapshots nothing, but registering loaders after the first
    // resource reload has begun would silently miss that reload's models
    GeometryModelLoadingPlugin.init();
  }
}
