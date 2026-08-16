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
}
