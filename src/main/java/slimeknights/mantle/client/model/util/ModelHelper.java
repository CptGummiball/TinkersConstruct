package slimeknights.mantle.client.model.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import slimeknights.mantle.client.model.BakedModelWrapper;
import slimeknights.mantle.client.model.data.ModelData;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Small helpers shared by the dynamic-texture models.
 *
 * <p>Only the members Tinkers' geometry actually calls are reproduced; upstream Mantle's version
 * also carries display-transform and JSON helpers with no consumer in this tree.
 */
public final class ModelHelper {
  private ModelHelper() {}

  /**
   * Calls the {@link ModelData}-carrying {@code getQuads} on models that understand it, falling
   * back to the vanilla three-argument call on those that do not.
   *
   * <p>Forge could put that overload on {@code BakedModel} itself as a default method, so a model
   * could always be asked with data. On Fabric only {@link BakedModelWrapper} carries it, and the
   * dynamic models routinely delegate to a plain {@code SimpleBakedModel} they baked themselves —
   * hence the dispatch.
   */
  public static List<BakedQuad> getQuads(BakedModel model, @Nullable BlockState state, @Nullable Direction side, RandomSource random, ModelData data, @Nullable RenderType renderType) {
    if (model instanceof BakedModelWrapper<?> wrapper) {
      return wrapper.getQuads(state, side, random, data, renderType);
    }
    return model.getQuads(state, side, random);
  }

  /** {@link #getQuads} for the particle icon. */
  public static TextureAtlasSprite getParticleIcon(BakedModel model, ModelData data) {
    if (model instanceof BakedModelWrapper<?> wrapper) {
      return wrapper.getParticleIcon(data);
    }
    return model.getParticleIcon();
  }

  /**
   * Gets the texture a retextured block should copy from the given block, which is that block's
   * particle texture — the one texture every block model is guaranteed to define.
   *
   * <p>Not cached: {@code BlockModelShaper} resolves a state to its baked model through an identity
   * map, so this is a couple of lookups, and a static cache here would go stale across a resource
   * reload while the baked models that consult it are rebuilt.
   */
  public static ResourceLocation getParticleTexture(Block block) {
    return Minecraft.getInstance().getBlockRenderer().getBlockModelShaper()
                    .getBlockModel(block.defaultBlockState())
                    .getParticleIcon().contents().name();
  }
}
