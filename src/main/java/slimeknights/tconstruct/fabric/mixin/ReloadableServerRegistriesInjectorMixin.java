package slimeknights.tconstruct.fabric.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.server.RegistryLayer;
import net.minecraft.server.ReloadableServerRegistries;
import net.minecraft.server.packs.resources.ResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import slimeknights.mantle.loot.LootTableInjector;

import java.util.concurrent.CompletableFuture;

/**
 * Applies mantle's loot table injections once the loot registries finish loading. Forge did
 * this through {@code LootTableLoadEvent}; in 1.21 loot tables are a datapack registry baked
 * here, so the injector patches the parsed tables right after the future completes, before
 * anything rolls them.
 */
@Mixin(ReloadableServerRegistries.class)
public class ReloadableServerRegistriesInjectorMixin {

  @ModifyReturnValue(method = "reload", at = @At("RETURN"))
  private static CompletableFuture<LayeredRegistryAccess<RegistryLayer>> tconstruct$injectLoot(CompletableFuture<LayeredRegistryAccess<RegistryLayer>> future, @Local(argsOnly = true) ResourceManager resourceManager) {
    return future.thenApply(layered -> {
      LootTableInjector.apply(layered.compositeAccess(), resourceManager);
      return layered;
    });
  }
}
