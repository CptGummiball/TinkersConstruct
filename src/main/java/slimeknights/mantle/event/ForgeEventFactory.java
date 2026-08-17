package slimeknights.mantle.event;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * Shim of the pieces of Forge's {@code ForgeEventFactory} the tables use. The crafting
 * event has no Fabric equivalent; the hook stays so the event-layer step can post one.
 */
public class ForgeEventFactory {
  private ForgeEventFactory() {}

  /** Fired after a player crafts an item in one of the tables; no-op until the event layer lands */
  public static void firePlayerCraftingEvent(Player player, ItemStack crafted, Container craftMatrix) {
    // no Fabric equivalent; kept as the hook point for the event-layer step
  }

  /**
   * Forge's bow-nock override hook; null means no override. Kept as the event-layer hook
   * point — Fabric has no equivalent event.
   */
  @javax.annotation.Nullable
  public static net.minecraft.world.InteractionResultHolder<ItemStack> onArrowNock(ItemStack bow, net.minecraft.world.level.Level level, Player player, net.minecraft.world.InteractionHand hand, boolean hasAmmo) {
    return null;
  }

  /**
   * Forge's arrow-loose hook; returns the (possibly modified) charge, negative to cancel.
   * Passthrough until the event layer lands.
   */
  public static int onArrowLoose(ItemStack bow, net.minecraft.world.level.Level level, Player player, int charge, boolean hasAmmo) {
    return charge;
  }

  /** Forge's projectile-impact cancel hook; false means not cancelled. */
  public static boolean onProjectileImpact(net.minecraft.world.entity.projectile.Projectile projectile, net.minecraft.world.phys.HitResult hitResult) {
    return false;
  }
}
