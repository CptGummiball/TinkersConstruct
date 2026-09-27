package slimeknights.tconstruct.plugin.emi;

import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.client.GuiUtil;
import slimeknights.tconstruct.library.client.materials.MaterialTooltipCache;
import slimeknights.tconstruct.library.materials.definition.MaterialVariant;
import slimeknights.tconstruct.library.recipe.partbuilder.IDisplayPartBuilderRecipe;

import java.util.List;

/**
 * Part builder: pattern stack, material and pattern face on the left, the carved part on the
 * right, with the material name above and the material cost below.
 */
public class PartBuilderEmiRecipe extends TinkerEmiRecipe {
  private static final ResourceLocation BACKGROUND = TConstruct.getResource("textures/gui/jei/tinker_station.png");
  private static final String KEY_COST = TConstruct.makeTranslationKey("jei", "part_builder.cost");

  private final IDisplayPartBuilderRecipe recipe;

  public PartBuilderEmiRecipe(dev.emi.emi.api.recipe.EmiRecipeCategory category, IDisplayPartBuilderRecipe recipe) {
    super(category, null, 121, 46);
    this.recipe = recipe;
  }

  @Override
  public List<EmiIngredient> getInputs() {
    return List.of(items(recipe.getPatternItems()), items(recipe.getMaterialItems()));
  }

  @Override
  public List<EmiStack> getOutputs() {
    return recipe.getResultItems().stream().map(EmiStack::of).toList();
  }

  @Override
  public void addWidgets(WidgetHolder widgets) {
    widgets.addTexture(BACKGROUND, 0, 0, 121, 46, 0, 117);

    MaterialVariant variant = recipe.getMaterial();
    if (!variant.isEmpty()) {
      widgets.addText(MaterialTooltipCache.getColoredDisplayName(variant.getVariant()), 3, 2, -1, true);
      Component cost = Component.translatable(KEY_COST, recipe.getCost());
      widgets.addText(cost, 3, 35, 0xFF808080, false);
    }

    widgets.addSlot(items(recipe.getPatternItems()), 3, 15).drawBack(false);
    widgets.addSlot(items(recipe.getMaterialItems()), 24, 15).drawBack(false);
    // the pattern face has no item form; drawn straight from its texture like the part builder gui
    widgets.addDrawable(46, 16, 16, 16, (graphics, mouseX, mouseY, delta) -> GuiUtil.renderPattern(graphics, recipe.getPattern(), 0, 0));
    widgets.addTooltipText(List.of(recipe.getPattern().getDisplayName()), 46, 16, 16, 16);

    widgets.addSlot(items(recipe.getResultItems()), 95, 14).drawBack(false).recipeContext(this);
  }
}
