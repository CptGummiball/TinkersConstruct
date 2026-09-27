package slimeknights.tconstruct.library.utils;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import lombok.Getter;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import slimeknights.mantle.event.MinecraftForge;
import slimeknights.mantle.event.entity.player.PlayerEvent.PlayerLoggedOutEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Logic to keep track of the side of the block that was last hit
 */
public class BlockSideHitListener {
  private static final Map<UUID,Direction> HIT_FACE = new HashMap<>();
  private static final Object2IntMap<UUID> LAST_XP = new Object2IntOpenHashMap<>();
  @Getter
  private static Direction clientSideHit = Direction.UP;
  private static boolean init = false;

  /** Initializes this listener */
  public static void init() {
    if (init) {
      return;
    }
    init = true;
    // Forge's LeftClickBlock(START) maps onto Fabric's attack-block callback, which fires
    // on both sides at the start of block attacks
    AttackBlockCallback.EVENT.register((player, world, hand, pos, direction) -> {
      if (world.isClientSide()) {
        clientSideHit = direction;
      } else {
        HIT_FACE.put(player.getUUID(), direction);
      }
      return InteractionResult.PASS;
    });
    // Forge's BlockEvent.BreakEvent carried the block's XP; the break-XP bridge lands with
    // the event-layer step (PORTING.md), so LAST_XP keeps its default until then.
    MinecraftForge.EVENT_BUS.addListener(PlayerLoggedOutEvent.class, BlockSideHitListener::onLeaveServer);
  }

  /** Called when a player leaves the server to clear the face */
  private static void onLeaveServer(PlayerLoggedOutEvent event) {
    UUID uuid = event.getEntity().getUUID();
    HIT_FACE.remove(uuid);
    LAST_XP.remove(uuid);
  }

  /**
   * Gets the side this player last hit, should return correct values in most modifier hooks related to block breaking
   * @param player  Player
   * @return  Side last hit
   */
  public static Direction getSideHit(Player player) {
    if (player.level().isClientSide()) {
      return clientSideHit;
    }
    return HIT_FACE.getOrDefault(player.getUUID(), Direction.UP);
  }

  /** Gets the last XP from the break block event */
  public static int getLastXP(Player player) {
    return LAST_XP.getOrDefault(player.getUUID(), 0);
  }
}
