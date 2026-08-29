package slimeknights.mantle.event.entity.living;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

/**
 * Mirrors of Forge's damage-pipeline events, posted by the event bridge's LivingEntity
 * mixins at the same three stages Forge patched:
 *
 * <ul>
 *   <li>{@link LivingAttackEvent} — entry of {@code hurt}, before invulnerability side
 *       effects; cancel to ignore the hit entirely.</li>
 *   <li>{@link LivingHurtEvent} — inside {@code actuallyHurt} before armor/magic
 *       absorption; amount mutable, cancel to deal nothing.</li>
 *   <li>{@link LivingDamageEvent} — after absorption right before health is reduced;
 *       amount mutable, cancel to deal nothing.</li>
 * </ul>
 */
public final class LivingDamageEvents {
  private LivingDamageEvents() {}

  /** Base of the three damage stages: entity + source + a mutable amount */
  public static class DamageStageEvent extends LivingEvent {
    private final DamageSource source;
    private float amount;

    protected DamageStageEvent(LivingEntity entity, DamageSource source, float amount) {
      super(entity);
      this.source = source;
      this.amount = amount;
    }

    public DamageSource getSource() {
      return source;
    }

    public float getAmount() {
      return amount;
    }

    public void setAmount(float amount) {
      this.amount = amount;
    }

    @Override
    public boolean isCancelable() {
      return true;
    }
  }

  /** Mirror of Forge's {@code LivingAttackEvent} */
  public static class LivingAttackEvent extends DamageStageEvent {
    public LivingAttackEvent(LivingEntity entity, DamageSource source, float amount) {
      super(entity, source, amount);
    }
  }

  /** Mirror of Forge's {@code LivingHurtEvent} */
  public static class LivingHurtEvent extends DamageStageEvent {
    public LivingHurtEvent(LivingEntity entity, DamageSource source, float amount) {
      super(entity, source, amount);
    }
  }

  /** Mirror of Forge's {@code LivingDamageEvent} */
  public static class LivingDamageEvent extends DamageStageEvent {
    public LivingDamageEvent(LivingEntity entity, DamageSource source, float amount) {
      super(entity, source, amount);
    }
  }

  /** Mirror of Forge's {@code LivingDeathEvent}: posted at the head of {@code die} */
  public static class LivingDeathEvent extends LivingEvent {
    private final DamageSource source;

    public LivingDeathEvent(LivingEntity entity, DamageSource source) {
      super(entity);
      this.source = source;
    }

    public DamageSource getSource() {
      return source;
    }

    @Override
    public boolean isCancelable() {
      return true;
    }
  }
}
