package slimeknights.mantle.client.model;

import com.mojang.math.Transformation;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.BlockElementFace;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import slimeknights.mantle.client.model.geometry.IGeometryBakingContext;
import slimeknights.mantle.client.model.util.SimpleBlockModel;

import java.util.HashSet;
import java.util.Set;

/**
 * Support for models whose textures are swapped at render time for a block chosen in NBT.
 *
 * <p>Upstream Mantle also registers this as a {@code mantle:retextured} geometry loader; that
 * loader has no source in this tree, so only the two pieces Tinkers' own geometry calls are here —
 * the texture-name expansion and the baking context that performs the swap.
 */
public final class RetexturedModel {
  private RetexturedModel() {}

  /** Strips the {@code #} a face uses to reference a texture, as vanilla's lookup does. */
  private static String trim(String name) {
    return !name.isEmpty() && name.charAt(0) == '#' ? name.substring(1) : name;
  }

  /**
   * Expands a set of texture names to include every other name in the model that aliases them.
   *
   * <p>A model JSON routinely points several names at one texture — {@code "particle": "#top"} on
   * the anvils, for instance. Retexturing only the name the JSON listed would leave the aliases
   * showing the placeholder, so every face texture (and {@code particle}) that resolves to the same
   * {@link Material} as a requested name is pulled in too.
   *
   * @param owner      Context the names resolve against
   * @param model      Model whose elements are scanned for aliases
   * @param wanted     Texture names the model asked to have swapped
   * @return  {@code wanted} plus every alias of it present in the model
   */
  public static Set<String> getAllRetextured(IGeometryBakingContext owner, SimpleBlockModel model, Set<String> wanted) {
    // resolve what the requested names actually point at
    Set<Material> targets = new HashSet<>();
    for (String name : wanted) {
      if (owner.hasMaterial(name)) {
        targets.add(owner.getMaterial(name));
      }
    }
    Set<String> retextured = new HashSet<>(wanted);
    if (targets.isEmpty()) {
      return retextured;
    }
    // any face pointing at the same texture is the same texture by another name
    for (BlockElement element : model.getElements()) {
      for (BlockElementFace face : element.faces.values()) {
        String name = trim(face.texture());
        if (!retextured.contains(name) && owner.hasMaterial(name) && targets.contains(owner.getMaterial(name))) {
          retextured.add(name);
        }
      }
    }
    // particle is never named by a face, but it decides the break/step texture, so check it too
    if (!retextured.contains("particle") && owner.hasMaterial("particle") && targets.contains(owner.getMaterial("particle"))) {
      retextured.add("particle");
    }
    return retextured;
  }

  /**
   * Baking context that reports one chosen texture for a set of names and delegates the rest.
   *
   * <p>The texture is a bare {@link ResourceLocation} — the particle texture of the block being
   * copied — so it is resolved against the block atlas, which is where any block texture is
   * stitched.
   */
  public static class RetexturedContext implements IGeometryBakingContext {
    private final IGeometryBakingContext base;
    private final Set<String> retextured;
    private final Material texture;

    public RetexturedContext(IGeometryBakingContext base, Set<String> retextured, ResourceLocation texture) {
      this.base = base;
      this.retextured = retextured;
      this.texture = new Material(InventoryMenu.BLOCK_ATLAS, texture);
    }

    @Override
    public String getModelName() {
      return base.getModelName();
    }

    @Override
    public boolean hasMaterial(String name) {
      return retextured.contains(trim(name)) || base.hasMaterial(name);
    }

    @Override
    public Material getMaterial(String name) {
      if (retextured.contains(trim(name))) {
        return texture;
      }
      return base.getMaterial(name);
    }

    @Override
    public boolean useBlockLight() {
      return base.useBlockLight();
    }

    @Override
    public boolean useAmbientOcclusion() {
      return base.useAmbientOcclusion();
    }

    @Override
    public boolean isGui3d() {
      return base.isGui3d();
    }

    @Override
    public ItemTransforms getTransforms() {
      return base.getTransforms();
    }

    @Override
    public Transformation getRootTransform() {
      return base.getRootTransform();
    }

    @Override
    public boolean isComponentVisible(String component, boolean fallback) {
      return base.isComponentVisible(component, fallback);
    }
  }
}
