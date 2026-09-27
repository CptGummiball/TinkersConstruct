package slimeknights.mantle.registration.deferred;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import slimeknights.mantle.registration.RegistryObject;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * Fabric-native replacement for the Forge {@code DeferredRegister} wrapper.
 *
 * <p>Forge collected suppliers and resolved them when its registry event fired; Fabric
 * registers eagerly, so {@link #register} runs the supplier immediately and returns an
 * already-filled {@link RegistryObject}. The API surface is kept identical because all of
 * Tinkers' registration modules are written against it — only the timing semantics change,
 * and they change in the direction that removes a failure mode (there is no window where a
 * {@code RegistryObject} exists but is empty).
 *
 * <p>Registration order still matters on Fabric exactly as far as it did on Forge: modules
 * run in the order the entrypoint calls them, which mirrors the Forge build's bus order.
 */
public class SynchronizedDeferredRegister<T> {

  private final ResourceKey<? extends Registry<T>> key;
  private final String modid;

  private SynchronizedDeferredRegister(ResourceKey<? extends Registry<T>> key, String modid) {
    this.key = key;
    this.modid = modid;
  }

  /** Creates a new instance for the given resource key */
  public static <T> SynchronizedDeferredRegister<T> create(ResourceKey<? extends Registry<T>> key, String modid) {
    return new SynchronizedDeferredRegister<>(key, modid);
  }

  /** Registers the given object immediately. */
  public <I extends T> RegistryObject<I> register(final String name, final Supplier<? extends I> sup) {
    ResourceLocation id = ResourceLocation.fromNamespaceAndPath(modid, name);
    I value = Registry.register(registry(), id, sup.get());
    return RegistryObject.of(id, value);
  }

  /** Resolves the actual registry; custom registries must exist before first use. */
  @SuppressWarnings("unchecked")
  private Registry<T> registry() {
    return Objects.requireNonNull(
      (Registry<T>) BuiltInRegistries.REGISTRY.get(key.location()),
      () -> "Registry " + key.location() + " does not exist");
  }
}
