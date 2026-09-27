package slimeknights.tconstruct.fabric.mixin.client;

import net.minecraft.client.model.SkullModelBase;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.world.level.block.SkullBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import slimeknights.tconstruct.fabric.client.SkullModelRegistry;

import java.util.HashMap;
import java.util.Map;

/**
 * Adds the mod's mob heads to vanilla's skull model map.
 *
 * <p>Vanilla builds that map from a hardcoded list of its own types, so a modded
 * {@link SkullBlock.Type} renders as nothing without this. Forge had an event for it; Fabric has no
 * hook, and the returned map is immutable, so the result is copied and extended.
 */
@Mixin(SkullBlockRenderer.class)
public class SkullBlockRendererMixin {
  @Inject(method = "createSkullRenderers", at = @At("RETURN"), cancellable = true)
  private static void tconstruct$addSkullModels(EntityModelSet modelSet, CallbackInfoReturnable<Map<SkullBlock.Type,SkullModelBase>> callback) {
    Map<SkullBlock.Type,SkullModelBase> models = new HashMap<>(callback.getReturnValue());
    SkullModelRegistry.bakeInto(models, modelSet);
    callback.setReturnValue(Map.copyOf(models));
  }
}
