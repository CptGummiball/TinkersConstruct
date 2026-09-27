package slimeknights.mantle.transfer.fluid;

import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.base.SingleStackStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

/**
 * Forge-shaped {@link IFluidHandlerItem} over Fabric's item fluid storage.
 *
 * <p>Forge's pattern for bucket-like interactions: copy the stack, obtain a handler, fill or
 * drain, then read {@link #getContainer()} for what the item became (bucket ⇄ empty bucket).
 * On Fabric the container swap happens through a {@link ContainerItemContext}, so this wraps
 * a private single-slot inventory holding the working copy — after any operation the slot
 * contains the post-exchange item, which is exactly what {@code getContainer()} must report.
 *
 * <p>{@code TransferUtil.getFluidHandlerItem}'s constant context cannot do this: it is
 * read-only and correct only for inspection. Interactions that keep the changed container
 * must go through {@link #of(ItemStack)}.
 */
public class FabricFluidHandlerItem extends FabricFluidHandler implements IFluidHandlerItem {

  private final BackingSlot slot;

  private FabricFluidHandlerItem(Storage<FluidVariant> storage, BackingSlot slot) {
    super(storage);
    this.slot = slot;
  }

  /** Wraps a copy of the stack, or empty when the item exposes no fluid storage. */
  public static Optional<IFluidHandlerItem> of(ItemStack stack) {
    if (stack.isEmpty()) {
      return Optional.empty();
    }
    BackingSlot slot = new BackingSlot(stack.copy());
    Storage<FluidVariant> storage = ContainerItemContext.ofSingleSlot(slot).find(FluidStorage.ITEM);
    return storage == null ? Optional.empty() : Optional.of(new FabricFluidHandlerItem(storage, slot));
  }

  @Override
  public ItemStack getContainer() {
    return slot.getStack();
  }

  /** Mutable single-slot inventory the container context exchanges the item through. */
  private static class BackingSlot extends SingleStackStorage {

    private ItemStack stack;

    BackingSlot(ItemStack stack) {
      this.stack = stack;
    }

    @Override
    protected ItemStack getStack() {
      return stack;
    }

    @Override
    protected void setStack(ItemStack stack) {
      this.stack = stack;
    }
  }
}
