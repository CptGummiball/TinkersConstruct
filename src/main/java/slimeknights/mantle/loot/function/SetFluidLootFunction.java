package slimeknights.mantle.loot.function;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import slimeknights.mantle.data.loadable.LoadableCodec;
import slimeknights.mantle.data.loadable.common.FluidStackLoadable;
import slimeknights.mantle.loot.MantleLoot;
import slimeknights.mantle.transfer.TransferUtil;
import slimeknights.mantle.transfer.fluid.FluidStack;
import slimeknights.mantle.transfer.fluid.IFluidHandler.FluidAction;

import java.util.List;

/**
 * Loot function to fill a dropped item with fluid ({@code mantle:set_fluid}), used by loot
 * injections to hand out pre-filled tanks and lanterns. Fills through the same item fluid
 * handler lookup the runtime transfer uses, so whatever storage the item declares receives
 * the fluid.
 */
public class SetFluidLootFunction extends LootItemConditionalFunction {
  public static final MapCodec<SetFluidLootFunction> CODEC = RecordCodecBuilder.mapCodec(instance ->
    commonFields(instance).and(
      new LoadableCodec<>(FluidStackLoadable.REQUIRED_STACK).fieldOf("fluid").forGetter(function -> function.fluid)
    ).apply(instance, SetFluidLootFunction::new));

  /** Fluid to add to the stack */
  private final FluidStack fluid;

  public SetFluidLootFunction(List<LootItemCondition> conditions, FluidStack fluid) {
    super(conditions);
    this.fluid = fluid;
  }

  @Override
  protected ItemStack run(ItemStack stack, LootContext context) {
    TransferUtil.getFluidHandlerItem(stack).ifPresent(handler -> handler.fill(fluid.copy(), FluidAction.EXECUTE));
    return stack;
  }

  /** Creates a new builder with the given fluid */
  public static LootItemConditionalFunction.Builder<?> builder(FluidStack fluid) {
    return simpleBuilder(conditions -> new SetFluidLootFunction(conditions, fluid));
  }

  @Override
  public LootItemFunctionType<SetFluidLootFunction> getType() {
    return MantleLoot.SET_FLUID_FUNCTION;
  }
}
