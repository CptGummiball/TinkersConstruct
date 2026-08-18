package slimeknights.tconstruct.library.tools.capability;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.modifiers.modules.build.ModifierTraitModule;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.stat.CapacityStat;
import slimeknights.tconstruct.library.tools.stat.ToolStatId;
import slimeknights.tconstruct.tools.TinkerModifiers;

/**
 * Energy storage on a tool.
 *
 * <p>These helpers are Tinkers' own API and stay integer-based, unchanged from the Forge
 * build. The outward face lives in {@link ToolEnergyStorage} instead, so this class carries
 * no reference to the energy API and the mod still loads when no energy mod is installed.
 */
public class ToolEnergyCapability {
  /** Format string to display energy amounts, used internally by the stat */
  public static final String ENERGY_FORMAT = TConstruct.makeDescriptionId("tool_stat", "energy");
  /** Stat marking the max capacity */
  public static final CapacityStat MAX_STAT = new CapacityStat(new ToolStatId(TConstruct.MOD_ID, "max_energy"), 0xa00000, ENERGY_FORMAT);
  /** Persistent data key for fetching the current energy */
  public static final ResourceLocation ENERGY_KEY = TConstruct.getResource("energy");
  /** Include this module in a modifier adding energy capacity or functionality to ensure capacity changes are properly cleaned up */
  public static final ModifierModule ENERGY_HANDLER = new ModifierTraitModule(TinkerModifiers.energyHandler.getId(), 1, true);

  /** Gets the energy capacity for the given tool */
  public static int getMaxEnergy(IToolStackView tool) {
    return tool.getStats().getInt(MAX_STAT);
  }

  /** Gets the current energy on a tool */
  public static int getEnergy(IToolStackView tool) {
    return tool.getPersistentData().getInt(ENERGY_KEY);
  }

  /** Internal method to set energy, skips a capacity check */
  private static void setEnergyRaw(IToolStackView tool, int energy) {
    if (energy == 0) {
      tool.getPersistentData().remove(ENERGY_KEY);
    } else {
      tool.getPersistentData().putInt(ENERGY_KEY, energy);
    }
  }

  /** Sets the energy on the tool */
  public static void setEnergy(IToolStackView tool, int energy) {
    setEnergyRaw(tool, Mth.clamp(energy, 0, getMaxEnergy(tool)));
  }

  /** Adds the given amount of energy to the tool. Can use negative to subtract energy. */
  public static void addEnergy(IToolStackView tool, int energy) {
    if (energy != 0) {
      setEnergy(tool, getEnergy(tool) + energy);
    }
  }

  /** Ensures the tool's energy is within the cap. Generally not necessary to call this directly as it's called by {@link #ENERGY_HANDLER} on tool change. */
  public static void checkEnergy(IToolStackView tool) {
    int energy = ToolEnergyCapability.getEnergy(tool);
    if (energy < 0) {
      ToolEnergyCapability.setEnergyRaw(tool, 0);
    } else {
      int capacity = ToolEnergyCapability.getMaxEnergy(tool);
      if (energy > capacity) {
        ToolEnergyCapability.setEnergyRaw(tool, capacity);
      }
    }
  }

}
