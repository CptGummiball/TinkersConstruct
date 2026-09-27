package slimeknights.mantle.client.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

/**
 * Generic container screen whose only customisation is the background texture, letting one menu
 * type serve several blocks.
 */
public class BackgroundContainerScreen<T extends AbstractContainerMenu> extends AbstractContainerScreen<T> {
  private final ResourceLocation background;

  public BackgroundContainerScreen(T menu, Inventory playerInventory, Component title, int height, ResourceLocation background) {
    super(menu, playerInventory, title);
    this.background = background;
    this.imageHeight = height;
    this.inventoryLabelY = this.imageHeight - 94;
  }

  @Override
  public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
    super.render(graphics, mouseX, mouseY, partialTicks);
    this.renderTooltip(graphics, mouseX, mouseY);
  }

  @Override
  protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
    graphics.blit(this.background, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
  }
}
