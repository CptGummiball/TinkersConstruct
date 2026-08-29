package slimeknights.mantle.item;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Fabric stand-in for Forge's {@code TierSortingRegistry}: a total order over tool tiers.
 *
 * <p>Forge kept a sortable registry so mods could slot custom tiers between vanilla ones;
 * 1.21 vanilla has no ordering at all. Tinkers needs one for harvest-level display and
 * "tier at least X" checks. Vanilla tiers seed the list in their canonical order and
 * {@link #registerTier} inserts relative to them, which covers how Tinkers used the Forge
 * API (register with before/after hints at bootstrap).
 */
public class TierSortingRegistry {

  private static final List<Tier> SORTED = Collections.synchronizedList(new ArrayList<>(List.of(
    Tiers.WOOD, Tiers.GOLD, Tiers.STONE, Tiers.IRON, Tiers.DIAMOND, Tiers.NETHERITE)));

  /** Names for display/serialization; Forge captured these at registration. */
  private static final java.util.Map<Tier, ResourceLocation> NAMES = Collections.synchronizedMap(new java.util.HashMap<>(java.util.Map.of(
    Tiers.WOOD, ResourceLocation.withDefaultNamespace("wood"),
    Tiers.GOLD, ResourceLocation.withDefaultNamespace("gold"),
    Tiers.STONE, ResourceLocation.withDefaultNamespace("stone"),
    Tiers.IRON, ResourceLocation.withDefaultNamespace("iron"),
    Tiers.DIAMOND, ResourceLocation.withDefaultNamespace("diamond"),
    Tiers.NETHERITE, ResourceLocation.withDefaultNamespace("netherite"))));

  /** Name a tier was registered under, or minecraft:unknown for unregistered tiers. */
  public static ResourceLocation getName(Tier tier) {
    return NAMES.getOrDefault(tier, ResourceLocation.withDefaultNamespace("unknown"));
  }

  /** Looks up a tier by registered name. */
  @javax.annotation.Nullable
  public static Tier byName(ResourceLocation name) {
    for (java.util.Map.Entry<Tier, ResourceLocation> entry : NAMES.entrySet()) {
      if (entry.getValue().equals(name)) {
        return entry.getKey();
      }
    }
    return null;
  }

  /** All tiers in ascending order. */
  public static List<Tier> getSortedTiers() {
    return List.copyOf(SORTED);
  }

  /** Registers a tier, placed directly after the named tiers it should beat. */
  public static Tier registerTier(Tier tier, ResourceLocation name, List<Object> after, List<Object> before) {
    int index = SORTED.size();
    for (Object entry : after) {
      if (entry instanceof Tier other) {
        int i = SORTED.indexOf(other);
        if (i >= 0) {
          index = Math.min(index, i + 1);
        }
      }
    }
    for (Object entry : before) {
      if (entry instanceof Tier other) {
        int i = SORTED.indexOf(other);
        if (i >= 0) {
          index = Math.min(index, i);
        }
      }
    }
    SORTED.add(Math.min(index, SORTED.size()), tier);
    NAMES.put(tier, name);
    return tier;
  }

  /** True when the first tier mines at least as well as the second. */
  /** Forge's state overload: on 1.21 correctness is expressed through the tier's incorrect-blocks tag */
  public static boolean isCorrectTierForDrops(Tier tier, net.minecraft.world.level.block.state.BlockState state) {
    return !state.is(tier.getIncorrectBlocksForDrops());
  }

  public static boolean isCorrectTierForDrops(Tier tier, Tier required) {
    return SORTED.indexOf(tier) >= SORTED.indexOf(required);
  }
}
