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
   * Chance for the model to swap itself out for a given display context, mirroring Forge's
   * {@code IForgeBakedModel.applyTransform}.
   *
   * <p>PORT: nothing calls this yet. Forge patched {@code ItemRenderer.render} to route every item
   * render through it; vanilla 1.21.1 applies {@link ItemTransforms} directly and offers no such
   * hook, so wiring it up needs an {@code ItemRenderer} mixin. Until then a subclass override (see
   * {@code UniqueGuiModel.Baked}) compiles and is correct but never fires, and the model renders as
   * its base variant in every context.
   */
  public BakedModel applyTransform(ItemDisplayContext displayContext, PoseStack poseStack, boolean applyLeftHandTransform) {
    getTransforms().getTransform(displayContext).apply(applyLeftHandTransform, poseStack);
    return this;
  }
}
