package slimeknights.tconstruct.library.tools.capability;

import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import team.reborn.energy.api.EnergyStorage;

import javax.annotation.Nullable;

/**
 * Presents a tool's energy to the Fabric ecosystem through Team Reborn's energy API, the
 * one the pack's tech mods speak.
 *
 * <p>Kept apart from {@link ToolEnergyCapability} so that class — and the stat and helpers
 * every tool touches — carries no reference to the energy API: this one is only loaded once
 * {@link #register()} runs, which the tools module guards on the energy mod being present.
 *
 * <p>Forge Energy was int-based with a simulate flag; here amounts widen to long and writes
 * join the caller's transaction. Following the tool fluid handler, the storage works on a
 * single-item copy and publishes it through {@link ContainerItemContext#exchange}, which is
 * what makes it transactional.
 */
public class ToolEnergyStorage implements EnergyStorage {
  private final ContainerItemContext context;

  private ToolEnergyStorage(ContainerItemContext context) {
    this.context = context;
  }

  /** Builds a tool stack over a copy of a single item from the context, or null when absent */
  @Nullable
  private ToolStack singleTool() {
    if (context.getAmount() < 1) {
      return null;
    }
    return ToolStack.from(context.getItemVariant().toStack());
  }

  /** Writes the modified tool back into the context as part of the transaction */
  private boolean publish(ToolStack tool, TransactionContext transaction) {
    return context.exchange(ItemVariant.of(tool.createStack()), 1, transaction) == 1;
  }

  @Override
  public long insert(long maxAmount, TransactionContext transaction) {
    ToolStack tool = singleTool();
    if (tool == null || maxAmount <= 0) {
      return 0;
    }
    int current = ToolEnergyCapability.getEnergy(tool);
    int filled = (int)Math.min(ToolEnergyCapability.getMaxEnergy(tool) - current, Math.min(maxAmount, Integer.MAX_VALUE));
    if (filled <= 0) {
      return 0;
    }
    ToolEnergyCapability.setEnergy(tool, current + filled);
    return publish(tool, transaction) ? filled : 0;
  }

  @Override
  public long extract(long maxAmount, TransactionContext transaction) {
    ToolStack tool = singleTool();
    if (tool == null || maxAmount <= 0) {
      return 0;
    }
    int current = ToolEnergyCapability.getEnergy(tool);
    if (current <= 0) {
      return 0;
    }
    int drained = (int)Math.min(current, Math.min(maxAmount, Integer.MAX_VALUE));
    ToolEnergyCapability.setEnergy(tool, current - drained);
    return publish(tool, transaction) ? drained : 0;
  }

  @Override
  public long getAmount() {
    ToolStack tool = singleTool();
    return tool == null ? 0 : ToolEnergyCapability.getEnergy(tool);
  }

  @Override
  public long getCapacity() {
    ToolStack tool = singleTool();
    return tool == null ? 0 : ToolEnergyCapability.getMaxEnergy(tool);
  }

  /**
   * Exposes tools carrying energy capacity to the ecosystem. Registered as a fallback rather
   * than per item so addon tools are covered too; the capacity check keeps tools without an
   * energy modifier out of the way entirely.
   */
  public static void register() {
    EnergyStorage.ITEM.registerFallback((stack, context) -> {
      if (!(stack.getItem() instanceof IModifiable) || ToolStack.from(stack).getStats().getInt(ToolEnergyCapability.MAX_STAT) <= 0) {
        return null;
      }
      return new ToolEnergyStorage(context);
    });
  }
}
