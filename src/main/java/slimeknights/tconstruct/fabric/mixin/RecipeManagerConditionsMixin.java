package slimeknights.tconstruct.fabric.mixin;

import com.google.gson.JsonElement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.crafting.RecipeManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import slimeknights.mantle.recipe.condition.ConditionalDataFilter;

import java.util.Map;

/**
 * Evaluates Forge-style recipe conditions and unwraps {@code forge:conditional} recipes
 * before the vanilla parser runs — the CraftingHelper behavior Forge patched in here.
 */
@Mixin(RecipeManager.class)
public class RecipeManagerConditionsMixin {

  @Inject(method = "apply(Ljava/util/Map;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)V", at = @At("HEAD"))
  private void tconstruct$applyConditions(Map<ResourceLocation, JsonElement> entries, ResourceManager resourceManager, ProfilerFiller profiler, CallbackInfo ci) {
    ConditionalDataFilter.filterRecipes(entries);
  }
}
