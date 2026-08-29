package slimeknights.mantle.fluid;

import slimeknights.mantle.transfer.fluid.FluidType;

/**
 * Fluid type whose color and textures are determined by the model.
 *
 * <p>On Forge this hooked {@code IClientFluidTypeExtensions}; on Fabric the client module
 * (phase 5) registers a {@code FluidRenderHandler} for fluids whose type is an instance of
 * this class, reading the same {@code mantle/fluid_texture} JSONs.
 */
public class TextureFluidType extends FluidType {

  public TextureFluidType(Properties properties) {
    super(properties);
  }
}
