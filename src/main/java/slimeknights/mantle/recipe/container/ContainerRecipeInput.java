package slimeknights.mantle.recipe.container;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

/**
 * Bridges a recipe container into 1.21's {@link RecipeInput}.
 *
 * <p>Why a wrapper: a type implementing both {@code Container} and {@code RecipeInput} cannot
 * be remapped — {@code getItem}/{@code isEmpty} exist on both vanilla interfaces under one
 * named signature but different intermediary names, which the jar remapper rejects as an
 * unfixable conflict. Wrapping keeps the container hierarchy purely {@code Container}-based
 * while vanilla recipe APIs receive this input view.
 */
public record ContainerRecipeInput<C extends IRecipeContainer>(C container) implements RecipeInput {

  @Override
  public ItemStack getItem(int index) {
    return container.getItem(index);
  }

  @Override
  public int size() {
    return container.getContainerSize();
  }

  @Override
  public boolean isEmpty() {
    return container.isEmpty();
  }
}
