package slimeknights.mantle.transfer.cap;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Shim of Forge's {@code LazyOptional}, kept because the smeltery multiblock builds its
 * neighbor-cache invalidation on this exact contract: holders hand out lazy views, attach
 * listeners, and {@link #invalidate()} tells every consumer to drop its cache. Fabric's
 * lookup API has no invalidation callback, so the internal wiring keeps this shape and only
 * the outward-facing side becomes Fabric storage registrations.
 */
public class LazyOptional<T> {
  private static final LazyOptional<Void> EMPTY = new LazyOptional<>(null);

  @Nullable
  private final NonNullSupplier<T> supplier;
  @Nullable
  private T resolved;
  private boolean isValid = true;
  @Nullable
  private List<NonNullConsumer<LazyOptional<T>>> listeners;

  private LazyOptional(@Nullable NonNullSupplier<T> supplier) {
    this.supplier = supplier;
  }

  /** Creates a lazy optional around the given supplier */
  public static <T> LazyOptional<T> of(NonNullSupplier<T> supplier) {
    return new LazyOptional<>(supplier);
  }

  /** Gets the singleton empty instance */
  @SuppressWarnings("unchecked")
  public static <T> LazyOptional<T> empty() {
    return (LazyOptional<T>) EMPTY;
  }

  @Nullable
  private T getValue() {
    if (!isValid || supplier == null) {
      return null;
    }
    if (resolved == null) {
      resolved = supplier.get();
    }
    return resolved;
  }

  /** True while this holds a supplier and has not been invalidated */
  public boolean isPresent() {
    return supplier != null && isValid;
  }

  /** Runs the consumer if a value is present */
  public void ifPresent(NonNullConsumer<? super T> consumer) {
    T value = getValue();
    if (value != null) {
      consumer.accept(value);
    }
  }

  /** Unchecked self-cast, mirroring the Forge contract (caller guarantees the type) */
  @SuppressWarnings("unchecked")
  public <X> LazyOptional<X> cast() {
    return (LazyOptional<X>) this;
  }

  /** Returns the value if present, else the fallback */
  public T orElse(T other) {
    T value = getValue();
    return value != null ? value : other;
  }

  /** Returns the value if present, else the supplied fallback */
  public T orElseGet(NonNullSupplier<? extends T> other) {
    T value = getValue();
    return value != null ? value : other.get();
  }

  /** Maps the value eagerly, empty when absent */
  public <U> Optional<U> map(Function<? super T, ? extends U> mapper) {
    T value = getValue();
    return value == null ? Optional.empty() : Optional.of(mapper.apply(value));
  }

  /** Maps into a new lazy optional resolving through this one */
  public <U> LazyOptional<U> lazyMap(Function<? super T, ? extends U> mapper) {
    return isPresent() ? of(() -> mapper.apply(orElseGet(() -> {
      throw new IllegalStateException("LazyOptional invalidated while mapping");
    }))) : empty();
  }

  /** Filters into a plain optional */
  public Optional<T> filter(Predicate<? super T> predicate) {
    T value = getValue();
    return value != null && predicate.test(value) ? Optional.of(value) : Optional.empty();
  }

  /** Resolves into a plain optional */
  public Optional<T> resolve() {
    return Optional.ofNullable(getValue());
  }

  /**
   * Registers a listener fired on {@link #invalidate()}; fired immediately when this is
   * already invalid, matching Forge.
   */
  public void addListener(NonNullConsumer<LazyOptional<T>> listener) {
    if (isPresent()) {
      if (listeners == null) {
        listeners = new ArrayList<>();
      }
      listeners.add(listener);
    } else {
      listener.accept(this);
    }
  }

  /** Invalidates the holder: consumers drop their caches via the registered listeners */
  public void invalidate() {
    if (isValid) {
      isValid = false;
      resolved = null;
      if (listeners != null) {
        List<NonNullConsumer<LazyOptional<T>>> toFire = listeners;
        listeners = null;
        toFire.forEach(listener -> listener.accept(this));
      }
    }
  }
}
