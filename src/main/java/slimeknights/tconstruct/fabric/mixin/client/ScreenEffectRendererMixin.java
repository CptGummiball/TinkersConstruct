package slimeknights.tconstruct.fabric.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ScreenEffectRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.shared.TinkerClient;

import javax.annotation.Nullable;

/**
 * Replaces Forge's {@code RenderBlockScreenEffectEvent}: when the block filling the camera is
 * tagged {@code tconstruct:transparent_overlay}, Tinkers draws its see-through overlay and
 * vanilla's opaque one is suppressed.
 *
 * <p>A redirect rather than an inject because 1.21's {@code getViewBlockingState} returns only the
 * state — the position, which the overlay needs for shape and light, is discarded inside it. The
 * handler repeats vanilla's eight-point scan (same offsets, same view-blocking test) keeping the
 * hit position in hand.
 */
@Mixin(ScreenEffectRenderer.class)
public class ScreenEffectRendererMixin {
  @Nullable
  @Redirect(method = "renderScreenEffect", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/ScreenEffectRenderer;getViewBlockingState(Lnet/minecraft/world/entity/player/Player;)Lnet/minecraft/world/level/block/state/BlockState;"))
  private static BlockState tconstruct$transparentOverlay(Player player, Minecraft minecraft, PoseStack poseStack) {
    BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
    for (int i = 0; i < 8; i++) {
      double x = player.getX() + (double) (((float) ((i >> 0) % 2) - 0.5F) * player.getBbWidth() * 0.8F);
      double y = player.getEyeY() + (double) (((float) ((i >> 1) % 2) - 0.5F) * 0.1F);
      double z = player.getZ() + (double) (((float) ((i >> 2) % 2) - 0.5F) * player.getBbWidth() * 0.8F);
      mutable.set(x, y, z);
      BlockState state = player.level().getBlockState(mutable);
      if (state.getRenderShape() != RenderShape.INVISIBLE && state.isViewBlocking(player.level(), mutable)) {
        if (state.is(TinkerTags.Blocks.TRANSPARENT_OVERLAY)) {
          TinkerClient.renderBlockOverlay(minecraft, poseStack, state, mutable.immutable());
          return null; // handled; vanilla draws no overlay for this block
        }
        return state;
      }
    }
    return null;
  }
}
