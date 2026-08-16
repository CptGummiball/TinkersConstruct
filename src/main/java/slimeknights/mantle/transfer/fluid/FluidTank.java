package slimeknights.mantle.transfer.fluid;

import net.minecraft.nbt.CompoundTag;

import java.util.function.Predicate;

/**
 * Fabric port of Forge's {@code net.minecraftforge.fluids.capability.templates.FluidTank},
 * a single-fluid tank with a capacity and an optional content filter.
 *
 * <p>Behaviour is deliberately identical to the Forge original, including the partial-fill
 * semantics the smeltery's drain/fill logic relies on.
 */
public class FluidTank implements IFluidHandler {

  protected Predicate<FluidStack> validator;
  protected FluidStack fluid = FluidStack.EMPTY;
  protected int capacity;

  public FluidTank(int capacity) {
    this(capacity, stack -> true);
  }

  public FluidTank(int capacity, Predicate<FluidStack> validator) {
    this.capacity = capacity;
    this.validator = validator;
  }

  public FluidTank setCapacity(int capacity) {
    this.capacity = capacity;
    return this;
  }

  public FluidTank setValidator(Predicate<FluidStack> validator) {
    this.validator = validator;
    return this;
  }

  public FluidStack getFluid() {
    return fluid;
  }

  public void setFluid(FluidStack stack) {
    this.fluid = stack;
  }

  public int getFluidAmount() {
    return fluid.getAmount();
  }

  public int getCapacity() {
    return capacity;
  }

  public boolean isEmpty() {
    return fluid.isEmpty();
  }

  /** Room left in mB. */
  public int getSpace() {
    return Math.max(0, capacity - fluid.getAmount());
  }

  /** Called whenever the contents change, for block entities to mark themselves dirty. */
  protected void onContentsChanged() {}

  /* IFluidHandler */

  @Override
  public int getTanks() {
    return 1;
  }

  @Override
  public FluidStack getFluidInTank(int tank) {
    return fluid;
  }

  @Override
  public int getTankCapacity(int tank) {
    return capacity;
  }

  @Override
  public boolean isFluidValid(int tank, FluidStack stack) {
    return validator.test(stack);
  }

  @Override
  public int fill(FluidStack resource, FluidAction action) {
    if (resource.isEmpty() || !isFluidValid(0, resource)) {
      return 0;
    }
    if (fluid.isEmpty()) {
      int filled = Math.min(capacity, resource.getAmount());
      if (action.execute() && filled > 0) {
        fluid = new FluidStack(resource, filled);
        onContentsChanged();
      }
      return filled;
    }
    if (!fluid.isFluidEqual(resource)) {
      return 0;
    }
    int filled = Math.min(getSpace(), resource.getAmount());
    if (action.execute() && filled > 0) {
      fluid.grow(filled);
      onContentsChanged();
    }
    return filled;
  }

  @Override
  public FluidStack drain(FluidStack resource, FluidAction action) {
    if (resource.isEmpty() || !resource.isFluidEqual(fluid)) {
      return FluidStack.EMPTY;
    }
    return drain(resource.getAmount(), action);
  }

  @Override
  public FluidStack drain(int maxDrain, FluidAction action) {
    if (maxDrain <= 0 || fluid.isEmpty()) {
      return FluidStack.EMPTY;
    }
    int drained = Math.min(fluid.getAmount(), maxDrain);
    FluidStack stack = new FluidStack(fluid, drained);
    if (action.execute() && drained > 0) {
      fluid.shrink(drained);
      if (fluid.isEmpty()) {
        fluid = FluidStack.EMPTY;
      }
      onContentsChanged();
    }
    return stack;
  }

  @Override
  public void restoreSnapshot(java.util.List<FluidStack> snapshot) {
    fluid = snapshot.isEmpty() ? FluidStack.EMPTY : snapshot.get(0).copy();
    onContentsChanged();
  }

  /* Serialisation — same NBT shape as Forge, so existing worlds load unchanged */

  public FluidTank readFromNBT(CompoundTag nbt) {
    setFluid(FluidStack.loadFluidStackFromNBT(nbt));
    return this;
  }

  public CompoundTag writeToNBT(CompoundTag nbt) {
    fluid.writeToNBT(nbt);
    return nbt;
  }
}
