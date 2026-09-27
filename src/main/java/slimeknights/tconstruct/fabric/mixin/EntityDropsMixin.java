package slimeknights.tconstruct.fabric.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import slimeknights.tconstruct.fabric.events.DeathDropsTracker;

/**
 * Event-bridge mixin recording item entities spawned during a living entity's
 * {@code dropAllDeathLoot} (started/finished by {@link LivingEntityEventsMixin}) so the
 * LivingDropsEvent can carry them. All drop overloads funnel into
 * {@code spawnAtLocation(ItemStack, float)}; the return is null for empty stacks and on the
 * client, both skipped.
 *
 * <p>Verified against the 1.21.1 mapped jar: public ItemEntity spawnAtLocation(ItemStack, float);
 * spawnAtLocation(ItemStack) delegates here with 0.0F.
 */
@Mixin(Entity.class)
public class EntityDropsMixin {

  @Inject(method = "spawnAtLocation(Lnet/minecraft/world/item/ItemStack;F)Lnet/minecraft/world/entity/item/ItemEntity;", at = @At("RETURN"))
  private void tconstruct$recordDeathDrop(ItemStack stack, float yOffset, CallbackInfoReturnable<ItemEntity> cir) {
    ItemEntity drop = cir.getReturnValue();
    if (drop != null) {
      DeathDropsTracker.record((Entity) (Object) this, drop);
    }
  }
}
