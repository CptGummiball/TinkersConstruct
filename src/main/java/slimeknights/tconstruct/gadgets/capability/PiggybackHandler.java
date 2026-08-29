package slimeknights.tconstruct.gadgets.capability;

import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import slimeknights.tconstruct.common.network.TinkerNetwork;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

/**
 * Passenger sync tracker for the piggyback pack.
 *
 * <p>On Forge this was a capability attached to every player; it never serialized (the
 * world saves the entities, they just dismount on logout), so on Fabric a weak map of
 * last-known passenger ids replaces the whole attach ceremony. UUIDs are stored instead
 * of entities so the map values never pin their own keys.
 */
public class PiggybackHandler {
  /** Last synced passenger list per player; weak keys drop entries when players unload */
  private static final Map<Player, List<UUID>> LAST_PASSENGERS = Collections.synchronizedMap(new WeakHashMap<>());

  private PiggybackHandler() {}

  /**
   * Updates the passengers on the back, resyncing the vanilla passenger packet if they changed serverside
   */
  public static void updatePassengers(Player riddenPlayer) {
    List<UUID> current = riddenPlayer.getPassengers().stream().map(Entity::getUUID).toList();
    // tell the player itself if his riders changed serverside
    if (!current.equals(LAST_PASSENGERS.get(riddenPlayer))) {
      if (riddenPlayer instanceof ServerPlayer) {
        TinkerNetwork.getInstance().sendVanillaPacket(riddenPlayer, new ClientboundSetPassengersPacket(riddenPlayer));
      }
      LAST_PASSENGERS.put(riddenPlayer, current);
    }
  }
}
