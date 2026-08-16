package slimeknights.tconstruct.library.tools.item;

import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;

/** Dummy tier implementation to allow Tinkers' Construct to make modifiable items extend TieredItem, for piglin compat */
public enum TinkerTier implements Tier {
  INSTANCE;

  @Override
  public int getUses() {
    return 0;
  }

  @Override
  public float getSpeed() {
    return 0;
  }

  @Override
  public float getAttackDamageBonus() {
    return 0;
  }

  @Override
  public net.minecraft.tags.TagKey<net.minecraft.world.level.block.Block> getIncorrectBlocksForDrops() {
    // harvest correctness is Tinkers' own logic; the least restrictive vanilla tag keeps the dummy tier out of the way
    return net.minecraft.tags.BlockTags.INCORRECT_FOR_NETHERITE_TOOL;
  }

  @Deprecated
  public int getLevel() {
    return 0;
  }

  @Override
  public int getEnchantmentValue() {
    return 0;
  }

  @Override
  public Ingredient getRepairIngredient() {
    return Ingredient.EMPTY;
  }
}
