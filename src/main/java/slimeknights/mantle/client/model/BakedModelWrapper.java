package slimeknights.mantle.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.state.BlockState;
import slimeknights.mantle.client.model.data.ModelData;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Baked model that delegates everything to another baked model, so subclasses only override what
 * they change. Shim for {@code net.minecraftforge.client.model.BakedModelWrapper}.
 *
 * <p>Also the home of the two Forge-shaped overloads that {@code IForgeBakedModel} added to every
 * baked model — the {@link ModelData}-carrying {@code getQuads} and {@code getParticleIcon}. On
 * Fabric they cannot be default methods on {@code BakedModel}, so they live here and the vanilla
 * calls are routed into them: a subclass overriding the data-carrying overload is reached by
 * vanilla's plain call with {@link ModelData#EMPTY}. Use
 * {@link slimeknights.mantle.client.model.util.ModelHelper#getQuads} to make the same call against
 * a model that may or may not be one of these.
 *
 * @param <T> wrapped model type
 */
public abstract class BakedModelWrapper<T extends BakedModel> implements BakedModel {
  protected final T originalModel;

  protected BakedModelWrapper(T originalModel) {
    this.originalModel = originalModel;
  }

  @Override
  public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource random) {
    return getQuads(state, side, random, ModelData.EMPTY, null);
  }

  /** Forge-shaped overload; the vanilla call above routes here so subclass overrides are reached. */
  public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource random, ModelData data, @Nullable RenderType renderType) {
    return originalModel.getQuads(state, side, random);
  }

  @Override
  public boolean useAmbientOcclusion() {
    return originalModel.useAmbientOcclusion();
  }

  @Override
  public boolean isGui3d() {
    return originalModel.isGui3d();
  }

  @Override
  public boolean usesBlockLight() {
    return originalModel.usesBlockLight();
  }

  @Override
  public boolean isCustomRenderer() {
    return originalModel.isCustomRenderer();
  }

  @Override
  public TextureAtlasSprite getParticleIcon() {
    return getParticleIcon(ModelData.EMPTY);
  }

  /** Forge-shaped overload; the vanilla call above routes here so subclass overrides are reached. */
  public TextureAtlasSprite getParticleIcon(ModelData data) {
    return originalModel.getParticleIcon();
  }

  @Override
  public ItemTransforms getTransforms() {
    return originalModel.getTransforms();
  }

  @Override
  public ItemOverrides getOverrides() {
    return originalModel.getOverrides();
  }

  /**
   * Chance for the model to swap itself out for a given display context.
   *
   * <p>Forge carried this as {@code IForgeBakedModel.applyTransform}, which also applied the
   * transform; vanilla 1.21.1 applies {@link ItemTransforms} itself, so the swap is separated from
   * it. {@code ItemRendererModelSwapMixin} calls this at the head of {@code ItemRenderer.render} and
   * feeds the result back into the parameter, which leaves vanilla to apply the returned model's own
   * transforms — the same order Forge produced.
   *
   * @param leftHand  Whether the item is held in the off hand, for models that differ by hand
   * @return  Model to render, {@code this} to keep the wrapper
   */
  public BakedModel getModelForContext(ItemDisplayContext displayContext, boolean leftHand) {
    return this;
  }

  /**
   * Swaps a model for its display-context variant and applies that variant's transforms.
   *
   * <p>Shim for Forge's {@code ForgeHooksClient.handleCameraTransforms}, for the render paths that
   * place an item model by hand rather than going through {@code ItemRenderer.render} — where
   * {@code ItemRendererModelSwapMixin} does the same job.
   */
  public static BakedModel applyTransform(BakedModel model, ItemDisplayContext displayContext, PoseStack poseStack, boolean leftHand) {
    if (model instanceof BakedModelWrapper<?> wrapper) {
      return wrapper.applyTransform(displayContext, poseStack, leftHand);
    }
    model.getTransforms().getTransform(displayContext).apply(leftHand, poseStack);
    return model;
  }

  /**
   * Swaps the model and applies its transforms in one call, as Forge's
   * {@code IForgeBakedModel.applyTransform} did.
   *
   * <p>Kept for callers written against that shape; the render path uses
   * {@link #getModelForContext} instead, since vanilla applies the transform itself.
   */
  public BakedModel applyTransform(ItemDisplayContext displayContext, PoseStack poseStack, boolean applyLeftHandTransform) {
    BakedModel model = getModelForContext(displayContext, applyLeftHandTransform);
    model.getTransforms().getTransform(displayContext).apply(applyLeftHandTransform, poseStack);
    return model;
  }
}
