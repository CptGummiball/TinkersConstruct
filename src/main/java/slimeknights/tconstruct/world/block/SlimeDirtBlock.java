package slimeknights.tconstruct.world.block;

import net.minecraft.world.level.block.Block;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

/**
 * Slime dirt. The Forge canSustainPlant hook (slime plants + plains plants) is gone on
 * Fabric: slime plants check {@code mayPlaceOn} themselves; letting vanilla plains plants
 * grow here would need slime dirt in the {@code minecraft:dirt} tag, a data-pass decision.
 */
public class SlimeDirtBlock extends Block {

  public SlimeDirtBlock(Properties properties) {
    super(properties);
  }
}
