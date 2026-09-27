package slimeknights.mantle.client.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;

/**
 * Vertical scrollbar used by the side inventories and info panels.
 *
 * <p>Values run from {@link #minValue} at the top to {@link #maxValue} at the bottom, stepping by
 * {@link #increment}. The owning screen polls {@link #getValue()} to decide what to show.
 */
public class SliderWidget extends Widget {
  /** Handle in its normal state */
  protected ElementScreen slider;
  /** Handle while hovered */
  protected ElementScreen sliderHighlighted;
  /** Handle while the slider is disabled */
  protected ElementScreen sliderDisabled;
  /** Cap drawn at the top of the track */
  protected ElementScreen sliderTop;
  /** Cap drawn at the bottom of the track */
  protected ElementScreen sliderBottom;
  /** Tiled track between the two caps */
  protected ScalableElementScreen sliderBackground;

  protected int minValue = 0;
  protected int maxValue = 0;
  protected int increment = 1;
  protected int value = 0;

  /** True while the handle is being dragged */
  protected boolean scrolling = false;
  /** True while the mouse is over the slider */
  protected boolean highlighted = false;

  public SliderWidget(ElementScreen slider, ElementScreen sliderHighlighted, ElementScreen sliderDisabled,
                      ElementScreen sliderTop, ElementScreen sliderBottom, ScalableElementScreen sliderBackground) {
    this.slider = slider;
    this.sliderHighlighted = sliderHighlighted;
    this.sliderDisabled = sliderDisabled;
    this.sliderTop = sliderTop;
    this.sliderBottom = sliderBottom;
    this.sliderBackground = sliderBackground;
    this.width = Math.max(sliderBackground.w, slider.w);
    this.height = 0;
  }

  /** Sets just the height; the width is fixed by the textures */
  public void setSize(int height) {
    this.height = height;
  }

  /** Sets the value range and step size, clamping the current value into the new range */
  public void setSliderParameters(int min, int max, int increment) {
    this.minValue = min;
    this.maxValue = Math.max(min, max);
    this.increment = Math.max(1, increment);
    this.setValue(this.value);
  }

  public int getValue() {
    return this.value;
  }

  public void setValue(int value) {
    this.value = Mth.clamp(value, this.minValue, this.maxValue);
  }

  /** Number of pixels the handle can travel */
  protected int scrollableHeight() {
    return Math.max(0, this.height - this.sliderTop.h - this.sliderBottom.h - this.slider.h);
  }

  /** Pixel offset of the handle from the top of the track */
  protected int handleOffset() {
    int range = this.maxValue - this.minValue;
    if (range <= 0) {
      return 0;
    }
    return (this.value - this.minValue) * this.scrollableHeight() / range;
  }

  /** Moves the value to match a mouse Y position */
  protected void updateFromMouse(int mouseY) {
    int range = this.maxValue - this.minValue;
    if (range <= 0) {
      return;
    }
    int trackTop = this.yPos + this.sliderTop.h + this.slider.h / 2;
    int travel = this.scrollableHeight();
    if (travel <= 0) {
      return;
    }
    float progress = Mth.clamp((mouseY - trackTop) / (float)travel, 0f, 1f);
    this.setValue(this.minValue + Math.round(progress * range));
  }

  /** Called on click; returns true if the click landed on the slider */
  public boolean handleMouseClicked(int mouseX, int mouseY, int mouseButton) {
    if (!this.isEnabled() || mouseButton != 0) {
      return false;
    }
    if (this.isMouseOver(mouseX, mouseY)) {
      this.scrolling = true;
      this.updateFromMouse(mouseY);
      return true;
    }
    return false;
  }

  /** Called on mouse release, ends any drag */
  public void handleMouseReleased() {
    this.scrolling = false;
  }

  /**
   * Scroll wheel handling.
   * @param delta     Wheel delta; positive scrolls up
   * @param inBounds  Whether the caller considers the mouse to be over the scrollable area
   * @return  True if the value changed
   */
  public boolean mouseScrolled(double delta, boolean inBounds) {
    if (!this.isEnabled() || !inBounds || delta == 0) {
      return false;
    }
    int old = this.value;
    this.setValue(this.value - (int)Math.signum(delta) * this.increment);
    return old != this.value;
  }

  /** Refreshes hover state and applies any in-progress drag; call once per frame before drawing */
  public void update(int mouseX, int mouseY) {
    this.highlighted = this.isEnabled() && this.isMouseOver(mouseX, mouseY);
    if (this.scrolling) {
      this.updateFromMouse(mouseY);
    }
  }

  @Override
  public void draw(GuiGraphics graphics) {
    if (this.hidden || this.height <= 0) {
      return;
    }

    int trackX = this.xPos + (this.width - this.sliderBackground.w) / 2;
    this.sliderTop.draw(graphics, trackX, this.yPos);
    int trackHeight = this.height - this.sliderTop.h - this.sliderBottom.h;
    if (trackHeight > 0) {
      this.sliderBackground.drawScaledY(graphics, trackX, this.yPos + this.sliderTop.h, trackHeight);
    }
    this.sliderBottom.draw(graphics, trackX, this.yPos + this.height - this.sliderBottom.h);

    ElementScreen handle = !this.enabled ? this.sliderDisabled : (this.highlighted ? this.sliderHighlighted : this.slider);
    handle.draw(graphics, this.xPos + (this.width - handle.w) / 2, this.yPos + this.sliderTop.h + this.handleOffset());
  }
}
