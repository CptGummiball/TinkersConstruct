package slimeknights.mantle.client.screen.book.element;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import com.mojang.math.Axis;
import slimeknights.mantle.Mantle;

import java.util.List;

/**
 * A turnable, layer-by-layer preview of a multiblock.
 *
 * <p>Blocks are drawn one at a time through the normal block renderer rather than baked into a
 * single mesh: the structures are a few dozen blocks, so the simple path costs nothing, and it
 * keeps every block looking exactly as it does in the world.
 */
public class StructureElement extends SizedBookElement {
  /** How far the view tips towards the viewer */
  private static final float PITCH = 30F;
  /** Degrees per press of a rotate button */
  private static final float ROTATION_STEP = 45F;

  private final List<StructureBlock> blocks;
  private final BlockPos size;

  private float rotation = 45F;
  /** Highest layer drawn; -1 shows the whole structure */
  private int layer = -1;

  public StructureElement(int x, int y, int width, int height, List<StructureBlock> blocks, BlockPos size) {
    super(x, y, width, height);
    this.blocks = blocks;
    this.size = size;
  }

  /** Turns the structure by one step */
  public void rotate(int direction) {
    this.rotation = (this.rotation + direction * ROTATION_STEP) % 360F;
  }

  /**
   * Steps the visible layer.
   *
   * <p>Stepping past the top returns to showing everything, so a reader can cycle without hunting
   * for a reset.
   */
  public void changeLayer(int direction) {
    int height = Math.max(1, this.size.getY());
    if (this.layer < 0) {
      this.layer = direction > 0 ? 0 : height - 1;
      return;
    }
    this.layer += direction;
    if (this.layer < 0 || this.layer >= height) {
      this.layer = -1;
    }
  }

  @Override
  public void draw(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks, Font fontRenderer) {
    if (this.blocks.isEmpty()) {
      return;
    }
    int span = Math.max(1, Math.max(this.size.getX(), Math.max(this.size.getY(), this.size.getZ())));
    float scale = Math.min(this.width, this.height) / (span * 1.8F);

    BlockRenderDispatcher dispatcher = this.mc.getBlockRenderer();
    MultiBufferSource.BufferSource buffers = graphics.bufferSource();
    PoseStack pose = graphics.pose();

    graphics.flush();
    RenderSystem.enableDepthTest();
    Lighting.setupFor3DItems();

    pose.pushPose();
    pose.translate(this.x + this.width / 2F, this.y + this.height / 2F, 100F);
    // GUI space has y growing downwards and z growing towards the viewer
    pose.scale(scale, -scale, scale);
    pose.mulPose(Axis.XP.rotationDegrees(PITCH));
    pose.mulPose(Axis.YP.rotationDegrees(this.rotation));
    pose.translate(-this.size.getX() / 2F, -this.size.getY() / 2F, -this.size.getZ() / 2F);

    try {
      for (StructureBlock block : this.blocks) {
        if (this.layer >= 0 && block.pos().getY() != this.layer) {
          continue;
        }
        pose.pushPose();
        pose.translate(block.pos().getX(), block.pos().getY(), block.pos().getZ());
        dispatcher.renderSingleBlock(block.state(), pose, buffers, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY);
        pose.popPose();
      }
    } catch (Exception e) {
      Mantle.logger.error("Failed to render book structure", e);
    }

    pose.popPose();
    graphics.flush();
    Lighting.setupForFlatItems();
  }

  /** One block of the structure */
  public record StructureBlock(BlockPos pos, BlockState state) {}
}
