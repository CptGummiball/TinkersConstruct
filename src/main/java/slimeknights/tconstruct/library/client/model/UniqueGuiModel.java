package slimeknights.tconstruct.library.client.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemDisplayContext;
import slimeknights.mantle.client.model.BakedModelWrapper;
import slimeknights.mantle.client.model.geometry.IGeometryBakingContext;
import slimeknights.mantle.client.model.geometry.IGeometryLoader;
import slimeknights.mantle.client.model.geometry.IUnbakedGeometry;
import slimeknights.mantle.client.model.util.SimpleBlockModel;

import java.util.function.Function;

/** Model providing a variant for the GUI */
@RequiredArgsConstructor
public class UniqueGuiModel implements IUnbakedGeometry<UniqueGuiModel> {
  /** Shared loader instance */
  public static final IGeometryLoader<UniqueGuiModel> LOADER = UniqueGuiModel::deserialize;

  protected final SimpleBlockModel model;
  protected final SimpleBlockModel gui;

  @Override
  public void resolveParents(Function<ResourceLocation,UnbakedModel> modelGetter, IGeometryBakingContext context) {
    model.resolveParents(modelGetter, context);
    gui.resolveParents(modelGetter, context);
  }

  @Override
  public BakedModel bake(IGeometryBakingContext owner, ModelBaker baker, Function<Material,TextureAtlasSprite> spriteGetter, ModelState transform, ItemOverrides overrides, ResourceLocation location) {
    return new Baked(
      model.bake(owner, baker, spriteGetter, transform, overrides, location),
      gui.bake(owner, baker, spriteGetter, transform, overrides, location)
    );
  }

  /**
   * Wrapper that swaps the model for the GUI.
   *
   * <p>Fabric port: Forge routed every item render through {@code IForgeBakedModel.applyTransform};
   * vanilla applies {@link ItemTransforms} inline in {@code ItemRenderer.render} with no hook of its
   * own, so {@code ItemRendererModelSwapMixin} calls {@link #getModelForContext} there instead.
   */
  public static class Baked extends BakedModelWrapper<BakedModel> {
    private final BakedModel gui;

    public Baked(BakedModel base, BakedModel gui) {
      super(base);
      this.gui = gui;
    }

    @Override
    public BakedModel getModelForContext(ItemDisplayContext itemDisplay, boolean leftHand) {
      return itemDisplay == ItemDisplayContext.GUI ? gui : originalModel;
    }
  }

  /** Loader for this model */
  public static UniqueGuiModel deserialize(JsonObject json, JsonDeserializationContext context) {
    return new UniqueGuiModel(
      SimpleBlockModel.deserialize(json, context),
      SimpleBlockModel.deserialize(GsonHelper.getAsJsonObject(json, "gui"), context)
    );
  }
}
