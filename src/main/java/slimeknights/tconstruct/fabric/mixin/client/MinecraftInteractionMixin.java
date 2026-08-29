package slimeknights.tconstruct.fabric.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import slimeknights.tconstruct.tools.client.ClientInteractionHandler;

import javax.annotation.Nullable;

/**
 * Runs Tinkers' interactions for a click that hits nothing.
 *
 * <p>Chestplates and some tools act on a right or left click into empty air. Forge patched both call
 * sites to fire an event; Fabric has no equivalent — {@code ClientPreAttackCallback} cancels an
 * attack outright rather than extending the miss case — so the two calls go in directly, at the same
 * points Forge's patches sat.
 */
@Mixin(Minecraft.class)
public class MinecraftInteractionMixin {
  @Shadow
  @Nullable
  public LocalPlayer player;

  /**
   * Right click into air.
   *
   * <p>Injected after the two guards and the right-click delay, before the hand loop. Cancelling
   * here skips both hands, which is what upstream's {@code cancelNextOffhand} flag emulated: Forge's
   * event could only suppress the hand it fired for.
   */
  @Inject(method = "startUseItem",
          at = @At(value = "INVOKE", target = "Lnet/minecraft/world/InteractionHand;values()[Lnet/minecraft/world/InteractionHand;"),
          cancellable = true)
  private void tconstruct$rightClickEmpty(CallbackInfo callback) {
    Minecraft minecraft = (Minecraft)(Object)this;
    if (player == null || !ClientInteractionHandler.missedEverything(minecraft)) {
      return;
    }
    // Forge fired its event per hand, and only for a hand holding nothing; same order, same condition
    for (InteractionHand hand : InteractionHand.values()) {
      ItemStack held = player.getItemInHand(hand);
      if (held.isEmpty() && ClientInteractionHandler.onRightClickEmpty(player, hand)) {
        callback.cancel();
        return;
      }
    }
  }

  /**
   * Left click into air.
   *
   * <p>Injected at the end of the miss branch, so vanilla has already set the miss time and reset
   * the attack strength; returning false from here only skips the swing, which the interaction does
   * itself when it wants one.
   */
  @Inject(method = "startAttack",
          at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;resetAttackStrengthTicker()V", shift = At.Shift.AFTER),
          cancellable = true)
  private void tconstruct$leftClickEmpty(CallbackInfoReturnable<Boolean> callback) {
    if (player != null && ClientInteractionHandler.onLeftClickEmpty(player)) {
      callback.setReturnValue(false);
    }
  }
}
