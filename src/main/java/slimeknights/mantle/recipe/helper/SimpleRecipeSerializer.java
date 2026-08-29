package slimeknights.mantle.recipe.helper;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;

import java.util.function.Supplier;

/**
 * Serializer for recipes with no properties of their own.
 *
 * <p>The 1.20 version constructed from a {@code Function<ResourceLocation,T>} — the recipe
 * received its own id. 1.21 keeps the id outside the recipe on {@code RecipeHolder} and never
 * gives it to the serializer, so the constructor is now a plain {@link Supplier}. Callers that
 * stored the id must take it from their holder instead (see "the recipe-ID problem" in
 * PORTING.md).
 */
public record SimpleRecipeSerializer<T extends Recipe<?>>(Supplier<T> constructor) implements RecipeSerializer<T> {

  @Override
  public MapCodec<T> codec() {
    return MapCodec.unit(constructor);
  }

  @Override
  public StreamCodec<RegistryFriendlyByteBuf, T> streamCodec() {
    return StreamCodec.of((buffer, recipe) -> {}, buffer -> constructor.get());
  }
}
