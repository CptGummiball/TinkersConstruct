package slimeknights.tconstruct.library.utils;

import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.function.BiFunction;

/**
 * Helper for use with our extensions of resource location for some type safety in IDs.
 * Note we left {@link ResourceLocation#withPath(String)} and alike as returning {@link ResourceLocation} as there is not much use extending an ID.
 * @see IdParser
 */
public abstract class ResourceId extends ResourceLocation {
  // 1.21 note: ResourceLocation went final with private constructors and the validating
  // Dummy overload removed; the access widener re-opens the (namespace, path) constructor.
  // Validation happens in the factory paths (parse/tryParse) exactly as vanilla does it.

  public ResourceId(ResourceLocation location) {
    super(location.getNamespace(), location.getPath());
  }

  public ResourceId(String namespace, String path) {
    super(namespace, path);
  }

  public ResourceId(String location) {
    super(namespaceOf(location), pathOf(location));
  }

  private static String namespaceOf(String location) {
    int colon = location.indexOf(':');
    return colon >= 1 ? location.substring(0, colon) : "minecraft";
  }

  private static String pathOf(String location) {
    int colon = location.indexOf(':');
    return colon >= 0 ? location.substring(colon + 1) : location;
  }


  /* Helpers for static constructors */

  /**
   * Creates a new ID from the given string
   * @param string  String
   * @return  ID, or null if invalid
   */
  @Nullable
  protected static <T extends ResourceLocation> T tryParse(String string, BiFunction<String,String,T> constructor) {
    return tryBuild(namespaceOf(string), pathOf(string), constructor);
  }

  /**
   * Creates a new ID from the given namespace and path
   * @param namespace  Namespace
   * @param path       Path
   * @return  ID, or null if invalid
   */
  @Nullable
  protected static <T extends ResourceLocation> T tryBuild(String namespace, String path, BiFunction<String,String,T> constructor) {
    if (isValidNamespace(namespace) && isValidPath(path)) {
      return constructor.apply(namespace, path);
    }
    return null;
  }
}
