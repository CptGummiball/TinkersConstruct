package slimeknights.mantle.recipe.helper;

import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;

/**
 * The id of the recipe currently being parsed from JSON.
 *
 * <p>1.20 handed every serializer the recipe id; 1.21 keeps it on {@code RecipeHolder} and
 * codecs never see it — but ~1700 shipped recipes have loadables declaring
 * {@code ContextKey.ID} as a required field. A mixin on {@code RecipeManager#fromJson}
 * publishes the id around each parse and {@link LoadableRecipeSerializer} feeds it into the
 * loadable context, restoring the 1.20 contract for the JSON path.
 *
 * <p>Deliberately not wired for the network path: the client's recipe sync decodes through
 * {@code RecipeHolder}'s stream codec where the id arrives before the recipe body — that
 * seam belongs to the client phase.
 */
public final class CurrentRecipeId {
  private static final ThreadLocal<ResourceLocation> CURRENT = new ThreadLocal<>();

  private CurrentRecipeId() {}

  /** Called by the RecipeManager mixin before parsing a recipe */
  public static void set(@Nullable ResourceLocation id) {
    if (id == null) {
      CURRENT.remove();
    } else {
      CURRENT.set(id);
    }
  }

  /** Id of the recipe currently being parsed, null outside recipe loading */
  @Nullable
  public static ResourceLocation get() {
    return CURRENT.get();
  }
}
