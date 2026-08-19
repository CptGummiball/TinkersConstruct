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
   * <p>The distinction from {@link #createUnbakedItemElements} is only which sprite is traced: the
   * caller bakes these with a sprite getter that returns a different texture — the fluid inside a
   * container — so the fluid shows exactly where the container's mask is opaque and nowhere else.
   *
   * @param tintIndex  Tint index the faces report
   * @param contents   Mask sprite whose opaque area the elements cover
   */
  public static List<BlockElement> createUnbakedItemMaskElements(int tintIndex, SpriteContents contents) {
    return trace(tintIndex, contents);
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
