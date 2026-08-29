package slimeknights.mantle.client.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

/**
 * Immutable description of a rectangular region of a GUI texture, plus the blit calls to draw it.
 *
 * <p>Fabric 1.21 port note: 1.20's {@code GuiGraphics#blit(int,int,int,int,int,int)} forms all take
 * an explicit {@link ResourceLocation} now, so the texture travels with the element instead of being
 * bound separately through {@code RenderSystem.setShaderTexture}.
 */
public class ElementScreen {
  /** Texture this element lives on */
  public final ResourceLocation texture;
  /** Texture U position */
  public final int x;
  /** Texture V position */
  public final int y;
  /** Element width */
  public final int w;
  /** Element height */
  public final int h;
  /** Width of the full texture sheet */
  public final int texW;
  /** Height of the full texture sheet */
  public final int texH;

  public ElementScreen(ResourceLocation texture, int x, int y, int w, int h, int texW, int texH) {
    this.texture = texture;
    this.x = x;
    this.y = y;
    this.w = w;
    this.h = h;
    this.texW = texW;
    this.texH = texH;
  }

  public ElementScreen(ResourceLocation texture, int x, int y, int w, int h) {
    this(texture, x, y, w, h, 256, 256);
  }

  /** Creates a copy of this element offset by the given amount, keeping the size */
  public ElementScreen shift(int xd, int yd) {
    return new ElementScreen(this.texture, this.x + xd, this.y + yd, this.w, this.h, this.texW, this.texH);
  }

  /** Creates a new element on the same texture sheet at the given position and size */
  public ElementScreen move(int x, int y, int w, int h) {
    return new ElementScreen(this.texture, x, y, w, h, this.texW, this.texH);
  }

  /**
   * Draws this element at the given screen position
   * @param graphics  Graphics context
   * @param xPos      Screen X
   * @param yPos      Screen Y
   * @return  Width drawn, for chaining
   */
  public int draw(GuiGraphics graphics, int xPos, int yPos) {
    graphics.blit(this.texture, xPos, yPos, this.x, this.y, this.w, this.h, this.texW, this.texH);
    return this.w;
  }

  /**
   * Draws this element at the given screen position with an explicit render depth
   * @param graphics  Graphics context
   * @param xPos      Screen X
   * @param yPos      Screen Y
   * @param zPos      Render depth
   * @return  Width drawn, for chaining
   */
  public int draw(GuiGraphics graphics, int xPos, int yPos, int zPos) {
    graphics.blit(this.texture, xPos, yPos, zPos, (float)this.x, (float)this.y, this.w, this.h, this.texW, this.texH);
    return this.w;
  }
}
