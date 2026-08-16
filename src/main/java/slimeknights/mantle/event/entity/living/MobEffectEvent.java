package slimeknights.mantle.event.entity.living;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import javax.annotation.Nullable;

/**
 * Mirror of Forge's {@code MobEffectEvent} family, reduced to the members Tinkers listens to.
 * Posted by the Fabric event bridge (effect add hook) once the event layer lands.
 */
public class MobEffectEvent extends LivingEvent {

  @Nullable
  private final MobEffectInstance effectInstance;

  protected MobEffectEvent(LivingEntity entity, @Nullable MobEffectInstance effectInstance) {
    super(entity);
    this.effectInstance = effectInstance;
  }

  @Nullable
  public MobEffectInstance getEffectInstance() {
    return effectInstance;
  }

  /** Fired when a new effect instance is added or an existing one is upgraded */
  public static class Added extends MobEffectEvent {
    @Nullable
    private final MobEffectInstance oldEffectInstance;
    @Nullable
    private final Entity source;

    public Added(LivingEntity entity, @Nullable MobEffectInstance oldInstance, MobEffectInstance newInstance, @Nullable Entity source) {
      super(entity, newInstance);
      this.oldEffectInstance = oldInstance;
      this.source = source;
    }

    /** The effect instance that was previously applied, null if the effect is new */
    @Nullable
    public MobEffectInstance getOldEffectInstance() {
      return oldEffectInstance;
    }

    /** Entity that applied the effect, if any */
    @Nullable
    public Entity getSource() {
      return source;
    }

    @Override
    public MobEffectInstance getEffectInstance() {
      MobEffectInstance instance = super.getEffectInstance();
      assert instance != null; // Added always carries the new instance
      return instance;
    }
  }
}
