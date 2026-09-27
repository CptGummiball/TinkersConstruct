package slimeknights.tconstruct.plugin.emi;

import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.resources.ResourceLocation;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.recipe.molding.MoldingRecipe;
import slimeknights.tconstruct.library.recipe.TinkerRecipeTypes;

import java.util.List;

/**
 * Molding sand casts on the table or basin: material below, pattern above, the block icon showing
 * where the shaping happens.
 */
public class MoldingEmiRecipe extends TinkerEmiRecipe {
  private static final ResourceLocation BACKGROUND = TConstruct.getResource("textures/gui/jei/casting.png");

  private final MoldingRecipe recipe;

  public MoldingEmiRecipe(dev.emi.emi.api.recipe.EmiRecipeCategory category, MoldingRecipe recipe) {
    super(category, null, 70, 57);
    this.recipe = recipe;
  }

  @Override
  public List<EmiIngredient> getInputs() {
    if (recipe.getPattern().isEmpty()) {
      return List.of(EmiIngredient.of(recipe.getMaterial()));
    }
    return List.of(EmiIngredient.of(recipe.getMaterial()), EmiIngredient.of(recipe.getPattern()));
  }

  @Override
  public List<EmiStack> getOutputs() {
    return List.of(EmiStack.of(recipe.getResultItem(net.minecraft.client.Minecraft.getInstance().level.registryAccess())));
  }

  @Override
  public void addWidgets(WidgetHolder widgets) {
    widgets.addTexture(BACKGROUND, 0, 0, 70, 57, 0, 55);

    boolean table = recipe.getType() == TinkerRecipeTypes.MOLDING_TABLE.get();
    // the block being molded on, under both sides
    widgets.addTexture(BACKGROUND, 3, 40, 16, 16, 117, table ? 0 : 16);
    widgets.addTexture(BACKGROUND, 51, 40, 16, 16, 117, table ? 0 : 16);

    widgets.addSlot(EmiIngredient.of(recipe.getMaterial()), 2, 23).drawBack(false);
    if (!recipe.getPattern().isEmpty()) {
      widgets.addSlot(EmiIngredient.of(recipe.getPattern()), 2, 0).drawBack(false).catalyst(!recipe.isPatternConsumed());
      if (!recipe.isPatternConsumed()) {
        // pattern returns after molding
        widgets.addSlot(EmiIngredient.of(recipe.getPattern()), 50, 7).drawBack(false);
        widgets.addTexture(BACKGROUND, 8, 17, 6, 6, 76, 55);
      } else {
        widgets.addTexture(BACKGROUND, 8, 17, 6, 6, 70, 55);
      }
    }
    widgets.addSlot(EmiStack.of(recipe.getResultItem(net.minecraft.client.Minecraft.getInstance().level.registryAccess())), 50, 23)
           .drawBack(false).recipeContext(this);
  }
}
