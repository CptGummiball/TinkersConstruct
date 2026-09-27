package slimeknights.mantle.client.screen.book.element;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import slimeknights.mantle.client.book.data.element.ImageData;

/**
 * Draws one image, either a whole file scaled into a box or one sprite out of a sheet.
 *
 * <p>A width or height of -1 means "use the size the image data declares", which is how page code
 * places a sprite without repeating its dimensions.
 */
public class ImageElement extends SizedBookElement {
  /** Colour used when the image data names no texture */
  private static final int MISSING_A = 0xFFF800F8;
  private static final int MISSING_B = 0xFF000000;

  protected final ImageData image;
  /** Tint applied to the texture, RGB */
  protected final int color;

  public ImageElement(int x, int y, int width, int height, ImageData image) {
    this(x, y, width, height, image, 0xFFFFFF);
  }

  public ImageElement(int x, int y, int width, int height, ImageData image, int color) {
    super(x, y, width < 0 ? image.width : width, height < 0 ? image.height : height);
    this.image = image;
    this.color = color;
  }

  @Override
  public void draw(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks, Font fontRenderer) {
    if (this.image == null || this.image.file == null) {
      drawMissing(graphics);
      return;
    }
    RenderSystem.enableBlend();
    RenderSystem.setShaderColor(((this.color >> 16) & 0xFF) / 255F, ((this.color >> 8) & 0xFF) / 255F, (this.color & 0xFF) / 255F, 1F);
    graphics.blit(this.image.file, this.x, this.y, this.image.x, this.image.y,
                  this.width, this.height, this.image.textureWidth(), this.image.textureHeight());
    RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
  }

  /** Checkerboard stand-in, so a missing image is obvious without spamming the texture manager */
  private void drawMissing(GuiGraphics graphics) {
    int half = Math.max(1, Math.min(this.width, this.height) / 2);
    for (int y = 0; y < this.height; y += half) {
      for (int x = 0; x < this.width; x += half) {
        boolean even = ((x / half) + (y / half)) % 2 == 0;
        graphics.fill(this.x + x, this.y + y,
                      this.x + Math.min(x + half, this.width), this.y + Math.min(y + half, this.height),
                      even ? MISSING_A : MISSING_B);
      }
    }
  }
}
