package slimeknights.mantle.transfer.item;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

/**
 * Fabric port of Forge's {@code net.minecraftforge.items.wrapper.InvWrapper}: presents a
 * vanilla {@link Container} as an {@link IItemHandlerModifiable}. Insert/extract semantics
 * match the Forge original, including respecting {@code canPlaceItem} and the container's
 * max stack size.
 */
public class InvWrapper implements IItemHandlerModifiable {

  private final Container inv;

  public InvWrapper(Container inv) {
    this.inv = inv;
  }

  public Container getInv() {
    return inv;
  }

  @Override
  public int getSlots() {
    return inv.getContainerSize();
  }

  @Override
  public ItemStack getStackInSlot(int slot) {
    return inv.getItem(slot);
  }

  @Override
  public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
    if (stack.isEmpty()) {
      return ItemStack.EMPTY;
    }
    if (!inv.canPlaceItem(slot, stack)) {
      return stack;
    }
    ItemStack present = inv.getItem(slot);
    int limit = Math.min(inv.getMaxStackSize(), stack.getMaxStackSize());
    if (!present.isEmpty()) {
      if (!ItemHandlerHelper.canItemStacksStack(present, stack)) {
        return stack;
      }
      limit -= present.getCount();
    }
    if (limit <= 0) {
      return stack;
    }
    int inserted = Math.min(limit, stack.getCount());
    if (!simulate) {
      if (present.isEmpty()) {
        inv.setItem(slot, stack.copyWithCount(inserted));
      } else {
        present.grow(inserted);
      }
      inv.setChanged();
    }
    int remaining = stack.getCount() - inserted;
    return remaining <= 0 ? ItemStack.EMPTY : stack.copyWithCount(remaining);
  }

  @Override
  public ItemStack extractItem(int slot, int amount, boolean simulate) {
    if (amount <= 0) {
      return ItemStack.EMPTY;
    }
    ItemStack present = inv.getItem(slot);
    if (present.isEmpty()) {
      return ItemStack.EMPTY;
    }
    int extracted = Math.min(amount, present.getCount());
    if (simulate) {
      return present.copyWithCount(extracted);
    }
    ItemStack split = inv.removeItem(slot, extracted);
    inv.setChanged();
    return split;
  }

  @Override
  public int getSlotLimit(int slot) {
    return inv.getMaxStackSize();
  }

  @Override
  public boolean isItemValid(int slot, ItemStack stack) {
    return inv.canPlaceItem(slot, stack);
  }

  @Override
  public void setStackInSlot(int slot, ItemStack stack) {
    inv.setItem(slot, stack);
  }
}
