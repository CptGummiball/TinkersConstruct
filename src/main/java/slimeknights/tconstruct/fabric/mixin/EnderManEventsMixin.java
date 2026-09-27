package slimeknights.tconstruct.fabric.mixin;

import net.minecraft.world.entity.monster.EnderMan;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import slimeknights.mantle.event.MinecraftForge;
import slimeknights.mantle.event.entity.EntityTeleportEvent;

import javax.annotation.Nullable;

/**
 * Event-bridge mixin for {@link EnderMan}: posts EntityTeleportEvent.EnderEntity before an
 * enderman teleport, like Forge. Both the random teleport() and teleportTowards(Entity)
 * funnel into the private teleport(double,double,double), so one seam covers all paths;
 * cancel returns false (no teleport) and listeners may retarget via the setters (the
 * enderference effect listens here to cancel).
 *
 * <p>Verified against the 1.21.1 mapped jar: private boolean teleport(double, double, double);
 * protected boolean teleport() and teleportTowards both end in invokevirtual teleport:(DDD)Z.
 */
@Mixin(EnderMan.class)
public abstract class EnderManEventsMixin {

  /** Teleport event stored between the HEAD inject (which posts) and the coordinate modifiers */
  @Unique @Nullable private EntityTeleportEvent.EnderEntity tconstruct$teleportEvent;

  @Inject(method = "teleport(DDD)Z", at = @At("HEAD"), cancellable = true)
  private void tconstruct$fireTeleport(double x, double y, double z, CallbackInfoReturnable<Boolean> cir) {
    EntityTeleportEvent.EnderEntity event = new EntityTeleportEvent.EnderEntity((EnderMan) (Object) this, x, y, z);
    if (MinecraftForge.EVENT_BUS.post(event)) {
      this.tconstruct$teleportEvent = null;
      cir.setReturnValue(false);
      return;
    }
    this.tconstruct$teleportEvent = event;
  }

  @ModifyVariable(method = "teleport(DDD)Z", at = @At("HEAD"), argsOnly = true, ordinal = 0)
  private double tconstruct$modifyTeleportX(double x) {
    EntityTeleportEvent.EnderEntity event = this.tconstruct$teleportEvent;
    return event != null ? event.getTargetX() : x;
  }

  @ModifyVariable(method = "teleport(DDD)Z", at = @At("HEAD"), argsOnly = true, ordinal = 1)
  private double tconstruct$modifyTeleportY(double y) {
    EntityTeleportEvent.EnderEntity event = this.tconstruct$teleportEvent;
    return event != null ? event.getTargetY() : y;
  }

  @ModifyVariable(method = "teleport(DDD)Z", at = @At("HEAD"), argsOnly = true, ordinal = 2)
  private double tconstruct$modifyTeleportZ(double z) {
    EntityTeleportEvent.EnderEntity event = this.tconstruct$teleportEvent;
    this.tconstruct$teleportEvent = null;
    return event != null ? event.getTargetZ() : z;
  }
}
