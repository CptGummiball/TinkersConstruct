package slimeknights.mantle.transfer.cap;

import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import slimeknights.mantle.transfer.TransferUtil;

import javax.annotation.Nullable;

/**
 * Replacement for Forge's {@code BlockEntity.getCapability} extension: resolves through the
 * shimmed {@link ICapabilityProvider} when the block entity implements it. For any other
 * block entity — another mod's tank or inventory — it falls back to the Fabric lookups, so
 * faucets, channels, gauges and tank tools work against the rest of the pack like they did
 * on Forge. Without the fallback those devices silently found nothing on foreign blocks.
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
    // foreign block entity: resolve through the Fabric lookup. Sided only — every caller that
    // passes a null side is targeting one of our own block entities, which take the shim path,
    // and Fabric's SIDED lookups declare a non-null direction context.
    if (be != null && side != null) {
      Level level = be.getLevel();
      if (level != null) {
        if (capability == ForgeCapabilities.FLUID_HANDLER) {
          return TransferUtil.getFluidHandler(level, be.getBlockPos(), side)
            .map(handler -> LazyOptional.of(() -> handler).<T>cast())
            .orElseGet(LazyOptional::empty);
        }
        if (capability == ForgeCapabilities.ITEM_HANDLER) {
          return TransferUtil.getItemHandler(level, be.getBlockPos(), side)
            .map(handler -> LazyOptional.of(() -> handler).<T>cast())
            .orElseGet(LazyOptional::empty);
        }
      }
    }
    return LazyOptional.empty();
  }
}
