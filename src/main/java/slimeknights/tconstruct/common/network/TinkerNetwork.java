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
import slimeknights.tconstruct.library.materials.definition.UpdateMaterialsPacket;
import slimeknights.tconstruct.library.materials.stats.UpdateMaterialStatsPacket;
import slimeknights.tconstruct.library.materials.traits.UpdateMaterialTraitsPacket;
import slimeknights.tconstruct.library.modifiers.UpdateModifiersPacket;
import slimeknights.tconstruct.library.modifiers.fluid.UpdateFluidEffectsPacket;
import slimeknights.tconstruct.library.tools.definition.UpdateToolDefinitionDataPacket;
import slimeknights.tconstruct.library.tools.layout.UpdateTinkerSlotLayoutsPacket;
import slimeknights.tconstruct.smeltery.network.ChannelFlowPacket;
import slimeknights.tconstruct.smeltery.network.FaucetActivationPacket;
import slimeknights.tconstruct.smeltery.network.FluidUpdatePacket;
import slimeknights.tconstruct.smeltery.network.SmelteryFluidClickedPacket;
import slimeknights.tconstruct.smeltery.network.SmelteryTankUpdatePacket;
import slimeknights.tconstruct.smeltery.network.StructureErrorPositionPacket;
import slimeknights.tconstruct.smeltery.network.StructureUpdatePacket;
import slimeknights.tconstruct.tables.network.StationTabPacket;
import slimeknights.tconstruct.tables.network.TinkerStationRenamePacket;
import slimeknights.tconstruct.tables.network.TinkerStationSelectionPacket;
import slimeknights.tconstruct.tables.network.UpdateCraftingRecipePacket;
import slimeknights.tconstruct.tables.network.UpdateStationScreenPacket;
import slimeknights.tconstruct.tables.network.UpdateTinkerStationRecipePacket;
import slimeknights.tconstruct.tools.network.EntityMovementChangePacket;
import slimeknights.tconstruct.tools.network.InteractWithAirPacket;
import slimeknights.tconstruct.tools.network.PushBlockRowPacket;
import slimeknights.tconstruct.tools.network.SyncProjectileModifiersPacket;
import slimeknights.tconstruct.tools.network.TinkerControlPacket;
import slimeknights.tconstruct.tools.network.ToolContainerFluidUpdatePacket;

import javax.annotation.Nullable;

/**
 * Base network class for all tinkers logic
 * <p>
 * In general, if you need to send packets you should use your own network class
 *
 * <p>Port note: registration is one block, as the Forge build had it. An earlier plan to let each
 * module register its own was never carried out, and the gap was invisible until a client actually
 * joined a world: an unregistered packet throws on send, and the very first one the server sends is
 * the modifier sync, so the join failed outright. One list is easier to check against the packet
 * classes than several.
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

    // shared
    instance.registerPacket(SyncPersistentDataPacket.class, SyncPersistentDataPacket::new, NetworkDirection.PLAY_TO_CLIENT);
    instance.registerPacket(InventorySlotSyncPacket.class, InventorySlotSyncPacket::new, NetworkDirection.PLAY_TO_CLIENT);
    instance.registerPacket(UpdateNeighborsPacket.class, UpdateNeighborsPacket::new, NetworkDirection.PLAY_TO_CLIENT);

    // datapack sync: without these a client joining a world has no materials, modifiers or tool
    // definitions, and the join itself fails on the first packet the manager tries to send
    instance.registerPacket(UpdateMaterialsPacket.class, UpdateMaterialsPacket::new, NetworkDirection.PLAY_TO_CLIENT);
    instance.registerPacket(UpdateMaterialStatsPacket.class, UpdateMaterialStatsPacket::new, NetworkDirection.PLAY_TO_CLIENT);
    instance.registerPacket(UpdateMaterialTraitsPacket.class, UpdateMaterialTraitsPacket::new, NetworkDirection.PLAY_TO_CLIENT);
    instance.registerPacket(UpdateModifiersPacket.class, UpdateModifiersPacket::new, NetworkDirection.PLAY_TO_CLIENT);
    instance.registerPacket(UpdateToolDefinitionDataPacket.class, UpdateToolDefinitionDataPacket::new, NetworkDirection.PLAY_TO_CLIENT);
    instance.registerPacket(UpdateTinkerSlotLayoutsPacket.class, UpdateTinkerSlotLayoutsPacket::new, NetworkDirection.PLAY_TO_CLIENT);
    instance.registerPacket(UpdateFluidEffectsPacket.class, UpdateFluidEffectsPacket::decode, NetworkDirection.PLAY_TO_CLIENT);
    instance.registerPacket(slimeknights.tconstruct.shared.network.GeneratePartTexturesPacket.class, slimeknights.tconstruct.shared.network.GeneratePartTexturesPacket::new, NetworkDirection.PLAY_TO_CLIENT);

    // tables
    instance.registerPacket(StationTabPacket.class, StationTabPacket::new, NetworkDirection.PLAY_TO_SERVER);
    instance.registerPacket(TinkerStationRenamePacket.class, TinkerStationRenamePacket::new, NetworkDirection.PLAY_TO_SERVER);
    instance.registerPacket(TinkerStationSelectionPacket.class, TinkerStationSelectionPacket::new, NetworkDirection.PLAY_TO_SERVER);
    instance.registerPacket(UpdateCraftingRecipePacket.class, UpdateCraftingRecipePacket::new, NetworkDirection.PLAY_TO_CLIENT);
    instance.registerPacket(UpdateTinkerStationRecipePacket.class, UpdateTinkerStationRecipePacket::new, NetworkDirection.PLAY_TO_CLIENT);
    // a signal with no payload, so the decoder hands back the singleton
    instance.registerPacket(UpdateStationScreenPacket.class, buffer -> UpdateStationScreenPacket.INSTANCE, NetworkDirection.PLAY_TO_CLIENT);

    // smeltery
    instance.registerPacket(FluidUpdatePacket.class, FluidUpdatePacket::new, NetworkDirection.PLAY_TO_CLIENT);
    instance.registerPacket(FaucetActivationPacket.class, FaucetActivationPacket::new, NetworkDirection.PLAY_TO_CLIENT);
    instance.registerPacket(ChannelFlowPacket.class, ChannelFlowPacket::new, NetworkDirection.PLAY_TO_CLIENT);
    instance.registerPacket(SmelteryTankUpdatePacket.class, SmelteryTankUpdatePacket::new, NetworkDirection.PLAY_TO_CLIENT);
    instance.registerPacket(StructureUpdatePacket.class, StructureUpdatePacket::new, NetworkDirection.PLAY_TO_CLIENT);
    instance.registerPacket(StructureErrorPositionPacket.class, StructureErrorPositionPacket::new, NetworkDirection.PLAY_TO_CLIENT);
    instance.registerPacket(SmelteryFluidClickedPacket.class, SmelteryFluidClickedPacket::new, NetworkDirection.PLAY_TO_SERVER);

    // tools
    instance.registerPacket(EntityMovementChangePacket.class, EntityMovementChangePacket::new, NetworkDirection.PLAY_TO_CLIENT);
    instance.registerPacket(SyncProjectileModifiersPacket.class, SyncProjectileModifiersPacket::new, NetworkDirection.PLAY_TO_CLIENT);
    instance.registerPacket(PushBlockRowPacket.class, PushBlockRowPacket::new, NetworkDirection.PLAY_TO_CLIENT);
    instance.registerPacket(ToolContainerFluidUpdatePacket.class, ToolContainerFluidUpdatePacket::new, NetworkDirection.PLAY_TO_CLIENT);
    // these two are enum singletons; their values ride the buffer rather than a constructor
    instance.registerPacket(InteractWithAirPacket.class, InteractWithAirPacket::read, NetworkDirection.PLAY_TO_SERVER);
    instance.registerPacket(TinkerControlPacket.class, buffer -> buffer.readEnum(TinkerControlPacket.class), NetworkDirection.PLAY_TO_SERVER);

    // PORT: the part-texture generation packet stays with the datagen phase, being the only one
    // whose handler is a datagen command rather than gameplay.
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
