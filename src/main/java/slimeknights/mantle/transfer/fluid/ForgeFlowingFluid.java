package slimeknights.mantle.transfer.fluid;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;

import javax.annotation.Nullable;
import java.util.function.Supplier;

/**
 * Fabric stand-in for Forge's {@code net.minecraftforge.fluids.ForgeFlowingFluid}: a
 * {@link FlowingFluid} configured from a {@link Properties} object instead of subclass
 * overrides.
 *
 * <p>This is the base of every molten metal in Tinkers, so the {@code Properties} API is kept
 * signature-identical to Forge's — {@code FluidBuilder} and the fluid registrations call
 * straight into it. The Forge original also carried the {@code FluidType} here; on Fabric
 * that association lives in {@link FluidType#register(Fluid...)} instead, made during fluid
 * registration.
 */
public abstract class ForgeFlowingFluid extends FlowingFluid {

  private final Properties properties;

  protected ForgeFlowingFluid(Properties properties) {
    this.properties = properties;
  }

  @Override
  public Fluid getFlowing() {
    return properties.flowing.get();
  }

  @Override
  public Fluid getSource() {
    return properties.still.get();
  }

  @Override
  protected boolean canConvertToSource(Level level) {
    return properties.canConvertToSource;
  }

  @Override
  protected void beforeDestroyingBlock(LevelAccessor level, BlockPos pos, BlockState state) {
    // Match Forge and vanilla water: drop the destroyed block's items.
    BlockEntity blockEntity = state.hasBlockEntity() ? level.getBlockEntity(pos) : null;
    Block.dropResources(state, level, pos, blockEntity);
  }

  @Override
  protected int getSlopeFindDistance(LevelReader level) {
    return properties.slopeFindDistance;
  }

  @Override
  protected int getDropOff(LevelReader level) {
    return properties.levelDecreasePerBlock;
  }

  @Override
  public Item getBucket() {
    return properties.bucket != null ? properties.bucket.get() : Items.AIR;
  }

  @Override
  protected boolean canBeReplacedWith(FluidState state, BlockGetter level, BlockPos pos, Fluid fluid, Direction direction) {
    // Forge's rule: downward flow of a different fluid displaces this one when not a source.
    return direction == Direction.DOWN && !isSame(fluid);
  }

  @Override
  public int getTickDelay(LevelReader level) {
    return properties.tickRate;
  }

  @Override
  protected float getExplosionResistance() {
    return properties.explosionResistance;
  }

  @Override
  protected BlockState createLegacyBlock(FluidState state) {
    if (properties.block != null) {
      return properties.block.get().defaultBlockState().setValue(LiquidBlock.LEVEL, getLegacyLevel(state));
    }
    return Blocks.AIR.defaultBlockState();
  }

  @Override
  public boolean isSame(Fluid fluid) {
    return fluid == properties.still.get() || fluid == properties.flowing.get();
  }

  /** Configuration for a still/flowing fluid pair, signature-identical to Forge's. */
  public static class Properties {

    private final Supplier<? extends FluidType> type;
    private final Supplier<? extends Fluid> still;
    private final Supplier<? extends Fluid> flowing;
    @Nullable
    private Supplier<? extends Item> bucket;
    @Nullable
    private Supplier<? extends LiquidBlock> block;
    private int slopeFindDistance = 4;
    private int levelDecreasePerBlock = 1;
    private float explosionResistance = 1;
    private int tickRate = 5;
    private boolean canConvertToSource = false;

    public Properties(Supplier<? extends FluidType> type, Supplier<? extends Fluid> still, Supplier<? extends Fluid> flowing) {
      this.type = type;
      this.still = still;
      this.flowing = flowing;
    }

    public Properties bucket(@Nullable Supplier<? extends Item> bucket) {
      this.bucket = bucket;
      return this;
    }

    public Properties block(@Nullable Supplier<? extends LiquidBlock> block) {
      this.block = block;
      return this;
    }

    public Properties slopeFindDistance(int slopeFindDistance) {
      this.slopeFindDistance = slopeFindDistance;
      return this;
    }

    public Properties levelDecreasePerBlock(int levelDecreasePerBlock) {
      this.levelDecreasePerBlock = levelDecreasePerBlock;
      return this;
    }

    public Properties explosionResistance(float explosionResistance) {
      this.explosionResistance = explosionResistance;
      return this;
    }

    public Properties tickRate(int tickRate) {
      this.tickRate = tickRate;
      return this;
    }

    public Properties canConvertToSource(boolean canConvertToSource) {
      this.canConvertToSource = canConvertToSource;
      return this;
    }

    public Supplier<? extends FluidType> getType() {
      return type;
    }
  }

  public static class Source extends ForgeFlowingFluid {

    public Source(Properties properties) {
      super(properties);
    }

    @Override
    public int getAmount(FluidState state) {
      return 8;
    }

    @Override
    public boolean isSource(FluidState state) {
      return true;
    }
  }

  public static class Flowing extends ForgeFlowingFluid {

    public Flowing(Properties properties) {
      super(properties);
      registerDefaultState(getStateDefinition().any().setValue(LEVEL, 7));
    }

    @Override
    protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> builder) {
      super.createFluidStateDefinition(builder);
      builder.add(LEVEL);
    }

    @Override
    public int getAmount(FluidState state) {
      return state.getValue(LEVEL);
    }

    @Override
    public boolean isSource(FluidState state) {
      return false;
    }
  }
}
