package slimeknights.mantle.event.entity.player;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

/**
 * Mirror of Forge's {@code CriticalHitEvent}: posted by the event bridge's player-attack
 * mixin when the vanilla critical check runs. Result ALLOW forces a critical, DENY forbids
 * it; the damage modifier multiplies attack damage when the hit ends up critical (vanilla
 * uses 1.5).
 */
public class CriticalHitEvent extends PlayerEvent {
  private final Entity target;
  private final boolean vanillaCritical;
  private float damageModifier;

  public CriticalHitEvent(Player player, Entity target, float damageModifier, boolean vanillaCritical) {
    super(player);
    this.target = target;
    this.vanillaCritical = vanillaCritical;
    this.damageModifier = damageModifier;
  }

  public Entity getTarget() {
    return target;
  }

  /** Whether vanilla's own check considered this hit critical */
  public boolean isVanillaCritical() {
    return vanillaCritical;
  }

  public float getDamageModifier() {
    return damageModifier;
  }

  public void setDamageModifier(float damageModifier) {
    this.damageModifier = damageModifier;
  }

  @Override
  public boolean hasResult() {
    return true;
  }
}
