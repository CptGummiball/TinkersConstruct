package slimeknights.mantle.client.model.geometry;

import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;

import java.util.Collection;
import java.util.function.Function;

/**
 * The {@link UnbakedModel} vanilla's {@code ModelBakery} sees in place of a model JSON that carried
 * a {@code "loader"} key: it keeps both halves of that file — the vanilla parse and the custom
 * geometry — and forwards the {@link UnbakedModel} contract onto them.
 *
 * <p>Dependency resolution runs on the vanilla half first, so the parent chain (and therefore the
 * texture map the geometry reads through {@link IGeometryBakingContext#getMaterial}) is populated
 * before the geometry gets a chance to resolve models of its own.
 */
public class GeometryUnbakedModel implements UnbakedModel {
  private final BlockModel base;
  private final IUnbakedGeometry<?> geometry;
  private final IGeometryBakingContext context;
  private final ResourceLocation location;

  public GeometryUnbakedModel(BlockModel base, IUnbakedGeometry<?> geometry, IGeometryBakingContext context, ResourceLocation location) {
    this.base = base;
    this.geometry = geometry;
    this.context = context;
    this.location = location;
  }

  @Override
  public Collection<ResourceLocation> getDependencies() {
    return base.getDependencies();
  }

  @Override
  public void resolveParents(Function<ResourceLocation,UnbakedModel> modelGetter) {
    base.resolveParents(modelGetter);
    geometry.resolveParents(modelGetter, context);
  }

  @Override
  public BakedModel bake(ModelBaker baker, Function<Material,TextureAtlasSprite> spriteGetter, ModelState state) {
    return geometry.bake(context, baker, spriteGetter, state, getItemOverrides(baker), location);
  }

  /**
   * Builds the overrides declared by the JSON's own {@code "overrides"} block.
   * Mirrors the private {@code BlockModel#getItemOverrides}, which is what vanilla baking uses.
   */
  private ItemOverrides getItemOverrides(ModelBaker baker) {
    if (base.getOverrides().isEmpty()) {
      return ItemOverrides.EMPTY;
    }
    return new ItemOverrides(baker, base, base.getOverrides());
  }
}
