package slimeknights.mantle.client.model.util;

import com.mojang.math.Transformation;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.resources.model.Material;
import slimeknights.mantle.client.model.geometry.IGeometryBakingContext;

import java.util.Map;

/**
 * {@link IGeometryBakingContext} that answers a handful of texture names from an override map and
 * delegates everything else to another context.
 *
 * <p>This is how the dynamic models rebake themselves: the tank model resolves the contained
 * fluid's sprites, drops them in under the names {@code fluid} and {@code flowing_fluid}, and bakes
 * the very same elements again against this view.
 */
public class ExtraTextureContext implements IGeometryBakingContext {
  private final IGeometryBakingContext base;
  private final Map<String,Material> extraTextures;

  public ExtraTextureContext(IGeometryBakingContext base, Map<String,Material> extraTextures) {
    this.base = base;
    this.extraTextures = extraTextures;
  }

  /** Strips the {@code #} a face uses to reference a texture, as vanilla's lookup does. */
  private static String trim(String name) {
    return !name.isEmpty() && name.charAt(0) == '#' ? name.substring(1) : name;
  }

  @Override
  public String getModelName() {
    return base.getModelName();
  }

  @Override
  public boolean hasMaterial(String name) {
    return extraTextures.containsKey(trim(name)) || base.hasMaterial(name);
  }

  @Override
  public Material getMaterial(String name) {
    Material material = extraTextures.get(trim(name));
    return material != null ? material : base.getMaterial(name);
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
