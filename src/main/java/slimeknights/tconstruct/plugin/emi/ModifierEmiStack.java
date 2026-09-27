package slimeknights.tconstruct.plugin.emi;

import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import slimeknights.tconstruct.library.client.modifiers.ModifierIconManager;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;

import java.util.List;

/**
 * A modifier as an EMI stack, replacing the JEI plugin's modifier ingredient type: it renders
 * through the modifier icon manager, carries the modifier id as its key so lookups group levels
 * of the same modifier, and names itself with the level-aware display name.
 */
public class ModifierEmiStack extends EmiStack {
  private final ModifierEntry entry;

  public ModifierEmiStack(ModifierEntry entry) {
    this.entry = entry;
  }

  public ModifierEntry getEntry() {
    return entry;
  }

  @Override
  public EmiStack copy() {
    ModifierEmiStack copy = new ModifierEmiStack(entry);
    copy.setAmount(getAmount());
    return copy;
  }

  @Override
  public boolean isEmpty() {
    return false;
  }

  @Override
  public DataComponentPatch getComponentChanges() {
    return DataComponentPatch.EMPTY;
  }

  @Override
  public Object getKey() {
    return entry.getId();
  }

  @Override
  public ResourceLocation getId() {
    return entry.getId();
  }

  @Override
  public void render(GuiGraphics draw, int x, int y, float delta, int flags) {
    if ((flags & RENDER_ICON) != 0) {
      ModifierIconManager.renderIcon(draw, entry.getModifier(), x, y, 100, 16);
    }
  }

  @Override
  public List<Component> getTooltipText() {
    return List.of(getName(), entry.getModifier().getDescription(entry.getLevel()));
  }

  @Override
  public Component getName() {
    return entry.getDisplayName();
  }
}
