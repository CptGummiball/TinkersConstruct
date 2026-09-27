package slimeknights.tconstruct.fabric.mixin.client;

import net.minecraft.client.player.AbstractClientPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import slimeknights.tconstruct.tools.client.ModifierClientEvents;

/**
 * Applies the zoom modifier to the field of view.
 *
 * <p>Forge had {@code ComputeFovModifierEvent}, which handed the listener both the value vanilla
 * computed and the value as it stood after other listeners. Fabric has no such event; the vanilla
 * return value serves as both, since nothing else has run by the time this fires.
 */
@Mixin(AbstractClientPlayer.class)
public class AbstractClientPlayerFovMixin {
  @Inject(method = "getFieldOfViewModifier", at = @At("RETURN"), cancellable = true)
  private void tconstruct$zoomModifier(CallbackInfoReturnable<Float> callback) {
    float vanilla = callback.getReturnValue();
    float modified = ModifierClientEvents.handleZoom((AbstractClientPlayer)(Object)this, vanilla, vanilla);
    if (modified != vanilla) {
      callback.setReturnValue(modified);
    }
  }
}
