package slimeknights.mantle.block;

import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import slimeknights.mantle.item.ToolAction;
import slimeknights.mantle.item.ToolActions;

import javax.annotation.Nullable;
import java.util.function.Supplier;

/** Log block that can be stripped */
public class StrippableLogBlock extends RotatedPillarBlock {
  private final Supplier<? extends Block> stripped;
  public StrippableLogBlock(Supplier<? extends Block> stripped, Properties properties) {
    super(properties);
    this.stripped = stripped;
  }

  /**
   * Forge hook kept as plain API: Tinkers' AXE_STRIP modifier logic calls it directly, and
   * vanilla axes reach it through Fabric's StrippableBlockRegistry, registered alongside
   * the blocks in phase 4.
   */
  @Nullable
  public BlockState getToolModifiedState(BlockState state, UseOnContext context, ToolAction toolAction, boolean simulate) {
    if (toolAction == ToolActions.AXE_STRIP) {
      return stripped.get().defaultBlockState().setValue(AXIS, state.getValue(AXIS));
    }
    return null;
  }
}
