package slimeknights.mantle.transfer.item;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Presents a Fabric {@code Storage<ItemVariant>} as a Forge-shaped {@link IItemHandler}, so
 * Tinkers' inventory logic can read other mods' containers unchanged.
 *
 * <p>Fabric storages expose views rather than fixed slots, so "slot" here means "view index".
 * That is stable for the chest-like inventories Tinkers interacts with.
 */
public class FabricItemHandler implements IItemHandler {

  private final Storage<ItemVariant> storage;

  public FabricItemHandler(Storage<ItemVariant> storage) {
    this.storage = storage;
  }

  public Storage<ItemVariant> getStorage() {
    return storage;
  }

  private List<StorageView<ItemVariant>> views() {
    List<StorageView<ItemVariant>> views = new ArrayList<>();
    for (StorageView<ItemVariant> view : storage) {
      views.add(view);
    }
    return views;
  }

  @Override
  public int getSlots() {
    return views().size();
  }

  @Override
  public ItemStack getStackInSlot(int slot) {
    List<StorageView<ItemVariant>> views = views();
    if (slot < 0 || slot >= views.size()) {
      return ItemStack.EMPTY;
    }
    StorageView<ItemVariant> view = views.get(slot);
    if (view.isResourceBlank()) {
      return ItemStack.EMPTY;
    }
    return view.getResource().toStack((int) view.getAmount());
  }

  @Override
  public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
    if (stack.isEmpty()) {
      return ItemStack.EMPTY;
    }
    try (Transaction tx = Transaction.openOuter()) {
      long inserted = storage.insert(ItemVariant.of(stack), stack.getCount(), tx);
      if (simulate) {
        tx.abort();
      } else {
        tx.commit();
      }
      int remaining = stack.getCount() - (int) inserted;
      return remaining <= 0 ? ItemStack.EMPTY : stack.copyWithCount(remaining);
    }
  }

  @Override
  public ItemStack extractItem(int slot, int amount, boolean simulate) {
    ItemStack present = getStackInSlot(slot);
    if (present.isEmpty() || amount <= 0) {
      return ItemStack.EMPTY;
    }
    try (Transaction tx = Transaction.openOuter()) {
      long extracted = storage.extract(ItemVariant.of(present), amount, tx);
      if (simulate) {
        tx.abort();
      } else {
        tx.commit();
      }
      return extracted <= 0 ? ItemStack.EMPTY : present.copyWithCount((int) extracted);
    }
  }

  @Override
  public int getSlotLimit(int slot) {
    List<StorageView<ItemVariant>> views = views();
    if (slot < 0 || slot >= views.size()) {
      return 0;
    }
    return (int) Math.min(Integer.MAX_VALUE, views.get(slot).getCapacity());
  }

  @Override
  public boolean isItemValid(int slot, ItemStack stack) {
    if (stack.isEmpty()) {
      return false;
    }
    try (Transaction tx = Transaction.openOuter()) {
      long inserted = storage.insert(ItemVariant.of(stack), stack.getCount(), tx);
      tx.abort();
      return inserted > 0;
    }
  }
}
