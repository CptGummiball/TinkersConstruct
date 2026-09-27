package slimeknights.tconstruct.shared;

import net.fabricmc.fabric.api.registry.FabricBrewingRecipeRegistryBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.Potions;
import slimeknights.mantle.registration.RegistryObject;
import slimeknights.mantle.registration.deferred.PotionDeferredRegister;
import slimeknights.mantle.registration.deferred.PotionDeferredRegister.PotionType;
import slimeknights.mantle.registration.object.EnumObject;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerEffect;
import slimeknights.tconstruct.common.TinkerModule;
import slimeknights.tconstruct.shared.effect.AntigravityEffect;
import slimeknights.tconstruct.shared.effect.ReturningEffect;
import slimeknights.tconstruct.tools.modifiers.effect.BleedingEffect;
import slimeknights.tconstruct.tools.modifiers.effect.MagneticEffect;
import slimeknights.tconstruct.tools.modifiers.effect.RepulsiveEffect;
import slimeknights.tconstruct.tools.modifiers.traits.skull.SelfDestructiveModifier.SelfDestructiveEffect;

import javax.annotation.Nullable;
import java.util.Optional;

/**
 * Handles registration for all status effects and potions in the mod.
 *
 * <p>Fabric port notes: brewing recipes are built per server through
 * {@link FabricBrewingRecipeRegistryBuilder}, replacing Forge's static
 * {@link PotionBrewing} mixes. The congealed slime ingredients live in the world module;
 * they are looked up by ID when the brewing registry builds, so the mixes activate
 * automatically once that module is ported (and are skipped quietly until then).
 */
public class TinkerEffects extends TinkerModule {
  private static final PotionDeferredRegister POTIONS = new PotionDeferredRegister(TConstruct.MOD_ID);

  // slimy potions
  public static final RegistryObject<TinkerEffect> experienced = MOB_EFFECTS.register("experienced", () -> new TinkerEffect(MobEffectCategory.BENEFICIAL, 0x82c873, true).addAttributeModifier(TinkerAttributes.EXPERIENCE_MULTIPLIER, TConstruct.getResource("effect.experienced"), 0.25f, Operation.ADD_MULTIPLIED_BASE));
  public static final RegistryObject<TinkerEffect> ricochet = MOB_EFFECTS.register("ricochet", () -> new TinkerEffect(MobEffectCategory.NEUTRAL, 0x01cbcd, true).addAttributeModifier(TinkerAttributes.KNOCKBACK_MULTIPLIER, TConstruct.getResource("effect.ricochet"), 0.5f, Operation.ADD_MULTIPLIED_BASE));
  public static final RegistryObject<TinkerEffect> enderference = MOB_EFFECTS.register("enderference", () -> new TinkerEffect(MobEffectCategory.HARMFUL, 0xD37CFF, true));
  /** Projectile persistent data key to allow ranged modifiers to hit endermen. */
  public static final ResourceLocation ENDERFERENCE_KEY = enderference.getId();

  // slimy cakes
  public static final RegistryObject<TinkerEffect> bouncy = MOB_EFFECTS.register("bouncy", () -> new TinkerEffect(MobEffectCategory.BENEFICIAL, 0x71AC63, true).addAttributeModifier(TinkerAttributes.BOUNCY, TConstruct.getResource("effect.bouncy"), 1, Operation.ADD_VALUE));
  public static final RegistryObject<TinkerEffect> doubleJump = MOB_EFFECTS.register("double_jump", () -> new TinkerEffect(MobEffectCategory.BENEFICIAL, 0xA99B87, true).addAttributeModifier(TinkerAttributes.JUMP_COUNT, TConstruct.getResource("effect.double_jump"), 1, Operation.ADD_VALUE));
  public static final RegistryObject<AntigravityEffect> antigravity = MOB_EFFECTS.register("antigravity", AntigravityEffect::new);
  public static final RegistryObject<ReturningEffect> returning = MOB_EFFECTS.register("returning", ReturningEffect::new);

  // modifier effects
  public static final RegistryObject<BleedingEffect> bleeding = MOB_EFFECTS.register("bleeding", BleedingEffect::new);
  public static final RegistryObject<MagneticEffect> magnetic = MOB_EFFECTS.register("magnetic", MagneticEffect::new);
  public static final RegistryObject<TinkerEffect> selfDestructing = MOB_EFFECTS.register("self_destructing", SelfDestructiveEffect::new);
  public static final RegistryObject<RepulsiveEffect> repulsive = MOB_EFFECTS.register("repulsive", RepulsiveEffect::new);
  public static final RegistryObject<TinkerEffect> pierce = MOB_EFFECTS.register("pierce", () -> new TinkerEffect(MobEffectCategory.HARMFUL, 0xD1D37A, true).addAttributeModifier(Attributes.ARMOR, TConstruct.getResource("effect.pierce"), -1, Operation.ADD_VALUE));
  // damage boost
  public static final RegistryObject<TinkerEffect> conductive = MOB_EFFECTS.register("conductive", () -> new TinkerEffect(MobEffectCategory.HARMFUL, 0xF2D500, true));
  public static final RegistryObject<TinkerEffect> venom = MOB_EFFECTS.register("venom", () -> new TinkerEffect(MobEffectCategory.HARMFUL, 0xA2935E, true));

  // potions
  public static final EnumObject<PotionType,Potion> experiencedPotion = POTIONS.registerTypes(experienced).withStrong().withLong().build();
  public static final EnumObject<PotionType,Potion> ricochetPotion = POTIONS.registerTypes(ricochet).withStrong().withLong().build();
  public static final EnumObject<PotionType,Potion> levitationPotion = POTIONS.registerTypes("levitation", () -> MobEffects.LEVITATION, 15 * 20, 0).withStrong().withLong(40 * 20, 0).build();
  public static final EnumObject<PotionType,Potion> enderferencePotion = POTIONS.registerTypes(enderference, 90 * 20, 0).withLong().build();

  /** Registers the brewing recipes; call once from the bootstrap */
  public static void init() {
    FabricBrewingRecipeRegistryBuilder.BUILD.register(builder -> {
      brewing(builder, experiencedPotion,  Potions.AWKWARD, "earth_congealed_slime");
      brewing(builder, ricochetPotion,     Potions.AWKWARD, "sky_congealed_slime");
      brewing(builder, levitationPotion,   Potions.AWKWARD, "ichor_congealed_slime");
      brewing(builder, enderferencePotion, Potions.AWKWARD, "ender_congealed_slime");
    });
  }

  /** Registers recipes for brewing, longer and stronger potions for the given object */
  private static void brewing(PotionBrewing.Builder builder, EnumObject<PotionType,Potion> potion, Holder<Potion> base, String ingredientName) {
    // the ingredient items live in the world module; look them up by ID so this class works
    // before that module is ported (the mix simply activates once the item exists)
    Optional<Item> ingredient = BuiltInRegistries.ITEM.getOptional(TConstruct.getResource(ingredientName));
    if (ingredient.isEmpty()) {
      TConstruct.LOG.debug("Skipping brewing recipe for missing ingredient tconstruct:{}", ingredientName);
      return;
    }
    Holder<Potion> normal = BuiltInRegistries.POTION.wrapAsHolder(potion.get(PotionType.NORMAL));
    builder.addMix(base, ingredient.get(), normal);
    Potion longer = potion.getOrNull(PotionType.LONG);
    if (longer != null) {
      builder.addMix(normal, Items.REDSTONE, BuiltInRegistries.POTION.wrapAsHolder(longer));
    }
    Potion strong = potion.getOrNull(PotionType.STRONG);
    if (strong != null) {
      builder.addMix(normal, Items.GLOWSTONE_DUST, BuiltInRegistries.POTION.wrapAsHolder(strong));
    }
  }

  /** Checks if the given entity can be hit considering enderman enderference */
  public static boolean canHitWithProjectile(@Nullable LivingEntity living) {
    return living == null || living.getType() != EntityType.ENDERMAN || living.hasEffect(enderference.get().holder());
  }

  /** Checks if the given entity needs special casing for enderference */
  public static boolean needsEnderferenceOverride(@Nullable Entity entity) {
    return entity != null && entity.getType() == EntityType.ENDERMAN && entity instanceof LivingEntity living && living.hasEffect(enderference.get().holder());
  }

  /** Checks if the given entity needs special casing for enderference */
  public static boolean needsEnderferenceOverride(@Nullable LivingEntity living) {
    return living != null && living.getType() == EntityType.ENDERMAN && living.hasEffect(enderference.get().holder());
  }
}
