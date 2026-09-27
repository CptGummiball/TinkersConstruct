package slimeknights.mantle.item;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.HoneycombItem;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

/**
 * Fabric stand-in for Forge's {@code BlockState.getToolModifiedState}: computes the block
 * state a tool action would turn a block into, without performing the action.
 *
 * <p>Backed by the vanilla transform maps (which Fabric API's content registries also write
 * into, so stripping/flattening registered by other mods is covered). Tilling is the
 * exception: vanilla stores tilling as consumers whose result cannot be queried, so the
 * vanilla tilling rules are mirrored here — modded tillables fall outside until a compat
 * hook exists.
 */
public final class ToolActionTransforms {

  private ToolActionTransforms() {}

  /** Computes the state the given action transforms the block into, or null if the action does not apply. */
  @Nullable
  public static BlockState getToolModifiedState(BlockState state, UseOnContext context, ToolAction action, boolean simulate) {
    if (action == ToolActions.AXE_STRIP) {
      Block stripped = AxeItem.STRIPPABLES.get(state.getBlock());
      return stripped != null ? stripped.withPropertiesOf(state) : null;
    }
    if (action == ToolActions.AXE_SCRAPE) {
      return WeatheringCopper.getPrevious(state).orElse(null);
    }
    if (action == ToolActions.AXE_WAX_OFF) {
      Block unwaxed = HoneycombItem.WAX_OFF_BY_BLOCK.get().get(state.getBlock());
      return unwaxed != null ? unwaxed.withPropertiesOf(state) : null;
    }
    if (action == ToolActions.SHOVEL_FLATTEN) {
      BlockState flattened = ShovelItem.FLATTENABLES.get(state.getBlock());
      return flattened != null ? flattened : null;
    }
    if (action == ToolActions.HOE_TILL) {
      // vanilla tilling rules; the tillables map stores consumers, so the results are mirrored here
      Block block = state.getBlock();
      if (block == Blocks.DIRT || block == Blocks.GRASS_BLOCK || block == Blocks.DIRT_PATH) {
        // farmland requires a clear sky above the clicked position
        BlockPos above = context.getClickedPos().above();
        if (!context.getLevel().getBlockState(above).isSolid()) {
          return Blocks.FARMLAND.defaultBlockState();
        }
        return null;
      }
      if (block == Blocks.COARSE_DIRT || block == Blocks.ROOTED_DIRT) {
        return Blocks.DIRT.defaultBlockState();
      }
      return null;
    }
    return null;
  }
}
