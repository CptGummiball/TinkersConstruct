package slimeknights.mantle.transfer.fluid;

/**
 * Fabric stand-in for Forge's {@code net.minecraftforge.fluids.capability.IFluidHandler}.
 *
 * <p>Signatures match the Forge 1.20.1 interface exactly so the ~110 call sites across the
 * smeltery, tanks and casting logic port as an import change. Fabric's {@code Storage}
 * equivalent is reached through {@link FluidStorageBridge}.
 */
public interface IFluidHandler {

  /** Whether a call should actually mutate the handler or only report what it would do. */
  enum FluidAction {
    EXECUTE, SIMULATE;

    public boolean execute() {
      return this == EXECUTE;
    }

    public boolean simulate() {
      return this == SIMULATE;
    }

    public static FluidAction of(boolean execute) {
      return execute ? EXECUTE : SIMULATE;
    }
  }

  int getTanks();

  FluidStack getFluidInTank(int tank);

  int getTankCapacity(int tank);

  boolean isFluidValid(int tank, FluidStack stack);

  /** @return millibuckets actually (or hypothetically) filled */
  int fill(FluidStack resource, FluidAction action);

  /** Drains the given fluid specifically; returns what was removed. */
  FluidStack drain(FluidStack resource, FluidAction action);

  /** Drains up to {@code maxDrain} mB of whatever is present. */
  FluidStack drain(int maxDrain, FluidAction action);

  /* Transaction support — not part of the Forge interface.
   *
   * Fabric storages are transactional: a transfer may be rolled back after it has been
   * applied. Forge handlers have no such concept, so handlers exposed to Fabric must be
   * able to snapshot and restore their contents. FluidStorageBridge relies on this. */

  /** Captures the full contents so a rolled-back transaction can undo an applied change. */
  default java.util.List<FluidStack> createSnapshot() {
    int tanks = getTanks();
    java.util.List<FluidStack> snapshot = new java.util.ArrayList<>(tanks);
    for (int i = 0; i < tanks; i++) {
      snapshot.add(getFluidInTank(i).copy());
    }
    return snapshot;
  }

  /**
   * Restores contents captured by {@link #createSnapshot()}.
   *
   * @throws UnsupportedOperationException if this handler cannot be rolled back, in which
   *         case it must not be exposed to Fabric through {@link FluidStorageBridge}.
   */
  default void restoreSnapshot(java.util.List<FluidStack> snapshot) {
    throw new UnsupportedOperationException(
      getClass().getName() + " cannot be rolled back; it must not be exposed as a Fabric Storage");
  }
}
