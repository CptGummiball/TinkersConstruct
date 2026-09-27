package slimeknights.tconstruct.common.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import slimeknights.mantle.recipe.data.IConditionBuilder;
import slimeknights.mantle.recipe.data.IRecipeHelper;
import slimeknights.mantle.recipe.data.MantleRecipeProvider;
import slimeknights.tconstruct.TConstruct;

import java.util.concurrent.CompletableFuture;

/**
 * Shared logic for each module's recipe provider
 */
public abstract class BaseRecipeProvider extends MantleRecipeProvider implements IConditionBuilder, IRecipeHelper {
  public BaseRecipeProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries) {
    super(packOutput, registries);
    TConstruct.sealTinkersClass(this, "BaseRecipeProvider", "BaseRecipeProvider is trivial to recreate and directly extending can lead to addon recipes polluting our namespace.");
  }

  @Override
  public abstract void buildRecipes(RecipeOutput consumer);

  @Override
  public abstract String getName();

  @Override
  public String getModId() {
    return TConstruct.MOD_ID;
  }
}
