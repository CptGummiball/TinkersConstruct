package slimeknights.mantle.client.model.generators;

import com.google.common.base.Preconditions;
import com.google.gson.JsonObject;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * A model with blockstate configuration: rotation, uv lock and weight.
 * Port of Forge's class of the same name.
 */
public final class ConfiguredModel {
  /** The default random weight of configured models, used if not otherwise set */
  public static final int DEFAULT_WEIGHT = 1;

  public final ModelFile model;
  public final int rotationX;
  public final int rotationY;
  public final boolean uvLock;
  public final int weight;

  private static IntStreamCheck checkRotation = (rot, axis) ->
    Preconditions.checkArgument(rot % 90 == 0, "Invalid %s rotation %s, must be a multiple of 90", axis, rot);

  private interface IntStreamCheck {
    void check(int rotation, String axis);
  }

  public ConfiguredModel(ModelFile model, int rotationX, int rotationY, boolean uvLock, int weight) {
    Preconditions.checkNotNull(model);
    this.model = model;
    checkRotation.check(rotationX, "X");
    checkRotation.check(rotationY, "Y");
    this.rotationX = normalize(rotationX);
    this.rotationY = normalize(rotationY);
    this.uvLock = uvLock;
    Preconditions.checkArgument(weight > 0, "Model weight must be greater than zero: %s", weight);
    this.weight = weight;
  }

  public ConfiguredModel(ModelFile model, int rotationX, int rotationY, boolean uvLock) {
    this(model, rotationX, rotationY, uvLock, DEFAULT_WEIGHT);
  }

  public ConfiguredModel(ModelFile model) {
    this(model, 0, 0, false);
  }

  private static int normalize(int rotation) {
    return ((rotation % 360) + 360) % 360;
  }

  public JsonObject toJSON(boolean includeWeight) {
    JsonObject modelJson = new JsonObject();
    modelJson.addProperty("model", model.getLocation().toString());

    if (rotationX != 0) {
      modelJson.addProperty("x", rotationX);
    }
    if (rotationY != 0) {
      modelJson.addProperty("y", rotationY);
    }
    if (uvLock) {
      modelJson.addProperty("uvlock", uvLock);
    }
    if (includeWeight && weight != DEFAULT_WEIGHT) {
      modelJson.addProperty("weight", weight);
    }
    return modelJson;
  }

  public static Builder<?> builder() {
    return new Builder<>();
  }

  static Builder<VariantBlockStateBuilder> builder(VariantBlockStateBuilder outer, VariantBlockStateBuilder.PartialBlockstate state) {
    return new Builder<>(models -> outer.setModels(state, models), new ArrayList<>());
  }

  static Builder<MultiPartBlockStateBuilder.PartBuilder> builder(MultiPartBlockStateBuilder outer) {
    return new Builder<>(models -> {
      MultiPartBlockStateBuilder.PartBuilder ret = outer.new PartBuilder(new BlockStateProvider.ConfiguredModelList(models));
      outer.addPart(ret);
      return ret;
    }, new ArrayList<>());
  }

  /** Builds one or more configured models, returning the owner object on completion */
  public static class Builder<T> {
    private ModelFile model;
    @Nullable
    private final Function<ConfiguredModel[], T> callback;
    private final List<ConfiguredModel> otherModels;
    private int rotationX;
    private int rotationY;
    private boolean uvLock;
    private int weight = DEFAULT_WEIGHT;

    Builder() {
      this(null, List.of());
    }

    Builder(@Nullable Function<ConfiguredModel[], T> callback, List<ConfiguredModel> otherModels) {
      this.callback = callback;
      this.otherModels = otherModels;
    }

    /** Sets the model file */
    public Builder<T> modelFile(ModelFile model) {
      Preconditions.checkNotNull(model, "Model must not be null");
      this.model = model;
      return this;
    }

    /** Sets the x-rotation, must be a multiple of 90 */
    public Builder<T> rotationX(int value) {
      checkRotation.check(value, "X");
      rotationX = value;
      return this;
    }

    /** Sets the y-rotation, must be a multiple of 90 */
    public Builder<T> rotationY(int value) {
      checkRotation.check(value, "Y");
      rotationY = value;
      return this;
    }

    public Builder<T> uvLock(boolean value) {
      uvLock = value;
      return this;
    }

    /** Sets the random weight */
    public Builder<T> weight(int value) {
      weight = value;
      return this;
    }

    /** Builds the current model and returns it, ignoring the owner */
    public ConfiguredModel buildLast() {
      return new ConfiguredModel(model, rotationX, rotationY, uvLock, weight);
    }

    /** Builds all models so far */
    public ConfiguredModel[] build() {
      return Stream.concat(otherModels.stream(), Stream.of(buildLast())).toArray(ConfiguredModel[]::new);
    }

    /** Applies the models to the owner callback and returns it */
    public T addModel() {
      Preconditions.checkNotNull(callback, "Cannot use addModel() without an owning builder present");
      return callback.apply(build());
    }

    /** Completes the current model and starts a new one */
    public Builder<T> nextModel() {
      Preconditions.checkNotNull(callback, "Cannot use nextModel() without an owning builder present");
      return new Builder<>(callback, Arrays.asList(build()));
    }
  }
}
