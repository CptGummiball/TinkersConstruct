package slimeknights.mantle.recipe.helper;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;

import java.util.Comparator;
import java.util.function.Supplier;
import java.util.List;
import java.util.Optional;

/**
 * Picks one preferred entry out of a tag.
 *
 * <p>When a recipe outputs "a copper ingot", several mods may supply one. Forge resolved this
 * with a config-driven mod priority list; this port does the same through the namespace list
 * Tinkers' common config injects here ({@code tagPreferences}). Earlier namespaces win, anything
 * unlisted ranks after every listed one, and ties fall back to the full id alphabetically so the
 * choice stays stable across launches rather than dependent on mod load order.
 *
 * <p>The default is {@code [minecraft, tconstruct]}. A pack running an output unifier (the
 * GummiCraft pack ships {@code unify}) should mirror the unifier's priority order in the config,
 * otherwise the smeltery casts one mod's ingot while unified crafting recipes yield another's.
 *
 * <p>Determinism is the point: an unstable pick would silently change recipe outputs between
 * launches. No caching is done — tags reload on datapack reload, and these lookups happen at
 * recipe-resolution time rather than per tick.
 */
public class TagPreference {

  private TagPreference() {}

  /** Namespace priority; replaced by the config once it loads */
  private static Supplier<List<? extends String>> preferences = () -> List.of("minecraft", "tconstruct");

  /** Injects the config-backed namespace priority list; called once from Tinkers' config setup. */
  public static void setPreferences(Supplier<List<? extends String>> supplier) {
    preferences = supplier;
  }

  /** Rank of a holder's namespace in the priority list; unlisted namespaces rank last */
  private static int namespaceRank(Holder<?> holder) {
    List<? extends String> list = preferences.get();
    String namespace = holder.unwrapKey()
      .map(key -> key.location().getNamespace())
      .orElse("");
    int index = list.indexOf(namespace);
    return index < 0 ? list.size() : index;
  }

  private static final Comparator<Holder<?>> PREFERENCE = Comparator
    .comparingInt(TagPreference::namespaceRank)
    .thenComparing(holder -> holder.unwrapKey()
      .map(key -> key.location())
      .map(ResourceLocation::toString)
      .orElse(""));

  /** Preferred entry of the given tag, or empty when the tag has no entries. */
  public static <T> Optional<T> getPreference(TagKey<T> tag) {
    Registry<T> registry = registryFor(tag);
    if (registry == null) {
      return Optional.empty();
    }
    return registry.getTag(tag)
      .map(named -> named.stream().toList())
      .filter(entries -> !entries.isEmpty())
      .map(entries -> entries.stream().min(PREFERENCE).orElse(entries.get(0)))
      .map(Holder::value);
  }

  /** All entries of the tag in preference order; useful for recipe viewers showing alternatives. */
  public static <T> List<T> getEntries(TagKey<T> tag) {
    Registry<T> registry = registryFor(tag);
    if (registry == null) {
      return List.of();
    }
    return registry.getTag(tag)
      .map(named -> named.stream().sorted(PREFERENCE).map(Holder::value).toList())
      .orElseGet(List::of);
  }

  @SuppressWarnings("unchecked")
  private static <T> Registry<T> registryFor(TagKey<T> tag) {
    return (Registry<T>) BuiltInRegistries.REGISTRY.get(tag.registry().location());
  }
}
