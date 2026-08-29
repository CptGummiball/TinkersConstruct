package slimeknights.mantle.recipe;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import slimeknights.mantle.recipe.container.ContainerRecipeInput;
import slimeknights.mantle.recipe.container.IRecipeContainer;

/**
 * Recipe that has an output other than an {@link ItemStack} — melting, casting, modifiers.
 * @param <C>  Inventory type
 */
public interface ICustomOutputRecipe<C extends IRecipeContainer> extends ICommonRecipe<C> {

  /** @deprecated Item stack output not supported */
  @Override
  @Deprecated
  default ItemStack getResultItem(HolderLookup.Provider registries) {
    return ItemStack.EMPTY;
  }

  /** @deprecated Item stack output not supported */
  @Override
  @Deprecated
  default ItemStack assemble(ContainerRecipeInput<C> input, HolderLookup.Provider registries) {
    return ItemStack.EMPTY;
  }
}
