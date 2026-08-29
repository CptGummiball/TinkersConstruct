package slimeknights.mantle.client.model.builder;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import slimeknights.mantle.Mantle;
import slimeknights.mantle.client.model.generators.CustomLoaderBuilder;
import slimeknights.mantle.client.model.generators.ModelBuilder;
import slimeknights.mantle.data.ExistingFileHelper;

import javax.annotation.Nullable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Builder for mantle's connected texture model loader ({@code mantle:connected}). Writes the
 * {@code connection} object mapping texture keys to connection types, the optional connection
 * predicate, and optionally baked colors as the colored loader would.
 */
public class ConnectedModelBuilder<T extends ModelBuilder<T>> extends CustomLoaderBuilder<T> {
  private final Map<String, String> connectedTextures = new LinkedHashMap<>();
  @Nullable
  private String predicate = null;
  private final JsonArray colors = new JsonArray();

  public ConnectedModelBuilder(T parent, ExistingFileHelper existingFileHelper) {
    super(Mantle.getResource("connected"), parent, existingFileHelper);
  }

  /** Marks the given texture key as connected with the given connection type */
  public ConnectedModelBuilder<T> connected(String texture, String type) {
    connectedTextures.put(texture, type);
    return this;
  }

  /** Sets the block connection predicate */
  public ConnectedModelBuilder<T> setPredicate(String predicate) {
    this.predicate = predicate;
    return this;
  }

  /** Adds a baked color to the model, one entry per element in element order */
  public ConnectedModelBuilder<T> color(int color) {
    JsonObject entry = new JsonObject();
    entry.addProperty("color", String.format("%08X", color));
    colors.add(entry);
    return this;
  }

  @Override
  public JsonObject toJson(JsonObject json) {
    json = super.toJson(json);
    JsonObject connection = new JsonObject();
    if (predicate != null) {
      connection.addProperty("predicate", predicate);
    }
    JsonObject textures = new JsonObject();
    connectedTextures.forEach(textures::addProperty);
    connection.add("textures", textures);
    json.add("connection", connection);
    if (!colors.isEmpty()) {
      json.add("colors", colors);
    }
    return json;
  }
}
