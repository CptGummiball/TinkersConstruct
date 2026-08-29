package slimeknights.mantle.loot.modifier;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import slimeknights.mantle.Mantle;

import java.util.List;
import java.util.function.BiFunction;

/**
 * Swaps matching items in the generated loot for another item, used to turn vanilla drops
 * into mod equivalents (wither skeletons dropping necrotic bones instead of bone).
 */
public class ReplaceItemLootModifier extends LootModifier {
  public static final ResourceLocation ID = Mantle.getResource("replace_item");
  public static final MapCodec<ReplaceItemLootModifier> CODEC = RecordCodecBuilder.mapCodec(instance ->
    codecStart(instance).and(instance.group(
      Ingredient.CODEC.fieldOf("original").forGetter(modifier -> modifier.original),
      BuiltInRegistries.ITEM.byNameCodec().fieldOf("replacement").forGetter(modifier -> modifier.replacement),
      LootItemFunctions.ROOT_CODEC.listOf().optionalFieldOf("functions", List.of()).forGetter(modifier -> modifier.functions)
    )).apply(instance, ReplaceItemLootModifier::new));

  private final Ingredient original;
  private final Item replacement;
  private final List<LootItemFunction> functions;
  private final BiFunction<ItemStack,LootContext,ItemStack> combinedFunctions;

  protected ReplaceItemLootModifier(List<LootItemCondition> conditions, Ingredient original, Item replacement, List<LootItemFunction> functions) {
    super(conditions);
    this.original = original;
    this.replacement = replacement;
    this.functions = functions;
    this.combinedFunctions = LootItemFunctions.compose(functions);
  }

  /** Creates a datagen builder, matching the forge-era API */
  public static Builder builder(Ingredient original, slimeknights.mantle.recipe.helper.ItemOutput replacement) {
    return new Builder(original, replacement.get().getItem());
  }

  public static class Builder {
    private final List<LootItemCondition> conditions = new java.util.ArrayList<>();
    private final List<LootItemFunction> functions = new java.util.ArrayList<>();
    private final Ingredient original;
    private final Item replacement;

    private Builder(Ingredient original, Item replacement) {
      this.original = original;
      this.replacement = replacement;
    }

    public Builder addCondition(LootItemCondition condition) {
      conditions.add(condition);
      return this;
    }

    public Builder addFunction(LootItemFunction function) {
      functions.add(function);
      return this;
    }

    public ReplaceItemLootModifier build() {
      return new ReplaceItemLootModifier(List.copyOf(conditions), original, replacement, List.copyOf(functions));
    }
  }

  @Override
  public ResourceLocation getTypeId() {
    return ID;
  }

  @Override
  protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
    for (int i = 0; i < generatedLoot.size(); i++) {
      ItemStack stack = generatedLoot.get(i);
      if (original.test(stack)) {
        // the replacement keeps the original stack size, then runs the modifier's own functions
        generatedLoot.set(i, combinedFunctions.apply(new ItemStack(replacement, stack.getCount()), context));
      }
    }
    return generatedLoot;
  }
}
