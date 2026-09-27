package slimeknights.mantle.recipe.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * Base recipe provider for mantle datagen. Extends the vanilla provider for its protected
 * static criterion helpers ({@code has}, {@code inventoryTrigger}, ...), but replaces the
 * writing half of {@link RecipeProvider#run(CachedOutput, HolderLookup.Provider)} entirely:
 * the vanilla implementation serializes recipes itself with no seam for load conditions or
 * type overrides, so this one hands subclasses an {@link IConditionalRecipeOutput} instead.
 *
 * <p>Serialization goes through {@link Recipe#CODEC} with registry-aware ops — the exact
 * codec path the game parses these files with at load, so datagen and runtime cannot drift
 * apart. Advancements are written through {@link Advancement#CODEC} the same way.
 */
public abstract class MantleRecipeProvider extends RecipeProvider {

  private final PackOutput.PathProvider recipePaths;
  private final PackOutput.PathProvider advancementPaths;

  public MantleRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
    super(output, registries);
    // the vanilla path providers are package-private; building our own is cheaper than an AW
    this.recipePaths = output.createPathProvider(PackOutput.Target.DATA_PACK, "recipe");
    this.advancementPaths = output.createPathProvider(PackOutput.Target.DATA_PACK, "advancement");
  }

  @Override
  public CompletableFuture<?> run(CachedOutput cache, HolderLookup.Provider registries) {
    RegistryOps<JsonElement> ops = registries.createSerializationContext(JsonOps.INSTANCE);
    Set<ResourceLocation> seen = new HashSet<>();
    List<CompletableFuture<?>> futures = new ArrayList<>();
    this.buildRecipes(new IConditionalRecipeOutput() {
      @Override
      public JsonObject serializeRecipe(Recipe<?> recipe) {
        return Recipe.CODEC.encodeStart(ops, recipe).getOrThrow().getAsJsonObject();
      }

      @Override
      public void acceptJson(ResourceLocation id, JsonObject recipe, @Nullable AdvancementHolder advancement) {
        if (!seen.add(id)) {
          throw new IllegalStateException("Duplicate recipe " + id);
        }
        futures.add(DataProvider.saveStable(cache, recipe, recipePaths.json(id)));
        if (advancement != null) {
          futures.add(DataProvider.saveStable(cache, Advancement.CODEC.encodeStart(ops, advancement.value()).getOrThrow(), advancementPaths.json(advancement.id())));
        }
      }

      @Override
      public Advancement.Builder advancement() {
        return Advancement.Builder.recipeAdvancement().parent(RecipeBuilder.ROOT_RECIPE_ADVANCEMENT);
      }
    });
    return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
  }
}
