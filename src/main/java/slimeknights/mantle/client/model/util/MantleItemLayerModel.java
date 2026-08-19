package slimeknights.mantle.client.model.util;

import com.mojang.datafixers.util.Either;
import com.mojang.math.Transformation;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.BlockElementFace;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.FaceBakery;
import net.minecraft.client.renderer.block.model.ItemModelGenerator;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.Direction;
import net.minecraft.world.inventory.InventoryMenu;
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
   * @param pixels  Ignored — see {@link ItemLayerPixels} for why the suppression is not implemented
   *                and which model would need it
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
}
