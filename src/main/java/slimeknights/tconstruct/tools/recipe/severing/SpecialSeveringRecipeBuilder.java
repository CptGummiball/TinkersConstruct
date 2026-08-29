package slimeknights.tconstruct.tools.recipe.severing;

import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import slimeknights.mantle.recipe.data.AbstractRecipeBuilder;
import slimeknights.mantle.recipe.data.IConditionalRecipeOutput;
import slimeknights.tconstruct.library.recipe.modifiers.severing.SeveringRecipe;

import java.util.Objects;
import java.util.function.Supplier;

/** Builder for severing recipes that have only the base chance and looting bonus as fields */
@Setter
@Accessors(chain = true)
@RequiredArgsConstructor(staticName = "serializer")
public class SpecialSeveringRecipeBuilder extends AbstractRecipeBuilder<SpecialSeveringRecipeBuilder> {
  private final RecipeSerializer<? extends SeveringRecipe> serializer;
  private float baseChance = 0.05f;
  private float lootingBonus = 0.01f;

  /** Creates a new builder for the given serializer. */
  public static SpecialSeveringRecipeBuilder serializer(Supplier<? extends RecipeSerializer<? extends SeveringRecipe>> supplier) {
    return serializer(supplier.get());
  }

  /** Doubles the drop chances for this rare mob */
  public SpecialSeveringRecipeBuilder rareMob() {
    baseChance = 0.1f;
    lootingBonus = 0.02f;
    return this;
  }

  @SuppressWarnings("deprecation")
  @Override
  public void save(RecipeOutput consumer) {
    save(consumer, Objects.requireNonNull(BuiltInRegistries.RECIPE_SERIALIZER.getKey(serializer)));
  }

  @Override
  public void save(RecipeOutput consumer, ResourceLocation id) {
    // written as raw JSON: these recipes are pure serializer dispatch with two floats, there
    // is no shared recipe class to construct for the codec path
    JsonObject json = new JsonObject();
    json.addProperty("type", Objects.requireNonNull(BuiltInRegistries.RECIPE_SERIALIZER.getKey(serializer)).toString());
    json.addProperty("per_level_chance", baseChance);
    json.addProperty("looting_bonus", lootingBonus);
    IConditionalRecipeOutput.of(consumer).acceptJson(id, json, null);
  }

  /** Finished recipe instance */
}
