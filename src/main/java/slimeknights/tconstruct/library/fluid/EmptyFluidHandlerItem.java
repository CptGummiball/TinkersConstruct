package slimeknights.tconstruct.library.fluid;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.world.item.ItemStack;
import slimeknights.mantle.transfer.fluid.EmptyFluidHandler;
import slimeknights.mantle.transfer.fluid.FluidStack;
import slimeknights.mantle.transfer.fluid.IFluidHandlerItem;

/**
 * Empty fluid handler item instance, usable like {@link EmptyFluidHandler#INSTANCE}.
 * The mantle shim is an enum, so the no-op surface is implemented here instead of inherited.
 */
@RequiredArgsConstructor
public class EmptyFluidHandlerItem implements IFluidHandlerItem {
  public static final EmptyFluidHandlerItem INSTANCE = new EmptyFluidHandlerItem(ItemStack.EMPTY);

  /** Container reference */
  @Getter
  private final ItemStack container;

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
