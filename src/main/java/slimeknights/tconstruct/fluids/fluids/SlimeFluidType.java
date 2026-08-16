package slimeknights.tconstruct.fluids.fluids;

import net.minecraft.world.entity.LivingEntity;
import slimeknights.mantle.fluid.TextureFluidType;
import slimeknights.tconstruct.common.TinkerTags;

/** Fluid Type that does not affect slimes */
public class SlimeFluidType extends TextureFluidType {
  public SlimeFluidType(Properties properties) {
    super(properties);
  }

  @Override
  public boolean canDrownIn(LivingEntity entity) {
    return !entity.getType().is(TinkerTags.EntityTypes.SLIMES);
  }

  /**
   * Marker subtype for fluids that flow upwards (molten ichor/cinderslime).
   *
   * <p>Forge attached {@code ClientInvertedFluidType} here through {@code initializeClient} to
   * flip the flowing texture; on Fabric the equivalent is a {@code FluidRenderHandler}
   * registered in the client phase, keyed off this type.
   */
  public static class Inverted extends SlimeFluidType {
    public Inverted(Properties properties) {
      super(properties);
    }
  }
}
