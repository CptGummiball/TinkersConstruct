package slimeknights.tconstruct.fabric.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import slimeknights.mantle.event.MinecraftForge;
import slimeknights.mantle.event.level.BlockEvent;
import slimeknights.tconstruct.fabric.events.BlockBreakContext;

/**
 * Event-bridge mixin for block-break XP: when {@code popExperience} runs inside a player's
 * {@code ServerPlayerGameMode#destroyBlock} (marked via {@link BlockBreakContext}), posts
 * BlockEvent.BreakEvent so listeners can scale or cancel the XP, mirroring the XP half of
 * Forge's break event. Without a marked player (furnaces, Tinkers AOE harvest which fires
 * its own break event) nothing fires.
 *
 * <p>Deviation: by popExperience time the block is usually already air, so the event's
 * state is the post-break state; the one listener (experienced modifier) only scales the
 * XP amount and never reads the state. Cancel drops no orbs, matching Forge's canceled
 * break dropping no XP.
 *
 * <p>Verified against the 1.21.1 mapped jar: protected void popExperience(ServerLevel,
 * BlockPos, int) — gamerule check then ExperienceOrb.award with the int argument.
 */
@Mixin(Block.class)
public class BlockEventsMixin {

  /** Break event stored between the HEAD inject (which posts) and the amount modifier; ThreadLocal since blocks are shared singletons */
  @Unique private static final ThreadLocal<BlockEvent.BreakEvent> TCONSTRUCT$BREAK_EVENT = new ThreadLocal<>();

  @Inject(method = "popExperience(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;I)V", at = @At("HEAD"), cancellable = true)
  private void tconstruct$fireBreakXp(ServerLevel level, BlockPos pos, int amount, CallbackInfo ci) {
    ServerPlayer player = BlockBreakContext.getPlayer();
    if (player != null) {
      BlockEvent.BreakEvent event = new BlockEvent.BreakEvent(level, pos, level.getBlockState(pos), player, amount);
      if (MinecraftForge.EVENT_BUS.post(event)) {
        TCONSTRUCT$BREAK_EVENT.remove();
        ci.cancel();
        return;
      }
      TCONSTRUCT$BREAK_EVENT.set(event);
    }
  }

  @ModifyVariable(method = "popExperience(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;I)V", at = @At("HEAD"), argsOnly = true)
  private int tconstruct$applyBreakXp(int amount) {
    BlockEvent.BreakEvent event = TCONSTRUCT$BREAK_EVENT.get();
    TCONSTRUCT$BREAK_EVENT.remove();
    return event != null ? event.getExpToDrop() : amount;
  }
}
