package slimeknights.tconstruct.plugin.emi;

import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.recipe.FluidValues;
import slimeknights.tconstruct.library.recipe.casting.IDisplayableCastingRecipe;

import java.util.ArrayList;
import java.util.List;

/**
 * Casting table and basin. Mirrors the JEI layout: tank at 3,3, cast at 38,19, output at 93,18,
 * the cooling arrow between them and the consumed/kept flag under the cast.
 */
public class CastingEmiRecipe extends TinkerEmiRecipe {
  private static final ResourceLocation BACKGROUND = TConstruct.getResource("textures/gui/jei/casting.png");
  private static final String KEY_COOLING_TIME = TConstruct.makeTranslationKey("jei", "time");
  private static final String KEY_CAST_KEPT = TConstruct.makeTranslationKey("jei", "casting.cast.kept");
  private static final String KEY_CAST_CONSUMED = TConstruct.makeTranslationKey("jei", "casting.cast.consumed");

  private final IDisplayableCastingRecipe recipe;

  public CastingEmiRecipe(EmiRecipeCategory category, IDisplayableCastingRecipe recipe) {
    super(category, null, 117, 54);
    this.recipe = recipe;
  }

  @Override
  public List<EmiIngredient> getInputs() {
    List<EmiIngredient> inputs = new ArrayList<>();
    inputs.add(fluids(recipe.getFluids()));
    if (recipe.hasCast() && recipe.isConsumed()) {
      inputs.add(items(recipe.getCastItems()));
    }
    return inputs;
  }

  @Override
  public List<EmiIngredient> getCatalysts() {
    if (recipe.hasCast() && !recipe.isConsumed()) {
      return List.of(items(recipe.getCastItems()));
    }
    return List.of();
  }

  @Override
  public List<EmiStack> getOutputs() {
    return recipe.getOutputs().stream().map(EmiStack::of).toList();
  }

  @Override
  public void addWidgets(WidgetHolder widgets) {
    widgets.addTexture(BACKGROUND, 0, 0, 117, 54, 0, 0);

    // tank with the input fluid; the overlay drawn above it restores the gauge lines
    widgets.addTank(fluids(recipe.getFluids()), 2, 2, 34, 34, capacity(FluidValues.METAL_BLOCK)).drawBack(false);
    widgets.addTexture(BACKGROUND, 3, 3, 32, 32, 133, 0);
    // the fluid pouring out of the faucet
    widgets.addTank(fluids(recipe.getFluids()), 43, 8, 6, recipe.hasCast() ? 11 : 27, 1).drawBack(false);

    // cast; a kept cast is a catalyst
    if (recipe.hasCast()) {
      widgets.addSlot(items(recipe.getCastItems()), 37, 18).drawBack(false).catalyst(!recipe.isConsumed());
      widgets.addTexture(BACKGROUND, 63, 39, 13, 11, 141, recipe.isConsumed() ? 32 : 43);
      widgets.addTooltipText(List.of(Component.translatable(recipe.isConsumed() ? KEY_CAST_CONSUMED : KEY_CAST_KEPT)), 63, 39, 13, 11);
    }

    // cooling arrow plus cooling time in seconds
    int coolingTime = Math.max(1, recipe.getCoolingTime());
    widgets.addAnimatedTexture(BACKGROUND, 58, 18, 24, 17, 117, 32, coolingTime * 50, true, false, false);
    addCenteredText(widgets, Component.translatable(KEY_COOLING_TIME, coolingTime / 20), 72, 5);

    widgets.addSlot(items(recipe.getOutputs()), 92, 17).drawBack(false).recipeContext(this);
  }
}
