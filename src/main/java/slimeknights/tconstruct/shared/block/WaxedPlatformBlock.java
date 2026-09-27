package slimeknights.tconstruct.shared.block;

import lombok.Getter;
import net.minecraft.world.level.block.WeatheringCopper.WeatherState;
import net.minecraft.world.level.block.state.BlockState;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.shared.TinkerCommons;

/**
 * Waxed copper platform.
 *
 * <p>Fabric port: the Forge {@code getToolModifiedState} (axe wax-off) override is gone —
 * vanilla's axe logic handles it once the waxed pairs are registered through Fabric's
 * {@code OxidizableBlocksRegistry}, done in {@link TinkerCommons#init()}.
 */
public class WaxedPlatformBlock extends PlatformBlock {
  @Getter
  private final WeatherState age;
  public WaxedPlatformBlock(WeatherState age, Properties prop) {
    super(prop);
    this.age = age;
  }

  @Override
  protected boolean verticalConnect(BlockState state) {
    return state.is(TinkerTags.Blocks.COPPER_PLATFORMS);
  }
}
