package slimeknights.mantle.util;

import java.util.function.Supplier;

/**
 * Shim of Forge's {@code net.minecraftforge.common.util.Lazy}: a memoizing supplier.
 * Only the non-concurrent form is provided; Tinkers uses it for lazily-built constants.
 */
public interface Lazy<T> extends Supplier<T> {

  static <T> Lazy<T> of(Supplier<T> supplier) {
    return new Fast<>(supplier);
  }

  class Fast<T> implements Lazy<T> {
    private Supplier<T> supplier;
    private T instance;

    private Fast(Supplier<T> supplier) {
      this.supplier = supplier;
    }

    @Override
    public T get() {
      if (supplier != null) {
        instance = supplier.get();
        supplier = null;
      }
      return instance;
    }
  }
}
