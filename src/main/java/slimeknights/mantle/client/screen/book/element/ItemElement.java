package slimeknights.mantle.client.screen.book.element;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;
import slimeknights.mantle.client.book.action.StringActionProcessor;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Draws an item, cycling through several when the page names a tag or a set of variants.
 *
 * <p>Cycling is driven by the game clock rather than by a per-element timer so every element on a
 * page flips at the same moment, which reads as intentional instead of noisy.
 */
public class ItemElement extends SizedBookElement {
  /** Unscaled size of an item; keeps its upstream name, which pages already reference */
  public static final int ITEM_SIZE_HARDCODED = 16;
  /** Ticks each stack of a cycle is shown for */
  private static final int CYCLE_TICKS = 30;

  /** Scale the item draws at */
  public float scale;
  /** Overrides the item's own tooltip when set */
  @Nullable
  public List<Component> tooltip = null;
  /** Action fired when the item is clicked */
  @Nullable
  public String action = null;

  protected final List<ItemStack> itemCycle;
  /** Index into {@link #itemCycle} currently shown */
  protected int currentItem = 0;

  public ItemElement(int x, int y, float scale, Item item) {
    this(x, y, scale, new ItemStack(item));
  }

  public ItemElement(int x, int y, float scale, Block item) {
    this(x, y, scale, new ItemStack(item));
  }

  public ItemElement(int x, int y, float scale, ItemStack item) {
    this(x, y, scale, List.of(item), null);
  }

  public ItemElement(int x, int y, float scale, ItemStack... itemCycle) {
    this(x, y, scale, List.of(itemCycle), null);
  }

  public ItemElement(int x, int y, float scale, ItemStack[] itemCycle, @Nullable String action) {
    this(x, y, scale, List.of(itemCycle), action);
  }

  public ItemElement(int x, int y, float scale, Collection<ItemStack> itemCycle) {
    this(x, y, scale, itemCycle, null);
  }

  public ItemElement(int x, int y, float scale, Collection<ItemStack> itemCycle, @Nullable String action) {
    super(x, y, Math.round(ITEM_SIZE_HARDCODED * scale), Math.round(ITEM_SIZE_HARDCODED * scale));
    this.scale = scale;
    this.itemCycle = new ArrayList<>(itemCycle);
    this.itemCycle.removeIf(ItemStack::isEmpty);
    this.action = action;
  }

  /** Every stack this element cycles through */
  public List<ItemStack> getStacks() {
    return java.util.Collections.unmodifiableList(this.itemCycle);
  }

  /** Stack currently on show, empty if this element resolved to nothing */
  public ItemStack getStack() {
    if (this.itemCycle.isEmpty()) {
      return ItemStack.EMPTY;
    }
    if (this.currentItem >= this.itemCycle.size()) {
      this.currentItem = 0;
    }
    return this.itemCycle.get(this.currentItem);
  }

  @Override
  public void update(int mouseX, int mouseY) {
    if (this.itemCycle.size() > 1) {
      long ticks = this.mc.level == null ? 0 : this.mc.level.getGameTime();
      this.currentItem = (int)((ticks / CYCLE_TICKS) % this.itemCycle.size());
    }
  }

  @Override
  public void draw(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks, Font fontRenderer) {
    ItemStack stack = getStack();
    if (stack.isEmpty()) {
      return;
    }
    update(mouseX, mouseY);
    graphics.pose().pushPose();
    graphics.pose().translate(this.x, this.y, 0);
    graphics.pose().scale(this.scale, this.scale, 1F);
    graphics.renderItem(stack, 0, 0);
    graphics.renderItemDecorations(fontRenderer, stack, 0, 0);
    graphics.pose().popPose();
  }

  @Override
  public void drawOverlay(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks, Font fontRenderer) {
    if (!this.isHovered(mouseX, mouseY)) {
      return;
    }
    if (this.tooltip != null) {
      this.drawTooltip(graphics, this.tooltip, mouseX, mouseY, fontRenderer);
      return;
    }
    ItemStack stack = getStack();
    if (!stack.isEmpty()) {
      this.drawTooltip(graphics, stack.getTooltipLines(
        Item.TooltipContext.of(this.mc.level), this.mc.player,
        this.mc.options.advancedItemTooltips ? TooltipFlag.Default.ADVANCED : TooltipFlag.Default.NORMAL), mouseX, mouseY, fontRenderer);
    }
  }

  @Override
  public void mouseClicked(double mouseX, double mouseY, int mouseButton) {
    if (this.action != null && this.isHovered(mouseX, mouseY)) {
      StringActionProcessor.process(this.action, this.parent);
    }
  }
}
