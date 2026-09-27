package slimeknights.tconstruct.fabric.events;

import net.minecraft.server.level.ServerPlayer;

import javax.annotation.Nullable;

/**
 * Thread-local handoff between the two halves of the block-break XP bridge: the
 * {@code ServerPlayerGameMode#destroyBlock} mixin records which player is breaking a block,
 * and the {@code Block#popExperience} mixin reads it to post
 * {@link slimeknights.mantle.event.level.BlockEvent.BreakEvent} with that player.
 *
 * <p>When no player is set (furnace XP, Tinkers AOE harvest which computes its own break
 * event and calls popExperience directly), the popExperience mixin stays silent, matching
 * Forge where BreakEvent fired once per player break rather than per XP drop.
 */
public final class BlockBreakContext {
  private BlockBreakContext() {}

  private static final ThreadLocal<ServerPlayer> BREAKING_PLAYER = new ThreadLocal<>();

  /** Marks the player currently running {@code ServerPlayerGameMode#destroyBlock} */
  public static void setPlayer(ServerPlayer player) {
    BREAKING_PLAYER.set(player);
  }

  /** Gets the player currently breaking a block, or null outside destroyBlock */
  @Nullable
  public static ServerPlayer getPlayer() {
    return BREAKING_PLAYER.get();
  }

  /** Clears the breaking player at the end of destroyBlock */
  public static void clear() {
    BREAKING_PLAYER.remove();
  }
}
