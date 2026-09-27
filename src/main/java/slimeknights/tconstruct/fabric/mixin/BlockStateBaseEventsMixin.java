package slimeknights.tconstruct.fabric.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import slimeknights.tconstruct.fabric.events.DigSpeedContext;

/**
 * Event-bridge mixin recording the block position whose destroy progress is being computed,
 * so {@link PlayerEventsMixin}'s BreakSpeed event carries the position Forge's
 * getDigSpeed(state, pos) overload provided — several mining-speed modifiers read it.
 *
 * <p>Verified against the 1.21.1 mapped jar: BlockBehaviour$BlockStateBase has
 * public float getDestroyProgress(Player, BlockGetter, BlockPos), which delegates to
 * Block.getDestroyProgress where player.getDestroySpeed(state) is evaluated.
 */
@Mixin(BlockBehaviour.BlockStateBase.class)
public abstract class BlockStateBaseEventsMixin {

  @Inject(method = "getDestroyProgress(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;)F", at = @At("HEAD"))
  private void tconstruct$pushDigPos(Player player, BlockGetter level, BlockPos pos, CallbackInfoReturnable<Float> cir) {
    DigSpeedContext.setPos(pos);
  }

  @Inject(method = "getDestroyProgress(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;)F", at = @At("RETURN"))
  private void tconstruct$popDigPos(Player player, BlockGetter level, BlockPos pos, CallbackInfoReturnable<Float> cir) {
    DigSpeedContext.clear();
  }
}
