package slimeknights.tconstruct.gadgets;

import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import slimeknights.tconstruct.common.ClientEventBase;
import slimeknights.tconstruct.gadgets.client.FancyItemFrameRenderer;
import slimeknights.tconstruct.gadgets.entity.FancyItemFrameEntity;
import slimeknights.tconstruct.gadgets.entity.shuriken.FlintShurikenEntity;
import slimeknights.tconstruct.gadgets.entity.shuriken.QuartzShurikenEntity;
import slimeknights.tconstruct.tools.client.material.ThrownShurikenRenderer;

/**
 * Client-side setup for the gadgets module.
 *
 * <p>Fabric port: Forge ran this from the {@code Register*Event} hooks on the mod bus; on Fabric
 * {@link #init()} is called from {@code TConstructClientBootstrap}. The additional model
 * registration became a {@link ModelLoadingPlugin}, the renderers a
 * {@link EntityRendererRegistry} call each.
 */
@SuppressWarnings("unused")
public class GadgetClientEvents extends ClientEventBase {
  /** Registers the gadget entity renderers and the models they draw with */
  public static void init() {
    // the frame models belong to no blockstate or item, so they need registering to get baked
    ModelLoadingPlugin.register(context -> {
      context.addModels(FancyItemFrameRenderer.LOCATIONS_MODEL.values());
      context.addModels(FancyItemFrameRenderer.LOCATIONS_MODEL_MAP.values());
    });

    EntityRendererRegistry.<FancyItemFrameEntity>register(TinkerGadgets.itemFrameEntity.get(), FancyItemFrameRenderer::new);
    EntityRendererRegistry.register(TinkerGadgets.glowBallEntity.get(), ThrownItemRenderer::new);
    EntityRendererRegistry.register(TinkerGadgets.eflnEntity.get(), ThrownItemRenderer::new);
    EntityRendererRegistry.<QuartzShurikenEntity>register(TinkerGadgets.quartzShurikenEntity.get(), ThrownShurikenRenderer::new);
    EntityRendererRegistry.<FlintShurikenEntity>register(TinkerGadgets.flintShurikenEntity.get(), ThrownShurikenRenderer::new);
  }
}
