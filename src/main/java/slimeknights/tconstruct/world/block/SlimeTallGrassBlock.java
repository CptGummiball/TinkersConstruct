package slimeknights.tconstruct.world.block;

import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import slimeknights.tconstruct.world.TinkerWorld;


public class SlimeTallGrassBlock extends BushBlock {
  /** 1.21 block codec; only used by the block-type registry, gameplay never round-trips it */
  public static final com.mojang.serialization.MapCodec<SlimeTallGrassBlock> CODEC = com.mojang.serialization.codecs.RecordCodecBuilder.mapCodec(instance -> instance.group(
    propertiesCodec(),
    FoliageType.CODEC.fieldOf("foliage").forGetter(SlimeTallGrassBlock::getFoliageType)
  ).apply(instance, SlimeTallGrassBlock::new));

  @Override
  protected com.mojang.serialization.MapCodec<SlimeTallGrassBlock> codec() {
    return CODEC;
  }


  private static final VoxelShape SHAPE = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 13.0D, 14.0D);

  @Getter
  private final FoliageType foliageType;
  public SlimeTallGrassBlock(Properties properties, FoliageType foliageType) {
    super(properties);
    this.foliageType = foliageType;
  }

  @Deprecated
  @Override
  public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
    return SHAPE;
  }

  // Forge port note: shearing drops come from the loot table (shears condition) on Fabric

  @Override
  protected boolean mayPlaceOn(BlockState state, BlockGetter worldIn, BlockPos pos) {
    Block block = state.getBlock();
    return TinkerWorld.slimeDirt.contains(block) || TinkerWorld.vanillaSlimeGrass.contains(block) || TinkerWorld.earthSlimeGrass.contains(block) || TinkerWorld.skySlimeGrass.contains(block) || TinkerWorld.enderSlimeGrass.contains(block) || TinkerWorld.ichorSlimeGrass.contains(block);
  }
}
