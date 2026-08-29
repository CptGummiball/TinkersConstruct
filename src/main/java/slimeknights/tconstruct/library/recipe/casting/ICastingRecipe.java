package slimeknights.tconstruct.library.recipe.casting;

import net.minecraft.world.item.ItemStack;
import slimeknights.mantle.transfer.fluid.FluidStack;
import slimeknights.mantle.recipe.ICommonRecipe;
import slimeknights.tconstruct.library.recipe.TinkerRecipeTypes;
import slimeknights.tconstruct.library.recipe.melting.IMeltingRecipe;
import slimeknights.tconstruct.fabric.ContentLookups;

import static slimeknights.tconstruct.library.recipe.melting.IMeltingRecipe.getTemperature;

/**
 * Base interface for all casting recipes
 */
public interface ICastingRecipe extends ICommonRecipe<ICastingContainer> {
  /** Recipe id, stored by the recipe implementations since 1.21 moved it off vanilla recipes; used to reload the active recipe from NBT */
  net.minecraft.resources.ResourceLocation getId();

  @Override
  default ItemStack getToastSymbol() {
    return ContentLookups.toastSymbol(getType() == TinkerRecipeTypes.CASTING_TABLE.get() ? "seared_table" : "seared_basin");
  }

  /**
   * Gets the amount of fluid required for this recipe
   * @param inv  Inventory instance
   * @return Fluid amount when using the fluid in the inventory
   */
  int getFluidAmount(ICastingContainer inv);

  /**
   * TODO 1.21: move this method to {@link IDisplayableCastingRecipe}.
   * {@return true if the cast item is consumed on crafting}
   */
  boolean isConsumed();

  /** {@return true if the cast item is consumed on crafting} */
  default boolean isConsumed(ICastingContainer inv) {
    return isConsumed();
  }

  /** {@return true if the recipe output is placed into the casting input slot} */
  boolean switchSlots();

  /**
   * @param inv ICastingInventory for casting recipe
   * {@return cooling time for the output}
   */
  int getCoolingTime(ICastingContainer inv);

  /**
   * Calculates the cooling time for a recipe based on the amount and temperature
   * @param temperature  Temperature baseline in celsius
   * @param amount       Output amount
   * @return  Cooling time based on the given inputs
   */
  static int calcCoolingTime(int temperature, int amount) {
    // the time in melting reipes assumes updating 5 times a second
    // we update 20 times a second, so get roughly a quart of those values
    return IMeltingRecipe.calcTimeForAmount(temperature, amount);
  }

  /**
   * Calculates the cooling time for a recipe based on the fluid input
   * @param fluid  Fluid input
   * @return  Time for the recipe
   */
  static int calcCoolingTime(FluidStack fluid) {
    return calcCoolingTime(getTemperature(fluid), fluid.getAmount());
  }
}
