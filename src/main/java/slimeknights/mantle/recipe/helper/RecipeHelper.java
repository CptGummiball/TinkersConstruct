package slimeknights.mantle.recipe.helper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import slimeknights.mantle.Mantle;
import slimeknights.mantle.recipe.IMultiRecipe;

import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Helpers for fetching recipes from the manager.
 *
 * <p>1.21 reshape: inventories are {@link RecipeInput} rather than {@code Container}, and
 * {@code RecipeManager.byType} yields {@link RecipeHolder}s — the recipe no longer knows its
 * own id, the holder carries it. Sorting and error reporting therefore work on holders, and
 * the JEI/EMI entry point takes a holder stream.
 */
@SuppressWarnings({"WeakerAccess", "unused"})
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RecipeHelper {

  /* Recipe manager utils */

  /** Gets all recipes of the type that are instances of the given class. */
  public static <I extends RecipeInput, T extends Recipe<I>, C extends T> List<C> getRecipes(RecipeManager manager, RecipeType<T> type, Class<C> clazz) {
    return manager.getAllRecipesFor(type).stream()
                  .map(RecipeHolder::value)
                  .filter(clazz::isInstance)
                  .map(clazz::cast)
                  .collect(Collectors.toList());
  }

  /**
   * Gets recipes for display in a UI list, sorted by recipe id so the order matches on both
   * sides of a connection, and filtered by class and predicate.
   */
  public static <I extends RecipeInput, T extends Recipe<I>, C extends T> List<C> getUIRecipes(RecipeManager manager, RecipeType<T> type, Class<C> clazz, Predicate<? super C> filter) {
    return manager.getAllRecipesFor(type).stream()
                  .filter(holder -> clazz.isInstance(holder.value()) && filter.test(clazz.cast(holder.value())))
                  .sorted(Comparator.comparing(RecipeHolder::id))
                  .map(holder -> clazz.cast(holder.value()))
                  .collect(Collectors.toList());
  }

  /**
   * Gets all recipes from the given holders, expanding multi recipes, for display in
   * JEI/EMI. Multi recipes sort after plain ones, then by id, so listings stay stable.
   */
  public static <C> List<C> getJEIRecipes(RegistryAccess access, Stream<? extends RecipeHolder<?>> recipes, Class<C> clazz) {
    return recipes
        .sorted((h1, h2) -> {
          // if one is multi, and the other not, the multi recipe is larger
          boolean m1 = h1.value() instanceof IMultiRecipe<?>;
          boolean m2 = h2.value() instanceof IMultiRecipe<?>;
          if (m1 && !m2) return 1;
          if (!m1 && m2) return -1;
          // fall back to recipe ID
          return h1.id().compareTo(h2.id());
        })
        .flatMap(holder -> {
          // if its a multi recipe, extract child recipes and stream those
          if (holder.value() instanceof IMultiRecipe<?> multi) {
            // most multi registries iterate some external registry to list their contents;
            // sometimes people register broken objects, so avoid breaking the whole plugin
            try {
              return multi.getRecipes(access).stream();
            } catch (Exception e) {
              Mantle.logger.error("Failed to fetch JEI recipes for multi recipe {} ({})", holder.id(), holder.value(), e);
              return Stream.empty();
            }
          }
          return Stream.of(holder.value());
        })
        .filter(clazz::isInstance)
        .map(clazz::cast)
        .collect(Collectors.toList());
  }

  /** Gets all recipes of a type from the manager, expanding multi recipes, for JEI/EMI. */
  public static <I extends RecipeInput, T extends Recipe<I>, C> List<C> getJEIRecipes(RegistryAccess access, RecipeManager manager, RecipeType<T> type, Class<C> clazz) {
    return getJEIRecipes(access, manager.getAllRecipesFor(type).stream(), clazz);
  }
}
