package slimeknights.tconstruct.fabric;

import net.fabricmc.api.ClientModInitializer;
import slimeknights.mantle.fluid.tooltip.FluidTooltipHandler;
import slimeknights.mantle.network.NetworkWrapperClient;
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

    // Custom model geometry: registers the "loader" ids and the Fabric ModelResolver bridge that
    // replaces Forge's patched model deserializer.
    TinkerModelLoaders.init();

    // Render layers: Forge honored a "render_type" key in the model JSON, vanilla and Fabric
    // only know a per-block mapping. This reads the key back off the models so they stay the
    // single source of truth rather than duplicating it in a hand-kept list.
    slimeknights.tconstruct.fabric.client.BlockRenderTypes.init();

    // Menu screens: Forge registered these from FMLClientSetupEvent inside each *ClientEvents
    // class. The resource listeners below back the GUIs (fluid unit tooltips, modifier button
    // icons); Forge registered them from Mantle's client setup and ToolClientEvents.
    FluidTooltipHandler.init();
    ModifierIconManager.init();
    TableClientEvents.init();
    SmelteryClientEvents.init();

    // Further client modules are wired in as each one finishes porting; see PORTING.md.
  }
}
