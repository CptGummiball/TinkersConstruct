package slimeknights.mantle.transfer.item;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import slimeknights.mantle.transfer.fluid.TransferComponents;

import javax.annotation.Nonnull;

/**
 * Shim of Forge's {@code ItemStackHandler}: a simple list-backed modifiable item handler
 * with NBT round-tripping. 1.21 note: stacks serialize through the registry-aware codec,
 * so (de)serialization takes a {@link HolderLookup.Provider}.
 */
public class ItemStackHandler implements IItemHandlerModifiable {
  protected NonNullList<ItemStack> stacks;

  public ItemStackHandler() {
    this(1);
  }

  public ItemStackHandler(int size) {
    this.stacks = NonNullList.withSize(size, ItemStack.EMPTY);
  }

  public ItemStackHandler(NonNullList<ItemStack> stacks) {
    this.stacks = stacks;
  }

  public void setSize(int size) {
    this.stacks = NonNullList.withSize(size, ItemStack.EMPTY);
  }

  @Override
  public int getSlots() {
    return stacks.size();
  }

  @Nonnull
  @Override
  public ItemStack getStackInSlot(int slot) {
    validateSlotIndex(slot);
    return stacks.get(slot);
  }

  @Override
  public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
    validateSlotIndex(slot);
    stacks.set(slot, stack);
    onContentsChanged(slot);
  }

  @Nonnull
  @Override
  public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
    if (stack.isEmpty() || !isItemValid(slot, stack)) {
      return stack;
    }
    validateSlotIndex(slot);
    ItemStack existing = stacks.get(slot);
    int limit = Math.min(getSlotLimit(slot), stack.getMaxStackSize());
    if (!existing.isEmpty()) {
      if (!ItemStack.isSameItemSameComponents(stack, existing)) {
        return stack;
      }
      limit -= existing.getCount();
    }
    if (limit <= 0) {
      return stack;
    }
    boolean reachedLimit = stack.getCount() > limit;
    if (!simulate) {
      if (existing.isEmpty()) {
        stacks.set(slot, reachedLimit ? stack.copyWithCount(limit) : stack.copy());
      } else {
        existing.grow(reachedLimit ? limit : stack.getCount());
      }
      onContentsChanged(slot);
    }
    return reachedLimit ? stack.copyWithCount(stack.getCount() - limit) : ItemStack.EMPTY;
  }

  @Nonnull
  @Override
  public ItemStack extractItem(int slot, int amount, boolean simulate) {
    if (amount == 0) {
      return ItemStack.EMPTY;
    }
    validateSlotIndex(slot);
    ItemStack existing = stacks.get(slot);
    if (existing.isEmpty()) {
      return ItemStack.EMPTY;
    }
    int toExtract = Math.min(amount, existing.getMaxStackSize());
    if (existing.getCount() <= toExtract) {
      if (!simulate) {
        stacks.set(slot, ItemStack.EMPTY);
        onContentsChanged(slot);
        return existing;
      }
      return existing.copy();
    }
    if (!simulate) {
      stacks.set(slot, existing.copyWithCount(existing.getCount() - toExtract));
      onContentsChanged(slot);
    }
    return existing.copyWithCount(toExtract);
  }

  @Override
  public int getSlotLimit(int slot) {
    return 64;
  }

  @Override
  public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
    return true;
  }

  /** Writes the inventory to NBT */
  public CompoundTag serializeNBT(HolderLookup.Provider registries) {
    ListTag list = new ListTag();
    for (int i = 0; i < stacks.size(); i++) {
      ItemStack stack = stacks.get(i);
      if (!stack.isEmpty()) {
        CompoundTag itemTag = (CompoundTag) stack.save(registries, new CompoundTag());
        itemTag.putInt("Slot", i);
        list.add(itemTag);
      }
    }
    CompoundTag tag = new CompoundTag();
    tag.put("Items", list);
    tag.putInt("Size", stacks.size());
    return tag;
  }

  /** Reads the inventory from NBT */
  public void deserializeNBT(HolderLookup.Provider registries, CompoundTag tag) {
    setSize(tag.contains("Size", Tag.TAG_INT) ? tag.getInt("Size") : stacks.size());
    ListTag list = tag.getList("Items", Tag.TAG_COMPOUND);
    for (int i = 0; i < list.size(); i++) {
      CompoundTag itemTag = list.getCompound(i);
      int slot = itemTag.getInt("Slot");
      if (slot >= 0 && slot < stacks.size()) {
        stacks.set(slot, ItemStack.parseOptional(registries, itemTag));
      }
    }
    onLoad();
  }

  protected void validateSlotIndex(int slot) {
    if (slot < 0 || slot >= stacks.size()) {
      throw new IndexOutOfBoundsException("Slot " + slot + " not in valid range - [0," + stacks.size() + ")");
    }
  }

  /** Called after the inventory deserialized */
  protected void onLoad() {}

  /** Called when a slot changed */
  protected void onContentsChanged(int slot) {}
}
