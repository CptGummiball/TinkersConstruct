package slimeknights.tconstruct.fabric.mixin;

import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import slimeknights.mantle.event.MinecraftForge;
import slimeknights.mantle.event.entity.ProjectileImpactEvent;

/**
 * Event-bridge mixin for {@link Projectile}: posts ProjectileImpactEvent at the head of
 * onHit like Forge did; cancel skips the impact processing entirely.
 *
 * <p>Deviation: Forge's SKIP_ENTITY result let piercing arrows continue to further targets;
 * our cancel (which the shim also sets for any non-DEFAULT impact result) stops the hit
 * processing entirely. Acceptable for now per the port plan.
 *
 * <p>Verified against the 1.21.1 mapped jar: protected void onHit(HitResult); projectile
 * subclasses that override onHit call super, so this seam covers them.
 */
@Mixin(Projectile.class)
public class ProjectileEventsMixin {

  @Inject(method = "onHit(Lnet/minecraft/world/phys/HitResult;)V", at = @At("HEAD"), cancellable = true)
  private void tconstruct$fireProjectileImpact(HitResult result, CallbackInfo ci) {
    if (MinecraftForge.EVENT_BUS.post(new ProjectileImpactEvent((Projectile) (Object) this, result))) {
      ci.cancel();
    }
  }
}
