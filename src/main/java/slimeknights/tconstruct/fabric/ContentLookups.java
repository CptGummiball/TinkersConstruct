package slimeknights.tconstruct.fabric;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.tools.nbt.IModDataView;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.Optional;
import java.util.function.BiFunction;

/**
 * Compile-order seam for the incremental port.
 *
 * <p>The library layer reads a handful of singletons that the content modules register
 * (entity types, effects, attributes, modifier ids). Upstream compiles library and content
 * together, so those reads are plain static field accesses; the port gate compiles the
 * library slice first, so the content classes ({@code TinkerTools}, {@code TinkerModifiers},
 * {@code TinkerEffects}, {@code TinkerAttributes}) are not on the compile path yet.
 *
 * <p>Each member here is the same read expressed as a lazy by-ID registry lookup — which is
 * all a Forge {@code RegistryObject} ever was. Before the content module registers, lookups
 * report absent and the helpers fall back to vanilla semantics; the affected code paths are
 * unreachable before content exists anyway. Once phase 4 gates the content classes in, call
 * sites may revert to the content statics — both forms read the same registry entry.
 */
public final class ContentLookups {

  private ContentLookups() {}

  /* TinkerTools.fishingHook — only ever used for entity-type identity checks */
  public static final ResourceLocation FISHING_HOOK = TConstruct.getResource("fishing_hook");

  /** Whether the entity is Tinkers' fishing hook ({@code TinkerTools.fishingHook}). */
  public static boolean isFishingHook(Entity entity) {
    return FISHING_HOOK.equals(BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()));
  }

  /* TinkerTools.indestructibleItem — resolved when an indestructible drop spawns */
  private static final ResourceKey<EntityType<?>> INDESTRUCTIBLE_ITEM =
    ResourceKey.create(Registries.ENTITY_TYPE, TConstruct.getResource("indestructible_item"));

  /** The indestructible item entity type; throws if the tools module has not registered it yet. */
  @SuppressWarnings("unchecked")
  public static <T extends Entity> EntityType<T> indestructibleItem() {
    return (EntityType<T>) BuiltInRegistries.ENTITY_TYPE.getHolder(INDESTRUCTIBLE_ITEM)
      .orElseThrow(() -> new IllegalStateException("tconstruct:indestructible_item is not registered; the tools content module is required to spawn indestructible drops"))
      .value();
  }

  /* TinkerEffects.enderference and its two helpers, verbatim logic */
  private static final ResourceKey<MobEffect> ENDERFERENCE =
    ResourceKey.create(Registries.MOB_EFFECT, TConstruct.getResource("enderference"));

  private static Optional<Holder.Reference<MobEffect>> enderference() {
    return BuiltInRegistries.MOB_EFFECT.getHolder(ENDERFERENCE);
  }

  /** Mirror of {@code TinkerEffects.canHitWithProjectile}: endermen dodge projectiles unless enderfered. */
  public static boolean canHitWithProjectile(@Nullable LivingEntity living) {
    return living == null || living.getType() != EntityType.ENDERMAN
      || enderference().map(living::hasEffect).orElse(false);
  }

  /** Mirror of {@code TinkerEffects.needsEnderferenceOverride}. */
  public static boolean needsEnderferenceOverride(@Nullable Entity entity) {
    return entity instanceof LivingEntity living && needsEnderferenceOverride(living);
  }

  /** Mirror of {@code TinkerEffects.needsEnderferenceOverride}. */
  public static boolean needsEnderferenceOverride(@Nullable LivingEntity living) {
    return living != null && living.getType() == EntityType.ENDERMAN
      && enderference().map(living::hasEffect).orElse(false);
  }

  /* TinkerEffects.bleeding — looting substitutes the bleed level on bleed-out kills */
  private static final ResourceKey<MobEffect> BLEEDING =
    ResourceKey.create(Registries.MOB_EFFECT, TConstruct.getResource("bleeding"));

  /** Mirror of {@code TinkerEffect.getAmplifier(living, TinkerEffects.bleeding.get())}: amplifier, or -1 when absent. */
  public static int bleedingAmplifier(LivingEntity living) {
    return BuiltInRegistries.MOB_EFFECT.getHolder(BLEEDING)
      .map(holder -> {
        var instance = living.getEffect(holder);
        return instance != null ? instance.getAmplifier() : -1;
      })
      .orElse(-1);
  }

  /* TinkerAttributes.PROTECTION_CAP — registered with default 0.8, range [0, 0.95] */
  private static final ResourceKey<Attribute> PROTECTION_CAP =
    ResourceKey.create(Registries.ATTRIBUTE, TConstruct.getResource("generic.protection_cap"));
  /** Default value of the protection cap attribute, used until the attribute is registered and attached. */
  public static final double PROTECTION_CAP_DEFAULT = 0.8;

  /** Value of {@code TinkerAttributes.PROTECTION_CAP} on the entity, falling back to the registration default. */
  public static double getProtectionCap(LivingEntity living) {
    return BuiltInRegistries.ATTRIBUTE.getHolder(PROTECTION_CAP)
      .map(holder -> {
        AttributeInstance instance = living.getAttribute(holder);
        return instance != null ? instance.getValue() : PROTECTION_CAP_DEFAULT;
      })
      .orElse(PROTECTION_CAP_DEFAULT);
  }

  /* TinkerModifiers.overslime / overworked — only their ids are read from the library layer */
  public static final ModifierId OVERSLIME = new ModifierId(TConstruct.MOD_ID, "overslime");
  public static final ModifierId OVERWORKED = new ModifierId(TConstruct.MOD_ID, "overworked");

  /*
   * TinkerRecipeTypes.MATERIAL and TinkerTables' material serializer / part builder.
   * MaterialRecipe reads these in getType/getSerializer/getToastSymbol; none of the three
   * is called before the tables module has registered its content.
   */
  private static final ResourceLocation MATERIAL_RECIPE = TConstruct.getResource("material");
  private static final ResourceLocation PART_BUILDER = TConstruct.getResource("part_builder");

  /** Mirror of {@code TinkerRecipeTypes.MATERIAL.get()}. */
  public static net.minecraft.world.item.crafting.RecipeType<?> materialRecipeType() {
    return BuiltInRegistries.RECIPE_TYPE.getOptional(MATERIAL_RECIPE)
      .orElseThrow(() -> new IllegalStateException("tconstruct:material recipe type is not registered; the tables content module provides it"));
  }

  /** Mirror of {@code TinkerTables.materialRecipeSerializer.get()}. */
  public static net.minecraft.world.item.crafting.RecipeSerializer<?> materialRecipeSerializer() {
    return BuiltInRegistries.RECIPE_SERIALIZER.getOptional(MATERIAL_RECIPE)
      .orElseThrow(() -> new IllegalStateException("tconstruct:material recipe serializer is not registered; the tables content module provides it"));
  }

  /** Mirror of {@code new ItemStack(TinkerTables.partBuilder)}; empty until the tables module registers. */
  public static net.minecraft.world.item.ItemStack partBuilderStack() {
    return BuiltInRegistries.ITEM.getOptional(PART_BUILDER)
      .map(net.minecraft.world.item.ItemStack::new)
      .orElse(net.minecraft.world.item.ItemStack.EMPTY);
  }

  /* TinkerToolParts.materialBlock — block entity type, resolved when a material block is placed */
  private static final ResourceLocation MATERIAL_BLOCK = TConstruct.getResource("material_block");

  /** Mirror of {@code TinkerToolParts.materialBlock.get()}. */
  public static net.minecraft.world.level.block.entity.BlockEntityType<?> materialBlockEntityType() {
    return BuiltInRegistries.BLOCK_ENTITY_TYPE.getOptional(MATERIAL_BLOCK)
      .orElseThrow(() -> new IllegalStateException("tconstruct:material_block block entity type is not registered; the tool parts content module provides it"));
  }

  /* TinkerRecipeTypes.DATA and TinkerSmeltery's material fluid serializer, read by MaterialFluidRecipe */
  private static final ResourceLocation DATA_RECIPE = TConstruct.getResource("data");
  private static final ResourceLocation MATERIAL_FLUID = TConstruct.getResource("material_fluid");

  /** Mirror of {@code TinkerRecipeTypes.DATA.get()}. */
  public static net.minecraft.world.item.crafting.RecipeType<?> dataRecipeType() {
    return BuiltInRegistries.RECIPE_TYPE.getOptional(DATA_RECIPE)
      .orElseThrow(() -> new IllegalStateException("tconstruct:data recipe type is not registered; the content modules provide it"));
  }

  /** Mirror of {@code TinkerSmeltery.materialFluidRecipe.get()}. */
  public static net.minecraft.world.item.crafting.RecipeSerializer<?> materialFluidSerializer() {
    return BuiltInRegistries.RECIPE_SERIALIZER.getOptional(MATERIAL_FLUID)
      .orElseThrow(() -> new IllegalStateException("tconstruct:material_fluid recipe serializer is not registered; the smeltery content module provides it"));
  }

  /*
   * ModifierSetWorktableRecipe.isInSet — the interaction-source checks read a modifier set
   * from persistent data. The worktable recipe class itself gates in with phase 4 (it hangs
   * off the full recipe-type tree); this mirrors its two static readers verbatim.
   */
  private static final BiFunction<CompoundTag, String, ListTag> LIST_GETTER = (tag, name) -> tag.getList(name, Tag.TAG_STRING);

  /** Mirror of {@code ModifierSetWorktableRecipe.isInSet(IModDataView, ResourceLocation, ModifierId)}. */
  public static boolean isInWorktableSet(IModDataView modData, ResourceLocation key, ModifierId modifier) {
    if (!modData.contains(key, Tag.TAG_LIST)) {
      return false;
    }
    return isInWorktableSet(modData.get(key, LIST_GETTER), modifier);
  }

  /** Mirror of {@code ModifierSetWorktableRecipe.isInSet(ListTag, ModifierId, false)}. */
  public static boolean isInWorktableSet(ListTag list, ModifierId modifier) {
    String modifierStr = modifier.toString();
    Iterator<Tag> iterator = list.iterator();
    while (iterator.hasNext()) {
      if (modifierStr.equals(iterator.next().getAsString())) {
        return true;
      }
    }
    return false;
  }
}
