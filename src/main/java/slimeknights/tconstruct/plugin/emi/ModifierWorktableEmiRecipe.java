package slimeknights.tconstruct.plugin.emi;

import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.resources.ResourceLocation;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.recipe.worktable.IModifierWorktableRecipe;

import java.util.List;

/**
 * Modifier worktable: the tool and up to two inputs on the left, the modifier options the
 * recipe offers cycling on the right, titled by the recipe.
 */
public class ModifierWorktableEmiRecipe extends TinkerEmiRecipe {
  private static final ResourceLocation BACKGROUND = TConstruct.getResource("textures/gui/jei/tinker_station.png");

  private final IModifierWorktableRecipe recipe;

  public ModifierWorktableEmiRecipe(dev.emi.emi.api.recipe.EmiRecipeCategory category, IModifierWorktableRecipe recipe) {
    super(category, null, 121, 35);
    this.recipe = recipe;
  }

  @Override
  public List<EmiIngredient> getInputs() {
    List<EmiIngredient> inputs = new java.util.ArrayList<>();
    if (recipe.isToolInput()) {
      inputs.add(items(recipe.getInputTools()));
    }
    int max = Math.min(2, recipe.getInputCount());
    for (int i = 0; i < max; i++) {
      inputs.add(items(recipe.getDisplayItems(i)));
    }
    return inputs;
  }

  @Override
  public List<EmiIngredient> getCatalysts() {
    return recipe.isToolInput() ? List.of() : List.of(items(recipe.getInputTools()));
  }

  @Override
  public List<EmiStack> getOutputs() {
    if (recipe.isModifierOutput()) {
      return recipe.getModifierOptions(null).stream().<EmiStack>map(ModifierEmiStack::new).toList();
    }
    return List.of();
  }

  @Override
  public void addWidgets(WidgetHolder widgets) {
    widgets.addTexture(BACKGROUND, 0, 0, 121, 35, 0, 166);
    widgets.addText(recipe.getTitle(), 3, 2, 0xFF404040, false);

    List<net.minecraft.world.item.ItemStack> tools = recipe.getInputTools();
    if (tools.isEmpty()) {
      widgets.addTexture(BACKGROUND, 23, 16, 16, 16, 128, 0);
    } else {
      widgets.addSlot(items(tools), 22, 15).drawBack(false).catalyst(!recipe.isToolInput());
    }
    int max = Math.min(2, recipe.getInputCount());
    for (int i = 0; i < max; i++) {
      List<net.minecraft.world.item.ItemStack> stacks = recipe.getDisplayItems(i);
      if (stacks.isEmpty()) {
        widgets.addTexture(BACKGROUND, 43 + i * 18, 16, 16, 16, 176 + i * 32, 0);
      } else {
        widgets.addSlot(items(stacks), 42 + i * 18, 15).drawBack(false);
      }
    }

    List<dev.emi.emi.api.stack.EmiIngredient> options = recipe.getModifierOptions(null).stream().<dev.emi.emi.api.stack.EmiIngredient>map(ModifierEmiStack::new).toList();
    widgets.addSlot(EmiIngredient.of(options), 81, 15).drawBack(false).recipeContext(this);
  }
}
