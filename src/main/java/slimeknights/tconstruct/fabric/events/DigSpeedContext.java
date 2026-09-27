package slimeknights.tconstruct.fabric.events;

import net.minecraft.core.BlockPos;

import javax.annotation.Nullable;

/**
 * Thread-local handoff for the break-speed event bridge. Forge added a BlockPos parameter to
 * the dig-speed calculation ({@code Player#getDigSpeed(BlockState, BlockPos)}); vanilla's
 * {@code Player#getDestroySpeed(BlockState)} has no position, but its caller
 * {@code BlockBehaviour$BlockStateBase#getDestroyProgress} does. The BlockStateBase mixin
 * records the position around that call so the Player mixin can post
 * {@link slimeknights.mantle.event.entity.player.PlayerEvent.BreakSpeed} with it — several
 * Tinkers modifiers (dwarven, temperate, block-light/temperature variables) read the
 * position from the event.
 *
 * <p>Direct {@code getDestroySpeed} calls outside getDestroyProgress see no position, the
 * same as Forge's {@code getDigSpeed(state, null)} overload.
 */
public final class DigSpeedContext {
  private DigSpeedContext() {}

  private static final ThreadLocal<BlockPos> POS = new ThreadLocal<>();

  /** Records the position whose destroy progress is being computed */
  public static void setPos(BlockPos pos) {
    POS.set(pos);
  }

  /** Gets the position being mined, or null when unknown */
  @Nullable
  public static BlockPos getPos() {
    return POS.get();
  }

  /** Clears the recorded position */
  public static void clear() {
    POS.remove();
  }
}
