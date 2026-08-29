package slimeknights.tconstruct.fabric.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import slimeknights.tconstruct.tools.client.ModifierClientEvents;

/**
 * Lets modifiers decide what the first-person hand shows.
 *
 * <p>Two cases, both from Forge's {@code RenderHandEvent}: a ballista held in the other hand should
 * not draw the melee weapon twice, and gloves or a chestplate's empty offhand should draw the arm
 * where vanilla draws nothing. Fabric has no hand-render event, so the hook goes in here.
 */
@Mixin(ItemInHandRenderer.class)
public class ItemInHandRendererMixin {
  @Inject(method = "renderArmWithItem", at = @At("HEAD"), cancellable = true)
  private void tconstruct$renderHand(AbstractClientPlayer player, float partialTicks, float pitch, InteractionHand hand,
                                     float swingProgress, ItemStack stack, float equipProgress,
                                     PoseStack matrices, MultiBufferSource buffer, int packedLight, CallbackInfo callback) {
    if (ModifierClientEvents.renderHand(hand, matrices, buffer, packedLight, equipProgress, swingProgress)) {
      callback.cancel();
    }
  }
}
