package slimeknights.mantle.transfer.cap;

/** Shim of Forge's non-null consumer used by {@link LazyOptional} listeners. */
@FunctionalInterface
public interface NonNullConsumer<T> {
  void accept(T t);
}
