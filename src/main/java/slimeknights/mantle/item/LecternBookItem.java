package slimeknights.mantle.item;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LecternBlock;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import slimeknights.mantle.util.BlockEntityHelper;

/**
 * Book item that can be placed on lecterns
 */
public abstract class LecternBookItem extends TooltipItem implements ILecternBookItem {
  public LecternBookItem(Properties properties) {
    super(properties);
  }

  @Override
  public InteractionResult useOn(UseOnContext context) {
    Level level = context.getLevel();
    BlockPos pos = context.getClickedPos();
    BlockState state = level.getBlockState(pos);
    if (state.is(Blocks.LECTERN)) {
      if (LecternBlock.tryPlaceBook(context.getPlayer(), level, pos, state, context.getItemInHand())) {
        return InteractionResult.sidedSuccess(level.isClientSide);
      }
    }
    return InteractionResult.PASS;
  }

  /**
   * Takes over the lectern interaction when the lectern holds one of our books.
   *
   * <p>Forge fired {@code RightClickBlock} and let the handler cancel it; Fabric's
   * {@link UseBlockCallback} does the same by returning a result other than PASS. Server side only,
   * because the client does not know what the lectern holds until the open packet arrives.
   */
  public static void init() {
    UseBlockCallback.EVENT.register((player, world, hand, hit) -> {
      if (world.isClientSide() || player.isShiftKeyDown()) {
        return InteractionResult.PASS;
      }
      BlockPos pos = hit.getBlockPos();
      BlockState state = world.getBlockState(pos);
      if (!state.is(Blocks.LECTERN)) {
        return InteractionResult.PASS;
      }
      return BlockEntityHelper.get(LecternBlockEntity.class, world, pos)
        .map(te -> {
          ItemStack book = te.getBook();
          if (!book.isEmpty() && book.getItem() instanceof ILecternBookItem lectern
              && lectern.openLecternScreen(world, pos, player, book)) {
            return InteractionResult.SUCCESS;
          }
          return InteractionResult.PASS;
        })
        .orElse(InteractionResult.PASS);
    });
  }
}
