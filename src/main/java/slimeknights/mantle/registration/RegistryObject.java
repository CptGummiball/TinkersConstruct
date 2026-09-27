package slimeknights.mantle.registration;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Fabric stand-in for Forge's {@code net.minecraftforge.registries.RegistryObject}.
 *
 * <p>Forge registered lazily, so a {@code RegistryObject} was a promise that resolved once the
 * registry event fired. Fabric registers eagerly, so the value is simply present — but the
 * holder is kept because Tinkers' registration objects are declared as static fields that pass
 * these around, and removing the indirection would mean rewriting all of them.
 *
 * @param <T> registry entry type
 */
public class RegistryObject<T> implements Supplier<T> {

  private final ResourceLocation id;
  @Nullable
  private final T value;

  private RegistryObject(ResourceLocation id, @Nullable T value) {
    this.id = id;
    this.value = value;
  }

  /** Wraps an already-registered value. */
  public static <T> RegistryObject<T> of(ResourceLocation id, T value) {
    return new RegistryObject<>(id, Objects.requireNonNull(value, "value"));
  }

  /** Looks an entry up in a registry, yielding an empty holder when absent. */
  public static <T> RegistryObject<T> of(ResourceLocation id, Registry<T> registry) {
    return new RegistryObject<>(id, registry.get(id));
  }

  public ResourceLocation getId() {
    return id;
  }

  @Override
  public T get() {
    if (value == null) {
      throw new IllegalStateException("Registry object " + id + " is not present");
    }
    return value;
  }

  public boolean isPresent() {
    return value != null;
  }

  public Optional<T> asOptional() {
    return Optional.ofNullable(value);
  }

  @Override
  public String toString() {
    return "RegistryObject[" + id + "]";
  }
}
