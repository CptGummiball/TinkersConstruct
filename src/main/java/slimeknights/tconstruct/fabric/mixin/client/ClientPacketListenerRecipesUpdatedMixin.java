package slimeknights.tconstruct.fabric.mixin.client;

import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundUpdateRecipesPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import slimeknights.tconstruct.common.recipe.RecipeCacheInvalidator;

/**
 * Replaces Forge's {@code RecipesUpdatedEvent}: whenever the server syncs recipes, the client's
 * recipe caches drop, so lookups rebuilt from the previous world or datapack state cannot leak.
 * The tail only runs on the game thread — the packet handler re-schedules itself off the netty
 * thread before reaching it.
 */
@Mixin(ClientPacketListener.class)
public class ClientPacketListenerRecipesUpdatedMixin {
  @Inject(method = "handleUpdateRecipes", at = @At("TAIL"))
  private void tconstruct$recipesUpdated(ClientboundUpdateRecipesPacket packet, CallbackInfo ci) {
    RecipeCacheInvalidator.reload(true);
  }
}
