package slimeknights.mantle.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.joml.Vector3f;

import javax.annotation.Nullable;

/**
 * Shared pieces of the block entity renderers: block rotation, displayed items and the fluid a
 * faucet pours into the block below.
 *
 * <p>Written for this tree; Mantle's client packages were never copied in.
 */
public final class RenderingHelper {
  private RenderingHelper() {}

  /* Rotation */

  /**
   * Rotates the matrix stack to match the block's facing, if it has one.
   * @return true if the stack was pushed and the caller must pop it
   */
  public static boolean applyRotation(PoseStack matrices, BlockState state) {
    if (state.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
      return applyRotation(matrices, state.getValue(BlockStateProperties.HORIZONTAL_FACING));
    }
    if (state.hasProperty(BlockStateProperties.FACING)) {
      return applyRotation(matrices, state.getValue(BlockStateProperties.FACING));
    }
    return false;
  }

  /**
   * Rotates the matrix stack around the block center so a model authored facing south points the
   * given way. Vertical directions and south need no rotation.
   * @return true if the stack was pushed and the caller must pop it
   */
  public static boolean applyRotation(PoseStack matrices, Direction direction) {
    if (direction.getAxis().isVertical() || direction == Direction.SOUTH) {
      return false;
    }
    matrices.pushPose();
    matrices.translate(0.5f, 0.5f, 0.5f);
    matrices.mulPose(Axis.YP.rotationDegrees(-90f * direction.get2DDataValue()));
    matrices.translate(-0.5f, -0.5f, -0.5f);
    return true;
  }


  /* Items */

  /** Renders an item at the placement the block's data map gives it */
  public static void renderItem(PoseStack matrices, MultiBufferSource buffer, ItemStack stack, RenderItem item, int light) {
    if (stack.isEmpty() || item.isHidden()) {
      return;
    }
    Minecraft mc = Minecraft.getInstance();
    matrices.pushPose();
    Vector3f center = item.center();
    matrices.translate(center.x() / 16f, center.y() / 16f, center.z() / 16f);
    if (item.x() != 0) {
      matrices.mulPose(Axis.XP.rotationDegrees(item.x()));
    }
    if (item.y() != 0) {
      matrices.mulPose(Axis.YP.rotationDegrees(item.y()));
    }
    float scale = item.size() / 16f;
    matrices.scale(scale, scale, scale);
    mc.getItemRenderer().renderStatic(stack, item.transform(), light, OverlayTexture.NO_OVERLAY, matrices, buffer, mc.level, 0);
    matrices.popPose();
  }


  /* Faucets */

  /**
   * Draws the fluid entering the block below the pouring block, if that block declares any.
   *
   * <p>The matrix is expected to still be at the pouring block's origin with its rotation applied,
   * as both the faucet and the channel renderers call this in the middle of their own rendering.
   *
   * @param world      Level to look up the receiving block in
   * @param pos        Position of the pouring block
   * @param direction  Facing of the pouring block; down means the fluid falls through the center
   */
  public static void renderFaucetFluids(Level world, BlockPos pos, Direction direction, PoseStack matrices, VertexConsumer buffer,
                                        @Nullable TextureAtlasSprite still, @Nullable TextureAtlasSprite flowing, int color, int light) {
    if (still == null) {
      return;
    }
    BlockPos below = pos.below();
    FaucetFluid model = FaucetFluid.REGISTRY.getNullable(world.getBlockState(below));
    if (model == null) {
      return;
    }
    FluidCuboid cube = model.forDirection(direction);
    if (cube == null) {
      return;
    }
    matrices.pushPose();
    matrices.translate(0, -1, 0);
    FluidRenderer.renderCuboid(matrices, buffer, cube, still, flowing, cube.getFromScaled(), cube.getToScaled(), color, light, false);
    matrices.popPose();
  }
}
