package slimeknights.mantle.transfer.fluid;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;

import java.util.ArrayList;
import java.util.List;

/**
 * The inverse of {@link FluidStorageBridge}: presents any Fabric {@code Storage<FluidVariant>}
 * as a Forge-shaped {@link IFluidHandler}.
 *
 * <p>This is what lets Tinkers' own logic — the faucet, the casting channel, the tank
 * interaction code — pull from and push into other Fabric mods' tanks without any of it
 * knowing that Fabric's transfer API exists.
 *
 * <p>{@code SIMULATE} maps onto an aborted transaction and {@code EXECUTE} onto a committed
 * one, which is exactly the semantics Forge's {@code FluidAction} describes.
 */
public class FabricFluidHandler implements IFluidHandler {

  private final Storage<FluidVariant> storage;

  public FabricFluidHandler(Storage<FluidVariant> storage) {
    this.storage = storage;
  }

  public Storage<FluidVariant> getStorage() {
    return storage;
  }

  /** Snapshots the views once so tank indices stay stable across a single operation. */
  private List<StorageView<FluidVariant>> views() {
    List<StorageView<FluidVariant>> views = new ArrayList<>();
    for (StorageView<FluidVariant> view : storage) {
      views.add(view);
    }
    return views;
  }

  @Override
  public int getTanks() {
    return views().size();
  }

  @Override
  public FluidStack getFluidInTank(int tank) {
    List<StorageView<FluidVariant>> views = views();
    if (tank < 0 || tank >= views.size()) {
      return FluidStack.EMPTY;
    }
    StorageView<FluidVariant> view = views.get(tank);
    return FluidStack.ofDroplets(view.getResource(), view.getAmount());
  }

  @Override
  public int getTankCapacity(int tank) {
    List<StorageView<FluidVariant>> views = views();
    if (tank < 0 || tank >= views.size()) {
      return 0;
    }
    return FluidStack.toMillibuckets(views.get(tank).getCapacity());
  }

  @Override
  public boolean isFluidValid(int tank, FluidStack stack) {
    // Fabric storages have no "would you accept this" query that does not move fluid;
    // a simulated insert is the closest equivalent and costs nothing when aborted.
    if (stack.isEmpty()) {
      return false;
    }
    try (Transaction tx = Transaction.openOuter()) {
      long inserted = storage.insert(stack.getVariant(), stack.getDroplets(), tx);
      tx.abort();
      return inserted > 0;
    }
  }

  @Override
  public int fill(FluidStack resource, FluidAction action) {
    if (resource.isEmpty()) {
      return 0;
    }
    try (Transaction tx = Transaction.openOuter()) {
      long inserted = storage.insert(resource.getVariant(), resource.getDroplets(), tx);
      if (action.execute()) {
        tx.commit();
      } else {
        tx.abort();
      }
      return FluidStack.toMillibuckets(inserted);
    }
  }

  @Override
  public FluidStack drain(FluidStack resource, FluidAction action) {
    if (resource.isEmpty()) {
      return FluidStack.EMPTY;
    }
    try (Transaction tx = Transaction.openOuter()) {
      long extracted = storage.extract(resource.getVariant(), resource.getDroplets(), tx);
      if (action.execute()) {
        tx.commit();
      } else {
        tx.abort();
      }
      return FluidStack.of(resource.getVariant(), FluidStack.toMillibuckets(extracted));
    }
  }

  @Override
  public FluidStack drain(int maxDrain, FluidAction action) {
    if (maxDrain <= 0) {
      return FluidStack.EMPTY;
    }
    // Untyped drain: take from the first non-empty view, matching Forge's behaviour.
    for (StorageView<FluidVariant> view : storage) {
      if (view.isResourceBlank()) {
        continue;
      }
      FluidVariant resource = view.getResource();
      try (Transaction tx = Transaction.openOuter()) {
        long extracted = storage.extract(resource, FluidStack.toDroplets(maxDrain), tx);
        if (extracted <= 0) {
          tx.abort();
          continue;
        }
        if (action.execute()) {
          tx.commit();
        } else {
          tx.abort();
        }
        return FluidStack.ofDroplets(resource, extracted);
      }
    }
    return FluidStack.EMPTY;
  }
}
