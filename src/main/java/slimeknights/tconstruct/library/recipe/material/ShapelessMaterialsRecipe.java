package slimeknights.tconstruct.library.recipe.material;

import com.google.gson.JsonSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.Getter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import slimeknights.mantle.data.loadable.Loadable;
import slimeknights.mantle.recipe.helper.LoggingRecipeSerializer;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.tables.TinkerTables;

import java.util.List;

/**
 * Shapeless recipe with a number of {@link slimeknights.tconstruct.library.recipe.ingredient.MaterialIngredient} and
 * {@link slimeknights.tconstruct.library.recipe.ingredient.MaterialValueIngredient} to set the materials of the result.
 */
public class ShapelessMaterialsRecipe extends ShapelessRecipe implements MaterialsCraftingTableRecipe {
  /** Number of parts to match */
  @Getter
  private final int partCount;
  /** List of additional materials to add beyond the parts */
  @Getter
  private final List<MaterialVariantId> extraMaterials;

  public ShapelessMaterialsRecipe(String group, CraftingBookCategory category, ItemStack result, NonNullList<Ingredient> ingredients, int partCount, List<MaterialVariantId> extraMaterials) {
    super(group, category, result, ingredients);
    this.partCount = partCount;
    this.extraMaterials = extraMaterials;
  }

  public ShapelessMaterialsRecipe(ShapelessRecipe recipe, int partCount, List<MaterialVariantId> extraMaterials) {
    this(recipe.getGroup(), recipe.category(), recipe.result, recipe.getIngredients(), partCount, extraMaterials);
  }

  @Override
  public List<Ingredient> getParts() {
    return getIngredients();
  }

  /** Sets the material for the given stack */
  @Override
  public void setMaterial(ItemStack stack, MaterialVariantId material) {
    ShapedMaterialsRecipe.setMaterial(stack, material, extraMaterials);
  }

  @Override
  public ItemStack assemble(CraftingInput inventory, HolderLookup.Provider registries) {
    return ShapedMaterialsRecipe.assemble(super.assemble(inventory, registries), inventory, getIngredients(), partCount, false, extraMaterials);
  }

  @Override
  public RecipeSerializer<?> getSerializer() {
    return TinkerTables.shapelessMaterialsRecipeSerializer.get();
  }

  public static class Serializer implements LoggingRecipeSerializer<ShapelessMaterialsRecipe> {
    static final Loadable<List<MaterialVariantId>> EXTRA_MATERIALS = ShapedMaterialsRecipe.Serializer.EXTRA_MATERIALS;

    private static final MapCodec<ShapelessMaterialsRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
      Codec.STRING.optionalFieldOf("group", "").forGetter(recipe -> recipe.getGroup()),
      CraftingBookCategory.CODEC.fieldOf("category").orElse(CraftingBookCategory.MISC).forGetter(recipe -> recipe.category()),
      ShapedMaterialsRecipe.Serializer.RESULT_CODEC.fieldOf("result").forGetter(recipe -> recipe.result),
      Ingredient.CODEC_NONEMPTY.listOf().fieldOf("ingredients").flatXmap(Serializer::readIngredients, DataResult::success).forGetter(recipe -> recipe.getIngredients()),
      Codec.INT.fieldOf("parts").forGetter(recipe -> recipe.partCount),
      ShapedMaterialsRecipe.Serializer.EXTRA_MATERIALS_CODEC.optionalFieldOf("extra_materials", List.of()).forGetter(recipe -> recipe.extraMaterials)
    ).apply(instance, Serializer::fromJson));

    /** Bounds the ingredient list the same way the vanilla shapeless serializer does */
    private static DataResult<NonNullList<Ingredient>> readIngredients(List<Ingredient> ingredients) {
      Ingredient[] nonEmpty = ingredients.stream().filter(ingredient -> !ingredient.isEmpty()).toArray(Ingredient[]::new);
      if (nonEmpty.length == 0) {
        return DataResult.error(() -> "No ingredients for shapeless recipe");
      }
      if (nonEmpty.length > 9) {
        return DataResult.error(() -> "Too many ingredients for shapeless recipe. The maximum is: 9");
      }
      return DataResult.success(NonNullList.of(Ingredient.EMPTY, nonEmpty));
    }

    private static ShapelessMaterialsRecipe fromJson(String group, CraftingBookCategory category, ItemStack result, NonNullList<Ingredient> ingredients, int parts, List<MaterialVariantId> extraMaterials) {
      if (parts < 1 || parts > ingredients.size()) {
        throw new JsonSyntaxException("Parts must be between 1 and the number of ingredients " + ingredients.size());
      }
      return new ShapelessMaterialsRecipe(group, category, result, ingredients, parts, extraMaterials);
    }

    @Override
    public MapCodec<ShapelessMaterialsRecipe> codec() {
      return CODEC;
    }

    @Override
    public ShapelessMaterialsRecipe fromNetworkSafe(RegistryFriendlyByteBuf buffer) {
      ShapelessRecipe recipe = ShapelessRecipe.Serializer.STREAM_CODEC.decode(buffer);
      return new ShapelessMaterialsRecipe(recipe, buffer.readByte(), EXTRA_MATERIALS.decode(buffer));
    }

    @Override
    public void toNetworkSafe(RegistryFriendlyByteBuf buffer, ShapelessMaterialsRecipe recipe) {
      ShapelessRecipe.Serializer.STREAM_CODEC.encode(buffer, recipe);
      buffer.writeByte(recipe.partCount);
      EXTRA_MATERIALS.encode(buffer, recipe.extraMaterials);
    }
  }
}
