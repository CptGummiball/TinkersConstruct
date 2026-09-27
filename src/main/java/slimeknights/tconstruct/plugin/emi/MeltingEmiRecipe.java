package slimeknights.tconstruct.plugin.emi;

import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import slimeknights.mantle.transfer.fluid.FluidStack;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.recipe.FluidValues;
import slimeknights.tconstruct.library.recipe.fuel.MeltingFuelLookup;
import slimeknights.tconstruct.library.recipe.melting.MeltingRecipe;

import java.util.List;

/**
 * Melter, smeltery and foundry melting. The foundry variant cycles the byproducts through the
 * output tank alongside the main fluid; the melter offers the solid fuel slot when the recipe is
 * cool enough for it.
 */
public class MeltingEmiRecipe extends TinkerEmiRecipe {
  static final ResourceLocation BACKGROUND = TConstruct.getResource("textures/gui/jei/melting.png");
  static final String KEY_TEMPERATURE = TConstruct.makeTranslationKey("jei", "temperature");

  private final MeltingRecipe recipe;
  private final boolean foundry;

  public MeltingEmiRecipe(EmiRecipeCategory category, MeltingRecipe recipe, boolean foundry) {
    super(category, null, 132, 40);
    this.recipe = recipe;
    this.foundry = foundry;
  }

  @Override
  public List<EmiIngredient> getInputs() {
    return List.of(EmiIngredient.of(recipe.getInput()));
  }

  @Override
  public List<EmiStack> getOutputs() {
    if (foundry) {
      return recipe.getOutputWithByproducts().stream().map(list -> fluid(list.get(0))).toList();
    }
    return List.of(fluid(recipe.getOutput()));
  }

  @Override
  public void addWidgets(WidgetHolder widgets) {
    widgets.addTexture(BACKGROUND, 0, 0, 132, 40, 0, 0);

    // input
    widgets.addSlot(EmiIngredient.of(recipe.getInput()), 23, 17).drawBack(false);

    // output tank with gauge overlay; the foundry cycles byproducts through it with a plus marker
    List<List<FluidStack>> outputs = recipe.getOutputWithByproducts();
    EmiIngredient tankContent;
    if (foundry && outputs.size() > 1) {
      tankContent = EmiIngredient.of(outputs.stream().map(list -> (EmiIngredient) fluid(list.get(0))).toList());
      widgets.addTexture(BACKGROUND, 87, 31, 6, 6, 132, 34);
    } else {
      tankContent = fluid(recipe.getOutput());
    }
    widgets.addTank(tankContent, 95, 3, 34, 34, capacity(FluidValues.METAL_BLOCK)).drawBack(false).recipeContext(this);
    widgets.addTexture(BACKGROUND, 96, 4, 32, 32, 132, 0);

    // melting arrow, sized by melting time
    widgets.addAnimatedTexture(BACKGROUND, 56, 18, 24, 17, 150, 41, Math.max(1, recipe.getTime()) * 5 * 50, true, false, false);

    // temperature
    int temperature = recipe.getTemperature();
    addCenteredText(widgets, Component.translatable(KEY_TEMPERATURE, temperature), 66, 5);

    // fuels: fluid fuels always, the solid fuel slot when the melter could run this on coal
    boolean solid = !foundry && temperature <= MeltingFuelLookup.getSolid().getTemperature();
    List<FluidStack> fuels = MeltingFuelHandler.getUsableFuels(temperature);
    if (!fuels.isEmpty()) {
      widgets.addTank(fluids(fuels), 3, 3, 14, solid ? 14 : 34, 1).drawBack(false).catalyst(true);
    }
    if (solid) {
      widgets.addTexture(BACKGROUND, 1, 19, 18, 20, 164, 0);
      widgets.addSlot(items(MeltingFuelHandler.SOLID_FUELS.get()), 1, 21).drawBack(false).catalyst(true);
    }
  }
}
