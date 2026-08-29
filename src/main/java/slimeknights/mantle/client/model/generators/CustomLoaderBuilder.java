package slimeknights.mantle.client.model.generators;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import slimeknights.mantle.data.ExistingFileHelper;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Base for builders writing a {@code loader} key and custom loader data into a model,
 * port of Forge's builder of the same name.
 */
public class CustomLoaderBuilder<T extends ModelBuilder<T>> {
  protected final ResourceLocation loaderId;
  protected final T parent;
  protected final ExistingFileHelper existingFileHelper;
  protected final Map<String, Boolean> visibility = new LinkedHashMap<>();

  protected CustomLoaderBuilder(ResourceLocation loaderId, T parent, ExistingFileHelper existingFileHelper) {
    this.loaderId = loaderId;
    this.parent = parent;
    this.existingFileHelper = existingFileHelper;
  }

  public CustomLoaderBuilder<T> visibility(String partName, boolean show) {
    this.visibility.put(partName, show);
    return this;
  }

  /** Finishes the loader portion, returning the parent model builder */
  public T end() {
    return parent;
  }

  public JsonObject toJson(JsonObject json) {
    json.addProperty("loader", loaderId.toString());

    if (!visibility.isEmpty()) {
      JsonObject visibilityObj = new JsonObject();
      for (Map.Entry<String, Boolean> entry : visibility.entrySet()) {
        visibilityObj.addProperty(entry.getKey(), entry.getValue());
      }
      json.add("visibility", visibilityObj);
    }
    return json;
  }
}
