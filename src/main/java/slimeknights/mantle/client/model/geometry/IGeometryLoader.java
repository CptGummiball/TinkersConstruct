package slimeknights.mantle.client.model.geometry;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

/**
 * Reads an {@link IUnbakedGeometry} out of a model JSON that carries a {@code "loader"} key.
 *
 * <p>Mirrors {@code net.minecraftforge.client.model.geometry.IGeometryLoader}. Forge patched the
 * vanilla model deserializer to dispatch on that key; Fabric has no such hook, so
 * {@link GeometryModelLoadingPlugin} does the dispatch from a
 * {@code net.fabricmc.fabric.api.client.model.loading.v1.ModelResolver} instead. The interface
 * shape is kept identical so the geometry classes port by import swap.
 *
 * @param <T> geometry type produced by this loader
 */
@FunctionalInterface
public interface IGeometryLoader<T extends IUnbakedGeometry<T>> {
  /**
   * Reads the geometry from the model JSON.
   *
   * @param json     the full model JSON, including the {@code "loader"} key itself
   * @param context  deserialization context wired to a GSON that knows the vanilla model types
   *                 ({@code BlockModel}, {@code BlockElement}, {@code ItemTransforms}, ...), so
   *                 {@code context.deserialize(json, BlockModel.class)} works as it did on Forge
   * @throws JsonParseException if the JSON is malformed
   */
  T read(JsonObject json, JsonDeserializationContext context) throws JsonParseException;
}
