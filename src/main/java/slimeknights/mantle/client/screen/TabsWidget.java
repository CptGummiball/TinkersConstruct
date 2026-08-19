package slimeknights.mantle.client.screen;

import com.google.common.collect.Lists;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * Row of item-icon tabs drawn above a container screen.
 *
 * <p>Selection is applied in {@link #update(int, int)} rather than directly in
 * {@link #handleMouseClicked(int, int, int)} so the owning widget can detect the change during its
 * render pass and fire the "tab changed" side effect exactly once.
 */
public class TabsWidget extends Widget {
  /** Pixels between two tabs */
  public int spacing = 0;
  /** Index of the currently selected tab */
  public int selected = 0;
  /** Index of the tab under the mouse, or -1 */
  public int highlighted = -1;

  /** Set by a click, committed by the next update */
  private int pendingSelection = -1;

  private final Screen parent;
  private final ElementScreen tabLeft, tabCenter, tabRight;
  private final ElementScreen activeLeft, activeCenter, activeRight;
  private final List<ItemStack> tabs = Lists.newArrayList();

  public TabsWidget(Screen parent, ElementScreen tabLeft, ElementScreen tabCenter, ElementScreen tabRight,
                    ElementScreen activeLeft, ElementScreen activeCenter, ElementScreen activeRight) {
    this.parent = parent;
    this.tabLeft = tabLeft;
    this.tabCenter = tabCenter;
    this.tabRight = tabRight;
    this.activeLeft = activeLeft;
    this.activeCenter = activeCenter;
    this.activeRight = activeRight;
    this.height = activeCenter.h;
  }

  /** Adds a tab displaying the given stack */
  public void addTab(ItemStack stack) {
    this.tabs.add(stack);
    this.width = this.tabs.size() * this.tabCenter.w + Math.max(0, this.tabs.size() - 1) * this.spacing;
  }

  public int tabCount() {
    return this.tabs.size();
  }

  /** Screen X of the given tab */
  protected int tabX(int index) {
    return this.xPos + index * (this.tabCenter.w + this.spacing);
  }

  /** Index of the tab at the given screen position, or -1 */
  protected int tabAt(int mouseX, int mouseY) {
    if (mouseY < this.yPos || mouseY >= this.yPos + this.height) {
      return -1;
    }
    for (int i = 0; i < this.tabs.size(); i++) {
      int x = this.tabX(i);
      if (mouseX >= x && mouseX < x + this.tabCenter.w) {
        return i;
      }
    }
    return -1;
  }

  /** Picks the left/center/right variant for a tab position */
  private ElementScreen element(int index, boolean active) {
    if (this.tabs.size() == 1) {
      return active ? this.activeCenter : this.tabCenter;
    }
    if (index == 0) {
      return active ? this.activeLeft : this.tabLeft;
    }
    if (index == this.tabs.size() - 1) {
      return active ? this.activeRight : this.tabRight;
    }
    return active ? this.activeCenter : this.tabCenter;
  }

  public boolean handleMouseClicked(int mouseX, int mouseY, int mouseButton) {
    if (this.hidden || mouseButton != 0) {
      return false;
    }
    int index = this.tabAt(mouseX, mouseY);
    if (index >= 0 && index != this.selected) {
      this.pendingSelection = index;
      return true;
    }
    return index >= 0;
  }

  public void handleMouseReleased() {
    // nothing to release, kept for symmetry with the other widgets
  }

  public void update(int mouseX, int mouseY) {
    this.highlighted = this.hidden ? -1 : this.tabAt(mouseX, mouseY);
    if (this.pendingSelection >= 0) {
      this.selected = this.pendingSelection;
      this.pendingSelection = -1;
    }
  }

  @Override
  public void draw(GuiGraphics graphics) {
    if (this.hidden) {
      return;
    }
    for (int i = 0; i < this.tabs.size(); i++) {
      boolean active = i == this.selected;
      ElementScreen element = this.element(i, active);
      int x = this.tabX(i);
      element.draw(graphics, x, this.yPos);
      ItemStack stack = this.tabs.get(i);
      if (!stack.isEmpty()) {
        // icon sits centered in the tab, nudged down to clear the tab's top bevel
        graphics.renderItem(stack, x + (element.w - 16) / 2, this.yPos + 7);
      }
    }
  }

  /** The screen owning this widget, exposed for subclasses that need its font or client */
  public Screen getParent() {
    return this.parent;
  }
}
