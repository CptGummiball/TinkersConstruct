package slimeknights.mantle.transfer.fluid;

import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.world.item.ItemStack;
import slimeknights.mantle.transfer.fluid.IFluidHandler.FluidAction;

import java.util.Collections;
import java.util.Iterator;
import java.util.function.Function;

/**
 * Presents a tag-driven {@link IFluidHandlerItem} (tank items, fluid cans) as a Fabric item
 * fluid storage. The inverse item-side counterpart of {@link FabricFluidHandler}.
 *
 * <p>Semantics follow Forge's {@code FluidHandlerItemStack}: the handler operates on a
 * single item, and the modified container replaces one item of the source stack through
 * {@link ContainerItemContext#exchange}, which keeps the whole operation transactional.
 */
public class ItemFluidStorageBridge implements Storage<FluidVariant> {

  private final ContainerItemContext context;
  private final Function<ItemStack, ? extends IFluidHandlerItem> factory;

  public ItemFluidStorageBridge(ContainerItemContext context, Function<ItemStack, ? extends IFluidHandlerItem> factory) {
    this.context = context;
    this.factory = factory;
  }

  /** Builds a handler over a copy of a single item from the context, or null when absent */
  private IFluidHandlerItem handler() {
    if (context.getAmount() < 1) {
      return null;
    }
    return factory.apply(context.getItemVariant().toStack());
  }

  @Override
  public long insert(FluidVariant resource, long maxAmount, TransactionContext transaction) {
    IFluidHandlerItem handler = handler();
    if (handler == null || resource.isBlank() || maxAmount <= 0) {
      return 0;
    }
    FluidStack toFill = FluidStack.ofDroplets(resource, maxAmount);
    int filled = handler.fill(toFill, FluidAction.EXECUTE);
    if (filled <= 0) {
      return 0;
    }
    if (context.exchange(ItemVariant.of(handler.getContainer()), 1, transaction) != 1) {
      return 0;
    }
    return FluidStack.toDroplets(filled);
  }

  @Override
  public long extract(FluidVariant resource, long maxAmount, TransactionContext transaction) {
    IFluidHandlerItem handler = handler();
    if (handler == null || resource.isBlank() || maxAmount <= 0) {
      return 0;
    }
    FluidStack toDrain = FluidStack.ofDroplets(resource, maxAmount);
    FluidStack drained = handler.drain(toDrain, FluidAction.EXECUTE);
    if (drained.isEmpty()) {
      return 0;
    }
    if (context.exchange(ItemVariant.of(handler.getContainer()), 1, transaction) != 1) {
      return 0;
    }
    return drained.getDroplets();
  }

  @Override
  public Iterator<StorageView<FluidVariant>> iterator() {
    IFluidHandlerItem handler = handler();
    if (handler == null) {
      return Collections.emptyIterator();
    }
    return Collections.<StorageView<FluidVariant>>singletonList(new View(handler)).iterator();
  }

  /** Read-only view of the single tank */
  private class View implements StorageView<FluidVariant> {
    private final IFluidHandlerItem handler;

    View(IFluidHandlerItem handler) {
      this.handler = handler;
    }

    @Override
    public long extract(FluidVariant resource, long maxAmount, TransactionContext transaction) {
      return ItemFluidStorageBridge.this.extract(resource, maxAmount, transaction);
    }

    @Override
    public boolean isResourceBlank() {
      return handler.getFluidInTank(0).isEmpty();
    }

    @Override
    public FluidVariant getResource() {
      return handler.getFluidInTank(0).getVariant();
    }

    @Override
    public long getAmount() {
      return handler.getFluidInTank(0).getDroplets();
    }

    @Override
    public long getCapacity() {
      return FluidStack.toDroplets(handler.getTankCapacity(0));
    }
  }
}
