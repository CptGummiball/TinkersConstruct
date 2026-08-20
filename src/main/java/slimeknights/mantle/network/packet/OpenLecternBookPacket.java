package slimeknights.mantle.network.packet;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import slimeknights.mantle.network.NetworkEvent.Context;

/**
 * Asks the client to open the book on a lectern.
 *
 * <p>Server side, because only the server knows what the lectern holds; the client then opens the
 * book screen for whatever item came back.
 */
public class OpenLecternBookPacket implements IThreadsafePacket {
  private final BlockPos pos;
  private final ItemStack book;

  public OpenLecternBookPacket(BlockPos pos, ItemStack book) {
    this.pos = pos;
    this.book = book;
  }

  public OpenLecternBookPacket(RegistryFriendlyByteBuf buffer) {
    this.pos = buffer.readBlockPos();
    this.book = ItemStack.OPTIONAL_STREAM_CODEC.decode(buffer);
  }

  @Override
  public void encode(RegistryFriendlyByteBuf buffer) {
    buffer.writeBlockPos(this.pos);
    ItemStack.OPTIONAL_STREAM_CODEC.encode(buffer, this.book);
  }

  @Override
  public void handleThreadsafe(Context context) {
    HandleClient.handle(this.pos, this.book);
  }

  /** Deferred so the client-only class is not loaded on a dedicated server */
  private static class HandleClient {
    private static void handle(BlockPos pos, ItemStack book) {
      if (!book.isEmpty() && book.getItem() instanceof slimeknights.mantle.item.ILecternBookItem lectern) {
        lectern.openLecternScreenClient(pos, book);
      }
    }
  }
}
