package slimeknights.mantle.transfer.cap;

import net.minecraft.core.Direction;

import javax.annotation.Nullable;

/**
 * Shim of Forge's capability provider surface. Block entities that expose handlers to the
 * smeltery wiring implement this; the outward Fabric side is a separate storage registration.
 */
public interface ICapabilityProvider {

  /** Gets the handler for the given capability and side, empty when unsupported */
  <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction side);

  /** Side-less lookup, matching Forge's convenience overload */
  default <T> LazyOptional<T> getCapability(Capability<T> capability) {
    return getCapability(capability, null);
  }
}
