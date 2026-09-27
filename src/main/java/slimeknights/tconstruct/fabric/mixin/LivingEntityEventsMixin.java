package slimeknights.tconstruct.fabric.mixin;

import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import slimeknights.mantle.event.Event;
import slimeknights.mantle.event.MinecraftForge;
import slimeknights.mantle.event.entity.living.LivingDamageEvents.LivingAttackEvent;
import slimeknights.mantle.event.entity.living.LivingDamageEvents.LivingDamageEvent;
import slimeknights.mantle.event.entity.living.LivingDamageEvents.LivingDeathEvent;
import slimeknights.mantle.event.entity.living.LivingDamageEvents.LivingHurtEvent;
import slimeknights.mantle.event.entity.living.LivingDropsEvent;
import slimeknights.mantle.event.entity.living.LivingEntityUseItemEvent;
import slimeknights.mantle.event.entity.living.LivingEvent.LivingJumpEvent;
import slimeknights.mantle.event.entity.living.LivingEvent.LivingTickEvent;
import slimeknights.mantle.event.entity.living.LivingFallEvent;
import slimeknights.mantle.event.entity.living.LivingKnockBackEvent;
import slimeknights.mantle.event.entity.living.LivingMiscEvents.LivingEquipmentChangeEvent;
import slimeknights.mantle.event.entity.living.LivingMiscEvents.LivingExperienceDropEvent;
import slimeknights.mantle.event.entity.living.LivingMiscEvents.LivingGetProjectileEvent;
import slimeknights.mantle.event.entity.living.LivingMiscEvents.LivingVisibilityEvent;
import slimeknights.mantle.event.entity.living.LivingMiscEvents.ShieldBlockEvent;
import slimeknights.mantle.event.entity.living.MobEffectEvent;
import slimeknights.tconstruct.fabric.events.DeathDropsTracker;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

/**
 * Event-bridge mixin posting the shimmed Forge living-entity events at the same seams the
 * Forge patches used. One mixin per vanilla class: everything on {@link LivingEntity} lives
 * here; {@code Player}/{@code ServerPlayer} overrides that replace (rather than call into)
 * these methods get mirrored hooks in their own mixins.
 *
 * <p>All injection targets verified against the 1.21.1 mapped jar via javap; see the porting
 * notes per injector for any semantic deviation from Forge.
 */
@Mixin(LivingEntity.class)
public abstract class LivingEntityEventsMixin {

  @Shadow protected ItemStack useItem;
  @Shadow @Nullable protected Player lastHurtByPlayer;
  @Shadow @Final private Map<Holder<MobEffect>, MobEffectInstance> activeEffects;
  @Shadow private ItemStack lastBodyItemStack;

  @Shadow protected abstract void hurtCurrentlyUsedShield(float damageAmount);

  /** Fall event stored between the HEAD inject and the two argument modifiers */
  @Unique @Nullable private LivingFallEvent tconstruct$fallEvent;
  /** Knockback event stored between the strength modifier (which posts) and the ratio modifiers */
  @Unique @Nullable private LivingKnockBackEvent tconstruct$knockbackEvent;
  /** Hurt event stored between the amount modifier (which posts) and the cancel check */
  @Unique @Nullable private LivingHurtEvent tconstruct$hurtEvent;
  /** Shield event stored between the block-check redirect (which posts) and the shield-damage/remainder hooks */
  @Unique @Nullable private ShieldBlockEvent tconstruct$shieldEvent;

  @Unique
  private LivingEntity tconstruct$self() {
    return (LivingEntity) (Object) this;
  }

  /* (1a) LivingJumpEvent — Forge fired at the end of jumpFromGround. Verified: public void jumpFromGround() */
  @Inject(method = "jumpFromGround()V", at = @At("TAIL"))
  private void tconstruct$fireJump(CallbackInfo ci) {
    MinecraftForge.EVENT_BUS.post(new LivingJumpEvent(tconstruct$self()));
  }

  /* (1b) LivingTickEvent — Forge fired at the head of tick(); our shim event is not
   * cancelable, so a plain post matches the only supported use. Verified: public void tick() */
  @Inject(method = "tick()V", at = @At("HEAD"))
  private void tconstruct$fireTick(CallbackInfo ci) {
    MinecraftForge.EVENT_BUS.post(new LivingTickEvent(tconstruct$self()));
  }

  /* (1c) LivingFallEvent — Forge fired at the head of causeFallDamage with mutable
   * distance/multiplier and cancel returning false. Declaration order matters: the HEAD
   * inject posts before the HEAD variable modifiers read the stored event.
   * Verified: public boolean causeFallDamage(float, float, DamageSource) */
  @Inject(method = "causeFallDamage(FFLnet/minecraft/world/damagesource/DamageSource;)Z", at = @At("HEAD"), cancellable = true)
  private void tconstruct$fireFall(float distance, float multiplier, DamageSource source, CallbackInfoReturnable<Boolean> cir) {
    LivingFallEvent event = new LivingFallEvent(tconstruct$self(), distance, multiplier);
    if (MinecraftForge.EVENT_BUS.post(event)) {
      this.tconstruct$fallEvent = null;
      cir.setReturnValue(false);
      return;
    }
    this.tconstruct$fallEvent = event;
  }

  @ModifyVariable(method = "causeFallDamage(FFLnet/minecraft/world/damagesource/DamageSource;)Z", at = @At("HEAD"), argsOnly = true, ordinal = 0)
  private float tconstruct$modifyFallDistance(float distance) {
    LivingFallEvent event = this.tconstruct$fallEvent;
    return event != null ? event.getDistance() : distance;
  }

  @ModifyVariable(method = "causeFallDamage(FFLnet/minecraft/world/damagesource/DamageSource;)Z", at = @At("HEAD"), argsOnly = true, ordinal = 1)
  private float tconstruct$modifyFallMultiplier(float multiplier) {
    LivingFallEvent event = this.tconstruct$fallEvent;
    this.tconstruct$fallEvent = null;
    return event != null ? event.getDamageMultiplier() : multiplier;
  }

  /* (1d) LivingKnockBackEvent — Forge fired at the head of knockback before the resistance
   * scaling. The strength modifier posts the event; returning 0 on cancel makes vanilla's
   * own (strength <= 0) check skip the knockback entirely, which is exactly Forge's cancel.
   * Verified: public void knockback(double, double, double) — resistance scaling happens
   * first, then the <= 0 early return. */
  @ModifyVariable(method = "knockback(DDD)V", at = @At("HEAD"), argsOnly = true, ordinal = 0)
  private double tconstruct$fireKnockback(double value, double strength, double ratioX, double ratioZ) {
    LivingKnockBackEvent event = new LivingKnockBackEvent(tconstruct$self(), (float) value, ratioX, ratioZ);
    if (MinecraftForge.EVENT_BUS.post(event)) {
      this.tconstruct$knockbackEvent = null;
      return 0.0D;
    }
    this.tconstruct$knockbackEvent = event;
    return event.getStrength();
  }

  @ModifyVariable(method = "knockback(DDD)V", at = @At("HEAD"), argsOnly = true, ordinal = 1)
  private double tconstruct$modifyKnockbackX(double ratioX) {
    LivingKnockBackEvent event = this.tconstruct$knockbackEvent;
    return event != null ? event.getRatioX() : ratioX;
  }

  @ModifyVariable(method = "knockback(DDD)V", at = @At("HEAD"), argsOnly = true, ordinal = 2)
  private double tconstruct$modifyKnockbackZ(double ratioZ) {
    LivingKnockBackEvent event = this.tconstruct$knockbackEvent;
    this.tconstruct$knockbackEvent = null;
    return event != null ? event.getRatioZ() : ratioZ;
  }

  /* (1e) LivingAttackEvent — Forge fired before hurt's invulnerability handling and only
   * server-side. Deviation (per port plan): we additionally skip when already invulnerable
   * or dead, mirroring the vanilla early returns instead of firing ahead of them.
   * Verified: public boolean hurt(DamageSource, float); early returns are
   * isInvulnerableTo -> level().isClientSide -> isDeadOrDying. */
  @Inject(method = "hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z", at = @At("HEAD"), cancellable = true)
  private void tconstruct$fireAttack(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
    LivingEntity self = tconstruct$self();
    if (!self.level().isClientSide && !self.isInvulnerableTo(source) && !self.isDeadOrDying()
        && MinecraftForge.EVENT_BUS.post(new LivingAttackEvent(self, source, amount))) {
      cir.setReturnValue(false);
    }
  }

  /* (1o) ShieldBlockEvent — Forge fired inside hurt where isDamageSourceBlocked passes.
   * The redirect posts the event: cancel reports "not blocked" so vanilla skips the whole
   * shield branch (full damage, no block animation — Forge's cancel semantics). Verified:
   * hurt calls isDamageSourceBlocked(DamageSource) at insn 95 guarded by amount > 0. */
  @Redirect(method = "hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z",
    at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;isDamageSourceBlocked(Lnet/minecraft/world/damagesource/DamageSource;)Z"))
  private boolean tconstruct$fireShieldBlock(LivingEntity self, DamageSource checkedSource, DamageSource source, float amount) {
    if (!self.isDamageSourceBlocked(checkedSource)) {
      return false;
    }
    ShieldBlockEvent event = new ShieldBlockEvent(self, checkedSource, amount);
    if (MinecraftForge.EVENT_BUS.post(event)) {
      this.tconstruct$shieldEvent = null;
      return false;
    }
    this.tconstruct$shieldEvent = event;
    return true;
  }

  /* shieldTakesDamage() false skips vanilla shield durability, like Forge's patched
   * "if (ev.shieldTakesDamage()) this.hurtCurrentlyUsedShield(amount)". Verified: hurt
   * calls hurtCurrentlyUsedShield(F) at insn 103, first statement of the blocked branch. */
  @Redirect(method = "hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z",
    at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;hurtCurrentlyUsedShield(F)V"))
  private void tconstruct$maybeHurtShield(LivingEntity self, float amount) {
    ShieldBlockEvent event = this.tconstruct$shieldEvent;
    if (event == null || event.shieldTakesDamage()) {
      this.hurtCurrentlyUsedShield(amount);
    }
  }

  /* Vanilla zeroes the damage after a block ("amount = 0.0F", the first store to the amount
   * argument); we instead let the un-blocked remainder through so a reduced
   * setBlockedDamage() means partial damage, like Forge's "amount -= ev.getBlockedDamage()".
   * Deviations: the f1 local fed to advancement criteria keeps the full amount, and the
   * "blocked" flag stays true for partially blocked hits (block animation plays either way).
   * Verified: fstore_2 at insn 110 is the only store to the float arg inside the shield
   * branch; later stores (freezing x5, helmet x0.75) come after. */
  @ModifyVariable(method = "hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z", at = @At(value = "STORE", ordinal = 0), argsOnly = true)
  private float tconstruct$applyShieldRemainder(float zeroed) {
    ShieldBlockEvent event = this.tconstruct$shieldEvent;
    this.tconstruct$shieldEvent = null;
    if (event != null) {
      return Math.max(0.0F, event.getOriginalBlockedDamage() - event.getBlockedDamage());
    }
    return zeroed;
  }

  /* (1f) LivingHurtEvent — Forge fired at the head of actuallyHurt's non-invulnerable
   * branch with a mutable amount; cancel (or amount <= 0) skipped the method. The HEAD
   * variable modifier posts and applies the amount; the cancel check sits on the first
   * armor-absorb call, before any side effect. Player overrides actuallyHurt without
   * calling super, so PlayerEventsMixin mirrors both hooks.
   * Verified: protected void actuallyHurt(DamageSource, float); first call inside the
   * branch is getDamageAfterArmorAbsorb(DamageSource,F)F (insn 12, owner LivingEntity). */
  @ModifyVariable(method = "actuallyHurt(Lnet/minecraft/world/damagesource/DamageSource;F)V", at = @At("HEAD"), argsOnly = true, ordinal = 0)
  private float tconstruct$fireHurt(float amount, DamageSource source) {
    LivingEntity self = tconstruct$self();
    if (self.isInvulnerableTo(source)) {
      this.tconstruct$hurtEvent = null;
      return amount;
    }
    LivingHurtEvent event = new LivingHurtEvent(self, source, amount);
    MinecraftForge.EVENT_BUS.post(event);
    this.tconstruct$hurtEvent = event;
    return event.getAmount();
  }

  @Inject(method = "actuallyHurt(Lnet/minecraft/world/damagesource/DamageSource;F)V",
    at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getDamageAfterArmorAbsorb(Lnet/minecraft/world/damagesource/DamageSource;F)F"),
    cancellable = true)
  private void tconstruct$cancelHurt(DamageSource source, float amount, CallbackInfo ci) {
    LivingHurtEvent event = this.tconstruct$hurtEvent;
    this.tconstruct$hurtEvent = null;
    if (event != null && (event.isCanceled() || event.getAmount() <= 0)) {
      ci.cancel();
    }
  }

  /* (1f, second stage) LivingDamageEvent — accepted fallback from the port plan: posted at
   * the return of getDamageAfterMagicAbsorb rather than right before the health reduction.
   * Fires once per damage application after armor + magic reductions but BEFORE absorption
   * is consumed (Forge fired after absorption); cancel returns 0 so the hit deals nothing
   * and, unlike Forge, also leaves absorption hearts intact. Witch overrides this method
   * calling super first, so witches see the pre-witch-reduction amount. Covers both
   * LivingEntity.actuallyHurt and Player.actuallyHurt since neither overrides this method.
   * Verified: protected float getDamageAfterMagicAbsorb(DamageSource, float) */
  @Inject(method = "getDamageAfterMagicAbsorb(Lnet/minecraft/world/damagesource/DamageSource;F)F", at = @At("RETURN"), cancellable = true)
  private void tconstruct$fireDamage(DamageSource source, float amount, CallbackInfoReturnable<Float> cir) {
    LivingDamageEvent event = new LivingDamageEvent(tconstruct$self(), source, cir.getReturnValueF());
    if (MinecraftForge.EVENT_BUS.post(event)) {
      cir.setReturnValue(0.0F);
    } else {
      cir.setReturnValue(event.getAmount());
    }
  }

  /* (1g) LivingDeathEvent — Forge fired at the head of die; cancel skips the death.
   * ServerPlayer.die fully replaces this method, so ServerPlayerEventsMixin mirrors it
   * (Player.die calls super and is covered here). Verified: public void die(DamageSource) */
  @Inject(method = "die(Lnet/minecraft/world/damagesource/DamageSource;)V", at = @At("HEAD"), cancellable = true)
  private void tconstruct$fireDeath(DamageSource source, CallbackInfo ci) {
    if (MinecraftForge.EVENT_BUS.post(new LivingDeathEvent(tconstruct$self(), source))) {
      ci.cancel();
    }
  }

  /* (1h) LivingDropsEvent — drops spawned during dropAllDeathLoot are recorded (already in
   * the world, see DeathDropsTracker for the deviation note) and the event posts on return.
   * Player inventory drops go through Player#drop rather than spawnAtLocation and are not
   * recorded; both current listeners only add drops and never read the list, matching
   * Forge-time behavior. Verified: protected void dropAllDeathLoot(ServerLevel, DamageSource),
   * single return; called by LivingEntity.die and ServerPlayer.die. */
  @Inject(method = "dropAllDeathLoot(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;)V", at = @At("HEAD"))
  private void tconstruct$startDropCapture(ServerLevel level, DamageSource source, CallbackInfo ci) {
    DeathDropsTracker.begin(tconstruct$self());
  }

  @Inject(method = "dropAllDeathLoot(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;)V", at = @At("RETURN"))
  private void tconstruct$fireDrops(ServerLevel level, DamageSource source, CallbackInfo ci) {
    List<ItemEntity> drops = DeathDropsTracker.end(tconstruct$self());
    if (drops != null) {
      MinecraftForge.EVENT_BUS.post(new LivingDropsEvent(tconstruct$self(), source, drops));
    }
  }

  /* (1i) LivingExperienceDropEvent — Forge wrapped getExperienceReward inside
   * dropExperience with the last-hurting player, awarding 0 on cancel. Verified:
   * protected void dropExperience(Entity) calls
   * getExperienceReward(ServerLevel,Entity)I (final, insn 69) feeding ExperienceOrb.award. */
  @Redirect(method = "dropExperience(Lnet/minecraft/world/entity/Entity;)V",
    at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getExperienceReward(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/Entity;)I"))
  private int tconstruct$fireExperienceDrop(LivingEntity self, ServerLevel level, @Nullable Entity killer) {
    int reward = self.getExperienceReward(level, killer);
    LivingExperienceDropEvent event = new LivingExperienceDropEvent(self, this.lastHurtByPlayer, reward);
    if (MinecraftForge.EVENT_BUS.post(event)) {
      return 0;
    }
    return event.getDroppedExperience();
  }

  /* (1j) LivingVisibilityEvent — Forge wrapped the return value, clamping at 0.
   * Verified: public double getVisibilityPercent(Entity), single return. */
  @Inject(method = "getVisibilityPercent(Lnet/minecraft/world/entity/Entity;)D", at = @At("RETURN"), cancellable = true)
  private void tconstruct$fireVisibility(@Nullable Entity lookingEntity, CallbackInfoReturnable<Double> cir) {
    LivingVisibilityEvent event = new LivingVisibilityEvent(tconstruct$self(), lookingEntity, cir.getReturnValueD());
    MinecraftForge.EVENT_BUS.post(event);
    cir.setReturnValue(Math.max(event.getVisibilityModifier(), 0.0D));
  }

  /* (1k) MobEffectEvent.Applicable — Forge fired at the head of canBeAffected: DENY means
   * immune, ALLOW bypasses vanilla immunities, DEFAULT falls through to vanilla.
   * Verified: public boolean canBeAffected(MobEffectInstance) */
  @Inject(method = "canBeAffected(Lnet/minecraft/world/effect/MobEffectInstance;)Z", at = @At("HEAD"), cancellable = true)
  private void tconstruct$fireApplicable(MobEffectInstance instance, CallbackInfoReturnable<Boolean> cir) {
    MobEffectEvent.Applicable event = new MobEffectEvent.Applicable(tconstruct$self(), instance);
    MinecraftForge.EVENT_BUS.post(event);
    if (event.getResult() != Event.Result.DEFAULT) {
      cir.setReturnValue(event.getResult() == Event.Result.ALLOW);
    }
  }

  /* (1l) MobEffectEvent.Added — Forge fired in addEffect right after the existing-instance
   * lookup, before the map put/update, covering both the new-effect and upgrade paths with
   * the pre-update old instance; injecting at the Map.get call (single occurrence) and
   * re-reading the map reproduces that exactly, so duration edits by listeners still apply.
   * Verified: public boolean addEffect(MobEffectInstance, Entity); activeEffects.get at
   * insn 18 is the only Map.get in the method. */
  @Inject(method = "addEffect(Lnet/minecraft/world/effect/MobEffectInstance;Lnet/minecraft/world/entity/Entity;)Z",
    at = @At(value = "INVOKE", target = "Ljava/util/Map;get(Ljava/lang/Object;)Ljava/lang/Object;"))
  private void tconstruct$fireEffectAdded(MobEffectInstance instance, @Nullable Entity source, CallbackInfoReturnable<Boolean> cir) {
    MobEffectInstance old = this.activeEffects.get(instance.getEffect());
    MinecraftForge.EVENT_BUS.post(new MobEffectEvent.Added(tconstruct$self(), old, instance, source));
  }

  /* (1m) LivingEntityUseItemEvent.Finish — Forge fired around ItemStack.finishUsingItem
   * with the pre-finish stack copy as getItem(); our read-only shim posts just before that
   * call with the same copy (the one listener, strong bones milk drinking, only reads it).
   * Verified: completeUsingItem() invokes
   * ItemStack.finishUsingItem(Level,LivingEntity) at insn 79 on the success path only. */
  @Inject(method = "completeUsingItem()V",
    at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;finishUsingItem(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;)Lnet/minecraft/world/item/ItemStack;"))
  private void tconstruct$fireUseItemFinish(CallbackInfo ci) {
    MinecraftForge.EVENT_BUS.post(new LivingEntityUseItemEvent.Finish(tconstruct$self(), this.useItem.copy()));
  }

  /* (1n) LivingEquipmentChangeEvent — Forge fired per changed slot inside
   * collectEquipmentChanges; the change map handed to handleEquipmentChanges contains
   * exactly those slots with their new stacks, and the last-known stacks are not updated
   * until the lambda inside runs, so posting at HEAD sees the same from/to pairs.
   * Server-side only, like Forge. getLastHandItem/getLastArmorItem are access-widened.
   * Verified: private void handleEquipmentChanges(Map); last-item updates happen in the
   * forEach lambda after this point. */
  @Inject(method = "handleEquipmentChanges(Ljava/util/Map;)V", at = @At("HEAD"))
  private void tconstruct$fireEquipmentChange(Map<EquipmentSlot, ItemStack> changes, CallbackInfo ci) {
    LivingEntity self = tconstruct$self();
    for (Map.Entry<EquipmentSlot, ItemStack> entry : changes.entrySet()) {
      EquipmentSlot slot = entry.getKey();
      ItemStack from = switch (slot.getType()) {
        case HAND -> self.getLastHandItem(slot);
        case HUMANOID_ARMOR -> self.getLastArmorItem(slot);
        case ANIMAL_ARMOR -> this.lastBodyItemStack;
      };
      MinecraftForge.EVENT_BUS.post(new LivingEquipmentChangeEvent(self, slot, from, entry.getValue()));
    }
  }

  /* (6) LivingGetProjectileEvent — Forge wrapped the projectile lookup result. Player
   * overrides getProjectile without calling super, so PlayerEventsMixin mirrors this.
   * Verified: public ItemStack getProjectile(ItemStack) (base returns ItemStack.EMPTY;
   * no Mob/AbstractSkeleton overrides in 1.21.1). */
  @Inject(method = "getProjectile(Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/world/item/ItemStack;", at = @At("RETURN"), cancellable = true)
  private void tconstruct$fireGetProjectile(ItemStack weapon, CallbackInfoReturnable<ItemStack> cir) {
    LivingGetProjectileEvent event = new LivingGetProjectileEvent(tconstruct$self(), weapon, cir.getReturnValue());
    MinecraftForge.EVENT_BUS.post(event);
    cir.setReturnValue(event.getProjectileItemStack());
  }
}
