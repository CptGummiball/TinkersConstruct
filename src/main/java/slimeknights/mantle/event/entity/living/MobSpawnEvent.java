package slimeknights.mantle.event.entity.living;

import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.level.ServerLevelAccessor;
import slimeknights.mantle.event.Cancelable;
import slimeknights.mantle.event.Event;

import javax.annotation.Nullable;

/**
 * Mirror of Forge's {@code MobSpawnEvent} family — only {@link FinalizeSpawn}, the one
 * Tinkers listens to (mob equipment injection). Fired from a bridge mixin on
 * {@code Mob.finalizeSpawn} in the event-layer step; cancelling suppresses vanilla's own
 * equipment population, matching Forge's contract.
 */
public class MobSpawnEvent extends Event {

  private final Mob mob;

  protected MobSpawnEvent(Mob mob) {
    this.mob = mob;
  }

  public Mob getEntity() {
    return mob;
  }

  /** Fired at the end of a mob's spawn setup, allowing equipment and data changes. */
  @Cancelable
  public static class FinalizeSpawn extends MobSpawnEvent {

    private final ServerLevelAccessor level;
    private final DifficultyInstance difficulty;
    private final MobSpawnType spawnType;
    @Nullable
    private final SpawnGroupData spawnData;

    public FinalizeSpawn(Mob mob, ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData spawnData) {
      super(mob);
      this.level = level;
      this.difficulty = difficulty;
      this.spawnType = spawnType;
      this.spawnData = spawnData;
    }

    public ServerLevelAccessor getLevel() {
      return level;
    }

    public DifficultyInstance getDifficulty() {
      return difficulty;
    }

    public MobSpawnType getSpawnType() {
      return spawnType;
    }

    /** Spawn group data; 1.21 dropped Forge's extra spawn tag, so this is the whole re-finalize payload. */
    @Nullable
    public SpawnGroupData getSpawnData() {
      return spawnData;
    }
  }
}
