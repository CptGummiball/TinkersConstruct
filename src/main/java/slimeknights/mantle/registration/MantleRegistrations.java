package slimeknights.mantle.registration;

import net.minecraft.world.level.block.entity.BlockEntityType;
import slimeknights.mantle.block.entity.MantleHangingSignBlockEntity;
import slimeknights.mantle.block.entity.MantleSignBlockEntity;

/**
 * Various objects registered under Mantle.
 *
 * <p>Forge filled these via {@code @ObjectHolder} injection. Fabric registers eagerly, so the
 * sign registration code assigns them directly during bootstrap; null until then.
 */
public class MantleRegistrations {

  private MantleRegistrations() {}

  public static BlockEntityType<MantleSignBlockEntity> SIGN;
  public static BlockEntityType<MantleHangingSignBlockEntity> HANGING_SIGN;
}
