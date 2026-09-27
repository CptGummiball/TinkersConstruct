package slimeknights.mantle.network.packet;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import slimeknights.mantle.client.book.BookHelper;
import slimeknights.mantle.item.ILecternBookItem;
import slimeknights.mantle.network.NetworkEvent.Context;

/**
 * Tells the server which page a held book was left on.
 *
 * <p>The page has to round-trip through the server because it lives on the item stack: the client
 * cannot write to its own inventory copy and have it stick.
 */
public class UpdateHeldPagePacket implements IThreadsafePacket {
  private final InteractionHand hand;
  private final String page;

  public UpdateHeldPagePacket(InteractionHand hand, String page) {
    this.hand = hand;
    this.page = page;
  }

  public UpdateHeldPagePacket(RegistryFriendlyByteBuf buffer) {
    this.hand = buffer.readEnum(InteractionHand.class);
    this.page = buffer.readUtf(BookPageLimits.MAX_PAGE_LENGTH);
  }

  @Override
  public void encode(RegistryFriendlyByteBuf buffer) {
    buffer.writeEnum(this.hand);
    buffer.writeUtf(this.page, BookPageLimits.MAX_PAGE_LENGTH);
  }

  @Override
  public void handleThreadsafe(Context context) {
    ServerPlayer player = context.getSender();
    if (player == null) {
      return;
    }
    ItemStack stack = player.getItemInHand(this.hand);
    if (!stack.isEmpty() && stack.getItem() instanceof ILecternBookItem) {
      BookHelper.writeSavedPage(stack, this.page);
    }
  }
}
