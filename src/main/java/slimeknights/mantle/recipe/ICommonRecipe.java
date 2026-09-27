package slimeknights.mantle.recipe;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import slimeknights.mantle.recipe.container.ContainerRecipeInput;
import slimeknights.mantle.recipe.container.IRecipeContainer;

/**
 * Extension of {@link Recipe} defaulting the methods that are always set the same way.
 *
 * <p>1.21 changes carried here: vanilla matches recipes against {@code RecipeInput}, which
 * the container hierarchy cannot implement (see {@link ContainerRecipeInput}). The vanilla
 * surface therefore runs on the wrapped input and delegates to the container-typed methods
 * that the recipe implementations keep from 1.20.
 */
public interface ICommonRecipe<C extends IRecipeContainer> extends Recipe<ContainerRecipeInput<C>> {

  /** Container-typed matches, the method recipe implementations override (1.20 signature). */
  boolean matches(C container, Level level);

  @Override
  default boolean matches(ContainerRecipeInput<C> input, Level level) {
    return matches(input.container(), level);
  }

  /**
   * Container-typed assemble, the 1.20 signature callers use directly; implementations
   * with custom output override this. The vanilla input-typed surface below delegates here.
   */
  default ItemStack assemble(C container, HolderLookup.Provider registries) {
    return getResultItem(registries).copy();
  }

  @Override
  default ItemStack assemble(ContainerRecipeInput<C> input, HolderLookup.Provider registries) {
    return assemble(input.container(), registries);
  }

  /** @deprecated Means nothing outside of crafting tables */
  @Deprecated
  @Override
  default boolean canCraftInDimensions(int width, int height) {
    return true;
  }

  /** Returns true to hide this recipe from the recipe book. */
  @Override
  default boolean isSpecial() {
    return true;
  }
}
