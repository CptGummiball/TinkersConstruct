package slimeknights.mantle.client.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

/**
 * An {@link ElementScreen} that can be tiled along either axis to fill an arbitrary area.
 * Used for GUI borders, slot rows, progress bars and scrollbar tracks.
 */
public class ScalableElementScreen extends ElementScreen {
  public ScalableElementScreen(ResourceLocation texture, int x, int y, int w, int h, int texW, int texH) {
    super(texture, x, y, w, h, texW, texH);
  }

  public ScalableElementScreen(ResourceLocation texture, int x, int y, int w, int h) {
    super(texture, x, y, w, h);
  }

  @Override
  public ScalableElementScreen shift(int xd, int yd) {
    return new ScalableElementScreen(this.texture, this.x + xd, this.y + yd, this.w, this.h, this.texW, this.texH);
  }

  @Override
  public ScalableElementScreen move(int x, int y, int w, int h) {
    return new ScalableElementScreen(this.texture, x, y, w, h, this.texW, this.texH);
  }

  /** Draws a partial slice of this element, clipping from the top left corner */
  protected void drawPartial(GuiGraphics graphics, int xPos, int yPos, int width, int height) {
    graphics.blit(this.texture, xPos, yPos, this.x, this.y, width, height, this.texW, this.texH);
  }

  /**
   * Tiles this element horizontally over the given width
   * @return  The width drawn, so callers can chain positions
   */
  public int drawScaledX(GuiGraphics graphics, int xPos, int yPos, int width) {
    int drawn = 0;
    while (drawn < width) {
      int step = Math.min(this.w, width - drawn);
      this.drawPartial(graphics, xPos + drawn, yPos, step, this.h);
      drawn += step;
    }
    return width;
  }

  /**
   * Tiles this element vertically over the given height
   * @return  The element width, so callers can chain positions
   */
  public int drawScaledY(GuiGraphics graphics, int xPos, int yPos, int height) {
    int drawn = 0;
    while (drawn < height) {
      int step = Math.min(this.h, height - drawn);
      this.drawPartial(graphics, xPos, yPos + drawn, this.w, step);
      drawn += step;
    }
    return this.w;
  }

  /**
   * Draws this element scaled vertically but anchored to the bottom, so it grows upwards.
   * Used by the smeltery fuel flame.
   */
  public int drawScaledYUp(GuiGraphics graphics, int xPos, int yPos, int height) {
    if (height <= 0) {
      return this.w;
    }
    // the bar's bottom edge is the element box's bottom edge, and it fills upwards from there
    int bottom = yPos + this.h;
    int remaining = height;
    while (remaining > 0) {
      int step = Math.min(this.h, remaining);
      int top = bottom - step;
      // clip the slice out of the bottom of the sprite so partial fills read as a rising bar
      graphics.blit(this.texture, xPos, top, this.x, this.y + (this.h - step), this.w, step, this.texW, this.texH);
      bottom = top;
      remaining -= step;
    }
    return this.w;
  }

  /** Tiles this element over a rectangular area */
  public int drawScaled(GuiGraphics graphics, int xPos, int yPos, int width, int height) {
    int drawnY = 0;
    while (drawnY < height) {
      int stepY = Math.min(this.h, height - drawnY);
      int drawnX = 0;
      while (drawnX < width) {
        int stepX = Math.min(this.w, width - drawnX);
        this.drawPartial(graphics, xPos + drawnX, yPos + drawnY, stepX, stepY);
        drawnX += stepX;
      }
      drawnY += stepY;
    }
    return width;
  }
}
