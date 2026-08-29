package slimeknights.mantle.event.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.HitResult;
import slimeknights.mantle.event.Event;

/**
 * Mirror of Forge's {@code ProjectileImpactEvent}: posted by the event bridge's
 * {@code Projectile#onHit} mixin before impact processing. Canceling (or setting a
 * non-default impact result) skips the vanilla impact handling; the bridge treats
 * {@code SKIP_ENTITY} like a cancel, which for piercing arrows means the pierce also stops
 * (a small deviation from Forge noted at the mixin).
 */
public class ProjectileImpactEvent extends Event {
  private final Projectile projectile;
  private final HitResult ray;
  private ImpactResult impactResult = ImpactResult.DEFAULT;

  public ProjectileImpactEvent(Projectile projectile, HitResult ray) {
    this.projectile = projectile;
    this.ray = ray;
  }

  public Projectile getProjectile() {
    return projectile;
  }

  /** Alias matching Forge's entity accessor; the projectile is the event entity */
  public Entity getEntity() {
    return projectile;
  }

  public HitResult getRayTraceResult() {
    return ray;
  }

  public ImpactResult getImpactResult() {
    return impactResult;
  }

  public void setImpactResult(ImpactResult result) {
    this.impactResult = result;
    if (result != ImpactResult.DEFAULT) {
      setCanceled(true);
    }
  }

  @Override
  public boolean isCancelable() {
    return true;
  }

  /** Mirror of Forge's impact result options */
  public enum ImpactResult {
    DEFAULT,
    SKIP_ENTITY,
    STOP_AT_CURRENT,
    STOP_AT_CURRENT_NO_DAMAGE
  }
}
