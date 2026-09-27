package slimeknights.mantle.item;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;

import javax.annotation.Nullable;

public class BurnableBlockItem extends BlockItem {
  private final int burnTime;
  public BurnableBlockItem(Block blockIn, Properties builder, int burnTime) {
    super(blockIn, builder);
    this.burnTime = burnTime;
    net.fabricmc.fabric.api.registry.FuelRegistry.INSTANCE.add(this, burnTime);
  }

  public int getBurnTime(ItemStack stack, @Nullable RecipeType<?> recipeType) {
    return burnTime;
  }
}
