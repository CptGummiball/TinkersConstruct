package slimeknights.tconstruct.fabric.mixin;

import net.minecraft.commands.Commands;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.tags.TagManager;
import net.minecraft.world.flag.FeatureFlagSet;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import slimeknights.mantle.recipe.condition.DataConditionContext;

/**
 * Publishes the datapack load's tag manager as the condition context, standing in for the
 * Forge patch that handed reload listeners an {@code ICondition.IContext}.
 */
@Mixin(ReloadableServerResources.class)
public class ReloadableServerResourcesMixin {
  @Shadow @Final private TagManager tagManager;

  @Inject(method = "<init>", at = @At("TAIL"))
  private void tconstruct$publishConditionContext(RegistryAccess.Frozen registryAccess, FeatureFlagSet enabledFeatures, Commands.CommandSelection commandSelection, int functionCompilationLevel, CallbackInfo ci) {
    DataConditionContext.setup(this.tagManager);
  }
}
