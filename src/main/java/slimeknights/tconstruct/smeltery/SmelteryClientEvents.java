package slimeknights.tconstruct.smeltery;

import net.minecraft.client.gui.screens.MenuScreens;
import slimeknights.tconstruct.common.ClientEventBase;
import slimeknights.tconstruct.smeltery.client.screen.AlloyerScreen;
import slimeknights.tconstruct.smeltery.client.screen.HeatingStructureScreen;
import slimeknights.tconstruct.smeltery.client.screen.MelterScreen;
import slimeknights.tconstruct.smeltery.client.screen.SingleItemScreenFactory;

/**
 * Client-side setup for the smeltery module.
 *
 * <p>Fabric port: Forge ran this from {@code FMLClientSetupEvent} and the {@code Register*Event}
 * hooks on the mod bus; on Fabric {@link #init()} is called from {@code TConstructClientBootstrap}.
 * Only the menu screen registration is ported so far — the block entity renderers, the fluid
 * reload listeners and the item-display/model-loader registrations wait on later slices.
 */
@SuppressWarnings("unused")
public class SmelteryClientEvents extends ClientEventBase {
  /** Registers the smeltery menu screens */
  public static void init() {
    MenuScreens.register(TinkerSmeltery.melterContainer.get(), MelterScreen::new);
    MenuScreens.register(TinkerSmeltery.smelteryContainer.get(), HeatingStructureScreen::new);
    MenuScreens.register(TinkerSmeltery.singleItemContainer.get(), new SingleItemScreenFactory());
    MenuScreens.register(TinkerSmeltery.alloyerContainer.get(), AlloyerScreen::new);
  }

  // PORT: block entity renderers wait on the renderer slice. Forge registered, through
  //   EntityRenderersEvent.RegisterRenderers: TankBlockEntityRenderer (tank, alloyer),
  //   TankInventoryBlockEntityRenderer (fluidCannon, melter, castingTank),
  //   FaucetBlockEntityRenderer, ChannelBlockEntityRenderer, GaugeBlockEntityRenderer,
  //   CastingBlockEntityRenderer (table, basin), ProxyTankBlockEntityRenderer and
  //   HeatingStructureBlockEntityRenderer (smeltery, foundry). Fabric uses
  //   BlockEntityRenderers.register.

  // PORT: FaucetFluid/ChannelFluids reload listeners (mantle.client.render) wait on the same
  //   renderer slice; Forge registered them from RegisterClientReloadListenersEvent.

  // PORT: ToolModel.registerSmallTool for MELTER, CASTING_BASIN and CASTING_TABLE, plus the
  //   "tank" and "fluid_texture" geometry loaders, belong to the model slice.
}
