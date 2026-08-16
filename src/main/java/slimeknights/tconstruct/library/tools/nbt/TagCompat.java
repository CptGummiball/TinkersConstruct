package slimeknights.tconstruct.library.tools.nbt;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

import javax.annotation.Nullable;

/**
 * The load-bearing ToolStack decision: all tool data (materials, modifiers, stats, mod data)
 * lives as one CompoundTag inside {@code minecraft:custom_data}, exactly as it lived in the
 * stack tag on 1.20.
 *
 * <p>Why not typed components per field: ToolStack and the modifier NBT layer are ~2000
 * lines of logic written against one mutable tree, with third-party-facing NBT paths
 * (tic_materials, tic_modifiers, ...) that the pack's data files also reference. One
 * component keeps every one of those paths and code lines valid. Vanilla interop that needs
 * real components (damage bar, enchantment glint) goes through the item overrides, which
 * read ToolStack — so nothing observable is lost.
 *
 * <p>These helpers are the only place that knows where the tag lives.
 */
public final class TagCompat {

  private TagCompat() {}

  /** Reads the legacy stack tag, or null when absent — the 1.20 {@code getTag()} contract. */
  @Nullable
  public static CompoundTag getTag(ItemStack stack) {
    CustomData data = stack.get(DataComponents.CUSTOM_DATA);
    return data == null ? null : data.copyTag();
  }

  /** True when the stack carries any legacy tag data. */
  public static boolean hasTag(ItemStack stack) {
    return stack.has(DataComponents.CUSTOM_DATA);
  }

  /** Reads the legacy tag, creating and attaching an empty one when absent. */
  public static CompoundTag getOrCreateTag(ItemStack stack) {
    CustomData data = stack.get(DataComponents.CUSTOM_DATA);
    if (data == null) {
      CompoundTag tag = new CompoundTag();
      stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
      return tag;
    }
    return data.copyTag();
  }

  /** Writes the tag back; null clears it. CustomData is immutable, so mutations must end here. */
  public static void setTag(ItemStack stack, @Nullable CompoundTag tag) {
    if (tag == null || tag.isEmpty()) {
      stack.remove(DataComponents.CUSTOM_DATA);
    } else {
      stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }
  }
}
