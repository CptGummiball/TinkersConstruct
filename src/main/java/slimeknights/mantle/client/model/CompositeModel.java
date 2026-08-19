package slimeknights.mantle.client.model;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.SimpleBakedModel;
import slimeknights.mantle.client.RenderTypeGroup;
import slimeknights.mantle.client.model.geometry.IGeometryBakingContext;

import java.util.List;

/**
 * Assembles a baked model out of several groups of quads. Shim for
 * {@code net.minecraftforge.client.model.CompositeModel}.
 *
 * <p>Only the baked half is here — the {@code forge:composite} geometry loader, which stitches
 * several sub-models named in one JSON, has no consumer in this tree (the three models declaring it
 * are Mantle's, whose geometry was never copied in). What Tinkers uses is the builder, as the way to
 * collect item layers into one model.
 *
 * <p>The groups collapse into a single quad list, because their only purpose on Forge was to give
 * each group its own {@link RenderTypeGroup}, which Fabric does not have (see that class). Every
 * quad lands unculled, which is what Forge's composite did too: a composite is a flat item model,
 * with no face that a neighbouring block could hide.
 */
public final class CompositeModel {
  private CompositeModel() {}

  /** Baked half of the composite model. */
  public static final class Baked {
    private Baked() {}

    /** Builder taking the lighting flags directly. */
    public static Builder builder(boolean ambientOcclusion, boolean useBlockLight, boolean isGui3d, TextureAtlasSprite particle, ItemOverrides overrides, ItemTransforms transforms) {
      return new Builder(new SimpleBakedModel.Builder(ambientOcclusion, useBlockLight, isGui3d, transforms, overrides).particle(particle));
    }

    /** Builder reading the lighting flags off a baking context. */
    public static Builder builder(IGeometryBakingContext context, TextureAtlasSprite particle, ItemOverrides overrides, ItemTransforms transforms) {
      return builder(context.useAmbientOcclusion(), context.useBlockLight(), context.isGui3d(), particle, overrides, transforms);
    }

    /** Collects quad groups into one baked model. */
    public static final class Builder {
      private final SimpleBakedModel.Builder builder;

      private Builder(SimpleBakedModel.Builder builder) {
        this.builder = builder;
      }

      /**
       * Adds a group of quads.
       *
       * @param renderTypes  Ignored on Fabric; see {@link RenderTypeGroup}
       */
      public Builder addQuads(RenderTypeGroup renderTypes, List<BakedQuad> quads) {
        for (BakedQuad quad : quads) {
          builder.addUnculledFace(quad);
        }
        return this;
      }

      public BakedModel build() {
        return builder.build();
      }
    }
  }
}
