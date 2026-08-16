package slimeknights.tconstruct.shared.block;

import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockState;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.shared.TinkerCommons;

import javax.annotation.Nullable;
import java.util.Optional;

/**
 * Weathering copper platform.
 *
 * <p>Fabric port: the Forge {@code getToolModifiedState} (axe scrape) and {@code use}
 * (honeycomb waxing) overrides are gone — vanilla's axe/honeycomb item logic handles both
 * once the platform pairs are registered through Fabric's {@code OxidizableBlocksRegistry},
 * done in {@link TinkerCommons#init()}.
 */
public class WeatheringPlatformBlock extends PlatformBlock implements WeatheringCopper {
  @Getter
  private final WeatherState age;
  public WeatheringPlatformBlock(WeatherState age, Properties props) {
    super(props);
    this.age = age;
  }

  @Override
  protected boolean verticalConnect(BlockState state) {
    return state.is(TinkerTags.Blocks.COPPER_PLATFORMS);
  }

  @Override
  public void randomTick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
    this.changeOverTime(pState, pLevel, pPos, pRandom);
  }

  /** Gets the next state for weathering */
  @Nullable
  private static WeatherState getNext(WeatherState original) {
    return switch (original) {
      case UNAFFECTED -> WeatherState.EXPOSED;
      case EXPOSED -> WeatherState.WEATHERED;
      case WEATHERED -> WeatherState.OXIDIZED;
      default -> null;
    };
  }

  @Override
  public boolean isRandomlyTicking(BlockState pState) {
    return getNext(age) != null;
  }

  @Override
  public Optional<BlockState> getNext(BlockState state) {
    return Optional.ofNullable(getNext(age))
                   .map(next -> TinkerCommons.copperPlatform.get(next).withPropertiesOf(state));
  }
}
