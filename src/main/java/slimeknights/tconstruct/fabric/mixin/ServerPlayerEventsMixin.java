package slimeknights.tconstruct.fabric.mixin;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import slimeknights.mantle.event.MinecraftForge;
import slimeknights.mantle.event.entity.living.LivingDamageEvents.LivingDeathEvent;

/**
 * Event-bridge mixin for {@link ServerPlayer}: its die override fully replaces
 * LivingEntity.die (no super call), so the LivingDeathEvent seam from
 * {@link LivingEntityEventsMixin} is mirrored here — Forge patched both spots the same way.
 * The soulbound listener relies on player deaths firing this event.
 *
 * <p>Verified against the 1.21.1 mapped jar: public void die(DamageSource) with no
 * invokespecial to a super die anywhere in the body.
 */
@Mixin(ServerPlayer.class)
public class ServerPlayerEventsMixin {

  @Inject(method = "die(Lnet/minecraft/world/damagesource/DamageSource;)V", at = @At("HEAD"), cancellable = true)
  private void tconstruct$fireDeath(DamageSource source, CallbackInfo ci) {
    if (MinecraftForge.EVENT_BUS.post(new LivingDeathEvent((ServerPlayer) (Object) this, source))) {
      ci.cancel();
    }
  }
}
