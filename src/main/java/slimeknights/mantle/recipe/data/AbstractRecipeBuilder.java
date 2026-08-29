package slimeknights.mantle.recipe.data;

import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Common logic to create a recipe builder class.
 *
 * <p>1.21 rework: {@code FinishedRecipe} is gone, so builders construct the real recipe
 * object and hand it to {@link RecipeOutput#accept} — serialization happens in the provider
 * through the same codec the game parses with. The advancement half changed shape too:
 * instead of returning an advancement ID for a {@code FinishedRecipe} to serialize later,
 * {@link #buildAdvancement(RecipeOutput, ResourceLocation, String)} now returns the built
 * {@link AdvancementHolder} to pass along in the same {@code accept} call.
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public abstract class AbstractRecipeBuilder<T extends AbstractRecipeBuilder<T>> {
  /**
   * Criteria for the recipe unlock advancement, in insertion order. Kept as our own map
   * rather than a vanilla {@code Advancement.Builder} so a repeated name replaces the
   * earlier criterion instead of erroring at build.
   */
  protected final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();
  /** Group for this recipe */
  @Nonnull
  protected String group = "";

  /**
   * Adds a criteria to the recipe
   * @param name      Criteria name
   * @param criterion Criteria instance
   * @return  Builder
   */
  @SuppressWarnings("unchecked")
  public T unlockedBy(String name, Criterion<?> criterion) {
    this.criteria.put(name, criterion);
    return (T)this;
  }

  /**
   * Sets the group for this recipe
   * @param group  Recipe group
   * @return  Builder
   */
  @SuppressWarnings("unchecked")
  public T group(String group) {
    this.group = group;
    return (T)this;
  }

  /**
   * Sets the group for this recipe
   * @param group  Recipe resource location group
   * @return  Builder
   */
  public T group(ResourceLocation group) {
    // if minecraft, no namepsace. Groups are technically not namespaced so this is for consistency with vanilla
    if ("minecraft".equals(group.getNamespace())) {
      return group(group.getPath());
    }
    return group(group.toString());
  }

  /**
   * Builds the recipe with a default recipe ID, typically based on the output
   * @param output  Recipe output
   */
  public abstract void save(RecipeOutput output);

  /**
   * Builds the recipe
   * @param output  Recipe output
   * @param id      Recipe ID
   */
  public abstract void save(RecipeOutput output, ResourceLocation id);

  /**
   * Base logic for advancement building
   * @param output  Recipe output, supplies the preconfigured recipe advancement builder
   * @param id      Recipe ID
   * @param folder  Group folder for saving recipes. Vanilla typically uses item groups, but for mods might as well base on the recipe
   * @return Built advancement holder
   */
  private AdvancementHolder buildAdvancementInternal(RecipeOutput output, ResourceLocation id, String folder) {
    // we add through the map as we want to replace an existing criterion of the same name instead of erroring
    this.criteria.put("has_the_recipe", RecipeUnlockedTrigger.unlocked(id));
    var builder = output.advancement()
      .rewards(AdvancementRewards.Builder.recipe(id))
      .requirements(AdvancementRequirements.Strategy.OR);
    this.criteria.forEach(builder::addCriterion);
    return builder.build(ResourceLocation.fromNamespaceAndPath(id.getNamespace(), "recipes/" + folder + "/" + id.getPath()));
  }

  /**
   * Builds and validates the advancement, intended to be called in {@link #save(RecipeOutput, ResourceLocation)}
   * @param output  Recipe output
   * @param id      Recipe ID
   * @param folder  Group folder for saving recipes. Vanilla typically uses item groups, but for mods might as well base on the recipe
   * @return Advancement holder
   */
  protected AdvancementHolder buildAdvancement(RecipeOutput output, ResourceLocation id, String folder) {
    if (this.criteria.isEmpty()) {
      throw new IllegalStateException("No way of obtaining recipe " + id);
    }
    return buildAdvancementInternal(output, id, folder);
  }

  /**
   * Builds an optional advancement, intended to be called in {@link #save(RecipeOutput, ResourceLocation)}
   * @param output  Recipe output
   * @param id      Recipe ID
   * @param folder  Group folder for saving recipes. Vanilla typically uses item groups, but for mods might as well base on the recipe
   * @return Advancement holder, or null if no criteria were defined
   */
  @SuppressWarnings("SameParameterValue")  // API
  @Nullable
  protected AdvancementHolder buildOptionalAdvancement(RecipeOutput output, ResourceLocation id, String folder) {
    if (this.criteria.isEmpty()) {
      return null;
    }
    return buildAdvancementInternal(output, id, folder);
  }
}
