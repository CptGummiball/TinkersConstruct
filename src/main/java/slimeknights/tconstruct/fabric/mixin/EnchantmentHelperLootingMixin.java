package slimeknights.tconstruct.fabric.mixin;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import slimeknights.mantle.event.MinecraftForge;
import slimeknights.mantle.event.entity.living.LootingLevelEvent;

/**
 * Posts the looting-level event so modifier-granted looting is visible to vanilla.
 *
 * <p>Forge had a dedicated looting hook; 1.21 made looting a data-driven enchantment read
 * through {@code getEnchantmentLevel}, which is the single funnel every drop path uses
 * (mob drops, rare drops, the enchanted-count-increase loot function). Substituting the
 * level here is what lets Tinkers' looting modifiers affect drops at all — without it
 * {@code ModifierLootingHandler} has no seam and every modifier reads zero.
 *
 * <p>The event carries no damage source: this seam is reached from loot generation, which
 * no longer threads one through. The only listener does not read it.
 */
@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperLootingMixin {

  @Inject(method = "getEnchantmentLevel(Lnet/minecraft/core/Holder;Lnet/minecraft/world/entity/LivingEntity;)I", at = @At("RETURN"), cancellable = true)
  private static void tconstruct$lootingLevel(Holder<Enchantment> enchantment, LivingEntity entity, CallbackInfoReturnable<Integer> cir) {
    if (enchantment.is(Enchantments.LOOTING)) {
      LootingLevelEvent event = new LootingLevelEvent(entity, null, cir.getReturnValue());
      MinecraftForge.EVENT_BUS.post(event);
      if (event.getLootingLevel() != cir.getReturnValue()) {
        cir.setReturnValue(event.getLootingLevel());
      }
    }
  }
}
