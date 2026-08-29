package slimeknights.mantle.network.packet;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import slimeknights.mantle.client.book.BookHelper;
import slimeknights.mantle.item.ILecternBookItem;
import slimeknights.mantle.network.NetworkEvent.Context;
import slimeknights.mantle.util.BlockEntityHelper;

/** Tells the server which page a book on a lectern was left on */
public class UpdateLecternPagePacket implements IThreadsafePacket {
  private final BlockPos pos;
  private final String page;

  public UpdateLecternPagePacket(BlockPos pos, String page) {
    this.pos = pos;
    this.page = page;
  }

  public UpdateLecternPagePacket(RegistryFriendlyByteBuf buffer) {
    this.pos = buffer.readBlockPos();
    this.page = buffer.readUtf(BookPageLimits.MAX_PAGE_LENGTH);
  }

  @Override
  public void encode(RegistryFriendlyByteBuf buffer) {
    buffer.writeBlockPos(this.pos);
    buffer.writeUtf(this.page, BookPageLimits.MAX_PAGE_LENGTH);
  }

  @Override
  public void handleThreadsafe(Context context) {
    ServerPlayer player = context.getSender();
    if (player == null) {
      return;
    }
    // reject a position the player could not be reading from
    if (!player.level().isLoaded(this.pos) || player.distanceToSqr(this.pos.getX() + 0.5, this.pos.getY() + 0.5, this.pos.getZ() + 0.5) > 64) {
      return;
    }
    BlockEntityHelper.get(LecternBlockEntity.class, player.level(), this.pos).ifPresent(lectern -> {
      ItemStack book = lectern.getBook();
      if (!book.isEmpty() && book.getItem() instanceof ILecternBookItem) {
        BookHelper.writeSavedPage(book, this.page);
        lectern.setChanged();
      }
    });
  }
}
