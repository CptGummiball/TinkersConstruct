package slimeknights.mantle.client.model;

import java.util.function.Function;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.ArrayList;
import javax.annotation.Nullable;
import slimeknights.mantle.client.model.util.SimpleBlockModel;
import slimeknights.mantle.client.model.geometry.IUnbakedGeometry;
import slimeknights.mantle.client.model.geometry.IGeometryLoader;
import slimeknights.mantle.client.model.geometry.BlockGeometryBakingContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.util.RandomSource;
import net.minecraft.util.GsonHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.Direction;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.renderer.block.model.BlockModel;
import com.google.gson.JsonSyntaxException;
import com.google.gson.JsonObject;
import com.google.gson.JsonElement;
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
 * <p>Two halves. The builder collects item layers into one flat model, which is what the item
 * geometries use. The {@link Geometry} half is the {@code forge:composite} loader itself, needed by
 * the three slime-metal storage blocks whose models pair an opaque frame with a translucent
 * overlay; those keep each child's culled faces, so they merge by delegation rather than through
 * the builder.
 *
 * <p>The groups collapse into a single quad list, because their only purpose on Forge was to give
 * each group its own {@link RenderTypeGroup}, which Fabric does not have (see that class). Every
 * quad lands unculled, which is what Forge's composite did too: a composite is a flat item model,
 * with no face that a neighbouring block could hide.
 */
public final class CompositeModel {
  private CompositeModel() {}

  /** Loader for the {@code forge:composite} id: several named sub-models baked as one block model. */
  public static final IGeometryLoader<Geometry> LOADER = (json, context) -> {
    JsonObject childrenJson = GsonHelper.getAsJsonObject(json, "children");
    Map<String,BlockModel> children = new LinkedHashMap<>(childrenJson.size());
    for (Map.Entry<String,JsonElement> entry : childrenJson.entrySet()) {
      children.put(entry.getKey(), context.deserialize(entry.getValue(), BlockModel.class));
    }
    if (children.isEmpty()) {
      throw new JsonSyntaxException("Composite model requires at least one child");
    }
    return new Geometry(children);
  };

  /**
   * Geometry of a composite: each child is a complete nested model with its own parent chain and
   * textures, resolved and baked independently, answering quad queries in declaration order.
   *
   * <p>A child may declare its own {@code render_type}; the per-model concept collapses on Fabric
   * (see {@link RenderTypeGroup}), so {@code BlockRenderTypes} instead lifts the most permissive
   * child layer onto the whole block.
   */
  public static class Geometry implements IUnbakedGeometry<Geometry> {
    private final Map<String,BlockModel> children;

    public Geometry(Map<String,BlockModel> children) {
      this.children = children;
    }

    @Override
    public void resolveParents(Function<ResourceLocation,UnbakedModel> modelGetter, IGeometryBakingContext context) {
      for (BlockModel child : children.values()) {
        child.resolveParents(modelGetter);
      }
    }

    @Override
    public BakedModel bake(IGeometryBakingContext context, ModelBaker baker, Function<Material,TextureAtlasSprite> spriteGetter, ModelState modelState, ItemOverrides overrides, ResourceLocation location) {
      List<BakedModel> parts = new ArrayList<>(children.size());
      for (BlockModel child : children.values()) {
        // context from the child, so its textures and lighting flags are the ones baked
        parts.add(new SimpleBlockModel(child).bake(new BlockGeometryBakingContext(child, location), baker, spriteGetter, modelState, ItemOverrides.EMPTY, location));
      }
      TextureAtlasSprite particle = spriteGetter.apply(context.getMaterial("particle"));
      return new BakedParts(List.copyOf(parts), particle, context.useAmbientOcclusion(), context.isGui3d(), context.useBlockLight(), context.getTransforms(), overrides);
    }
  }

  /** Baked composite: keeps each part's culled faces by delegating the quad query per part. */
  private record BakedParts(List<BakedModel> parts, TextureAtlasSprite particle, boolean ambientOcclusion, boolean gui3d, boolean blockLight, ItemTransforms transforms, ItemOverrides overrides) implements BakedModel {
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource random) {
      List<BakedQuad> quads = new ArrayList<>();
      for (BakedModel part : parts) {
        quads.addAll(part.getQuads(state, side, random));
      }
      return quads;
    }

    @Override
    public boolean useAmbientOcclusion() {
      return ambientOcclusion;
    }

    @Override
    public boolean isGui3d() {
      return gui3d;
    }

    @Override
    public boolean usesBlockLight() {
      return blockLight;
    }

    @Override
    public boolean isCustomRenderer() {
      return false;
    }

    @Override
    public TextureAtlasSprite getParticleIcon() {
      return particle;
    }

    @Override
    public ItemTransforms getTransforms() {
      return transforms;
    }

    @Override
    public ItemOverrides getOverrides() {
      return overrides;
    }
  }

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
