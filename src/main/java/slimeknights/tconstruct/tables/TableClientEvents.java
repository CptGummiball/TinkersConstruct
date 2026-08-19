package slimeknights.tconstruct.tables;

import net.minecraft.client.gui.screens.MenuScreens;
import slimeknights.tconstruct.common.ClientEventBase;
import slimeknights.tconstruct.tables.client.inventory.CraftingStationScreen;
import slimeknights.tconstruct.tables.client.inventory.ModifierWorktableScreen;
import slimeknights.tconstruct.tables.client.inventory.PartBuilderScreen;
import slimeknights.tconstruct.tables.client.inventory.TinkerChestScreen;
import slimeknights.tconstruct.tables.client.inventory.TinkerStationScreen;

/**
 * Client-side setup for the tables module.
 *
 * <p>Fabric port: Forge ran this from {@code FMLClientSetupEvent} and the various
 * {@code Register*Event} hooks on the mod bus; on Fabric {@link #init()} is called from
 * {@code TConstructClientBootstrap}. Only the menu screen registration is ported so far — the
 * block entity renderers and the tinkers' chest colour handlers wait on the renderer/colour slice.
 */
@SuppressWarnings("unused")
public class TableClientEvents extends ClientEventBase {
  /** Registers the table menu screens */
  public static void init() {
    MenuScreens.register(TinkerTables.craftingStationContainer.get(), CraftingStationScreen::new);
    MenuScreens.register(TinkerTables.tinkerStationContainer.get(), TinkerStationScreen::new);
    MenuScreens.register(TinkerTables.partBuilderContainer.get(), PartBuilderScreen::new);
    MenuScreens.register(TinkerTables.modifierWorktableContainer.get(), ModifierWorktableScreen::new);
    MenuScreens.register(TinkerTables.tinkerChestContainer.get(), TinkerChestScreen::new);
  }

  // PORT: block entity renderers wait on the renderer slice. Forge registered
  //   InventoryBlockEntityRenderer::new for craftingStationTile, tinkerStationTile,
  //   modifierWorktableTile and partBuilderTile through EntityRenderersEvent.RegisterRenderers;
  //   the Fabric equivalent is BlockEntityRenderers.register, but
  //   slimeknights.mantle.client.render.InventoryBlockEntityRenderer is not ported yet.

  // PORT: colour handlers wait on the colour slice. Forge registered a block colour reading
  //   TinkersChestBlockEntity#getColor and an item colour reading DyeableLeatherItem#getColor
  //   (removed in 1.21 in favour of the DYED_COLOR data component) for TinkerTables.tinkersChest,
  //   via RegisterColorHandlersEvent; the Fabric equivalent is ColorProviderRegistry.
}
