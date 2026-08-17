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
}
