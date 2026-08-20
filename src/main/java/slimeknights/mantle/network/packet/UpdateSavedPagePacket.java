package slimeknights.mantle.network.packet;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import slimeknights.mantle.client.book.BookHelper;
import slimeknights.mantle.item.ILecternBookItem;
import slimeknights.mantle.network.NetworkEvent.Context;

/** Tells the server which page a book sitting in an inventory slot was left on */
public class UpdateSavedPagePacket implements IThreadsafePacket {
  private final int slot;
  private final String page;

  public UpdateSavedPagePacket(int slot, String page) {
    this.slot = slot;
    this.page = page;
  }

  public UpdateSavedPagePacket(RegistryFriendlyByteBuf buffer) {
    this.slot = buffer.readVarInt();
    this.page = buffer.readUtf(BookPageLimits.MAX_PAGE_LENGTH);
  }

  @Override
  public void encode(RegistryFriendlyByteBuf buffer) {
    buffer.writeVarInt(this.slot);
    buffer.writeUtf(this.page, BookPageLimits.MAX_PAGE_LENGTH);
  }

  @Override
  public void handleThreadsafe(Context context) {
    ServerPlayer player = context.getSender();
    if (player == null) {
      return;
    }
    // the slot index comes from the client, so it is bounds checked before use
    if (this.slot < 0 || this.slot >= player.getInventory().getContainerSize()) {
      return;
    }
    ItemStack stack = player.getInventory().getItem(this.slot);
    if (!stack.isEmpty() && stack.getItem() instanceof ILecternBookItem) {
      BookHelper.writeSavedPage(stack, this.page);
    }
  }
}
