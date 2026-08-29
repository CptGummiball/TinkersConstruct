package slimeknights.mantle.network;

/**
 * Shim for Forge's {@code net.minecraftforge.network.NetworkDirection}: which way a packet
 * is allowed to travel. Only the two play directions exist — the login handshake pair had
 * no users, and Fabric handles configuration-phase sync itself.
 */
public enum NetworkDirection {
  PLAY_TO_CLIENT,
  PLAY_TO_SERVER
}
