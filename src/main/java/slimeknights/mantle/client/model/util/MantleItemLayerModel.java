package slimeknights.mantle.client.model.util;

import java.util.function.Function;
import slimeknights.mantle.client.model.geometry.IUnbakedGeometry;
import slimeknights.mantle.client.model.geometry.IGeometryLoader;
import slimeknights.mantle.client.model.CompositeModel;
import net.minecraft.util.GsonHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import com.google.gson.JsonObject;
import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Either;
import com.mojang.math.Transformation;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.BlockElementFace;
import net.minecraft.client.renderer.block.model.BlockFaceUV;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.FaceBakery;
import net.minecraft.client.renderer.block.model.ItemModelGenerator;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.Direction;
import net.minecraft.world.inventory.InventoryMenu;
import org.joml.Vector3f;
import slimeknights.mantle.client.RenderTypeGroup;
import slimeknights.mantle.client.model.IQuadTransformer;
import slimeknights.mantle.client.model.QuadTransformers;
import slimeknights.mantle.client.model.SimpleModelState;
import slimeknights.mantle.client.model.geometry.IGeometryBakingContext;
import slimeknights.mantle.util.ItemLayerPixels;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Turns a single sprite into the quads of a flat item layer, with an optional tint and light level.
 *
 * <p>This is the geometry behind every material-textured item: a tool part is one of these per
 * material layer, drawn from the material's own sprite.
 *
 * <h2>Where the outline comes from</h2>
 * A flat item is not one quad — it is the sprite's silhouette, with side faces closing every
 * transparent-to-opaque transition, which is why an item in hand has visible thickness. Vanilla
 * already traces exactly that in {@link ItemModelGenerator}, the code behind
 * {@code builtin/generated}, so this feeds it a one-layer model and bakes what it returns rather
 * than reimplementing the span tracing. Forge's {@code UnbakedGeometryHelper} took the same shape
 * from its own copy of that algorithm.
 */
public final class MantleItemLayerModel {
  private MantleItemLayerModel() {}

  private static final FaceBakery FACE_BAKERY = new FaceBakery();
  private static final ItemModelGenerator ITEM_MODEL_GENERATOR = new ItemModelGenerator();
  /** Name the generated elements reference; must be one of {@link ItemModelGenerator#LAYERS}. */
  private static final String LAYER = "layer0";
  /** Corners of the layer's flat rectangle, matching {@link ItemModelGenerator}'s own element. */
  private static final Vector3f FRONT_FROM = new Vector3f(0, 0, 7.5f);
  private static final Vector3f FRONT_TO = new Vector3f(16, 16, 8.5f);

  /**
   * Render types a plain item layer draws with.
   *
   * <p>Always empty: Fabric has no per-model render layer (see {@link RenderTypeGroup}). Kept
   * because the callers pass the result straight into a composite builder that ignores it.
   */
  public static RenderTypeGroup getDefaultRenderType(IGeometryBakingContext owner) {
    return RenderTypeGroup.EMPTY;
  }

  /**
   * Builds the quads of one item layer.
   *
   * @param color       ARGB tint baked into the quads, or -1 to leave them untinted
   * @param tintIndex   Tint index the quads report, so an {@code ItemColor} can recolour them
   * @param sprite      Sprite to draw
   * @param transform   Extra transform, used to offset a tool part within a tool
   * @param emissivity  Block light level baked in, 0 for none
   */
  public static List<BakedQuad> getQuadsForSprite(int color, int tintIndex, TextureAtlasSprite sprite, Transformation transform, int emissivity) {
    return getQuadsForSprite(color, tintIndex, sprite, transform, emissivity, null);
  }

  /**
   * {@link #getQuadsForSprite(int, int, TextureAtlasSprite, Transformation, int)} with an overlap
   * record.
   *
   * @param pixels  Ignored — vanilla's generator draws the front of a layer as one full-size quad,
   *                so there is no per-pixel face to trim; {@link ItemLayerPixels} works through why
   *                that costs nothing but overdraw
   */
  public static List<BakedQuad> getQuadsForSprite(int color, int tintIndex, TextureAtlasSprite sprite, Transformation transform, int emissivity, @Nullable ItemLayerPixels pixels) {
    List<BakedQuad> quads = new ArrayList<>();
    ModelState state = new SimpleModelState(transform);
    IQuadTransformer quadTransformer = quadTransformer(color, emissivity);
    for (BlockElement element : traceSprite(sprite)) {
      for (Map.Entry<Direction,BlockElementFace> entry : element.faces.entrySet()) {
        BlockElementFace face = entry.getValue();
        // the generator numbers faces by layer index; this model's caller decides the tint index
        BlockElementFace retinted = new BlockElementFace(face.cullForDirection(), tintIndex, face.texture(), face.uv());
        BakedQuad quad = FACE_BAKERY.bakeQuad(element.from, element.to, retinted, sprite, entry.getKey(), state, element.rotation, element.shade);
        if (quadTransformer != null) {
          quadTransformer.processInPlace(quad);
        }
        quads.add(quad);
      }
    }
    return quads;
  }

  /**
   * Builds only the front-facing quad of a layer, skipping the silhouette sides.
   *
   * <p>For overlays that exist to be seen flat-on and would only add thickness anywhere else. The
   * banner patterns on a tool are the case: a dozen of them stack on one tool, and giving each its
   * own set of side faces would ring the tool in seams no pattern actually has.
   *
   * <p>Geometry matches the front face vanilla's generator produces — the full sprite rectangle at
   * z 8.5, uv 0 to 16 — so it lines up exactly with the layers around it.
   */
  public static BakedQuad getQuadForGui(int color, int tintIndex, TextureAtlasSprite sprite, Transformation transform, int emissivity) {
    BlockElementFace face = new BlockElementFace(null, tintIndex, LAYER, new BlockFaceUV(new float[]{0, 0, 16, 16}, 0));
    BakedQuad quad = FACE_BAKERY.bakeQuad(FRONT_FROM, FRONT_TO, face, sprite, Direction.SOUTH, new SimpleModelState(transform), null, true);
    IQuadTransformer quadTransformer = quadTransformer(color, emissivity);
    if (quadTransformer != null) {
      quadTransformer.processInPlace(quad);
    }
    return quad;
  }

  /** Builds the combined colour and emissivity transform, or null when neither applies. */
  @Nullable
  private static IQuadTransformer quadTransformer(int color, int emissivity) {
    IQuadTransformer transformer = null;
    if (color != -1) {
      transformer = QuadTransformers.applyingColor(color);
    }
    if (emissivity > 0) {
      IQuadTransformer light = QuadTransformers.settingEmissivity(emissivity);
      transformer = transformer == null ? light : transformer.andThen(light);
    }
    return transformer;
  }

  /**
   * Runs vanilla's item model generator over a single sprite, returning the silhouette elements.
   *
   * <p>The throwaway {@link BlockModel} exists only to carry the one texture the generator reads;
   * its parent, transforms and overrides never come into play, since the generator only looks at
   * the {@code layerN} texture map.
   */
  private static List<BlockElement> traceSprite(TextureAtlasSprite sprite) {
    Material material = new Material(InventoryMenu.BLOCK_ATLAS, sprite.contents().name());
    BlockModel template = new BlockModel(null, List.of(), Map.of(LAYER, Either.left(material)), null, null, ItemTransforms.NO_TRANSFORMS, List.of());
    return ITEM_MODEL_GENERATOR.generateBlockModel($ -> sprite, template).getElements();
  }

  /* The mantle:item_layer geometry */

  /** Loader for the {@code mantle:item_layer} id */
  public static final IGeometryLoader<Geometry> LOADER = (json, context) -> {
    List<LayerData> layers = List.of();
    if (json.has("layers")) {
      List<LayerData> parsed = new ArrayList<>();
      for (JsonElement element : GsonHelper.getAsJsonArray(json, "layers")) {
        parsed.add(LayerData.fromJson(GsonHelper.convertToJsonObject(element, "layers[]")));
      }
      layers = List.copyOf(parsed);
    }
    return new Geometry(layers);
  };

  /**
   * Colour, glow and tint override for one {@code layerN} texture, read from the model's
   * {@code "layers"} array running parallel to the texture indexes.
   *
   * <p>Upstream also reads a per-layer {@code "render_type"} here; per-model render types collapse
   * on Fabric (see {@link RenderTypeGroup}), so the key is accepted and ignored.
   *
   * @param color       ARGB colour baked into the layer, or -1 for untinted
   * @param luminosity  Block light level baked into the layer, 0 for none
   * @param noTint      True to suppress the layer's tint index, opting out of {@code ItemColors}
   */
  public record LayerData(int color, int luminosity, boolean noTint) {
    public static final LayerData DEFAULT = new LayerData(-1, 0, false);

    public static LayerData fromJson(JsonObject json) {
      int color = -1;
      if (json.has("color")) {
        // parsed as hex so the JSON can write "FF00FF00" rather than a signed decimal
        color = (int) Long.parseLong(GsonHelper.getAsString(json, "color"), 16);
      }
      return new LayerData(color, GsonHelper.getAsInt(json, "luminosity", 0), GsonHelper.getAsBoolean(json, "no_tint", false));
    }
  }

  /**
   * Geometry for the {@code mantle:item_layer} loader: a vanilla layered item model whose layers
   * can carry a baked-in colour, a glow and a tint opt-out. Every model in this tree declaring the
   * loader is single-layer, so the layers never contend for depth.
   */
  public static class Geometry implements IUnbakedGeometry<Geometry> {
    private final List<LayerData> layers;
    private List<Material> textures = List.of();

    public Geometry(List<LayerData> layers) {
      this.layers = layers;
    }

    /** Gets the data for the given layer, defaulting to plain */
    private LayerData getLayer(int index) {
      return index < layers.size() ? layers.get(index) : LayerData.DEFAULT;
    }

    @Override
    public void resolveParents(Function<ResourceLocation,UnbakedModel> modelGetter, IGeometryBakingContext owner) {
      List<Material> builder = new ArrayList<>();
      for (int i = 0; owner.hasMaterial("layer" + i); i++) {
        builder.add(owner.getMaterial("layer" + i));
      }
      textures = List.copyOf(builder);
    }

    @Override
    public BakedModel bake(IGeometryBakingContext owner, ModelBaker baker, Function<Material,TextureAtlasSprite> spriteGetter, ModelState transform, ItemOverrides overrides, ResourceLocation location) {
      if (textures.isEmpty()) {
        throw new IllegalStateException("Empty textures list for item layer model " + location);
      }
      TextureAtlasSprite particle = spriteGetter.apply(owner.hasMaterial("particle") ? owner.getMaterial("particle") : textures.get(0));
      Transformation rotation = transform.getRotation();
      ItemLayerPixels pixels = textures.size() == 1 ? null : new ItemLayerPixels();
      CompositeModel.Baked.Builder builder = CompositeModel.Baked.builder(owner, particle, overrides, owner.getTransforms());
      RenderTypeGroup renderType = getDefaultRenderType(owner);
      for (int i = 0; i < textures.size(); i++) {
        LayerData data = getLayer(i);
        builder.addQuads(renderType, getQuadsForSprite(data.color(), data.noTint() ? -1 : i, spriteGetter.apply(textures.get(i)), rotation, data.luminosity(), pixels));
      }
      return builder.build();
    }
  }
}
