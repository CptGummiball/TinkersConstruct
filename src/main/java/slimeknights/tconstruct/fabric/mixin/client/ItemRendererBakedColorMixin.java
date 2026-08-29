package slimeknights.tconstruct.fabric.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.entity.ItemRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import slimeknights.mantle.client.model.IQuadTransformer;

/**
 * Makes an item model's baked vertex colours and light levels count.
 *
 * <p>A {@link BakedQuad} carries a colour and a lightmap per vertex, and vanilla reads neither when
 * drawing an item: {@code ItemRenderer.renderQuadList} calls the {@code putBulkData} overload that
 * passes {@code readExistingColor = false} and overwrites the lightmap outright, so only the
 * {@code ItemColors} tint for the quad's tint index survives. Blocks do read the baked colour, which
 * is why the same model looks right placed and wrong held. Forge patched this call site; this
 * redirect is the same patch.
 *
 * <p>What it costs to leave out: every material that tints a greyscale sprite renders grey on items
 * — the fallback colour for a material with no texture of its own, most modifier overlays, dyed and
 * potion-tinted layers — and every modifier that declares a {@code luminosity} stops glowing.
 *
 * <p>Safe for vanilla models: their quads are baked opaque white with a zero lightmap, and both
 * values compose (multiply, then component-wise max), so white and zero change nothing.
 */
@Mixin(ItemRenderer.class)
public class ItemRendererBakedColorMixin {
  /** Per-vertex brightness multiplier the plain overload uses; items have no ambient occlusion */
  private static final float[] FULL_BRIGHTNESS = {1.0F, 1.0F, 1.0F, 1.0F};

  @Redirect(
    method = "renderQuadList",
    at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/VertexConsumer;putBulkData(Lcom/mojang/blaze3d/vertex/PoseStack$Pose;Lnet/minecraft/client/renderer/block/model/BakedQuad;FFFFII)V"))
  private void tconstruct$bakedColorAndLight(VertexConsumer consumer, PoseStack.Pose pose, BakedQuad quad,
                                             float red, float green, float blue, float alpha, int light, int overlay) {
    int[] vertices = quad.getVertices();
    int[] lightmap = new int[4];
    for (int i = 0; i < 4; i++) {
      lightmap[i] = brighter(light, vertices[i * IQuadTransformer.STRIDE + IQuadTransformer.UV2]);
    }
    consumer.putBulkData(pose, quad, FULL_BRIGHTNESS, red, green, blue, alpha, lightmap, overlay, true);
  }

  /** Takes the higher of the two light levels in each channel, so a baked glow can only add light */
  private static int brighter(int packed, int baked) {
    if (baked == 0) {
      return packed;
    }
    return LightTexture.pack(
      Math.max(LightTexture.block(packed), LightTexture.block(baked)),
      Math.max(LightTexture.sky(packed), LightTexture.sky(baked)));
  }
}
