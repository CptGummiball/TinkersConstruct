package slimeknights.mantle.util;

import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;

/**
 * Shim of the 1.20 vanilla projectile dispense base; 1.20.5 rebuilt dispensing around
 * {@code ProjectileItem}, which Tinkers' modifiable projectiles do not implement. Keeps the
 * old contract: subclasses provide the projectile, this fires it dispenser-style.
 */
public abstract class AbstractProjectileDispenseBehavior extends DefaultDispenseItemBehavior {

  @Override
  public ItemStack execute(BlockSource source, ItemStack stack) {
    Level level = source.level();
    Position position = DispenserBlock.getDispensePosition(source);
    Direction direction = source.state().getValue(DispenserBlock.FACING);
    Projectile projectile = this.getProjectile(level, position, stack);
    projectile.shoot(direction.getStepX(), (float) direction.getStepY() + 0.1F, direction.getStepZ(), this.getPower(), this.getUncertainty());
    level.addFreshEntity(projectile);
    stack.shrink(1);
    return stack;
  }

  @Override
  protected void playSound(BlockSource source) {
    source.level().levelEvent(1002, source.pos(), 0);
  }

  /** Creates the projectile entity to fire */
  protected abstract Projectile getProjectile(Level level, Position position, ItemStack stack);

  protected float getUncertainty() {
    return 6.0F;
  }

  protected float getPower() {
    return 1.1F;
  }
}
