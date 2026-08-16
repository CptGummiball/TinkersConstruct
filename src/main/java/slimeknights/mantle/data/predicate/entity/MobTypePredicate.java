package slimeknights.mantle.data.predicate.entity;

import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.registry.NamedComponentRegistry;

/**
 * Predicate matching a category of mob.
 *
 * <p>1.21 deleted {@code MobType} and {@code LivingEntity.getMobType()}; the categories they
 * expressed are entity type tags now. The predicate therefore holds a {@link TagKey} — but the
 * JSON names are kept identical to the old {@code MobType} names, so existing data files
 * ({@code "mobs": "undead"}) keep working without a migration.
 *
 * <p>Tags are also strictly more capable than the old enum: a mob had exactly one MobType,
 * whereas it can carry several tags, and datapacks can extend them.
 */
public record MobTypePredicate(TagKey<EntityType<?>> type) implements LivingEntityPredicate {

  /** Registry of mob categories, so addons can register their own names. */
  public static final NamedComponentRegistry<TagKey<EntityType<?>>> MOB_TYPES =
    new NamedComponentRegistry<>("Unknown mob type");

  static {
    // Names match Forge's MobType constants so existing JSON keeps parsing.
    MOB_TYPES.register(slimeknights.mantle.Mantle.commonResource("undead"), EntityTypeTags.UNDEAD);
    MOB_TYPES.register(slimeknights.mantle.Mantle.commonResource("arthropod"), EntityTypeTags.ARTHROPOD);
    MOB_TYPES.register(slimeknights.mantle.Mantle.commonResource("illager"), EntityTypeTags.ILLAGER);
    MOB_TYPES.register(slimeknights.mantle.Mantle.commonResource("water"), EntityTypeTags.AQUATIC);
  }

  /** Loader for a mob type predicate */
  public static RecordLoadable<MobTypePredicate> LOADER =
    RecordLoadable.create(MOB_TYPES.requiredField("mobs", MobTypePredicate::type), MobTypePredicate::new);

  @Override
  public boolean matches(LivingEntity input) {
    return input.getType().is(type);
  }

  @Override
  public RecordLoadable<? extends LivingEntityPredicate> getLoader() {
    return LOADER;
  }
}
