package slimeknights.tconstruct.library.recipe.ingredient;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredient;
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredientSerializer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import slimeknights.mantle.data.loadable.field.RecordField;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.predicate.IJsonPredicate;
import slimeknights.mantle.recipe.helper.LoadableIngredientSerializer;
import slimeknights.mantle.util.typed.TypedMap;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.json.predicate.material.MaterialPredicate;
import slimeknights.tconstruct.library.json.predicate.material.MaterialPredicateField;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.recipe.material.MaterialRecipe;
import slimeknights.tconstruct.library.recipe.material.MaterialRecipeCache;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

/**
 * Ingredient matching material items with the given value. Typically, matches ingots or blocks
 */
@Getter
@RequiredArgsConstructor
public class MaterialValueIngredient implements CustomIngredient {
  /** Serializer instance, keeping the 1.20 {@code value} format (number, or object with min/max) */
  public static final LoadableIngredientSerializer<MaterialValueIngredient> SERIALIZER = new LoadableIngredientSerializer<>(
    TConstruct.getResource("material_value"),
    RecordLoadable.create(
      new MaterialPredicateField<>("material", i -> i.material),
      ValueField.INSTANCE,
      (material, range) -> new MaterialValueIngredient(material, range.min, range.max)));

  private final IJsonPredicate<MaterialVariantId> material;
  private final float minValue;
  private final float maxValue;
  @Getter(lombok.AccessLevel.NONE)
  @Nullable
  private List<ItemStack> items;

  /** Creates an ingredient matching a range of values */
  public static MaterialValueIngredient of(IJsonPredicate<MaterialVariantId> materials, float minValue, float maxValue) {
    return new MaterialValueIngredient(materials, minValue, maxValue);
  }

  /** Creates an ingredient matching an exact value */
  public static MaterialValueIngredient of(IJsonPredicate<MaterialVariantId> materials, float value) {
    return of(materials, value, value);
  }

  /** Checks the given material recipe against our filters */
  public boolean test(MaterialRecipe material) {
    float value = material.getValue() / (float) material.getNeeded();
    return minValue <= value && value <= maxValue && this.material.matches(material.getMaterial().getVariant());
  }

  @Override
  public boolean test(@Nullable ItemStack stack) {
    if (stack == null) {
      return false;
    }
    MaterialRecipe recipe = MaterialRecipeCache.findRecipe(stack);
    return recipe != MaterialRecipe.EMPTY && test(recipe);
  }

  @Override
  public List<ItemStack> getMatchingStacks() {
    if (items == null) {
      items = MaterialRecipeCache.getAllRecipes().stream()
        .filter(this::test)
        .flatMap(material -> Arrays.stream(material.getIngredient().getItems()))
        .toList();
    }
    return items;
  }

  @Override
  public boolean requiresTesting() {
    return true;
  }


  /* Helpers for ShapedMaterialRecipe */

  /** Checks if this ingredient fully contains the range of the other */
  private boolean contains(MaterialValueIngredient other) {
    return this.minValue <= other.minValue && other.maxValue <= this.maxValue;
  }

  /** Creates an ingredient that matches anything either of the two ingredients matches */
  public MaterialValueIngredient merge(MaterialValueIngredient other) {
    if (this == other) return this;

    // if we have the same predicate, we can possibly skip creating a new instance
    IJsonPredicate<MaterialVariantId> predicate = this.material;
    if (this.material.equals(other.material)) {
      if (this.contains(other)) {
        return this;
      }
      if (other.contains(this)) {
        return other;
      }
    } else {
      predicate = MaterialPredicate.or(this.material, other.material);
    }
    return new MaterialValueIngredient(predicate, Math.min(this.minValue, other.minValue), Math.max(this.maxValue, other.maxValue));
  }

  /** Gets the material matching this recipe */
  @Nullable
  public MaterialVariantId getMaterial(ItemStack stack) {
    MaterialRecipe recipe = MaterialRecipeCache.findRecipe(stack);
    return recipe != MaterialRecipe.EMPTY && test(recipe) ? recipe.getMaterial().getVariant() : null;
  }


  /* JSON */

  @Override
  public CustomIngredientSerializer<?> getSerializer() {
    return SERIALIZER;
  }

  /** Serializes to JSON for datagen */
  public JsonElement toJson() {
    return SERIALIZER.serialize(this);
  }

  /** Value range for the loadable */
  private record ValueRange(float min, float max) {}

  /** Field keeping the 1.20 format: {@code value} is a number for exact, or an object with optional min/max */
  private enum ValueField implements RecordField<ValueRange,MaterialValueIngredient> {
    INSTANCE;

    @Override
    public ValueRange get(JsonObject json, TypedMap context) {
      JsonElement value = json.get("value");
      if (value == null) {
        throw new com.google.gson.JsonSyntaxException("Missing value on material value ingredient");
      }
      if (value.isJsonPrimitive()) {
        float exact = value.getAsJsonPrimitive().getAsFloat();
        return new ValueRange(exact, exact);
      }
      JsonObject object = GsonHelper.convertToJsonObject(value, "value");
      return new ValueRange(GsonHelper.getAsFloat(object, "min", 0), GsonHelper.getAsFloat(object, "max", Float.POSITIVE_INFINITY));
    }

    @Override
    public void serialize(MaterialValueIngredient parent, JsonObject json) {
      if (parent.minValue == parent.maxValue) {
        json.addProperty("value", parent.minValue);
      } else {
        JsonObject value = new JsonObject();
        if (parent.minValue > 0) {
          value.addProperty("min", parent.minValue);
        }
        if (Float.isFinite(parent.maxValue)) {
          value.addProperty("max", parent.maxValue);
        }
        json.add("value", value);
      }
    }

    @Override
    public ValueRange decode(RegistryFriendlyByteBuf buffer, TypedMap context) {
      return new ValueRange(buffer.readFloat(), buffer.readFloat());
    }

    @Override
    public void encode(RegistryFriendlyByteBuf buffer, MaterialValueIngredient parent) {
      buffer.writeFloat(parent.minValue);
      buffer.writeFloat(parent.maxValue);
    }
  }
}
