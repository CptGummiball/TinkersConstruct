package slimeknights.mantle.transfer.fluid;

import net.minecraft.world.item.ItemStack;

/**
 * Fabric stand-in for Forge's {@code IFluidHandlerItem}: a fluid handler backed by an item
 * stack, where filling or draining may replace the container (bucket ⇄ empty bucket).
 */
public interface IFluidHandlerItem extends IFluidHandler {

  /** The container after any fill/drain, which may be a different item than it started as. */
  ItemStack getContainer();
}
