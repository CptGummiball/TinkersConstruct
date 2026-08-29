package slimeknights.tconstruct.fabric.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import slimeknights.mantle.client.model.BakedModelWrapper;

/**
 * Lets a model choose a different variant of itself per display context.
 *
 * <p>Forge patched {@code ItemRenderer.render} to call {@code IForgeBakedModel.applyTransform},
 * which both picked the variant and applied its transforms. Vanilla applies
 * {@code ItemTransforms} inline and has no such hook, so the model is swapped into the parameter at
 * the head of the method and vanilla goes on to apply the swapped model's own transforms — the same
 * result, without having to redirect the transform call as well.
 *
 * <p>Two models need it. The tool model bakes four variants — right hand, left hand, a small one
 * for the display contexts that ask for it, and a flat GUI one with only front faces — and without
 * this hook every context would draw the large right-handed variant, so a tool in the inventory
 * would show the side faces of its layers. {@code UniqueGuiModel} is the same story with one
 * variant.
 */
@Mixin(ItemRenderer.class)
public class ItemRendererModelSwapMixin {
  @ModifyVariable(
    method = "render(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;ZLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IILnet/minecraft/client/resources/model/BakedModel;)V",
    at = @At("HEAD"), argsOnly = true)
  private BakedModel tconstruct$modelForContext(BakedModel model, ItemStack stack, ItemDisplayContext displayContext, boolean leftHand,
                                                PoseStack poseStack, MultiBufferSource buffer, int light, int overlay, BakedModel unused) {
    if (model instanceof BakedModelWrapper<?> wrapper) {
      return wrapper.getModelForContext(displayContext, leftHand);
    }
    return model;
  }
}
