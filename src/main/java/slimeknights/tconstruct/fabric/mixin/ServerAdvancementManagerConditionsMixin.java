package slimeknights.tconstruct.fabric.mixin;

import com.google.gson.JsonElement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ServerAdvancementManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import slimeknights.mantle.recipe.condition.ConditionalDataFilter;

import java.util.Map;

/**
 * Evaluates Forge-style advancement conditions before parsing, matching the Forge patch.
 * The recipe-unlock advancements carry the same condition guards as their recipes; without
 * this they would all load, toasting recipes the conditions disabled.
 */
@Mixin(ServerAdvancementManager.class)
public class ServerAdvancementManagerConditionsMixin {

  @Inject(method = "apply(Ljava/util/Map;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)V", at = @At("HEAD"))
  private void tconstruct$applyConditions(Map<ResourceLocation, JsonElement> entries, ResourceManager resourceManager, ProfilerFiller profiler, CallbackInfo ci) {
    ConditionalDataFilter.filterAdvancements(entries);
  }
}
