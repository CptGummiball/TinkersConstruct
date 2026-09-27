package slimeknights.tconstruct.library.client.model;

import slimeknights.mantle.client.model.util.SimpleBlockModel;
import slimeknights.mantle.client.model.util.MantleItemLayerModel;
import slimeknights.mantle.client.model.util.ColoredBlockModel;
import slimeknights.mantle.client.model.geometry.IGeometryLoader;
import slimeknights.mantle.client.model.connected.ConnectedModel;
import slimeknights.mantle.client.model.NBTKeyModel;
import slimeknights.mantle.client.model.CompositeModel;
import net.minecraft.resources.ResourceLocation;
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

    // Mantle's own loaders, registered here because Mantle registered them from its own client
    // setup, which is not in this tree. Retextured carries a block entity's chosen texture into
    // its model; connected is the borderless glass; item_layer and nbt_key are item models with
    // baked colour/glow and data-selected textures; colored_block is the glowing storage block.
    GeometryLoaderRegistry.register(Mantle.getResource("retextured"), RetexturedModel.Geometry.LOADER);
    GeometryLoaderRegistry.register(Mantle.getResource("connected"), ConnectedModel.LOADER);
    GeometryLoaderRegistry.register(Mantle.getResource("item_layer"), MantleItemLayerModel.LOADER);
    GeometryLoaderRegistry.register(Mantle.getResource("nbt_key"), NBTKeyModel.LOADER);
    // no LOADER constant on ColoredBlockModel: IGeometryLoader's self type does not fit a subclass
    // of SimpleBlockModel, so the reference is typed at the registration instead
    GeometryLoaderRegistry.register(Mantle.getResource("colored_block"), (IGeometryLoader<SimpleBlockModel>) ColoredBlockModel::deserialize);

    // the slime-metal storage blocks pair an opaque frame with a translucent overlay through
    // Forge's composite loader, so that id is served too
    GeometryLoaderRegistry.register(ResourceLocation.fromNamespaceAndPath("forge", "composite"), CompositeModel.LOADER);

    // must come last: the plugin snapshots nothing, but registering loaders after the first
    // resource reload has begun would silently miss that reload's models
    GeometryModelLoadingPlugin.init();
  }
}
