package slimeknights.tconstruct.fabric.events;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Thread-local capture of the item entities spawned while a living entity runs
 * {@code dropAllDeathLoot}, used to post {@link slimeknights.mantle.event.entity.living.LivingDropsEvent}.
 *
 * <p>Deviation from Forge: Forge's captureDrops kept drops out of the world until after the
 * event so listeners could remove them. Here the entities are already spawned when the event
 * fires, so the list is observational — removing from it does nothing, and additions are
 * expected to be pre-spawned (both Tinkers listeners, creeper head drops and chrysophilite,
 * add via {@code entity.spawnAtLocation(...)} exactly like they did on Forge, where the
 * capture was also already consumed by event time). The shim event is not cancelable.
 */
public final class DeathDropsTracker {
  private DeathDropsTracker() {}

  private record Capture(LivingEntity entity, List<ItemEntity> drops) {}

  private static final ThreadLocal<Capture> CAPTURE = new ThreadLocal<>();

  /** Starts capturing drops for the given dying entity. Nested deaths replace the outer capture (vanishingly rare; degrades to an incomplete outer list). */
  public static void begin(LivingEntity entity) {
    CAPTURE.set(new Capture(entity, new ArrayList<>()));
  }

  /** Records a spawned drop if a capture is active for this entity */
  public static void record(Entity entity, ItemEntity drop) {
    Capture capture = CAPTURE.get();
    if (capture != null && capture.entity() == entity) {
      capture.drops().add(drop);
    }
  }

  /** Ends the capture for the entity, returning the recorded drops or null if this entity was not being captured */
  @Nullable
  public static List<ItemEntity> end(LivingEntity entity) {
    Capture capture = CAPTURE.get();
    if (capture != null && capture.entity() == entity) {
      CAPTURE.remove();
      return capture.drops();
    }
    return null;
  }
}
