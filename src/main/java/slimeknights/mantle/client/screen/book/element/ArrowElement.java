package slimeknights.mantle.client.screen.book.element;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import slimeknights.mantle.client.screen.book.ArrowButton;
import slimeknights.mantle.client.screen.book.ArrowButton.ArrowType;

import javax.annotation.Nullable;

/**
 * An arrow a page places itself, as opposed to the page-turn arrows the screen owns.
 *
 * <p>Wraps a real {@link ArrowButton} so the hover and press behaviour is vanilla's, but positions
 * it in page coordinates like every other element.
 */
public class ArrowElement extends SizedBookElement {
  /** The button this element drives; subclasses fire it from their own click handling */
  @Nullable
  protected ArrowButton button;

  private final ArrowType arrowType;
  private final int arrowColor;
  private final int arrowColorHover;

  public ArrowElement(int x, int y, ArrowType arrowType, int arrowColor, int arrowColorHover, @Nullable Button.OnPress onPress) {
    super(x, y, arrowType.w, arrowType.h);
    this.arrowType = arrowType;
    this.arrowColor = arrowColor;
    this.arrowColorHover = arrowColorHover;
    if (onPress != null) {
      this.button = new ArrowButton(null, x, y, arrowType, arrowColor, arrowColorHover, onPress);
    }
  }

  @Override
  public void draw(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks, Font fontRenderer) {
    if (this.button == null) {
      // decorative arrow, most often the one between a recipe and its result
      this.button = new ArrowButton(bookTexture(), this.x, this.y, this.arrowType, this.arrowColor, this.arrowColorHover, b -> {});
      this.button.active = false;
    }
    this.button.bookTexture = bookTexture();
    this.button.setX(this.x);
    this.button.setY(this.y);
    this.button.render(graphics, mouseX, mouseY, partialTicks);
  }

  @Override
  public void mouseClicked(double mouseX, double mouseY, int mouseButton) {
    if (this.button != null && this.button.active && this.isHovered(mouseX, mouseY)) {
      this.button.playDownSound(this.mc.getSoundManager());
      this.button.onPress();
    }
  }

  /** Texture the arrow sprite comes from, taken from the book being displayed */
  @Nullable
  private net.minecraft.resources.ResourceLocation bookTexture() {
    return this.parent == null ? null : this.parent.book.appearance.bookTexture;
  }
}
