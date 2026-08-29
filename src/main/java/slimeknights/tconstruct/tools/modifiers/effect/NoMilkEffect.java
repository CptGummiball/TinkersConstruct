package slimeknights.tconstruct.tools.modifiers.effect;

import net.minecraft.world.effect.MobEffectCategory;
import slimeknights.tconstruct.common.TinkerEffect;

/**
 * Effect that cannot be cured with milk.
 *
 * <p>1.21 removed the curative-items API; milk simply calls {@code removeAllEffects}. The
 * event-layer step adds a {@code LivingEntity} hook that skips effects whose type implements
 * this marker when milk does the clearing.
 * TODO 1.21: move to {@link slimeknights.tconstruct.shared.effect}
 */
public class NoMilkEffect extends TinkerEffect {
  public NoMilkEffect(MobEffectCategory typeIn, int color, boolean show) {
    super(typeIn, color, show);
  }

  /** If false, milk does not remove this effect. Consumed by the milk hook in the event layer. */
  public boolean isCuredByMilk() {
    return false;
  }
}
