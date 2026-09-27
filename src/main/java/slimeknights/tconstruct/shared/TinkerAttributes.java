package slimeknights.tconstruct.shared;

import fuzs.forgeconfigapiport.fabric.api.forge.v4.ForgeModConfigEvents;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.fml.config.ModConfig;
import slimeknights.mantle.registration.deferred.AttributeDeferredRegister;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.config.Config;

/**
 * Tinkers' custom entity attributes.
 *
 * <p>Fabric port notes: registration is eager, so touching this class registers everything.
 * Forge's {@code EntityAttributeModificationEvent} (which attached these attributes to entity
 * types) is replaced by the {@code DefaultAttributesMixin}, which merges them into every
 * living entity's attribute supplier on first lookup — see
 * {@link slimeknights.tconstruct.fabric.TinkerAttributeInjector}.
 */
public class TinkerAttributes {
  private TinkerAttributes() {}

  private static final AttributeDeferredRegister ATTRIBUTES = new AttributeDeferredRegister(TConstruct.MOD_ID);

  // booleans
  /** If true, the entity will bounce. Used to implement slime boots */
  public static final Holder<Attribute> BOUNCY = ATTRIBUTES.registerPercent("generic.bouncy", 0f, true);

  // stat replacements
  /** Changes the speed debuff percentage when the player moves while using an item */
  public static final Holder<Attribute> USE_ITEM_SPEED = ATTRIBUTES.registerPercent("player.use_item_speed", 0.2f, true);
  /** Changes the speed debuff when the player moves while using an item */
  public static final Holder<Attribute> PROTECTION_CAP = ATTRIBUTES.register("generic.protection_cap", 0.8, 0, 0.95f, true);
  /** Percentage boost to critical hits for any airborne attacker, used for {@link slimeknights.tconstruct.tools.data.ModifierIds#dragonborn} */
  public static final Holder<Attribute> CRITICAL_DAMAGE = ATTRIBUTES.register("player.critical_damage", 1.5f, 0, 100, false);

  // stat bonuses
  /** Bonus jump height in blocks */
  public static final Holder<Attribute> JUMP_BOOST = ATTRIBUTES.register("generic.jump_boost", 0, 0, 100, true);
  /** Distance you can safely fall without damage */
  public static final Holder<Attribute> SAFE_FALL_DISTANCE = ATTRIBUTES.register("generic.safe_fall_distance", 0, -10, 100, true);
  /** Number of jumps the player may perform, used by the double jump modifier. */
  public static final Holder<Attribute> JUMP_COUNT = ATTRIBUTES.register("player.jump_count", 1, 1, 100, true);

  // stat multipliers
  /** Multiplier for knockback this entity takes. Similar to {@link net.minecraft.world.entity.ai.attributes.Attributes#KNOCKBACK_RESISTANCE} but can be used to increase knockback */
  public static final Holder<Attribute> KNOCKBACK_MULTIPLIER = ATTRIBUTES.registerMultiplier("generic.knockback_multiplier", true);
  /** Player modifier data key for mining speed multiplier as an additive percentage boost on mining speed. Used for armor haste. */
  public static final Holder<Attribute> MINING_SPEED_MULTIPLIER = ATTRIBUTES.registerMultiplier("player.mining_speed_multiplier", true);
  /** Attribute for experience from all sources */
  public static final Holder<Attribute> EXPERIENCE_MULTIPLIER = ATTRIBUTES.registerMultiplier("player.experience_multiplier", false);
  /** Percentage boost to damage while crouching, used by {@link slimeknights.tconstruct.tools.data.ModifierIds#shulking} */
  public static final Holder<Attribute> CROUCH_DAMAGE_MULTIPLIER = ATTRIBUTES.registerMultiplier("generic.crouch_damage_multiplier", false);
  // effect durations
  /** Percentage boost to positive potion effects */
  public static final Holder<Attribute> GOOD_EFFECT_DURATION = ATTRIBUTES.registerMultiplier("generic.good_effect_duration_multiplier", false);
  /** Percentage boost to negative potion effects, used for {@link slimeknights.tconstruct.tools.data.ModifierIds#magicProtection} */
  public static final Holder<Attribute> BAD_EFFECT_DURATION = ATTRIBUTES.registerMultiplier("generic.bad_effect_duration_multiplier", false);
  /**
   * Swim speed multiplier, replacing Forge's {@code forge:swim_speed} (1.21 vanilla has no
   * swim speed attribute). Read by the entity travel hook in the event layer.
   */
  public static final Holder<Attribute> SWIM_SPEED = ATTRIBUTES.registerMultiplier("generic.swim_speed", true);

  /**
   * Called from the bootstrap to apply config-driven attribute tweaks; replaces the Forge
   * common-setup hook. Config values are not available during mod init on Fabric, so the
   * tweak runs when Forge Config API Port loads the common config.
   */
  public static void init() {
    ForgeModConfigEvents.loading(TConstruct.MOD_ID).register(config -> {
      // make knockback resistance syncable, as we need that info clientside
      if (config.getType() == ModConfig.Type.COMMON
          && Config.COMMON.syncKnockbackResistance.get() && Attributes.KNOCKBACK_RESISTANCE.value() instanceof RangedAttribute ranged) {
        ranged.setSyncable(true);
      }
    });
  }
}
