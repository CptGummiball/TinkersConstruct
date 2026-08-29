package slimeknights.tconstruct.tools.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import slimeknights.mantle.transfer.fluid.FluidStack;
import slimeknights.mantle.network.NetworkEvent.Context;
import slimeknights.mantle.client.SafeClientAccess;
import slimeknights.mantle.network.packet.IThreadsafePacket;
import slimeknights.tconstruct.tools.menu.ToolContainerMenu;

/** Packet used when a fluid is changed inside a tool container menu */
public record ToolContainerFluidUpdatePacket(FluidStack fluid) implements IThreadsafePacket {
  public ToolContainerFluidUpdatePacket(RegistryFriendlyByteBuf buffer) {
    this(FluidStack.STREAM_CODEC.decode(buffer));
  }

  @Override
  public void encode(RegistryFriendlyByteBuf buffer) {
    FluidStack.STREAM_CODEC.encode(buffer, fluid);
  }

  @Override
  public void handleThreadsafe(Context context) {
    Player player = SafeClientAccess.getPlayer();
    if (player != null && player.containerMenu instanceof ToolContainerMenu toolMenu) {
      toolMenu.getTank().setFluid(fluid);
    }
  }
}
