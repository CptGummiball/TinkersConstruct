package slimeknights.mantle.loot.modifier;

import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.List;

/**
 * Base for a modifier gated by vanilla loot conditions, mirroring Forge's class of the same
 * name: the conditions all have to pass before {@link #doApply} runs.
 */
public abstract class LootModifier implements IGlobalLootModifier {
  protected final List<LootItemCondition> conditions;

  protected LootModifier(List<LootItemCondition> conditions) {
    this.conditions = conditions;
  }

  /** Adds the shared {@code conditions} field to a subclass codec */
  protected static <T extends LootModifier> com.mojang.datafixers.Products.P1<RecordCodecBuilder.Mu<T>, List<LootItemCondition>> codecStart(Instance<T> instance) {
    return instance.group(
      LootItemCondition.DIRECT_CODEC.listOf().optionalFieldOf("conditions", List.of()).forGetter(modifier -> modifier.conditions));
  }

  @Override
  public final ObjectArrayList<ItemStack> apply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
    for (LootItemCondition condition : conditions) {
      if (!condition.test(context)) {
        return generatedLoot;
      }
    }
    return doApply(generatedLoot, context);
  }

  /** Applies this modifier once its conditions passed */
  protected abstract ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context);
}
