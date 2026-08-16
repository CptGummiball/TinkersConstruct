package slimeknights.mantle.event.entity.living;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

import javax.annotation.Nullable;

/**
 * Mirror of Forge's {@code LootingLevelEvent}: fired when a mob dies to determine the looting
 * level applied to its drops. Posted from the loot bridge in the event-layer step; Tinkers'
 * {@code ModifierLootingHandler} listens to substitute the held tool's looting modifiers.
 */
public class LootingLevelEvent extends LivingEvent {

  @Nullable
  private final DamageSource damageSource;
  private int lootingLevel;

  public LootingLevelEvent(LivingEntity entity, @Nullable DamageSource damageSource, int lootingLevel) {
    super(entity);
    this.damageSource = damageSource;
    this.lootingLevel = lootingLevel;
  }

  @Nullable
  public DamageSource getDamageSource() {
    return damageSource;
  }

  public int getLootingLevel() {
    return lootingLevel;
  }

  public void setLootingLevel(int lootingLevel) {
    this.lootingLevel = lootingLevel;
  }
}
