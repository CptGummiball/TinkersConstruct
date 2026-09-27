package slimeknights.tconstruct.library.utils;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import javax.annotation.Nullable;
import slimeknights.mantle.network.packet.ISimplePacket;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.common.network.TinkerNetwork;

/** Helpers for a few JSON related tasks */
public class JsonUtils {
  private JsonUtils() {}

  /** Called when the player logs in to send packets */
  /**
   * Sends packets when datapack data reloads.
   *
   * <p>Forge fired OnDatapackSyncEvent with an optional target player (join vs reload);
   * on Fabric the two call sites are explicit: player join passes that player, a /reload
   * passes null and the packets go to everyone.
   */
  public static void syncPackets(MinecraftServer server, @Nullable ServerPlayer targetedPlayer, ISimplePacket... packets) {
    if (targetedPlayer != null) {
      for (ISimplePacket packet : packets) {
        TinkerNetwork.getInstance().sendTo(packet, targetedPlayer);
      }
    } else {
      for (ServerPlayer player : server.getPlayerList().getPlayers()) {
        for (ISimplePacket packet : packets) {
          TinkerNetwork.getInstance().sendTo(packet, player);
        }
      }
    }
  }

  /** Creates a JSON object with the given key set to a resource location */
  public static JsonObject withLocation(String key, ResourceLocation value) {
    JsonObject json = new JsonObject();
    json.addProperty(key, value.toString());
    return json;
  }

  /** Creates a JSON object with the given type set, makes using {@link slimeknights.mantle.data.gson.GenericRegisteredSerializer} easier */
  public static JsonObject withType(ResourceLocation type) {
    return withLocation("type", type);
  }
}
