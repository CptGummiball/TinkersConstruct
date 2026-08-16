package slimeknights.mantle.recipe.condition;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;

/**
 * Shim for Forge's {@code IConditionSerializer}: a named JSON read/write pair for recipe
 * load conditions. Registration goes through {@link ConditionHelper}, which replaces
 * Forge's {@code CraftingHelper} condition registry on Fabric.
 */
public interface IConditionSerializer<C extends ICondition> {
  /** Condition type ID, as written to the {@code condition} key in data files */
  ResourceLocation getID();

  /** Writes the condition's fields to JSON (datagen) */
  void write(JsonObject json, C value);

  /** Reads the condition from JSON */
  C read(JsonObject json);

  /** Registers this serializer with the condition deserializer */
  default void register() {
    ConditionHelper.register(getID(), this::read);
  }
}
