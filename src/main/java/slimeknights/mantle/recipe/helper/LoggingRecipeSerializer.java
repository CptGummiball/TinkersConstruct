package slimeknights.mantle.recipe.helper;

import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import slimeknights.mantle.Mantle;

import javax.annotation.Nullable;

/**
 * Recipe serializer that logs network exceptions before throwing them, as otherwise they can
 * vanish into netty's pipeline with no context.
 *
 * <p>1.21 reshape: the {@code fromNetwork}/{@code toNetwork} pair this interface used to wrap
 * no longer exists on {@link RecipeSerializer} — network (de)serialisation goes through
 * {@link #streamCodec()} now, so the logging wrap lives in the default implementation of that
 * method instead. Implementors provide the same two methods as before, minus the recipe id,
 * which 1.21 no longer hands to serializers (it lives on {@code RecipeHolder}).
 */
public interface LoggingRecipeSerializer<T extends Recipe<?>> extends RecipeSerializer<T> {

  /**
   * Reads the recipe from the packet.
   * @throws RuntimeException  If any errors happen, the exception will be logged automatically
   */
  @Nullable
  T fromNetworkSafe(RegistryFriendlyByteBuf buffer);

  /**
   * Writes the recipe to the buffer.
   * @throws RuntimeException  If any errors happen, the exception will be logged automatically
   */
  void toNetworkSafe(RegistryFriendlyByteBuf buffer, T recipe);

  @Override
  default StreamCodec<RegistryFriendlyByteBuf, T> streamCodec() {
    return StreamCodec.of(
      (buffer, recipe) -> {
        try {
          toNetworkSafe(buffer, recipe);
        } catch (RuntimeException e) {
          String error = getClass().getSimpleName() + ": Error writing recipe of class "
            + recipe.getClass().getSimpleName() + " to packet";
          Mantle.logger.error("{}", error, e);
          throw new EncoderException(error + " - " + e.getMessage(), e);
        }
      },
      buffer -> {
        try {
          T recipe = fromNetworkSafe(buffer);
          if (recipe == null) {
            throw new DecoderException(getClass().getSimpleName() + " returned null from the network");
          }
          return recipe;
        } catch (RuntimeException e) {
          String error = getClass().getSimpleName() + ": Error reading recipe from packet";
          Mantle.logger.error("{}", error, e);
          throw e instanceof DecoderException ? e : new DecoderException(error + " - " + e.getMessage(), e);
        }
      });
  }
}
