package slimeknights.mantle.data.loadable.common;

import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.item.crafting.Ingredient;
import slimeknights.mantle.data.loadable.ErrorFactory;
import slimeknights.mantle.data.loadable.Loadable;
import slimeknights.mantle.data.loadable.field.ContextKey;
import slimeknights.mantle.util.typed.TypedMap;

/**
 * Loadable for ingredients.
 *
 * <p>1.21 removed {@code Ingredient.fromJson}/{@code toJson} and the byte-buf pair in favour
 * of codecs. Ingredients now hold a {@code HolderSet} of items, so both JSON and network
 * forms need registry access — JSON through {@link RegistryOps} and network through the
 * {@link RegistryFriendlyByteBuf} the framework was widened to carry.
 *
 * <p>Vanilla's {@code Ingredient.CODEC} rejects empty ingredients outright, so the
 * empty-allowing variant is handled here rather than by picking a different codec.
 */
public enum IngredientLoadable implements Loadable<Ingredient> {
  ALLOW_EMPTY,
  DISALLOW_EMPTY;

  @Override
  public Ingredient convert(JsonElement element, String key, TypedMap context) {
    if (element.isJsonArray() && element.getAsJsonArray().isEmpty()) {
      if (this == ALLOW_EMPTY) {
        return Ingredient.EMPTY;
      }
      throw ErrorFactory.JSON_SYNTAX_ERROR.create(key + " may not be an empty ingredient");
    }
    RegistryOps<JsonElement> ops = registryOps(context);
    return Ingredient.CODEC.parse(ops, element).getOrThrow(ErrorFactory.JSON_SYNTAX_ERROR::create);
  }

  @Override
  public JsonElement serialize(Ingredient object) {
    if (object.isEmpty()) {
      if (this == DISALLOW_EMPTY) {
        throw new IllegalArgumentException("Ingredient cannot be empty");
      }
      return new com.google.gson.JsonArray();
    }
    return Ingredient.CODEC.encodeStart(JsonOps.INSTANCE, object)
      .getOrThrow(ErrorFactory.RUNTIME::create);
  }

  @Override
  public Ingredient decode(RegistryFriendlyByteBuf buffer, TypedMap context) {
    return Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);
  }

  @Override
  public void encode(RegistryFriendlyByteBuf buffer, Ingredient object) {
    Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, object);
  }

  /**
   * Registry-aware ops for JSON parsing.
   *
   * <p>Datagen and recipe loading both put a lookup provider in the context; when absent —
   * as in tests that parse standalone JSON — plain ops still resolve item ids, which is all
   * a non-tag ingredient needs.
   */
  static RegistryOps<JsonElement> registryOps(TypedMap context) {
    HolderLookup.Provider provider = context.get(ContextKey.REGISTRY_ACCESS);
    return RegistryOps.create(JsonOps.INSTANCE,
      provider != null ? provider : RegistryAccess.EMPTY);
  }
}
