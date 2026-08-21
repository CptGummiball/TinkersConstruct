package slimeknights.mantle.recipe.crafting;

import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import slimeknights.mantle.recipe.MantleRecipes;
import slimeknights.mantle.recipe.data.IConditionalRecipeOutput;

import javax.annotation.Nullable;

/**
 * Builds a shaped recipe that copies its texture from an input, by wrapping a vanilla shaped
 * recipe builder.
 *
 * <p>1.21 rework: instead of decorating a {@code FinishedRecipe}, the wrap happens at the
 * mantle provider's JSON seam — the vanilla builder serializes as usual, then the type is
 * swapped to the retextured serializer and the texture key appended, which is exactly the
 * shape the runtime serializer parses. Only the key form of the texture source survives;
 * the deprecated ingredient form no longer exists in 1.21's holder-backed ingredients and
 * nothing generated it anymore.
 */
@SuppressWarnings("unused")
@RequiredArgsConstructor(staticName = "fromShaped")
public class ShapedRetexturedRecipeBuilder {
  private final ShapedRecipeBuilder parent;
  private char textureKey = '\0';
  private boolean matchAll = false;

  /** Sets the texture source to a key from the texture map. Is not validated as that is too much work. */
  public ShapedRetexturedRecipeBuilder setSource(char textureKey) {
    this.textureKey = textureKey;
    return this;
  }

  /**
   * Sets the match first property on the recipe.
   * If set, the recipe uses the first ingredient match for the texture. If unset, all items that match the ingredient must be the same or no texture is applied
   * @return Builder instance
   */
  public ShapedRetexturedRecipeBuilder setMatchAll() {
    this.matchAll = true;
    return this;
  }

  /**
   * Builds the recipe with the default name using the given output
   * @param output Recipe output
   */
  public void build(RecipeOutput output) {
    this.validate();
    parent.save(new Wrapped(IConditionalRecipeOutput.of(output), textureKey, matchAll));
  }

  /**
   * Builds the recipe using the given output
   * @param output   Recipe output
   * @param location Recipe location
   */
  public void build(RecipeOutput output, ResourceLocation location) {
    this.validate();
    parent.save(new Wrapped(IConditionalRecipeOutput.of(output), textureKey, matchAll), location);
  }

  /**
   * Ensures this recipe can be built
   * @throws IllegalStateException If the recipe cannot be built
   */
  private void validate() {
    if (textureKey == '\0') {
      throw new IllegalStateException("No texture defined for texture recipe");
    }
  }

  /** Swaps the serialized shaped recipe onto the retextured type and appends the texture keys */
  private record Wrapped(IConditionalRecipeOutput parent, char textureKey, boolean matchAll) implements IConditionalRecipeOutput {
    @Override
    public JsonObject serializeRecipe(Recipe<?> recipe) {
      return parent.serializeRecipe(recipe);
    }

    @Override
    public void acceptJson(ResourceLocation id, JsonObject recipe, @Nullable AdvancementHolder advancement) {
      recipe.addProperty("type", BuiltInRegistries.RECIPE_SERIALIZER.getKey(MantleRecipes.CRAFTING_SHAPED_RETEXTURED.get()).toString());
      recipe.addProperty("texture", String.valueOf(textureKey));
      recipe.addProperty("match_all", matchAll);
      parent.acceptJson(id, recipe, advancement);
    }

    @Override
    public Advancement.Builder advancement() {
      return parent.advancement();
    }
  }
}
