package slimeknights.tconstruct.library.json;

import com.google.gson.JsonObject;
import lombok.Data;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import slimeknights.mantle.recipe.condition.ConditionHelper;
import slimeknights.mantle.recipe.condition.ICondition;
import slimeknights.mantle.util.JsonHelper;

import javax.annotation.Nullable;

/** Represents a redirect in a material or modifier JSON */
@SuppressWarnings("ClassCanBeRecord") // GSON does not support records
@Data
public class JsonRedirect {
  private final ResourceLocation id;
  @Nullable
  private final ICondition condition;

  /** Serializes this to JSON */
  public JsonObject toJson() {
    JsonObject json = new JsonObject();
    json.addProperty("id", id.toString());
    if (condition != null) {
      json.add("condition", ConditionHelper.serialize(condition));
    }
    return json;
  }

  /** Deserializes this to JSON */
  public static JsonRedirect fromJson(JsonObject json) {
    ResourceLocation id = JsonHelper.getResourceLocation(json, "id");
    ICondition condition = null;
    if (json.has("condition")) {
      condition = ConditionHelper.getCondition(GsonHelper.getAsJsonObject(json, "condition"));
    }
    return new JsonRedirect(id, condition);
  }
}
