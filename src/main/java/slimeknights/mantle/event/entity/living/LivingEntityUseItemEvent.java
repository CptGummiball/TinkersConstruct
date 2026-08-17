package slimeknights.mantle.event.entity.living;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

/**
 * Shim of Forge's item-use event family; only the pieces Tinkers listens to. Posted by the
 * event-layer step (Fabric has no direct equivalent; the finish case hooks item use ticks).
 */
public class LivingEntityUseItemEvent extends LivingEvent {
  private final ItemStack item;

  protected LivingEntityUseItemEvent(LivingEntity entity, ItemStack item) {
    super(entity);
    this.item = item;
  }

  /** Stack being used */
  public ItemStack getItem() {
    return item;
  }

  /** Fired when an entity finishes using an item (eating, drinking) */
  public static class Finish extends LivingEntityUseItemEvent {
    public Finish(LivingEntity entity, ItemStack item) {
      super(entity, item);
    }
  }
}
