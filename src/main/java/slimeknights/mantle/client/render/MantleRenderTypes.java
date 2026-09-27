package slimeknights.mantle.client.render;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderType;

/**
 * Render types shared by the block entity fluid renderers.
 *
 * <p>Fabric port: written for this tree, as Mantle's client packages were never copied in. Extends
 * {@link RenderType} purely for access to the protected state shards, the same trick vanilla's own
 * render type holders use.
 *
 * <p>1.21 note: {@code RenderType.create} is private in vanilla (Forge's access transformer made it
 * public), so it is opened by {@code tconstruct.accesswidener} together with the package-private
 * {@code CompositeRenderType} it returns.
 */
public final class MantleRenderTypes extends RenderType {
  private MantleRenderTypes() {
    super("", DefaultVertexFormat.BLOCK, Mode.QUADS, 256, false, false, () -> {}, () -> {});
    throw new UnsupportedOperationException("No instances");
  }

  /** Shader for fluid rendering, position, color, texture and lightmap with no normals */
  public static final ShaderStateShard FLUID_SHADER = new ShaderStateShard(GameRenderer::getPositionColorTexLightmapShader);

  /** Render type used for all block entity fluid rendering; translucent, mipmapped block sheet */
  public static final RenderType FLUID = create(
    "mantle_fluid", DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP, Mode.QUADS, 256, false, true,
    CompositeState.builder()
                  .setLightmapState(LIGHTMAP)
                  .setShaderState(FLUID_SHADER)
                  .setTextureState(BLOCK_SHEET_MIPPED)
                  .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                  .createCompositeState(false));
}
