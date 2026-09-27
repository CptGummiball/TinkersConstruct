package slimeknights.mantle.transfer.item;

import net.minecraft.world.item.ItemStack;

/** An {@link IItemHandler} whose slots can be written directly, as in Forge. */
public interface IItemHandlerModifiable extends IItemHandler {

  /** Overwrites a slot, bypassing the insert validity checks. */
  void setStackInSlot(int slot, ItemStack stack);
}
