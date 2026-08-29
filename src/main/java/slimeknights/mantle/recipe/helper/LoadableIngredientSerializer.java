package slimeknights.mantle.recipe.helper;

import com.google.gson.JsonObject;
import com.mojang.serialization.MapCodec;
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredient;
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredientSerializer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import slimeknights.mantle.data.loadable.LoadableMapCodec;
import slimeknights.mantle.data.loadable.LoadableStreamCodec;
import slimeknights.mantle.data.loadable.record.RecordLoadable;

/**
 * Ingredient serializer made using loadables.
 *
 * <p>Port note: Forge's {@code IIngredientSerializer} became Fabric's
 * {@link CustomIngredientSerializer}, which wants codecs; the loadable bridges supply them.
 * The record component name satisfies {@link #getIdentifier()} directly.
 */
public record LoadableIngredientSerializer<T extends CustomIngredient>(ResourceLocation getIdentifier, RecordLoadable<T> loadable) implements CustomIngredientSerializer<T> {

  @Override
  public MapCodec<T> getCodec(boolean allowEmpty) {
    return new LoadableMapCodec<>(loadable);
  }

  @Override
  public StreamCodec<RegistryFriendlyByteBuf, T> getPacketCodec() {
    return new LoadableStreamCodec<>(loadable);
  }

  /** Serializes the ingredient to JSON, for datagen */
  public JsonObject serialize(T ingredient) {
    JsonObject json = new JsonObject();
    json.addProperty("type", getIdentifier.toString());
    loadable.serialize(ingredient, json);
    return json;
  }
}
