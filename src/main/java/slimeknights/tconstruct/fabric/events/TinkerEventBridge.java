package slimeknights.tconstruct.fabric.events;

import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.BlockHitResult;
import slimeknights.mantle.event.MinecraftForge;
import slimeknights.mantle.event.entity.player.AttackEntityEvent;
import slimeknights.mantle.event.entity.player.PlayerEvent;
import slimeknights.mantle.event.entity.player.PlayerInteractEvent;

/**
 * Wires the Fabric interaction callbacks onto the shim event bus, translating cancellation
 * back into the callbacks' return contract. Events without a Fabric callback (damage
 * pipeline, jumps, knockback, break speed, ...) come from the event-bridge mixins instead;
 * this class is only the Fabric-API side of the bridge.
 */
public final class TinkerEventBridge {
  private TinkerEventBridge() {}

  private static boolean initialized = false;

  public static void init() {
    if (initialized) {
      return;
    }
    initialized = true;

    // right click block -> RightClickBlock; a canceling listener supplies the result
    UseBlockCallback.EVENT.register((player, level, hand, hit) -> {
      if (player.isSpectator()) {
        return InteractionResult.PASS;
      }
      PlayerInteractEvent.RightClickBlock event = new PlayerInteractEvent.RightClickBlock(player, hand, hit.getBlockPos(), hit);
      MinecraftForge.EVENT_BUS.post(event);
      if (event.isCanceled()) {
        return toCallbackResult(event.getCancellationResult(), level.isClientSide());
      }
      return InteractionResult.PASS;
    });

    // right click entity -> EntityInteract
    UseEntityCallback.EVENT.register((player, level, hand, target, hit) -> {
      // Fabric fires this variant also for the targeted-position path; Forge's event is once per interact
      if (player.isSpectator() || hit != null) {
        return InteractionResult.PASS;
      }
      PlayerInteractEvent.EntityInteract event = new PlayerInteractEvent.EntityInteract(player, hand, target);
      MinecraftForge.EVENT_BUS.post(event);
      if (event.isCanceled()) {
        return toCallbackResult(event.getCancellationResult(), level.isClientSide());
      }
      return InteractionResult.PASS;
    });

    // left click block -> LeftClickBlock (Fabric only reports the click start)
    AttackBlockCallback.EVENT.register((player, level, hand, pos, direction) -> {
      if (player.isSpectator()) {
        return InteractionResult.PASS;
      }
      PlayerInteractEvent.LeftClickBlock event = new PlayerInteractEvent.LeftClickBlock(player, hand, pos, direction, PlayerInteractEvent.LeftClickBlock.Action.START);
      MinecraftForge.EVENT_BUS.post(event);
      if (event.isCanceled()) {
        return toCallbackResult(event.getCancellationResult(), level.isClientSide());
      }
      return InteractionResult.PASS;
    });

    // attack entity -> AttackEntityEvent
    AttackEntityCallback.EVENT.register((player, level, hand, target, hit) -> {
      AttackEntityEvent event = new AttackEntityEvent(player, target);
      MinecraftForge.EVENT_BUS.post(event);
      if (event.isCanceled()) {
        // Forge's cancel skips the attack without a swing result; SUCCESS stops vanilla here
        return InteractionResult.SUCCESS;
      }
      return InteractionResult.PASS;
    });

    // start tracking -> projectile modifier sync and similar listeners
    net.fabricmc.fabric.api.networking.v1.EntityTrackingEvents.START_TRACKING.register((trackedEntity, player) -> {
      if (trackedEntity instanceof Projectile || trackedEntity instanceof Player) {
        MinecraftForge.EVENT_BUS.post(new PlayerEvent.StartTracking(player, trackedEntity));
      }
    });
  }

  /** Posts a left-click-empty event; called by the client's interact-with-air packet handler */
  public static void onLeftClickEmpty(ServerPlayer player) {
    MinecraftForge.EVENT_BUS.post(new PlayerInteractEvent.LeftClickEmpty(player));
  }

  /** Maps a Forge cancellation result onto the Fabric callback contract */
  private static InteractionResult toCallbackResult(InteractionResult result, boolean clientSide) {
    // consuming results stop vanilla processing; PASS-with-cancel means "do nothing", which
    // Fabric expresses as FAIL (stop processing without a swing)
    if (result.consumesAction()) {
      return result;
    }
    return InteractionResult.FAIL;
  }
}
