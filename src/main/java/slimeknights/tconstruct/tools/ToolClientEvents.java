package slimeknights.tconstruct.tools;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.entity.ItemEntityRenderer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import org.lwjgl.glfw.GLFW;
import slimeknights.mantle.client.ResourceColorManager;
import slimeknights.mantle.client.SafeClientAccess;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.mantle.event.MinecraftForge;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.ClientEventBase;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.common.network.TinkerNetwork;
import slimeknights.tconstruct.fabric.client.ClientReloadListeners;
import slimeknights.tconstruct.library.client.armor.ArmorModelManager;
import slimeknights.tconstruct.library.client.armor.TinkerArmorRenderer;
import slimeknights.tconstruct.library.client.armor.texture.ArmorTextureLoaders;
import slimeknights.tconstruct.library.client.armor.texture.TrimArmorTextureSupplier;
import slimeknights.tconstruct.library.client.model.DynamicTextureLoader;
import slimeknights.tconstruct.library.client.particle.AttackParticle;
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
import slimeknights.tconstruct.library.modifiers.modules.technical.ArmorStatModule;
import slimeknights.tconstruct.library.tools.capability.TinkerDataKeys;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.library.modifiers.ModifierManager;
import slimeknights.tconstruct.library.utils.HarvestTiers;
import slimeknights.tconstruct.library.utils.Util;
import slimeknights.tconstruct.tools.client.OverslimeModifierModel;
import slimeknights.tconstruct.tools.item.ModifierCrystalItem;
import slimeknights.tconstruct.tools.logic.DoubleJumpHandler;
import slimeknights.tconstruct.tools.logic.InteractionHandler;
import slimeknights.tconstruct.tools.network.TinkerControlPacket;
import slimeknights.tconstruct.tools.modules.ranged.ammo.SmashingModule;

import slimeknights.tconstruct.shared.TinkerAttributes;
import slimeknights.tconstruct.shared.TinkerEffects;
import slimeknights.tconstruct.tools.client.CrystalshotRenderer;
import slimeknights.tconstruct.tools.client.ModifierClientEvents;
import slimeknights.tconstruct.tools.client.ToolContainerScreen;
import slimeknights.tconstruct.tools.client.ToolRenderEvents;
import slimeknights.tconstruct.tools.client.SlimeskullArmorModel;
import slimeknights.tconstruct.tools.client.material.CombatFishingHookRenderer;
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
    EntityRendererRegistry.register(TinkerTools.fishingHook.get(), CombatFishingHookRenderer::new);

    // armor: the layer types a model can name, then a renderer per item that names a model
    ArmorTextureLoaders.init();
    TinkerArmorRenderer.init();

    ModifierClientEvents.init();
    ToolRenderEvents.init();
    particleFactories();
    keyBindings();
    MenuScreens.register(TinkerTools.toolContainer.get(), ToolContainerScreen::new);

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
   */
  private static void addResourceListeners() {
    ModifierModelManager.init();
    ModifierModelMapManager.init();
    DynamicTextureLoader.init();
    ArmorModelManager.init();
    ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(MODIFIER_RELOAD_LISTENER);
    ClientReloadListeners.register(TConstruct.getResource("slimeskull_models"), SlimeskullArmorModel.RELOAD_LISTENER);
    ClientReloadListeners.register(TConstruct.getResource("harvest_tiers"), HarvestTiers.RELOAD_LISTENER);
    ClientReloadListeners.register(TConstruct.getResource("armor_trim_cache"), TrimArmorTextureSupplier.CACHE_INVALIDATOR);
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

  /** The three attack particles, all drawn from a sprite set */
  private static void particleFactories() {
    ParticleFactoryRegistry registry = ParticleFactoryRegistry.getInstance();
    registry.register(TinkerTools.hammerAttackParticle.get(), AttackParticle.Factory::new);
    registry.register(TinkerTools.axeAttackParticle.get(), AttackParticle.Factory::new);
    registry.register(TinkerTools.bonkAttackParticle.get(), AttackParticle.Factory::new);
  }


  /* Key bindings and the input they drive */

  /** Keybinding for interacting using a helmet */
  private static KeyMapping HELMET_INTERACT;
  /** Keybinding for interacting using leggings */
  private static KeyMapping LEGGINGS_INTERACT;

  /** If true, we were jumping last tick */
  private static boolean wasJumping = false;
  /** If true, we were interacting with helmet last tick */
  private static boolean wasHelmetInteracting = false;
  /** If true, we were interacting with leggings last tick */
  private static boolean wasLeggingsInteracting = false;

  /**
   * Registers the two armour interaction keys and the tick that reads them.
   *
   * <p>Fabric port: Forge registered the mappings through {@code RegisterKeyMappingsEvent} and read
   * them from {@code PlayerTickEvent}; {@code KeyConflictContext.IN_GAME} has no Fabric counterpart,
   * so the in-game check is the tick callback only firing with a level loaded.
   */
  private static void keyBindings() {
    HELMET_INTERACT = KeyBindingHelper.registerKeyBinding(new KeyMapping(
      TConstruct.makeTranslationKey("key", "helmet_interact"), InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_Z, "key.categories.tconstruct"));
    LEGGINGS_INTERACT = KeyBindingHelper.registerKeyBinding(new KeyMapping(
      TConstruct.makeTranslationKey("key", "leggings_interact"), InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_I, "key.categories.tconstruct"));
    ClientTickEvents.START_CLIENT_TICK.register(ToolClientEvents::handleKeyBindings);
  }

  /** Called on client tick to handle keybinding presses */
  private static void handleKeyBindings(Minecraft minecraft) {
    LocalPlayer player = minecraft.player;
    if (player == null || player.isSpectator()) {
      return;
    }

    // jumping in mid air for double jump
    // ensure we pressed the key since the last tick, holding should not use all your jumps at once
    boolean isJumping = minecraft.options.keyJump.isDown();
    if (!wasJumping && isJumping) {
      if (TinkerEffects.antigravity.get().antigravityJump(player)) {
        TinkerNetwork.getInstance().sendToServer(TinkerControlPacket.ANTIGRAVITY_JUMP);
      } else if (DoubleJumpHandler.extraJump(player)) {
        TinkerNetwork.getInstance().sendToServer(TinkerControlPacket.DOUBLE_JUMP);
      }
    }
    wasJumping = isJumping;

    // helmet interaction
    boolean isHelmetInteracting = HELMET_INTERACT.isDown();
    if (!wasHelmetInteracting && isHelmetInteracting) {
      TooltipKey key = SafeClientAccess.getTooltipKey();
      if (InteractionHandler.startArmorInteract(player, EquipmentSlot.HEAD, key)) {
        TinkerNetwork.getInstance().sendToServer(TinkerControlPacket.getStartHelmetInteract(key));
      }
    }
    if (wasHelmetInteracting && !isHelmetInteracting) {
      if (InteractionHandler.stopArmorInteract(player, EquipmentSlot.HEAD)) {
        TinkerNetwork.getInstance().sendToServer(TinkerControlPacket.STOP_HELMET_INTERACT);
      }
    }

    // leggings interaction
    boolean isLeggingsInteract = LEGGINGS_INTERACT.isDown();
    if (!wasLeggingsInteracting && isLeggingsInteract) {
      TooltipKey key = SafeClientAccess.getTooltipKey();
      if (InteractionHandler.startArmorInteract(player, EquipmentSlot.LEGS, key)) {
        TinkerNetwork.getInstance().sendToServer(TinkerControlPacket.getStartLeggingsInteract(key));
      }
    }
    if (wasLeggingsInteracting && !isLeggingsInteract) {
      if (InteractionHandler.stopArmorInteract(player, EquipmentSlot.LEGS)) {
        TinkerNetwork.getInstance().sendToServer(TinkerControlPacket.STOP_LEGGINGS_INTERACT);
      }
    }

    wasHelmetInteracting = isHelmetInteracting;
    wasLeggingsInteracting = isLeggingsInteract;
  }

  /**
   * Scales movement back up by the use item speed stat, undoing part of vanilla's 20% slowdown.
   *
   * <p>Called from {@code LocalPlayerMovementMixin} at the point Forge fired
   * {@code MovementInputUpdateEvent}.
   */
  public static void applyUseItemSpeed(Player player, Input input) {
    if (player.isUsingItem() && !player.isPassenger()) {
      ItemStack using = player.getUseItem();
      // start with the attribute
      double speed = player.getAttributeValue(TinkerAttributes.USE_ITEM_SPEED);
      // start by calculating tool stat, not an attribute to ensure both hands get their say
      if (using.is(TinkerTags.Items.HELD)) {
        ToolStack tool = ToolStack.from(using);
        speed += tool.getStats().get(ToolStats.USE_ITEM_SPEED) - ToolStats.USE_ITEM_SPEED.getDefaultValue();
      }
      // next, add in deprecated key bonus
      speed = Mth.clamp(speed + ArmorStatModule.getStat(player, TinkerDataKeys.USE_ITEM_SPEED), 0, 1);
      // update speed, note if the armor stat is 0 and the held tool is not tinkers this is a no-op effectively
      // multiply by 5 to cancel out the vanilla 20%
      input.leftImpulse *= (float)(speed * 5);
      input.forwardImpulse *= (float)(speed * 5);
    }
  }

  // PORT: the book's fallback parts still wait on the book slice. Forge called
  //   AbstractMaterialContent.registerFallbackPart from FMLClientSetupEvent for the fake ingot and
  //   storage block the material pages show.

}
