package slimeknights.tconstruct.tables;

import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import slimeknights.mantle.client.render.InventoryBlockEntityRenderer;
import slimeknights.mantle.client.render.RenderItem;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.world.item.component.DyedItemColor;
import slimeknights.tconstruct.tables.block.entity.chest.TinkersChestBlockEntity;
import slimeknights.tconstruct.common.ClientEventBase;
import slimeknights.tconstruct.library.TinkerItemDisplays;
import slimeknights.tconstruct.shared.block.entity.TableBlockEntity;
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
 * {@code TConstructClientBootstrap}. The menu screens and the block entity renderers are ported;
 * the tinkers' chest colour handlers wait on the colour slice.
 */
@SuppressWarnings("unused")
public class TableClientEvents extends ClientEventBase {
  /** Registers the table menu screens */
  public static void init() {
    registerColors();
    MenuScreens.register(TinkerTables.craftingStationContainer.get(), CraftingStationScreen::new);
    MenuScreens.register(TinkerTables.tinkerStationContainer.get(), TinkerStationScreen::new);
    MenuScreens.register(TinkerTables.partBuilderContainer.get(), PartBuilderScreen::new);
    MenuScreens.register(TinkerTables.modifierWorktableContainer.get(), ModifierWorktableScreen::new);
    MenuScreens.register(TinkerTables.tinkerChestContainer.get(), TinkerChestScreen::new);

    // the item placements the renderer below reads; registering twice is a no-op, the smeltery
    // module initializes the same two
    TinkerItemDisplays.init();
    RenderItem.STATE_REGISTRY.init();

    // block entity renderers; Forge registered these from EntityRenderersEvent.RegisterRenderers
    BlockEntityRendererProvider<TableBlockEntity> tableRenderer = InventoryBlockEntityRenderer::new;
    BlockEntityRendererRegistry.register(TinkerTables.craftingStationTile.get(), tableRenderer);
    BlockEntityRendererRegistry.register(TinkerTables.tinkerStationTile.get(), tableRenderer);
    BlockEntityRendererRegistry.register(TinkerTables.modifierWorktableTile.get(), tableRenderer);
    BlockEntityRendererRegistry.register(TinkerTables.partBuilderTile.get(), tableRenderer);
  }

  /**
   * Colours for the tinkers' chest, which is dyed like leather armour.
   *
   * <p>The block reads the dye off its block entity; the item reads the {@code dyed_color}
   * component, which is where 1.21 moved what {@code DyeableLeatherItem} used to answer.
   */
  private static void registerColors() {
    ColorProviderRegistry.BLOCK.register(
      (state, view, pos, index) -> {
        if (index == 0 && view != null && pos != null && view.getBlockEntity(pos) instanceof TinkersChestBlockEntity chest) {
          return chest.getColor();
        }
        return -1;
      },
      TinkerTables.tinkersChest.get());
    ColorProviderRegistry.ITEM.register(
      (stack, index) -> index == 0 ? DyedItemColor.getOrDefault(stack, TinkersChestBlockEntity.DEFAULT_COLOR) : -1,
      TinkerTables.tinkersChest.asItem());
  }
}
