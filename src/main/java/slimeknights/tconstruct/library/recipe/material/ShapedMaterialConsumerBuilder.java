package slimeknights.tconstruct.library.recipe.material;

import com.google.gson.JsonObject;
import lombok.NoArgsConstructor;
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

/** Special variant of {@link ConsumerWrapperBuilder} for {@link ShapedMaterialRecipe} */
@Deprecated
@NoArgsConstructor(staticName = "wrap")
public class ShapedMaterialConsumerBuilder {
  private final List<MaterialVariantId> materials = new ArrayList<>();

  /** Adds a material to the builder */
  public ShapedMaterialConsumerBuilder material(MaterialVariantId material) {
    materials.add(material);
    return this;
  }

  /** Builds the wrapped consumer */
  public RecipeOutput build(RecipeOutput consumer) {
    return new Wrapped(IConditionalRecipeOutput.of(consumer), materials);
  }

  /** JSON-level rewrite onto the shaped material type; see {@code MaterialsConsumerBuilder.Wrapped} */
  private record Wrapped(IConditionalRecipeOutput parent, List<MaterialVariantId> materials) implements IConditionalRecipeOutput {
    @Override
    public JsonObject serializeRecipe(Recipe<?> recipe) {
      return parent.serializeRecipe(recipe);
    }

    @Override
    public void acceptJson(ResourceLocation id, JsonObject json, @Nullable AdvancementHolder advancement) {
      json.addProperty("type", BuiltInRegistries.RECIPE_SERIALIZER.getKey(TinkerTables.shapedMaterialRecipeSerializer.get()).toString());
      if (!materials.isEmpty()) {
        json.add("extra_materials", ShapedMaterialsRecipe.Serializer.EXTRA_MATERIALS.serialize(materials));
      }
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
