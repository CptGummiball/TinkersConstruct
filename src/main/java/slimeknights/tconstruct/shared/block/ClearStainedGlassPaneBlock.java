package slimeknights.tconstruct.shared.block;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.BeaconBeamBlock;
import slimeknights.tconstruct.shared.block.ClearStainedGlassBlock.GlassColor;

import javax.annotation.Nullable;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class ClearStainedGlassPaneBlock extends ClearGlassPaneBlock implements BeaconBeamBlock {

  private final GlassColor glassColor;
  public ClearStainedGlassPaneBlock(Properties builder, GlassColor glassColor) {
    super(builder);
    this.glassColor = glassColor;
  }

  // 1.21: beacon coloring is the vanilla BeaconBeamBlock interface instead of a Forge hook
  @Override
  public DyeColor getColor() {
    return this.glassColor.getDye();
  }
}
