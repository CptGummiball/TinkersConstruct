package slimeknights.mantle.network;

import net.minecraft.server.level.ServerPlayer;

import javax.annotation.Nullable;

/**
 * Shim for Forge's {@code net.minecraftforge.network.NetworkEvent}, keeping the
 * {@code NetworkEvent.Context} shape that all packet handlers are written against
 * (26 call sites in TConstruct).
 *
 * <p>Only the context survives — the event itself was Forge bus plumbing with no Fabric
 * counterpart.
 */
public final class NetworkEvent {

  private NetworkEvent() {}

  /** What a packet handler may ask about the delivery. */
  public static class Context {

    @Nullable
    private final ServerPlayer sender;

    public Context(@Nullable ServerPlayer sender) {
      this.sender = sender;
    }

    /** Sending player for client→server packets; null for server→client. */
    @Nullable
    public ServerPlayer getSender() {
      return sender;
    }

    /**
     * Runs work on the game thread.
     *
     * <p>On Forge, packet handlers ran on netty threads and this rescheduled. Fabric's play
     * networking already delivers on the game thread, so the work runs immediately — which
     * also means {@code IThreadsafePacket} semantics hold with no extra hop.
     */
    public void enqueueWork(Runnable work) {
      work.run();
    }

    /** Kept for source compatibility; Fabric tracks nothing equivalent. */
    public void setPacketHandled(boolean handled) {}
  }
}
