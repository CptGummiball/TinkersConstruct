package slimeknights.mantle.transfer.fluid;

/** Immutable no-op handler, mirroring Forge's {@code EmptyFluidHandler.INSTANCE}. */
public enum EmptyFluidHandler implements IFluidHandler {
  INSTANCE;

  @Override
  public int getTanks() {
    return 0;
  }

  @Override
  public FluidStack getFluidInTank(int tank) {
    return FluidStack.EMPTY;
  }

  @Override
  public int getTankCapacity(int tank) {
    return 0;
  }

  @Override
  public boolean isFluidValid(int tank, FluidStack stack) {
    return false;
  }

  @Override
  public int fill(FluidStack resource, FluidAction action) {
    return 0;
  }

  @Override
  public FluidStack drain(FluidStack resource, FluidAction action) {
    return FluidStack.EMPTY;
  }

  @Override
  public FluidStack drain(int maxDrain, FluidAction action) {
    return FluidStack.EMPTY;
  }
}
