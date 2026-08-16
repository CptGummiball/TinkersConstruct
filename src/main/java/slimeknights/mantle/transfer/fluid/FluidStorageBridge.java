package slimeknights.mantle.transfer.fluid;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import slimeknights.mantle.transfer.fluid.IFluidHandler.FluidAction;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Exposes a Forge-shaped {@link IFluidHandler} to Fabric as a {@code Storage<FluidVariant>}.
 *
 * <p>This is the seam where Tinkers' smeltery, tanks and casting basins meet the rest of the
 * Fabric ecosystem — Oritech, Energized Power and Refined Storage all move fluids through
 * {@code Storage}.
 *
 * <h2>Transactions</h2>
 * Fabric may roll a transfer back after it was applied, which Forge handlers have no notion
 * of. Changes are therefore applied immediately (so nested reads inside the same transaction
 * see them, as the {@code Storage} contract requires) with the previous contents snapshotted
 * via {@link SnapshotParticipant}. Applying eagerly and undoing on abort is what keeps a
 * rolled-back transfer from duplicating fluid.
 *
 * <h2>Units</h2>
 * Fabric counts droplets, Forge millibuckets. Amounts are floored to whole millibuckets on
 * the way in, so a partial millibucket is never silently consumed.
 */
public class FluidStorageBridge extends SnapshotParticipant<List<FluidStack>> implements Storage<FluidVariant> {

  private final IFluidHandler handler;

  public FluidStorageBridge(IFluidHandler handler) {
    this.handler = handler;
  }

  public IFluidHandler getHandler() {
    return handler;
  }

  /* SnapshotParticipant */

  @Override
  protected List<FluidStack> createSnapshot() {
    return handler.createSnapshot();
  }

  @Override
  protected void readSnapshot(List<FluidStack> snapshot) {
    handler.restoreSnapshot(snapshot);
  }

  /* Storage */

  @Override
  public boolean supportsInsertion() {
    return true;
  }

  @Override
  public long insert(FluidVariant resource, long maxAmount, TransactionContext transaction) {
    if (resource.isBlank() || maxAmount <= 0) {
      return 0;
    }
    int requestedMb = FluidStack.toMillibuckets(maxAmount);
    if (requestedMb <= 0) {
      return 0;
    }
    FluidStack stack = FluidStack.of(resource, requestedMb);
    int simulated = handler.fill(stack, FluidAction.SIMULATE);
    if (simulated <= 0) {
      return 0;
    }
    updateSnapshots(transaction);
    int filled = handler.fill(stack, FluidAction.EXECUTE);
    return FluidStack.toDroplets(filled);
  }

  @Override
  public boolean supportsExtraction() {
    return true;
  }

  @Override
  public long extract(FluidVariant resource, long maxAmount, TransactionContext transaction) {
    if (resource.isBlank() || maxAmount <= 0) {
      return 0;
    }
    int requestedMb = FluidStack.toMillibuckets(maxAmount);
    if (requestedMb <= 0) {
      return 0;
    }
    FluidStack stack = FluidStack.of(resource, requestedMb);
    FluidStack simulated = handler.drain(stack, FluidAction.SIMULATE);
    if (simulated.isEmpty()) {
      return 0;
    }
    updateSnapshots(transaction);
    FluidStack drained = handler.drain(stack, FluidAction.EXECUTE);
    return FluidStack.toDroplets(drained.getAmount());
  }

  @Override
  public Iterator<StorageView<FluidVariant>> iterator() {
    int tanks = handler.getTanks();
    List<StorageView<FluidVariant>> views = new ArrayList<>(tanks);
    for (int i = 0; i < tanks; i++) {
      views.add(new TankView(i));
    }
    return views.iterator();
  }

  /** A single tank of the wrapped handler, seen as a Fabric storage view. */
  private class TankView implements StorageView<FluidVariant> {

    private final int tank;

    TankView(int tank) {
      this.tank = tank;
    }

    @Override
    public long extract(FluidVariant resource, long maxAmount, TransactionContext transaction) {
      // Delegate to the handler: it decides which tank a specific fluid comes out of.
      return FluidStorageBridge.this.extract(resource, maxAmount, transaction);
    }

    @Override
    public boolean isResourceBlank() {
      return handler.getFluidInTank(tank).isEmpty();
    }

    @Override
    public FluidVariant getResource() {
      return handler.getFluidInTank(tank).getVariant();
    }

    @Override
    public long getAmount() {
      return FluidStack.toDroplets(handler.getFluidInTank(tank).getAmount());
    }

    @Override
    public long getCapacity() {
      return FluidStack.toDroplets(handler.getTankCapacity(tank));
    }
  }
}
