package slimeknights.mantle.transfer.fluid;

import slimeknights.mantle.transfer.fluid.IFluidHandler.FluidAction;

/** Shim of Forge's single-tank view interface; {@link FluidTank} implements it. */
public interface IFluidTank {

  /** Fluid in the tank, do not modify */
  FluidStack getFluid();

  /** Current amount in the tank */
  int getFluidAmount();

  /** Tank capacity */
  int getCapacity();

  /** True if the tank accepts this fluid */
  boolean isFluidValid(FluidStack stack);

  /** Fills the tank, returning the amount accepted */
  int fill(FluidStack resource, FluidAction action);

  /** Drains up to the given amount */
  FluidStack drain(int maxDrain, FluidAction action);

  /** Drains up to the given stack */
  FluidStack drain(FluidStack resource, FluidAction action);
}
