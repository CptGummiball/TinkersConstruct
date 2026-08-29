package slimeknights.tconstruct.world.block;

import lombok.Getter;
import net.minecraft.world.level.block.SlimeBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.BiPredicate;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

/**
 * Slime block with custom piston stickiness rules.
 *
 * <p>Fabric note: Forge's isSlimeBlock/isStickyBlock/canStickTo extensions have no Fabric
 * equivalent without a piston mixin; the predicate is kept for the event-layer step to
 * consume. Until then these behave as regular (fully sticky) slime blocks.
 */
public class StickySlimeBlock extends SlimeBlock {

  @Getter
  private final BiPredicate<BlockState, BlockState> stickyPredicate;
  public StickySlimeBlock(Properties properties, BiPredicate<BlockState, BlockState> stickyPredicate) {
    super(properties);
    this.stickyPredicate = stickyPredicate;
  }
}
