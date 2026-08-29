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
    if (ticks == 50) {
      // regression: geometry-inherited models must keep vanilla item transforms (a scale of 1
      // means the block/block display chain got lost and the held controller renders huge)
      var itemModel = minecraft.getItemRenderer().getModel(new net.minecraft.world.item.ItemStack(slimeknights.tconstruct.smeltery.TinkerSmeltery.smelteryController.get()), null, null, 0);
      float scale = itemModel.getTransforms().firstPersonRightHand.scale.x();
      if (scale >= 0.99f) {
        TConstruct.LOG.error("[block harness] TRANSFORM FAIL: controller item first-person scale is {}", scale);
      } else {
        TConstruct.LOG.info("[block harness] TRANSFORM PASS: controller item first-person scale {}", scale);
      }
    }
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
      // stand back far enough to see all three rows and the held item
      MinecraftServer server = minecraft.getSingleplayerServer();
      if (server != null && !server.getPlayerList().getPlayers().isEmpty()) {
        var serverPlayer = server.getPlayerList().getPlayers().get(0);
        serverPlayer.teleportTo(serverPlayer.serverLevel(), ORIGIN.getX() - 1.5, ORIGIN.getY() + 1, ORIGIN.getZ() + 7.5, 180, 15);
      }
    }
    if (ticks == 60) {
      Screenshot.grab(minecraft.gameDirectory, "blocks_model_data.png", minecraft.getMainRenderTarget(),
                      message -> TConstruct.LOG.info("[block harness] {}", message.getString()));
    }
    if (ticks == 80) {
      // stand inside the clear glass: the transparent overlay mixin must show the glass texture
      // instead of vanilla's opaque black wall
      MinecraftServer server = minecraft.getSingleplayerServer();
      if (server != null && !server.getPlayerList().getPlayers().isEmpty()) {
        var serverPlayer = server.getPlayerList().getPlayers().get(0);
        serverPlayer.teleportTo(serverPlayer.serverLevel(), ORIGIN.getX() + 0.5, ORIGIN.getY(), ORIGIN.getZ() + 5.5, 180, 0);
      }
    }
    if (ticks == 95) {
      Screenshot.grab(minecraft.gameDirectory, "blocks_overlay.png", minecraft.getMainRenderTarget(),
                      message -> TConstruct.LOG.info("[block harness] {}", message.getString()));
    }
    if (ticks == 100) {
      // stand before the filled tank looking at it: Jade (dev runtime) shows its overlay with the
      // fluid bar, proving the block-level fluid storage bridge answers lookups
      minecraft.options.hideGui = false; // Jade draws in the gui layer, F1 would hide it
      MinecraftServer server = minecraft.getSingleplayerServer();
      if (server != null && !server.getPlayerList().getPlayers().isEmpty()) {
        var serverPlayer = server.getPlayerList().getPlayers().get(0);
        serverPlayer.teleportTo(serverPlayer.serverLevel(), ORIGIN.getX() + 2.5, ORIGIN.getY(), ORIGIN.getZ() + 4.5, 180, 20);
      }
    }
    if (ticks == 130) {
      Screenshot.grab(minecraft.gameDirectory, "blocks_jade.png", minecraft.getMainRenderTarget(),
                      message -> TConstruct.LOG.info("[block harness] {}", message.getString()));
    }
    if (ticks == 150) {
      // a real smeltery: floor, one-block bore, controller and drain in the wall. This is the
      // reported in-the-wild case — structure blocks flip to in_structure and the controller
      // window shows the tank fluid through the dynamic fluid-texture rebake.
      MinecraftServer server = minecraft.getSingleplayerServer();
      if (server != null) {
        server.execute(() -> buildSmeltery(server));
      }
    }
    if (ticks == 195) {
      MinecraftServer server = minecraft.getSingleplayerServer();
      if (server != null) {
        server.execute(() -> checkSmeltery(server, minecraft));
      }
    }
    if (ticks == 205) {
      MinecraftServer server = minecraft.getSingleplayerServer();
      if (server != null) {
        server.execute(() -> checkCraftingStation(server));
      }
    }
    if (ticks == 210) {
      MinecraftServer server = minecraft.getSingleplayerServer();
      if (server != null) {
        server.execute(() -> checkTankTransfer(server));
      }
    }
    if (ticks == 212) {
      // regression for the shaped-material recipes (tinkers anvil): both sides must agree on a
      // 3x3 pattern with 9 ingredient slots — EMI indexes the ingredient list by grid position
      // and a shorter list breaks both its display and, if server-side, the actual crafting
      var id = net.minecraft.resources.ResourceLocation.parse("tconstruct:tables/tinkers_anvil_material");
      MinecraftServer server = minecraft.getSingleplayerServer();
      boolean pass = true;
      for (var side : java.util.List.of("server", "client")) {
        var manager = side.equals("server") && server != null ? server.getRecipeManager() : minecraft.level.getRecipeManager();
        var holder = manager.byKey(id).orElse(null);
        if (holder == null || !(holder.value() instanceof net.minecraft.world.item.crafting.ShapedRecipe shaped)) {
          TConstruct.LOG.error("[block harness] RECIPE FAIL: {} anvil recipe missing or not shaped: {}", side, holder);
          pass = false;
          continue;
        }
        var ingredients = shaped.getIngredients();
        long empty = ingredients.stream().filter(net.minecraft.world.item.crafting.Ingredient::isEmpty).count();
        long custom = ingredients.stream().filter(ing -> ing.getCustomIngredient() != null).count();
        TConstruct.LOG.info("[block harness] {} anvil recipe: {} {}x{}, {} ingredients ({} empty, {} custom)",
          side, shaped.getClass().getSimpleName(), shaped.getWidth(), shaped.getHeight(), ingredients.size(), empty, custom);
        if (shaped.getWidth() != 3 || shaped.getHeight() != 3 || ingredients.size() != 9 || custom != 3) {
          pass = false;
        }
      }
      // and the client's copy of the material tags, via a tag that is filled without compat mods
      TConstruct.LOG.info("[block harness] client material tag check: cobalt in nether tag = {}",
        slimeknights.tconstruct.library.materials.MaterialRegistry.getInstance().isInTag(
          slimeknights.tconstruct.tools.data.material.MaterialIds.cobalt,
          slimeknights.tconstruct.common.TinkerTags.Materials.NETHER));
      if (pass) {
        TConstruct.LOG.info("[block harness] RECIPE PASS: anvil recipe is 3x3 with 9 ingredient slots on both sides");
      } else {
        TConstruct.LOG.error("[block harness] RECIPE FAIL: anvil recipe pattern mangled, see lines above");
      }
    }
    if (ticks == 213) {
      // blank-item scan: the pack reported empty-looking slots in EMI and creative. An item
      // whose model bakes zero quads and has no custom renderer draws exactly nothing, so
      // walk every creative stack and flag those.
      net.minecraft.world.item.CreativeModeTabs.tryRebuildTabContents(minecraft.level.enabledFeatures(), true, minecraft.level.registryAccess());
      var rand = net.minecraft.util.RandomSource.create(42);
      java.util.Set<String> blank = new java.util.TreeSet<>();
      int checked = 0;
      for (var tab : net.minecraft.world.item.CreativeModeTabs.allTabs()) {
        for (var stack : tab.getDisplayItems()) {
          var id = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(stack.getItem());
          if (!"tconstruct".equals(id.getNamespace())) {
            continue;
          }
          checked++;
          var model = minecraft.getItemRenderer().getModel(stack, minecraft.level, null, 0);
          if (model.isCustomRenderer()) {
            continue; // drawn by a special renderer, quads say nothing
          }
          boolean hasQuads = !model.getQuads(null, null, rand).isEmpty();
          if (!hasQuads) {
            for (var dir : net.minecraft.core.Direction.values()) {
              if (!model.getQuads(null, dir, rand).isEmpty()) {
                hasQuads = true;
                break;
              }
            }
          }
          if (!hasQuads) {
            blank.add(id.toString());
          }
        }
      }
      if (blank.isEmpty()) {
        TConstruct.LOG.info("[block harness] ITEMMODEL PASS: {} creative stacks all bake visible models", checked);
      } else {
        TConstruct.LOG.error("[block harness] ITEMMODEL FAIL: {} items bake no quads out of {} stacks: {}", blank.size(), checked, blank);
      }
    }
    if (ticks == 215) {
      // stand south of the smeltery at ground level looking at the controller wall
      minecraft.options.hideGui = true;
      MinecraftServer server = minecraft.getSingleplayerServer();
      if (server != null && !server.getPlayerList().getPlayers().isEmpty()) {
        var serverPlayer = server.getPlayerList().getPlayers().get(0);
        serverPlayer.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND, net.minecraft.world.item.ItemStack.EMPTY);
        serverPlayer.teleportTo(serverPlayer.serverLevel(), ORIGIN.getX() - 8 + 0.5, ORIGIN.getY() - 1, ORIGIN.getZ() + 11.5, 180, -5);
      }
    }
    if (ticks == 222) {
      // alloying regression: 90 copper + 90 gold with lava fuel must become 180 rose gold.
      // The first fueled tick alloys (exercising the recipe-list shuffle) and the next tick
      // re-checks the cached recipe against the now-consumed inputs (exercising the prune) —
      // both threw UnsupportedOperationException while the cache was an immutable list.
      MinecraftServer server = minecraft.getSingleplayerServer();
      if (server != null) {
        server.execute(() -> {
          ServerLevel level = server.overworld();
          if (level.getBlockEntity(ORIGIN.offset(-8, 0, 6)) instanceof slimeknights.tconstruct.smeltery.block.entity.controller.HeatingStructureBlockEntity structure) {
            structure.getTank().setFluids(java.util.List.of(
              new FluidStack(TinkerFluids.moltenCopper.get(), 90),
              new FluidStack(TinkerFluids.moltenGold.get(), 90)));
            // setFluids is a test backdoor; fire the change note melting would have fired, as
            // the alloy module drops its cached recipe list on it
            structure.notifyFluidsChanged(slimeknights.tconstruct.smeltery.block.entity.tank.ISmelteryTankHandler.FluidChange.ADDED,
              new FluidStack(TinkerFluids.moltenCopper.get(), 90));
            TConstruct.LOG.info("[block harness] alloy inputs set: 90 copper + 90 gold");
          } else {
            TConstruct.LOG.error("[block harness] ALLOY FAIL: no structure block entity to fill");
          }
        });
      }
    }
    if (ticks == 252) {
      MinecraftServer server = minecraft.getSingleplayerServer();
      if (server != null) {
        server.execute(() -> {
          ServerLevel level = server.overworld();
          if (level.getBlockEntity(ORIGIN.offset(-8, 0, 6)) instanceof slimeknights.tconstruct.smeltery.block.entity.controller.HeatingStructureBlockEntity structure) {
            var fluids = structure.getTank().getFluids();
            int roseGold = 0;
            int leftovers = 0;
            for (FluidStack fluid : fluids) {
              String path = net.minecraft.core.registries.BuiltInRegistries.FLUID.getKey(fluid.getFluid()).getPath();
              if ("molten_rose_gold".equals(path)) {
                roseGold += fluid.getAmount();
              } else if ("molten_copper".equals(path) || "molten_gold".equals(path)) {
                leftovers += fluid.getAmount();
              }
            }
            if (roseGold == 180 && leftovers == 0) {
              TConstruct.LOG.info("[block harness] ALLOY PASS: 90 copper + 90 gold alloyed to exactly 180 rose gold");
            } else {
              TConstruct.LOG.error("[block harness] ALLOY FAIL: rose gold {} (want 180), unconsumed inputs {}, tank: {}", roseGold, leftovers, fluids);
              TConstruct.LOG.error("[block harness] ALLOY DEBUG: hasStructure={}, hasTanks={}, hasFuel={}, possibleTemp={}, canAlloy={}, alloyRecipesLoaded={}",
                structure.getStructure() != null,
                structure.getStructure() != null && structure.getStructure().hasTanks(),
                structure.getFuelModule().hasFuel(),
                structure.getFuelModule().findFuel(false),
                ((slimeknights.tconstruct.smeltery.block.entity.controller.SmelteryBlockEntity) structure).getAlloyingModule().canAlloy(),
                level.getRecipeManager().getAllRecipesFor(slimeknights.tconstruct.library.recipe.TinkerRecipeTypes.ALLOYING.get()).size());
              var probe = new slimeknights.tconstruct.smeltery.block.entity.module.alloying.SmelteryAlloyTank(structure.getTank());
              probe.setTemperature(1000);
              TConstruct.LOG.error("[block harness] ALLOY DEBUG2: smeltery tank reports {} tanks, [0]={}, [1]={}",
                structure.getTank().getTanks(), structure.getTank().getFluidInTank(0), structure.getTank().getFluidInTank(1));
              for (var holder : level.getRecipeManager().getAllRecipesFor(slimeknights.tconstruct.library.recipe.TinkerRecipeTypes.ALLOYING.get())) {
                if (holder.value().matches(probe, level)) {
                  TConstruct.LOG.error("[block harness] ALLOY DEBUG2: {} matches, canPerform={}", holder.id(), holder.value().canPerform(probe));
                }
              }
              TConstruct.LOG.error("[block harness] ALLOY DEBUG2: copper.is(c:molten_copper)={}",
                TinkerFluids.moltenCopper.get().is(net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.FLUID, net.minecraft.resources.ResourceLocation.parse("c:molten_copper"))));
            }
          }
        });
      }
    }
    if (ticks == 240) {
      // regression: the formed controller must bake real sprites for its window and fluid;
      // missingno here is the in-structure missing-texture bug
      var cPos = ORIGIN.offset(-8, 0, 6);
      var cState = minecraft.level.getBlockState(cPos);
      Object rd = ((net.fabricmc.fabric.api.blockview.v2.FabricBlockView) minecraft.level).getBlockEntityRenderData(cPos);
      if (rd instanceof slimeknights.mantle.client.model.data.ModelData md
          && minecraft.getBlockRenderer().getBlockModel(cState) instanceof slimeknights.mantle.client.model.BakedModelWrapper<?> wrapper) {
        var rand = net.minecraft.util.RandomSource.create(42);
        java.util.Set<String> sprites = new java.util.TreeSet<>();
        for (var dir : net.minecraft.core.Direction.values()) {
          for (var quad : wrapper.getQuads(cState, dir, rand, md, null)) {
            sprites.add(quad.getSprite().contents().name().toString());
          }
        }
        if (sprites.contains("minecraft:missingno") || sprites.isEmpty()) {
          TConstruct.LOG.error("[block harness] STRUCTURE FAIL: formed controller bakes {}", sprites);
        } else {
          TConstruct.LOG.info("[block harness] STRUCTURE PASS: formed controller bakes {}", sprites);
        }
      } else {
        TConstruct.LOG.error("[block harness] STRUCTURE FAIL: no render data or wrapper model for the controller");
      }
    }
    if (ticks == 245) {
      Screenshot.grab(minecraft.gameDirectory, "blocks_smeltery.png", minecraft.getMainRenderTarget(),
                      message -> TConstruct.LOG.info("[block harness] {}", message.getString()));
    }
    if (ticks > 265) {
      TConstruct.LOG.info("[block harness] done");
      minecraft.stop();
    }
  }

  /** Builds a minimal smeltery west of the scene: 3x3 floor, 1x1 bore two high, controller south */
  private static void buildSmeltery(MinecraftServer server) {
    ServerLevel level = server.overworld();
    BlockPos center = ORIGIN.offset(-8, 0, 5);
    BlockState bricks = TinkerSmeltery.searedBricks.get().defaultBlockState();
    // floor at y-1
    for (int x = -1; x <= 1; x++) {
      for (int z = -1; z <= 1; z++) {
        level.setBlock(center.offset(x, -1, z), bricks, 3);
      }
    }
    // walls two high around the single-column bore
    for (int y = 0; y <= 1; y++) {
      for (int x = -1; x <= 1; x++) {
        for (int z = -1; z <= 1; z++) {
          if (x != 0 || z != 0) {
            level.setBlock(center.offset(x, y, z), bricks, 3);
          }
        }
      }
    }
    // a fuel tank full of lava in the wall, so the smeltery can actually heat and alloy;
    // placed before the controller so the structure scan sees it from the start, and in the
    // middle of the east wall — corners count as frame, which some structures treat specially
    BlockPos fuelPos = center.offset(1, 0, 0);
    level.setBlock(fuelPos, TinkerSmeltery.searedTank.get(TankType.FUEL_TANK).defaultBlockState(), 3);
    if (level.getBlockEntity(fuelPos) instanceof TankBlockEntity fuelTank) {
      fuelTank.getTank().setFluid(new FluidStack(net.minecraft.world.level.material.Fluids.LAVA, fuelTank.getTank().getCapacity()));
      fuelTank.onTankContentsChanged();
    }
    // controller facing the camera (south wall, looking south), drain beside it
    level.setBlock(center.offset(0, 0, 1), TinkerSmeltery.smelteryController.get().defaultBlockState()
      .setValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING, net.minecraft.core.Direction.SOUTH), 3);
    level.setBlock(center.offset(-1, 0, 1), TinkerSmeltery.searedDrain.get().defaultBlockState()
      .setValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING, net.minecraft.core.Direction.SOUTH), 3);
    TConstruct.LOG.info("[block harness] smeltery built at {}", center);
  }

  /** Verifies the smeltery formed, fills its tank for the window rebake, and exercises the menu slots */
  private static void checkSmeltery(MinecraftServer server, Minecraft minecraft) {
    ServerLevel level = server.overworld();
    BlockPos controllerPos = ORIGIN.offset(-8, 0, 6);
    BlockState controllerState = level.getBlockState(controllerPos);
    boolean formed = controllerState.hasProperty(slimeknights.tconstruct.smeltery.block.controller.ControllerBlock.IN_STRUCTURE)
                     && controllerState.getValue(slimeknights.tconstruct.smeltery.block.controller.ControllerBlock.IN_STRUCTURE);
    TConstruct.LOG.info("[block harness] smeltery controller {} formed={}", controllerState, formed);
    if (!(level.getBlockEntity(controllerPos) instanceof slimeknights.tconstruct.smeltery.block.entity.controller.HeatingStructureBlockEntity structure)) {
      TConstruct.LOG.error("[block harness] SMELTERY FAIL: no structure block entity");
      return;
    }
    if (!formed) {
      TConstruct.LOG.error("[block harness] SMELTERY FAIL: structure did not form");
      return;
    }
    // fluid behind the window: the dynamic rebake the missing-texture report was about
    structure.getTank().setFluids(java.util.List.of(new FluidStack(slimeknights.tconstruct.fluids.TinkerFluids.moltenIron.get(), 1000)));

    // menu slot regression: items placed into the GUI must stay, shift-click must not duplicate
    var player = server.getPlayerList().getPlayers().get(0);
    // open through the block's own interaction path, which carries the position payload
    controllerState.useWithoutItem(level, player, new net.minecraft.world.phys.BlockHitResult(
      net.minecraft.world.phys.Vec3.atCenterOf(controllerPos), net.minecraft.core.Direction.SOUTH, controllerPos, false));
    if (!(player.containerMenu instanceof slimeknights.tconstruct.smeltery.menu.HeatingStructureContainerMenu menu)) {
      TConstruct.LOG.error("[block harness] SLOT FAIL: menu did not open, got {}", player.containerMenu);
      return;
    }
    var ingot = net.minecraft.world.item.Items.IRON_INGOT;
    // place a stack of 5 into the first melting slot by clicking with it
    int meltingSlot = -1;
    for (int i = 0; i < menu.slots.size(); i++) {
      var slot = menu.slots.get(i);
      if (slot.container != player.getInventory() && !(slot instanceof slimeknights.tconstruct.smeltery.menu.HeatingStructureContainerMenu.BucketSlot)
          && !(slot instanceof slimeknights.tconstruct.smeltery.menu.HeatingStructureContainerMenu.ResultSlot)
          && slot.getItem().isEmpty() && slot.mayPlace(new net.minecraft.world.item.ItemStack(ingot))) {
        meltingSlot = i;
        break;
      }
    }
    if (meltingSlot == -1) {
      TConstruct.LOG.error("[block harness] SLOT FAIL: no melting slot found in menu; side inventory has {} slots, melting inventory {}",
        menu.getSideInventory() == null ? -1 : menu.getSideInventory().getSlotCount(), structure.getMeltingInventory().getSlots());
      for (int i = 0; i < Math.min(menu.slots.size(), 8); i++) {
        var slot = menu.slots.get(i);
        TConstruct.LOG.error("[block harness]   slot {} = {} empty={} mayPlaceIngot={}", i, slot.getClass().getSimpleName(), slot.getItem().isEmpty(), slot.mayPlace(new net.minecraft.world.item.ItemStack(ingot)));
      }
      return;
    }
    int baseline = 0;
    for (var slot : menu.slots) {
      if (slot.getItem().is(ingot)) {
        baseline += slot.getItem().getCount();
      }
    }
    menu.setCarried(new net.minecraft.world.item.ItemStack(ingot, 5));
    menu.clicked(meltingSlot, 0, net.minecraft.world.inventory.ClickType.PICKUP, player);
    int inSlot = menu.slots.get(meltingSlot).getItem().getCount();
    int carried = menu.getCarried().getCount();
    if (inSlot + carried != 5 || inSlot == 0) {
      TConstruct.LOG.error("[block harness] SLOT FAIL: placed 5, slot has {} carried {}", inSlot, carried);
    } else {
      TConstruct.LOG.info("[block harness] slot place ok: slot {} carried {}", inSlot, carried);
    }
    // shift click it back out: total across menu and player inventory must stay put
    menu.clicked(meltingSlot, 0, net.minecraft.world.inventory.ClickType.QUICK_MOVE, player);
    int total = menu.getCarried().getCount();
    for (var slot : menu.slots) {
      var stack = slot.getItem();
      if (stack.is(ingot)) {
        total += stack.getCount();
      }
    }
    if (total - baseline != 5) {
      TConstruct.LOG.error("[block harness] SLOT FAIL: added 5, delta is {} (dupe or loss); baseline {}", total - baseline, baseline);
    } else {
      TConstruct.LOG.info("[block harness] SLOT PASS: added 5, delta still 5 after place and shift-click (baseline {})", baseline);
    }
    player.closeContainer();
  }

  /** Regression for the crafting station result dupe: an ingredient in the grid centre must be
   * consumed by a shift-click craft — 1.21's trimmed CraftingInput used to miss it entirely,
   * crafting forever off the same block. */
  private static void checkCraftingStation(MinecraftServer server) {
    ServerLevel level = server.overworld();
    BlockPos stationPos = ORIGIN.offset(-2, 0, -2);
    BlockState state = level.getBlockState(stationPos);
    var player = server.getPlayerList().getPlayers().get(0);
    state.useWithoutItem(level, player, new net.minecraft.world.phys.BlockHitResult(
      net.minecraft.world.phys.Vec3.atCenterOf(stationPos), net.minecraft.core.Direction.UP, stationPos, false));
    if (!(player.containerMenu instanceof slimeknights.tconstruct.tables.menu.CraftingStationContainerMenu menu)) {
      TConstruct.LOG.error("[block harness] CRAFT FAIL: station menu did not open, got {}", player.containerMenu);
      return;
    }
    var ingot = net.minecraft.world.item.Items.IRON_INGOT;
    int baseline = 0;
    for (var slot : menu.slots) {
      if (slot.getItem().is(ingot)) {
        baseline += slot.getItem().getCount();
      }
    }
    // iron block into the centre of the grid (slot 4), then shift-click the result (slot 9)
    menu.slots.get(4).set(new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.IRON_BLOCK));
    menu.clicked(9, 0, net.minecraft.world.inventory.ClickType.QUICK_MOVE, player);
    int total = 0;
    for (var slot : menu.slots) {
      if (slot.getItem().is(ingot)) {
        total += slot.getItem().getCount();
      }
    }
    boolean gridEmpty = menu.slots.get(4).getItem().isEmpty();
    if (total - baseline == 9 && gridEmpty) {
      TConstruct.LOG.info("[block harness] CRAFT PASS: one block crafted to exactly 9 ingots and was consumed");
    } else {
      TConstruct.LOG.error("[block harness] CRAFT FAIL: ingot delta {} (want 9), grid slot empty {}", total - baseline, gridEmpty);
    }

    // second craft, through the c:glass parent tag: the fuel gauge pattern with vanilla
    // glass. Dead while the parent tag was empty — the pack's "recipes don't work" report.
    var brick = TinkerSmeltery.searedBrick.get();
    var gauge = TinkerSmeltery.searedTank.get(TankType.FUEL_GAUGE).asItem();
    int gaugeBase = 0;
    for (var slot : menu.slots) {
      if (slot.getItem().is(gauge)) {
        gaugeBase += slot.getItem().getCount();
      }
    }
    for (int i = 0; i < 9; i++) {
      boolean isBrick = i == 0 || i == 2 || i == 6 || i == 8;
      menu.slots.get(i).set(new net.minecraft.world.item.ItemStack(isBrick ? brick : net.minecraft.world.item.Items.GLASS));
    }
    menu.clicked(9, 0, net.minecraft.world.inventory.ClickType.QUICK_MOVE, player);
    int gaugeTotal = 0;
    boolean gaugeGridEmpty = true;
    for (int i = 0; i < 9; i++) {
      if (!menu.slots.get(i).getItem().isEmpty()) {
        gaugeGridEmpty = false;
      }
    }
    for (var slot : menu.slots) {
      if (slot.getItem().is(gauge)) {
        gaugeTotal += slot.getItem().getCount();
      }
    }
    if (gaugeTotal - gaugeBase == 1 && gaugeGridEmpty) {
      TConstruct.LOG.info("[block harness] GAUGE PASS: seared fuel gauge crafted from vanilla glass, grid consumed");
    } else {
      TConstruct.LOG.error("[block harness] GAUGE FAIL: gauge delta {} (want 1), grid empty {}", gaugeTotal - gaugeBase, gaugeGridEmpty);
    }
    player.closeContainer();
  }

  /** Regression for the block fluid seam: a bucket emptied into a seared tank and filled back
   * out runs the fluid-handler lookup end to end through the real click path. A wrapper
   * regression here dupes buckets (hand keeps the lava bucket while the tank fills) or
   * swallows fluid (bucket empties into nothing). */
  private static void checkTankTransfer(MinecraftServer server) {
    ServerLevel level = server.overworld();
    BlockPos pos = ORIGIN.offset(3, 3, 3);
    level.setBlock(pos, TinkerSmeltery.searedTank.get(TankType.FUEL_TANK).defaultBlockState(), 3);
    var player = server.getPlayerList().getPlayers().get(0);
    // survival, so bucket exchange rules are the strict ones; creative would keep the full bucket
    player.setGameMode(net.minecraft.world.level.GameType.SURVIVAL);
    var hand = net.minecraft.world.InteractionHand.MAIN_HAND;
    var hit = new net.minecraft.world.phys.BlockHitResult(
      net.minecraft.world.phys.Vec3.atCenterOf(pos), net.minecraft.core.Direction.NORTH, pos, false);
    player.setItemInHand(hand, new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.LAVA_BUCKET));
    level.getBlockState(pos).useItemOn(player.getItemInHand(hand), level, player, hand, hit);
    if (!(level.getBlockEntity(pos) instanceof TankBlockEntity tank)) {
      TConstruct.LOG.error("[block harness] TANK FAIL: no tank block entity at {}", pos);
      return;
    }
    FluidStack afterFill = tank.getTank().getFluid();
    var held = player.getItemInHand(hand);
    if (!afterFill.getFluid().isSame(net.minecraft.world.level.material.Fluids.LAVA) || afterFill.getAmount() != 1000
        || !held.is(net.minecraft.world.item.Items.BUCKET) || held.getCount() != 1) {
      TConstruct.LOG.error("[block harness] TANK FAIL: after bucket empty, tank has {} mB of {}, hand {}",
        afterFill.getAmount(), afterFill.getFluid(), held);
      return;
    }
    // and back out with the empty bucket
    level.getBlockState(pos).useItemOn(held, level, player, hand, hit);
    FluidStack afterDrain = tank.getTank().getFluid();
    held = player.getItemInHand(hand);
    if (afterDrain.isEmpty() && held.is(net.minecraft.world.item.Items.LAVA_BUCKET) && held.getCount() == 1) {
      TConstruct.LOG.info("[block harness] TANK PASS: bucket emptied to exactly 1000 mB and filled back, no dupe either direction");
    } else {
      TConstruct.LOG.error("[block harness] TANK FAIL: after refill, tank has {} mB, hand {} x{}",
        afterDrain.getAmount(), held, held.getCount());
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
      // suspects line-up, floating with gaps so the photograph is unambiguous
      level.setBlock(ORIGIN.offset(-4, 3, 0), TinkerSmeltery.searedDrain.get().defaultBlockState(), 3);
      retexture(level, ORIGIN.offset(-2, 3, 0), TinkerSmeltery.searedDrain.get().defaultBlockState(), "minecraft:diamond_block");
      level.setBlock(ORIGIN.offset(0, 3, 0), TinkerSmeltery.searedDuct.get().defaultBlockState(), 3);
      level.setBlock(ORIGIN.offset(2, 3, 0), TinkerSmeltery.searedChute.get().defaultBlockState(), 3);
      level.setBlock(ORIGIN.offset(4, 3, 0), TinkerSmeltery.searedMelter.get().defaultBlockState(), 3);

      // In-structure variants: the same functional blocks with in_structure=true, the state a
      // formed smeltery puts them into. They must keep their textures; the report was a missing
      // texture the moment the structure formed. The player also holds a controller for the
      // first-person item transform check.
      var controller = slimeknights.tconstruct.smeltery.TinkerSmeltery.smelteryController.get().defaultBlockState();
      var inStructure = slimeknights.tconstruct.smeltery.block.controller.ControllerBlock.IN_STRUCTURE;
      var active = slimeknights.tconstruct.smeltery.block.controller.ControllerBlock.ACTIVE;
      level.setBlock(ORIGIN.offset(-4, 0, -2), controller.setValue(inStructure, true), 3);
      level.setBlock(ORIGIN.offset(-4, 0, 1), controller.setValue(inStructure, true).setValue(active, true), 3);
      level.setBlock(ORIGIN.offset(-3, 0, 1), TinkerSmeltery.searedDrain.get().defaultBlockState().setValue(slimeknights.tconstruct.smeltery.block.component.SearedBlock.IN_STRUCTURE, true), 3);
      if (!server.getPlayerList().getPlayers().isEmpty()) {
        server.getPlayerList().getPlayers().get(0).setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND,
          new net.minecraft.world.item.ItemStack(slimeknights.tconstruct.smeltery.TinkerSmeltery.smelteryController.get()));
      }

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

      // a soul glass block to stand in: the transparent-overlay tag must swap vanilla's opaque
      // in-a-block wall for the see-through version when the harness steps inside later
      level.setBlock(ORIGIN.offset(0, 0, 5), TinkerCommons.soulGlass.get().defaultBlockState(), 3);
      level.setBlock(ORIGIN.offset(0, 1, 5), TinkerCommons.soulGlass.get().defaultBlockState(), 3);

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
