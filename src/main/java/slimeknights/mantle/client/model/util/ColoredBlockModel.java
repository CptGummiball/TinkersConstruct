package slimeknights.mantle.client.model.util;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.math.Transformation;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.BlockElementFace;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.SimpleBakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import slimeknights.mantle.client.model.IQuadTransformer;
import slimeknights.mantle.client.model.QuadTransformers;
import slimeknights.mantle.client.model.SimpleModelState;
import slimeknights.mantle.client.model.geometry.IGeometryBakingContext;
import slimeknights.mantle.util.LogicHelper;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * A {@link SimpleBlockModel} whose elements each carry an optional tint, light level and uv-lock
 * override, read from a {@code "colors"} array running parallel to {@code "elements"}.
 *
 * <p>No {@code IGeometryLoader} constant here: upstream Mantle exposes this as
 * {@code mantle:colored_block}, but {@link slimeknights.mantle.client.model.geometry.IGeometryLoader}
 * is bound to a self type and this class reports itself as an {@code IUnbakedGeometry<SimpleBlockModel>}
 * through its parent. The one model in this tree declaring that loader id is a Mantle model whose
 * geometry was never copied in, so nothing is lost; Tinkers' own users construct it directly.
 *
 * <p>Two things need this. A model with a glowing part — the lit face of a smeltery controller —
 * would otherwise need a separate model and render layer per state; and a model with a
 * dynamically coloured part — the fluid inside a tank — needs the colour applied after baking,
 * because the sprite is shared across every fluid and only the tint differs.
 */
public class ColoredBlockModel extends SimpleBlockModel {
  private final List<ColorData> colorData;

  public ColoredBlockModel(BlockModel model, List<ColorData> colorData) {
    super(model);
    this.colorData = colorData;
  }

  /** Per-element colour data, indexed the same as {@link #getElements()}. */
  public List<ColorData> getColorData() {
    return colorData;
  }

  /** Reads the model plus its {@code "colors"} array. */
  public static ColoredBlockModel deserialize(JsonObject json, JsonDeserializationContext context) {
    BlockModel model = context.deserialize(json, BlockModel.class);
    List<ColorData> colorData = List.of();
    if (json.has("colors")) {
      List<ColorData> parsed = new ArrayList<>();
      for (JsonElement element : GsonHelper.getAsJsonArray(json, "colors")) {
        parsed.add(ColorData.fromJson(GsonHelper.convertToJsonObject(element, "colors[]")));
      }
      colorData = List.copyOf(parsed);
    }
    return new ColoredBlockModel(model, colorData);
  }

  /**
   * Colour, light level and uv-lock override for a single element.
   *
   * @param color       ARGB tint multiplied over the element, or -1 for untinted
   * @param luminosity  Block light level baked into the element, 0 for none
   * @param uvLock      Overrides the blockstate's uv lock; null keeps whatever the state says
   */
  public record ColorData(int color, int luminosity, @Nullable Boolean uvLock) {
    /** Untinted, unlit, no uv-lock override — what an element with no {@code colors} entry gets. */
    public static final ColorData DEFAULT = new ColorData(-1, 0, null);

    public static ColorData fromJson(JsonObject json) {
      int color = -1;
      if (json.has("color")) {
        // parsed as hex so the JSON can write "FF00FF00" rather than a signed decimal
        color = (int) Long.parseLong(GsonHelper.getAsString(json, "color"), 16);
      }
      int luminosity = GsonHelper.getAsInt(json, "luminosity", 0);
      Boolean uvLock = json.has("uvlock") ? GsonHelper.getAsBoolean(json, "uvlock") : null;
      return new ColorData(color, luminosity, uvLock);
    }

    /** Resolves the uv lock, falling back to the blockstate's setting when unset. */
    public boolean isUvLock(boolean defaultValue) {
      return uvLock == null ? defaultValue : uvLock;
    }
  }

  /**
   * Quad transform that multiplies an ARGB colour over the vertex colours.
   *
   * @see QuadTransformers#applyingColor(int)
   */
  public static IQuadTransformer applyColorQuadTransformer(int color) {
    return QuadTransformers.applyingColor(color);
  }

  /**
   * Swaps the red and blue bytes of a packed colour.
   *
   * <p>{@code ItemColors} and the model JSON both speak ARGB, while a vertex's colour element is
   * four unsigned bytes in R,G,B,A order — little-endian ABGR when read as an int. Writing a colour
   * straight into the vertex array therefore needs this swap once.
   */
  public static int swapColorRedBlue(int color) {
    return (color & 0xFF00FF00) | ((color >> 16) & 0x000000FF) | ((color << 16) & 0x00FF0000);
  }

  /**
   * Bakes one element with a light level and a per-element uv lock, resolving textures against the
   * given context.
   *
   * <p>The uv lock cannot be folded into the caller's {@code ModelState}, because it varies per
   * element while the state is per model — hence rotation and lock arriving separately and being
   * recombined here.
   *
   * @param builder      Model being assembled
   * @param owner        Context textures resolve against; usually already retextured
   * @param element      Element to bake
   * @param luminosity   Block light baked into the element, 0 for none
   * @param spriteGetter Sprite lookup
   * @param rotation     Blockstate rotation
   * @param transformer  Transform applied to every baked quad, typically the element's colour
   * @param uvlock       Whether uvs stay put under the rotation
   * @param location     Model id, for error messages
   */
  public static void bakePart(SimpleBakedModel.Builder builder, IGeometryBakingContext owner, BlockElement element, int luminosity, Function<Material,TextureAtlasSprite> spriteGetter, Transformation rotation, IQuadTransformer transformer, boolean uvlock, @Nullable ResourceLocation location) {
    ModelState state = new SimpleModelState(rotation, uvlock);
    IQuadTransformer full = luminosity > 0 ? transformer.andThen(QuadTransformers.settingEmissivity(luminosity)) : transformer;
    for (Direction direction : element.faces.keySet()) {
      BlockElementFace face = element.faces.get(direction);
      TextureAtlasSprite sprite = spriteGetter.apply(owner.getMaterial(face.texture()));
      BakedQuad quad = FACE_BAKERY.bakeQuad(element.from, element.to, face, sprite, direction, state, element.rotation, element.shade);
      full.processInPlace(quad);
      if (face.cullForDirection() == null) {
        builder.addUnculledFace(quad);
      } else {
        builder.addCulledFace(Direction.rotate(rotation.getMatrix(), face.cullForDirection()), quad);
      }
    }
  }

  @Override
  public BakedModel bake(IGeometryBakingContext owner, ModelBaker baker, Function<Material,TextureAtlasSprite> spriteGetter, ModelState transform, ItemOverrides overrides, ResourceLocation location) {
    SimpleBakedModel.Builder builder = bakedBuilder(owner, overrides).particle(spriteGetter.apply(getMaterial(owner, "particle")));
    IQuadTransformer quadTransformer = applyTransform(transform, owner.getRootTransform());
    Transformation rotation = transform.getRotation();
    boolean defaultUvLock = transform.isUvLocked();
    List<BlockElement> elements = getElements();
    for (int i = 0; i < elements.size(); i++) {
      ColorData colors = LogicHelper.getOrDefault(colorData, i, ColorData.DEFAULT);
      IQuadTransformer partTransformer = colors.color() == -1 ? quadTransformer : quadTransformer.andThen(applyColorQuadTransformer(colors.color()));
      bakePart(builder, owner, elements.get(i), colors.luminosity(), spriteGetter, rotation, partTransformer, colors.isUvLock(defaultUvLock), location);
    }
    return builder.build();
  }
}
