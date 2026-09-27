package slimeknights.mantle.client.model.generators;

import com.google.common.base.Preconditions;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.CrossCollisionBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.WallSignBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.block.state.properties.StairsShape;
import slimeknights.mantle.data.ExistingFileHelper;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Provider generating blockstate files plus their block and item models, port of Forge's
 * provider of the same name. Owns a private block and item model provider whose outputs are
 * written together with the blockstates.
 */
public abstract class BlockStateProvider implements DataProvider {
  private static final int DEFAULT_ANGLE_OFFSET = 180;

  @SuppressWarnings("deprecation")
  protected final Map<Block, IGeneratedBlockState> registeredBlocks = new LinkedHashMap<>();

  private final PackOutput output;
  private final String modid;
  private final BlockModelProvider blockModels;
  private final ItemModelProvider itemModels;

  public BlockStateProvider(PackOutput output, String modid, ExistingFileHelper exFileHelper) {
    this.output = output;
    this.modid = modid;
    this.blockModels = new BlockModelProvider(output, modid, exFileHelper) {
      @Override
      protected void registerModels() {}
    };
    this.itemModels = new ItemModelProvider(output, modid, this.blockModels.existingFileHelper) {
      @Override
      protected void registerModels() {}
    };
  }

  @Override
  public CompletableFuture<?> run(CachedOutput cache) {
    models().clear();
    itemModels().clear();
    registeredBlocks.clear();
    registerStatesAndModels();
    CompletableFuture<?>[] futures = new CompletableFuture<?>[2 + this.registeredBlocks.size()];
    int i = 0;
    futures[i++] = models().generateAll(cache);
    futures[i++] = itemModels().generateAll(cache);
    for (Map.Entry<Block, IGeneratedBlockState> entry : this.registeredBlocks.entrySet()) {
      futures[i++] = saveBlockState(cache, entry.getValue().toJson(), entry.getKey());
    }
    return CompletableFuture.allOf(futures);
  }

  protected abstract void registerStatesAndModels();

  public VariantBlockStateBuilder getVariantBuilder(Block b) {
    if (registeredBlocks.containsKey(b)) {
      IGeneratedBlockState old = registeredBlocks.get(b);
      Preconditions.checkState(old instanceof VariantBlockStateBuilder);
      return (VariantBlockStateBuilder)old;
    } else {
      VariantBlockStateBuilder ret = new VariantBlockStateBuilder(b);
      registeredBlocks.put(b, ret);
      return ret;
    }
  }

  public MultiPartBlockStateBuilder getMultipartBuilder(Block b) {
    if (registeredBlocks.containsKey(b)) {
      IGeneratedBlockState old = registeredBlocks.get(b);
      Preconditions.checkState(old instanceof MultiPartBlockStateBuilder);
      return (MultiPartBlockStateBuilder)old;
    } else {
      MultiPartBlockStateBuilder ret = new MultiPartBlockStateBuilder(b);
      registeredBlocks.put(b, ret);
      return ret;
    }
  }

  public BlockModelProvider models() {
    return blockModels;
  }

  public ItemModelProvider itemModels() {
    return itemModels;
  }

  public ResourceLocation modLoc(String name) {
    return ResourceLocation.fromNamespaceAndPath(modid, name);
  }

  public ResourceLocation mcLoc(String name) {
    // parse: forge accepted embedded namespaces here, defaulting to minecraft
    return ResourceLocation.parse(name);
  }

  @SuppressWarnings("deprecation")
  private ResourceLocation key(Block block) {
    return BuiltInRegistries.BLOCK.getKey(block);
  }

  private String name(Block block) {
    return key(block).getPath();
  }

  public ResourceLocation blockTexture(Block block) {
    ResourceLocation name = key(block);
    return ResourceLocation.fromNamespaceAndPath(name.getNamespace(), ModelProvider.BLOCK_FOLDER + "/" + name.getPath());
  }

  private ResourceLocation extend(ResourceLocation rl, String suffix) {
    return ResourceLocation.fromNamespaceAndPath(rl.getNamespace(), rl.getPath() + suffix);
  }

  public ModelFile cubeAll(Block block) {
    return models().cubeAll(name(block), blockTexture(block));
  }

  public void simpleBlock(Block block) {
    simpleBlock(block, cubeAll(block));
  }

  public void simpleBlock(Block block, Function<ModelFile, ConfiguredModel[]> expander) {
    simpleBlock(block, expander.apply(cubeAll(block)));
  }

  public void simpleBlock(Block block, ModelFile model) {
    simpleBlock(block, new ConfiguredModel(model));
  }

  public void simpleBlockItem(Block block, ModelFile model) {
    itemModels().getBuilder(key(block).getPath()).parent(model);
  }

  public void simpleBlockWithItem(Block block, ModelFile model) {
    simpleBlock(block, model);
    simpleBlockItem(block, model);
  }

  public void simpleBlock(Block block, ConfiguredModel... models) {
    getVariantBuilder(block)
      .partialState().setModels(models);
  }

  public void axisBlock(RotatedPillarBlock block) {
    axisBlock(block, blockTexture(block));
  }

  public void logBlock(RotatedPillarBlock block) {
    axisBlock(block, blockTexture(block), extend(blockTexture(block), "_top"));
  }

  public void axisBlock(RotatedPillarBlock block, ResourceLocation baseName) {
    axisBlock(block, extend(baseName, "_side"), extend(baseName, "_end"));
  }

  public void axisBlock(RotatedPillarBlock block, ResourceLocation side, ResourceLocation end) {
    axisBlock(block,
              models().cubeColumn(name(block), side, end),
              models().cubeColumnHorizontal(name(block) + "_horizontal", side, end));
  }

  public void axisBlock(RotatedPillarBlock block, ModelFile vertical, ModelFile horizontal) {
    getVariantBuilder(block)
      .partialState().with(RotatedPillarBlock.AXIS, Direction.Axis.Y)
      .modelForState().modelFile(vertical).addModel()
      .partialState().with(RotatedPillarBlock.AXIS, Direction.Axis.Z)
      .modelForState().modelFile(horizontal).rotationX(90).addModel()
      .partialState().with(RotatedPillarBlock.AXIS, Direction.Axis.X)
      .modelForState().modelFile(horizontal).rotationX(90).rotationY(90).addModel();
  }

  private static final int[] DEFAULT_ANGLE_OFFSETS = {};

  public void horizontalBlock(Block block, ModelFile model) {
    horizontalBlock(block, model, DEFAULT_ANGLE_OFFSET);
  }

  public void horizontalBlock(Block block, ModelFile model, int angleOffset) {
    horizontalBlock(block, $ -> model, angleOffset);
  }

  public void horizontalBlock(Block block, Function<BlockState, ModelFile> modelFunc, int angleOffset) {
    getVariantBuilder(block)
      .forAllStates(state -> ConfiguredModel.builder()
        .modelFile(modelFunc.apply(state))
        .rotationY(((int)state.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot() + angleOffset) % 360)
        .build());
  }

  public void stairsBlock(StairBlock block, ModelFile stairs, ModelFile stairsInner, ModelFile stairsOuter) {
    getVariantBuilder(block)
      .forAllStatesExcept(state -> {
        Direction facing = state.getValue(StairBlock.FACING);
        Half half = state.getValue(StairBlock.HALF);
        StairsShape shape = state.getValue(StairBlock.SHAPE);
        int yRot = (int)facing.getClockWise().toYRot(); // Stairs model is rotated 90 degrees clockwise for some reason
        if (shape == StairsShape.INNER_LEFT || shape == StairsShape.OUTER_LEFT) {
          yRot += 270; // Left facing stairs are rotated 90 degrees clockwise
        }
        if (shape != StairsShape.STRAIGHT && half == Half.TOP) {
          yRot += 90; // Top stairs are rotated 90 degrees clockwise
        }
        yRot %= 360;
        boolean uvlock = yRot != 0 || half == Half.TOP; // Don't set uvlock for states that have no rotation
        return ConfiguredModel.builder()
          .modelFile(shape == StairsShape.STRAIGHT ? stairs : shape == StairsShape.INNER_LEFT || shape == StairsShape.INNER_RIGHT ? stairsInner : stairsOuter)
          .rotationX(half == Half.BOTTOM ? 0 : 180)
          .rotationY(yRot)
          .uvLock(uvlock)
          .build();
      }, StairBlock.WATERLOGGED);
  }

  public void slabBlock(SlabBlock block, ModelFile bottom, ModelFile top, ModelFile doubleslab) {
    getVariantBuilder(block)
      .partialState().with(SlabBlock.TYPE, SlabType.BOTTOM).addModels(new ConfiguredModel(bottom))
      .partialState().with(SlabBlock.TYPE, SlabType.TOP).addModels(new ConfiguredModel(top))
      .partialState().with(SlabBlock.TYPE, SlabType.DOUBLE).addModels(new ConfiguredModel(doubleslab));
  }

  public void buttonBlock(ButtonBlock block, ModelFile button, ModelFile buttonPressed) {
    getVariantBuilder(block).forAllStates(state -> {
      Direction facing = state.getValue(ButtonBlock.FACING);
      AttachFace face = state.getValue(ButtonBlock.FACE);
      boolean powered = state.getValue(ButtonBlock.POWERED);

      return ConfiguredModel.builder()
        .modelFile(powered ? buttonPressed : button)
        .rotationX(face == AttachFace.FLOOR ? 0 : (face == AttachFace.WALL ? 90 : 180))
        .rotationY((int)(face == AttachFace.CEILING ? facing : facing.getOpposite()).toYRot())
        .uvLock(face == AttachFace.WALL)
        .build();
    });
  }

  public void pressurePlateBlock(PressurePlateBlock block, ModelFile pressurePlate, ModelFile pressurePlateDown) {
    getVariantBuilder(block)
      .partialState().with(PressurePlateBlock.POWERED, true).addModels(new ConfiguredModel(pressurePlateDown))
      .partialState().with(PressurePlateBlock.POWERED, false).addModels(new ConfiguredModel(pressurePlate));
  }

  public void signBlock(StandingSignBlock signBlock, WallSignBlock wallSignBlock, ModelFile sign) {
    simpleBlock(signBlock, sign);
    simpleBlock(wallSignBlock, sign);
  }

  public void fourWayBlock(CrossCollisionBlock block, ModelFile post, ModelFile side) {
    MultiPartBlockStateBuilder builder = getMultipartBuilder(block)
      .part().modelFile(post).addModel().end();
    fourWayMultipart(builder, side);
  }

  public void fourWayMultipart(MultiPartBlockStateBuilder builder, ModelFile side) {
    net.minecraft.world.level.block.PipeBlock.PROPERTY_BY_DIRECTION.forEach((dir, value) -> {
      if (dir.getAxis().isHorizontal()) {
        builder.part().modelFile(side).rotationY((((int)dir.toYRot()) + 180) % 360).uvLock(true).addModel()
               .condition(value, true);
      }
    });
  }

  public void fenceGateBlock(FenceGateBlock block, ModelFile gate, ModelFile gateOpen, ModelFile gateWall, ModelFile gateWallOpen) {
    getVariantBuilder(block).forAllStatesExcept(state -> {
      ModelFile model = gate;
      if (state.getValue(FenceGateBlock.IN_WALL)) {
        model = gateWall;
      }
      if (state.getValue(FenceGateBlock.OPEN)) {
        model = model == gateWall ? gateWallOpen : gateOpen;
      }
      return ConfiguredModel.builder()
        .modelFile(model)
        .rotationY((int)state.getValue(FenceGateBlock.FACING).toYRot())
        .uvLock(true)
        .build();
    }, FenceGateBlock.POWERED);
  }

  public void doorBlock(DoorBlock block, ModelFile bottomLeft, ModelFile bottomLeftOpen, ModelFile bottomRight, ModelFile bottomRightOpen,
                        ModelFile topLeft, ModelFile topLeftOpen, ModelFile topRight, ModelFile topRightOpen) {
    getVariantBuilder(block).forAllStatesExcept(state -> {
      int yRot = ((int)state.getValue(DoorBlock.FACING).toYRot()) + 90;
      boolean right = state.getValue(DoorBlock.HINGE) == DoorHingeSide.RIGHT;
      boolean open = state.getValue(DoorBlock.OPEN);
      boolean lower = state.getValue(DoorBlock.HALF) == net.minecraft.world.level.block.state.properties.DoubleBlockHalf.LOWER;
      if (open) {
        yRot += 90;
      }
      if (right && open) {
        yRot += 180;
      }
      yRot %= 360;

      ModelFile model = null;
      if (lower && right && open) {
        model = bottomRightOpen;
      } else if (lower && !right && open) {
        model = bottomLeftOpen;
      }
      if (lower && right && !open) {
        model = bottomRight;
      } else if (lower && !right && !open) {
        model = bottomLeft;
      }
      if (!lower && right && open) {
        model = topRightOpen;
      } else if (!lower && !right && open) {
        model = topLeftOpen;
      }
      if (!lower && right && !open) {
        model = topRight;
      } else if (!lower && !right && !open) {
        model = topLeft;
      }

      return ConfiguredModel.builder().modelFile(model)
        .rotationY(yRot)
        .build();
    }, DoorBlock.POWERED);
  }

  public void trapdoorBlock(TrapDoorBlock block, ModelFile bottom, ModelFile top, ModelFile open, boolean orientable) {
    getVariantBuilder(block).forAllStatesExcept(state -> {
      int xRot = 0;
      int yRot = ((int)state.getValue(TrapDoorBlock.FACING).toYRot()) + 180;
      boolean isOpen = state.getValue(TrapDoorBlock.OPEN);
      if (orientable && isOpen && state.getValue(TrapDoorBlock.HALF) == Half.TOP) {
        xRot += 180;
        yRot += 180;
      }
      if (!orientable && !isOpen) {
        yRot = 0;
      }
      yRot %= 360;
      return ConfiguredModel.builder().modelFile(isOpen ? open : state.getValue(TrapDoorBlock.HALF) == Half.TOP ? top : bottom)
        .rotationX(xRot)
        .rotationY(yRot)
        .build();
    }, TrapDoorBlock.POWERED, TrapDoorBlock.WATERLOGGED);
  }

  public void paneBlock(IronBarsBlock block, ModelFile post, ModelFile side, ModelFile sideAlt, ModelFile noSide, ModelFile noSideAlt) {
    MultiPartBlockStateBuilder builder = getMultipartBuilder(block)
      .part().modelFile(post).addModel().end();
    net.minecraft.world.level.block.PipeBlock.PROPERTY_BY_DIRECTION.forEach((dir, value) -> {
      if (dir.getAxis().isHorizontal()) {
        boolean alt = dir == Direction.SOUTH;
        builder.part().modelFile(alt || dir == Direction.WEST ? sideAlt : side).rotationY(dir.getAxis() == Direction.Axis.X ? 90 : 0).addModel()
               .condition(value, true).end()
               .part().modelFile(alt || dir == Direction.EAST ? noSideAlt : noSide).rotationY(dir == Direction.WEST ? 270 : dir == Direction.SOUTH ? 90 : 0).addModel()
               .condition(value, false);
      }
    });
  }

  private CompletableFuture<?> saveBlockState(CachedOutput cache, JsonObject stateJson, Block owner) {
    ResourceLocation blockName = Preconditions.checkNotNull(key(owner));
    java.nio.file.Path outputPath = this.output.getOutputFolder(PackOutput.Target.RESOURCE_PACK)
                                               .resolve(blockName.getNamespace()).resolve("blockstates").resolve(blockName.getPath() + ".json");
    return DataProvider.saveStable(cache, stateJson, outputPath);
  }

  @Override
  public String getName() {
    return "Block States: " + modid;
  }

  /** List of configured models, serializing as one object or an array */
  public static class ConfiguredModelList {
    private final List<ConfiguredModel> models;

    private ConfiguredModelList(List<ConfiguredModel> models) {
      Preconditions.checkArgument(!models.isEmpty());
      this.models = models;
    }

    public ConfiguredModelList(ConfiguredModel model) {
      this(List.of(model));
    }

    public ConfiguredModelList(ConfiguredModel... models) {
      this(Arrays.asList(models));
    }

    public JsonElement toJSON() {
      if (models.size() == 1) {
        return models.get(0).toJSON(false);
      } else {
        JsonArray ret = new JsonArray();
        for (ConfiguredModel m : models) {
          ret.add(m.toJSON(true));
        }
        return ret;
      }
    }

    public ConfiguredModelList append(ConfiguredModel... models) {
      return new ConfiguredModelList(java.util.stream.Stream.concat(this.models.stream(), Arrays.stream(models)).collect(Collectors.toList()));
    }
  }
}
