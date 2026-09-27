package slimeknights.mantle.data.predicate;

import slimeknights.mantle.Mantle;
import slimeknights.mantle.data.predicate.block.BlockPredicate;
import slimeknights.mantle.data.predicate.block.BlockPropertiesPredicate;
import slimeknights.mantle.data.predicate.damage.DamageSourcePredicate;
import slimeknights.mantle.data.predicate.damage.DamageTypePredicate;
import slimeknights.mantle.data.predicate.damage.SourceAttackerPredicate;
import slimeknights.mantle.data.predicate.damage.SourceMessagePredicate;
import slimeknights.mantle.data.predicate.entity.BlockAtEntityPredicate;
import slimeknights.mantle.data.predicate.entity.HasEnchantmentEntityPredicate;
import slimeknights.mantle.data.predicate.entity.HasMobEffectPredicate;
import slimeknights.mantle.data.predicate.entity.LivingEntityPredicate;
import slimeknights.mantle.data.predicate.entity.MobTypePredicate;
import slimeknights.mantle.data.predicate.fluid.FluidPredicate;
import slimeknights.mantle.data.predicate.item.ItemPredicate;

/**
 * Mantle's own named predicate loaders, exactly as upstream registered them from its
 * {@code @Mod} class during the recipe-serializer register event. The vendored port
 * dropped that mod class, but datapack JSON (the dynamic modifiers above all) speaks
 * these {@code mantle:} names, so they register here from the bootstrap instead.
 */
public final class MantlePredicates {
  private MantlePredicates() {}

  private static boolean initialized = false;

  public static void init() {
    if (initialized) {
      return;
    }
    initialized = true;

    // block predicates
    BlockPredicate.LOADER.register(Mantle.getResource("requires_tool"), BlockPredicate.REQUIRES_TOOL.getLoader());
    BlockPredicate.LOADER.register(Mantle.getResource("blocks_motion"), BlockPredicate.BLOCKS_MOTION.getLoader());
    BlockPredicate.LOADER.register(Mantle.getResource("can_be_replaced"), BlockPredicate.CAN_BE_REPLACED.getLoader());
    BlockPredicate.LOADER.register(Mantle.getResource("block_properties"), BlockPropertiesPredicate.LOADER);

    // item predicates
    ItemPredicate.LOADER.register(Mantle.getResource("has_container"), ItemPredicate.HAS_CONTAINER.getLoader());
    // PORT: upstream also registered mantle:may_have_transfer; the constant is bound to the
    // Forge item fluid capability and was not vendored. No datapack references it.

    // fluid predicates
    // PORT: upstream also registered mantle:fluid_type (FluidTypePredicate); FluidType is a
    // Forge concept the port shims per-mod, and no datapack references the predicate.
    FluidPredicate.LOADER.register(Mantle.getResource("is_source"), FluidPredicate.SOURCE.getLoader());
    FluidPredicate.LOADER.register(Mantle.getResource("has_bucket"), FluidPredicate.HAS_BUCKET.getLoader());
    FluidPredicate.LOADER.register(Mantle.getResource("lighter_than_air"), FluidPredicate.LIGHTER_THAN_AIR.getLoader());

    // living entity predicates
    LivingEntityPredicate.LOADER.register(Mantle.getResource("fire_immune"), LivingEntityPredicate.FIRE_IMMUNE.getLoader());
    LivingEntityPredicate.LOADER.register(Mantle.getResource("can_freeze"), LivingEntityPredicate.CAN_FREEZE.getLoader());
    LivingEntityPredicate.LOADER.register(Mantle.getResource("water_sensitive"), LivingEntityPredicate.WATER_SENSITIVE.getLoader());
    LivingEntityPredicate.LOADER.register(Mantle.getResource("on_fire"), LivingEntityPredicate.ON_FIRE.getLoader());
    LivingEntityPredicate.LOADER.register(Mantle.getResource("is_freezing"), LivingEntityPredicate.IS_FREEZING.getLoader());
    LivingEntityPredicate.LOADER.register(Mantle.getResource("is_in_powdered_snow"), LivingEntityPredicate.IS_IN_POWDERED_SNOW.getLoader());
    LivingEntityPredicate.LOADER.register(Mantle.getResource("on_ground"), LivingEntityPredicate.ON_GROUND.getLoader());
    LivingEntityPredicate.LOADER.register(Mantle.getResource("crouching"), LivingEntityPredicate.CROUCHING.getLoader());
    LivingEntityPredicate.LOADER.register(Mantle.getResource("sprinting"), LivingEntityPredicate.SPRINTING.getLoader());
    LivingEntityPredicate.LOADER.register(Mantle.getResource("blocking"), LivingEntityPredicate.BLOCKING.getLoader());
    LivingEntityPredicate.LOADER.register(Mantle.getResource("elytra_flying"), LivingEntityPredicate.ELYTRA_FLYING.getLoader());
    LivingEntityPredicate.LOADER.register(Mantle.getResource("has_effect"), HasMobEffectPredicate.LOADER);
    LivingEntityPredicate.LOADER.register(Mantle.getResource("block_at_entity"), BlockAtEntityPredicate.LOADER);
    LivingEntityPredicate.LOADER.register(Mantle.getResource("eyes_in_water"), LivingEntityPredicate.EYES_IN_WATER.getLoader());
    LivingEntityPredicate.LOADER.register(Mantle.getResource("feet_in_water"), LivingEntityPredicate.FEET_IN_WATER.getLoader());
    LivingEntityPredicate.LOADER.register(Mantle.getResource("underwater"), LivingEntityPredicate.UNDERWATER.getLoader());
    LivingEntityPredicate.LOADER.register(Mantle.getResource("raining_at"), LivingEntityPredicate.RAINING.getLoader());
    LivingEntityPredicate.LOADER.register(Mantle.getResource("mob_type"), MobTypePredicate.LOADER);
    LivingEntityPredicate.LOADER.register(Mantle.getResource("has_enchantment"), HasEnchantmentEntityPredicate.LOADER);

    // damage source predicates
    DamageSourcePredicate.LOADER.register(Mantle.getResource("has_entity"), DamageSourcePredicate.HAS_ENTITY.getLoader());
    DamageSourcePredicate.LOADER.register(Mantle.getResource("is_indirect"), DamageSourcePredicate.IS_INDIRECT.getLoader());
    DamageSourcePredicate.LOADER.register(Mantle.getResource("can_protect"), DamageSourcePredicate.CAN_PROTECT.getLoader());
    DamageSourcePredicate.LOADER.register(Mantle.getResource("damage_type"), DamageTypePredicate.LOADER);
    DamageSourcePredicate.LOADER.register(Mantle.getResource("message"), SourceMessagePredicate.LOADER);
    DamageSourcePredicate.LOADER.register(Mantle.getResource("attacker"), SourceAttackerPredicate.LOADER);
  }
}
