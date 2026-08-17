package slimeknights.mantle.transfer.item;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Exposes an {@link IItemHandler} as a Fabric item storage; the item-side counterpart of
 * {@link slimeknights.mantle.transfer.fluid.FluidStorageBridge}. Used to register block
 * entity inventories with {@code ItemStorage.SIDED} so hoppers and pipes from other mods
 * interact the way Forge capabilities allowed.
 */
public class ItemStorageBridge extends SnapshotParticipant<List<ItemStack>> implements Storage<ItemVariant> {

  private final IItemHandler handler;

  public ItemStorageBridge(IItemHandler handler) {
    this.handler = handler;
  }

  public IItemHandler getHandler() {
    return handler;
  }

  /* SnapshotParticipant */

  @Override
  protected List<ItemStack> createSnapshot() {
    int slots = handler.getSlots();
    List<ItemStack> snapshot = new ArrayList<>(slots);
    for (int i = 0; i < slots; i++) {
      snapshot.add(handler.getStackInSlot(i).copy());
    }
    return snapshot;
  }

  @Override
  protected void readSnapshot(List<ItemStack> snapshot) {
    if (handler instanceof IItemHandlerModifiable modifiable) {
      for (int i = 0; i < snapshot.size() && i < handler.getSlots(); i++) {
        modifiable.setStackInSlot(i, snapshot.get(i));
      }
    }
  }

  /* Storage */

  @Override
  public boolean supportsInsertion() {
    return true;
  }

  @Override
  public long insert(ItemVariant resource, long maxAmount, TransactionContext transaction) {
    if (resource.isBlank() || maxAmount <= 0) {
      return 0;
    }
    int requested = (int) Math.min(maxAmount, Integer.MAX_VALUE);
    ItemStack stack = resource.toStack(requested);
    // simulate across all slots first to know whether anything fits
    ItemStack remainder = ItemHandlerHelper.insertItem(handler, stack.copy(), true);
    int accepted = requested - remainder.getCount();
    if (accepted <= 0) {
      return 0;
    }
    updateSnapshots(transaction);
    ItemStack executed = ItemHandlerHelper.insertItem(handler, stack, false);
    return requested - executed.getCount();
  }

  @Override
  public boolean supportsExtraction() {
    return true;
  }

  @Override
  public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
    if (resource.isBlank() || maxAmount <= 0) {
      return 0;
    }
    long remaining = maxAmount;
    long extracted = 0;
    boolean snapshotTaken = false;
    for (int slot = 0; slot < handler.getSlots() && remaining > 0; slot++) {
      ItemStack inSlot = handler.getStackInSlot(slot);
      if (inSlot.isEmpty() || !resource.matches(inSlot)) {
        continue;
      }
      int request = (int) Math.min(remaining, Integer.MAX_VALUE);
      ItemStack simulated = handler.extractItem(slot, request, true);
      if (simulated.isEmpty()) {
        continue;
      }
      if (!snapshotTaken) {
        updateSnapshots(transaction);
        snapshotTaken = true;
      }
      ItemStack pulled = handler.extractItem(slot, simulated.getCount(), false);
      extracted += pulled.getCount();
      remaining -= pulled.getCount();
    }
    return extracted;
  }

  @Override
  public java.util.Iterator<StorageView<ItemVariant>> iterator() {
    int slots = handler.getSlots();
    List<StorageView<ItemVariant>> views = new ArrayList<>(slots);
    for (int i = 0; i < slots; i++) {
      views.add(new SlotView(i));
    }
    return views.iterator();
  }

  /** A single slot of the wrapped handler, seen as a Fabric storage view. */
  private class SlotView implements StorageView<ItemVariant> {

    private final int slot;

    SlotView(int slot) {
      this.slot = slot;
    }

    @Override
    public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
      if (resource.isBlank() || maxAmount <= 0 || !resource.matches(handler.getStackInSlot(slot))) {
        return 0;
      }
      int request = (int) Math.min(maxAmount, Integer.MAX_VALUE);
      ItemStack simulated = handler.extractItem(slot, request, true);
      if (simulated.isEmpty()) {
        return 0;
      }
      updateSnapshots(transaction);
      return handler.extractItem(slot, simulated.getCount(), false).getCount();
    }

    @Override
    public boolean isResourceBlank() {
      return handler.getStackInSlot(slot).isEmpty();
    }

    @Override
    public ItemVariant getResource() {
      return ItemVariant.of(handler.getStackInSlot(slot));
    }

    @Override
    public long getAmount() {
      return handler.getStackInSlot(slot).getCount();
    }

    @Override
    public long getCapacity() {
      return handler.getSlotLimit(slot);
    }
  }
}
