package slimeknights.mantle.loot;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryType;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import slimeknights.mantle.Mantle;
import slimeknights.mantle.loot.entry.TagPreferenceLootEntry;
import slimeknights.mantle.recipe.condition.TagCondition;
import slimeknights.mantle.recipe.condition.TagEmptyCondition;
import slimeknights.mantle.recipe.condition.TagFilledCondition;

import java.util.function.Function;

/**
 * Mantle's loot registrations, cut down to the pieces Tinkers' data uses: the tag_filled /
 * tag_empty loot conditions and the tag_preference loot entry.
 *
 * <p>1.21 note: loot serializers are {@link MapCodec}s; the JSON shape matches the 1.20 GSON
 * serializers, so existing data files keep parsing.
 */
public class MantleLoot {
  private MantleLoot() {}

  /** Condition type for {@link TagFilledCondition} */
  public static LootItemConditionType TAG_FILLED;
  /** Condition type for {@link TagEmptyCondition} */
  public static LootItemConditionType TAG_EMPTY;
  /** Entry type for {@link TagPreferenceLootEntry} */
  public static LootPoolEntryType TAG_PREFERENCE;
  /** Function type for {@link slimeknights.mantle.loot.function.RetexturedLootFunction} */
  public static net.minecraft.world.level.storage.loot.functions.LootItemFunctionType<slimeknights.mantle.loot.function.RetexturedLootFunction> RETEXTURED_FUNCTION;

  /** Registers the loot types; call once from the bootstrap */
  public static void register() {
    if (TAG_FILLED != null) {
      return;
    }
    TAG_FILLED = condition("tag_filled", tagConditionCodec(TagFilledCondition::new));
    TAG_EMPTY = condition("tag_empty", tagConditionCodec(TagEmptyCondition::new));
    TAG_PREFERENCE = Registry.register(BuiltInRegistries.LOOT_POOL_ENTRY_TYPE, Mantle.getResource("tag_preference"), new LootPoolEntryType(TagPreferenceLootEntry.CODEC));
    RETEXTURED_FUNCTION = Registry.register(BuiltInRegistries.LOOT_FUNCTION_TYPE, Mantle.getResource("fill_retextured_block"), new net.minecraft.world.level.storage.loot.functions.LootItemFunctionType<>(slimeknights.mantle.loot.function.RetexturedLootFunction.CODEC));
  }

  /** Registers a loot condition type */
  private static LootItemConditionType condition(String name, MapCodec<? extends LootItemCondition> codec) {
    return Registry.register(BuiltInRegistries.LOOT_CONDITION_TYPE, Mantle.getResource(name), new LootItemConditionType(codec));
  }

  /**
   * Builds the loot codec for a tag condition, matching the 1.20 JSON shape: optional
   * {@code registry} (defaulting to items) plus {@code tag}.
   */
  private static <C extends TagCondition<?> & LootItemCondition> MapCodec<C> tagConditionCodec(Function<TagKey<?>,C> constructor) {
    return RecordCodecBuilder.mapCodec(instance -> instance.group(
      ResourceLocation.CODEC.optionalFieldOf("registry", Registries.ITEM.location()).forGetter(condition -> condition.getTag().registry().location()),
      ResourceLocation.CODEC.fieldOf("tag").forGetter(condition -> condition.getTag().location())
    ).apply(instance, (registry, tag) -> constructor.apply(TagKey.create(ResourceKey.createRegistryKey(registry), tag))));
  }
}
