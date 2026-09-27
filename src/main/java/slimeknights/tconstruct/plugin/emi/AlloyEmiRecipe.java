package slimeknights.tconstruct.plugin.emi;

import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import slimeknights.tconstruct.library.recipe.alloying.AlloyRecipe.AlloyIngredient;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.recipe.alloying.AlloyRecipe;

import java.util.List;

/**
 * Alloying: a row of input fluids sharing 48 pixels of width, the output fluid on the right, the
 * usable fuels in the small tank below the arrow. Amount proportions between the tanks mirror the
 * JEI category by sizing every tank to the largest amount in the recipe.
 */
public class AlloyEmiRecipe extends TinkerEmiRecipe {
  private static final ResourceLocation BACKGROUND = TConstruct.getResource("textures/gui/jei/alloy.png");

  private final AlloyRecipe recipe;

  public AlloyEmiRecipe(dev.emi.emi.api.recipe.EmiRecipeCategory category, AlloyRecipe recipe) {
    super(category, null, 172, 62);
    this.recipe = recipe;
  }

  @Override
  public List<EmiIngredient> getInputs() {
    return recipe.getInputs().stream().map(input -> fluids(input.fluid().getFluids())).toList();
  }

  @Override
  public List<EmiStack> getOutputs() {
    return List.of(fluid(recipe.getOutput()));
  }

  @Override
  public void addWidgets(WidgetHolder widgets) {
    widgets.addTexture(BACKGROUND, 0, 0, 172, 62, 0, 0);

    // every tank scales to the largest amount involved so the fill levels relate
    List<AlloyIngredient> inputs = recipe.getInputs();
    int maxAmount = recipe.getOutput().getAmount();
    for (AlloyIngredient input : inputs) {
      for (var fluidStack : input.fluid().getFluids()) {
        maxAmount = Math.max(maxAmount, fluidStack.getAmount());
      }
    }

    // inputs share the 48 wide box, the last takes the rounding remainder
    int count = inputs.size();
    if (count > 0) {
      int w = 48 / count;
      for (int i = 0; i < count; i++) {
        int width = i == count - 1 ? 48 - w * (count - 1) : w;
        widgets.addTank(fluids(inputs.get(i).fluid().getFluids()), 18, 10, width + 2, 34, capacity(maxAmount))
               .drawBack(false);
      }
    }

    // output
    widgets.addTank(fluid(recipe.getOutput()), 136, 10, 18, 34, capacity(maxAmount)).drawBack(false).recipeContext(this);

    // arrow and temperature
    widgets.addAnimatedTexture(BACKGROUND, 90, 21, 24, 17, 172, 0, 10000, true, false, false);
    addCenteredText(widgets, Component.translatable(MeltingEmiRecipe.KEY_TEMPERATURE, recipe.getTemperature()), 86, 3);

    // fuels
    var fuels = MeltingFuelHandler.getUsableFuels(recipe.getTemperature());
    if (!fuels.isEmpty()) {
      widgets.addTank(fluids(fuels), 94, 43, 16, 16, 1).drawBack(false).catalyst(true);
      widgets.addTexture(BACKGROUND, 94, 43, 16, 16, 172, 17);
    }
  }
}
