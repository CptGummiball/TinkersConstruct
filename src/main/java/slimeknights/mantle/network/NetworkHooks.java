package slimeknights.mantle.network;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;

import javax.annotation.Nullable;

/**
 * Shim of the menu-opening half of Forge's {@code NetworkHooks}. Mantle menus all send a
 * block position as their opening data (see {@code MenuTypeDeferredRegister}), so this wraps
 * the provider in Fabric's {@link ExtendedScreenHandlerFactory} carrying that position.
 */
public class NetworkHooks {
  private NetworkHooks() {}

  /** Opens a menu whose client factory reads a block position */
  public static void openScreen(ServerPlayer player, MenuProvider provider, BlockPos pos) {
    player.openMenu(new ExtendedScreenHandlerFactory<BlockPos>() {
      @Override
      public BlockPos getScreenOpeningData(ServerPlayer serverPlayer) {
        return pos;
      }

      @Override
      public Component getDisplayName() {
        return provider.getDisplayName();
      }

      @Nullable
      @Override
      public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
        return provider.createMenu(windowId, inventory, player);
      }
    });
  }
}
