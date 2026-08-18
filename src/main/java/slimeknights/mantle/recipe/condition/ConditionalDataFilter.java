package slimeknights.mantle.recipe.condition;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import slimeknights.mantle.Mantle;

import java.util.Iterator;
import java.util.Map;

/**
 * Applies Forge-style {@code "conditions"} blocks while vanilla managers load their JSON,
 * standing in for the CraftingHelper hooks Forge patched into the loaders.
 *
 * <p>Two shapes exist in the shipped data: a top-level {@code "conditions"} array on the
 * entry itself, and the {@code forge:conditional} wrapper recipe whose {@code "recipes"}
 * branches each carry conditions plus a payload recipe — the first passing branch wins.
 * Entries whose conditions fail (or cannot be evaluated — failing open would double up
 * recipes the guard meant to disable) are removed before the vanilla parser sees them.
 */
public final class ConditionalDataFilter {
  private ConditionalDataFilter() {}

  private static final String CONDITIONAL_RECIPE_TYPE = "forge:conditional";

  /** Filters recipe JSON: unwraps {@code forge:conditional} and drops failed conditions. */
  public static void filterRecipes(Map<ResourceLocation, JsonElement> entries) {
    filter(entries, true);
  }

  /** Filters advancement JSON: drops entries whose conditions fail. */
  public static void filterAdvancements(Map<ResourceLocation, JsonElement> entries) {
    filter(entries, false);
  }

  private static void filter(Map<ResourceLocation, JsonElement> entries, boolean unwrapConditional) {
    ICondition.IContext context = DataConditionContext.current();
    Iterator<Map.Entry<ResourceLocation, JsonElement>> iterator = entries.entrySet().iterator();
    while (iterator.hasNext()) {
      Map.Entry<ResourceLocation, JsonElement> entry = iterator.next();
      if (!entry.getValue().isJsonObject()) {
        continue;
      }
      JsonObject json = entry.getValue().getAsJsonObject();
      try {
        if (unwrapConditional && CONDITIONAL_RECIPE_TYPE.equals(GsonHelper.getAsString(json, "type", ""))) {
          JsonObject selected = selectConditionalBranch(json, context);
          if (selected == null) {
            iterator.remove();
            continue;
          }
          entry.setValue(selected);
          json = selected;
        }
        // Forge's conditional-advancement wrapper: {"advancements": [{"conditions", "advancement"}, ...]}
        if (!unwrapConditional && json.has("advancements") && !json.has("criteria")) {
          JsonObject selected = selectAdvancementBranch(json, context);
          if (selected == null) {
            iterator.remove();
            continue;
          }
          entry.setValue(selected);
          json = selected;
        }
        if (!ConditionHelper.processConditions(json, "conditions", context)) {
          iterator.remove();
        }
      } catch (JsonSyntaxException e) {
        Mantle.logger.error("Failed evaluating load conditions of {}, skipping entry: {}", entry.getKey(), e.getMessage());
        iterator.remove();
      }
    }
  }

  /** Picks the payload of the first branch whose conditions pass, or null when none do */
  private static JsonObject selectConditionalBranch(JsonObject wrapper, ICondition.IContext context) {
    for (JsonElement branch : GsonHelper.getAsJsonArray(wrapper, "recipes")) {
      JsonObject branchObject = GsonHelper.convertToJsonObject(branch, "recipe entry");
      if (ConditionHelper.processConditions(branchObject, "conditions", context)) {
        return GsonHelper.getAsJsonObject(branchObject, "recipe");
      }
    }
    return null;
  }

  /** Same selection for the advancement wrapper's "advancements" branches */
  private static JsonObject selectAdvancementBranch(JsonObject wrapper, ICondition.IContext context) {
    for (JsonElement branch : GsonHelper.getAsJsonArray(wrapper, "advancements")) {
      JsonObject branchObject = GsonHelper.convertToJsonObject(branch, "advancement entry");
      if (ConditionHelper.processConditions(branchObject, "conditions", context)) {
        return GsonHelper.getAsJsonObject(branchObject, "advancement");
      }
    }
    return null;
  }
}
