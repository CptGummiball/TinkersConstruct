package slimeknights.mantle.client.model.geometry;

import com.mojang.math.Transformation;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.resources.model.Material;

/**
 * The model-level data an {@link IUnbakedGeometry} needs while baking: textures, display
 * transforms and lighting flags.
 *
 * <p>Mirrors {@code net.minecraftforge.client.model.geometry.IGeometryBakingContext}. Forge built
 * one of these from its patched {@code BlockModel}; {@link BlockGeometryBakingContext} builds it
 * from the stock 1.21.1 {@link net.minecraft.client.renderer.block.model.BlockModel} parsed from
 * the very same JSON, so the geometry classes see the data they saw on Forge.
 *
 * <p>Only the members the ported geometry actually uses are declared. Forge additionally carried
 * {@code getRenderTypeHint()} and {@code getVisibilityData()}; the render type hint has no Fabric
 * analogue (block render layers are registered per block, not per model) and the visibility data is
 * only read by Forge's own composite model, so neither is reproduced here.
 */
public interface IGeometryBakingContext {
  /** Name of the model being baked, for error messages. */
  String getModelName();

  /** Checks whether the named texture reference resolves to a real texture. */
  boolean hasMaterial(String name);

  /** Resolves the named texture reference; returns the missing texture if it does not resolve. */
  Material getMaterial(String name);

  /** If true the model is lit like a block rather than like a flat item. */
  boolean useBlockLight();

  /** If true smooth lighting applies to the baked quads. */
  boolean useAmbientOcclusion();

  /** If true the model renders with depth in inventories. */
  boolean isGui3d();

  /** Display transforms for the eight item display contexts. */
  ItemTransforms getTransforms();

  /**
   * Extra transform applied to the whole model, from Forge's root {@code "transform"} key.
   *
   * <p>Nothing in Tinkers' 1.21.1 model set uses it on a geometry model (the handful of
   * {@code "transform"} keys in the block models sit on plain vanilla models, where the key is
   * simply ignored as it always was outside Forge), so the default is the identity.
   */
  default Transformation getRootTransform() {
    return Transformation.identity();
  }

  /**
   * Whether a named sub-component of the model should be baked. Forge drove this from the
   * {@code "visibility"} key; no Tinkers model sets it, so the fallback is always returned.
   */
  default boolean isComponentVisible(String component, boolean fallback) {
    return fallback;
  }
}
