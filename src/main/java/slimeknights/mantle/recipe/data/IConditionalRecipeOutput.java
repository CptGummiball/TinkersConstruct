package slimeknights.mantle.recipe.data;

import com.google.gson.JsonObject;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import slimeknights.mantle.recipe.condition.ConditionHelper;
import slimeknights.mantle.recipe.condition.ICondition;

import javax.annotation.Nullable;

/**
 * The recipe output contract mantle datagen runs on. 1.21 deleted {@code FinishedRecipe} —
 * vanilla providers now hand real {@link Recipe} objects to a {@link RecipeOutput} and the
 * provider serializes them through {@link Recipe#CODEC}. That covers the vanilla case, but
 * this codebase writes three things vanilla cannot express:
 *
 * <ul>
 *   <li>load conditions ("conditions" array in Forge dialect, evaluated by mantle's
 *       {@link ConditionHelper} at load)</li>
 *   <li>type overrides (a recipe serialized under another serializer's name, e.g. a shaped
 *       recipe written as a {@code ceramics:kiln} recipe for a mod that is not installed)</li>
 *   <li>{@code forge:conditional} wrapper recipes, whose inner recipes are JSON fragments</li>
 * </ul>
 *
 * <p>All three need a JSON-level seam, so this interface adds one: recipes can be serialized
 * eagerly via {@link #serializeRecipe(Recipe)} (using the provider's registry ops) and handed
 * over as JSON via {@link #acceptJson(ResourceLocation, JsonObject, AdvancementHolder)}.
 * Wrappers like {@link ConsumerWrapperBuilder} patch the JSON in between; plain vanilla
 * builders never notice, since the default {@link RecipeOutput#accept} routes through the
 * same path unchanged.
 */
public interface IConditionalRecipeOutput extends RecipeOutput {

  /** Unwraps a recipe output, for builders that need the JSON-level seam directly. */
  static IConditionalRecipeOutput of(RecipeOutput output) {
    if (output instanceof IConditionalRecipeOutput conditional) {
      return conditional;
    }
    throw new IllegalStateException("This recipe requires a mantle recipe provider, got " + output.getClass().getName());
  }

  /** Serializes a recipe to JSON through the provider's registry-aware ops. */
  JsonObject serializeRecipe(Recipe<?> recipe);

  /**
   * Accepts a recipe already serialized to JSON. This is the single sink every recipe of the
   * provider flows through; duplicate-ID checking and file writing live behind it.
   */
  void acceptJson(ResourceLocation id, JsonObject recipe, @Nullable AdvancementHolder advancement);

  /** Accepts a recipe with load conditions attached. */
  default void accept(ResourceLocation id, Recipe<?> recipe, @Nullable AdvancementHolder advancement, ICondition... conditions) {
    JsonObject json = serializeRecipe(recipe);
    if (conditions.length > 0) {
      json.add("conditions", ConditionHelper.serialize(conditions));
    }
    acceptJson(id, json, advancement);
  }

  @Override
  default void accept(ResourceLocation id, Recipe<?> recipe, @Nullable AdvancementHolder advancement) {
    acceptJson(id, serializeRecipe(recipe), advancement);
  }
}
