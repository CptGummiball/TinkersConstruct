package slimeknights.tconstruct.library.data.recipe;

import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import slimeknights.mantle.recipe.data.IConditionalRecipeOutput;

import javax.annotation.Nullable;

/**
 * Helper to add extra result components to vanilla recipes, whose builders take an item
 * rather than a stack.
 *
 * <p>1.20 injected {@code nbt} into the serialized result; 1.21 replaced stack NBT with data
 * components, so this now writes the {@code components} map the vanilla item stack codec
 * reads. Component values that need registry context are fine: the wrapped patch is
 * serialized with plain ops, which covers the simple components this is used for (names).
 */
public record CraftingNBTWrapper(IConditionalRecipeOutput parent, DataComponentPatch components) implements IConditionalRecipeOutput {

  /** Creates a wrapped output, adding the given components to every result written through it */
  public static RecipeOutput wrap(RecipeOutput base, DataComponentPatch components) {
    return new CraftingNBTWrapper(IConditionalRecipeOutput.of(base), components);
  }

  @Override
  public JsonObject serializeRecipe(Recipe<?> recipe) {
    return parent.serializeRecipe(recipe);
  }

  @Override
  public void acceptJson(ResourceLocation id, JsonObject recipe, @Nullable AdvancementHolder advancement) {
    if (recipe.get("result") instanceof JsonObject result) {
      result.add("components", DataComponentPatch.CODEC.encodeStart(JsonOps.INSTANCE, components).getOrThrow());
    }
    parent.acceptJson(id, recipe, advancement);
  }

  @Override
  public Advancement.Builder advancement() {
    return parent.advancement();
  }
}
