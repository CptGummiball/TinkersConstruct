package slimeknights.tconstruct.fabric.client;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import slimeknights.mantle.block.entity.IRetexturedBlockEntity;
import slimeknights.mantle.transfer.fluid.FluidStack;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.fluids.TinkerFluids;
import slimeknights.tconstruct.library.recipe.FluidValues;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.shared.TinkerMaterials;
import slimeknights.tconstruct.shared.block.ClearStainedGlassBlock.GlassColor;
import slimeknights.tconstruct.smeltery.block.entity.CastingBlockEntity;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.block.component.SearedTankBlock.TankType;
import slimeknights.tconstruct.smeltery.block.entity.component.TankBlockEntity;
import slimeknights.tconstruct.tables.TinkerTables;

/**
 * Development harness that builds a row of blocks whose models depend on block entity state, then
 * photographs them.
 *
 * <p>Model data is delivered while a chunk mesh is built, so nothing about it shows up in a compile
 * or in a log: a tank with the wiring broken renders as an empty tank, and a retextured table
 * renders as its default wood. The only check is to place one and look.
 *
 * <p>The scene is two rows. The back row is what a player gets by placing the block, and is the row
 * to compare against the Forge build. The front row is the same blocks retextured, in gold and
 * diamond — colours chosen because they cannot be mistaken for the default, which is the whole point
 * of a check: a retexture that silently did nothing would otherwise look exactly like one that
 * worked. Enabled with {@code -Dtconstruct.blockHarness=true}.
 */
public final class BlockRenderDevHarness {
  private BlockRenderDevHarness() {}

  /** System property gating the harness */
  private static final String PROPERTY = "tconstruct.blockHarness";
  /** Where the scene is built, well away from anything the world generated */
  private static final BlockPos ORIGIN = new BlockPos(0, 100, 0);

  private static int ticks = 0;
  private static boolean built = false;

  /** Registers the harness if the property is set */
  public static void init() {
    if (!Boolean.getBoolean(PROPERTY)) {
      return;
    }
    ClientTickEvents.END_CLIENT_TICK.register(BlockRenderDevHarness::tick);
  }

  private static void tick(Minecraft minecraft) {
    if (minecraft.level == null || minecraft.player == null) {
      return;
    }
    ticks++;
    if (ticks == 1) {
      // a dev window loses focus while the harness runs, and singleplayer pauses when it does
      minecraft.options.pauseOnLostFocus = false;
      minecraft.options.menuBackgroundBlurriness().set(0);
      // the overlay is not what is being checked, and the join spam covers the whole scene
      minecraft.options.hideGui = true;
    }
    if (!built) {
      if (ticks < 60) {
        return;
      }
      built = true;
      minecraft.gui.getChat().clearMessages(true);
      minecraft.getToasts().clear();
      buildScene(minecraft);
      ticks = 0;
      return;
    }
    // give the chunk time to rebuild and the block entity data time to sync back to the client
    if (ticks == 55) {
      // what the client believes about the tank, which is what its model and renderer see
      if (minecraft.level.getBlockEntity(ORIGIN.offset(2, 0, 1)) instanceof TankBlockEntity tank) {
        TConstruct.LOG.info("[block harness] client tank holds {}, {} fluid cuboids registered", tank.getTank().getFluid(),
                            slimeknights.mantle.client.render.FluidCuboid.REGISTRY.get(tank.getBlockState(), java.util.List.of()).size());
      } else {
        TConstruct.LOG.warn("[block harness] client has no tank block entity");
      }
    }
    if (ticks == 58) {
      minecraft.setScreen(null);
    }
    if (ticks == 60) {
      Screenshot.grab(minecraft.gameDirectory, "blocks_model_data.png", minecraft.getMainRenderTarget(),
                      message -> TConstruct.LOG.info("[block harness] {}", message.getString()));
    }
    if (ticks > 80) {
      TConstruct.LOG.info("[block harness] done");
      minecraft.stop();
    }
  }

  /** Clears a patch of sky and fills it with the blocks worth looking at */
  private static void buildScene(Minecraft minecraft) {
    MinecraftServer server = minecraft.getSingleplayerServer();
    if (server == null) {
      TConstruct.LOG.error("[block harness] needs a singleplayer world");
      minecraft.stop();
      return;
    }
    server.execute(() -> {
      ServerLevel level = server.overworld();
      // broad daylight and no weather, so the photograph is of the blocks and not of the sky
      level.setDayTime(6000);
      level.getGameRules().getRule(net.minecraft.world.level.GameRules.RULE_DAYLIGHT).set(false, server);
      level.setWeatherParameters(6000, 0, false, false);

      // the room has to reach past where the camera stands, or the camera stands inside rock
      BlockState floor = Blocks.WHITE_CONCRETE.defaultBlockState();
      BlockState air = Blocks.AIR.defaultBlockState();
      for (int x = -5; x <= 5; x++) {
        for (int z = -5; z <= 10; z++) {
          level.setBlock(ORIGIN.offset(x, -1, z), floor, 3);
          for (int y = 0; y < 5; y++) {
            level.setBlock(ORIGIN.offset(x, y, z), air, 3);
          }
        }
      }

      // Back row: exactly what a player gets by placing the block, with no texture chosen. This is
      // the row to compare against the Forge build — anything but the default look here is a bug.
      level.setBlock(ORIGIN.offset(-2, 0, -2), TinkerTables.craftingStation.get().defaultBlockState(), 3);
      level.setBlock(ORIGIN.offset(0, 0, -2), TinkerSmeltery.searedDrain.get().defaultBlockState(), 3);
      level.setBlock(ORIGIN.offset(2, 0, -2), TinkerSmeltery.searedTank.get(TankType.FUEL_TANK).defaultBlockState(), 3);

      // Front row: the same blocks retextured. Gold and diamond are chosen precisely because they
      // cannot be mistaken for the default — a retexture that silently did nothing would otherwise
      // look identical to one that worked.
      retexture(level, ORIGIN.offset(-2, 0, 1), TinkerTables.craftingStation.get().defaultBlockState(), "minecraft:gold_block");
      retexture(level, ORIGIN.offset(0, 0, 1), TinkerSmeltery.searedDrain.get().defaultBlockState(), "minecraft:diamond_block");

      // and a tank with fluid in it: the model reads the fluid and its capacity from the block entity
      BlockPos tankPos = ORIGIN.offset(2, 0, 1);
      level.setBlock(tankPos, TinkerSmeltery.searedTank.get(TankType.FUEL_TANK).defaultBlockState(), 3);
      fillTank(level, tankPos);

      // A casting basin with fluid and a pane of clear glass. Neither is about model data: both are
      // here because a block whose render_type is declared by a parent template used to land on the
      // solid layer, where a see-through texture turns opaque black and hides what is behind it.
      BlockPos basinPos = ORIGIN.offset(4, 0, 1);
      level.setBlock(basinPos, TinkerSmeltery.searedBasin.get().defaultBlockState(), 3);
      if (level.getBlockEntity(basinPos) instanceof CastingBlockEntity basin) {
        basin.updateFluidTo(new FluidStack(TinkerFluids.moltenIron.get(), FluidValues.INGOT * 4));
      }
      // Connected textures: a 2x2 wall of clear glass and one of stained glass. With the connected
      // loader running, the borders between the four blocks vanish and only the outer rim remains;
      // without it every block wears a full frame. The stained wall additionally proves the baked
      // colour survives the connection rebake.
      for (int x = 4; x <= 5; x++) {
        for (int y = 0; y <= 1; y++) {
          level.setBlock(ORIGIN.offset(x, y, -2), TinkerCommons.clearGlass.get().defaultBlockState(), 3);
          level.setBlock(ORIGIN.offset(x - 9, y, -2), TinkerCommons.clearStainedGlass.get(GlassColor.BLUE).defaultBlockState(), 3);
        }
      }
      // a two-high pane column: panes connect through their own predicate and multipart models
      level.setBlock(ORIGIN.offset(5, 0, 3), TinkerCommons.clearGlassPane.get().defaultBlockState(), 3);
      level.setBlock(ORIGIN.offset(5, 1, 3), TinkerCommons.clearGlassPane.get().defaultBlockState(), 3);

      // the slime-metal storage blocks: queen's slime is the colored_block loader (baked glow),
      // slimesteel and cinderslime are forge:composite (opaque frame + translucent overlay)
      level.setBlock(ORIGIN.offset(-4, 0, 3), TinkerMaterials.queensSlime.get().defaultBlockState(), 3);
      level.setBlock(ORIGIN.offset(-3, 0, 3), TinkerMaterials.slimesteel.get().defaultBlockState(), 3);
      level.setBlock(ORIGIN.offset(-2, 0, 3), TinkerMaterials.cinderslime.get().defaultBlockState(), 3);

      // stand back and look at the row
      server.getPlayerList().getPlayers().forEach(player -> {
        player.teleportTo(level, ORIGIN.getX() + 1.5, ORIGIN.getY() + 1, ORIGIN.getZ() + 7.5, 180, 18);
        player.setNoGravity(true);
      });
    });
  }

  /** Places a block and tells its block entity which block to copy its texture from */
  private static void retexture(ServerLevel level, BlockPos pos, BlockState state, String texture) {
    level.setBlock(pos, state, 3);
    BlockEntity be = level.getBlockEntity(pos);
    if (be instanceof IRetexturedBlockEntity retextured) {
      retextured.updateTexture(texture);
    } else {
      TConstruct.LOG.error("[block harness] {} is not retexturable", state.getBlock());
    }
  }

  /** Fills a placed tank so its model has something to draw */
  private static void fillTank(ServerLevel level, BlockPos pos) {
    if (level.getBlockEntity(pos) instanceof TankBlockEntity tank) {
      tank.getTank().setFluid(new FluidStack(TinkerFluids.moltenIron.get(), tank.getTank().getCapacity() * 3 / 4));
      tank.onTankContentsChanged();
      tank.setChanged();
      BlockState state = level.getBlockState(pos);
      level.sendBlockUpdated(pos, state, state, 3);
    } else {
      TConstruct.LOG.error("[block harness] no tank at {}", pos);
    }
  }
}
