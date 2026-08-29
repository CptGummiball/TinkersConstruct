package slimeknights.mantle.transfer.cap;

import slimeknights.mantle.transfer.fluid.IFluidHandler;
import slimeknights.mantle.transfer.fluid.IFluidHandlerItem;
import slimeknights.mantle.transfer.item.IItemHandler;

/** Shim of Forge's standard capability tokens, keyed to the mantle transfer interfaces. */
public class ForgeCapabilities {
  private ForgeCapabilities() {}

  public static final Capability<IItemHandler> ITEM_HANDLER = new Capability<>("item_handler");
  public static final Capability<IFluidHandler> FLUID_HANDLER = new Capability<>("fluid_handler");
  public static final Capability<IFluidHandlerItem> FLUID_HANDLER_ITEM = new Capability<>("fluid_handler_item");
}
