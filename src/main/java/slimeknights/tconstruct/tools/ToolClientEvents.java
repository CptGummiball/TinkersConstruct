package slimeknights.tconstruct.tools;

import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.renderer.entity.ItemEntityRenderer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import slimeknights.tconstruct.common.ClientEventBase;
import slimeknights.tconstruct.tools.client.CrystalshotRenderer;
import slimeknights.tconstruct.tools.client.FluidEffectProjectileRenderer;
import slimeknights.tconstruct.tools.client.material.ThrownShurikenRenderer;
import slimeknights.tconstruct.tools.client.material.ThrownToolRenderer;
import slimeknights.tconstruct.tools.entity.ModifiableArrow;
import slimeknights.tconstruct.tools.entity.ThrownShuriken;
import slimeknights.tconstruct.tools.entity.ThrownTool;

/**
 * Client-side setup for the tools module.
 *
 * <p>Fabric port: Forge ran this from {@code FMLClientSetupEvent} and the {@code Register*Event}
 * hooks on the mod bus; on Fabric {@link #init()} is called from {@code TConstructClientBootstrap}.
 * Only the entity renderers are ported so far, the rest of the module's client setup waits on later
 * slices as listed below.
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
  }

  // PORT: the fishing bobber renderer waits on the material render info slice. Forge registered
  //   CombatFishingHookRenderer for TinkerTools.fishingHook; the renderer needs
  //   MaterialRenderInfoLoader, library.client.armor.texture.ArmorTextureSupplier and
  //   TintedArmorTexture, none of which are ported, and 1.21 made
  //   FishingHookRenderer#stringVertex private so the line drawing has to be recreated.

  // PORT: model loaders wait on the model slice. Forge registered, through RegisterGeometryLoaders:
  //   "material" (MaterialModel), "tool" (ToolModel) and "material_block" (MaterialBlockModel);
  //   TinkerModelLoaders holds the Fabric side of those ids.

  // PORT: modifier models wait on the model slice. Forge registered, through
  //   ModifierModelRegistrationEvent: normal, overslime, fluid, tank, material, dyed, trim
  //   (also legacy blacklisted), potion and smashing_fluid.

  // PORT: client reload listeners wait on the model and armor slices. Forge registered, through
  //   RegisterClientReloadListenersEvent: ModifierModelManager, ModifierModelMapManager,
  //   MaterialTooltipCache, DynamicTextureLoader, a listener clearing the modifier caches,
  //   SlimeskullArmorModel.RELOAD_LISTENER, HarvestTiers.RELOAD_LISTENER, ArmorModelManager,
  //   TrimArmorTextureSupplier.CACHE_INVALIDATOR and ShieldBannerModifierSpriteSource.

  // PORT: keybinds and the input handling wait on the tool interaction slice. Forge registered
  //   HELMET_INTERACT (z) and LEGGINGS_INTERACT (i) through RegisterKeyMappingsEvent, then listened
  //   to PlayerTickEvent and MovementInputUpdateEvent to send TinkerControlPacket and to apply the
  //   double jump and interaction handling. Fabric uses KeyBindingHelper plus the client tick and
  //   input events.

  // PORT: item properties, the tool container screen and the book's fallback parts wait on the
  //   model slice. Forge did all three from FMLClientSetupEvent: MenuScreens.register for
  //   TinkerTools.toolContainer with ToolContainerScreen, AbstractMaterialContent.registerFallbackPart
  //   for the fake ingot and storage block, and TinkerItemProperties.registerToolProperties /
  //   registerCrossbowProperties / registerBrokenProperty across every tool and armor piece.
  //   AbstractArmorModel.init() belongs to the armor slice.

  // PORT: particle factories wait on the particle slice. Forge registered AttackParticle.Factory as
  //   a sprite set for hammerAttackParticle, axeAttackParticle and bonkAttackParticle through
  //   RegisterParticleProvidersEvent; Fabric uses ParticleFactoryRegistry.

  // PORT: item colours wait on the colour slice. Forge registered, through
  //   RegisterColorHandlersEvent.Item: the modifier crystal tint and ToolModel.registerItemColors
  //   for every tool; Fabric uses ColorProviderRegistry.ITEM.
}
