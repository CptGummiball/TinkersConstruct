package slimeknights.mantle.network;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import slimeknights.mantle.Mantle;
import slimeknights.mantle.network.packet.ISimplePacket;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Fabric-native replacement for the Forge {@code SimpleChannel} wrapper, keeping the same
 * registration and sending surface so Mantle's and Tinkers' network classes port unchanged.
 *
 * <p>Forge multiplexed all packets over one channel with incremental ids; Fabric gives every
 * packet its own {@link CustomPacketPayload.Type}. The id is derived from the channel name
 * plus the registration index — registration order is identical on both sides (same code
 * runs), which is the same property Forge's numeric ids relied on.
 *
 * <p>Server→client receivers can only be registered from client code
 * ({@code ClientPlayNetworking} does not exist on a dedicated server), so they are collected
 * in {@link #CLIENT_HANDLERS} and drained by {@code NetworkWrapperClient} from the client
 * entrypoint.
 */
public class NetworkWrapper {

  /** S2C registrations waiting for the client entrypoint; see class javadoc. */
  public static final List<ClientRegistration<?>> CLIENT_HANDLERS = new ArrayList<>();

  private final ResourceLocation channelName;
  private final Map<Class<?>, CustomPacketPayload.Type<PacketPayload>> types = new HashMap<>();
  private int id = 0;

  public NetworkWrapper(ResourceLocation channelName) {
    this(channelName, "1");
  }

  /** The version parameter is unused: Fabric rejects mismatched payload codecs on its own. */
  public NetworkWrapper(ResourceLocation channelName, String version) {
    this.channelName = channelName;
  }

  /** Registers a new {@link ISimplePacket}. */
  public <MSG extends ISimplePacket> void registerPacket(Class<MSG> clazz, Function<RegistryFriendlyByteBuf, MSG> decoder, @Nullable NetworkDirection direction) {
    CustomPacketPayload.Type<PacketPayload> type = new CustomPacketPayload.Type<>(
      channelName.withSuffix("/" + id++));
    types.put(clazz, type);

    StreamCodec<RegistryFriendlyByteBuf, PacketPayload> codec = StreamCodec.of(
      (buffer, payload) -> {
        try {
          payload.packet().encode(buffer);
        } catch (Exception e) {
          Mantle.logger.error("Exception while encoding packet of class {}", clazz.getName(), e);
          throw e;
        }
      },
      buffer -> {
        try {
          return new PacketPayload(decoder.apply(buffer), type);
        } catch (Exception e) {
          Mantle.logger.error("Exception while decoding packet of class {}", clazz.getName(), e);
          throw e;
        }
      });

    if (direction == null || direction == NetworkDirection.PLAY_TO_SERVER) {
      PayloadTypeRegistry.playC2S().register(type, codec);
      ServerPlayNetworking.registerGlobalReceiver(type, (payload, context) ->
        payload.packet().handle(() -> new NetworkEvent.Context(context.player())));
    }
    if (direction == null || direction == NetworkDirection.PLAY_TO_CLIENT) {
      PayloadTypeRegistry.playS2C().register(type, codec);
      CLIENT_HANDLERS.add(new ClientRegistration<>(type));
    }
  }

  /* Sending packets */

  /** Wraps a packet in its payload, failing loudly on unregistered classes. */
  private PacketPayload payload(Object msg) {
    CustomPacketPayload.Type<PacketPayload> type = types.get(msg.getClass());
    if (type == null || !(msg instanceof ISimplePacket packet)) {
      throw new IllegalArgumentException("Packet class " + msg.getClass().getName() + " is not registered on channel " + channelName);
    }
    return new PacketPayload(packet, type);
  }

  /** Sends a packet to the server; client-side only by nature. */
  public void sendToServer(Object msg) {
    NetworkWrapperClient.sendToServer(payload(msg));
  }

  public void sendVanillaPacket(Packet<?> packet, Entity player) {
    if (player instanceof ServerPlayer serverPlayer) {
      serverPlayer.connection.send(packet);
    }
  }

  public void sendTo(Object msg, Player player) {
    if (player instanceof ServerPlayer serverPlayer) {
      sendTo(msg, serverPlayer);
    }
  }

  public void sendTo(Object msg, ServerPlayer player) {
    // Fake players (machine interactions) have no real connection, matching Forge's skip.
    if (!(player instanceof net.fabricmc.fabric.api.entity.FakePlayer)) {
      ServerPlayNetworking.send(player, payload(msg));
    }
  }

  /** Sends to every player tracking the chunk at the given position. */
  public void sendToClientsAround(Object msg, ServerLevel world, BlockPos position) {
    PacketPayload payload = payload(msg);
    for (ServerPlayer player : PlayerLookup.tracking(world, position)) {
      ServerPlayNetworking.send(player, payload);
    }
  }

  public void sendToTrackingAndSelf(Object msg, Entity entity) {
    PacketPayload payload = payload(msg);
    for (ServerPlayer player : PlayerLookup.tracking(entity)) {
      ServerPlayNetworking.send(player, payload);
    }
    if (entity instanceof ServerPlayer self) {
      ServerPlayNetworking.send(self, payload);
    }
  }

  public void sendToTracking(Object msg, Entity entity) {
    PacketPayload payload = payload(msg);
    for (ServerPlayer player : PlayerLookup.tracking(entity)) {
      ServerPlayNetworking.send(player, payload);
    }
  }

  /** Payload carrying one of our packets under its registered type. */
  public record PacketPayload(ISimplePacket packet, CustomPacketPayload.Type<PacketPayload> typeHolder) implements CustomPacketPayload {
    @Override
    public Type<? extends CustomPacketPayload> type() {
      return typeHolder;
    }
  }

  /** An S2C type whose client receiver still needs registering from the client entrypoint. */
  public record ClientRegistration<T extends PacketPayload>(CustomPacketPayload.Type<PacketPayload> type) {}
}
