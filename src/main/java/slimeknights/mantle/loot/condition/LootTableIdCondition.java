package slimeknights.mantle.loot.condition;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import slimeknights.mantle.loot.MantleLoot;
import slimeknights.mantle.loot.modifier.GlobalLootManager;

/**
 * Matches the loot table currently being rolled, Forge's {@code forge:loot_table_id}.
 *
 * <p>Only meaningful inside a global loot modifier: the table id comes from
 * {@link GlobalLootManager}, which is the only thing that knows which table is rolling.
 * Used elsewhere it simply never matches.
 */
public record LootTableIdCondition(ResourceLocation lootTableId) implements LootItemCondition {
  public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath("forge", "loot_table_id");
  public static final MapCodec<LootTableIdCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
    ResourceLocation.CODEC.fieldOf("loot_table_id").forGetter(LootTableIdCondition::lootTableId)
  ).apply(instance, LootTableIdCondition::new));

  @Override
  public LootItemConditionType getType() {
    return MantleLoot.LOOT_TABLE_ID;
  }

  @Override
  public boolean test(LootContext context) {
    return lootTableId.equals(GlobalLootManager.currentTable());
  }
}
