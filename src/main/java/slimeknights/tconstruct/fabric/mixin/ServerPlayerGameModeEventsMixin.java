package slimeknights.tconstruct.fabric.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import slimeknights.tconstruct.fabric.events.BlockBreakContext;

/**
 * Event-bridge mixin marking which player is running a server-side block break, so the
 * {@link BlockEventsMixin} popExperience hook can post BlockEvent.BreakEvent with the
 * breaking player (Forge fired the event directly from its patched destroyBlock; the XP is
 * the only part Tinkers still needs from it here — the break itself bridges through
 * Fabric's PlayerBlockBreakEvents elsewhere).
 *
 * <p>Verified against the 1.21.1 mapped jar: public boolean destroyBlock(BlockPos);
 * protected final ServerPlayer player.
 */
@Mixin(ServerPlayerGameMode.class)
public class ServerPlayerGameModeEventsMixin {

  @Shadow @Final protected ServerPlayer player;

  @Inject(method = "destroyBlock(Lnet/minecraft/core/BlockPos;)Z", at = @At("HEAD"))
  private void tconstruct$pushBreakingPlayer(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
    BlockBreakContext.setPlayer(this.player);
  }

  @Inject(method = "destroyBlock(Lnet/minecraft/core/BlockPos;)Z", at = @At("RETURN"))
  private void tconstruct$popBreakingPlayer(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
    BlockBreakContext.clear();
  }
}
