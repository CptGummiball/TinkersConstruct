package slimeknights.tconstruct.fabric.mixin.client;

import net.minecraft.client.player.LocalPlayer;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import slimeknights.tconstruct.tools.ToolClientEvents;

/**
 * Lets the use-item speed stat undo vanilla's slowdown.
 *
 * <p>Using an item cuts movement to 20%; the {@code use_item_speed} tool stat and the matching
 * armour stat scale that back up. Forge fired {@code MovementInputUpdateEvent} immediately after the
 * slowdown for exactly this; the injection point here is that same spot, right after vanilla writes
 * the reduced forward impulse.
 */
@Mixin(LocalPlayer.class)
public class LocalPlayerMovementMixin {
  @Inject(method = "aiStep",
          at = @At(value = "FIELD", target = "Lnet/minecraft/client/player/Input;forwardImpulse:F", opcode = Opcodes.PUTFIELD, ordinal = 0, shift = At.Shift.AFTER))
  private void tconstruct$useItemSpeed(CallbackInfo callback) {
    LocalPlayer player = (LocalPlayer)(Object)this;
    ToolClientEvents.applyUseItemSpeed(player, player.input);
  }
}
