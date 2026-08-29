package slimeknights.tconstruct.fabric.mixin;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.DefaultAttributes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import slimeknights.tconstruct.fabric.TinkerAttributeInjector;

/**
 * Merges Tinkers' custom attributes into every living entity's attribute supplier; replaces
 * Forge's {@code EntityAttributeModificationEvent}. Hooking the lookup (rather than mutating
 * the static supplier map) keeps it order-independent for entities other mods register later.
 */
@Mixin(DefaultAttributes.class)
public class DefaultAttributesMixin {

  @Inject(method = "getSupplier", at = @At("RETURN"), cancellable = true)
  private static void tconstruct$injectAttributes(EntityType<? extends LivingEntity> type, CallbackInfoReturnable<AttributeSupplier> cir) {
    cir.setReturnValue(TinkerAttributeInjector.inject(type, cir.getReturnValue()));
  }
}
