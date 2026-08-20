package slimeknights.mantle.client.book.data.element;

import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;

/**
 * A rectangle of a texture, either the whole file or one sprite out of a sheet.
 *
 * <p>Page JSON only ever gives {@code file}, {@code width} and {@code height}: the whole image
 * scaled into that box, which is why {@link #texWidth} defaults to the display size. Code-built
 * instances pass the full seven arguments to pick one sprite out of a sheet.
 */
public class ImageData {
  /** Drawn in place of an image that could not be resolved */
  public static final ImageData MISSING = new ImageData();

  /** Texture to draw from; null only for {@link #MISSING} */
  @Nullable
  public ResourceLocation file = null;
  /** Left edge of the sprite within the texture */
  public int x = 0;
  /** Top edge of the sprite within the texture */
  public int y = 0;
  /** Width the sprite is drawn at */
  public int width = -1;
  /** Height the sprite is drawn at */
  public int height = -1;
  /** Width of the source texture; defaults to the display width, which draws the whole file */
  public int texWidth = -1;
  /** Height of the source texture; defaults to the display height */
  public int texHeight = -1;
  /** Tint applied when drawing, ARGB. -1 leaves the texture untouched */
  public int colors = -1;

  public ImageData() {}

  public ImageData(ResourceLocation file, int x, int y, int width, int height, int texWidth, int texHeight) {
    this.file = file;
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
    this.texWidth = texWidth;
    this.texHeight = texHeight;
  }

  public ImageData(ResourceLocation file, int width, int height) {
    this(file, 0, 0, width, height, width, height);
  }

  /** True if this instance names a texture */
  public boolean isPresent() {
    return this.file != null;
  }

  /** Source texture width, falling back to the display width so an unsheeted file scales to fit */
  public int textureWidth() {
    return this.texWidth > 0 ? this.texWidth : Math.max(this.width, 1);
  }

  /** Source texture height, falling back to the display height */
  public int textureHeight() {
    return this.texHeight > 0 ? this.texHeight : Math.max(this.height, 1);
  }
}
