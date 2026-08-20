package slimeknights.tconstruct.library.client;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import slimeknights.mantle.block.entity.MantleBlockEntity;
import slimeknights.mantle.transfer.fluid.FluidTank;

/**
 * This class contains various methods that are safe to call on both sides, which internally call client only code.
 *
 * <p>Fabric port: upstream guarded these with {@code FMLEnvironment.dist} and hid the body in a
 * nested class, because the body touched {@code Minecraft}. It no longer does — asking a block
 * entity to rebuild its model is a common-side call that checks the level's own side — so the guard
 * and the nested class are gone. The class stays because it is the seam the tank code calls.
 */
public class SafeClient {
  /**
   * Triggers a model update if needed for this tank block
   * @param be          Block entity instance
   * @param tank        Fluid tank instance
   * @param oldAmount   Old fluid amount
   * @param newAmount   New fluid amount
   */
  public static void updateFluidModel(BlockEntity be, FluidTank tank, int oldAmount, int newAmount) {
    Level level = be.getLevel();
    // if the amount changed at all, the fluid's height in the model changed with it
    if (level != null && level.isClientSide && oldAmount != newAmount && be instanceof MantleBlockEntity mantle) {
      mantle.requestModelDataUpdate();
    }
  }
}
