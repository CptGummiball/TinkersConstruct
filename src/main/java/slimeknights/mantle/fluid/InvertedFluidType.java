package slimeknights.mantle.fluid;

import slimeknights.mantle.transfer.fluid.FluidType;

/**
 * Fluid type adding an extra flipped texture for the in-world block, for upward-flowing
 * fluids. Client rendering registers in phase 5; see {@link TextureFluidType}.
 */
public class InvertedFluidType extends TextureFluidType {

  public InvertedFluidType(Properties properties) {
    super(properties);
  }
}
