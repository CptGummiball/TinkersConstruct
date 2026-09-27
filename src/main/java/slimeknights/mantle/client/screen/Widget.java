package slimeknights.mantle.client.screen;

import net.minecraft.client.gui.GuiGraphics;

/**
 * Lightweight positioned GUI element, drawn manually by the owning screen.
 *
 * <p>Deliberately not a vanilla {@code AbstractWidget}: these are drawn from inside
 * {@code renderBg} at arbitrary depths and do not take part in vanilla's focus/narration
 * handling.
 */
public abstract class Widget {
  /** Screen X position of the top left corner */
  public int xPos = 0;
  /** Screen Y position of the top left corner */
  public int yPos = 0;
  /** Widget width */
  public int width = 0;
  /** Widget height */
  public int height = 0;

  /** If false, the widget is drawn greyed out and ignores input */
  protected boolean enabled = true;
  /** If true, the widget is not drawn at all */
  protected boolean hidden = false;

  public void setPosition(int xPos, int yPos) {
    this.xPos = xPos;
    this.yPos = yPos;
  }

  public void setSize(int width, int height) {
    this.width = width;
    this.height = height;
  }

  public boolean isEnabled() {
    return this.enabled && !this.hidden;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public void enable() {
    this.setEnabled(true);
  }

  public void disable() {
    this.setEnabled(false);
  }

  public boolean isHidden() {
    return this.hidden;
  }

  public void setHidden(boolean hidden) {
    this.hidden = hidden;
  }

  public void show() {
    this.setHidden(false);
  }

  public void hide() {
    this.setHidden(true);
  }

  /** Checks if the given screen position is inside this widget */
  public boolean isMouseOver(double mouseX, double mouseY) {
    return mouseX >= this.xPos && mouseX < this.xPos + this.width
        && mouseY >= this.yPos && mouseY < this.yPos + this.height;
  }

  /** Draws the widget */
  public abstract void draw(GuiGraphics graphics);
}
