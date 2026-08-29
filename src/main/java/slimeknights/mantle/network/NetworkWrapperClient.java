package slimeknights.mantle.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import slimeknights.mantle.network.NetworkWrapper.PacketPayload;

/**
 * Client half of {@link NetworkWrapper}.
 *
 * <p>{@code ClientPlayNetworking} does not exist on a dedicated server, so everything
 * touching it lives here. This class is only ever loaded from client paths — the client
 * entrypoint calls {@link #init()}, and {@code NetworkWrapper.sendToServer} resolves this
 * class lazily on first use, which only happens on the client.
 */
public class NetworkWrapperClient {

  /** Registers all pending server→client receivers. Called from the client entrypoint. */
  public static void init() {
    for (NetworkWrapper.ClientRegistration<?> registration : NetworkWrapper.CLIENT_HANDLERS) {
      register(registration.type());
    }
    NetworkWrapper.CLIENT_HANDLERS.clear();
  }

  private static void register(CustomPacketPayload.Type<PacketPayload> type) {
    ClientPlayNetworking.registerGlobalReceiver(type, (payload, context) ->
      // Server→client: there is no sender player.
      payload.packet().handle(() -> new NetworkEvent.Context(null)));
  }

  static void sendToServer(PacketPayload payload) {
    ClientPlayNetworking.send(payload);
  }
}
