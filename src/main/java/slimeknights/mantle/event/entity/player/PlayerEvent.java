package slimeknights.mantle.event.entity.player;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import slimeknights.mantle.event.Cancelable;
import slimeknights.mantle.event.Event;

import javax.annotation.Nullable;
import java.util.Optional;

/**
 * Mirror of Forge's {@code PlayerEvent} hierarchy — only the members Tinkers uses.
 *
 * <p>{@link BreakSpeed} is the significant one: 12 files of mining-speed modifier logic are
 * written against it. It fires from a mixin bridge on the vanilla dig-speed calculation,
 * wired in the event-layer step.
 */
public class PlayerEvent extends Event {

  private final Player player;

  public PlayerEvent(Player player) {
    this.player = player;
  }

  public Player getEntity() {
    return player;
  }

  /** Fired when computing a player's mining speed against a block, allowing modification. */
  @Cancelable
  public static class BreakSpeed extends PlayerEvent {

    /** Position marker meaning "no position known"; matches Forge's contract. */
    private static final BlockPos LEGACY_UNKNOWN = new BlockPos(0, -1, 0);

    private final BlockState state;
    private final float originalSpeed;
    private final Optional<BlockPos> pos;
    private float newSpeed;

    public BreakSpeed(Player player, BlockState state, float original, @Nullable BlockPos pos) {
      super(player);
      this.state = state;
      this.originalSpeed = original;
      this.newSpeed = original;
      this.pos = Optional.ofNullable(pos);
    }

    public BlockState getState() {
      return state;
    }

    public float getOriginalSpeed() {
      return originalSpeed;
    }

    public float getNewSpeed() {
      return newSpeed;
    }

    public void setNewSpeed(float newSpeed) {
      this.newSpeed = newSpeed;
    }

    public Optional<BlockPos> getPosition() {
      return pos;
    }
  }

  /** Fired when a player logs out of the server. */
  public static class PlayerLoggedOutEvent extends PlayerEvent {

    public PlayerLoggedOutEvent(Player player) {
      super(player);
    }
  }

  /** Fired when a player logs in to the server. */
  public static class PlayerLoggedInEvent extends PlayerEvent {

    public PlayerLoggedInEvent(Player player) {
      super(player);
    }
  }
}
