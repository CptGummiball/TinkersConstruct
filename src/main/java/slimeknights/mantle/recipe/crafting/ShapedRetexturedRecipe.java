package slimeknights.mantle.recipe.crafting;

import com.google.gson.JsonSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.Getter;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import slimeknights.mantle.recipe.MantleRecipes;
import slimeknights.mantle.util.RetexturedHelper;

import java.util.Map;

/**
 * Recipe which sets the texture for a {@link slimeknights.mantle.block.RetexturedBlock} based on an ingredient input.
 *
 * <p>1.21 reshape: built on {@link ShapedRecipePattern} and codecs. The {@code "texture"}
 * JSON value is a single-character key symbol resolved against the pattern's retained key
 * map (the deprecated inline-ingredient form was unused by every shipped recipe and is gone).
 */
@SuppressWarnings("WeakerAccess")
public class ShapedRetexturedRecipe extends ShapedRecipe {
  /** Ingredient used to determine the texture on the output */
  @Getter
  private final Ingredient texture;
  private final boolean matchAll;

  protected ShapedRetexturedRecipe(String group, CraftingBookCategory category, ShapedRecipePattern pattern, ItemStack result, boolean showNotification, Ingredient texture, boolean matchAll) {
    super(group, category, pattern, result, showNotification);
    this.texture = texture;
    this.matchAll = matchAll;
  }

  /**
   * Gets the output using the given texture
   * @param texture  Texture to use
   * @return  Output with texture. Will be blank if the input is not a block
   */
  public ItemStack getResultItem(Item texture, HolderLookup.Provider access) {
    return RetexturedHelper.setTexture(getResultItem(access).copy(), Block.byItem(texture));
  }

  @Override
  public ItemStack assemble(CraftingInput craftMatrix, HolderLookup.Provider access) {
    ItemStack result = super.assemble(craftMatrix, access);
    Block currentTexture = null;
    for (int i = 0; i < craftMatrix.size(); i++) {
      ItemStack stack = craftMatrix.getItem(i);
      if (!stack.isEmpty() && texture.test(stack)) {
        // fetch texture from the block if it has one
        Block block = RetexturedHelper.getTexture(stack);
        // assuming it does not, use the block itself as the texture (provided it is not the result that is)
        if (block == Blocks.AIR && stack.getItem() != result.getItem()) {
          block = Block.byItem(stack.getItem());
        }
        // if no texture, skip
        if (block == Blocks.AIR) {
          continue;
        }

        // if we have not found a texture yet, store the found block
        if (currentTexture == null) {
          currentTexture = block;
          // match all means we must check the rest. If not match all, we can be done
          if (!matchAll) {
            break;
          }

          // if we found a texture before, must match or we do no texture
        } else if (currentTexture != block) {
          currentTexture = null;
          break;
        }
      }
    }

    // set the texture if found. No texture will use the fallback
    if (currentTexture != null) {
      return RetexturedHelper.setTexture(result, currentTexture);
    }
    return result;
  }

  @Override
  public RecipeSerializer<?> getSerializer() {
    return MantleRecipes.CRAFTING_SHAPED_RETEXTURED.get();
  }

  public static class Serializer implements RecipeSerializer<ShapedRetexturedRecipe> {
    private static final MapCodec<ShapedRetexturedRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
      Codec.STRING.optionalFieldOf("group", "").forGetter(ShapedRecipe::getGroup),
      CraftingBookCategory.CODEC.optionalFieldOf("category", CraftingBookCategory.MISC).forGetter(ShapedRecipe::category),
      ShapedRecipePattern.MAP_CODEC.forGetter(recipe -> recipe.pattern),
      ItemStack.STRICT_CODEC.fieldOf("result").forGetter(recipe -> recipe.result),
      Codec.BOOL.optionalFieldOf("show_notification", true).forGetter(ShapedRecipe::showNotification),
      Codec.STRING.fieldOf("texture").forGetter(Serializer::encodeTextureKey),
      Codec.BOOL.optionalFieldOf("match_all", false).forGetter(recipe -> recipe.matchAll)
    ).apply(instance, Serializer::create));

    private static final StreamCodec<RegistryFriendlyByteBuf, ShapedRetexturedRecipe> STREAM_CODEC = StreamCodec.of(Serializer::toNetwork, Serializer::fromNetwork);

    /** Resolves the texture symbol against the pattern's key map */
    private static ShapedRetexturedRecipe create(String group, CraftingBookCategory category, ShapedRecipePattern pattern, ItemStack result, boolean showNotification, String textureKey, boolean matchAll) {
      if (textureKey.length() != 1) {
        throw new JsonSyntaxException("Invalid texture key: '" + textureKey + "' is an invalid symbol (must be 1 character only).");
      }
      Ingredient texture = pattern.data.map(data -> data.key().get(textureKey.charAt(0))).orElse(null);
      if (texture == null || texture == Ingredient.EMPTY) {
        throw new JsonSyntaxException("Texture ingredient references symbol '" + textureKey + "' but it's not defined in the key");
      }
      return new ShapedRetexturedRecipe(group, category, pattern, result, showNotification, texture, matchAll);
    }

    /** Finds the key symbol for the texture ingredient, for serialization */
    private static String encodeTextureKey(ShapedRetexturedRecipe recipe) {
      return recipe.pattern.data
        .flatMap(data -> data.key().entrySet().stream().filter(entry -> entry.getValue() == recipe.texture).findFirst().map(Map.Entry::getKey))
        .map(String::valueOf)
        .orElseThrow(() -> new IllegalStateException("Texture ingredient of a retextured recipe is not part of its key"));
    }

    private static ShapedRetexturedRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
      ShapedRecipe base = RecipeSerializer.SHAPED_RECIPE.streamCodec().decode(buffer);
      Ingredient texture = Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);
      boolean matchAll = buffer.readBoolean();
      return new ShapedRetexturedRecipe(base.getGroup(), base.category(), base.pattern, base.result, base.showNotification(), texture, matchAll);
    }

    private static void toNetwork(RegistryFriendlyByteBuf buffer, ShapedRetexturedRecipe recipe) {
      RecipeSerializer.SHAPED_RECIPE.streamCodec().encode(buffer, recipe);
      Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.texture);
      buffer.writeBoolean(recipe.matchAll);
    }

    @Override
    public MapCodec<ShapedRetexturedRecipe> codec() {
      return CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, ShapedRetexturedRecipe> streamCodec() {
      return STREAM_CODEC;
    }
  }
}
