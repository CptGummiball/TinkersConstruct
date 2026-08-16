package slimeknights.mantle.recipe.condition;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Fabric stand-in for Forge's {@code net.minecraftforge.common.crafting.conditions.ICondition}.
 *
 * <p>Recipe and data conditions gate whether a JSON file loads. Fabric API has its own
 * {@code ResourceConditions}, but Tinkers' 12.5k data files already carry Forge-shaped
 * {@code "conditions"} blocks using {@code forge:} type names. Reimplementing the same shape
 * here keeps every one of those files valid — migrating them would be a large, risky rewrite
 * of data that currently works.
 */
public interface ICondition {

  /** Type id, e.g. {@code forge:mod_loaded}. */
  ResourceLocation getID();

  boolean test(IContext context);

  /** What a condition is allowed to ask about the world outside the JSON file. */
  interface IContext {

    /** Context for use before tags exist; any tag query reports empty. */
    IContext TAGS_INVALID = new IContext() {
      @Override
      public <T> Map<ResourceLocation, Collection<Holder<T>>> getAllTags(ResourceKey<? extends Registry<T>> registry) {
        return Map.of();
      }
    };

    <T> Map<ResourceLocation, Collection<Holder<T>>> getAllTags(ResourceKey<? extends Registry<T>> registry);

    default <T> Collection<Holder<T>> getTag(TagKey<T> key) {
      return getAllTags(key.registry()).getOrDefault(key.location(), Set.of());
    }
  }
}
