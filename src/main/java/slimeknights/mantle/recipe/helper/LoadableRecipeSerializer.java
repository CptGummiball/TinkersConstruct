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
 * <p>{@link ContextKey#ID} is restored on both paths. On the JSON path the RecipeManager mixin
 * publishes the id being parsed through {@link CurrentRecipeId} and {@link #contextBuilder()} picks
 * it up. On the network path 1.21 gives the serializer no id at all — the {@code RecipeHolder} keeps
 * it and writes it separately — so the id is written into the payload here and read back on the
 * other side. Without it, the 67 recipe classes that declare the id as a required context field
 * throw while decoding and take the joining player's connection with them.
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

  /** Builds the parsing context; the id is present during JSON loading, absent on the network */
  protected TypedMapBuilder contextBuilder() {
    TypedMapBuilder builder = TypedMapBuilder.builder()
      .put(ContextKey.DEBUG, "Recipe via " + loadable)
      .put(SERIALIZER, this);
    net.minecraft.resources.ResourceLocation id = CurrentRecipeId.get();
    if (id != null) {
      builder.put(ContextKey.ID, id);
    }
    return builder;
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
      net.minecraft.resources.ResourceLocation id = buffer.readNullable(net.minecraft.network.FriendlyByteBuf::readResourceLocation);
      TypedMapBuilder context = contextBuilder();
      if (id != null && CurrentRecipeId.get() == null) {
        context.put(ContextKey.ID, id);
      }
      return loadable.decode(buffer, context.build());
    } catch (RuntimeException e) {
      Mantle.logger.error("{}: Error reading recipe from packet using loadable {}", getClass().getSimpleName(), loadable, e);
      throw e;
    }
  }

  private void toNetwork(RegistryFriendlyByteBuf buffer, T recipe) {
    try {
      buffer.writeNullable(recipeId(recipe), net.minecraft.network.FriendlyByteBuf::writeResourceLocation);
      loadable.encode(buffer, recipe);
    } catch (RuntimeException e) {
      // the id is worth more than the loadable dump when a single recipe out of thousands fails;
      // 1.21 recipes carry no id, but every loadable recipe reads one into a field it can expose
      Mantle.logger.error("{}: Error writing recipe {} of class {} to packet", getClass().getSimpleName(), recipeId(recipe), recipe.getClass().getSimpleName(), e);
      throw e;
    }
  }

  /** One reflective lookup per recipe class, not per recipe */
  private static final java.util.Map<Class<?>,java.util.Optional<java.lang.reflect.AccessibleObject>> ID_ACCESSORS = new java.util.concurrent.ConcurrentHashMap<>();

  /**
   * Id of a recipe that keeps one, or null.
   *
   * <p>1.21 moved the id out of {@code Recipe} and onto {@code RecipeHolder}, but every recipe whose
   * loadable declares {@link ContextKey#ID} still stores it and exposes a getter — often generated
   * by Lombok, so there is no shared interface to ask. Reflection finds it once per class.
   */
  @javax.annotation.Nullable
  private static net.minecraft.resources.ResourceLocation recipeId(Recipe<?> recipe) {
    if (recipe instanceof slimeknights.mantle.registration.object.IdAwareObject aware) {
      return aware.getId();
    }
    java.util.Optional<java.lang.reflect.AccessibleObject> accessor = ID_ACCESSORS.computeIfAbsent(recipe.getClass(), LoadableRecipeSerializer::findIdAccessor);
    if (accessor.isPresent()) {
      try {
        java.lang.reflect.AccessibleObject member = accessor.get();
        if (member instanceof java.lang.reflect.Method method) {
          return (net.minecraft.resources.ResourceLocation)method.invoke(recipe);
        }
        return (net.minecraft.resources.ResourceLocation)((java.lang.reflect.Field)member).get(recipe);
      } catch (ReflectiveOperationException | RuntimeException e) {
        return null;
      }
    }
    return null;
  }

  /** A getter if the class has one, else the field itself — several recipes keep the id without a getter */
  private static java.util.Optional<java.lang.reflect.AccessibleObject> findIdAccessor(Class<?> cls) {
    try {
      java.lang.reflect.Method method = cls.getMethod("getId");
      if (net.minecraft.resources.ResourceLocation.class.isAssignableFrom(method.getReturnType())) {
        return java.util.Optional.of(method);
      }
    } catch (NoSuchMethodException e) {
      // fall through to the field
    }
    for (Class<?> current = cls; current != null && current != Object.class; current = current.getSuperclass()) {
      try {
        java.lang.reflect.Field field = current.getDeclaredField("id");
        if (net.minecraft.resources.ResourceLocation.class.isAssignableFrom(field.getType())) {
          field.setAccessible(true);
          return java.util.Optional.of(field);
        }
      } catch (NoSuchFieldException | RuntimeException e) {
        // keep walking up
      }
    }
    return java.util.Optional.empty();
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
