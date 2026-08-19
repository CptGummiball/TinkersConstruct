package slimeknights.mantle.client.screen;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import slimeknights.mantle.inventory.MultiModuleContainerMenu;
import slimeknights.mantle.inventory.WrapperSlot;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Container screen composed of a main GUI plus any number of {@link ModuleScreen} side panels.
 *
 * <p>{@link #cornerX}/{@link #cornerY} and {@link #realWidth}/{@link #realHeight} mirror the
 * protected vanilla {@code leftPos}/{@code topPos}/{@code imageWidth}/{@code imageHeight} so
 * modules and widgets outside this package can position themselves against the main GUI.
 */
public class MultiModuleScreen<CONTAINER extends AbstractContainerMenu> extends AbstractContainerScreen<CONTAINER> {
  /** Modules attached to this screen */
  protected final List<ModuleScreen<?, ?>> modules = Lists.newArrayList();

  /** X position of the main GUI's top-left corner */
  public int cornerX;
  /** Y position of the main GUI's top-left corner */
  public int cornerY;
  /** Width of the main GUI, ignoring modules */
  public int realWidth;
  /** Height of the main GUI, ignoring modules */
  public int realHeight;

  public MultiModuleScreen(CONTAINER container, Inventory playerInventory, Component title) {
    super(container, playerInventory, title);
    this.realWidth = -1;
    this.realHeight = -1;
  }

  /** Attaches a module to this screen */
  protected void addModule(ModuleScreen<?, ?> module) {
    this.modules.add(module);
  }

  @Override
  protected void init() {
    if (this.realWidth > -1) {
      // reset before super so the centering maths sees the unmodified size on a resize
      this.imageWidth = this.realWidth;
      this.imageHeight = this.realHeight;
    }
    super.init();

    this.cornerX = this.leftPos;
    this.cornerY = this.topPos;
    this.realWidth = this.imageWidth;
    this.realHeight = this.imageHeight;

    for (ModuleScreen<?, ?> module : this.modules) {
      module.init(this.minecraft, this.width, this.height);
      this.updateSubmodule(module);
    }
  }

  /** Repositions a single module against the current corner */
  protected void updateSubmodule(ModuleScreen<?, ?> module) {
    module.updatePosition(this.cornerX, this.cornerY, this.realWidth, this.realHeight);
  }

  /** Repositions every module; call after moving the main GUI */
  protected void updateSubmodules() {
    for (ModuleScreen<?, ?> module : this.modules) {
      this.updateSubmodule(module);
    }
  }

  /* Slot plumbing */

  /** Finds the module owning the given slot index, or null if the main GUI owns it */
  @Nullable
  protected ModuleScreen<?, ?> getModuleForSlot(int slotNumber) {
    if (!(this.menu instanceof MultiModuleContainerMenu<?> multi)) {
      return null;
    }
    AbstractContainerMenu sub = multi.getSlotContainer(slotNumber);
    if (sub == this.menu) {
      return null;
    }
    for (ModuleScreen<?, ?> module : this.modules) {
      if (module.getMenu() == sub) {
        return module;
      }
    }
    return null;
  }

  /**
   * Copies positions from the module-owned slots onto the wrappers the parent menu actually renders.
   * Modules move their own slots during {@code renderBg}, so this runs right after that pass.
   */
  protected void syncWrapperSlots() {
    for (Slot slot : this.menu.slots) {
      if (slot instanceof WrapperSlot wrapper) {
        slot.x = wrapper.parent.x;
        slot.y = wrapper.parent.y;
      }
    }
  }

  @Override
  protected void renderSlot(GuiGraphics graphics, Slot slot) {
    ModuleScreen<?, ?> module = this.getModuleForSlot(slot.index);
    if (module != null) {
      Slot target = slot instanceof WrapperSlot wrapper ? wrapper.parent : slot;
      if (!module.shouldDrawSlot(target)) {
        return;
      }
    }
    super.renderSlot(graphics, slot);
  }

  @Override
  protected boolean isHovering(Slot slot, double mouseX, double mouseY) {
    ModuleScreen<?, ?> module = this.getModuleForSlot(slot.index);
    if (module != null) {
      Slot target = slot instanceof WrapperSlot wrapper ? wrapper.parent : slot;
      if (!module.shouldDrawSlot(target)) {
        return false;
      }
    }
    return super.isHovering(slot, mouseX, mouseY);
  }

  /* Rendering */

  @Override
  public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
    // 1.21 reordered the render pass: Screen#render calls renderBackground, which is what reaches
    // renderBg, so subclasses only add the tooltip pass on top of super.
    super.render(graphics, mouseX, mouseY, partialTicks);
    this.renderTooltip(graphics, mouseX, mouseY);
  }

  /** Draws the main GUI background at the real corner */
  protected void drawBackground(GuiGraphics graphics, ResourceLocation background) {
    graphics.blit(background, this.cornerX, this.cornerY, 0, 0, this.realWidth, this.realHeight);
  }

  @Override
  protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
    for (ModuleScreen<?, ?> module : this.modules) {
      module.renderBg(graphics, partialTicks, mouseX, mouseY);
    }
    // modules reposition their slots while drawing, so refresh the wrappers afterwards
    this.syncWrapperSlots();
  }

  /** Draws the container's own name; the pose is already translated to the GUI corner */
  protected void drawContainerName(GuiGraphics graphics) {
    graphics.drawString(this.font, this.getTitle(), this.titleLabelX, this.titleLabelY, 0x404040, false);
  }

  /** Draws the player inventory name; the pose is already translated to the GUI corner */
  protected void drawPlayerInventoryName(GuiGraphics graphics) {
    graphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 0x404040, false);
  }

  @Override
  protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
    this.drawContainerName(graphics);
    this.drawPlayerInventoryName(graphics);

    PoseStack pose = graphics.pose();
    for (ModuleScreen<?, ?> module : this.modules) {
      pose.pushPose();
      // labels are drawn corner-relative, so shift into each module's own space
      pose.translate(module.leftPos - this.leftPos, module.topPos - this.topPos, 0);
      module.renderLabels(graphics, mouseX, mouseY);
      pose.popPose();
    }
  }

  @Override
  protected void renderTooltip(GuiGraphics graphics, int mouseX, int mouseY) {
    super.renderTooltip(graphics, mouseX, mouseY);
    for (ModuleScreen<?, ?> module : this.modules) {
      module.renderTooltip(graphics, mouseX, mouseY);
    }
  }

  /** Areas covered by this screen's modules, for recipe viewers to exclude */
  public List<Rect2i> getModuleAreas() {
    List<Rect2i> areas = Lists.newArrayList();
    for (ModuleScreen<?, ?> module : this.modules) {
      areas.add(new Rect2i(module.leftPos, module.topPos, module.imageWidth, module.imageHeight));
    }
    return areas;
  }

  /* Input */

  @Override
  public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
    for (ModuleScreen<?, ?> module : this.modules) {
      if (module.handleMouseClicked(mouseX, mouseY, mouseButton)) {
        return false;
      }
    }
    return super.mouseClicked(mouseX, mouseY, mouseButton);
  }

  @Override
  public boolean mouseDragged(double mouseX, double mouseY, int clickedMouseButton, double dragX, double dragY) {
    for (ModuleScreen<?, ?> module : this.modules) {
      if (module.handleMouseClickMove(mouseX, mouseY, clickedMouseButton, dragX)) {
        return false;
      }
    }
    return super.mouseDragged(mouseX, mouseY, clickedMouseButton, dragX, dragY);
  }

  @Override
  public boolean mouseReleased(double mouseX, double mouseY, int state) {
    for (ModuleScreen<?, ?> module : this.modules) {
      if (module.handleMouseReleased(mouseX, mouseY, state)) {
        return false;
      }
    }
    return super.mouseReleased(mouseX, mouseY, state);
  }

  /** 1.21 note: {@code mouseScrolled} gained a horizontal delta; modules still take the vertical one. */
  @Override
  public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
    for (ModuleScreen<?, ?> module : this.modules) {
      if (module.handleMouseScrolled(mouseX, mouseY, scrollY)) {
        return false;
      }
    }
    return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
  }

  @Override
  protected boolean hasClickedOutside(double mouseX, double mouseY, int guiLeft, int guiTop, int mouseButton) {
    boolean outside = super.hasClickedOutside(mouseX, mouseY, guiLeft, guiTop, mouseButton);
    if (!outside) {
      return false;
    }
    for (ModuleScreen<?, ?> module : this.modules) {
      if (module.isMouseInModule((int)mouseX, (int)mouseY)) {
        return false;
      }
    }
    return true;
  }
}
