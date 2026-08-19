package slimeknights.mantle.client.model.data;

import javax.annotation.Nullable;
import java.util.IdentityHashMap;
import java.util.Map;

/**
 * Immutable bag of {@link ModelProperty} values handed to a baked model at render time. Shim for
 * {@code net.minecraftforge.client.model.data.ModelData}.
 *
 * <p>The container itself is faithful — build one, read it back, derive a new one — so any code
 * that constructs model data and immediately bakes from it behaves exactly as it did on Forge.
 *
 * <p>PORT: what has no Fabric analogue is the <em>delivery</em>. Forge asked every block entity for
 * its {@code ModelData} during a chunk rebuild and threaded the result through
 * {@code BakedModel.getQuads}. Vanilla 1.21.1 has no such parameter, so the block-render pipeline
 * always passes {@link #EMPTY} and a data-driven model falls back to its base variant. Fabric's
 * equivalent is a render attachment ({@code RenderDataBlockEntity} plus
 * {@code RenderAttachedBlockView}), read through the Fabric renderer API; wiring that up belongs
 * with the block-entity render slice, not here. Item rendering is unaffected — it never used model
 * data, it goes through {@code ItemOverrides}, which works as before.
 */
public final class ModelData {
  /** Shared empty instance; also what the block pipeline passes until render attachments are wired up. */
  public static final ModelData EMPTY = new ModelData(Map.of());

  private final Map<ModelProperty<?>,Object> properties;

  private ModelData(Map<ModelProperty<?>,Object> properties) {
    this.properties = properties;
  }

  /** Checks whether a value is present for the given property. */
  public boolean has(ModelProperty<?> property) {
    return properties.containsKey(property);
  }

  /** Gets the value stored for the given property, or null if absent. */
  @SuppressWarnings("unchecked")
  @Nullable
  public <T> T get(ModelProperty<T> property) {
    return (T) properties.get(property);
  }

  /** Creates a builder pre-filled with this data's contents. */
  public Builder derive() {
    return new Builder(properties);
  }

  /** Creates an empty builder. */
  public static Builder builder() {
    return new Builder(Map.of());
  }

  /** Builder for {@link ModelData}. */
  public static final class Builder {
    private final Map<ModelProperty<?>,Object> properties;

    private Builder(Map<ModelProperty<?>,Object> initial) {
      this.properties = new IdentityHashMap<>(initial);
    }

    /** Stores a value under the given property; a null value removes it. */
    public <T> Builder with(ModelProperty<T> property, @Nullable T value) {
      if (value == null) {
        properties.remove(property);
      } else {
        if (!property.test(value)) {
          throw new IllegalArgumentException("Value " + value + " rejected by model property");
        }
        properties.put(property, value);
      }
      return this;
    }

    public ModelData build() {
      return properties.isEmpty() ? EMPTY : new ModelData(Map.copyOf(properties));
    }
  }
}
