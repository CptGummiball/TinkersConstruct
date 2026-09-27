package slimeknights.mantle.event.level;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import slimeknights.mantle.event.Event;

/**
 * Mirror of the pieces of Forge's {@code BlockEvent} hierarchy Tinkers listens to.
 */
public class BlockEvent extends Event {
  private final LevelAccessor level;
  private final BlockPos pos;
  private final BlockState state;

  protected BlockEvent(LevelAccessor level, BlockPos pos, BlockState state) {
    this.level = level;
    this.pos = pos;
    this.state = state;
  }

  public LevelAccessor getLevel() {
    return level;
  }

  public BlockPos getPos() {
    return pos;
  }

  public BlockState getState() {
    return state;
  }

  /**
   * Mirror of Forge's {@code BlockEvent.BreakEvent}. On Fabric the break itself comes from
   * {@code PlayerBlockBreakEvents}; the experience amount has no Fabric hook, so the bridge
   * scales it through the block-experience mixin using the value set here.
   */
  public static class BreakEvent extends BlockEvent {
    private final Player player;
    private int exp;

    public BreakEvent(LevelAccessor level, BlockPos pos, BlockState state, Player player, int exp) {
      super(level, pos, state);
      this.player = player;
      this.exp = exp;
    }

    public Player getPlayer() {
      return player;
    }

    public int getExpToDrop() {
      return exp;
    }

    public void setExpToDrop(int exp) {
      this.exp = exp;
    }

    @Override
    public boolean isCancelable() {
      return true;
    }
  }
}
