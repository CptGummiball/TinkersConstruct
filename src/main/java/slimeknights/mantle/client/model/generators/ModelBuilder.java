package slimeknights.mantle.client.model.generators;

import com.google.common.base.Preconditions;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import slimeknights.mantle.data.ExistingFileHelper;

import javax.annotation.Nullable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * Builder for a single model JSON, port of Forge's builder of the same name. Carries the
 * surface the tinkers providers use: parent, textures, render type and custom loaders.
 * Vanilla element/display building was never used by any provider in this tree; models needing
 * them extend existing parents instead.
 */
public class ModelBuilder<T extends ModelBuilder<T>> extends ModelFile {
  protected final ExistingFileHelper existingFileHelper;

  @Nullable
  protected ModelFile parent;
  protected final Map<String, String> textures = new LinkedHashMap<>();
  @Nullable
  protected ResourceLocation renderType = null;
  @Nullable
  protected CustomLoaderBuilder<T> customLoader = null;

  protected ModelBuilder(ResourceLocation outputLocation, ExistingFileHelper existingFileHelper) {
    super(outputLocation);
    this.existingFileHelper = existingFileHelper;
  }

  @SuppressWarnings("unchecked")
  private T self() {
    return (T)this;
  }

  @Override
  protected boolean exists() {
    return true;
  }

  /**
   * Set the parent model for the current model.
   * @param parent the parent model, cannot be null
   * @return this builder
   */
  public T parent(ModelFile parent) {
    Preconditions.checkNotNull(parent, "Parent must not be null");
    parent.assertExistence();
    this.parent = parent;
    return self();
  }

  /**
   * Set the texture for a given dictionary key.
   * @param key     the texture key
   * @param texture the texture, can be another key e.g. {@code #all}
   * @return this builder
   */
  public T texture(String key, String texture) {
    Preconditions.checkNotNull(key, "Key must not be null");
    Preconditions.checkNotNull(texture, "Texture must not be null");
    if (texture.charAt(0) == '#') {
      this.textures.put(key, texture);
      return self();
    }
    // absolute path: validate as a real texture
    ResourceLocation asLoc;
    if (texture.contains(":")) {
      asLoc = ResourceLocation.parse(texture);
    } else {
      asLoc = ResourceLocation.fromNamespaceAndPath(location.getNamespace(), texture);
    }
    return texture(key, asLoc);
  }

  /**
   * Set the texture for a given dictionary key.
   * @param key     the texture key
   * @param texture the texture
   * @return this builder
   */
  public T texture(String key, ResourceLocation texture) {
    Preconditions.checkNotNull(key, "Key must not be null");
    Preconditions.checkNotNull(texture, "Texture must not be null");
    Preconditions.checkArgument(existingFileHelper.exists(texture, ModelProvider.TEXTURE),
                                "Texture %s does not exist in any known resource pack", texture);
    this.textures.put(key, texture.toString());
    return self();
  }

  /** Sets the render type name, resolved against the minecraft namespace when bare */
  public T renderType(String renderType) {
    Preconditions.checkNotNull(renderType, "Render type must not be null");
    return renderType(ResourceLocation.parse(renderType));
  }

  /** Sets the render type */
  public T renderType(ResourceLocation renderType) {
    Preconditions.checkNotNull(renderType, "Render type must not be null");
    this.renderType = renderType;
    return self();
  }

  /** Use a custom loader instead of the vanilla elements. */
  public <L extends CustomLoaderBuilder<T>> L customLoader(BiFunction<T, ExistingFileHelper, L> customLoaderFactory) {
    Preconditions.checkNotNull(customLoaderFactory, "customLoaderFactory must not be null");
    L customLoader = customLoaderFactory.apply(self(), existingFileHelper);
    this.customLoader = customLoader;
    return customLoader;
  }

  public JsonObject toJson() {
    JsonObject root = new JsonObject();

    if (this.parent != null) {
      root.addProperty("parent", this.parent.getLocation().toString());
    }

    if (this.renderType != null) {
      root.addProperty("render_type", this.renderType.toString());
    }

    if (!this.textures.isEmpty()) {
      JsonObject textures = new JsonObject();
      for (Map.Entry<String, String> e : this.textures.entrySet()) {
        textures.addProperty(e.getKey(), serializeLocOrKey(e.getValue()));
      }
      root.add("textures", textures);
    }

    if (customLoader != null) {
      return customLoader.toJson(root);
    }
    return root;
  }

  private String serializeLocOrKey(String tex) {
    if (tex.charAt(0) == '#') {
      return tex;
    }
    return ResourceLocation.parse(tex).toString();
  }
}
