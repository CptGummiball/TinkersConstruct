package slimeknights.mantle.event.entity.living;

import net.minecraft.world.entity.LivingEntity;

/**
 * Mirror of Forge's {@code LivingKnockBackEvent}: posted by the event bridge's knockback
 * mixin. Strength and the direction ratios are mutable; cancel to skip the knockback.
 */
public class LivingKnockBackEvent extends LivingEvent {
  private float strength;
  private double ratioX;
  private double ratioZ;

  public LivingKnockBackEvent(LivingEntity entity, float strength, double ratioX, double ratioZ) {
    super(entity);
    this.strength = strength;
    this.ratioX = ratioX;
    this.ratioZ = ratioZ;
  }

  public float getStrength() {
    return strength;
  }

  public void setStrength(float strength) {
    this.strength = strength;
  }

  public double getRatioX() {
    return ratioX;
  }

  public void setRatioX(double ratioX) {
    this.ratioX = ratioX;
  }

  public double getRatioZ() {
    return ratioZ;
  }

  public void setRatioZ(double ratioZ) {
    this.ratioZ = ratioZ;
  }

  @Override
  public boolean isCancelable() {
    return true;
  }
}
