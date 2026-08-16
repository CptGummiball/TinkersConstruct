package slimeknights.tconstruct.common.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;
import slimeknights.mantle.network.NetworkDirection;
import slimeknights.mantle.network.NetworkWrapper;
import slimeknights.tconstruct.TConstruct;

import javax.annotation.Nullable;

/**
 * Base network class for all tinkers logic
 * <p>
 * In general, if you need to send packets you should use your own network class
 *
 * <p>Port note: the Forge build registered all ~28 packets here in one block. Most of those
 * classes belong to modules that port later (tables, smeltery, tools), so each module now
 * registers its own packets from its bootstrap — the registration index stays deterministic
 * because bootstrap order is fixed. The packets registered here are only the ones whose
 * classes exist already.
 */
public class TinkerNetwork extends NetworkWrapper {

  private static TinkerNetwork instance = null;

  private TinkerNetwork() {
    super(TConstruct.getResource("network"));
  }

  /** Gets the instance of the network */
  public static TinkerNetwork getInstance() {
    if (instance == null) {
      throw new IllegalStateException("Attempt to call network getInstance before network is setup");
    }
    return instance;
  }

  /**
   * Called during mod construction to setup the network
   */
  public static void setup() {
    if (instance != null) {
      return;
    }
    instance = new TinkerNetwork();

    // Packets register with their owning module's bootstrap as each module is ported:
    // shared/gadgets/tables/tools/modifiers/smeltery. See the class javadoc.
    instance.registerPacket(SyncPersistentDataPacket.class, SyncPersistentDataPacket::new, NetworkDirection.PLAY_TO_CLIENT);
  }

  /**
   * Sends a vanilla packet to the given player
   * @param player  Player
   * @param packet  Packet
   */
  public void sendVanillaPacket(Entity player, Packet<?> packet) {
    if (player instanceof ServerPlayer serverPlayer) {
      serverPlayer.connection.send(packet);
    }
  }

  /**
   * Same as {@link #sendToClientsAround(Object, ServerLevel, BlockPos)}, but checks that the world is a serverworld
   * @param msg       Packet to send
   * @param world     World instance
   * @param position  Target position
   */
  public void sendToClientsAround(Object msg, @Nullable LevelAccessor world, BlockPos position) {
    if (world instanceof ServerLevel server) {
      sendToClientsAround(msg, server, position);
    }
  }

  /**
   * Sends a packet to the whole player list
   * @param targetedPlayer  Main player to target, if null uses whole list
   * @param playerList      Player list to use if main player is null
   * @param msg             Message to send
   */
  public void sendToPlayerList(@Nullable ServerPlayer targetedPlayer, PlayerList playerList, Object msg) {
    if (targetedPlayer != null) {
      sendTo(msg, targetedPlayer);
    } else {
      for (ServerPlayer player : playerList.getPlayers()) {
        sendTo(msg, player);
      }
    }
  }
}
