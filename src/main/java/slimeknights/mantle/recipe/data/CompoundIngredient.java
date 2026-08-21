package slimeknights.mantle.recipe.data;

import net.fabricmc.fabric.api.recipe.v1.ingredient.DefaultCustomIngredients;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.Arrays;

/**
 * Shim for Forge's {@code CompoundIngredient} union factory, for datagen.
 *
 * <p>Forge flattened all-vanilla children into a single plain value array, and that is the
 * shape every shipped data file keeps — plain {@code [{"tag"...},{"item"...}]} arrays.
 * Fabric's {@code DefaultCustomIngredients.any} instead wraps in a {@code fabric:any} custom
 * ingredient, which parses fine but is only needed when a child is itself custom. So: merge
 * value arrays when every child is vanilla, fall back to {@code fabric:any} otherwise.
 */
public class CompoundIngredient {

  private CompoundIngredient() {}

  /** Creates an ingredient matching any of the children */
  public static Ingredient of(Ingredient... children) {
    for (Ingredient child : children) {
      if (child.getCustomIngredient() != null) {
        return DefaultCustomIngredients.any(children);
      }
    }
    return Ingredient.fromValues(Arrays.stream(children).flatMap(child -> Arrays.stream(child.values)));
  }
}
