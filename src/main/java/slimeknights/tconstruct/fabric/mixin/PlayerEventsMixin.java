package slimeknights.tconstruct.fabric.mixin;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import slimeknights.mantle.event.Event;
import slimeknights.mantle.event.MinecraftForge;
import slimeknights.mantle.event.entity.living.LivingDamageEvents.LivingHurtEvent;
import slimeknights.mantle.event.entity.living.LivingMiscEvents.LivingGetProjectileEvent;
import slimeknights.mantle.event.entity.player.CriticalHitEvent;
import slimeknights.mantle.event.entity.player.PlayerEvent.BreakSpeed;
import slimeknights.tconstruct.fabric.events.DigSpeedContext;

import javax.annotation.Nullable;

/**
 * Event-bridge mixin for the {@link Player}-specific seams: break speed, the critical-hit
 * check inside attack, and the actuallyHurt/getProjectile overrides that fully replace the
 * LivingEntity versions hooked in {@link LivingEntityEventsMixin}.
 *
 * <p>All injection targets verified against the 1.21.1 mapped jar via javap.
 */
@Mixin(Player.class)
public abstract class PlayerEventsMixin {

  /** Hurt event stored between the amount modifier (which posts) and the cancel check */
  @Unique @Nullable private LivingHurtEvent tconstruct$playerHurtEvent;
  /** Critical hit event stored between the crit-flag modifier (which posts) and the damage-modifier constant */
  @Unique @Nullable private CriticalHitEvent tconstruct$critEvent;

  @Unique
  private Player tconstruct$self() {
    return (Player) (Object) this;
  }

  /* (2a) PlayerEvent.BreakSpeed — Forge computed getDigSpeed(state, pos) through the event,
   * with cancel meaning "cannot mine" (negative speed). Vanilla's getDestroySpeed has no
   * position, so the BlockStateBase getDestroyProgress mixin records it in DigSpeedContext;
   * direct getDestroySpeed calls see no position, like Forge's null-pos overload.
   * Verified: public float getDestroySpeed(BlockState), single return. */
  @Inject(method = "getDestroySpeed(Lnet/minecraft/world/level/block/state/BlockState;)F", at = @At("RETURN"), cancellable = true)
  private void tconstruct$fireBreakSpeed(BlockState state, CallbackInfoReturnable<Float> cir) {
    BreakSpeed event = new BreakSpeed(tconstruct$self(), state, cir.getReturnValueF(), DigSpeedContext.getPos());
    if (MinecraftForge.EVENT_BUS.post(event)) {
      cir.setReturnValue(-1.0F);
    } else {
      cir.setReturnValue(event.getNewSpeed());
    }
  }

  /* (2b) CriticalHitEvent — Forge replaced the vanilla crit decision: the event fires with
   * the vanilla verdict and 1.5x/1.0x base modifier; the hit crits when the result is ALLOW,
   * or when vanilla said so and the result is not DENY; the 1.5x multiplier becomes
   * event.getDamageModifier(). Verified in attack(Entity): the merged crit flag has its
   * single store at istore 9 (insn 335) and 1.5f appears exactly once (ldc at insn 343,
   * only loaded when the flag is set). */
  @ModifyVariable(method = "attack(Lnet/minecraft/world/entity/Entity;)V", at = @At("STORE"), index = 9)
  private boolean tconstruct$fireCriticalHit(boolean vanillaCritical, Entity target) {
    CriticalHitEvent event = new CriticalHitEvent(tconstruct$self(), target, vanillaCritical ? 1.5F : 1.0F, vanillaCritical);
    MinecraftForge.EVENT_BUS.post(event);
    this.tconstruct$critEvent = event;
    if (event.getResult() == Event.Result.ALLOW) {
      return true;
    }
    if (event.getResult() == Event.Result.DENY) {
      return false;
    }
    return vanillaCritical;
  }

  @ModifyConstant(method = "attack(Lnet/minecraft/world/entity/Entity;)V", constant = @Constant(floatValue = 1.5F))
  private float tconstruct$applyCritModifier(float constant) {
    CriticalHitEvent event = this.tconstruct$critEvent;
    this.tconstruct$critEvent = null;
    return event != null ? event.getDamageModifier() : constant;
  }

  /* (1f mirror) LivingHurtEvent — Player.actuallyHurt fully replaces the LivingEntity
   * version, so the same two hooks are mirrored here. Verified: protected void
   * actuallyHurt(DamageSource, float); first call inside the non-invulnerable branch is
   * getDamageAfterArmorAbsorb (insn 12, owner Player in this class's constant pool). The
   * LivingDamageEvent stage lives in getDamageAfterMagicAbsorb (not overridden by Player),
   * hooked in LivingEntityEventsMixin. */
  @ModifyVariable(method = "actuallyHurt(Lnet/minecraft/world/damagesource/DamageSource;F)V", at = @At("HEAD"), argsOnly = true, ordinal = 0)
  private float tconstruct$fireHurt(float amount, DamageSource source) {
    Player self = tconstruct$self();
    if (self.isInvulnerableTo(source)) {
      this.tconstruct$playerHurtEvent = null;
      return amount;
    }
    LivingHurtEvent event = new LivingHurtEvent(self, source, amount);
    MinecraftForge.EVENT_BUS.post(event);
    this.tconstruct$playerHurtEvent = event;
    return event.getAmount();
  }

  @Inject(method = "actuallyHurt(Lnet/minecraft/world/damagesource/DamageSource;F)V",
    at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;getDamageAfterArmorAbsorb(Lnet/minecraft/world/damagesource/DamageSource;F)F"),
    cancellable = true)
  private void tconstruct$cancelHurt(DamageSource source, float amount, CallbackInfo ci) {
    LivingHurtEvent event = this.tconstruct$playerHurtEvent;
    this.tconstruct$playerHurtEvent = null;
    if (event != null && (event.isCanceled() || event.getAmount() <= 0)) {
      ci.cancel();
    }
  }

  /* (6 mirror) LivingGetProjectileEvent — Player.getProjectile replaces the LivingEntity
   * version without calling super. Fires at each of the four returns (held projectile,
   * inventory hit, creative arrow, empty). Verified: public ItemStack getProjectile(ItemStack). */
  @Inject(method = "getProjectile(Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/world/item/ItemStack;", at = @At("RETURN"), cancellable = true)
  private void tconstruct$fireGetProjectile(ItemStack weapon, CallbackInfoReturnable<ItemStack> cir) {
    LivingGetProjectileEvent event = new LivingGetProjectileEvent(tconstruct$self(), weapon, cir.getReturnValue());
    MinecraftForge.EVENT_BUS.post(event);
    cir.setReturnValue(event.getProjectileItemStack());
  }
}
