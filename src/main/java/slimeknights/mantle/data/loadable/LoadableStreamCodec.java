package slimeknights.mantle.data.loadable;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import slimeknights.mantle.util.typed.TypedMap;

import java.util.function.Supplier;

/**
 * Adapts a {@link Loadable} to a {@link StreamCodec}, the network half of what 1.21's
 * {@code RecipeSerializer} now expects.
 *
 * <p>This side is a straight delegation: {@link Streamable#encode}/{@link Streamable#decode}
 * already take a {@link RegistryFriendlyByteBuf} after the framework was widened, so no
 * intermediate representation is involved — unlike {@link LoadableMapCodec}, which has to
 * route through JSON.
 */
public class LoadableStreamCodec<T> implements StreamCodec<RegistryFriendlyByteBuf, T> {

  private final Loadable<T> loadable;
  private final Supplier<TypedMap> context;

  public LoadableStreamCodec(Loadable<T> loadable, Supplier<TypedMap> context) {
    this.loadable = loadable;
    this.context = context;
  }

  public LoadableStreamCodec(Loadable<T> loadable) {
    this(loadable, () -> TypedMap.EMPTY);
  }

  @Override
  public T decode(RegistryFriendlyByteBuf buffer) {
    return loadable.decode(buffer, context.get());
  }

  @Override
  public void encode(RegistryFriendlyByteBuf buffer, T value) {
    loadable.encode(buffer, value);
  }

  @Override
  public String toString() {
    return "LoadableStreamCodec[" + loadable + "]";
  }
}
