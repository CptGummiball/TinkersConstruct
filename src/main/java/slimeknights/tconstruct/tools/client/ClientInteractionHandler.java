package slimeknights.tconstruct.tools.client;

import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.HitResult;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.common.network.TinkerNetwork;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InteractionSource;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.tools.logic.InteractionHandler;
import slimeknights.tconstruct.tools.network.InteractWithAirPacket;

/**
 * Client side interaction hooks.
 *
 * <p>Fabric port: Forge fired {@code PlayerInteractEvent.RightClickEmpty} and {@code LeftClickEmpty}
 * from patches inside {@code Minecraft.startUseItem} and {@code startAttack}, plus
 * {@code InputEvent.InteractionKeyMappingTriggered} to break out of the two-hand loop. Fabric has no
 * counterpart for any of them, so {@code MinecraftInteractionMixin} calls the two methods below from
 * the same two places. Cancelling the whole of {@code startUseItem} replaces the
 * {@code cancelNextOffhand} flag upstream needed, since Forge could only suppress one hand at a time.
 */
public class ClientInteractionHandler {
  private ClientInteractionHandler() {}

  /**
   * Implements the client side of chestplate {@link slimeknights.tconstruct.library.modifiers.hook.interaction.GeneralInteractionModifierHook#onToolUse(IToolStackView, ModifierEntry, Player, InteractionHand, InteractionSource)}.
   *
   * <p>Called once per hand, in vanilla's order, and only for a hand holding nothing with nothing
   * under the crosshair — the condition Forge's event carried.
   *
   * @return true if the interaction was handled and the rest of the item use should be skipped
   */
  public static boolean onRightClickEmpty(Player player, InteractionHand hand) {
    ItemStack chestplate = player.getItemBySlot(EquipmentSlot.CHEST);
    if (player.isSpectator() || !chestplate.is(TinkerTags.Items.INTERACTABLE_ARMOR)) {
      return false;
    }
    // found an interaction, time to notify the server and run logic for the client
    TinkerNetwork.getInstance().sendToServer(InteractWithAirPacket.fromChestplate(hand));
    InteractionResult result = InteractionHandler.onChestplateUse(player, chestplate, hand);
    if (result.consumesAction()) {
      if (result.shouldSwing()) {
        player.swing(hand);
      }
      Minecraft.getInstance().gameRenderer.itemInHandRenderer.itemUsed(hand);
      return true;
    }
    return false;
  }

  /**
   * Implements the client side of left click interaction for {@link slimeknights.tconstruct.library.modifiers.hook.interaction.GeneralInteractionModifierHook#onToolUse(IToolStackView, ModifierEntry, Player, InteractionHand, InteractionSource)}.
   *
   * @return true if the interaction was handled and vanilla's swing should be skipped
   */
  public static boolean onLeftClickEmpty(Player player) {
    ItemStack tool = player.getMainHandItem();
    if (player.isSpectator() || !tool.is(TinkerTags.Items.INTERACTABLE_LEFT)) {
      return false;
    }
    // found an interaction, time to notify the server and run logic for the client
    TinkerNetwork.getInstance().sendToServer(InteractWithAirPacket.LEFT_CLICK);
    InteractionResult result = InteractionHandler.onLeftClickInteraction(player, tool, InteractionHand.MAIN_HAND);
    if (result.consumesAction()) {
      if (result.shouldSwing()) {
        player.swing(InteractionHand.MAIN_HAND);
      }
      Minecraft.getInstance().gameRenderer.itemInHandRenderer.itemUsed(InteractionHand.MAIN_HAND);
      return true;
    }
    return false;
  }

  /** True when nothing is under the crosshair, which is what both events required */
  public static boolean missedEverything(Minecraft minecraft) {
    HitResult hit = minecraft.hitResult;
    return hit == null || hit.getType() == HitResult.Type.MISS;
  }
}
