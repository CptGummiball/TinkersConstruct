package slimeknights.mantle.client.model;

import net.minecraft.client.renderer.LightTexture;

/**
 * Factories for the {@link IQuadTransformer}s Tinkers uses. Shim for
 * {@code net.minecraftforge.client.model.QuadTransformers}.
 *
 * <h2>How far these carry on Fabric</h2>
 * Both transformers write into the quad's vertex array, which is what Forge did. Whether the
 * renderer then <em>reads</em> those bytes differs by path:
 * <ul>
 *   <li><b>Blocks</b> — vanilla's {@code ModelBlockRenderer} calls
 *       {@code VertexConsumer.putBulkData(..., readAlpha = true)}, so the baked colour is picked up
 *       and multiplied with the block tint exactly as on Forge.</li>
 *   <li><b>Items</b> — {@code ItemRenderer.renderQuadList} calls the {@code readAlpha = false}
 *       overload, so vanilla ignores the baked colour and uses only the {@code ItemColors} tint for
 *       the quad's tint index. Forge patched that call site; reproducing it needs an
 *       {@code ItemRenderer} mixin or the Fabric renderer API, both of which belong to the renderer
 *       slice.</li>
 *   <li><b>Emissivity</b> — vanilla overwrites each vertex's lightmap from the light value the
 *       renderer passes in, on both paths, so a baked lightmap never survives. Forge OR-ed the two
 *       together. Same hook, same slice.</li>
 * </ul>
 * The data is written correctly either way, so those paths light up as soon as the hook lands.
 */
public final class QuadTransformers {
  private QuadTransformers() {}

  /** Transformer that changes nothing. */
  public static IQuadTransformer empty() {
    return IQuadTransformer.EMPTY;
  }

  /**
   * Sets every vertex's lightmap to the given packed value.
   * @see LightTexture#pack(int, int)
   */
  public static IQuadTransformer applyingLightmap(int packedLight) {
    return quad -> {
      int[] vertices = quad.getVertices();
      for (int i = 0; i < 4; i++) {
        vertices[i * IQuadTransformer.STRIDE + IQuadTransformer.UV2] = packedLight;
      }
    };
  }

  /** Lights every vertex as if it sat under a block light source of the given level. */
  public static IQuadTransformer settingEmissivity(int emissivity) {
    return applyingLightmap(LightTexture.pack(emissivity, emissivity));
  }

  /**
   * Multiplies every vertex colour by the given ARGB colour.
   *
   * <p>Multiplying rather than replacing matters for models whose elements already carry a colour:
   * a tinted fluid inside a tinted tank composes both. Quads straight out of {@code FaceBakery}
   * start at opaque white, where multiplying is the same as setting.
   */
  public static IQuadTransformer applyingColor(int color) {
    int a = (color >> 24) & 0xFF;
    int r = (color >> 16) & 0xFF;
    int g = (color >>  8) & 0xFF;
    int b =  color        & 0xFF;
    return quad -> {
      int[] vertices = quad.getVertices();
      for (int i = 0; i < 4; i++) {
        int offset = i * IQuadTransformer.STRIDE + IQuadTransformer.COLOR;
        int packed = vertices[offset];
        // the colour element is four unsigned bytes in R,G,B,A order, which reads back
        // little-endian as ABGR; keep that layout so the renderer sees what it expects
        int vr = ( packed        & 0xFF) * r / 0xFF;
        int vg = ((packed >>  8) & 0xFF) * g / 0xFF;
        int vb = ((packed >> 16) & 0xFF) * b / 0xFF;
        int va = ((packed >>> 24) & 0xFF) * a / 0xFF;
        vertices[offset] = (va << 24) | (vb << 16) | (vg << 8) | vr;
      }
    };
  }
}
