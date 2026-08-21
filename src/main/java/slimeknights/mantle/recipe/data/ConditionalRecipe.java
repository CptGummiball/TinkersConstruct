package slimeknights.mantle.recipe.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import slimeknights.mantle.recipe.condition.ConditionHelper;
import slimeknights.mantle.recipe.condition.ICondition;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Datagen shim for Forge's {@code ConditionalRecipe}: several condition-guarded recipe
 * alternatives under one ID, first match wins. The runtime side is the RecipeManager mixin,
 * which unwraps {@code forge:conditional} at load — this builder writes the exact shape that
 * mixin parses:
 *
 * <pre>{"type": "forge:conditional", "recipes": [{"conditions": [...], "recipe": {...}}]}</pre>
 *
 * <p>Inner recipes arrive as callbacks writing into a normal {@link RecipeOutput}; they are
 * captured as JSON fragments instead of files. Only the last advancement any alternative
 * produced is written, matching Forge, whose builder took one advancement for the whole set.
 */
public class ConditionalRecipe {

  private ConditionalRecipe() {}

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private final List<ICondition[]> conditions = new ArrayList<>();
    private final List<Consumer<RecipeOutput>> recipes = new ArrayList<>();
    private List<ICondition> pending = new ArrayList<>();
    private boolean generateAdvancement = false;

    /** Adds a condition guarding the next recipe added. */
    public Builder addCondition(ICondition condition) {
      pending.add(condition);
      return this;
    }

    /**
     * Requests the unlock advancement of the wrapped recipes to be written alongside the
     * wrapper; without this, matching Forge, inner advancements are dropped.
     */
    public Builder generateAdvancement() {
      generateAdvancement = true;
      return this;
    }

    /** Adds a recipe alternative guarded by the conditions added since the last one. */
    public Builder addRecipe(Consumer<RecipeOutput> recipe) {
      if (pending.isEmpty()) {
        throw new IllegalStateException("Cannot add a recipe without conditions");
      }
      conditions.add(pending.toArray(new ICondition[0]));
      pending = new ArrayList<>();
      recipes.add(recipe);
      return this;
    }

    /** Writes the conditional wrapper to the provider output. */
    public void build(RecipeOutput output, ResourceLocation id) {
      if (!pending.isEmpty()) {
        throw new IllegalStateException("Conditions added without a recipe");
      }
      if (recipes.isEmpty()) {
        throw new IllegalStateException("Cannot build a conditional recipe with no recipes");
      }
      if (!(output instanceof IConditionalRecipeOutput parent)) {
        throw new IllegalStateException("Conditional recipes require a mantle recipe provider, got " + output.getClass().getName());
      }
      JsonArray alternatives = new JsonArray();
      AdvancementHolder advancement = null;
      for (int i = 0; i < recipes.size(); i++) {
        Capture capture = new Capture(parent);
        recipes.get(i).accept(capture);
        if (capture.recipe == null) {
          throw new IllegalStateException("Recipe callback " + i + " for " + id + " wrote no recipe");
        }
        JsonObject entry = new JsonObject();
        entry.add("conditions", ConditionHelper.serialize(conditions.get(i)));
        entry.add("recipe", capture.recipe);
        alternatives.add(entry);
        if (capture.advancement != null) {
          advancement = capture.advancement;
        }
      }
      JsonObject json = new JsonObject();
      json.addProperty("type", "forge:conditional");
      json.add("recipes", alternatives);
      parent.acceptJson(id, json, generateAdvancement ? advancement : null);
    }
  }

  /** Recipe output capturing a single serialized recipe instead of writing it to disk */
  private static class Capture implements IConditionalRecipeOutput {
    private final IConditionalRecipeOutput parent;
    @Nullable
    private JsonObject recipe;
    @Nullable
    private AdvancementHolder advancement;

    private Capture(IConditionalRecipeOutput parent) {
      this.parent = parent;
    }

    @Override
    public JsonObject serializeRecipe(Recipe<?> recipe) {
      return parent.serializeRecipe(recipe);
    }

    @Override
    public void acceptJson(ResourceLocation id, JsonObject recipe, @Nullable AdvancementHolder advancement) {
      if (this.recipe != null) {
        throw new IllegalStateException("Conditional recipe alternative wrote more than one recipe: " + id);
      }
      this.recipe = recipe;
      this.advancement = advancement;
    }

    @Override
    public Advancement.Builder advancement() {
      return parent.advancement();
    }
  }
}
