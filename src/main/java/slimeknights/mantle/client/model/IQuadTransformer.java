package slimeknights.mantle.client.model;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import net.minecraft.client.renderer.block.model.BakedQuad;

import java.util.List;

/**
 * In-place edit applied to a baked quad's raw vertex data. Shim for
 * {@code net.minecraftforge.client.model.IQuadTransformer}.
 *
 * <p>A {@link BakedQuad} in 1.21.1 holds a flat {@code int[]} of four vertices in
 * {@link DefaultVertexFormat#BLOCK} layout; the offsets below name where each attribute sits inside
 * one vertex, so a transformer can rewrite colours, lightmaps or positions without rebaking. The
 * constants are read off the format rather than hardcoded, so they follow any change to it.
 */
@FunctionalInterface
public interface IQuadTransformer {
  /** Ints per vertex in the block format. */
  int STRIDE = DefaultVertexFormat.BLOCK.getVertexSize() / Integer.BYTES;
  /** Offset of the x/y/z floats within a vertex. */
  int POSITION = DefaultVertexFormat.BLOCK.getOffset(VertexFormatElement.POSITION) / Integer.BYTES;
  /** Offset of the packed RGBA colour within a vertex. */
  int COLOR = DefaultVertexFormat.BLOCK.getOffset(VertexFormatElement.COLOR) / Integer.BYTES;
  /** Offset of the texture UV floats within a vertex. */
  int UV0 = DefaultVertexFormat.BLOCK.getOffset(VertexFormatElement.UV0) / Integer.BYTES;
  /** Offset of the packed lightmap coordinate within a vertex. */
  int UV2 = DefaultVertexFormat.BLOCK.getOffset(VertexFormatElement.UV2) / Integer.BYTES;
  /** Offset of the packed normal within a vertex. */
  int NORMAL = DefaultVertexFormat.BLOCK.getOffset(VertexFormatElement.NORMAL) / Integer.BYTES;

  /** Applies this transform to a single quad. */
  void processInPlace(BakedQuad quad);

  /** Applies this transform to every quad in the list. */
  default void processInPlace(List<BakedQuad> quads) {
    for (BakedQuad quad : quads) {
      processInPlace(quad);
    }
  }

  /** Runs this transform, then the given one. */
  default IQuadTransformer andThen(IQuadTransformer after) {
    return quad -> {
      this.processInPlace(quad);
      after.processInPlace(quad);
    };
  }

  /** Transformer that changes nothing. */
  IQuadTransformer EMPTY = quad -> {};
}
