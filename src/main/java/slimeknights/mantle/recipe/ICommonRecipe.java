package slimeknights.mantle.recipe;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;

/**
 * Extension of {@link Recipe} defaulting the methods that are always set the same way.
 *
 * <p>1.21 changes carried here: the inventory bound moved from {@code Container} to
 * {@link RecipeInput}, and registry access arrives as {@link HolderLookup.Provider} rather
 * than {@code RegistryAccess}.
 */
public interface ICommonRecipe<C extends RecipeInput> extends Recipe<C> {

  @Override
  default ItemStack assemble(C input, HolderLookup.Provider registries) {
    return getResultItem(registries).copy();
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
