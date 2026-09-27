package slimeknights.mantle.client.book;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

/**
 * Reads and writes the page a book stack was last left on.
 *
 * <p>Stored in {@code custom_data} under the same key the 1.20 build used, so books carried over
 * from a Forge world reopen where their owner left them.
 */
public final class BookHelper {
  private BookHelper() {}

  /** NBT key holding the current page */
  public static final String TAG_CURRENT_PAGE = "current_page";

  /** Page reference stored on the stack, or an empty string for the cover */
  public static String getSavedPage(ItemStack stack) {
    if (stack.isEmpty()) {
      return "";
    }
    CustomData data = stack.get(DataComponents.CUSTOM_DATA);
    if (data == null) {
      return "";
    }
    CompoundTag tag = data.copyTag();
    return tag.contains(TAG_CURRENT_PAGE) ? tag.getString(TAG_CURRENT_PAGE) : "";
  }

  /** Writes the current page onto a stack; server side */
  public static void writeSavedPage(ItemStack stack, String page) {
    if (stack.isEmpty()) {
      return;
    }
    CustomData data = stack.get(DataComponents.CUSTOM_DATA);
    CompoundTag tag = data == null ? new CompoundTag() : data.copyTag();
    if (page == null || page.isEmpty()) {
      tag.remove(TAG_CURRENT_PAGE);
    } else {
      tag.putString(TAG_CURRENT_PAGE, page);
    }
    if (tag.isEmpty()) {
      stack.remove(DataComponents.CUSTOM_DATA);
    } else {
      stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }
  }
}
