package slimeknights.mantle.client.screen.book.element;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import java.util.List;

/**
 * One drawable thing on a book page.
 *
 * <p>Elements are rebuilt every time a page is opened and positioned in page-local coordinates; the
 * screen translates to the page origin before drawing them, so an element never has to know which
 * half of the spread it is on.
 */
public abstract class BookElement {
  protected final Minecraft mc = Minecraft.getInstance();

  /** Screen this element belongs to; set when the page is built */
  public net.minecraft.client.gui.screens.Screen parentScreen;
  /** Book screen this element belongs to */
  public slimeknights.mantle.client.screen.book.BookScreen parent;

  public int x;
  public int y;

  public BookElement(int x, int y) {
    this.x = x;
    this.y = y;
  }

  /** Draws the element itself */
  public abstract void draw(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks, Font fontRenderer);

  /** Draws anything that must sit above every other element, tooltips above all */
  public void drawOverlay(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks, Font fontRenderer) {}

  /** Called once per frame before drawing */
  public void update(int mouseX, int mouseY) {}

  public void mouseClicked(double mouseX, double mouseY, int mouseButton) {}

  public void mouseReleased(double mouseX, double mouseY, int mouseButton) {}

  public void mouseDragged(double clickX, double clickY, double mouseX, double mouseY, double lastX, double lastY, int button) {}

  /** True if the pointer is inside this element; unsized elements are never hovered */
  public boolean isHovered(double mouseX, double mouseY) {
    return false;
  }

  /** Draws a tooltip at the pointer, in screen space rather than page space */
  public void drawTooltip(GuiGraphics graphics, List<Component> tooltip, int mouseX, int mouseY, Font fontRenderer) {
    if (tooltip == null || tooltip.isEmpty()) {
      return;
    }
    graphics.renderComponentTooltip(fontRenderer, tooltip, mouseX, mouseY);
  }
}
