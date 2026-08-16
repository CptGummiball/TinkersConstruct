package slimeknights.mantle.recipe.helper;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import slimeknights.mantle.Mantle;
import slimeknights.mantle.data.loadable.LoadableMapCodec;
import slimeknights.mantle.data.loadable.field.ContextKey;
import slimeknights.mantle.data.loadable.field.LoadableField;
import slimeknights.mantle.data.loadable.primitive.StringLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.util.typed.TypedMap;
import slimeknights.mantle.util.typed.TypedMapBuilder;

import java.util.function.Supplier;

/**
 * Recipe serializer backed by a {@link RecordLoadable}.
 *
 * <p>1.21 reshape: {@code fromJson}/{@code fromNetwork}/{@code toNetwork} are gone from
 * {@link RecipeSerializer}; JSON goes through {@link #codec()} (bridged by
 * {@link LoadableMapCodec}) and the network through {@link #streamCodec()}. Network errors
 * are still logged with the loadable's identity before rethrowing, since netty otherwise
 * swallows the context.
 *
 * <p><b>{@link ContextKey#ID} is no longer populated.</b> 1.20 handed the recipe id to the
 * serializer; 1.21 keeps it on {@code RecipeHolder} and the serializer never sees it. Recipes
 * that need their id must receive it from their holder — see "the recipe-ID problem" in
 * PORTING.md.
 */
public class LoadableRecipeSerializer<T extends Recipe<?>> implements RecipeSerializer<T> {

  /** Context key to use if you want the recipe serializer passed into your recipe */
  public static final ContextKey<RecipeSerializer<?>> SERIALIZER = new ContextKey<>("serializer");
  /** Context key for a type aware serializer, requires {@link #of(RecordLoadable, Supplier)} */
  public static final ContextKey<TypeAwareRecipeSerializer<?>> TYPED_SERIALIZER = new ContextKey<>("typed_serializer");
  /** Context key for the recipe type, requires {@link #of(RecordLoadable, Supplier)} */
  public static final ContextKey<RecipeType<?>> TYPE = new ContextKey<>("type");
  /** Field for a group key in a recipe (common requirement) */
  public static final LoadableField<String, Recipe<?>> RECIPE_GROUP = StringLoadable.DEFAULT.defaultField("group", "", Recipe::getGroup);

  protected final RecordLoadable<T> loadable;
  private final MapCodec<T> codec;
  private final StreamCodec<RegistryFriendlyByteBuf, T> streamCodec;

  protected LoadableRecipeSerializer(RecordLoadable<T> loadable) {
    this.loadable = loadable;
    this.codec = new LoadableMapCodec<>(loadable, this::buildContext);
    this.streamCodec = StreamCodec.of(this::toNetwork, this::fromNetwork);
  }

  /** Creates a standard serializer from a loadable */
  public static <T extends Recipe<?>> RecipeSerializer<T> of(RecordLoadable<T> loadable) {
    return new LoadableRecipeSerializer<>(loadable);
  }

  /** Creates a type aware serializer from a loadable */
  public static <T extends R, R extends Recipe<?>> TypeAwareRecipeSerializer<T> of(RecordLoadable<T> loadable, Supplier<? extends RecipeType<R>> type) {
    return new TypeAware<>(loadable, type);
  }

  /** Creates a serializer that is deprecated, logging a warning when used */
  public static <T extends Recipe<?>> RecipeSerializer<T> deprecated(RecordLoadable<T> loadable, String replacement) {
    return new Deprecated<>(loadable, replacement);
  }

  /** Builds the parsing context. The recipe id is deliberately absent; see the class javadoc. */
  protected TypedMapBuilder contextBuilder() {
    return TypedMapBuilder.builder()
      .put(ContextKey.DEBUG, "Recipe via " + loadable)
      .put(SERIALIZER, this);
  }

  private TypedMap buildContext() {
    return contextBuilder().build();
  }

  @Override
  public MapCodec<T> codec() {
    return codec;
  }

  @Override
  public StreamCodec<RegistryFriendlyByteBuf, T> streamCodec() {
    return streamCodec;
  }

  private T fromNetwork(RegistryFriendlyByteBuf buffer) {
    try {
      return loadable.decode(buffer, buildContext());
    } catch (RuntimeException e) {
      Mantle.logger.error("{}: Error reading recipe from packet using loadable {}", getClass().getSimpleName(), loadable, e);
      throw e;
    }
  }

  private void toNetwork(RegistryFriendlyByteBuf buffer, T recipe) {
    try {
      loadable.encode(buffer, recipe);
    } catch (RuntimeException e) {
      Mantle.logger.error("{}: Error writing recipe of class {} to packet using loadable {}", getClass().getSimpleName(), recipe.getClass().getSimpleName(), loadable, e);
      throw e;
    }
  }

  public static class TypeAware<T extends Recipe<?>> extends LoadableRecipeSerializer<T> implements TypeAwareRecipeSerializer<T> {

    private final Supplier<? extends RecipeType<?>> type;

    protected TypeAware(RecordLoadable<T> loadable, Supplier<? extends RecipeType<?>> type) {
      super(loadable);
      this.type = type;
    }

    @Override
    protected TypedMapBuilder contextBuilder() {
      return super.contextBuilder().put(TYPE, getType()).put(TYPED_SERIALIZER, this);
    }

    @Override
    public RecipeType<?> getType() {
      return type.get();
    }
  }

  /** Helper class that logs a warning on recipe parse about planned removal */
  private static class Deprecated<T extends Recipe<?>> extends LoadableRecipeSerializer<T> {

    private final String replacement;
    private final MapCodec<T> warningCodec;

    protected Deprecated(RecordLoadable<T> loadable, String replacement) {
      super(loadable);
      this.replacement = replacement;
      // The warning hooks the codec since fromJson no longer exists to override.
      this.warningCodec = super.codec().xmap(this::warn, recipe -> recipe);
    }

    private T warn(T recipe) {
      Mantle.logger.warn("Using deprecated recipe serializer {}, {}", BuiltInRegistries.RECIPE_SERIALIZER.getKey(this), replacement);
      return recipe;
    }

    @Override
    public MapCodec<T> codec() {
      return warningCodec;
    }
  }
}
