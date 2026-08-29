package slimeknights.mantle.transfer.item;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

/**
 * Fabric port of Forge's {@code net.minecraftforge.items.SlotItemHandler}: a menu {@link Slot}
 * backed by an {@link IItemHandler} instead of a {@link Container}.
 *
 * <p>The dummy container handed to the vanilla super constructor is never touched — every
 * accessor is overridden to hit the handler, exactly as Forge did it.
 */
public class SlotItemHandler extends Slot {

  private static final Container EMPTY = new SimpleContainer(0);

  private final IItemHandler itemHandler;
  private final int index;

  public SlotItemHandler(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
    super(EMPTY, index, xPosition, yPosition);
    this.itemHandler = itemHandler;
    this.index = index;
  }

  public IItemHandler getItemHandler() {
    return itemHandler;
  }

  @Override
  public boolean mayPlace(ItemStack stack) {
    if (stack.isEmpty()) {
      return false;
    }
    return itemHandler.isItemValid(index, stack);
  }

  @Override
  public ItemStack getItem() {
    return itemHandler.getStackInSlot(index);
  }

  @Override
  public void set(ItemStack stack) {
    if (itemHandler instanceof IItemHandlerModifiable modifiable) {
      modifiable.setStackInSlot(index, stack);
    }
    setChanged();
  }

  @Override
  public void onQuickCraft(ItemStack oldStack, ItemStack newStack) {}

  @Override
  public int getMaxStackSize() {
    return itemHandler.getSlotLimit(index);
  }

  @Override
  public int getMaxStackSize(ItemStack stack) {
    // Simulate an insert of a full stack: whatever does not come back fits.
    ItemStack maxInput = stack.copyWithCount(stack.getMaxStackSize());
    ItemStack remainder = itemHandler.insertItem(index, maxInput, true);
    return maxInput.getCount() - remainder.getCount();
  }

  @Override
  public boolean mayPickup(Player player) {
    return !itemHandler.extractItem(index, 1, true).isEmpty();
  }

  @Override
  public ItemStack remove(int amount) {
    return itemHandler.extractItem(index, amount, false);
  }
}
