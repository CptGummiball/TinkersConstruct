package slimeknights.mantle.transfer.item;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * Fabric port of Forge's {@code ItemHandlerHelper}. Only the four methods Tinkers actually
 * calls are provided; the rest of the Forge class has no users here.
 */
public final class ItemHandlerHelper {

  private ItemHandlerHelper() {}

  /** 1.21 has a first-class {@code copyWithCount}; Forge predated it. */
  public static ItemStack copyStackWithSize(ItemStack stack, int size) {
    if (size == 0 || stack.isEmpty()) {
      return ItemStack.EMPTY;
    }
    return stack.copyWithCount(size);
  }

  /**
   * Whether two stacks would merge. In 1.21 this means same item <i>and</i> equal data
   * components — the component rewrite replaced Forge's NBT comparison.
   */
  public static boolean canItemStacksStack(ItemStack a, ItemStack b) {
    if (a.isEmpty() || !ItemStack.isSameItem(a, b)) {
      return false;
    }
    return ItemStack.isSameItemSameComponents(a, b);
  }

  /** Inserts into a handler across all slots, returning what did not fit. */
  public static ItemStack insertItem(IItemHandler handler, ItemStack stack, boolean simulate) {
    if (handler == null || stack.isEmpty()) {
      return stack;
    }
    ItemStack remainder = stack;
    for (int slot = 0; slot < handler.getSlots() && !remainder.isEmpty(); slot++) {
      remainder = handler.insertItem(slot, remainder, simulate);
    }
    return remainder;
  }

  /** Gives the stack to the player, dropping whatever does not fit — as Forge did. */
  public static void giveItemToPlayer(Player player, ItemStack stack) {
    giveItemToPlayer(player, stack, -1);
  }

  public static void giveItemToPlayer(Player player, ItemStack stack, int preferredSlot) {
    if (stack.isEmpty()) {
      return;
    }
    ItemStack remainder = stack.copy();
    if (preferredSlot >= 0 && preferredSlot < player.getInventory().items.size()) {
      ItemStack existing = player.getInventory().getItem(preferredSlot);
      if (existing.isEmpty()) {
        player.getInventory().setItem(preferredSlot, remainder);
        remainder = ItemStack.EMPTY;
      } else if (canItemStacksStack(existing, remainder)) {
        int room = Math.min(existing.getMaxStackSize(), player.getInventory().getMaxStackSize()) - existing.getCount();
        int moved = Math.min(room, remainder.getCount());
        if (moved > 0) {
          existing.grow(moved);
          remainder.shrink(moved);
        }
      }
    }
    if (!remainder.isEmpty() && player.getInventory().add(remainder)) {
      remainder = ItemStack.EMPTY;
    }
    if (!remainder.isEmpty()) {
      ItemEntity entity = player.drop(remainder, false);
      if (entity != null) {
        entity.setNoPickUpDelay();
        entity.setTarget(player.getUUID());
      }
      return;
    }
    player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
      SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2f,
      ((player.getRandom().nextFloat() - player.getRandom().nextFloat()) * 0.7f + 1.0f) * 2.0f);
    player.containerMenu.broadcastChanges();
  }
}
