package slimeknights.tconstruct.tools.modifiers.loot;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount.BinomialWithBonusCount;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount.Formula;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount.OreDrops;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount.UniformBonusCount;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.tools.TinkerModifiers;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/** Boosts drop rates based on modifier level */
public class ModifierBonusLootFunction extends LootItemConditionalFunction {
  /* Formula helpers. Vanilla's formula dispatch (ApplyBonusCount.FORMULA_CODEC, FORMULA_TYPE_CODEC, FORMULAS and the
   * FormulaType record) is package-private and the access widener only exposes the formula classes themselves, so we
   * mirror the dispatch here. JSON shape matches both vanilla and our 1.20 serializers: "formula" holds the type ID
   * and "parameters" the formula's fields, omitted when empty. */

  /** ID for the binomial formula, kept in sync with {@link BinomialWithBonusCount#TYPE} */
  private static final ResourceLocation BINOMIAL_WITH_BONUS_COUNT_ID = ResourceLocation.withDefaultNamespace("binomial_with_bonus_count");
  /** ID for the ore drops formula, kept in sync with {@link OreDrops#TYPE} */
  private static final ResourceLocation ORE_DROPS_ID = ResourceLocation.withDefaultNamespace("ore_drops");
  /** ID for the uniform bonus formula, kept in sync with {@link UniformBonusCount#TYPE} */
  private static final ResourceLocation UNIFORM_BONUS_COUNT_ID = ResourceLocation.withDefaultNamespace("uniform_bonus_count");

  /** Singleton ore drops formula; its codec is public even though its constructor is not */
  static final OreDrops ORE_DROPS = OreDrops.CODEC.parse(JsonOps.INSTANCE, new JsonObject()).getOrThrow();

  /** Creates a binomial with bonus formula; its constructor is opened by the access widener */
  static BinomialWithBonusCount binomial(int extraRounds, float probability) {
    return new BinomialWithBonusCount(extraRounds, probability);
  }

  /** Creates a uniform bonus count formula through its public codec */
  static UniformBonusCount uniform(int bonusMultiplier) {
    JsonObject json = new JsonObject();
    json.addProperty("bonusMultiplier", bonusMultiplier);
    return UniformBonusCount.CODEC.parse(JsonOps.INSTANCE, json).getOrThrow();
  }

  /** Codec for the binomial formula, matching vanilla's private {@code BinomialWithBonusCount#CODEC} field names */
  private static final Codec<BinomialWithBonusCount> BINOMIAL_CODEC = RecordCodecBuilder.create(instance -> instance.group(
    Codec.INT.fieldOf("extra").forGetter(BinomialWithBonusCount::extraRounds),
    Codec.FLOAT.fieldOf("probability").forGetter(BinomialWithBonusCount::probability)
  ).apply(instance, ModifierBonusLootFunction::binomial));

  /** Formula codecs by ID, mirroring vanilla's private {@code ApplyBonusCount#FORMULAS} */
  private static final Map<ResourceLocation,Codec<? extends Formula>> FORMULA_CODECS = Map.of(
    BINOMIAL_WITH_BONUS_COUNT_ID, BINOMIAL_CODEC,
    ORE_DROPS_ID, OreDrops.CODEC,
    UNIFORM_BONUS_COUNT_ID, UniformBonusCount.CODEC);

  /** Gets the ID for a formula; replacement for {@code Formula#getType()} whose return type is package-private */
  private static ResourceLocation getFormulaId(Formula formula) {
    if (formula instanceof OreDrops) {
      return ORE_DROPS_ID;
    }
    if (formula instanceof UniformBonusCount) {
      return UNIFORM_BONUS_COUNT_ID;
    }
    if (formula instanceof BinomialWithBonusCount) {
      return BINOMIAL_WITH_BONUS_COUNT_ID;
    }
    throw new IllegalArgumentException("Unknown formula " + formula);
  }

  /** Codec validating that a formula ID is one we know, matching the 1.20 error message */
  private static final Codec<ResourceLocation> FORMULA_ID_CODEC = ResourceLocation.CODEC.comapFlatMap(
    id -> FORMULA_CODECS.containsKey(id) ? DataResult.success(id) : DataResult.error(() -> "Invalid formula id: " + id),
    Function.identity());

  /** Dispatch codec for formulas: "formula" is the type ID, "parameters" holds the fields (absent when empty) */
  static final MapCodec<Formula> FORMULA_CODEC = ExtraCodecs.dispatchOptionalValue(
    "formula", "parameters", FORMULA_ID_CODEC,
    ModifierBonusLootFunction::getFormulaId,
    FORMULA_CODECS::get);

  /** Loot codec, keeps the 1.20 JSON shape */
  public static final MapCodec<ModifierBonusLootFunction> CODEC = RecordCodecBuilder.mapCodec(instance ->
    commonFields(instance).and(instance.group(
      ResourceLocation.CODEC.xmap(ModifierId::new, id -> id).fieldOf("modifier").forGetter(function -> function.modifier),
      FORMULA_CODEC.forGetter(function -> function.formula),
      Codec.BOOL.optionalFieldOf("include_base", true).forGetter(function -> function.includeBase)
    )).apply(instance, ModifierBonusLootFunction::new));

  /** Modifier ID to use for multiplier bonus */
  private final ModifierId modifier;
  /** Formula to apply */
  private final Formula formula;
  /** If true, considers level 1 as bonus, if false considers level 1 as no bonus */
  private final boolean includeBase;

  protected ModifierBonusLootFunction(List<LootItemCondition> conditions, ModifierId modifier, Formula formula, boolean includeBase) {
    super(conditions);
    this.modifier = modifier;
    this.formula = formula;
    this.includeBase = includeBase;
  }

  /** Creates a generic builder */
  public static Builder<?> builder(ModifierId modifier, Formula formula, boolean includeBase) {
    return simpleBuilder(conditions -> new ModifierBonusLootFunction(conditions, modifier, formula, includeBase));
  }

  /** Creates a builder for the binomial with bonus formula */
  public static Builder<?> binomialWithBonusCount(ModifierId modifier, float probability, int extra, boolean includeBase) {
    return builder(modifier, binomial(extra, probability), includeBase);
  }

  /** Creates a builder for the ore drops formula */
  public static Builder<?> oreDrops(ModifierId modifier, boolean includeBase) {
    return builder(modifier, ORE_DROPS, includeBase);
  }

  /** Creates a builder for the uniform bonus count */
  public static Builder<?> uniformBonusCount(ModifierId modifier, int bonusMultiplier, boolean includeBase) {
    return builder(modifier, uniform(bonusMultiplier), includeBase);
  }

  @Override
  public LootItemFunctionType getType() {
    return TinkerModifiers.modifierBonusFunction.get();
  }

  @Override
  public Set<LootContextParam<?>> getReferencedContextParams() {
    return ImmutableSet.of(LootContextParams.TOOL);
  }

  @Override
  protected ItemStack run(ItemStack stack, LootContext context) {
    int level = ModifierUtil.getModifierLevel(context.getParam(LootContextParams.TOOL), modifier);
    if (!includeBase) {
      level--;
    }
    if (level > 0) {
      stack.setCount(formula.calculateNewCount(context.getRandom(), stack.getCount(), level));
    }
    return stack;
  }
}
