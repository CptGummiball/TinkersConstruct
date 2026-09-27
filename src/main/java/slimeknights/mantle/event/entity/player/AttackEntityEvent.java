package slimeknights.mantle.event.entity.player;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

/**
 * Mirror of Forge's {@code AttackEntityEvent}: posted by the event bridge's attack callback
 * before the player's attack runs; cancel to skip the vanilla attack.
 */
public class AttackEntityEvent extends PlayerEvent {
  private final Entity target;

  public AttackEntityEvent(Player player, Entity target) {
    super(player);
    this.target = target;
  }

  public Entity getTarget() {
    return target;
  }

  @Override
  public boolean isCancelable() {
    return true;
  }
}
