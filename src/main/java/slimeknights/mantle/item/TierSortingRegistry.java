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
    return tier;
  }

  /** True when the first tier mines at least as well as the second. */
  public static boolean isCorrectTierForDrops(Tier tier, Tier required) {
    return SORTED.indexOf(tier) >= SORTED.indexOf(required);
  }
}
