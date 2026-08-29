package slimeknights.mantle.client.model.geometry;

import com.mojang.datafixers.util.Either;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.BlockElementFace;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.FaceBakery;
import net.minecraft.client.renderer.block.model.ItemModelGenerator;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Builds item geometry from a sprite and bakes loose element lists. Shim for
 * {@code net.minecraftforge.client.model.geometry.UnbakedGeometryHelper}.
 *
 * <p>A flat item is not one quad: it is the sprite's silhouette, with a side face closing every
 * transparent-to-opaque transition, which is what gives an item in hand its thickness. Forge
 * carried its own copy of that tracing; vanilla already has it in {@link ItemModelGenerator}, the
 * code behind {@code builtin/generated}, so both element factories below drive that instead of
 * reimplementing it — which also means Tinkers' dynamic items trace identically to a vanilla item.
 */
public final class UnbakedGeometryHelper {
  private UnbakedGeometryHelper() {}

  private static final FaceBakery FACE_BAKERY = new FaceBakery();
  private static final ItemModelGenerator ITEM_MODEL_GENERATOR = new ItemModelGenerator();
  /** Texture name the generated elements reference; must be one of {@link ItemModelGenerator#LAYERS}. */
  private static final String LAYER = "layer0";
  /** Depth of a mask layer, a hair in front of the 7.5 to 8.5 slab a generated item layer occupies */
  private static final float MASK_FRONT = 7.44F;
  private static final float MASK_BACK = 8.56F;

  /**
   * Builds the elements of a flat item layer from the sprite that will texture it.
   *
   * @param tintIndex  Tint index the faces report, so an {@code ItemColor} can recolour them
   * @param contents   Sprite whose silhouette the elements take
   */
  public static List<BlockElement> createUnbakedItemElements(int tintIndex, SpriteContents contents) {
    return trace(tintIndex, contents);
  }

  /**
   * Builds the elements of a layer shaped by a mask rather than by its own texture.
   *
   * <p>This cannot go through {@link ItemModelGenerator} the way {@link #createUnbakedItemElements}
   * does. That generator always emits one full-size front quad and lets the sprite's own alpha cut
   * the shape out — correct when the quad is textured with the very sprite that was traced, and
   * wrong here, because the caller textures these quads with a <em>different</em> sprite: the fluid
   * inside the container. A full quad would then paint the fluid over the whole item, which is
   * exactly what every filled bucket looked like before this was written out properly.
   *
   * <p>So the mask's opaque pixels are covered with real rectangles instead. Runs are merged
   * greedily, first along a row and then downwards, which turns a typical container mask into a
   * handful of boxes rather than one per pixel.
   *
   * @param tintIndex  Tint index the faces report
   * @param contents   Mask sprite whose opaque area the elements cover
   */
  public static List<BlockElement> createUnbakedItemMaskElements(int tintIndex, SpriteContents contents) {
    int width = contents.width();
    int height = contents.height();
    if (width <= 0 || height <= 0) {
      return List.of();
    }
    boolean[] opaque = new boolean[width * height];
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        opaque[y * width + x] = !contents.isTransparent(0, x, y);
      }
    }

    List<BlockElement> elements = new ArrayList<>();
    boolean[] used = new boolean[width * height];
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        if (!opaque[y * width + x] || used[y * width + x]) {
          continue;
        }
        // widen along the row
        int right = x;
        while (right + 1 < width && opaque[y * width + right + 1] && !used[y * width + right + 1]) {
          right++;
        }
        // then deepen while the whole run stays opaque
        int bottom = y;
        deepen:
        while (bottom + 1 < height) {
          for (int column = x; column <= right; column++) {
            int index = (bottom + 1) * width + column;
            if (!opaque[index] || used[index]) {
              break deepen;
            }
          }
          bottom++;
        }
        for (int row = y; row <= bottom; row++) {
          for (int column = x; column <= right; column++) {
            used[row * width + column] = true;
          }
        }
        elements.add(maskElement(tintIndex, x, y, right + 1, bottom + 1, width, height));
      }
    }
    return elements;
  }

  /** Builds one box of the mask, in the same coordinate space a generated item layer uses */
  private static BlockElement maskElement(int tintIndex, int left, int top, int right, int bottom, int width, int height) {
    float scaleX = 16F / width;
    float scaleY = 16F / height;
    // the model's y axis runs upwards while the sprite's runs downwards
    org.joml.Vector3f from = new org.joml.Vector3f(left * scaleX, 16 - bottom * scaleY, MASK_FRONT);
    org.joml.Vector3f to = new org.joml.Vector3f(right * scaleX, 16 - top * scaleY, MASK_BACK);
    Map<Direction,BlockElementFace> faces = new java.util.EnumMap<>(Direction.class);
    faces.put(Direction.SOUTH, new BlockElementFace(null, tintIndex, LAYER,
      new net.minecraft.client.renderer.block.model.BlockFaceUV(new float[]{left * scaleX, top * scaleY, right * scaleX, bottom * scaleY}, 0)));
    faces.put(Direction.NORTH, new BlockElementFace(null, tintIndex, LAYER,
      new net.minecraft.client.renderer.block.model.BlockFaceUV(new float[]{right * scaleX, top * scaleY, left * scaleX, bottom * scaleY}, 0)));
    return new BlockElement(from, to, faces, null, true);
  }

  /** Runs the vanilla generator over one sprite and renumbers the faces to the wanted tint index. */
  private static List<BlockElement> trace(int tintIndex, SpriteContents contents) {
    Material material = new Material(InventoryMenu.BLOCK_ATLAS, contents.name());
    BlockModel template = new BlockModel(null, List.of(), Map.of(LAYER, Either.left(material)), null, null, ItemTransforms.NO_TRANSFORMS, List.of());
    // the generator only reads the layer textures off the model, never bakes, so a stub sprite
    // getter that ignores the material is enough — it needs the contents, which it already has
    TextureAtlasSprite stub = new StubSprite(contents);
    List<BlockElement> generated = ITEM_MODEL_GENERATOR.generateBlockModel($ -> stub, template).getElements();
    if (tintIndex == 0) {
      return generated;
    }
    List<BlockElement> retinted = new ArrayList<>(generated.size());
    for (BlockElement element : generated) {
      Map<Direction,BlockElementFace> faces = new java.util.EnumMap<>(Direction.class);
      for (Map.Entry<Direction,BlockElementFace> entry : element.faces.entrySet()) {
        BlockElementFace face = entry.getValue();
        faces.put(entry.getKey(), new BlockElementFace(face.cullForDirection(), tintIndex, face.texture(), face.uv()));
      }
      retinted.add(new BlockElement(element.from, element.to, faces, element.rotation, element.shade));
    }
    return retinted;
  }

  /**
   * Bakes a loose list of elements into quads.
   *
   * <p>The sprite getter is expected to answer the same sprite for every material, which is how
   * both call sites use it and what Forge's contract assumed: the elements come from
   * {@link #createUnbakedItemElements} or {@link #createUnbakedItemMaskElements}, whose face
   * textures name the generator's internal layer rather than anything resolvable.
   *
   * @param elements      Elements to bake
   * @param spriteGetter  Sprite lookup, effectively constant
   * @param modelState    Rotation applied while baking
   * @param modelLocation Model id, for error messages
   */
  public static List<BakedQuad> bakeElements(List<BlockElement> elements, Function<Material,TextureAtlasSprite> spriteGetter, ModelState modelState, @Nullable ResourceLocation modelLocation) {
    TextureAtlasSprite sprite = spriteGetter.apply(new Material(InventoryMenu.BLOCK_ATLAS, MissingTextureAtlasSprite.getLocation()));
    List<BakedQuad> quads = new ArrayList<>();
    for (BlockElement element : elements) {
      for (Direction direction : element.faces.keySet()) {
        BlockElementFace face = element.faces.get(direction);
        quads.add(FACE_BAKERY.bakeQuad(element.from, element.to, face, sprite, direction, modelState, element.rotation, element.shade));
      }
    }
    return quads;
  }

  /**
   * Sprite that exists only to hand {@link ItemModelGenerator} the contents it traces.
   *
   * <p>The generator reads {@code contents()} and nothing else — it produces elements, not quads,
   * so it never touches the atlas position or uv range that a real stitched sprite would carry.
   */
  private static final class StubSprite extends TextureAtlasSprite {
    private StubSprite(SpriteContents contents) {
      super(InventoryMenu.BLOCK_ATLAS, contents, contents.width(), contents.height(), 0, 0);
    }
  }
}
