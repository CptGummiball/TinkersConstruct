package slimeknights.mantle.client.model.geometry;

import net.minecraft.resources.ResourceLocation;
import slimeknights.mantle.Mantle;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Registry of {@code "loader"} ids to the geometry loader that handles them.
 *
 * <p>Replaces Forge's {@code ModelEvent.RegisterGeometryLoaders}. Forge fired that event once per
 * client start and stored the map inside its patched model deserializer; here the map is a plain
 * static registry that {@link GeometryModelLoadingPlugin} reads while resolving models. Register
 * from the client entrypoint, before the first resource reload.
 */
public final class GeometryLoaderRegistry {
  private GeometryLoaderRegistry() {}

  private static final Map<ResourceLocation,IGeometryLoader<?>> LOADERS = new ConcurrentHashMap<>();

  /**
   * Registers a loader for the given {@code "loader"} id.
   *
   * @throws IllegalArgumentException if the id is already taken
   */
  public static void register(ResourceLocation id, IGeometryLoader<?> loader) {
    IGeometryLoader<?> existing = LOADERS.putIfAbsent(id, loader);
    if (existing != null) {
      throw new IllegalArgumentException("Duplicate geometry loader registered for " + id);
    }
    Mantle.logger.debug("Registered geometry loader {}", id);
  }

  /** Gets the loader for the given id, or null if none is registered. */
  @Nullable
  public static IGeometryLoader<?> get(ResourceLocation id) {
    return LOADERS.get(id);
  }

  /** Checks whether any loader is registered; lets the resolver skip its work entirely. */
  public static boolean isEmpty() {
    return LOADERS.isEmpty();
  }
}
