package slimeknights.mantle.recipe.condition;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagManager;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Map;

/**
 * The live {@link ICondition.IContext} for the current datapack load.
 *
 * <p>Forge's {@code ReloadableServerResources} patch handed every reload listener a context
 * backed by the tag manager; here a mixin on the same class publishes the tag manager, and
 * the recipe/advancement condition mixins read it. Tag data is queried lazily at condition
 * time, which is safe because vanilla runs {@code TagManager} before {@code RecipeManager}
 * and {@code ServerAdvancementManager} in the reload listener order.
 */
public final class DataConditionContext implements ICondition.IContext {
  /** Context of the reload currently in progress; empty outside a reload */
  private static volatile ICondition.IContext current = ICondition.IContext.EMPTY;

  private final TagManager tagManager;

  private DataConditionContext(TagManager tagManager) {
    this.tagManager = tagManager;
  }

  /** Called by the ReloadableServerResources mixin when a datapack load begins */
  public static void setup(@Nullable TagManager tagManager) {
    current = tagManager == null ? ICondition.IContext.EMPTY : new DataConditionContext(tagManager);
  }

  /** Context for the datapack load currently in progress */
  public static ICondition.IContext current() {
    return current;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> Map<ResourceLocation, Collection<Holder<T>>> getAllTags(ResourceKey<? extends Registry<T>> registry) {
    for (TagManager.LoadResult<?> result : tagManager.getResult()) {
      if (result.key() == registry) {
        return ((TagManager.LoadResult<T>) result).tags();
      }
    }
    return Map.of();
  }
}
