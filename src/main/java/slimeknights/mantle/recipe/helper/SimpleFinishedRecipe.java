package slimeknights.mantle.recipe.helper;

import com.google.gson.JsonObject;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import slimeknights.mantle.recipe.data.IConditionalRecipeOutput;

import java.util.Objects;

/**
 * Datagen helper for recipes that are nothing but their serializer type — vanilla-style
 * "special" recipes whose behaviour is entirely in code (repair, dyeing, and the like).
 *
 * <p>1.21 rework: {@code FinishedRecipe} is gone and these serializers have no shared recipe
 * class to hand the codec path, so the single-key JSON is written directly through the
 * mantle provider's JSON seam. The class name stays for the 1.20 diff.
 */
public class SimpleFinishedRecipe {

  private SimpleFinishedRecipe() {}

  /** Writes a recipe consisting only of the given serializer's type. */
  public static void save(RecipeOutput output, ResourceLocation id, RecipeSerializer<?> serializer) {
    JsonObject json = new JsonObject();
    json.addProperty("type", Objects.requireNonNull(BuiltInRegistries.RECIPE_SERIALIZER.getKey(serializer), "Unregistered serializer for recipe " + id).toString());
    IConditionalRecipeOutput.of(output).acceptJson(id, json, null);
  }
}
