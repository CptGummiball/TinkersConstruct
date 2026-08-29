package slimeknights.mantle.recipe.crafting;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.level.Level;
import slimeknights.mantle.recipe.MantleRecipes;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Shaped recipe that fails to match whenever one of its alternative recipes matches.
 *
 * <p>1.21 reshape: the vanilla shaped map codec is embedded for the base keys and the
 * alternatives ride along as an extra field; matching works on {@link CraftingInput}.
 */
@SuppressWarnings("WeakerAccess")
public class ShapedFallbackRecipe extends ShapedRecipe {

  /** Recipes to skip if they match */
  private final List<ResourceLocation> alternatives;
  private List<CraftingRecipe> alternativeCache;

  /**
   * Creates a recipe using a shaped recipe as a base
   * @param base          Shaped recipe to copy data from
   * @param alternatives  List of recipe names to fail this match if they match
   */
  public ShapedFallbackRecipe(ShapedRecipe base, List<ResourceLocation> alternatives) {
    super(base.getGroup(), base.category(), base.pattern, base.result, base.showNotification());
    this.alternatives = alternatives;
  }

  @Override
  public boolean matches(CraftingInput inv, Level world) {
    // if this recipe does not match, fail it
    if (!super.matches(inv, world)) {
      return false;
    }

    // fetch all alternatives, fail if any match
    // cache to save effort down the line
    if (alternativeCache == null) {
      RecipeManager manager = world.getRecipeManager();
      alternativeCache = alternatives.stream()
                                     .map(manager::byKey)
                                     .filter(Optional::isPresent)
                                     .map(holder -> holder.get().value())
                                     .filter(recipe -> {
                                       // only allow exact shaped or shapeless match, prevent infinite recursion due to complex recipes
                                       Class<?> clazz = recipe.getClass();
                                       return clazz == ShapedRecipe.class || clazz == ShapelessRecipe.class;
                                     })
                                     .map(recipe -> (CraftingRecipe) recipe).toList();
    }
    // fail if any alternative matches
    return this.alternativeCache.stream().noneMatch(recipe -> recipe.matches(inv, world));
  }

  @Override
  public RecipeSerializer<?> getSerializer() {
    return MantleRecipes.CRAFTING_SHAPED_FALLBACK.get();
  }

  public static class Serializer implements RecipeSerializer<ShapedFallbackRecipe> {
    private static final MapCodec<ShapedFallbackRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
      RecipeSerializer.SHAPED_RECIPE.codec().forGetter(recipe -> recipe),
      ResourceLocation.CODEC.listOf().fieldOf("alternatives").forGetter(recipe -> recipe.alternatives)
    ).apply(instance, ShapedFallbackRecipe::new));

    private static final StreamCodec<RegistryFriendlyByteBuf, ShapedFallbackRecipe> STREAM_CODEC = StreamCodec.of(Serializer::toNetwork, Serializer::fromNetwork);

    private static ShapedFallbackRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
      ShapedRecipe base = RecipeSerializer.SHAPED_RECIPE.streamCodec().decode(buffer);
      int size = buffer.readVarInt();
      List<ResourceLocation> alternatives = new ArrayList<>(size);
      for (int i = 0; i < size; i++) {
        alternatives.add(buffer.readResourceLocation());
      }
      return new ShapedFallbackRecipe(base, List.copyOf(alternatives));
    }

    private static void toNetwork(RegistryFriendlyByteBuf buffer, ShapedFallbackRecipe recipe) {
      RecipeSerializer.SHAPED_RECIPE.streamCodec().encode(buffer, recipe);
      buffer.writeVarInt(recipe.alternatives.size());
      for (ResourceLocation alternative : recipe.alternatives) {
        buffer.writeResourceLocation(alternative);
      }
    }

    @Override
    public MapCodec<ShapedFallbackRecipe> codec() {
      return CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, ShapedFallbackRecipe> streamCodec() {
      return STREAM_CODEC;
    }
  }
}
