package slimeknights.mantle.network;

import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import slimeknights.mantle.Mantle;
import slimeknights.mantle.fluid.transfer.FluidContainerTransferManager;
import slimeknights.mantle.fluid.transfer.FluidContainerTransferPacket;
import slimeknights.mantle.network.packet.SwingArmPacket;

/** Mantle's own network channel. */
public class MantleNetwork {

  /** Network instance */
  public static final NetworkWrapper INSTANCE = new NetworkWrapper(Mantle.getResource("network"));

  /**
   * Registers packets into this network.
   *
   * <p>Reduced from the Forge original: the five lectern/book packets belong to the book
   * module (phase 5 client work) and register there once it is ported.
   */
  public static void registerPackets() {
    INSTANCE.registerPacket(FluidContainerTransferPacket.class, FluidContainerTransferPacket::new, NetworkDirection.PLAY_TO_CLIENT);
    INSTANCE.registerPacket(SwingArmPacket.class, SwingArmPacket::new, NetworkDirection.PLAY_TO_CLIENT);

    // Forge synced container items on OnDatapackSyncEvent; the Fabric counterpart is the
    // join event. Datapack reloads resync automatically because the reload listener runs
    // before players are notified of the new data.
    ServerPlayConnectionEvents.JOIN.register((handler, sender, server) ->
      INSTANCE.sendTo(new FluidContainerTransferPacket(FluidContainerTransferManager.INSTANCE.getContainerItems()), handler.getPlayer()));
  }
}
