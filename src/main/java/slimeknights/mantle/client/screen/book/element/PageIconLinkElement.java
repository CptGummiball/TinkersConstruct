package slimeknights.mantle.client.screen.book.element;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import slimeknights.mantle.client.book.action.StringActionProcessor;
import slimeknights.mantle.client.book.data.PageData;

import javax.annotation.Nullable;

/** An icon on an index page that links to another page */
public class PageIconLinkElement extends SizedBookElement {
  public PageData pageData;
  public SizedBookElement displayElement;
  public String action;
  @Nullable
  public Component name;

  public PageIconLinkElement(int x, int y, SizedBookElement displayElement, @Nullable Component name, PageData pageData) {
    this(x, y, displayElement.width, displayElement.height, displayElement, name, pageData);
  }

  public PageIconLinkElement(int x, int y, int width, int height, SizedBookElement displayElement, @Nullable Component name, PageData pageData) {
    super(x, y, width, height);
    this.displayElement = displayElement;
    this.pageData = pageData;
    this.action = "mantle:go-to-page-rtn " + pageData.parent.name + "." + pageData.name;
    this.name = name;
  }

  @Override
  public void draw(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks, Font fontRenderer) {
    if (this.isHovered(mouseX, mouseY) && this.parent != null) {
      graphics.fill(this.x, this.y, this.x + this.width, this.y + this.height,
                    (this.parent.book.appearance.hoverColor & 0xFFFFFF) | (0x77 << 24));
    }
    RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
    this.displayElement.parent = this.parent;
    this.displayElement.draw(graphics, mouseX, mouseY, partialTicks, fontRenderer);
  }

  @Override
  public void drawOverlay(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks, Font fontRenderer) {
    if (this.name != null && !this.name.getString().isEmpty() && this.isHovered(mouseX, mouseY)) {
      this.drawTooltip(graphics, ImmutableList.of(this.name), mouseX, mouseY, fontRenderer);
    }
  }

  @Override
  public void mouseClicked(double mouseX, double mouseY, int mouseButton) {
    if (this.isHovered(mouseX, mouseY)) {
      StringActionProcessor.process(this.action, this.parent);
    }
  }
}
