package slimeknights.mantle.client.model.geometry;

import com.mojang.math.Transformation;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;

/**
 * {@link IGeometryBakingContext} that overrides a few flags on top of another context. Shim for
 * {@code net.minecraftforge.client.model.geometry.StandaloneGeometryBakingContext}.
 *
 * <p>Forge's version could also stand alone with no delegate at all, for geometry baked outside any
 * model file. Nothing here does that — the one user wraps its own context to force a model flat in
 * inventories — so only the wrapping form is provided.
 */
public class StandaloneGeometryBakingContext implements IGeometryBakingContext {
  private final IGeometryBakingContext base;
  private final String name;
  @Nullable
  private final Boolean gui3d;
  @Nullable
  private final Boolean useBlockLight;
  @Nullable
  private final Boolean useAmbientOcclusion;

  private StandaloneGeometryBakingContext(IGeometryBakingContext base, String name, @Nullable Boolean gui3d, @Nullable Boolean useBlockLight, @Nullable Boolean useAmbientOcclusion) {
    this.base = base;
    this.name = name;
    this.gui3d = gui3d;
    this.useBlockLight = useBlockLight;
    this.useAmbientOcclusion = useAmbientOcclusion;
  }

  public static Builder builder(IGeometryBakingContext base) {
    return new Builder(base);
  }

  @Override
  public String getModelName() {
    return name;
  }

  @Override
  public boolean hasMaterial(String name) {
    return base.hasMaterial(name);
  }

  @Override
  public Material getMaterial(String name) {
    return base.getMaterial(name);
  }

  @Override
  public boolean useBlockLight() {
    return useBlockLight != null ? useBlockLight : base.useBlockLight();
  }

  @Override
  public boolean useAmbientOcclusion() {
    return useAmbientOcclusion != null ? useAmbientOcclusion : base.useAmbientOcclusion();
  }

  @Override
  public boolean isGui3d() {
    return gui3d != null ? gui3d : base.isGui3d();
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

  /** Builder collecting the overrides. */
  public static class Builder {
    private final IGeometryBakingContext base;
    @Nullable
    private Boolean gui3d;
    @Nullable
    private Boolean useBlockLight;
    @Nullable
    private Boolean useAmbientOcclusion;

    private Builder(IGeometryBakingContext base) {
      this.base = base;
    }

    public Builder withGui3d(boolean gui3d) {
      this.gui3d = gui3d;
      return this;
    }

    public Builder withUseBlockLight(boolean useBlockLight) {
      this.useBlockLight = useBlockLight;
      return this;
    }

    public Builder withUseAmbientOcclusion(boolean useAmbientOcclusion) {
      this.useAmbientOcclusion = useAmbientOcclusion;
      return this;
    }

    public StandaloneGeometryBakingContext build(ResourceLocation name) {
      return new StandaloneGeometryBakingContext(base, name.toString(), gui3d, useBlockLight, useAmbientOcclusion);
    }
  }
}
