package slimeknights.mantle.client.model.builder;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import slimeknights.mantle.Mantle;
import slimeknights.mantle.client.model.generators.CustomLoaderBuilder;
import slimeknights.mantle.client.model.generators.ModelBuilder;
import slimeknights.mantle.data.ExistingFileHelper;

/**
 * Builder for mantle's item layer model loader ({@code mantle:item_layer}), which supports
 * per-layer static colors and luminosity.
 */
public class MantleItemLayerBuilder<T extends ModelBuilder<T>> extends CustomLoaderBuilder<T> {
  private final JsonArray layers = new JsonArray();

  public MantleItemLayerBuilder(T parent, ExistingFileHelper existingFileHelper) {
    super(Mantle.getResource("item_layer"), parent, existingFileHelper);
  }

  /** Adds a color for the next layer */
  public MantleItemLayerBuilder<T> color(int color) {
    JsonObject entry = new JsonObject();
    entry.addProperty("color", String.format("%08X", color));
    layers.add(entry);
    return this;
  }

  @Override
  public JsonObject toJson(JsonObject json) {
    json = super.toJson(json);
    if (!layers.isEmpty()) {
      json.add("layers", layers);
    }
    return json;
  }
}
