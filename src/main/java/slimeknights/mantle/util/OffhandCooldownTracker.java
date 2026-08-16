package slimeknights.mantle.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import slimeknights.mantle.Mantle;
import slimeknights.mantle.network.MantleNetwork;
import slimeknights.mantle.network.packet.SwingArmPacket;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Logic to handle offhand having its own cooldown.
 *
 * <p>Forge attached one tracker per player through {@code AttachCapabilitiesEvent} without a
 * serializer, i.e. runtime-only state. A weak identity map keyed by the player gives the same
 * contract on Fabric: one transient tracker per player instance, dying with the entity.
 */
public class OffhandCooldownTracker {
  public static final ResourceLocation KEY = Mantle.getResource("offhand_cooldown");

  /** Trackers per player; weak keys so entries die with the player instance */
  private static final Map<Player, OffhandCooldownTracker> TRACKERS =
    Collections.synchronizedMap(new WeakHashMap<>());

  /** No event wiring needed on Fabric; kept so bootstrap call sites match Forge's shape. */
  public static void init() {}

  /** Player receiving cooldowns */
  @Nullable
  private final Player player;
  /** Scale of the last cooldown */
  private int lastCooldown = 0;
  /** Time in ticks when the player can next attack for full power */
  private int attackReady = 0;

  /** Enables the cooldown tracker if above 0. Intended to be set in equipment change events, not serialized */
  private int enabled = 0;

  public OffhandCooldownTracker(@Nullable Player player) {
    this.player = player;
  }

  /** Null safe way to get the player's ticks existed */
  private int getTicksExisted() {
    if (player == null) {
      return 0;
    }
    return player.tickCount;
  }

  /** If true, the tracker is enabled despite a cooldown item not being held */
  @Deprecated(forRemoval = true)
  public boolean isEnabled() {
    return enabled > 0;
  }

  /**
   * Call this method when your item causing offhand cooldown to be needed is enabled and disabled. If multiple places call this, the tracker will automatically keep enabled until all places disable
   * @param enable  If true, enable. If false, disable
   * @deprecated No longer used, so you can just remove calls.
   */
  @Deprecated(forRemoval = true)
  public void setEnabled(boolean enable) {
    if (enable) {
      enabled++;
    } else {
      enabled--;
    }
  }

  /**
   * Applies the given amount of cooldown
   * @param cooldown  Cooldown amount
   */
  public void applyCooldown(int cooldown) {
    this.lastCooldown = cooldown;
    this.attackReady = getTicksExisted() + cooldown;
  }

  /**
   * Returns a number from 0 to 1 denoting the current cooldown amount, akin to {@link Player#getAttackStrengthScale(float)}
   * @return  number from 0 to 1, with 1 being no cooldown
   */
  public float getCooldown() {
    int ticksExisted = getTicksExisted();
    if (ticksExisted > this.attackReady || this.lastCooldown == 0) {
      return 1.0f;
    }
    return Mth.clamp((this.lastCooldown + ticksExisted - this.attackReady) / (float) this.lastCooldown, 0f, 1f);
  }

  /**
   * Checks if we can perform another attack yet.
   * This counteracts rapid attacks via click macros, in a similar way to vanilla by limiting to once every 10 ticks
   */
  public boolean isAttackReady() {
    return getTicksExisted() + this.lastCooldown > this.attackReady;
  }


  /* Helpers */

  /** Gets the tracker instance for the target entity */
  public static OffhandCooldownTracker get(Player player) {
    return TRACKERS.computeIfAbsent(player, OffhandCooldownTracker::new);
  }

  /**
   * Gets the offhand cooldown for the given player
   * @param player  Player
   * @return  Offhand cooldown
   */
  public static float getCooldown(Player player) {
    return get(player).getCooldown();
  }

  /**
   * Applies cooldown to the given player
   * @param player  Player
   * @param cooldown  Cooldown to apply
   */
  public static void applyCooldown(Player player, int cooldown) {
    get(player).applyCooldown(cooldown);
  }

  /**
   * Checks whether the given player may attack again
   * @param player  Player
   */
  public static boolean isAttackReady(Player player) {
    return get(player).isAttackReady();
  }

  /**
   * Applies cooldown using attack speed
   * @param attackSpeed   Attack speed of the held item
   * @param cooldownTime  Relative cooldown time for the given source, 20 is vanilla
   */
  public static void applyCooldown(Player player, float attackSpeed, int cooldownTime) {
    applyCooldown(player, Math.round(cooldownTime / attackSpeed));
  }

  /** Swings the entities hand without resetting cooldown */
  public static void swingHand(LivingEntity entity, InteractionHand hand, boolean updateSelf) {
    if (!entity.swinging || entity.swingTime >= entity.getCurrentSwingDuration() / 2 || entity.swingTime < 0) {
      entity.swingTime = -1;
      entity.swinging = true;
      entity.swingingArm = hand;
      if (!entity.level().isClientSide) {
        SwingArmPacket packet = new SwingArmPacket(entity, hand);
        if (updateSelf) {
          MantleNetwork.INSTANCE.sendToTrackingAndSelf(packet, entity);
        } else {
          MantleNetwork.INSTANCE.sendToTracking(packet, entity);
        }
      }
    }
  }
}
