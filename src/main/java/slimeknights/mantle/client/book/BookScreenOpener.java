package slimeknights.mantle.client.book;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

/**
 * Something that can show a book screen.
 *
 * <p>Three overloads because the book remembers the page you left it on, and where that page has
 * to be written back differs: a held stack, a stack in an open container, or a book on a lectern.
 */
public interface BookScreenOpener {
  /** Opens the book held in the given hand */
  void openGui(@Nullable InteractionHand hand, ItemStack item);

  /** Opens a book sitting in the given inventory slot */
  void openGui(int slot, ItemStack item);

  /** Opens a book placed on the lectern at the given position */
  void openGui(BlockPos pos, ItemStack item);
}
