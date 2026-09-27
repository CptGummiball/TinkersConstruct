package slimeknights.tconstruct.library.recipe.ingredient;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.item.crafting.Ingredient;
import slimeknights.mantle.data.loadable.common.IngredientLoadable;
import slimeknights.mantle.data.loadable.field.RecordField;
import slimeknights.mantle.util.typed.TypedMap;

import java.util.Map;
import java.util.function.Function;

/**
 * Field for the nested vanilla ingredient of a {@link NestedIngredient}, keeping the 1.20 wire
 * format: a vanilla object-form ingredient sits flat on the custom ingredient's own object
 * (as the shipped data files expect), while array forms and custom ingredients nest under
 * {@code match}.
 */
public record NestedIngredientField<P extends NestedIngredient>(Function<P,Ingredient> getter) implements RecordField<Ingredient,P> {

  @Override
  public Ingredient get(JsonObject json, TypedMap context) {
    if (json.has("match")) {
      return IngredientLoadable.DISALLOW_EMPTY.getIfPresent(json, "match");
    }
    // flat form: the vanilla keys (item/tag) sit next to the type key. Strip the type keys
    // before re-parsing, otherwise Fabric's ingredient codec would dispatch back to us.
    JsonObject copy = new JsonObject();
    for (Map.Entry<String,JsonElement> entry : json.entrySet()) {
      String key = entry.getKey();
      if (!"type".equals(key) && !"fabric:type".equals(key)) {
        copy.add(key, entry.getValue());
      }
    }
    return IngredientLoadable.DISALLOW_EMPTY.convert(copy, "ingredient");
  }

  @Override
  public void serialize(P parent, JsonObject json) {
    Ingredient nested = getter.apply(parent);
    JsonElement element = IngredientLoadable.DISALLOW_EMPTY.serialize(nested);
    // custom ingredients carry their own type key, so they must stay nested to avoid clashes
    if (element.isJsonObject() && nested.getCustomIngredient() == null) {
      for (Map.Entry<String,JsonElement> entry : element.getAsJsonObject().entrySet()) {
        json.add(entry.getKey(), entry.getValue());
      }
    } else {
      json.add("match", element);
    }
  }

  @Override
  public Ingredient decode(RegistryFriendlyByteBuf buffer, TypedMap context) {
    return IngredientLoadable.DISALLOW_EMPTY.decode(buffer);
  }

  @Override
  public void encode(RegistryFriendlyByteBuf buffer, P parent) {
    IngredientLoadable.DISALLOW_EMPTY.encode(buffer, getter.apply(parent));
  }
}
