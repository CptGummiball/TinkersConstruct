package slimeknights.tconstruct.library.recipe.ingredient;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredient;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

/**
 * Ingredient that contains another ingredient nested inside.
 *
 * <p>Fabric port of the Forge {@code AbstractIngredient} base: implements Fabric's
 * {@link CustomIngredient}, deferring the stack test and display list to the nested vanilla
 * ingredient.
 */
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class NestedIngredient implements CustomIngredient {
  protected final Ingredient nested;


  /* Defer to nested */

  @Override
  public boolean test(@Nullable ItemStack stack) {
    return nested.test(stack);
  }

  @Override
  public List<ItemStack> getMatchingStacks() {
    return Arrays.asList(nested.getItems());
  }

  @Override
  public boolean requiresTesting() {
    // custom ingredients wrapping a plain item list still add semantics (e.g. container
    // checks), so default to testing; subclasses relax this where matching is item-level
    return true;
  }
}
