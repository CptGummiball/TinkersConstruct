package slimeknights.mantle.client.model;

import slimeknights.mantle.client.RenderTypeGroup;

/**
 * Stand-in for the static half of {@code net.minecraftforge.client.model.DynamicFluidContainerModel}.
 *
 * <p>Tinkers' own {@code FluidContainerModel} is written as a rework of Forge's model rather than a
 * user of it, so the only member it borrows is the render type lookup — and that collapses on
 * Fabric, where a model does not choose its render layer (see {@link RenderTypeGroup}).
 *
 * <p>Forge used the flag to switch a glowing fluid onto an unlit, emissive-friendly render type.
 * The corresponding Fabric behaviour comes from the baked lightmap instead, which is applied where
 * the quads are built; nothing about layer selection is lost by returning an empty group here,
 * because Fabric's item renderer picks between the solid and translucent atlas shaders from the
 * model itself either way.
 */
public final class DynamicFluidContainerModel {
  private DynamicFluidContainerModel() {}

  /**
   * Render types for one layer of a fluid container.
   *
   * @param unlit  Whether the layer should ignore world lighting; has no effect on Fabric
   */
  public static RenderTypeGroup getLayerRenderTypes(boolean unlit) {
    return RenderTypeGroup.EMPTY;
  }
}
