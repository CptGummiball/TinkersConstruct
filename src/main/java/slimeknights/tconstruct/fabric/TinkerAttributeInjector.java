package slimeknights.tconstruct.fabric;

import com.google.common.collect.ImmutableMap;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import slimeknights.tconstruct.shared.TinkerAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Replaces Forge's {@code EntityAttributeModificationEvent}: merges Tinkers' custom attributes
 * into every living entity's {@link AttributeSupplier}.
 *
 * <p>Called from {@code DefaultAttributesMixin} on {@code DefaultAttributes.getSupplier}, so it
 * catches vanilla and modded entity types alike, regardless of registration order. Results are
 * cached per original supplier; suppliers are static singletons, so the cache stays tiny.
 */
public class TinkerAttributeInjector {
  private TinkerAttributeInjector() {}

  /** Attributes every living entity receives */
  private static final List<Holder<Attribute>> ALL_LIVING = List.of(
    TinkerAttributes.BOUNCY,
    TinkerAttributes.PROTECTION_CAP,
    TinkerAttributes.JUMP_BOOST,
    TinkerAttributes.SAFE_FALL_DISTANCE,
    TinkerAttributes.CROUCH_DAMAGE_MULTIPLIER,
    TinkerAttributes.KNOCKBACK_MULTIPLIER,
    TinkerAttributes.GOOD_EFFECT_DURATION,
    TinkerAttributes.BAD_EFFECT_DURATION,
    TinkerAttributes.SWIM_SPEED);
  /** Attributes only players receive */
  private static final List<Holder<Attribute>> PLAYER_ONLY = List.of(
    TinkerAttributes.USE_ITEM_SPEED,
    TinkerAttributes.CRITICAL_DAMAGE,
    TinkerAttributes.MINING_SPEED_MULTIPLIER,
    TinkerAttributes.EXPERIENCE_MULTIPLIER,
    TinkerAttributes.JUMP_COUNT);

  /** Cache of merged suppliers, keyed by the original (suppliers have identity semantics) */
  private static final Map<AttributeSupplier,AttributeSupplier> CACHE = new ConcurrentHashMap<>();

  /** Merges Tinkers' attributes into the given supplier */
  public static AttributeSupplier inject(EntityType<? extends LivingEntity> type, AttributeSupplier original) {
    boolean player = type == EntityType.PLAYER;
    return CACHE.computeIfAbsent(original, o -> merge(player, o));
  }

  /** Builds a new supplier containing the original plus Tinkers' attributes */
  private static AttributeSupplier merge(boolean player, AttributeSupplier original) {
    // instances field + constructor are opened by the access widener
    Map<Holder<Attribute>,AttributeInstance> instances = new HashMap<>(original.instances);
    ALL_LIVING.forEach(attribute -> add(instances, attribute));
    if (player) {
      PLAYER_ONLY.forEach(attribute -> add(instances, attribute));
    }
    return new AttributeSupplier(ImmutableMap.copyOf(instances));
  }

  /** Adds a single attribute at its default value, keeping any existing entry */
  private static void add(Map<Holder<Attribute>,AttributeInstance> instances, Holder<Attribute> attribute) {
    // the AttributeInstance constructor initializes the base value to the attribute default,
    // matching what the Forge event's add(type, attribute) did
    instances.computeIfAbsent(attribute, holder -> new AttributeInstance(holder, instance -> {}));
  }
}
