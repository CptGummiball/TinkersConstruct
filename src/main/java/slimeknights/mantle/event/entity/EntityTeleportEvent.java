package slimeknights.mantle.event.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import slimeknights.mantle.event.Cancelable;
import slimeknights.mantle.event.Event;

/**
 * Mirror of Forge's {@code EntityTeleportEvent} on the shim bus.
 *
 * <p>Tinkers derives nine of its own teleport events from this (slimesling, enderporting,
 * ender slime, ...) and both fires and listens to them. Vanilla-initiated teleports (ender
 * pearls, chorus fruit) additionally fire the {@link EnderPearl}/{@link ChorusFruit}
 * variants from bridge hooks wired in the event-layer step.
 */
@Cancelable
public class EntityTeleportEvent extends Event {

  private final Entity entity;
  private final double prevX;
  private final double prevY;
  private final double prevZ;
  private double targetX;
  private double targetY;
  private double targetZ;

  public EntityTeleportEvent(Entity entity, double targetX, double targetY, double targetZ) {
    this.entity = entity;
    this.targetX = targetX;
    this.targetY = targetY;
    this.targetZ = targetZ;
    this.prevX = entity.getX();
    this.prevY = entity.getY();
    this.prevZ = entity.getZ();
  }

  public Entity getEntity() {
    return entity;
  }

  public double getTargetX() {
    return targetX;
  }

  public void setTargetX(double targetX) {
    this.targetX = targetX;
  }

  public double getTargetY() {
    return targetY;
  }

  public void setTargetY(double targetY) {
    this.targetY = targetY;
  }

  public double getTargetZ() {
    return targetZ;
  }

  public void setTargetZ(double targetZ) {
    this.targetZ = targetZ;
  }

  public Vec3 getTarget() {
    return new Vec3(targetX, targetY, targetZ);
  }

  public double getPrevX() {
    return prevX;
  }

  public double getPrevY() {
    return prevY;
  }

  public double getPrevZ() {
    return prevZ;
  }

  /** Teleports initiated by living entities with a target position (endermen, Tinkers gear). */
  public static class EnderEntity extends EntityTeleportEvent {

    private final LivingEntity entityLiving;

    public EnderEntity(LivingEntity entity, double targetX, double targetY, double targetZ) {
      super(entity, targetX, targetY, targetZ);
      this.entityLiving = entity;
    }

    public LivingEntity getEntityLiving() {
      return entityLiving;
    }
  }

  /** Fired for ender pearl throws; carries the pearl-thrower context. */
  public static class EnderPearl extends EnderEntity {

    public EnderPearl(LivingEntity entity, double targetX, double targetY, double targetZ) {
      super(entity, targetX, targetY, targetZ);
    }
  }

  /** Fired for chorus fruit teleports. */
  public static class ChorusFruit extends EnderEntity {

    public ChorusFruit(LivingEntity entity, double targetX, double targetY, double targetZ) {
      super(entity, targetX, targetY, targetZ);
    }
  }
}
