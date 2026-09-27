package slimeknights.mantle.transfer.item;

import net.minecraft.world.item.ItemStack;

/**
 * Fabric stand-in for Forge's {@code net.minecraftforge.items.IItemHandler}.
 *
 * <p>Signatures match Forge 1.20.1 so the tool station, part chest and casting inventories
 * port without touching their logic. {@link ItemStorageBridge} maps this onto Fabric's
 * {@code Storage<ItemVariant>}.
 */
public interface IItemHandler {

  int getSlots();

  ItemStack getStackInSlot(int slot);

  /** @return the remainder that did <i>not</i> fit, matching Forge's return convention */
  ItemStack insertItem(int slot, ItemStack stack, boolean simulate);

  /** @return what was actually removed */
  ItemStack extractItem(int slot, int amount, boolean simulate);

  int getSlotLimit(int slot);

  boolean isItemValid(int slot, ItemStack stack);
}
