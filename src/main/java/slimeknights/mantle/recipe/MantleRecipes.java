package slimeknights.mantle.recipe;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import slimeknights.mantle.registration.RegistryObject;
import slimeknights.mantle.Mantle;
import slimeknights.mantle.recipe.crafting.ShapedFallbackRecipe;
import slimeknights.mantle.recipe.crafting.ShapedRetexturedRecipe;
import slimeknights.mantle.registration.deferred.SynchronizedDeferredRegister;

/** Handles any custom recipes added by Mantle */
public class MantleRecipes {
  private static final SynchronizedDeferredRegister<RecipeSerializer<?>> RECIPES = SynchronizedDeferredRegister.create(Registries.RECIPE_SERIALIZER, Mantle.modId);

  private MantleRecipes() {}

  /**
   * Fabric: the deferred register runs eagerly during class initialization, so being touched
   * from the bootstrap is all the registration needs. Without this call nothing references
   * the class and mantle:crafting_shaped_retextured (15 shipped recipes) never registers.
   */
  public static void init() {}

  // crafting
  public static final RegistryObject<ShapedFallbackRecipe.Serializer> CRAFTING_SHAPED_FALLBACK = RECIPES.register("crafting_shaped_fallback", ShapedFallbackRecipe.Serializer::new);
  public static final RegistryObject<ShapedRetexturedRecipe.Serializer> CRAFTING_SHAPED_RETEXTURED = RECIPES.register("crafting_shaped_retextured", ShapedRetexturedRecipe.Serializer::new);
  // PORT: upstream also registered mantle:smelting/blasting/smoking/campfire result-override
  // cooking recipes; the cooking package was never vendored and no shipped data uses them.
}
