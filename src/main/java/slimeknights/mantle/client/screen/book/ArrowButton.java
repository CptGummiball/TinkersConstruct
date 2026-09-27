package slimeknights.mantle.client.screen.book;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;

/**
 * The arrows a book navigates with.
 *
 * <p>Sprites come out of the book's own page texture, which is why the arrow matches each book's
 * palette; the coordinates below are the strip along its right edge. A book with no texture falls
 * back to a drawn triangle so navigation never disappears.
 */
public class ArrowButton extends Button {
  /** Size of the book texture sheet */
  public static final int TEX_SIZE = 512;

  public final ArrowType arrowType;
  public int color;
  public int hoverColor;
  @Nullable
  public ResourceLocation bookTexture;

  public ArrowButton(@Nullable ResourceLocation bookTexture, int x, int y, ArrowType arrowType, int color, int hoverColor, OnPress onPress) {
    super(x, y, arrowType.w, arrowType.h, Component.empty(), onPress, DEFAULT_NARRATION);
    this.bookTexture = bookTexture;
    this.arrowType = arrowType;
    this.color = color;
    this.hoverColor = hoverColor;
  }

  @Override
  public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
    int color = this.isHoveredOrFocused() ? this.hoverColor : this.color;
    float red = ((color >> 16) & 0xFF) / 255F;
    float green = ((color >> 8) & 0xFF) / 255F;
    float blue = (color & 0xFF) / 255F;

    if (this.bookTexture == null) {
      drawFallback(graphics, 0xFF000000 | (color & 0xFFFFFF));
      return;
    }
    RenderSystem.enableBlend();
    RenderSystem.setShaderColor(red, green, blue, this.alpha);
    graphics.blit(this.bookTexture, this.getX(), this.getY(), this.arrowType.x, this.arrowType.y,
                  this.arrowType.w, this.arrowType.h, TEX_SIZE, TEX_SIZE);
    RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
  }

  /** Draws a plain triangle when the book has no texture to take a sprite from */
  private void drawFallback(GuiGraphics graphics, int color) {
    int w = this.arrowType.w;
    int h = this.arrowType.h;
    int x = this.getX();
    int y = this.getY();
    switch (this.arrowType) {
      case UP -> {
        for (int i = 0; i < h; i++) {
          int half = Math.max(1, w * i / (2 * h));
          graphics.fill(x + w / 2 - half, y + i, x + w / 2 + half, y + i + 1, color);
        }
      }
      case DOWN -> {
        for (int i = 0; i < h; i++) {
          int half = Math.max(1, w * (h - i) / (2 * h));
          graphics.fill(x + w / 2 - half, y + i, x + w / 2 + half, y + i + 1, color);
        }
      }
      case LEFT, PREV, BACK -> {
        for (int i = 0; i < w; i++) {
          int half = Math.max(1, h * (w - i) / (2 * w));
          graphics.fill(x + i, y + h / 2 - half, x + i + 1, y + h / 2 + half, color);
        }
      }
      default -> {
        for (int i = 0; i < w; i++) {
          int half = Math.max(1, h * i / (2 * w));
          graphics.fill(x + i, y + h / 2 - half, x + i + 1, y + h / 2 + half, color);
        }
      }
    }
  }

  @Override
  public void updateWidgetNarration(NarrationElementOutput output) {
    this.defaultButtonNarrationText(output);
  }

  /**
   * The arrow sprites, as laid out down the right edge of a book's page texture.
   *
   * <p>Fields are public because page code positions arrows relative to their width.
   */
  public enum ArrowType {
    /** Curved arrow used for the next section */
    NEXT(412, 0, 18, 10),
    /** Curved arrow used for the previous section */
    PREV(412, 10, 18, 10),
    /** Plain right arrow, the next page */
    RIGHT(412, 20, 18, 10),
    /** Plain left arrow, the previous page */
    LEFT(412, 30, 18, 10),
    /** Hooked arrow returning to where a link was followed from */
    BACK(412, 41, 14, 16),
    UP(412, 58, 10, 18),
    DOWN(422, 58, 10, 18),
    REFRESH(412, 76, 18, 18);

    public final int x;
    public final int y;
    public final int w;
    public final int h;

    ArrowType(int x, int y, int w, int h) {
      this.x = x;
      this.y = y;
      this.w = w;
      this.h = h;
    }
  }
}
