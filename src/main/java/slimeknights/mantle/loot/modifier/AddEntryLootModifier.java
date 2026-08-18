package slimeknights.mantle.loot.modifier;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntries;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import slimeknights.mantle.Mantle;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;

/**
 * Injects an extra loot entry into an existing table, the workhorse behind the ore-bonus
 * and mob-drop modifiers.
 *
 * <p>{@code post_conditions} run against the loot the table already produced, which is how
 * "only add a nugget if the ore itself did not drop" is expressed.
 */
public class AddEntryLootModifier extends LootModifier {
  public static final ResourceLocation ID = Mantle.getResource("add_entry");
  public static final MapCodec<AddEntryLootModifier> CODEC = RecordCodecBuilder.mapCodec(instance ->
    codecStart(instance).and(instance.group(
      ILootModifierCondition.CODEC.listOf().optionalFieldOf("post_conditions", List.of()).forGetter(modifier -> modifier.modifierConditions),
      LootPoolEntries.CODEC.fieldOf("entry").forGetter(modifier -> modifier.entry),
      LootItemFunctions.ROOT_CODEC.listOf().optionalFieldOf("functions", List.of()).forGetter(modifier -> modifier.functions)
    )).apply(instance, AddEntryLootModifier::new));

  private final List<ILootModifierCondition> modifierConditions;
  private final LootPoolEntryContainer entry;
  private final List<LootItemFunction> functions;
  /** Functions merged into one, matching how vanilla pools apply theirs */
  private final BiFunction<ItemStack,LootContext,ItemStack> combinedFunctions;

  protected AddEntryLootModifier(List<LootItemCondition> conditions, List<ILootModifierCondition> modifierConditions, LootPoolEntryContainer entry, List<LootItemFunction> functions) {
    super(conditions);
    this.modifierConditions = modifierConditions;
    this.entry = entry;
    this.functions = functions;
    this.combinedFunctions = LootItemFunctions.compose(functions);
  }

  @Override
  public ResourceLocation getTypeId() {
    return ID;
  }

  @Override
  protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
    for (ILootModifierCondition condition : modifierConditions) {
      if (!condition.test(generatedLoot, context)) {
        return generatedLoot;
      }
    }
    Consumer<ItemStack> consumer = LootItemFunction.decorate(combinedFunctions, generatedLoot::add, context);
    entry.expand(context, generator -> generator.createItemStack(consumer, context));
    return generatedLoot;
  }
}
