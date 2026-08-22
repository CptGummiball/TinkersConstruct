package slimeknights.mantle.transfer;

import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import slimeknights.mantle.transfer.fluid.FabricFluidHandler;
import slimeknights.mantle.transfer.fluid.IFluidHandler;
import slimeknights.mantle.transfer.item.FabricItemHandler;
import slimeknights.mantle.transfer.item.IItemHandler;

import javax.annotation.Nullable;
import java.util.Optional;

/**
 * Replaces Forge's capability lookups with Fabric API lookups.
 *
 * <p>Forge asked a block entity for a capability and got a {@code LazyOptional}; Fabric asks a
 * {@code BlockApiLookup} at a position. The mapping is:
 *
 * <ul>
 *   <li>{@code ForgeCapabilities.FLUID_HANDLER} → {@code FluidStorage.SIDED}</li>
 *   <li>{@code ForgeCapabilities.FLUID_HANDLER_ITEM} → {@code FluidStorage.ITEM}</li>
 *   <li>{@code ForgeCapabilities.ITEM_HANDLER} → {@code ItemStorage.SIDED}</li>
 * </ul>
 *
 * <p>Results are wrapped back into the Forge-shaped handler interfaces so calling code stays
 * unchanged. Lookups are not cached here: Fabric invalidates by position, and Tinkers' callers
 * already query per-interaction rather than holding handlers across ticks.
 */
public final class TransferUtil {

  private TransferUtil() {}

  /** Fluid handler of a block, or empty if that side exposes none. */
  public static Optional<IFluidHandler> getFluidHandler(Level level, BlockPos pos, @Nullable Direction side) {
    var storage = FluidStorage.SIDED.find(level, pos, side);
    return storage == null ? Optional.empty() : Optional.of(new FabricFluidHandler(storage));
  }

  /**
   * Fluid handler of an item stack (buckets, tanks-in-hand).
   *
   * <p>Fabric needs a {@link ContainerItemContext} to know where a changed container should
   * go. This uses a standalone context, which is correct for read-only inspection and for
   * callers that write the resulting stack back themselves — which is how Tinkers' casting
   * and tank interactions are written.
   */
  public static Optional<IFluidHandler> getFluidHandlerItem(ItemStack stack) {
    if (stack.isEmpty()) {
      return Optional.empty();
    }
    ContainerItemContext context = ContainerItemContext.withConstant(ItemVariant.of(stack), stack.getCount());
    var storage = context.find(FluidStorage.ITEM);
    return storage == null ? Optional.empty() : Optional.of(new FabricFluidHandler(storage));
  }

  /** Item handler of a block, or empty if that side exposes none. */
  public static Optional<IItemHandler> getItemHandler(Level level, BlockPos pos, @Nullable Direction side) {
    var storage = ItemStorage.SIDED.find(level, pos, side);
    if (storage == null) {
      return Optional.empty();
    }
    // our own block entities register their Forge-shaped handler through ItemStorageBridge;
    // unwrap it rather than round-tripping through the transaction API. The round trip loses
    // slot identity and setStackInSlot, which menus rely on: a slot backed by the double
    // bridge silently dropped inserted items and let shift-click loops duplicate stacks.
    if (storage instanceof slimeknights.mantle.transfer.item.ItemStorageBridge bridge) {
      return Optional.of(bridge.getHandler());
    }
    return Optional.of(new FabricItemHandler(storage));
  }
}
