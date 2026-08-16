package slimeknights.mantle.event.entity.living;

import net.minecraft.world.entity.LivingEntity;
import slimeknights.mantle.event.Event;

/**
 * Mirror of Forge's {@code LivingEvent} base class: an event carrying the living entity it
 * concerns. Subclassed by Tinkers' own events (equipment change) and the shimmed Forge
 * families; posted on the shim bus.
 */
public class LivingEvent extends Event {

  private final LivingEntity entity;

  public LivingEvent(LivingEntity entity) {
    this.entity = entity;
  }

  public LivingEntity getEntity() {
    return entity;
  }

  /**
   * Mirror of Forge's {@code LivingEvent.LivingJumpEvent}: fired when a living entity jumps.
   * Posted by the Fabric event bridge (LivingEntity jump hook) once the event layer lands.
   */
  public static class LivingJumpEvent extends LivingEvent {
    public LivingJumpEvent(LivingEntity entity) {
      super(entity);
    }
  }
}
