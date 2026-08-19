package slimeknights.mantle.client.model.util;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
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
  private static final FaceBakery FACE_BAKERY = new FaceBakery();

  private final BlockModel model;

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
  private Material getMaterial(IGeometryBakingContext owner, String name) {
    if (model.hasTexture(name)) {
      return model.getMaterial(name);
    }
    return owner.getMaterial(name);
  }

  /** Creates a baked model builder carrying the context's lighting flags and display transforms. */
  public static SimpleBakedModel.Builder bakedBuilder(IGeometryBakingContext owner, ItemOverrides overrides) {
    return new SimpleBakedModel.Builder(owner.useAmbientOcclusion(), owner.useBlockLight(), owner.isGui3d(), owner.getTransforms(), overrides);
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

  @Override
  public BakedModel bake(IGeometryBakingContext owner, ModelBaker baker, Function<Material,TextureAtlasSprite> spriteGetter, ModelState transform, ItemOverrides overrides, ResourceLocation location) {
    SimpleBakedModel.Builder builder = bakedBuilder(owner, overrides).particle(spriteGetter.apply(getMaterial(owner, "particle")));
    for (BlockElement element : getElements()) {
      bakePart(builder, owner, element, spriteGetter, transform);
    }
    return builder.build();
  }
}
