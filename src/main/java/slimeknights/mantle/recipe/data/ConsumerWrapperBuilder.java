package slimeknights.mantle.recipe.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import slimeknights.mantle.recipe.condition.ConditionHelper;
import slimeknights.mantle.recipe.condition.ICondition;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Builds a recipe output wrapper, which adds some extra properties to every recipe written
 * through it: load conditions and optionally a serializer ("type") override.
 *
 * <p>1.21 rework: the wrapped thing used to be a {@code Consumer<FinishedRecipe>} patching
 * the serialized JSON. The consumer is now a {@link RecipeOutput}, but the job is unchanged —
 * the wrapper intercepts at {@link IConditionalRecipeOutput#acceptJson} where the JSON exists,
 * patches it, and forwards. The type override stays a plain string replacement on purpose:
 * its use case is writing a recipe under another mod's serializer name (e.g.
 * {@code ceramics:kiln}), whose codec is not available to serialize with.
 */
@SuppressWarnings("unused")  // API
public class ConsumerWrapperBuilder {
  private final List<ICondition> conditions = new ArrayList<>();
  @Nullable
  private final ResourceLocation overrideName;

  private ConsumerWrapperBuilder(@Nullable ResourceLocation overrideName) {
    this.overrideName = overrideName;
  }

  /**
   * Creates a wrapper builder with the default serializer
   * @return Default serializer builder
   */
  public static ConsumerWrapperBuilder wrap() {
    return new ConsumerWrapperBuilder(null);
  }

  /**
   * Creates a wrapper builder with a serializer override
   * @param override Serializer override
   * @return Default serializer builder
   */
  public static ConsumerWrapperBuilder wrap(RecipeSerializer<?> override) {
    return new ConsumerWrapperBuilder(Objects.requireNonNull(BuiltInRegistries.RECIPE_SERIALIZER.getKey(override), "Unregistered recipe serializer " + override));
  }

  /**
   * Creates a wrapper builder with a serializer name override
   * @param override Serializer override
   * @return Default serializer builder
   */
  public static ConsumerWrapperBuilder wrap(ResourceLocation override) {
    return new ConsumerWrapperBuilder(override);
  }

  /**
   * Adds a conditional to the consumer
   * @param condition Condition to add
   * @return Added condition
   */
  public ConsumerWrapperBuilder addCondition(ICondition condition) {
    this.conditions.add(condition);
    return this;
  }

  /**
   * Builds the wrapped output
   * @param output  Output to wrap
   * @return Output with the extra properties applied to every recipe written
   */
  public RecipeOutput build(RecipeOutput output) {
    if (!(output instanceof IConditionalRecipeOutput parent)) {
      throw new IllegalStateException("Recipe conditions and type overrides require a mantle recipe provider, got " + output.getClass().getName());
    }
    return new Wrapped(parent, conditions, overrideName);
  }

  private record Wrapped(IConditionalRecipeOutput parent, List<ICondition> conditions, @Nullable ResourceLocation overrideName) implements IConditionalRecipeOutput {
    @Override
    public JsonObject serializeRecipe(Recipe<?> recipe) {
      return parent.serializeRecipe(recipe);
    }

    @Override
    public void acceptJson(ResourceLocation id, JsonObject recipe, @Nullable AdvancementHolder advancement) {
      if (overrideName != null) {
        recipe.addProperty("type", overrideName.toString());
      }
      if (!conditions.isEmpty()) {
        // append after any the recipe already carries: with nested wrappers the innermost
        // writes first, which is the order the forge datagen produced
        JsonArray array;
        if (recipe.has("conditions")) {
          array = recipe.getAsJsonArray("conditions");
        } else {
          array = new JsonArray();
          recipe.add("conditions", array);
        }
        for (JsonElement condition : ConditionHelper.serialize(conditions.toArray(new ICondition[0]))) {
          array.add(condition);
        }
      }
      parent.acceptJson(id, recipe, advancement);
    }

    @Override
    public Advancement.Builder advancement() {
      return parent.advancement();
    }
  }
}
