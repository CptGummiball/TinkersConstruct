package slimeknights.tconstruct.plugin.emi;

import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.recipe.FluidValues;
import slimeknights.tconstruct.library.recipe.entitymelting.EntityMeltingRecipe;

import java.util.List;

/**
 * Melting entities in the smeltery. The entity spins in the box on the left the way the JEI
 * category drew it; the spawn eggs sit invisibly underneath so the recipe tree and lookups still
 * see item inputs.
 */
public class EntityMeltingEmiRecipe extends TinkerEmiRecipe {
  private static final ResourceLocation BACKGROUND = MeltingEmiRecipe.BACKGROUND;
  private static final String KEY_PER_HEARTS = TConstruct.makeTranslationKey("jei", "entity_melting.per_hearts");

  private final EntityMeltingRecipe recipe;

  public EntityMeltingEmiRecipe(dev.emi.emi.api.recipe.EmiRecipeCategory category, EntityMeltingRecipe recipe) {
    super(category, null, 150, 62);
    this.recipe = recipe;
  }

  @Override
  public List<EmiIngredient> getInputs() {
    return List.of(items(recipe.getIngredient().getEggs()));
  }

  @Override
  public List<EmiStack> getOutputs() {
    return List.of(fluid(recipe.getOutput()));
  }

  @Override
  public void addWidgets(WidgetHolder widgets) {
    widgets.addTexture(BACKGROUND, 0, 0, 150, 62, 0, 41);

    // eggs give the hover target and the recipe tree link; the entity draws over them
    widgets.addSlot(items(recipe.getIngredient().getEggs()), 26, 18).drawBack(false);
    addEntityRenderer(widgets, recipe.getIngredient().getDisplay().stream().<EntityType<?>>map(input -> input.type()).toList(), 19, 11);

    // damage dealt per melt, next to the heart in the background
    String damage = Float.toString(recipe.getDamage() / 2f);
    widgets.addText(Component.literal(damage), 84 - Minecraft.getInstance().font.width(damage), 8, 0xFFFF0000, false);
    widgets.addTooltipText(List.of(Component.translatable(KEY_PER_HEARTS, recipe.getDamage() / 2f)), 68, 4, 18, 12);

    // arrow, output, fuels
    widgets.addAnimatedTexture(BACKGROUND, 71, 21, 24, 17, 150, 41, 10000, true, false, false);
    widgets.addTank(fluid(recipe.getOutput()), 115, 11, 18, 34, capacity(FluidValues.INGOT * 2)).drawBack(false).recipeContext(this);
    var fuels = MeltingFuelHandler.getUsableFuels(1);
    if (!fuels.isEmpty()) {
      widgets.addTank(fluids(fuels), 75, 43, 16, 16, 1).drawBack(false).catalyst(true);
      widgets.addTexture(BACKGROUND, 75, 43, 16, 16, 150, 74);
    }
  }
}
