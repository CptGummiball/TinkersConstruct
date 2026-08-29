package slimeknights.mantle.client.model;

import com.mojang.math.Transformation;
import net.minecraft.client.resources.model.ModelState;

/**
 * {@link ModelState} built from a transformation and a uv-lock flag. Shim for
 * {@code net.minecraftforge.client.model.SimpleModelState}.
 *
 * <p>Used where a geometry needs to hand a modified rotation down to a nested bake — the gas flip
 * and the fluid-layer nudge in the fluid container model.
 */
public record SimpleModelState(Transformation transformation, boolean uvLock) implements ModelState {
  /** Identity state, matching {@code ModelState}'s own defaults. */
  public static final SimpleModelState IDENTITY = new SimpleModelState(Transformation.identity(), false);

  public SimpleModelState(Transformation transformation) {
    this(transformation, false);
  }

  @Override
  public Transformation getRotation() {
    return transformation;
  }

  @Override
  public boolean isUvLocked() {
    return uvLock;
  }
}
