package slimeknights.mantle.transfer.cap;

/** Shim of Forge's non-null supplier used by {@link LazyOptional}. */
@FunctionalInterface
public interface NonNullSupplier<T> {
  T get();
}
