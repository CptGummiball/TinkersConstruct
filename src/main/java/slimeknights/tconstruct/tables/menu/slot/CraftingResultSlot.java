package slimeknights.tconstruct.tables.menu.slot;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.item.ItemStack;
import slimeknights.mantle.event.ForgeEventFactory;
import slimeknights.mantle.inventory.CustomResultSlot;
import slimeknights.mantle.inventory.IContainerCraftingCustom;

import javax.annotation.Nonnull;

/**
 * Same as {@link CustomResultSlot}, but does not require an crafting inventory
 */
public class CraftingResultSlot extends ResultSlot {
  private final IContainerCraftingCustom callback;
  // 1.21 made ResultSlot's player and removeCount private, so track our own copies;
  // every read/write below goes through these, the superclass fields stay unused
  private final Player crafter;
  private int amountCrafted;

  @SuppressWarnings("ConstantConditions")
  public CraftingResultSlot(IContainerCraftingCustom callback, Player player, Container inv, int index, int x, int y) {
    // pass in null for CraftingInventory
    super(player, null, inv, index, x, y);
    this.callback = callback;
    this.crafter = player;
  }

  /* Methods that reference CraftingInventory */

  @Override
  public ItemStack remove(int amount) {
    if (this.hasItem()) {
      this.amountCrafted += Math.min(amount, this.getItem().getCount());
    }
    return super.remove(amount);
  }

  @Override
  protected void onQuickCraft(ItemStack stack, int amount) {
    this.amountCrafted += amount;
    this.checkTakeAchievements(stack);
  }

  @Override
  protected void checkTakeAchievements(ItemStack stack) {
    if (this.amountCrafted > 0) {
      stack.onCraftedBy(this.crafter.level(), this.crafter, this.amountCrafted);
      ForgeEventFactory.firePlayerCraftingEvent(this.crafter, stack, this.container);
    }
    this.amountCrafted = 0;
  }

  @Override
  public void onTake(Player playerIn, @Nonnull ItemStack stack) {
    this.checkTakeAchievements(stack);
    this.callback.onCrafting(playerIn, stack, this.container);
  }
}
