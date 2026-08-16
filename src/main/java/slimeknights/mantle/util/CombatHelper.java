package slimeknights.mantle.util;

import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import slimeknights.mantle.item.ToolAction;
import slimeknights.mantle.item.ToolActions;
import slimeknights.mantle.Mantle;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Helpers for attacking with weapons */
public class CombatHelper {
  private static final float TO_RADIAN = (float)Math.PI / 180f;
  /** Attribute modifier to disable knockback on a target */
  private static final AttributeModifier ANTI_KNOCKBACK_MODIFIER = new AttributeModifier(Mantle.getResource("anti_knockback"), 1f, Operation.ADD_VALUE);
  /** Tool action to disable the base knockback of the weapon. Requires replacing left click behavior of your weapon. */
  public static final ToolAction NO_BASE_KNOCKBACK = ToolAction.get("no_base_knockback");

  private CombatHelper() {}

  /** Gets the item stack in the main hand that contributes to attributes. Exposed for benefit of Tinkers' Construct which can optimize these methods for its tools. */
  public static ItemStack getMainhandAttributeStack(LivingEntity entity) {
    // clientside does not use last item stack, so our best choice is the mainhand stack
    if (entity.level().isClientSide) {
      return entity.getMainHandItem();
    }
    // serverside, use the last item stack instead of the current. Should be the same, but if they mismatch then last item stack has correct attributes
    return entity.getLastHandItem(EquipmentSlot.MAINHAND);
  }

  /**
   * Gets a modifiable map that is a copy of the modifiers from the given attribute instance. All operations are guaranteed to have a valid set.
   * Note we use a map instead of a full attribute instance as we don't need the cache or other data structures.
   */
  public static Map<Operation, Set<AttributeModifier>> copyModifiers(AttributeInstance instance) {
    Map<Operation, Set<AttributeModifier>> modifiers = new EnumMap<>(Operation.class);
    for (Operation operation : Operation.values()) {
      Collection<AttributeModifier> original = instance.getModifiersOrEmpty(operation);
      Set<AttributeModifier> copy = new HashSet<>(original.size());
      copy.addAll(original);
      modifiers.put(operation, copy);
    }
    return modifiers;
  }

  /** Gets the attribute for the offhand by subtracting mainhand attributes and adding in offhand stack attributes. */
  public static float getOffhandAttribute(ItemStack stack, LivingEntity entity, Attribute attribute) {
    AttributeInstance instance = entity.getAttribute(attribute);
    if (instance == null) {
      return (float) entity.getAttributeBaseValue(attribute);
    }

    // fetch attributes for both relevant stacks
    ItemStack mainStack = getMainhandAttributeStack(entity);
    Collection<AttributeModifier> mainModifiers = List.of();
    if (!mainStack.isEmpty()) {
      mainModifiers = mainStack.getAttributeModifiers(EquipmentSlot.MAINHAND).get(attribute);
    }
    Collection<AttributeModifier> offhandModifiers = stack.getAttributeModifiers(EquipmentSlot.MAINHAND).get(attribute);

    // if no modifier changed, can save some work by just using the cached value
    if (mainModifiers.isEmpty() && offhandModifiers.isEmpty()) {
      return (float) instance.getValue();
    }

    // start by creating a modifiable copy of the per operation attribute map
    Map<Operation, Set<AttributeModifier>> modifiers = copyModifiers(instance);
    // remove all mainhand modifiers
    for (AttributeModifier modifier : mainModifiers) {
      modifiers.get(modifier.getOperation()).remove(modifier);
    }
    // add in all offhand modifiers
    for (AttributeModifier modifier : offhandModifiers) {
      // while there should be no duplicates due to mainhand modifiers above,
      // this will remove duplicates due to AttributeModifier equals only checking UUID
      modifiers.get(modifier.getOperation()).add(modifier);
    }
    // compute the value
    return (float) computeAttribute(attribute, instance.getBaseValue(), modifiers);
  }

  /** Computes the value for the given attribute. Copied from {@link AttributeInstance#calculateValue} */
  public static double computeAttribute(Attribute attribute, double base, Map<Operation,Set<AttributeModifier>> modifiers) {
    // addition modifiers
    for (AttributeModifier modifier : modifiers.get(Operation.ADDITION)) {
      base += modifier.getAmount();
    }
    // multiply base
    double value = base;
    for (AttributeModifier modifier : modifiers.get(Operation.MULTIPLY_BASE)) {
      value += base * modifier.getAmount();
    }
    // multiply total
    for (AttributeModifier modifier : modifiers.get(Operation.MULTIPLY_TOTAL)) {
      value *= 1.0 + modifier.getAmount();
    }
    return attribute.sanitizeValue(value);
  }
  // The attack()/isAttackable simulation family is cut for now: it posts Forge combat
  // events (CriticalHitEvent, knockback/sweep hooks) and returns with the event-layer
  // step. Only the attribute and damage-source helpers below are used by the port.



  /* Damage source creation */

  /** Makes a damage source from the given key */
  public static Holder<DamageType> damageType(RegistryAccess access, ResourceKey<DamageType> key) {
    return access.registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(key);
  }

  /** Makes a damage source from the given key */
  public static DamageSource damageSource(RegistryAccess access, ResourceKey<DamageType> key) {
    return new DamageSource(damageType(access, key));
  }

  /** Makes a damage source from the given key */
  public static DamageSource damageSource(Level level, ResourceKey<DamageType> key) {
    return new DamageSource(damageType(level.registryAccess(), key));
  }

  /** Makes a damage source from the given key for direct damage from an entity. */
  public static DamageSource damageSource(ResourceKey<DamageType> key, Entity entity) {
    return new DamageSource(damageType(entity.level().registryAccess(), key), entity);
  }

  /** Makes a damage source from the given key for indirect damage, such as from a projectile. */
  public static DamageSource damageSource(ResourceKey<DamageType> key, Entity direct, @Nullable Entity causing) {
    return new DamageSource(damageType(direct.level().registryAccess(), key), direct, causing);
  }
}
