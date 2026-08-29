package slimeknights.mantle.client.model;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.SimpleBakedModel;
import net.minecraft.core.Direction;
import slimeknights.mantle.client.RenderTypeGroup;

/**
 * Collects quads into a baked model. Shim for {@code net.minecraftforge.client.model.IModelBuilder}.
 *
 * <p>Forge needed an interface here because its builder also carried the model's render types and
 * its own quad storage; on Fabric the whole thing is vanilla's {@link SimpleBakedModel.Builder},
 * so this is a wrapper that keeps the call shape the dynamic models were written against. It stays
 * an interface with the self type because {@code ToolModel} declares its locals as
 * {@code IModelBuilder<?>}.
 */
public interface IModelBuilder<T extends IModelBuilder<T>> {
  /**
   * Creates a builder.
   *
   * @param renderTypes  Ignored on Fabric; see {@link RenderTypeGroup} for why there is no
   *                     per-model render layer to carry
   */
  static IModelBuilder<?> of(boolean hasAmbientOcclusion, boolean isGui3d, boolean usesBlockLight, ItemTransforms transforms, ItemOverrides overrides, TextureAtlasSprite particle, RenderTypeGroup renderTypes) {
    return new Simple(new SimpleBakedModel.Builder(hasAmbientOcclusion, usesBlockLight, isGui3d, transforms, overrides).particle(particle));
  }

  /** Adds a quad hidden when the neighbouring block on that side is solid */
  T addCulledFace(Direction facing, BakedQuad quad);

  /** Adds a quad that always draws */
  T addUnculledFace(BakedQuad quad);

  /** Finishes the model */
  BakedModel build();

  /** The only implementation; kept separate so the interface stays the type callers name. */
  final class Simple implements IModelBuilder<Simple> {
    private final SimpleBakedModel.Builder builder;

    private Simple(SimpleBakedModel.Builder builder) {
      this.builder = builder;
    }

    @Override
    public Simple addCulledFace(Direction facing, BakedQuad quad) {
      builder.addCulledFace(facing, quad);
      return this;
    }

    @Override
    public Simple addUnculledFace(BakedQuad quad) {
      builder.addUnculledFace(quad);
      return this;
    }

    @Override
    public BakedModel build() {
      return builder.build();
    }
  }
}
