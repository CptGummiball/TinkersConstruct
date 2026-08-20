package slimeknights.mantle.network;

import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import slimeknights.mantle.Mantle;
import slimeknights.mantle.fluid.transfer.FluidContainerTransferManager;
import slimeknights.mantle.fluid.transfer.FluidContainerTransferPacket;
import slimeknights.mantle.item.LecternBookItem;
import slimeknights.mantle.network.packet.OpenLecternBookPacket;
import slimeknights.mantle.network.packet.SwingArmPacket;
import slimeknights.mantle.network.packet.UpdateHeldPagePacket;
import slimeknights.mantle.network.packet.UpdateLecternPagePacket;
import slimeknights.mantle.network.packet.UpdateSavedPagePacket;

/** Mantle's own network channel. */
public class MantleNetwork {

  /** Network instance */
  public static final NetworkWrapper INSTANCE = new NetworkWrapper(Mantle.getResource("network"));

  /** Registers packets into this network. */
  public static void registerPackets() {
    INSTANCE.registerPacket(FluidContainerTransferPacket.class, FluidContainerTransferPacket::new, NetworkDirection.PLAY_TO_CLIENT);
    INSTANCE.registerPacket(SwingArmPacket.class, SwingArmPacket::new, NetworkDirection.PLAY_TO_CLIENT);

    // Book pages: the reader's place lives on the item stack, so the client tells the server where
    // it is whenever a page turns. Three packets because a book can be held, in an open container,
    // or on a lectern, and each is written back a different way.
    INSTANCE.registerPacket(UpdateHeldPagePacket.class, UpdateHeldPagePacket::new, NetworkDirection.PLAY_TO_SERVER);
    INSTANCE.registerPacket(UpdateSavedPagePacket.class, UpdateSavedPagePacket::new, NetworkDirection.PLAY_TO_SERVER);
    INSTANCE.registerPacket(UpdateLecternPagePacket.class, UpdateLecternPagePacket::new, NetworkDirection.PLAY_TO_SERVER);
    INSTANCE.registerPacket(OpenLecternBookPacket.class, OpenLecternBookPacket::new, NetworkDirection.PLAY_TO_CLIENT);

    // Lecterns holding one of our books open the book screen instead of the vanilla one.
    LecternBookItem.init();

    // Forge synced container items on OnDatapackSyncEvent; the Fabric counterpart is the
    // join event. Datapack reloads resync automatically because the reload listener runs
    // before players are notified of the new data.
    ServerPlayConnectionEvents.JOIN.register((handler, sender, server) ->
      INSTANCE.sendTo(new FluidContainerTransferPacket(FluidContainerTransferManager.INSTANCE.getContainerItems()), handler.getPlayer()));
  }
}
