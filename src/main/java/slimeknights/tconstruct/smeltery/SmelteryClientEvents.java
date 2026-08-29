package slimeknights.tconstruct.smeltery;

import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import slimeknights.mantle.client.render.ChannelFluids;
import slimeknights.mantle.client.render.FaucetFluid;
import slimeknights.mantle.client.render.FluidCuboid;
import slimeknights.mantle.client.render.RenderItem;
import slimeknights.tconstruct.common.ClientEventBase;
import slimeknights.tconstruct.library.TinkerItemDisplays;
import slimeknights.tconstruct.smeltery.block.entity.CastingBlockEntity;
import slimeknights.tconstruct.smeltery.block.entity.CastingTankBlockEntity;
import slimeknights.tconstruct.smeltery.block.entity.FluidCannonBlockEntity;
import slimeknights.tconstruct.smeltery.block.entity.component.TankBlockEntity;
import slimeknights.tconstruct.smeltery.block.entity.controller.AlloyerBlockEntity;
import slimeknights.tconstruct.smeltery.block.entity.controller.HeatingStructureBlockEntity;
import slimeknights.tconstruct.smeltery.block.entity.controller.MelterBlockEntity;
import slimeknights.tconstruct.smeltery.client.render.CastingBlockEntityRenderer;
import slimeknights.tconstruct.smeltery.client.render.ChannelBlockEntityRenderer;
import slimeknights.tconstruct.smeltery.client.render.FaucetBlockEntityRenderer;
import slimeknights.tconstruct.smeltery.client.render.GaugeBlockEntityRenderer;
import slimeknights.tconstruct.smeltery.client.render.HeatingStructureBlockEntityRenderer;
import slimeknights.tconstruct.smeltery.client.render.ProxyTankBlockEntityRenderer;
import slimeknights.tconstruct.smeltery.client.render.TankBlockEntityRenderer;
import slimeknights.tconstruct.smeltery.client.render.TankInventoryBlockEntityRenderer;
import slimeknights.tconstruct.smeltery.client.screen.AlloyerScreen;
import slimeknights.tconstruct.smeltery.client.screen.HeatingStructureScreen;
import slimeknights.tconstruct.smeltery.client.screen.MelterScreen;
import slimeknights.tconstruct.smeltery.client.screen.SingleItemScreenFactory;

/**
 * Client-side setup for the smeltery module.
 *
 * <p>Fabric port: Forge ran this from {@code FMLClientSetupEvent} and the {@code Register*Event}
 * hooks on the mod bus; on Fabric {@link #init()} is called from {@code TConstructClientBootstrap}.
 * The menu screens, the block entity renderers and the reload listeners feeding them are ported;
 * the item display and model loader registrations wait on the model slice.
 */
@SuppressWarnings("unused")
public class SmelteryClientEvents extends ClientEventBase {
  /** Registers the smeltery menu screens, block entity renderers and their data */
  public static void init() {
    MenuScreens.register(TinkerSmeltery.melterContainer.get(), MelterScreen::new);
    MenuScreens.register(TinkerSmeltery.smelteryContainer.get(), HeatingStructureScreen::new);
    MenuScreens.register(TinkerSmeltery.singleItemContainer.get(), new SingleItemScreenFactory());
    MenuScreens.register(TinkerSmeltery.alloyerContainer.get(), AlloyerScreen::new);

    // resource driven data behind the renderers below; Forge registered these from
    // RegisterClientReloadListenersEvent, on Fabric each map registers itself
    TinkerItemDisplays.init();
    FluidCuboid.REGISTRY.init();
    RenderItem.STATE_REGISTRY.init();
    FaucetFluid.init();
    ChannelFluids.init();

    // block entity renderers; Forge registered these from EntityRenderersEvent.RegisterRenderers
    BlockEntityRendererRegistry.<TankBlockEntity>register(TinkerSmeltery.tank.get(), TankBlockEntityRenderer::new);
    BlockEntityRendererRegistry.<FluidCannonBlockEntity>register(TinkerSmeltery.fluidCannon.get(), context -> new TankInventoryBlockEntityRenderer<>(BlockStateProperties.FACING));
    BlockEntityRendererRegistry.register(TinkerSmeltery.faucet.get(), FaucetBlockEntityRenderer::new);
    BlockEntityRendererRegistry.register(TinkerSmeltery.channel.get(), ChannelBlockEntityRenderer::new);
    BlockEntityRendererRegistry.<CastingBlockEntity>register(TinkerSmeltery.table.get(), CastingBlockEntityRenderer::new);
    BlockEntityRendererRegistry.<CastingBlockEntity>register(TinkerSmeltery.basin.get(), CastingBlockEntityRenderer::new);
    BlockEntityRendererRegistry.register(TinkerSmeltery.proxyTank.get(), ProxyTankBlockEntityRenderer::new);
    BlockEntityRendererRegistry.<MelterBlockEntity>register(TinkerSmeltery.melter.get(), context -> new TankInventoryBlockEntityRenderer<>(BlockStateProperties.HORIZONTAL_FACING));
    BlockEntityRendererRegistry.<AlloyerBlockEntity>register(TinkerSmeltery.alloyer.get(), TankBlockEntityRenderer::new);
    BlockEntityRendererProvider<HeatingStructureBlockEntity> heatingStructure = HeatingStructureBlockEntityRenderer::new;
    BlockEntityRendererRegistry.register(TinkerSmeltery.smeltery.get(), heatingStructure);
    BlockEntityRendererRegistry.register(TinkerSmeltery.foundry.get(), heatingStructure);
    BlockEntityRendererRegistry.<CastingTankBlockEntity>register(TinkerSmeltery.castingTank.get(), context -> new TankInventoryBlockEntityRenderer<>(BlockStateProperties.HORIZONTAL_FACING));
    // upstream never registered the gauge renderer, though it ships the block_fluids data for the
    // obsidian gauge; without this the gauge shows nothing, as its fluid comes from a neighbour
    // tank and so cannot come from the block model
    BlockEntityRendererRegistry.register(TinkerSmeltery.gauge.get(), GaugeBlockEntityRenderer::new);
  }

  // PORT: ToolModel.registerSmallTool for MELTER, CASTING_BASIN and CASTING_TABLE, plus the
  //   "tank" and "fluid_texture" geometry loaders, belong to the model slice.
}
