package slimeknights.mantle.client.model.data;

import java.util.function.Predicate;

/**
 * Typed key for a value carried in {@link ModelData}. Shim for
 * {@code net.minecraftforge.client.model.data.ModelProperty}.
 *
 * <p>Identity is the instance itself, exactly as on Forge: a property is created once as a
 * {@code static final} field and used as a map key.
 *
 * <p>Deliberately free of any client-only import. Tinkers declares one of these in
 * {@code slimeknights.mantle.util.RetexturedHelper}, which is loaded on the dedicated server too.
 *
 * @param <T> value type
 */
public class ModelProperty<T> {
  private final Predicate<T> predicate;

  public ModelProperty() {
    this(o -> true);
  }

  public ModelProperty(Predicate<T> predicate) {
    this.predicate = predicate;
  }

  /** Checks whether the given value may be stored under this property. */
  public boolean test(T value) {
    return predicate.test(value);
  }
}
