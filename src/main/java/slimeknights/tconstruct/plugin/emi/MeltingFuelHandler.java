package slimeknights.tconstruct.plugin.emi;

import com.mojang.datafixers.util.Pair;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import slimeknights.mantle.transfer.fluid.FluidStack;
import slimeknights.mantle.util.Lazy;
import slimeknights.tconstruct.library.recipe.fuel.MeltingFuel;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Fuel lookup for the melting categories: which fluids can smelt a recipe of a given temperature.
 * Ported unchanged from the JEI plugin's handler of the same name — the display layer changed,
 * the lookup did not.
 */
public class MeltingFuelHandler {
  /**
   * List of pairs of temperature and list of fluids with that or greater temperature.
   * Sorted from highest to lowest temperature
   */
  private static List<Pair<Integer,List<FluidStack>>> fuelLookup = Collections.emptyList();

  /** List of solid fuels for solid melting */
  public static final Lazy<List<ItemStack>> SOLID_FUELS = Lazy.of(() -> Arrays.asList(
    new ItemStack(Items.COAL), new ItemStack(Items.CHARCOAL), new ItemStack(Blocks.OAK_LOG), new ItemStack(Blocks.OAK_PLANKS), new ItemStack(Items.BLAZE_ROD)));

  /** Updates the melting cache, called when the EMI plugin reloads */
  public static void setMeltingFuels(List<MeltingFuel> fuels) {
    fuels.sort(Comparator.comparingInt(MeltingFuel::getTemperature));
    fuelLookup = fuels.stream()
                      .mapToInt(MeltingFuel::getTemperature)
                      .distinct()
                      .mapToObj(temperature -> Pair.of(temperature, fuels.stream()
                          .filter(fuel -> fuel.getTemperature() >= temperature)
                          .flatMap(fuel -> fuel.getInputs().stream())
                          .collect(Collectors.toList())))
                      .collect(Collectors.toList());
  }

  /** Gets a fluid stack list for the given temperature */
  public static List<FluidStack> getUsableFuels(int temperature) {
    for (Pair<Integer,List<FluidStack>> pair : fuelLookup) {
      if (temperature <= pair.getFirst()) {
        return pair.getSecond();
      }
    }
    return Collections.emptyList();
  }
}
