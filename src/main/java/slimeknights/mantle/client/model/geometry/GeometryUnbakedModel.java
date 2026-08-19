package slimeknights.mantle.client.model.geometry;

import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * The model vanilla's {@code ModelBakery} sees in place of a model JSON that carried a
 * {@code "loader"} key: it keeps both halves of that file — the vanilla parse and the custom
 * geometry — and routes baking into the geometry.
 *
 * <h2>Why this is a {@link BlockModel} and not a plain {@link UnbakedModel}</h2>
 * Vanilla's {@link BlockModel#resolveParents} walks the {@code parent} chain and throws
 * {@code "BlockModel parent has to be a block model."} the moment a link in that chain is not a
 * {@link BlockModel}. 114 of Tinkers' loader-carrying models are used as the {@code parent} of
 * another model (144 children in total — the shared block templates alone account for dozens), so
 * a plain {@code UnbakedModel} here takes down the entire resource pack at bake time, not just the
 * models that carry a loader. Forge's geometry lived inside a patched {@code BlockModel} for the
 * same reason.
 *
 * <p>Rather than copying the parsed model's fields — they are private, and reading them through
 * getters silently resolves the "inherit from parent" nulls — this model is an <em>empty</em>
 * {@link BlockModel} whose parent is the vanilla parse of the same file. Every inherited lookup
 * vanilla performs (elements, texture map, display transforms, ambient occlusion, gui light) walks
 * the parent chain already, so a child that inherits from this model sees exactly the data the
 * JSON declared, one hop further up.
 *
 * <p>PORT: what a child does <em>not</em> inherit is the custom geometry itself — a model that
 * declares only {@code display} overrides on top of a loader-carrying parent bakes from the
 * parent's static elements instead of running the loader. Vanilla bakes a child through the
 * child's own {@code BlockModel.bake}, which this class cannot intercept without a mixin. The
 * models affected are the 16 {@code blocking}/{@code pulling} pose variants under
 * {@code tconstruct:item/tool/**}, all of which sit under {@code tconstruct:tool} — a loader that
 * is still parked — so nothing registered today is hit by it.
 */
public class GeometryUnbakedModel extends BlockModel {
  private final BlockModel base;
  private final IUnbakedGeometry<?> geometry;
  private final IGeometryBakingContext context;
  private final ResourceLocation location;

  public GeometryUnbakedModel(BlockModel base, IUnbakedGeometry<?> geometry, ResourceLocation location) {
    // no parent location, no textures, no elements: everything is inherited from the vanilla parse
    // below, which carries the JSON's real parent, texture map, transforms and lighting flags
    super(null, List.of(), Map.of(), null, null, ItemTransforms.NO_TRANSFORMS, List.of());
    this.parent = base;
    this.base = base;
    this.geometry = geometry;
    this.context = new BlockGeometryBakingContext(base, location);
    this.location = location;
    this.name = location.toString();
  }

  /**
   * Reports the vanilla parse's dependencies rather than this model's own (which are empty, since
   * the parent link is set directly rather than through a location).
   */
  @Override
  public Collection<ResourceLocation> getDependencies() {
    return base.getDependencies();
  }

  /**
   * Resolution has to reach the vanilla parse: {@code super} would stop immediately, since this
   * model's own parent link is already filled in and it declares no parent location of its own.
   */
  @Override
  public boolean isResolved() {
    return base.isResolved();
  }

  @Override
  public void resolveParents(Function<ResourceLocation,UnbakedModel> modelGetter) {
    base.resolveParents(modelGetter);
    geometry.resolveParents(modelGetter, context);
  }

  /**
   * Stops the parent walk here, which keeps vanilla from mistaking this for a generated item model.
   *
   * <p>{@code ModelBakery.ModelBakerImpl.bakeUncached} tests {@code getRootModel() == GENERATION_MARKER}
   * and, when it matches, runs {@code ItemModelGenerator} over the model's {@code layerN} textures
   * <em>instead of</em> calling {@link #bake} at all. 170 of Tinkers' loader-carrying models parent
   * (transitively) to {@code minecraft:item/generated} — every {@code fluid_container} and every
   * {@code material} model among them — and none of them declare {@code layerN} textures, so
   * without this they would bake to nothing. Forge tested for custom geometry before the generation
   * marker for the same reason.
   */
  @Override
  public BlockModel getRootModel() {
    return this;
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
