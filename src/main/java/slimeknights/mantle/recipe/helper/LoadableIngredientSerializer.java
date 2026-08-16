package slimeknights.mantle.recipe.helper;

import com.google.gson.JsonObject;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.item.crafting.Ingredient;
import slimeknights.mantle.recipe.condition.ConditionHelper;
import net.minecraftforge.common.crafting.IIngredientSerializer;
import slimeknights.mantle.data.loadable.record.RecordLoadable;

import java.util.Objects;

/** Ingredient serializer made using loadables */
public record LoadableIngredientSerializer<T extends Ingredient>(RecordLoadable<T> loadable) implements IIngredientSerializer<T> {
  @Override
  public T parse(RegistryFriendlyByteBuf buffer) {
    return loadable.decode(buffer);
  }

  @Override
  public T parse(JsonObject json) {
    return loadable.deserialize(json);
  }

  @Override
  public void write(RegistryFriendlyByteBuf buffer, T ingredient) {
    loadable.encode(buffer, ingredient);
  }

  /** Serializes the ingredient to JSON */
  public JsonObject serialize(T ingredient) {
    JsonObject json = new JsonObject();
    json.addProperty("type", Objects.requireNonNull(ConditionHelper.getID(this)).toString());
    loadable.serialize(ingredient, json);
    return json;
  }
}
