package slimeknights.mantle.item;

import net.minecraft.world.item.HangingSignItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;

import javax.annotation.Nullable;

public class BurnableHangingSignItem extends HangingSignItem {
  private final int burnTime;
  public BurnableHangingSignItem(Properties propertiesIn, Block hangingBlock, Block wallBlock, int burnTime) {
    super(hangingBlock, wallBlock, propertiesIn);
    this.burnTime = burnTime;
    net.fabricmc.fabric.api.registry.FuelRegistry.INSTANCE.add(this, burnTime);
  }

  public int getBurnTime(ItemStack itemStack, @Nullable RecipeType<?> recipeType) {
    return burnTime;
  }
}
