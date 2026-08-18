package slimeknights.mantle.loot.modifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.storage.loot.LootContext;
import slimeknights.mantle.Mantle;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Condition on a loot modifier that can inspect the loot generated so far.
 *
 * <p>Distinct from a vanilla {@code LootItemCondition}, which only sees the loot context —
 * these run after the table produced its drops, which is what lets a modifier say "only add
 * a nugget if the table did not already drop the ore itself".
 */
public interface ILootModifierCondition {
  /** Registry of condition types, dispatched on the {@code type} key like the vanilla loot codecs */
  Map<ResourceLocation, MapCodec<? extends ILootModifierCondition>> TYPES = new HashMap<>();

  Codec<ILootModifierCondition> CODEC = ResourceLocation.CODEC
    .<ILootModifierCondition>dispatch(
      "type",
      condition -> condition.getTypeId(),
      id -> {
        MapCodec<? extends ILootModifierCondition> codec = TYPES.get(id);
        return codec != null ? codec : MapCodec.unit(() -> null);
      })
    .validate(condition -> condition == null
      ? com.mojang.serialization.DataResult.error(() -> "Unknown loot modifier condition type")
      : com.mojang.serialization.DataResult.success(condition));

  /** Registers a condition type */
  static void register(ResourceLocation id, MapCodec<? extends ILootModifierCondition> codec) {
    TYPES.put(id, codec);
  }

  /** Registers the conditions Mantle ships */
  static void init() {
    register(ContainsItem.ID, ContainsItem.CODEC);
    register(Inverted.ID, Inverted.CODEC);
  }

  /** Id of this condition's type, for serialization */
  ResourceLocation getTypeId();

  /** Checks whether the modifier may run against the loot generated so far */
  boolean test(List<ItemStack> generatedLoot, LootContext context);

  /** Requires that the loot generated so far contains a matching item */
  record ContainsItem(Ingredient ingredient, int amountNeeded) implements ILootModifierCondition {
    static final ResourceLocation ID = Mantle.getResource("contains_item");
    static final MapCodec<ContainsItem> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
      Ingredient.CODEC.fieldOf("ingredient").forGetter(ContainsItem::ingredient),
      Codec.INT.optionalFieldOf("needed", 1).forGetter(ContainsItem::amountNeeded)
    ).apply(instance, ContainsItem::new));

    @Override
    public ResourceLocation getTypeId() {
      return ID;
    }

    @Override
    public boolean test(List<ItemStack> generatedLoot, LootContext context) {
      int matched = 0;
      for (ItemStack stack : generatedLoot) {
        if (ingredient.test(stack)) {
          matched += stack.getCount();
          if (matched >= amountNeeded) {
            return true;
          }
        }
      }
      return false;
    }
  }

  /** Inverts another condition */
  record Inverted(ILootModifierCondition condition) implements ILootModifierCondition {
    static final ResourceLocation ID = Mantle.getResource("inverted");
    static final MapCodec<Inverted> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
      CODEC_HOLDER.fieldOf("condition").forGetter(Inverted::condition)
    ).apply(instance, Inverted::new));

    @Override
    public ResourceLocation getTypeId() {
      return ID;
    }

    @Override
    public boolean test(List<ItemStack> generatedLoot, LootContext context) {
      return !condition.test(generatedLoot, context);
    }
  }

  /** Indirection so the nested condition codec can reference {@link #CODEC} before it is initialized */
  Codec<ILootModifierCondition> CODEC_HOLDER = Codec.lazyInitialized(() -> CODEC);
}
