package slimeknights.mantle.client.model.util;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.mojang.math.Transformation;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.BlockElementFace;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.FaceBakery;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.SimpleBakedModel;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import slimeknights.mantle.Mantle;
import slimeknights.mantle.client.model.IQuadTransformer;
import slimeknights.mantle.client.model.QuadTransformers;
import slimeknights.mantle.client.model.geometry.IGeometryBakingContext;
import slimeknights.mantle.client.model.geometry.IUnbakedGeometry;

import java.util.List;
import java.util.function.Function;

/**
 * A plain list of block elements plus its own textures, bakeable against an
 * {@link IGeometryBakingContext} that is not necessarily its own.
 *
 * <p>That last part is the whole reason this exists: a geometry model often carries several element
 * lists in one file (a block variant and a GUI variant, say), and only the outermost object declares
 * {@code textures}, {@code parent} and {@code display}. Vanilla's {@link BlockModel#bake} can take a
 * separate texture owner but reads lighting and display transforms off the model it is called on,
 * which for a nested object means vanilla defaults instead of the file's. So the bake loop is
 * reproduced here, reading elements from this model and everything else from the context.
 *
 * <p>The JSON half is delegated to the vanilla {@link BlockModel} deserializer, which already
 * understands {@code parent}, {@code textures} and {@code elements} — the three keys Mantle's Forge
 * version parsed by hand.
 */
public class SimpleBlockModel implements IUnbakedGeometry<SimpleBlockModel> {
  protected static final FaceBakery FACE_BAKERY = new FaceBakery();
  /** Logged at most once; see {@link #applyTransform}. */
  private static boolean warnedRootTransform = false;

  protected final BlockModel model;

  public SimpleBlockModel(BlockModel model) {
    this.model = model;
  }

  /** Reads a model from a JSON object holding {@code parent}, {@code textures} and {@code elements}. */
  public static SimpleBlockModel deserialize(JsonObject json, JsonDeserializationContext context) {
    return new SimpleBlockModel(context.deserialize(json, BlockModel.class));
  }

  /** The elements this model bakes. */
  public List<BlockElement> getElements() {
    return model.getElements();
  }

  @Override
  public void resolveParents(Function<ResourceLocation,UnbakedModel> modelGetter, IGeometryBakingContext context) {
    model.resolveParents(modelGetter);
  }

  /**
   * Resolves a texture reference, preferring this model's own textures and falling back to the
   * context's. A nested element list normally declares none of its own, so it inherits the file's.
   */
  protected Material getMaterial(IGeometryBakingContext owner, String name) {
    if (model.hasTexture(name)) {
      return model.getMaterial(name);
    }
    return owner.getMaterial(name);
  }

  /** Creates a baked model builder carrying the context's lighting flags and display transforms. */
  public static SimpleBakedModel.Builder bakedBuilder(IGeometryBakingContext owner, ItemOverrides overrides) {
    return new SimpleBakedModel.Builder(owner.useAmbientOcclusion(), owner.useBlockLight(), owner.isGui3d(), owner.getTransforms(), overrides);
  }

  /**
   * Builds the quad transform applied to every quad of a model on top of what {@code FaceBakery}
   * already did.
   *
   * <p>On Forge that was Forge's root {@code "transform"} key, applied after baking because the
   * face bakery only knows the blockstate rotation. Nothing in this port produces a non-identity
   * root transform — {@link IGeometryBakingContext#getRootTransform} is documented as always the
   * identity, since no Tinkers geometry model uses the key — so this is the identity transformer,
   * and the warning below fires if that assumption ever stops holding.
   *
   * @param transform      Blockstate transform; already applied by the face bakery
   * @param rootTransform  Extra whole-model transform from the context
   */
  public static IQuadTransformer applyTransform(ModelState transform, Transformation rootTransform) {
    if (!Transformation.identity().equals(rootTransform) && !warnedRootTransform) {
      warnedRootTransform = true;
      Mantle.logger.warn("Model requested a root transform, which this port does not apply; the model will render unrotated");
    }
    return QuadTransformers.empty();
  }

  /**
   * Bakes one element's faces into the builder, resolving textures against the given context.
   *
   * <p>Unlike the instance overload this ignores the model's own texture map: the callers pass a
   * context that has already had the dynamic textures substituted in, and honouring the static map
   * first would undo the substitution.
   */
  public static void bakePart(SimpleBakedModel.Builder builder, IGeometryBakingContext owner, BlockElement element, Function<Material,TextureAtlasSprite> spriteGetter, ModelState transform, IQuadTransformer quadTransformer, ResourceLocation location) {
    for (Direction direction : element.faces.keySet()) {
      BlockElementFace face = element.faces.get(direction);
      TextureAtlasSprite sprite = spriteGetter.apply(owner.getMaterial(face.texture()));
      BakedQuad quad = FACE_BAKERY.bakeQuad(element.from, element.to, face, sprite, direction, transform, element.rotation, element.shade);
      quadTransformer.processInPlace(quad);
      if (face.cullForDirection() == null) {
        builder.addUnculledFace(quad);
      } else {
        builder.addCulledFace(Direction.rotate(transform.getRotation().getMatrix(), face.cullForDirection()), quad);
      }
    }
  }

  /** Bakes one element's faces into the builder, mirroring the loop inside {@link BlockModel#bake}. */
  public void bakePart(SimpleBakedModel.Builder builder, IGeometryBakingContext owner, BlockElement element, Function<Material,TextureAtlasSprite> spriteGetter, ModelState transform) {
    for (Direction direction : element.faces.keySet()) {
      BlockElementFace face = element.faces.get(direction);
      TextureAtlasSprite sprite = spriteGetter.apply(getMaterial(owner, face.texture()));
      BakedQuad quad = FACE_BAKERY.bakeQuad(element.from, element.to, face, sprite, direction, transform, element.rotation, element.shade);
      if (face.cullForDirection() == null) {
        builder.addUnculledFace(quad);
      } else {
        builder.addCulledFace(Direction.rotate(transform.getRotation().getMatrix(), face.cullForDirection()), quad);
      }
    }
  }

  /**
   * Rebakes this model against a context whose textures were swapped at runtime, using the atlas
   * directly rather than a baker.
   *
   * <p>This is the path every dynamic model takes on its second and later bakes: by then the
   * resource reload is long over, so the only sprite source left is the stitched atlas that
   * {@link Material#sprite()} reads.
   */
  public BakedModel bakeDynamic(IGeometryBakingContext owner, ModelState transform) {
    Function<Material,TextureAtlasSprite> spriteGetter = Material::sprite;
    SimpleBakedModel.Builder builder = bakedBuilder(owner, ItemOverrides.EMPTY).particle(spriteGetter.apply(owner.getMaterial("particle")));
    IQuadTransformer quadTransformer = applyTransform(transform, owner.getRootTransform());
    for (BlockElement element : getElements()) {
      bakePart(builder, owner, element, spriteGetter, transform, quadTransformer, null);
    }
    return builder.build();
  }

  @Override
  public BakedModel bake(IGeometryBakingContext owner, ModelBaker baker, Function<Material,TextureAtlasSprite> spriteGetter, ModelState transform, ItemOverrides overrides, ResourceLocation location) {
    SimpleBakedModel.Builder builder = bakedBuilder(owner, overrides).particle(spriteGetter.apply(getMaterial(owner, "particle")));
    for (BlockElement element : getElements()) {
      bakePart(builder, owner, element, spriteGetter, transform);
    }
    return builder.build();
  }
}
