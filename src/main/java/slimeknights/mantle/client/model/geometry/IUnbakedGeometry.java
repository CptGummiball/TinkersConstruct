package slimeknights.mantle.client.model.geometry;

import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;

/**
 * Custom geometry parsed out of a model JSON, ready to be baked.
 *
 * <p>Mirrors {@code net.minecraftforge.client.model.geometry.IUnbakedGeometry}, including the
 * argument order of {@code bake}, so the geometry classes port by import swap.
 *
 * <p>On Fabric the instance is held by a {@link GeometryUnbakedModel}, which is what the vanilla
 * {@code ModelBakery} actually sees; it forwards {@code resolveParents} and {@code bake} here with
 * an {@link IGeometryBakingContext} built from the vanilla parse of the same JSON.
 *
 * @param <T> self type
 */
public interface IUnbakedGeometry<T extends IUnbakedGeometry<T>> {
  /**
   * Bakes this geometry into a renderable model.
   *
   * @param context        view of the vanilla side of the same model JSON (textures, transforms, ...)
   * @param baker          baker for nested models
   * @param spriteGetter   resolves a {@link Material} to its stitched sprite
   * @param modelState     rotation/uv-lock state from the blockstate or parent model
   * @param overrides      item overrides parsed from the same JSON, or {@link ItemOverrides#EMPTY}
   * @param modelLocation  id of the model being baked
   */
  BakedModel bake(IGeometryBakingContext context, ModelBaker baker, Function<Material,TextureAtlasSprite> spriteGetter, ModelState modelState, ItemOverrides overrides, ResourceLocation modelLocation);

  /**
   * Resolves any models this geometry references, so they are loaded before baking.
   * Called from {@link UnbakedModel#resolveParents(Function)} on the wrapper.
   */
  default void resolveParents(Function<ResourceLocation,UnbakedModel> modelGetter, IGeometryBakingContext context) {}
}
