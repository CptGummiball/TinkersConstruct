package slimeknights.mantle.transfer.cap;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nullable;

/**
 * Replacement for Forge's {@code BlockEntity.getCapability} extension: resolves through the
 * shimmed {@link ICapabilityProvider} when the block entity implements it, empty otherwise.
 */
public class CapabilityHelper {
  private CapabilityHelper() {}

  /** Gets a capability from a block entity, sideless */
  public static <T> LazyOptional<T> get(@Nullable BlockEntity be, Capability<T> capability) {
    return get(be, capability, null);
  }

  /** Gets a capability from a block entity for the given side */
  public static <T> LazyOptional<T> get(@Nullable BlockEntity be, Capability<T> capability, @Nullable Direction side) {
    if (be instanceof ICapabilityProvider provider) {
      return provider.getCapability(capability, side);
    }
    return LazyOptional.empty();
  }
}
