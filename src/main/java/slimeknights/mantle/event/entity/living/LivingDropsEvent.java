package slimeknights.mantle.event.entity.living;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;

import java.util.Collection;

/**
 * Shim of Forge's living-drops event; posted by the event-layer step from the death loot
 * hook so listeners can add or remove drops.
 */
public class LivingDropsEvent extends LivingEvent {
  private final DamageSource source;
  private final Collection<ItemEntity> drops;

  public LivingDropsEvent(LivingEntity entity, DamageSource source, Collection<ItemEntity> drops) {
    super(entity);
    this.source = source;
    this.drops = drops;
  }

  /** Damage source that killed the entity */
  public DamageSource getSource() {
    return source;
  }

  /** Mutable drop list */
  public Collection<ItemEntity> getDrops() {
    return drops;
  }
}
