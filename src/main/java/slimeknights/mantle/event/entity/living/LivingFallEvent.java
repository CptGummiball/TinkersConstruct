package slimeknights.mantle.event.entity.living;

import net.minecraft.world.entity.LivingEntity;

/**
 * Mirror of Forge's {@code LivingFallEvent}: posted by the event bridge's fall-damage mixin
 * before fall damage is computed. Distance and damage multiplier are mutable; cancel to
 * skip fall damage entirely.
 */
public class LivingFallEvent extends LivingEvent {
  private float distance;
  private float damageMultiplier;

  public LivingFallEvent(LivingEntity entity, float distance, float damageMultiplier) {
    super(entity);
    this.distance = distance;
    this.damageMultiplier = damageMultiplier;
  }

  public float getDistance() {
    return distance;
  }

  public void setDistance(float distance) {
    this.distance = distance;
  }

  public float getDamageMultiplier() {
    return damageMultiplier;
  }

  public void setDamageMultiplier(float damageMultiplier) {
    this.damageMultiplier = damageMultiplier;
  }

  @Override
  public boolean isCancelable() {
    return true;
  }
}
