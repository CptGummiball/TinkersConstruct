package slimeknights.tconstruct.fabric.mixin;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import slimeknights.mantle.loot.modifier.GlobalLootManager;

import java.util.Optional;

/**
 * Applies the global loot modifiers to every table roll.
 *
 * <p>All the public {@code getRandomItems} overloads funnel through this one private
 * method, so injecting here catches block drops, entity drops and chest generation alike
 * with a single seam — matching where Forge patched.
 *
 * <p>The table identity comes from {@code randomSequence}, which 1.21 populates with the
 * table's own id; the only consumer is the {@code forge:loot_table_id} condition, which
 * simply does not match when it is absent.
 */
@Mixin(LootTable.class)
public class LootTableGlobalModifierMixin {
  @Shadow @Final private Optional<ResourceLocation> randomSequence;

  @Inject(method = "getRandomItems(Lnet/minecraft/world/level/storage/loot/LootContext;)Lit/unimi/dsi/fastutil/objects/ObjectArrayList;", at = @At("RETURN"), cancellable = true)
  private void tconstruct$applyGlobalLootModifiers(LootContext context, CallbackInfoReturnable<ObjectArrayList<ItemStack>> cir) {
    ObjectArrayList<ItemStack> loot = cir.getReturnValue();
    ObjectArrayList<ItemStack> modified = GlobalLootManager.apply(randomSequence.orElse(null), loot, context);
    if (modified != loot) {
      cir.setReturnValue(modified);
    }
  }
}
