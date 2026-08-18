package slimeknights.tconstruct.fabric.mixin;

import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.crafting.RecipeManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import slimeknights.mantle.recipe.helper.CurrentRecipeId;

import java.util.Map;

/**
 * Publishes the recipe id around each JSON parse. 1.20 serializers received the id as a
 * parameter; 1.21 parses inline in {@code apply} (the {@code fromJson} helper is dead code
 * there) and codecs never see the id — but the mantle loadables behind most Tinkers recipes
 * declare it as a required context field. The loop reads each entry's value exactly once,
 * right before parsing it, so that read is the id seam.
 */
@Mixin(RecipeManager.class)
public class RecipeManagerIdMixin {

  @Redirect(
    method = "apply(Ljava/util/Map;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)V",
    at = @At(value = "INVOKE", target = "Ljava/util/Map$Entry;getValue()Ljava/lang/Object;"))
  private Object tconstruct$publishRecipeId(Map.Entry<?, ?> entry) {
    if (entry.getKey() instanceof net.minecraft.resources.ResourceLocation id) {
      CurrentRecipeId.set(id);
    }
    return entry.getValue();
  }

  @Inject(
    method = "apply(Ljava/util/Map;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)V",
    at = @At("RETURN"))
  private void tconstruct$clearRecipeId(Map<?, ?> entries, ResourceManager resourceManager, ProfilerFiller profiler, CallbackInfo ci) {
    CurrentRecipeId.set(null);
  }
}
