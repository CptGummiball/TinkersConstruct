package slimeknights.mantle.client.screen.book.element;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

/**
 * A grid of empty item slots drawn from primitives.
 *
 * <p>Forge Mantle blitted these from its own texture sheet; drawing them instead keeps the book
 * working without shipping a second set of GUI assets, and lets the book's {@code slotColor} tint
 * apply to the fill rather than to a fixed grey sprite.
 */
public class SlotElement extends SizedBookElement {
  /** Edge length of one slot including its border */
  public static final int SLOT_SIZE = 18;

  private final int columns;
  private final int rows;
  private final int color;

  public SlotElement(int x, int y, int columns, int rows, int color) {
    super(x, y, columns * SLOT_SIZE, rows * SLOT_SIZE);
    this.columns = columns;
    this.rows = rows;
    this.color = color;
  }

  @Override
  public void draw(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks, Font fontRenderer) {
    int fill = 0xFF000000 | (this.color & 0xFFFFFF);
    int shadow = 0xFF000000 | multiply(this.color, 0.65F);
    int highlight = 0xFF000000 | multiply(this.color, 1.25F);
    for (int row = 0; row < this.rows; row++) {
      for (int column = 0; column < this.columns; column++) {
        int left = this.x + column * SLOT_SIZE;
        int top = this.y + row * SLOT_SIZE;
        graphics.fill(left, top, left + SLOT_SIZE, top + SLOT_SIZE, highlight);
        graphics.fill(left, top, left + SLOT_SIZE - 1, top + SLOT_SIZE - 1, shadow);
        graphics.fill(left + 1, top + 1, left + SLOT_SIZE - 1, top + SLOT_SIZE - 1, fill);
      }
    }
  }

  /** Scales each channel of a packed colour, clamping at full brightness */
  private static int multiply(int color, float factor) {
    int r = Math.min(255, (int)(((color >> 16) & 0xFF) * factor));
    int g = Math.min(255, (int)(((color >> 8) & 0xFF) * factor));
    int b = Math.min(255, (int)((color & 0xFF) * factor));
    return (r << 16) | (g << 8) | b;
  }
}
