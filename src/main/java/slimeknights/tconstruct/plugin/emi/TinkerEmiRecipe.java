package slimeknights.tconstruct.plugin.emi;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.resources.ResourceLocation;
import slimeknights.mantle.transfer.fluid.FluidStack;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Base for Tinkers' EMI recipes: carries the category, id and dimensions, and converts between
 * Tinkers' millibucket fluid amounts and EMI's droplets (81 droplets to the millibucket, the
 * Fabric transfer convention EMI displays in).
 */
public abstract class TinkerEmiRecipe implements EmiRecipe {
  private final EmiRecipeCategory category;
  @Nullable
  private final ResourceLocation id;
  private final int width;
  private final int height;

  protected TinkerEmiRecipe(EmiRecipeCategory category, @Nullable ResourceLocation id, int width, int height) {
    this.category = category;
    this.id = id;
    this.width = width;
    this.height = height;
  }

  @Override
  public EmiRecipeCategory getCategory() {
    return category;
  }

  @Nullable
  @Override
  public ResourceLocation getId() {
    return id;
  }

  @Override
  public int getDisplayWidth() {
    return width;
  }

  @Override
  public int getDisplayHeight() {
    return height;
  }

  /** Converts a Tinkers fluid stack to an EMI stack, millibuckets to droplets */
  public static EmiStack fluid(FluidStack stack) {
    return EmiStack.of(stack.getFluid(), stack.getAmount() * 81L);
  }

  /** Converts a list of Tinkers fluid stacks to one cycling EMI ingredient */
  public static EmiIngredient fluids(List<FluidStack> stacks) {
    return EmiIngredient.of(stacks.stream().map(s -> (EmiIngredient) fluid(s)).toList());
  }

  /** Converts a list of item stacks to one cycling EMI ingredient */
  public static EmiIngredient items(List<net.minecraft.world.item.ItemStack> stacks) {
    return EmiIngredient.of(stacks.stream().map(s -> (EmiIngredient) EmiStack.of(s)).toList());
  }

  /** Converts a millibucket capacity to droplets for a tank widget */
  public static int capacity(int mb) {
    return mb * 81;
  }

  /** Centered grey text, the way the JEI categories drew their headers */
  static void addCenteredText(dev.emi.emi.api.widget.WidgetHolder widgets, net.minecraft.network.chat.Component text, int centerX, int y) {
    widgets.addText(text, centerX - net.minecraft.client.Minecraft.getInstance().font.width(text) / 2, y, 0xFF808080, false);
  }
}
