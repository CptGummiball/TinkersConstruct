package slimeknights.tconstruct.library.recipe.material;

import com.google.gson.JsonSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import net.minecraft.world.level.Level;
import slimeknights.mantle.data.loadable.Loadable;
import slimeknights.mantle.recipe.helper.LoggingRecipeSerializer;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.recipe.ingredient.MaterialValueIngredient;
import slimeknights.tconstruct.tables.TinkerTables;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Shaped recipe with a number of {@link slimeknights.tconstruct.library.recipe.ingredient.MaterialValueIngredient} to set the material of the result.
 * @deprecated use {@link ShapedMaterialsRecipe}, which requires specifying the ingredients for each part.
 */
@Deprecated
public class ShapedMaterialRecipe extends ShapedRecipe {
  private MaterialValueIngredient material;
  private final List<MaterialVariantId> extraMaterials;
  public ShapedMaterialRecipe(String group, CraftingBookCategory category, ShapedRecipePattern pattern, ItemStack result, boolean showNotification, List<MaterialVariantId> extraMaterials) {
    super(group, category, pattern, result, showNotification);
    this.extraMaterials = extraMaterials;
  }

  public ShapedMaterialRecipe(ShapedRecipe recipe, List<MaterialVariantId> extraMaterials) {
    this(recipe.getGroup(), recipe.category(), recipe.pattern, recipe.result, recipe.showNotification(), extraMaterials);
  }

  /** Gets the material to match */
  @Nullable
  public MaterialValueIngredient getMaterial() {
    if (material == null) {
      // assume all material ingredients match the same stat type
      for (Ingredient ingredient : getIngredients()) {
        // collect all ingredients that match; custom ingredients sit behind the Fabric wrapper
        if (ingredient.getCustomIngredient() instanceof MaterialValueIngredient materialValue) {
          if (material == null) {
            material = materialValue;
          } else {
            // ensure the stat type matches, and expand the range
            material = material.merge(materialValue);
          }
        }
      }
      // if we found no materials, that is also an issue
      if (material == null) {
        TConstruct.LOG.error("No material ingredient found for material shaped recipe producing {}, this indicates a broken recipe", result);
      }
    }
    return material;
  }

  @Nullable
  private MaterialVariantId findMaterial(CraftingInput inventory) {
    MaterialValueIngredient material = getMaterial();
    if (material == null) {
      return null;
    }
    // ensure same material in all slots
    MaterialVariantId firstMaterial = null;
    for (int i = 0; i < inventory.size(); i++) {
      ItemStack stack = inventory.getItem(i);
      if (!stack.isEmpty()) {
        // ignore anything that does not meet our requirements
        MaterialVariantId matchedMaterial = material.getMaterial(stack);
        if (matchedMaterial != null) {
          // first match is set
          if (firstMaterial == null) {
            firstMaterial = matchedMaterial;
          } else if (!firstMaterial.matchesVariant(matchedMaterial)) {
            // if same material but different variants, just discard the variant
            if (firstMaterial.getId().equals(matchedMaterial.getId())) {
              firstMaterial = firstMaterial.getId();
            } else {
              // if different materials, no match
              return null;
            }
          }
        }
      }
    }
    return firstMaterial;
  }

  @Override
  public boolean matches(CraftingInput inventory, Level level) {
    if (!super.matches(inventory, level)) {
      return false;
    }

    // must have a material to match, no mixing
    return findMaterial(inventory) != null;
  }

  /** Sets the material for the given stack */
  public void setMaterial(ItemStack stack, MaterialVariantId material) {
    ShapedMaterialsRecipe.setMaterial(stack, material, extraMaterials);
  }

  @Override
  public ItemStack assemble(CraftingInput inventory, HolderLookup.Provider registries) {
    ItemStack stack = super.assemble(inventory, registries);
    MaterialVariantId material = findMaterial(inventory);
    if (material != null) {
      setMaterial(stack, material);
    }
    return stack;
  }

  @Override
  public RecipeSerializer<?> getSerializer() {
    return TinkerTables.shapedMaterialRecipeSerializer.get();
  }

  public static class Serializer implements LoggingRecipeSerializer<ShapedMaterialRecipe> {
    static final Loadable<List<MaterialVariantId>> EXTRA_MATERIALS = ShapedMaterialsRecipe.Serializer.EXTRA_MATERIALS;

    private static final MapCodec<ShapedMaterialRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
      Codec.STRING.optionalFieldOf("group", "").forGetter(recipe -> recipe.getGroup()),
      CraftingBookCategory.CODEC.fieldOf("category").orElse(CraftingBookCategory.MISC).forGetter(recipe -> recipe.category()),
      ShapedRecipePattern.MAP_CODEC.forGetter(recipe -> recipe.pattern),
      ShapedMaterialsRecipe.Serializer.RESULT_CODEC.fieldOf("result").forGetter(recipe -> recipe.result),
      Codec.BOOL.optionalFieldOf("show_notification", true).forGetter(recipe -> recipe.showNotification()),
      ShapedMaterialsRecipe.Serializer.EXTRA_MATERIALS_CODEC.optionalFieldOf("extra_materials", List.of()).forGetter(recipe -> recipe.extraMaterials)
    ).apply(instance, Serializer::fromJson));

    private static ShapedMaterialRecipe fromJson(String group, CraftingBookCategory category, ShapedRecipePattern pattern, ItemStack result, boolean showNotification, List<MaterialVariantId> extraMaterials) {
      ShapedMaterialRecipe recipe = new ShapedMaterialRecipe(group, category, pattern, result, showNotification, extraMaterials);
      // ensure the material is valid, since we have all the needed information to check
      // better now than at runtime
      if (recipe.getMaterial() == null) {
        throw new JsonSyntaxException("Invalid material ingredients for shaped material recipe producing " + result.getItem());
      }
      return recipe;
    }

    @Override
    public MapCodec<ShapedMaterialRecipe> codec() {
      return CODEC;
    }

    @Override
    public ShapedMaterialRecipe fromNetworkSafe(RegistryFriendlyByteBuf buffer) {
      ShapedRecipe recipe = ShapedRecipe.Serializer.STREAM_CODEC.decode(buffer);
      List<MaterialVariantId> extraMaterials = EXTRA_MATERIALS.decode(buffer);
      return new ShapedMaterialRecipe(recipe, extraMaterials);
    }

    @Override
    public void toNetworkSafe(RegistryFriendlyByteBuf buffer, ShapedMaterialRecipe recipe) {
      ShapedRecipe.Serializer.STREAM_CODEC.encode(buffer, recipe);
      EXTRA_MATERIALS.encode(buffer, recipe.extraMaterials);
    }
  }
}
