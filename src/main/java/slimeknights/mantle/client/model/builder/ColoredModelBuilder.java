package slimeknights.mantle.client.model.builder;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import slimeknights.mantle.Mantle;
import slimeknights.mantle.client.model.generators.CustomLoaderBuilder;
import slimeknights.mantle.client.model.generators.ModelBuilder;
import slimeknights.mantle.data.ExistingFileHelper;

/**
 * Builder for mantle's colored block model loader ({@code mantle:colored_block}), which bakes
 * static tints into the model rather than using block color handlers.
 */
public class ColoredModelBuilder<T extends ModelBuilder<T>> extends CustomLoaderBuilder<T> {
  private final JsonArray colors = new JsonArray();

  public ColoredModelBuilder(T parent, ExistingFileHelper existingFileHelper) {
    super(Mantle.getResource("colored_block"), parent, existingFileHelper);
  }

  /** Adds a color to the model, one entry per element in element order */
  public ColoredModelBuilder<T> color(int color) {
    JsonObject entry = new JsonObject();
    entry.addProperty("color", String.format("%08X", color));
    colors.add(entry);
    return this;
  }

  @Override
  public JsonObject toJson(JsonObject json) {
    json = super.toJson(json);
    if (!colors.isEmpty()) {
      json.add("colors", colors);
    }
    return json;
  }
}
