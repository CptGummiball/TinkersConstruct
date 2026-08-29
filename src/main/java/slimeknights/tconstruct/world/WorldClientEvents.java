package slimeknights.tconstruct.world;

import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.client.model.PiglinHeadModel;
import net.minecraft.client.model.SkullModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import slimeknights.mantle.util.Lazy;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.ClientEventBase;
import slimeknights.tconstruct.library.client.particle.SlimeParticle;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.shared.block.SlimeType;
import slimeknights.tconstruct.tools.client.SlimeskullArmorModel;
import slimeknights.tconstruct.tools.data.material.MaterialIds;
import slimeknights.tconstruct.world.block.FoliageType;
import slimeknights.tconstruct.world.client.DragonSkullModel;
import slimeknights.tconstruct.world.client.SkullModelHelper;
import slimeknights.tconstruct.world.client.SlimeColorReloadListener;
import slimeknights.tconstruct.world.client.SlimeColorizer;
import slimeknights.tconstruct.world.client.TerracubeRenderer;
import slimeknights.tconstruct.world.client.TinkerSlimeRenderer;

import javax.annotation.Nullable;
import java.util.function.Supplier;

/**
 * Client-side setup for the world module: slime entity renderers, the plant colours, the slime
 * particles and the mob head models.
 *
 * <p>Fabric port: Forge spread this across {@code RegisterClientReloadListenersEvent},
 * {@code RegisterParticleProvidersEvent}, three {@code EntityRenderersEvent}s,
 * {@code RegisterColorHandlersEvent} and {@code FMLClientSetupEvent}. Each has a direct Fabric
 * counterpart except the skull models, which have none — see {@link #registerSkullModels()}.
 */
@SuppressWarnings("unused")
public class WorldClientEvents extends ClientEventBase {
  /** Called from the client entrypoint */
  public static void init() {
    addResourceListeners();
    registerParticleFactories();
    registerLayerDefinitions();
    registerSkullModels();
    registerRenderers();
    registerHeadTextures();
    registerBlockColorHandlers();
    registerItemColorHandlers();
  }

  /** One listener per foliage type, each reading that type's colour map */
  private static void addResourceListeners() {
    for (FoliageType type : FoliageType.values()) {
      SlimeColorReloadListener listener = new SlimeColorReloadListener(type);
      ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new IdentifiableResourceReloadListener() {
        @Override
        public ResourceLocation getFabricId() {
          return TConstruct.getResource("slime_color_" + type.getSerializedName());
        }

        @Override
        public java.util.concurrent.CompletableFuture<Void> reload(PreparationBarrier stage, ResourceManager manager, net.minecraft.util.profiling.ProfilerFiller prepareProfiler, net.minecraft.util.profiling.ProfilerFiller applyProfiler, java.util.concurrent.Executor prepareExecutor, java.util.concurrent.Executor applyExecutor) {
          return listener.reload(stage, manager, prepareProfiler, applyProfiler, prepareExecutor, applyExecutor);
        }
      });
    }
  }

  private static void registerParticleFactories() {
    ParticleFactoryRegistry registry = ParticleFactoryRegistry.getInstance();
    registry.register(TinkerWorld.skySlimeParticle.get(), new SlimeParticle.Factory(SlimeType.SKY));
    registry.register(TinkerWorld.enderSlimeParticle.get(), new SlimeParticle.Factory(SlimeType.ENDER));
    registry.register(TinkerWorld.terracubeParticle.get(), new SlimeParticle.Factory(Items.CLAY_BALL));
  }

  private static void registerLayerDefinitions() {
    // TODO: do we really need a separate copy of each head for each mob, or can we reuse them?
    Supplier<LayerDefinition> normalHead = Lazy.of(SkullModel::createMobHeadLayer);
    Supplier<LayerDefinition> customHead = Lazy.of(() -> SkullModelHelper.createHeadLayer(0, 0, 32, 16));
    Supplier<LayerDefinition> headOverlayCustom = Lazy.of(() -> SkullModelHelper.createHeadHatLayer(0, 16, 32, 32));
    registerLayerDefinition(TinkerHeadType.BLAZE, normalHead);
    registerLayerDefinition(TinkerHeadType.ENDERMAN, customHead);
    registerLayerDefinition(TinkerHeadType.STRAY, headOverlayCustom);

    // zombie
    registerLayerDefinition(TinkerHeadType.HUSK, Lazy.of(() -> SkullModelHelper.createHeadLayer(0, 0, 64, 64)));
    registerLayerDefinition(TinkerHeadType.DROWNED, headOverlayCustom);

    // spiders
    Supplier<LayerDefinition> spiderHead = Lazy.of(() -> SkullModelHelper.createHeadLayer(32, 4, 64, 32));
    registerLayerDefinition(TinkerHeadType.SPIDER, spiderHead);
    registerLayerDefinition(TinkerHeadType.CAVE_SPIDER, spiderHead);

    // piglin
    Supplier<LayerDefinition> piglinHead = Lazy.of(() -> LayerDefinition.create(PiglinHeadModel.createHeadModel(), 64, 64));
    registerLayerDefinition(TinkerHeadType.PIGLIN_BRUTE, piglinHead);
    registerLayerDefinition(TinkerHeadType.ZOMBIFIED_PIGLIN, piglinHead);

    // crafted
    registerLayerDefinition(TinkerHeadType.VENOMBONE, customHead);
    registerLayerDefinition(TinkerHeadType.BLAZING_BONE, customHead);
    registerLayerDefinition(TinkerHeadType.NECRONIUM, customHead);
    EntityModelLayerRegistry.registerModelLayer(SkullModelHelper.FLUID_CANNON, headOverlayCustom::get);
  }

  /**
   * Makes the mod's skull types renderable.
   *
   * <p>Forge fired {@code EntityRenderersEvent.CreateSkullModels}; vanilla builds the map in
   * {@code SkullBlockRenderer.createSkullRenderers} from a hardcoded list, so
   * {@code SkullBlockRendererMixin} appends to the returned map instead. This only records what to
   * append — the mixin cannot bake models before the entity model set exists.
   */
  private static void registerSkullModels() {
    SkullModelHelper.HEAD_LAYERS.forEach((type, layer) -> slimeknights.tconstruct.fabric.client.SkullModelRegistry.register(
      type, layer, type.isPiglin()));
  }

  private static void registerRenderers() {
    EntityRendererRegistry.register(TinkerWorld.skySlimeEntity.get(), TinkerSlimeRenderer.SKY_SLIME_FACTORY);
    EntityRendererRegistry.register(TinkerWorld.enderSlimeEntity.get(), TinkerSlimeRenderer.ENDER_SLIME_FACTORY);
    EntityRendererRegistry.register(TinkerWorld.terracubeEntity.get(), TerracubeRenderer::new);
  }

  /** Skull textures, and the head models the slimeskull draws under a helmet */
  private static void registerHeadTextures() {
    registerHeadModel(TinkerHeadType.BLAZE, MaterialIds.blaze, ResourceLocation.parse("textures/entity/blaze.png"));
    registerHeadModel(TinkerHeadType.ENDERMAN, MaterialIds.enderPearl, TConstruct.getResource("textures/entity/skull/enderman.png"));
    SlimeskullArmorModel.registerHeadModel(MaterialIds.dragonScale, modelSet -> new DragonSkullModel(modelSet.bakeLayer(ModelLayers.DRAGON_SKULL)), ResourceLocation.parse("textures/entity/enderdragon/dragon.png"));
    SlimeskullArmorModel.registerHeadModel(MaterialIds.glass, ModelLayers.CREEPER_HEAD, ResourceLocation.parse("textures/entity/creeper/creeper.png"));
    // skeleton
    SlimeskullArmorModel.registerHeadModel(MaterialIds.bone, ModelLayers.SKELETON_SKULL, ResourceLocation.parse("textures/entity/skeleton/skeleton.png"));
    SlimeskullArmorModel.registerHeadModel(MaterialIds.necroticBone, ModelLayers.WITHER_SKELETON_SKULL, ResourceLocation.parse("textures/entity/skeleton/wither_skeleton.png"));
    registerHeadModel(TinkerHeadType.STRAY, MaterialIds.ice, TConstruct.getResource("textures/entity/skull/stray.png"));
    // zombies
    SlimeskullArmorModel.registerHeadModel(MaterialIds.leather, ModelLayers.ZOMBIE_HEAD, ResourceLocation.parse("textures/entity/zombie/zombie.png"));
    registerHeadModel(TinkerHeadType.HUSK, MaterialIds.iron, ResourceLocation.parse("textures/entity/zombie/husk.png"));
    registerHeadModel(TinkerHeadType.DROWNED, MaterialIds.copper, TConstruct.getResource("textures/entity/skull/drowned.png"));
    // spider
    registerHeadModel(TinkerHeadType.SPIDER, MaterialIds.string, ResourceLocation.parse("textures/entity/spider/spider.png"));
    registerHeadModel(TinkerHeadType.CAVE_SPIDER, MaterialIds.darkthread, ResourceLocation.parse("textures/entity/spider/cave_spider.png"));
    // piglins
    SlimeskullArmorModel.registerPiglinHeadModel(MaterialIds.gold, ModelLayers.PIGLIN_HEAD, ResourceLocation.parse("textures/entity/piglin/piglin.png"));
    registerPiglinHeadModel(TinkerHeadType.PIGLIN_BRUTE, MaterialIds.roseGold, ResourceLocation.parse("textures/entity/piglin/piglin_brute.png"));
    registerPiglinHeadModel(TinkerHeadType.ZOMBIFIED_PIGLIN, MaterialIds.pigIron, ResourceLocation.parse("textures/entity/piglin/zombified_piglin.png"));
    // crafted
    registerHeadModel(TinkerHeadType.VENOMBONE,    MaterialIds.venombone,   TConstruct.getResource("textures/entity/skull/venombone.png"));
    registerHeadModel(TinkerHeadType.BLAZING_BONE, MaterialIds.blazingBone, TConstruct.getResource("textures/entity/skull/blazing_bone.png"));
    registerHeadModel(TinkerHeadType.NECRONIUM,    MaterialIds.necronium,   TConstruct.getResource("textures/entity/skull/necronium.png"));
    SlimeskullArmorModel.registerHeadModel(MaterialIds.knightmetal, SkullModelHelper.FLUID_CANNON, TConstruct.getResource("textures/entity/skull/fluid_cannon.png"));
  }

  private static void registerBlockColorHandlers() {
    // slime plants - blocks
    for (FoliageType type : FoliageType.values()) {
      registerBlocks((state, reader, pos, index) -> getSlimeColorByPos(pos, type, null),
        TinkerWorld.vanillaSlimeGrass.get(type), TinkerWorld.earthSlimeGrass.get(type), TinkerWorld.skySlimeGrass.get(type),
        TinkerWorld.enderSlimeGrass.get(type), TinkerWorld.ichorSlimeGrass.get(type));
      registerBlocks((state, reader, pos, index) -> getSlimeColorByPos(pos, type, SlimeColorizer.LOOP_OFFSET),
        TinkerWorld.slimeLeaves.get(type));
      registerBlocks((state, reader, pos, index) -> getSlimeColorByPos(pos, type, null),
        TinkerWorld.slimeFern.get(type), TinkerWorld.slimeTallGrass.get(type), TinkerWorld.pottedSlimeFern.get(type));
    }

    // vines
    registerBlocks((state, reader, pos, index) -> getSlimeColorByPos(pos, FoliageType.SKY, SlimeColorizer.LOOP_OFFSET),
      TinkerWorld.skySlimeVine.get());
    registerBlocks((state, reader, pos, index) -> getSlimeColorByPos(pos, FoliageType.ENDER, SlimeColorizer.LOOP_OFFSET),
      TinkerWorld.enderSlimeVine.get());
  }

  /** Registers a block colour provider; Forge's event took the varargs, Fabric's registry does too */
  private static void registerBlocks(net.minecraft.client.color.block.BlockColor color, Block... blocks) {
    ColorProviderRegistry.BLOCK.register(color, blocks);
  }

  private static void registerItemColorHandlers() {
    // slime grass items
    registerBlockItemColorAlias(TinkerWorld.vanillaSlimeGrass);
    registerBlockItemColorAlias(TinkerWorld.earthSlimeGrass);
    registerBlockItemColorAlias(TinkerWorld.skySlimeGrass);
    registerBlockItemColorAlias(TinkerWorld.enderSlimeGrass);
    registerBlockItemColorAlias(TinkerWorld.ichorSlimeGrass);
    // plant items
    registerBlockItemColorAlias(TinkerWorld.slimeLeaves);
    registerBlockItemColorAlias(TinkerWorld.slimeFern);
    registerBlockItemColorAlias(TinkerWorld.slimeTallGrass);
    registerBlockItemColorAlias(TinkerWorld.skySlimeVine);
    registerBlockItemColorAlias(TinkerWorld.enderSlimeVine);
  }

  /**
   * Block colors for a slime type
   * @param pos   Block position
   * @param type  Slime foliage color
   * @param add   Offset position
   * @return  Color for the given position, or the default if position is null
   */
  private static int getSlimeColorByPos(@Nullable BlockPos pos, FoliageType type, @Nullable BlockPos add) {
    if (pos == null) {
      return type.getColor();
    }
    if (add != null) {
      pos = pos.offset(add);
    }

    return SlimeColorizer.getColorForPos(pos, type);
  }

  /** Registers a skull with the entity renderer and the slimeskull renderer */
  private static void registerHeadModel(TinkerHeadType skull, MaterialId materialId, ResourceLocation texture) {
    SkullBlockRenderer.SKIN_BY_TYPE.put(skull, texture);
    SlimeskullArmorModel.registerHeadModel(materialId, SkullModelHelper.HEAD_LAYERS.get(skull), texture);
  }

  /** Registers a skull with the entity renderer and the slimeskull renderer */
  private static void registerPiglinHeadModel(TinkerHeadType skull, MaterialId materialId, ResourceLocation texture) {
    SkullBlockRenderer.SKIN_BY_TYPE.put(skull, texture);
    SlimeskullArmorModel.registerPiglinHeadModel(materialId, SkullModelHelper.HEAD_LAYERS.get(skull), texture);
  }

  /** Register a head layer definition */
  private static void registerLayerDefinition(TinkerHeadType head, Supplier<LayerDefinition> supplier) {
    ModelLayerLocation layer = SkullModelHelper.HEAD_LAYERS.get(head);
    EntityModelLayerRegistry.registerModelLayer(layer, supplier::get);
  }
}
