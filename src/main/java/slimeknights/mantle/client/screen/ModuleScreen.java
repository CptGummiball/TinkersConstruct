package slimeknights.mantle.client.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;

/**
 * A screen that is drawn as part of a {@link MultiModuleScreen} rather than on its own.
 *
 * <p>The parent owns the render pass; every hook here is driven by the parent, which is why the
 * vanilla {@code renderBg}/{@code renderLabels}/{@code renderTooltip} overrides are re-declared in
 * this package: protected members of {@code AbstractContainerScreen} are not reachable from
 * {@link MultiModuleScreen} through a {@code ModuleScreen} reference, but a re-declaration here is.
 */
public class ModuleScreen<P extends MultiModuleScreen<?>, C extends AbstractContainerMenu> extends AbstractContainerScreen<C> {
  /** Screen this module belongs to */
  public final P parent;
  /** If true, the module is placed to the right of the parent instead of the left */
  protected final boolean right;
  /** If true, the module is placed below the parent */
  protected final boolean bottom;

  /** Extra X offset applied on top of the parent-relative position */
  public int xOffset = 0;
  /** Extra Y offset applied on top of the parent-relative position */
  public int yOffset = 0;

  public ModuleScreen(P parent, C container, Inventory playerInventory, Component title, boolean right, boolean bottom) {
    super(container, playerInventory, title);
    this.parent = parent;
    this.right = right;
    this.bottom = bottom;
  }

  /* Positioning */

  /**
   * Places this module relative to its parent.
   * @param parentX      Parent GUI corner X
   * @param parentY      Parent GUI corner Y
   * @param parentSizeX  Parent GUI width
   * @param parentSizeY  Parent GUI height
   */
  public void updatePosition(int parentX, int parentY, int parentSizeX, int parentSizeY) {
    this.leftPos = parentX + this.xOffset;
    this.topPos = parentY + this.yOffset;

    if (this.right) {
      this.leftPos += parentSizeX;
    } else {
      this.leftPos -= this.imageWidth;
    }
    if (this.bottom) {
      this.topPos += parentSizeY;
    }
  }

  public int guiRight() {
    return this.leftPos + this.imageWidth;
  }

  public int guiBottom() {
    return this.topPos + this.imageHeight;
  }

  /** Checks if the mouse is inside this module's area */
  public boolean isMouseInModule(int mouseX, int mouseY) {
    return mouseX >= this.leftPos && mouseX < this.guiRight()
        && mouseY >= this.topPos && mouseY < this.guiBottom();
  }

  /**
   * Checks if the mouse is over one of this module's slots that has an item in it.
   *
   * <p>Module slots carry positions relative to the parent's GUI corner, not to the module, so the
   * hover test is done against the parent rather than through {@code AbstractContainerScreen}'s.
   */
  public boolean isMouseOverFullSlot(double mouseX, double mouseY) {
    double x = mouseX - this.parent.cornerX;
    double y = mouseY - this.parent.cornerY;
    for (Slot slot : this.menu.slots) {
      if (slot.hasItem()
          && x >= slot.x - 1 && x < slot.x + 17
          && y >= slot.y - 1 && y < slot.y + 17) {
        return true;
      }
    }
    return false;
  }

  /** If false, the parent skips rendering the given slot */
  public boolean shouldDrawSlot(Slot slot) {
    return true;
  }

  /* Input, forwarded by the parent */

  public boolean handleMouseClicked(double mouseX, double mouseY, int mouseButton) {
    return false;
  }

  public boolean handleMouseClickMove(double mouseX, double mouseY, int clickedMouseButton, double timeSinceLastClick) {
    return false;
  }

  public boolean handleMouseReleased(double mouseX, double mouseY, int state) {
    return false;
  }

  public boolean handleMouseScrolled(double mouseX, double mouseY, double scrollData) {
    return false;
  }

  /* Rendering; the parent drives all three */

  @Override
  protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {}

  @Override
  protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {}

  @Override
  protected void renderTooltip(GuiGraphics graphics, int mouseX, int mouseY) {
    // deliberately does not call super: the parent screen renders the hovered slot tooltip once
  }
}
