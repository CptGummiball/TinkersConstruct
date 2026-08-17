package slimeknights.tconstruct.tables.network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.RegistryFriendlyByteBuf;
import slimeknights.mantle.network.NetworkEvent.Context;
import slimeknights.mantle.network.packet.IThreadsafePacket;

public class UpdateStationScreenPacket implements IThreadsafePacket {
  public static final UpdateStationScreenPacket INSTANCE = new UpdateStationScreenPacket();

  private UpdateStationScreenPacket() {}

  @Override
  public void encode(RegistryFriendlyByteBuf packetBuffer) {}

  @Override
  public void handleThreadsafe(Context context) {
    HandleClient.handle();
  }

  /** Safely runs client side only code in a method only called on client */
  private static class HandleClient {
    private static void handle() {
      // phase 5: screen refresh returns with the client screens
    }
  }
}
