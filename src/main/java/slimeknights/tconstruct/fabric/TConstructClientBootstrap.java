package slimeknights.tconstruct.fabric;

import net.fabricmc.api.ClientModInitializer;
import slimeknights.mantle.fluid.tooltip.FluidTooltipHandler;
import slimeknights.mantle.network.NetworkWrapperClient;
import slimeknights.tconstruct.library.client.materials.MaterialRenderInfoLoader;
import slimeknights.tconstruct.library.client.model.TinkerModelLoaders;
import slimeknights.tconstruct.library.client.modifiers.ModifierIconManager;
import slimeknights.tconstruct.smeltery.SmelteryClientEvents;
import slimeknights.tconstruct.tables.TableClientEvents;

/**
 * Fabric {@code client} entrypoint, replacing Forge's {@code FMLClientSetupEvent} and the
 * various {@code Register*Event} client hooks.
 */
public class TConstructClientBootstrap implements ClientModInitializer {

  @Override
  public void onInitializeClient() {
    // Server->client packet receivers collect during common init and register here,
    // since ClientPlayNetworking does not exist on a dedicated server.
    NetworkWrapperClient.init();

    // Material render info: which sprite and tint each material draws with. Must be registered
    // before the first resource reload, and loads in a model-loading preparation stage so it is
    // in place before any material model bakes.
    MaterialRenderInfoLoader.init();

    // Custom model geometry: registers the "loader" ids and the Fabric ModelResolver bridge that
    // replaces Forge's patched model deserializer.
    TinkerModelLoaders.init();

    // Render layers: Forge honored a "render_type" key in the model JSON, vanilla and Fabric
    // only know a per-block mapping. This reads the key back off the models so they stay the
    // single source of truth rather than duplicating it in a hand-kept list.
    slimeknights.tconstruct.fabric.client.BlockRenderTypes.init();

    // Fluid rendering: Forge read sprites and tint from a client fluid-type extension; Fabric
    // asks a render handler instead. The manager reads the same generated data and registers
    // the handler once it knows which fluids declared textures.
    slimeknights.mantle.fluid.texture.FluidTextureManager.init();

    // Menu screens: Forge registered these from FMLClientSetupEvent inside each *ClientEvents
    // class. The resource listeners below back the GUIs (fluid unit tooltips, modifier button
    // icons); Forge registered them from Mantle's client setup and ToolClientEvents.
    // both hang off the tag-load event, which fires for every resource and datapack reload:
    // one fills the colour table tooltips read, the other drops the caches built from it
    slimeknights.mantle.client.ResourceColorManager.init();
    slimeknights.tconstruct.library.client.materials.MaterialTooltipCache.init();
    FluidTooltipHandler.init();
    ModifierIconManager.init();
    slimeknights.tconstruct.shared.CommonsClientEvents.init();
    slimeknights.tconstruct.fluids.FluidClientEvents.init();
    TableClientEvents.init();
    SmelteryClientEvents.init();
    // Block entity and entity renderers: Forge registered these from EntityRenderersEvent inside
    // each *ClientEvents class; the two below carry only their renderer registrations so far.
    slimeknights.tconstruct.tools.ToolClientEvents.init();
    slimeknights.tconstruct.gadgets.GadgetClientEvents.init();
    slimeknights.tconstruct.world.WorldClientEvents.init();

    // The guide books. Registration is cheap; the books themselves build lazily on first open,
    // since their content depends on the recipes and datapack registries of the world being played.
    slimeknights.mantle.client.book.BookLoader.init();
    slimeknights.mantle.command.client.MantleClientCommand.init();
    // sprite transformer serializers, read by the part texture generator command
    slimeknights.tconstruct.shared.TinkerClient.init();
    slimeknights.tconstruct.library.client.book.TinkerBook.initBook();
    slimeknights.tconstruct.fabric.client.BookDevHarness.init();
    slimeknights.tconstruct.fabric.client.BlockRenderDevHarness.init();
    slimeknights.tconstruct.fabric.client.CommandDevHarness.init();
    // the EMI harness links against EMI classes, so the flag gate sits out here
    if (Boolean.getBoolean("tconstruct.emiHarness")) {
      slimeknights.tconstruct.fabric.client.EmiDevHarness.init();
    }

    // Further client modules are wired in as each one finishes porting; see PORTING.md.
  }
}
