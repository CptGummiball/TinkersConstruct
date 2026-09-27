package slimeknights.tconstruct.smeltery.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.block.entity.BlockEntity;
import slimeknights.mantle.transfer.cap.ForgeCapabilities;
import slimeknights.mantle.inventory.SmartItemHandlerSlot;
import slimeknights.tconstruct.shared.inventory.TriggeringBaseContainerMenu;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;

import javax.annotation.Nullable;

/**
 * Container for a block with a single item inventory
 */
public class SingleItemContainerMenu extends TriggeringBaseContainerMenu<BlockEntity> {
  public SingleItemContainerMenu(int id, @Nullable Inventory inv, @Nullable BlockEntity te) {
    super(TinkerSmeltery.singleItemContainer.get(), id, inv, te);
    if (te != null) {
      slimeknights.mantle.transfer.cap.CapabilityHelper.get(te, ForgeCapabilities.ITEM_HANDLER)
        .ifPresent(handler -> this.addSlot(new SmartItemHandlerSlot(handler, 0, 80, 20)));
      this.addInventorySlots();
    }
  }

  public SingleItemContainerMenu(int id, Inventory inv, net.minecraft.core.BlockPos pos) {
    this(id, inv, getTileEntityFromPos(pos, BlockEntity.class));
  }

  @Override
  protected int getInventoryYOffset() {
    return 51;
  }
}
