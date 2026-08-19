package slimeknights.tconstruct.tools;

import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.client.renderer.entity.ItemEntityRenderer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import slimeknights.mantle.client.ResourceColorManager;
import slimeknights.mantle.event.MinecraftForge;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.ClientEventBase;
import slimeknights.tconstruct.library.client.model.DynamicTextureLoader;
import slimeknights.tconstruct.library.client.model.TinkerItemProperties;
import slimeknights.tconstruct.library.client.model.tools.ToolModel;
import slimeknights.tconstruct.library.client.modifiers.DyedModifierModel;
import slimeknights.tconstruct.library.client.modifiers.FluidModifierModel;
import slimeknights.tconstruct.library.client.modifiers.MaterialModifierModel;
import slimeknights.tconstruct.library.client.modifiers.ModifierModelManager;
import slimeknights.tconstruct.library.client.modifiers.ModifierModelManager.ModifierModelRegistrationEvent;
import slimeknights.tconstruct.library.client.modifiers.ModifierModelMapManager;
import slimeknights.tconstruct.library.client.modifiers.NormalModifierModel;
import slimeknights.tconstruct.library.client.modifiers.PotionModifierModel;
import slimeknights.tconstruct.library.client.modifiers.TankModifierModel;
import slimeknights.tconstruct.library.client.modifiers.TrimModifierModel;
import slimeknights.tconstruct.library.client.modifiers.model.ModifierModelLoaders;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.modifiers.ModifierManager;
import slimeknights.tconstruct.library.utils.Util;
import slimeknights.tconstruct.tools.client.OverslimeModifierModel;
import slimeknights.tconstruct.tools.item.ModifierCrystalItem;
import slimeknights.tconstruct.tools.modules.ranged.ammo.SmashingModule;

import slimeknights.tconstruct.tools.client.CrystalshotRenderer;
import slimeknights.tconstruct.tools.client.FluidEffectProjectileRenderer;
import slimeknights.tconstruct.tools.client.material.ThrownShurikenRenderer;
import slimeknights.tconstruct.tools.client.material.ThrownToolRenderer;
import slimeknights.tconstruct.tools.entity.ModifiableArrow;
import slimeknights.tconstruct.tools.entity.ThrownShuriken;
import slimeknights.tconstruct.tools.entity.ThrownTool;

import java.util.function.Supplier;

/**
 * Client-side setup for the tools module.
 *
 * <p>Fabric port: Forge ran this from {@code FMLClientSetupEvent} and the {@code Register*Event}
 * hooks on the mod bus; on Fabric {@link #init()} is called from {@code TConstructClientBootstrap}.
 * Entity renderers, the modifier model registrations, the client reload listeners and the tool tint
 * handlers are live; what is left waits on later slices, as listed below.
 */
@SuppressWarnings("unused")
public class ToolClientEvents extends ClientEventBase {
  /** Registers the tool entity renderers */
  public static void init() {
    // must precede the first resource load: the block atlas definition names this source, and
    // an unknown type there makes the whole definition fail to parse
    slimeknights.tconstruct.tools.client.ShieldBannerModifierSpriteSource.register();

    EntityRendererRegistry.register(TinkerTools.indestructibleItem.get(), ItemEntityRenderer::new);
    EntityRendererRegistry.register(TinkerTools.crystalshotEntity.get(), CrystalshotRenderer::new);
    // TODO: config option for vanilla style renderer?
    EntityRendererRegistry.<ModifiableArrow>register(TinkerTools.materialArrow.get(), ThrownToolRenderer::new);
    EntityRendererRegistry.<ThrownShuriken>register(TinkerTools.thrownShuriken.get(), ThrownShurikenRenderer::new);
    EntityRendererRegistry.<ThrownTool>register(TinkerTools.thrownTool.get(), ThrownToolRenderer::new);
    EntityRendererRegistry.register(TinkerModifiers.fluidSpitEntity.get(), FluidEffectProjectileRenderer::new);
    EntityRendererRegistry.register(TinkerModifiers.fireball.get(), context -> new ThrownItemRenderer<>(context, 0.75f, true));

    // the legacy manager posts its registration event on its first reload, so this listener has to
    // be in place before the reload listeners registered below ever run
    MinecraftForge.EVENT_BUS.addListener(ModifierModelRegistrationEvent.class, ToolClientEvents::registerModifierModels);
    ModifierModelLoaders.init();
    addResourceListeners();
    itemColors();
    itemProperties();
  }

  /**
   * Registers the item properties the tool models' overrides read.
   *
   * <p>Every {@code tconstruct:tool} model names {@code tconstruct:broken} and
   * {@code tconstruct:charging} in its overrides, so without these a broken tool keeps its intact
   * texture and a drawn bow never changes pose. Vanilla resolves an unknown property to zero rather
   * than complaining, which is why this is silent when it is missing.
   */
  private static void itemProperties() {
    // rock
    toolProperties(TinkerTools.pickaxe, TinkerTools.sledgeHammer, TinkerTools.veinHammer);
    // dirt
    toolProperties(TinkerTools.mattock, TinkerTools.pickadze, TinkerTools.excavator);
    // wood
    toolProperties(TinkerTools.handAxe, TinkerTools.broadAxe);
    // scythe
    toolProperties(TinkerTools.kama, TinkerTools.scythe);
    // weapon
    toolProperties(TinkerTools.dagger, TinkerTools.sword, TinkerTools.cleaver);
    // ranged: the crossbows additionally report which ammo they hold
    TinkerItemProperties.registerCrossbowProperties(TinkerTools.crossbow.get());
    TinkerItemProperties.registerCrossbowProperties(TinkerTools.warPick.get());
    toolProperties(TinkerTools.longbow, TinkerTools.fishingRod, TinkerTools.javelin,
                   TinkerTools.arrow, TinkerTools.shuriken, TinkerTools.throwingAxe);
    // ancient
    toolProperties(TinkerTools.meltingPan, TinkerTools.battlesign, TinkerTools.swasher, TinkerTools.minotaurAxe);
    // shields
    toolProperties(TinkerTools.travelersShield, TinkerTools.plateShield);
    // armor only needs the broken texture; it has no use animation of its own
    TinkerTools.travelersGear.forEach(item -> TinkerItemProperties.registerBrokenProperty(item.asItem()));
    TinkerTools.plateArmor.forEach(item -> TinkerItemProperties.registerBrokenProperty(item.asItem()));
    TinkerTools.slimesuit.forEach(item -> TinkerItemProperties.registerBrokenProperty(item.asItem()));
    TinkerItemProperties.registerBrokenProperty(TinkerTools.slimeWings.get());
  }

  /** Registers the broken, charging, charge and cast properties on each of the given items */
  @SafeVarargs
  private static void toolProperties(Supplier<? extends ItemLike>... items) {
    for (Supplier<? extends ItemLike> item : items) {
      TinkerItemProperties.registerToolProperties(item.get());
    }
  }

  /** Listener clearing each modifier's client-side cache when resources reload */
  private static final SimpleSynchronousResourceReloadListener MODIFIER_RELOAD_LISTENER = new SimpleSynchronousResourceReloadListener() {
    @Override
    public ResourceLocation getFabricId() {
      return TConstruct.getResource("modifier_client_cache");
    }

    @Override
    public void onResourceManagerReload(ResourceManager manager) {
      ModifierManager.INSTANCE.getAllValues().forEach(modifier -> modifier.clearCache(PackType.CLIENT_RESOURCES));
    }
  };

  /**
   * Registers the module's client reload listeners.
   *
   * <p>Forge collected these through {@code RegisterClientReloadListenersEvent}; on Fabric each one
   * registers itself with {@code ResourceManagerHelper} and carries an id for reload ordering.
   * SlimeskullArmorModel, HarvestTiers, ArmorModelManager and TrimArmorTextureSupplier are not here
   * yet, as they belong to the armor slice.
   */
  private static void addResourceListeners() {
    ModifierModelManager.init();
    ModifierModelMapManager.init();
    DynamicTextureLoader.init();
    ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(MODIFIER_RELOAD_LISTENER);
  }

  /** Registers the modifier models the legacy per-tool manager can name */
  private static void registerModifierModels(ModifierModelRegistrationEvent event) {
    event.registerModel(TConstruct.getResource("normal"), NormalModifierModel.UNBAKED_INSTANCE);
    event.registerModel(TConstruct.getResource("overslime"), OverslimeModifierModel.UNBAKED_INSTANCE);
    event.registerModel(TConstruct.getResource("fluid"), FluidModifierModel.UNBAKED_INSTANCE);
    event.registerModel(TConstruct.getResource("tank"), TankModifierModel.UNBAKED_INSTANCE);
    event.registerModel(TConstruct.getResource("material"), MaterialModifierModel.UNBAKED_INSTANCE);
    event.registerModel(TConstruct.getResource("dyed"), DyedModifierModel.UNBAKED_INSTANCE);
    // trim shows up as valid on every tool, skip to reduce memory overhead on tools using the new system - add it using the new system if you want it
    event.registerModel(TConstruct.getResource("trim"), TrimModifierModel.UNBAKED_INSTANCE);
    ModifierModelMapManager.legacyBlacklist(TrimModifierModel.UNBAKED_INSTANCE);
    event.registerModel(TConstruct.getResource("potion"), PotionModifierModel.UNBAKED_INSTANCE);
    event.registerModel(TConstruct.getResource("smashing_fluid"), new FluidModifierModel.Unbaked(SmashingModule.TANK_HELPER));
  }

  /**
   * Registers the tool tint handler on every modifiable item.
   *
   * <p>Forge handed out the whole {@code ItemColors} instance through
   * {@code RegisterColorHandlersEvent.Item}; Fabric registers per item instead.
   */
  private static void itemColors() {
    // rock
    registerToolColors(TinkerTools.pickaxe, TinkerTools.sledgeHammer, TinkerTools.veinHammer);
    // dirt
    registerToolColors(TinkerTools.mattock, TinkerTools.pickadze, TinkerTools.excavator);
    // wood
    registerToolColors(TinkerTools.handAxe, TinkerTools.broadAxe);
    // scythe
    registerToolColors(TinkerTools.kama, TinkerTools.scythe);
    // weapon
    registerToolColors(TinkerTools.dagger, TinkerTools.sword, TinkerTools.cleaver);
    // bow
    registerToolColors(TinkerTools.crossbow, TinkerTools.longbow, TinkerTools.fishingRod, TinkerTools.javelin,
                       TinkerTools.arrow, TinkerTools.shuriken, TinkerTools.throwingAxe);
    // ancient
    registerToolColors(TinkerTools.meltingPan, TinkerTools.warPick, TinkerTools.battlesign, TinkerTools.swasher);
    // Forge only tinted the minotaur axe when Twilight Forest was present, because that is when it
    // registered the item. This build registers it either way, so the tint follows.
    registerToolColors(TinkerTools.minotaurAxe);
    // armor
    registerToolColors(TinkerTools.travelersShield, TinkerTools.plateShield, TinkerTools.slimeWings);
    TinkerTools.travelersGear.forEach(ToolClientEvents::registerToolColor);
    TinkerTools.plateArmor.forEach(ToolClientEvents::registerToolColor);
    TinkerTools.slimesuit.forEach(ToolClientEvents::registerToolColor);

    // modifier crystal takes the modifier's own text colour
    ColorProviderRegistry.ITEM.register((stack, index) -> {
      ModifierId modifier = ModifierCrystalItem.getModifier(stack);
      if (modifier != null) {
        return ResourceColorManager.getColor(Util.makeTranslationKey("modifier", modifier));
      }
      return -1;
    }, TinkerModifiers.modifierCrystal.get());
  }

  /** Registers the tool colour handler on each of the given item suppliers */
  @SafeVarargs
  private static void registerToolColors(Supplier<? extends ItemLike>... items) {
    for (Supplier<? extends ItemLike> item : items) {
      registerToolColor(item.get().asItem());
    }
  }

  /** Registers the tool colour handler on a single item */
  private static void registerToolColor(Item item) {
    ColorProviderRegistry.ITEM.register(ToolModel.COLOR_HANDLER, item);
  }

  // PORT: the fishing bobber renderer waits on the material render info slice. Forge registered
  //   CombatFishingHookRenderer for TinkerTools.fishingHook; the renderer needs
  //   MaterialRenderInfoLoader, library.client.armor.texture.ArmorTextureSupplier and
  //   TintedArmorTexture, none of which are ported, and 1.21 made
  //   FishingHookRenderer#stringVertex private so the line drawing has to be recreated.

  // PORT: keybinds and the input handling wait on the tool interaction slice. Forge registered
  //   HELMET_INTERACT (z) and LEGGINGS_INTERACT (i) through RegisterKeyMappingsEvent, then listened
  //   to PlayerTickEvent and MovementInputUpdateEvent to send TinkerControlPacket and to apply the
  //   double jump and interaction handling. Fabric uses KeyBindingHelper plus the client tick and
  //   input events.

  // PORT: the tool container screen and the book's fallback parts still wait. Forge did both from
  //   FMLClientSetupEvent: MenuScreens.register for TinkerTools.toolContainer with
  //   ToolContainerScreen, and AbstractMaterialContent.registerFallbackPart for the fake ingot and
  //   storage block. AbstractArmorModel.init() belongs to the armor slice.

  // PORT: particle factories wait on the particle slice. Forge registered AttackParticle.Factory as
  //   a sprite set for hammerAttackParticle, axeAttackParticle and bonkAttackParticle through
  //   RegisterParticleProvidersEvent; Fabric uses ParticleFactoryRegistry.

}
