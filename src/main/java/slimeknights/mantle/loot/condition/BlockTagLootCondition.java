package slimeknights.mantle.loot.condition;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import slimeknights.mantle.Mantle;
import slimeknights.mantle.loot.MantleLoot;

/**
 * Matches the block being broken against a block tag ({@code mantle:block_tag}).
 *
 * <p>Vanilla's own block-state condition only matches a single block, which the ore-bonus
 * modifiers cannot use — they need "any iron ore" across mods. Never matches when the
 * context carries no block, e.g. entity loot.
 */
public record BlockTagLootCondition(TagKey<Block> tag) implements LootItemCondition {
  public static final ResourceLocation ID = Mantle.getResource("block_tag");
  public static final MapCodec<BlockTagLootCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
    ResourceLocation.CODEC.xmap(id -> TagKey.create(Registries.BLOCK, id), TagKey::location).fieldOf("tag").forGetter(BlockTagLootCondition::tag)
  ).apply(instance, BlockTagLootCondition::new));

  @Override
  public LootItemConditionType getType() {
    return MantleLoot.BLOCK_TAG;
  }

  @Override
  public boolean test(LootContext context) {
    BlockState state = context.getParamOrNull(LootContextParams.BLOCK_STATE);
    return state != null && state.is(tag);
  }
}
