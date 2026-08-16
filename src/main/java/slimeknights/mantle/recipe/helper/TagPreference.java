package slimeknights.mantle.recipe.helper;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Picks one preferred entry out of a tag.
 *
 * <p>When a recipe outputs "a copper ingot", several mods may supply one. Forge resolved this
 * with a config-driven mod priority list refreshed on {@code TagsUpdatedEvent}. Fabric has
 * neither the event nor the config, so preference is decided by a fixed, predictable rule
 * instead:
 *
 * <ol>
 *   <li>{@code minecraft} — vanilla always wins, so recipes yield the item players expect</li>
 *   <li>{@code tconstruct} — our own entry next, so Tinkers recipes stay self-consistent</li>
 *   <li>everything else alphabetically by namespace, which at least makes the choice stable
 *       across launches rather than dependent on mod load order</li>
 * </ol>
 *
 * <p>Determinism is the point: an unstable pick would silently change recipe outputs between
 * launches. No caching is done — tags reload on datapack reload, and these lookups happen at
 * recipe-resolution time rather than per tick.
 */
public class TagPreference {

  private TagPreference() {}

  private static final String VANILLA = "minecraft";
  private static final String TINKERS = "tconstruct";

  private static final Comparator<Holder<?>> PREFERENCE = Comparator
    .comparingInt((Holder<?> holder) -> {
      String namespace = holder.unwrapKey()
        .map(key -> key.location().getNamespace())
        .orElse("");
      if (VANILLA.equals(namespace)) {
        return 0;
      }
      return TINKERS.equals(namespace) ? 1 : 2;
    })
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
