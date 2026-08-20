package slimeknights.tconstruct.plugin.emi;

import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.resources.ResourceLocation;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.recipe.modifiers.severing.SeveringRecipe;

import java.util.List;

/** Severing: the entity on the left, what its head (or the rest) drops on the right. */
public class SeveringEmiRecipe extends TinkerEmiRecipe {
  private static final ResourceLocation BACKGROUND = TConstruct.getResource("textures/gui/jei/tinker_station.png");

  private final SeveringRecipe recipe;

  public SeveringEmiRecipe(dev.emi.emi.api.recipe.EmiRecipeCategory category, SeveringRecipe recipe) {
    super(category, null, 100, 38);
    this.recipe = recipe;
  }

  @Override
  public List<EmiIngredient> getInputs() {
    return List.of(items(recipe.getIngredient().getEggs()));
  }

  @Override
  public List<EmiStack> getOutputs() {
    return List.of(EmiStack.of(recipe.getOutput()));
  }

  @Override
  public void addWidgets(WidgetHolder widgets) {
    widgets.addTexture(BACKGROUND, 0, 0, 100, 38, 0, 78);
    widgets.addSlot(items(recipe.getIngredient().getEggs()), 10, 10).drawBack(false);
    addEntityRenderer(widgets, recipe.getIngredient().getDisplay().stream().<net.minecraft.world.entity.EntityType<?>>map(input -> input.type()).toList(), 3, 3);
    widgets.addSlot(EmiStack.of(recipe.getOutput()), 75, 10).drawBack(false).recipeContext(this);
  }
}
