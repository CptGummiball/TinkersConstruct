package slimeknights.tconstruct.library.recipe.material;

import com.google.gson.JsonObject;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import slimeknights.mantle.recipe.data.ConsumerWrapperBuilder;
import slimeknights.mantle.recipe.data.IConditionalRecipeOutput;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.tables.TinkerTables;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/** Special variant of {@link ConsumerWrapperBuilder} for {@link ShapedMaterialsRecipe} and {@link ShapelessMaterialsRecipe} */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class MaterialsConsumerBuilder {
  private final String parts;
  private final int partCount;
  private final List<MaterialVariantId> materials = new ArrayList<>();

  /** Creates a new shaped recipe with the given ingredients as parts */
  public static MaterialsConsumerBuilder shaped(String parts) {
    if (parts.isEmpty()) {
      throw new IllegalArgumentException("Parts may not be empty");
    }
    return new MaterialsConsumerBuilder(parts, 0);
  }

  /** Creates a new shapeless recipe with the first ingredients as parts */
  public static MaterialsConsumerBuilder shapeless(int parts) {
    if (parts <= 0) {
      throw new IllegalArgumentException("Parts must be greater than 0");
    }
    return new MaterialsConsumerBuilder("", parts);
  }

  /** Adds a material to the builder */
  public MaterialsConsumerBuilder material(MaterialVariantId material) {
    materials.add(material);
    return this;
  }

  /** Builds the wrapped consumer */
  public RecipeOutput build(RecipeOutput consumer) {
    return new Wrapped(IConditionalRecipeOutput.of(consumer), materials, parts, partCount);
  }

  /**
   * Rewrites a vanilla shaped/shapeless recipe into the materials variant at the JSON level:
   * swaps the type, injects the parts selector and extra materials, and moves the result back
   * to the {@code {"item": ...}} shape the materials serializers kept from 1.20.
   */
  private record Wrapped(IConditionalRecipeOutput parent, List<MaterialVariantId> materials, String parts, int partCount) implements IConditionalRecipeOutput {
    @Override
    public JsonObject serializeRecipe(Recipe<?> recipe) {
      return parent.serializeRecipe(recipe);
    }

    @Override
    public void acceptJson(ResourceLocation id, JsonObject json, @Nullable AdvancementHolder advancement) {
      json.addProperty("type", BuiltInRegistries.RECIPE_SERIALIZER.getKey(
        partCount > 0 ? TinkerTables.shapelessMaterialsRecipeSerializer.get() : TinkerTables.shapedMaterialsRecipeSerializer.get()).toString());
      if (!materials.isEmpty()) {
        json.add("extra_materials", ShapedMaterialsRecipe.Serializer.EXTRA_MATERIALS.serialize(materials));
      }
      if (!parts.isEmpty()) {
        json.addProperty("parts", parts);
      } else {
        json.addProperty("parts", partCount);
      }
      // the materials serializers read the result under "item" (1.20 shape), vanilla wrote "id"
      if (json.get("result") instanceof JsonObject result && result.has("id")) {
        result.add("item", result.remove("id"));
      }
      parent.acceptJson(id, json, advancement);
    }

    @Override
    public Advancement.Builder advancement() {
      return parent.advancement();
    }
  }
}
