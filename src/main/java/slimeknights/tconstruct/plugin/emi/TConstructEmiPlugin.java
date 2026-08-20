package slimeknights.tconstruct.plugin.emi;

import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.client.Minecraft;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.ItemLike;
import slimeknights.mantle.recipe.helper.RecipeHelper;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.recipe.alloying.AlloyRecipe;
import slimeknights.tconstruct.library.recipe.casting.IDisplayableCastingRecipe;
import slimeknights.tconstruct.library.recipe.entitymelting.EntityMeltingRecipe;
import slimeknights.tconstruct.library.recipe.fuel.MeltingFuel;
import slimeknights.tconstruct.library.recipe.melting.MeltingRecipe;
import slimeknights.tconstruct.library.recipe.molding.MoldingRecipe;
import slimeknights.tconstruct.library.recipe.TinkerRecipeTypes;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;

import java.util.ArrayList;
import java.util.List;

/**
 * EMI integration, replacing the Forge build's JEI plugin — EMI is the recipe viewer of the
 * GummiCraft pack. This wave carries the smeltery family: casting, melting, foundry, alloying,
 * entity melting and molding, with fuels and workstations. The tables family (modifiers, part
 * builder, tool building, severing) follows in the next wave.
 */
@EmiEntrypoint
public class TConstructEmiPlugin implements EmiPlugin {
  public static final EmiRecipeCategory CASTING_TABLE = category("casting_table", TinkerSmeltery.searedTable);
  public static final EmiRecipeCategory CASTING_BASIN = category("casting_basin", TinkerSmeltery.searedBasin);
  public static final EmiRecipeCategory MELTING = category("melting", TinkerSmeltery.searedMelter);
  public static final EmiRecipeCategory FOUNDRY = category("foundry", TinkerSmeltery.foundryController);
  public static final EmiRecipeCategory ALLOY = category("alloy", TinkerSmeltery.smelteryController);
  public static final EmiRecipeCategory ENTITY_MELTING = category("entity_melting", TinkerSmeltery.smelteryController);
  public static final EmiRecipeCategory MOLDING = category("molding", TinkerSmeltery.blankSandCast);

  private static EmiRecipeCategory category(String name, ItemLike icon) {
    return new EmiRecipeCategory(TConstruct.getResource(name), EmiStack.of(icon));
  }

  @Override
  public void register(EmiRegistry registry) {
    RecipeManager manager = registry.getRecipeManager();
    RegistryAccess access = Minecraft.getInstance().level.registryAccess();

    // categories
    registry.addCategory(CASTING_TABLE);
    registry.addCategory(CASTING_BASIN);
    registry.addCategory(MELTING);
    registry.addCategory(FOUNDRY);
    registry.addCategory(ALLOY);
    registry.addCategory(ENTITY_MELTING);
    registry.addCategory(MOLDING);

    // workstations
    registry.addWorkstation(CASTING_TABLE, EmiStack.of(TinkerSmeltery.searedTable));
    registry.addWorkstation(CASTING_TABLE, EmiStack.of(TinkerSmeltery.scorchedTable));
    registry.addWorkstation(CASTING_BASIN, EmiStack.of(TinkerSmeltery.searedBasin));
    registry.addWorkstation(CASTING_BASIN, EmiStack.of(TinkerSmeltery.scorchedBasin));
    registry.addWorkstation(MELTING, EmiStack.of(TinkerSmeltery.searedMelter));
    registry.addWorkstation(MELTING, EmiStack.of(TinkerSmeltery.smelteryController));
    registry.addWorkstation(FOUNDRY, EmiStack.of(TinkerSmeltery.foundryController));
    registry.addWorkstation(ALLOY, EmiStack.of(TinkerSmeltery.scorchedAlloyer));
    registry.addWorkstation(ALLOY, EmiStack.of(TinkerSmeltery.smelteryController));
    registry.addWorkstation(ENTITY_MELTING, EmiStack.of(TinkerSmeltery.smelteryController));
    registry.addWorkstation(MOLDING, EmiStack.of(TinkerSmeltery.searedTable));
    registry.addWorkstation(MOLDING, EmiStack.of(TinkerSmeltery.searedBasin));

    // fuels must load before the melting categories build their fuel displays
    MeltingFuelHandler.setMeltingFuels(RecipeHelper.getRecipes(manager, TinkerRecipeTypes.FUEL.get(), MeltingFuel.class));

    // casting
    for (IDisplayableCastingRecipe recipe : RecipeHelper.getJEIRecipes(access, manager, TinkerRecipeTypes.CASTING_TABLE.get(), IDisplayableCastingRecipe.class)) {
      registry.addRecipe(new CastingEmiRecipe(CASTING_TABLE, recipe));
    }
    for (IDisplayableCastingRecipe recipe : RecipeHelper.getJEIRecipes(access, manager, TinkerRecipeTypes.CASTING_BASIN.get(), IDisplayableCastingRecipe.class)) {
      registry.addRecipe(new CastingEmiRecipe(CASTING_BASIN, recipe));
    }

    // melting and the foundry view of the same recipes
    for (MeltingRecipe recipe : RecipeHelper.getJEIRecipes(access, manager, TinkerRecipeTypes.MELTING.get(), MeltingRecipe.class)) {
      registry.addRecipe(new MeltingEmiRecipe(MELTING, recipe, false));
      registry.addRecipe(new MeltingEmiRecipe(FOUNDRY, recipe, true));
    }

    // alloying
    for (AlloyRecipe recipe : RecipeHelper.getJEIRecipes(access, manager, TinkerRecipeTypes.ALLOYING.get(), AlloyRecipe.class)) {
      registry.addRecipe(new AlloyEmiRecipe(ALLOY, recipe));
    }

    // entity melting, plus the catch-all default
    List<EntityMeltingRecipe> entityMelting = RecipeHelper.getJEIRecipes(access, manager, TinkerRecipeTypes.ENTITY_MELTING.get(), EntityMeltingRecipe.class);
    for (EntityMeltingRecipe recipe : entityMelting) {
      registry.addRecipe(new EntityMeltingEmiRecipe(ENTITY_MELTING, recipe));
    }
    registry.addRecipe(new EntityMeltingEmiRecipe(ENTITY_MELTING, new DefaultEntityMeltingRecipe(entityMelting)));

    // molding, both tables and basins in one category
    List<MoldingRecipe> molding = new ArrayList<>();
    molding.addAll(RecipeHelper.getJEIRecipes(access, manager, TinkerRecipeTypes.MOLDING_TABLE.get(), MoldingRecipe.class));
    molding.addAll(RecipeHelper.getJEIRecipes(access, manager, TinkerRecipeTypes.MOLDING_BASIN.get(), MoldingRecipe.class));
    for (MoldingRecipe recipe : molding) {
      registry.addRecipe(new MoldingEmiRecipe(MOLDING, recipe));
    }
  }
}
