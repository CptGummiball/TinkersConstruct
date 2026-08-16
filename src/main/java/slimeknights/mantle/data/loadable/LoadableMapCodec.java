package slimeknights.mantle.data.loadable;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.util.typed.TypedMap;

import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Adapts a {@link RecordLoadable} to a {@link MapCodec}.
 *
 * <p>This is the bridge 1.21 forces. Recipe serializers used to expose
 * {@code fromJson}/{@code toNetwork}; now they expose {@code codec()} returning a
 * {@code MapCodec} and {@code streamCodec()}. Mantle's whole serialization layer is built on
 * {@link Loadable}, so rather than rewrite ~400 loadable definitions the loadable is adapted
 * to the codec interface here.
 *
 * <p>Conversion goes through {@code JsonOps}, matching the existing {@link LoadableCodec}.
 * That costs an intermediate JSON tree when the ops are NBT, but loadables are defined in
 * terms of {@code JsonElement} throughout, and recipe (de)serialization happens on datapack
 * load rather than per tick.
 *
 * <p><b>Limitation:</b> {@link #keys} returns an empty stream. A loadable does not expose its
 * field names, and the value is only used by strict ops to report unknown keys — decoding and
 * encoding are unaffected.
 */
public class LoadableMapCodec<T> extends MapCodec<T> {

  private final RecordLoadable<T> loadable;
  private final Supplier<TypedMap> context;

  public LoadableMapCodec(RecordLoadable<T> loadable, Supplier<TypedMap> context) {
    this.loadable = loadable;
    this.context = context;
  }

  public LoadableMapCodec(RecordLoadable<T> loadable) {
    this(loadable, () -> TypedMap.EMPTY);
  }

  @Override
  public <O> DataResult<T> decode(DynamicOps<O> ops, MapLike<O> input) {
    JsonObject json = new JsonObject();
    input.entries().forEach(entry -> ops.getStringValue(entry.getFirst())
      .result()
      .ifPresent(key -> json.add(key, ops.convertTo(JsonOps.INSTANCE, entry.getSecond()))));
    try {
      return DataResult.success(loadable.deserialize(json, context.get()));
    } catch (RuntimeException e) {
      // Loadables signal failure by throwing; codecs signal it by returning an error.
      return DataResult.error(() -> describe(e));
    }
  }

  @Override
  public <O> RecordBuilder<O> encode(T input, DynamicOps<O> ops, RecordBuilder<O> prefix) {
    JsonElement serialized;
    try {
      serialized = loadable.serialize(input);
    } catch (RuntimeException e) {
      return prefix.withErrorsFrom(DataResult.error(() -> describe(e)));
    }
    if (!(serialized instanceof JsonObject json)) {
      return prefix.withErrorsFrom(DataResult.error(
        () -> loadable + " serialized to " + serialized + " rather than an object"));
    }
    RecordBuilder<O> builder = prefix;
    for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
      builder = builder.add(entry.getKey(), JsonOps.INSTANCE.convertTo(ops, entry.getValue()));
    }
    return builder;
  }

  @Override
  public <O> Stream<O> keys(DynamicOps<O> ops) {
    return Stream.empty();
  }

  private String describe(RuntimeException e) {
    String message = e.getMessage();
    return message == null ? e.getClass().getSimpleName() : message;
  }

  @Override
  public String toString() {
    return "LoadableMapCodec[" + loadable + "]";
  }
}
