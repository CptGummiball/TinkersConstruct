package slimeknights.tconstruct.tools.logic;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import slimeknights.mantle.event.MinecraftForge;
import slimeknights.mantle.event.entity.living.LivingMiscEvents.LivingEquipmentChangeEvent;
import slimeknights.tconstruct.library.events.ToolEquipmentChangeEvent;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.tools.context.EquipmentChangeContext;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

/**
 * Runs the modifier equipment change hooks whenever a living entity's equipment changes.
 */
public class EquipmentChangeWatcher {
  private EquipmentChangeWatcher() {}

  /** Registers the equipment change listener */
  public static void init() {
    // equipment change is used on both sides
    MinecraftForge.EVENT_BUS.addListener(LivingEquipmentChangeEvent.class, EquipmentChangeWatcher::onEquipmentChange);
    // PORT (phase 5 client): the Forge build additionally attached a PlayerLastEquipment capability to client-side
    // players and polled it from PlayerTickEvent, since LivingEquipmentChangeEvent only fires on the server.
    // That client-side equipment polling returns with the client phase.
  }


  /* Events */

  /** Serverside modifier hooks */
  private static void onEquipmentChange(LivingEquipmentChangeEvent event) {
    runModifierHooks(event.getEntity(), event.getSlot(), event.getFrom(), event.getTo());
  }


  /* Helpers */

  /** Shared modifier hook logic */
  private static void runModifierHooks(LivingEntity entity, EquipmentSlot changedSlot, ItemStack original, ItemStack replacement) {
    EquipmentChangeContext context = new EquipmentChangeContext(entity, changedSlot, original, replacement);

    // first, fire event to notify an item was removed
    IToolStackView tool = context.getOriginalTool();
    if (tool != null && ModifierUtil.validArmorSlot(tool, changedSlot)) {
      for (ModifierEntry entry : tool.getModifierList()) {
        entry.getHook(ModifierHooks.EQUIPMENT_CHANGE).onUnequip(tool, entry, context);
      }
    }

    // next, fire event to notify an item was added
    tool = context.getReplacementTool();
    if (tool != null && ModifierUtil.validArmorSlot(tool, changedSlot)) {
      for (ModifierEntry entry : tool.getModifierList()) {
        entry.getHook(ModifierHooks.EQUIPMENT_CHANGE).onEquip(tool, entry, context);
      }
    }

    // finally, fire events on all other slots to say something changed
    for (EquipmentSlot otherSlot : EquipmentSlot.values()) {
      if (otherSlot != changedSlot) {
        tool = context.getValidTool(otherSlot);
        if (tool != null) {
          for (ModifierEntry entry : tool.getModifierList()) {
            entry.getHook(ModifierHooks.EQUIPMENT_CHANGE).onEquipmentChange(tool, entry, context, otherSlot);
          }
        }
      }
    }
    // fire event for modifiers that want to watch equipment when not equipped
    MinecraftForge.EVENT_BUS.post(new ToolEquipmentChangeEvent(context));
  }
}
