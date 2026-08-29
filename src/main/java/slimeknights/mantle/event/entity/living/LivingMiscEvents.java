package slimeknights.mantle.event.entity.living;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

/**
 * Mirrors of the smaller Forge living-entity events Tinkers listens to, each posted by the
 * matching event-bridge mixin. Surfaces are reduced to what the handlers consume.
 */
public final class LivingMiscEvents {
  private LivingMiscEvents() {}

  /** Mirror of Forge's {@code LivingVisibilityEvent}: multiplies mob visibility range */
  public static class LivingVisibilityEvent extends LivingEvent {
    @Nullable
    private final Entity lookingEntity;
    private double visibilityModifier;

    public LivingVisibilityEvent(LivingEntity entity, @Nullable Entity lookingEntity, double originalMultiplier) {
      super(entity);
      this.lookingEntity = lookingEntity;
      this.visibilityModifier = originalMultiplier;
    }

    @Nullable
    public Entity getLookingEntity() {
      return lookingEntity;
    }

    /** Multiplies the visibility modifier by the given factor */
    public void modifyVisibility(double factor) {
      visibilityModifier *= factor;
    }

    public double getVisibilityModifier() {
      return visibilityModifier;
    }
  }

  /** Mirror of Forge's {@code LivingExperienceDropEvent} */
  public static class LivingExperienceDropEvent extends LivingEvent {
    @Nullable
    private final Player attackingPlayer;
    private final int originalExperience;
    private int droppedExperience;

    public LivingExperienceDropEvent(LivingEntity entity, @Nullable Player attackingPlayer, int originalExperience) {
      super(entity);
      this.attackingPlayer = attackingPlayer;
      this.originalExperience = originalExperience;
      this.droppedExperience = originalExperience;
    }

    @Nullable
    public Player getAttackingPlayer() {
      return attackingPlayer;
    }

    public int getOriginalExperience() {
      return originalExperience;
    }

    public int getDroppedExperience() {
      return droppedExperience;
    }

    public void setDroppedExperience(int droppedExperience) {
      this.droppedExperience = droppedExperience;
    }

    @Override
    public boolean isCancelable() {
      return true;
    }
  }

  /** Mirror of Forge's {@code LivingGetProjectileEvent}: lets listeners swap ranged ammo */
  public static class LivingGetProjectileEvent extends LivingEvent {
    private final ItemStack projectileWeaponItemStack;
    private ItemStack projectileItemStack;

    public LivingGetProjectileEvent(LivingEntity entity, ItemStack weapon, ItemStack ammo) {
      super(entity);
      this.projectileWeaponItemStack = weapon;
      this.projectileItemStack = ammo;
    }

    public ItemStack getProjectileWeaponItemStack() {
      return projectileWeaponItemStack;
    }

    public ItemStack getProjectileItemStack() {
      return projectileItemStack;
    }

    public void setProjectileItemStack(ItemStack projectileItemStack) {
      this.projectileItemStack = projectileItemStack;
    }
  }

  /** Mirror of Forge's {@code LivingEquipmentChangeEvent} */
  public static class LivingEquipmentChangeEvent extends LivingEvent {
    private final EquipmentSlot slot;
    private final ItemStack from;
    private final ItemStack to;

    public LivingEquipmentChangeEvent(LivingEntity entity, EquipmentSlot slot, ItemStack from, ItemStack to) {
      super(entity);
      this.slot = slot;
      this.from = from;
      this.to = to;
    }

    public EquipmentSlot getSlot() {
      return slot;
    }

    public ItemStack getFrom() {
      return from;
    }

    public ItemStack getTo() {
      return to;
    }
  }

  /** Mirror of Forge's {@code ShieldBlockEvent} */
  public static class ShieldBlockEvent extends LivingEvent {
    private final DamageSource source;
    private final float originalBlocked;
    private float dmgBlocked;
    private boolean shieldTakesDamage = true;

    public ShieldBlockEvent(LivingEntity blocker, DamageSource source, float blocked) {
      super(blocker);
      this.source = source;
      this.originalBlocked = blocked;
      this.dmgBlocked = blocked;
    }

    public DamageSource getDamageSource() {
      return source;
    }

    public float getOriginalBlockedDamage() {
      return originalBlocked;
    }

    public float getBlockedDamage() {
      return dmgBlocked;
    }

    public void setBlockedDamage(float blocked) {
      this.dmgBlocked = Math.min(blocked, originalBlocked);
    }

    public boolean shieldTakesDamage() {
      return shieldTakesDamage;
    }

    public void setShieldTakesDamage(boolean damage) {
      this.shieldTakesDamage = damage;
    }

    @Override
    public boolean isCancelable() {
      return true;
    }
  }
}
