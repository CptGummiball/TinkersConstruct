package slimeknights.mantle.event;

import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;

/**
 * Fabric-backed stand-ins for the handful of {@code net.minecraftforge.common.ForgeHooks}
 * calls Tinkers makes. Each maps onto the Fabric event that other pack mods (FTB Chunks
 * protection, claim mods) actually listen to — the point of these hooks is interop, so
 * firing the loader-native event is the correct translation, not an approximation.
 */
public final class ForgeHooks {

  private ForgeHooks() {}

  /**
   * Fires the block-break hooks for AOE breaking.
   *
   * @return the XP to drop, or -1 when a listener cancelled the break
   */
  public static int onBlockBreakEvent(Level level, GameType gameType, ServerPlayer player, BlockPos pos) {
    if (player.blockActionRestricted(level, pos, gameType)) {
      return -1;
    }
    // Fabric's BEFORE event is what protection mods hook; false means cancelled.
    boolean allowed = PlayerBlockBreakEvents.BEFORE.invoker().beforeBlockBreak(
      level, player, pos, level.getBlockState(pos), level.getBlockEntity(pos));
    if (!allowed) {
      PlayerBlockBreakEvents.CANCELED.invoker().onBlockBreakCanceled(
        level, player, pos, level.getBlockState(pos), level.getBlockEntity(pos));
      return -1;
    }
    // Forge returned the block's XP here; Tinkers only checks for -1 and otherwise hands
    // XP dropping to the loot logic, so 0 is faithful.
    return 0;
  }

  /** Selects the projectile for a weapon, matching Forge's player-aware lookup. */
  /**
   * Mirror of Forge's living-fall hook. Returns {distance, damageMultiplier} or null to
   * cancel the fall damage entirely; the event layer will post a cancellable event here.
   */
  @Nullable
  public static float[] onLivingFall(LivingEntity entity, float distance, float damageMultiplier) {
    return new float[] {distance, damageMultiplier};
  }

  /** Crafting player context, mirroring Forge's hook; consumers read it for remainder logic */
  private static final ThreadLocal<Player> CRAFTING_PLAYER = new ThreadLocal<>();

  public static void setCraftingPlayer(@Nullable Player player) {
    CRAFTING_PLAYER.set(player);
  }

  @Nullable
  public static Player getCraftingPlayer() {
    return CRAFTING_PLAYER.get();
  }

  public static ItemStack getProjectile(LivingEntity entity, ItemStack weapon, ItemStack fallback) {
    // Vanilla's lookup already handles creative arrows and the projectile predicate;
    // Forge's version only added a hook for mods overriding ammo, which on Fabric is done
    // by overriding getProjectile on the entity.
    if (weapon.getItem() instanceof ProjectileWeaponItem) {
      ItemStack found = entity.getProjectile(weapon);
      return found.isEmpty() ? fallback : found;
    }
    return fallback;
  }

  /** Fires the use-block hook, letting protection and interaction mods respond first. */
  @Nullable
  public static InteractionResult onRightClickBlock(Player player, InteractionHand hand, BlockPos pos, BlockHitResult hitResult) {
    InteractionResult result = UseBlockCallback.EVENT.invoker().interact(player, player.level(), hand, hitResult);
    return result == InteractionResult.PASS ? null : result;
  }

  /** Fires the use-entity-at hook. */
  @Nullable
  public static InteractionResult onInteractEntityAt(Player player, Entity entity, Vec3 vec, InteractionHand hand) {
    InteractionResult result = UseEntityCallback.EVENT.invoker().interact(
      player, player.level(), hand, entity, new EntityHitResult(entity, vec.add(entity.position())));
    return result == InteractionResult.PASS ? null : result;
  }
}
