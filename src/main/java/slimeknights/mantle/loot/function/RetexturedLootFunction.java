package slimeknights.mantle.loot.function;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import slimeknights.mantle.block.entity.IRetexturedBlockEntity;
import slimeknights.mantle.loot.MantleLoot;
import slimeknights.mantle.util.RetexturedHelper;

import java.util.List;

/**
 * Loot function to copy a retextured block's texture from the block entity to the dropped
 * item ({@code mantle:fill_retextured_block} in every retexturable block's loot table).
 * The 1.20 GSON serializer had no fields beyond the shared conditions, and the codec keeps
 * that JSON shape.
 */
public class RetexturedLootFunction extends LootItemConditionalFunction {
  public static final MapCodec<RetexturedLootFunction> CODEC = RecordCodecBuilder.mapCodec(instance ->
    commonFields(instance).apply(instance, RetexturedLootFunction::new));

  public RetexturedLootFunction(List<LootItemCondition> conditions) {
    super(conditions);
  }

  /** Creates a new instance with no conditions */
  public RetexturedLootFunction() {
    this(List.of());
  }

  @Override
  protected ItemStack run(ItemStack stack, LootContext context) {
    BlockEntity blockEntity = context.getParamOrNull(LootContextParams.BLOCK_ENTITY);
    if (blockEntity instanceof IRetexturedBlockEntity retextured) {
      String texture = retextured.getTextureName();
      if (!texture.isEmpty()) {
        RetexturedHelper.setTexture(stack, texture);
      }
    }
    return stack;
  }

  @Override
  public LootItemFunctionType<RetexturedLootFunction> getType() {
    return MantleLoot.RETEXTURED_FUNCTION;
  }
}
