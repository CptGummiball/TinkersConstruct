package slimeknights.tconstruct.shared;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import org.joml.Matrix4f;
import slimeknights.tconstruct.library.client.data.spritetransformer.FramesSpriteTransformer;
import slimeknights.tconstruct.library.client.data.spritetransformer.GreyToColorMapping;
import slimeknights.tconstruct.library.client.data.spritetransformer.GreyToSpriteTransformer;
import slimeknights.tconstruct.library.client.data.spritetransformer.IColorMapping;
import slimeknights.tconstruct.library.client.data.spritetransformer.ISpriteTransformer;
import slimeknights.tconstruct.library.client.data.spritetransformer.OffsettingSpriteTransformer;
import slimeknights.tconstruct.library.client.data.spritetransformer.RecolorSpriteTransformer;

/**
 * Client-only odds and ends.
 *
 * <p>PORT: on Forge this class was also the client constructor — the book, icon manager, render
 * info loader, armor texture suppliers and modifier models registered here. Those all moved to
 * {@code TConstructClientBootstrap}, {@code ArmorTextureLoaders} and {@code ModifierModelLoaders}
 * in earlier client slices. What remains here is what nothing else had claimed: the sprite
 * transformer serializers the part texture generator reads, and the transparent block overlay,
 * which {@code ScreenEffectRendererMixin} calls in place of Forge's
 * {@code RenderBlockScreenEffectEvent}. The recipe cache reload that hung off
 * {@code RecipesUpdatedEvent} is {@code ClientPacketListenerRecipesUpdatedMixin}.
 */
public class TinkerClient {
  /** Registers the sprite transformer serializers; called from the client bootstrap */
  public static void init() {
    ISpriteTransformer.SERIALIZER.registerDeserializer(RecolorSpriteTransformer.NAME, RecolorSpriteTransformer.DESERIALIZER);
    GreyToSpriteTransformer.init();
    ISpriteTransformer.SERIALIZER.registerDeserializer(OffsettingSpriteTransformer.NAME, OffsettingSpriteTransformer.DESERIALIZER);
    ISpriteTransformer.SERIALIZER.registerDeserializer(FramesSpriteTransformer.NAME, FramesSpriteTransformer.DESERIALIZER);
    IColorMapping.SERIALIZER.registerDeserializer(GreyToColorMapping.NAME, GreyToColorMapping.DESERIALIZER);
  }

  /**
   * Draws the see-through version of the inside-a-block overlay for blocks tagged
   * {@code tconstruct:transparent_overlay}, so standing inside clear glass shows the glass
   * texture instead of vanilla's opaque black wall.
   *
   * <p>Mostly a clone of the vanilla logic from {@code ScreenEffectRenderer.renderTex} with three
   * changes: the position-texture shader (no per-vertex colour), brightness from the player's
   * block light so the overlay dims like the world, and alpha blending enabled so the texture's
   * transparency survives.
   *
   * @return true when the overlay was handled here, whether or not it drew; vanilla then skips its own
   */
  public static boolean renderBlockOverlay(Minecraft minecraft, PoseStack poseStack, BlockState state, BlockPos pos) {
    if (minecraft.level == null || minecraft.player == null) {
      return false;
    }
    Player player = minecraft.player;
    float width = player.getBbWidth() * 0.8F;
    // check collision of the block again, for non-full blocks
    if (Shapes.joinIsNotEmpty(state.getShape(minecraft.level, pos).move(pos.getX(), pos.getY(), pos.getZ()),
                              Shapes.create(AABB.ofSize(player.getEyePosition(), width, 1.0E-6D, width)), BooleanOp.AND)) {
      TextureAtlasSprite texture = minecraft.getBlockRenderer().getBlockModelShaper().getParticleIcon(state);
      RenderSystem.setShaderTexture(0, texture.atlasLocation());
      RenderSystem.setShader(GameRenderer::getPositionTexShader);

      BlockPos lightPos = BlockPos.containing(player.getX(), player.getEyeY(), player.getZ());
      float brightness = LightTexture.getBrightness(player.level().dimensionType(), player.level().getMaxLocalRawBrightness(lightPos));
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.setShaderColor(brightness, brightness, brightness, 1.0f);

      float u0 = texture.getU0();
      float u1 = texture.getU1();
      float v0 = texture.getV0();
      float v1 = texture.getV1();
      Matrix4f matrix4f = poseStack.last().pose();
      BufferBuilder buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
      buffer.addVertex(matrix4f, -1, -1, -0.5f).setUv(u1, v1);
      buffer.addVertex(matrix4f, 1, -1, -0.5f).setUv(u0, v1);
      buffer.addVertex(matrix4f, 1, 1, -0.5f).setUv(u0, v0);
      buffer.addVertex(matrix4f, -1, 1, -0.5f).setUv(u1, v0);
      BufferUploader.drawWithShader(buffer.buildOrThrow());
      RenderSystem.disableBlend();
      RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
    }
    return true;
  }
}
