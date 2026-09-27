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
import dev.emi.emi.api.stack.Comparison;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.recipe.modifiers.adding.IDisplayModifierRecipe;
import slimeknights.tconstruct.library.recipe.modifiers.severing.SeveringRecipe;
import slimeknights.tconstruct.library.recipe.partbuilder.IDisplayPartBuilderRecipe;
import slimeknights.tconstruct.library.recipe.tinkerstation.building.ToolBuildingRecipe;
import slimeknights.tconstruct.library.recipe.worktable.IModifierWorktableRecipe;
import slimeknights.tconstruct.library.tools.SlotType;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.part.IMaterialItem;
import slimeknights.tconstruct.tables.TinkerTables;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.TinkerTools;
import slimeknights.tconstruct.tools.item.CreativeSlotItem;
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
  public static final EmiRecipeCategory MODIFIERS = category("modifiers", CreativeSlotItem.withSlot(new ItemStack(TinkerModifiers.creativeSlotItem), SlotType.UPGRADE));
  public static final EmiRecipeCategory MODIFIER_WORKTABLE = category("modifier_worktable", TinkerTables.modifierWorktable);
  public static final EmiRecipeCategory TOOL_BUILDING = new EmiRecipeCategory(TConstruct.getResource("tool_building"), lazyTool(TinkerTools.pickaxe));
  public static final EmiRecipeCategory PART_BUILDER = category("part_builder", TinkerTables.partBuilder);
  public static final EmiRecipeCategory SEVERING = new EmiRecipeCategory(TConstruct.getResource("severing"), lazyTool(TinkerTools.cleaver));

  private static EmiRecipeCategory category(String name, ItemLike icon) {
    return new EmiRecipeCategory(TConstruct.getResource(name), EmiStack.of(icon));
  }

  private static EmiRecipeCategory category(String name, ItemStack icon) {
    return new EmiRecipeCategory(TConstruct.getResource(name), EmiStack.of(icon));
  }

  /**
   * Icon rendering a built display tool, resolved on first draw: the categories are constructed
   * when EMI scans entrypoints, long before tool definitions load, and {@code getRenderTool}
   * caches whatever it built first.
   */
  private static dev.emi.emi.api.render.EmiRenderable lazyTool(java.util.function.Supplier<? extends slimeknights.tconstruct.library.tools.item.IModifiableDisplay> tool) {
    EmiStack[] cache = new EmiStack[1];
    return (draw, x, y, delta) -> {
      if (cache[0] == null) {
        cache[0] = EmiStack.of(tool.get().getRenderTool());
      }
      cache[0].render(draw, x, y, delta);
    };
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
    registry.addCategory(MODIFIERS);
    registry.addCategory(MODIFIER_WORKTABLE);
    registry.addCategory(TOOL_BUILDING);
    registry.addCategory(PART_BUILDER);
    registry.addCategory(SEVERING);

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
    registry.addWorkstation(MODIFIERS, EmiStack.of(TinkerTables.tinkerStation));
    registry.addWorkstation(MODIFIERS, EmiStack.of(TinkerTables.tinkersAnvil));
    registry.addWorkstation(MODIFIERS, EmiStack.of(TinkerTables.scorchedAnvil));
    registry.addWorkstation(TOOL_BUILDING, EmiStack.of(TinkerTables.tinkerStation));
    registry.addWorkstation(TOOL_BUILDING, EmiStack.of(TinkerTables.tinkersAnvil));
    registry.addWorkstation(TOOL_BUILDING, EmiStack.of(TinkerTables.scorchedAnvil));
    registry.addWorkstation(MODIFIER_WORKTABLE, EmiStack.of(TinkerTables.modifierWorktable));
    registry.addWorkstation(PART_BUILDER, EmiStack.of(TinkerTables.partBuilder));
    // the severing and melting modifiers act as the workstation for their drops
    registry.addWorkstation(SEVERING, new ModifierEmiStack(new ModifierEntry(TinkerModifiers.severing, 1)));
    registry.addWorkstation(MELTING, new ModifierEmiStack(new ModifierEntry(TinkerModifiers.melting, 1)));
    registry.addWorkstation(ENTITY_MELTING, new ModifierEmiStack(new ModifierEntry(TinkerModifiers.melting, 1)));

    // tools and parts differ by their components, and every potion bucket shares one item
    Comparison components = Comparison.compareComponents();
    for (Item item : BuiltInRegistries.ITEM) {
      if (item instanceof IModifiable || item instanceof IMaterialItem) {
        registry.setDefaultComparison(item, components);
      }
    }
    registry.setDefaultComparison(slimeknights.tconstruct.fluids.TinkerFluids.potion.asItem(), components);

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

    // the tables family
    for (IDisplayModifierRecipe recipe : RecipeHelper.getJEIRecipes(access, manager, TinkerRecipeTypes.TINKER_STATION.get(), IDisplayModifierRecipe.class)) {
      registry.addRecipe(new ModifierEmiRecipe(MODIFIERS, recipe));
    }
    for (ToolBuildingRecipe recipe : RecipeHelper.getJEIRecipes(access, manager, TinkerRecipeTypes.TINKER_STATION.get(), ToolBuildingRecipe.class)) {
      registry.addRecipe(new ToolBuildingEmiRecipe(TOOL_BUILDING, recipe));
    }
    for (IModifierWorktableRecipe recipe : RecipeHelper.getJEIRecipes(access, manager, TinkerRecipeTypes.MODIFIER_WORKTABLE.get(), IModifierWorktableRecipe.class)) {
      registry.addRecipe(new ModifierWorktableEmiRecipe(MODIFIER_WORKTABLE, recipe));
    }
    for (IDisplayPartBuilderRecipe recipe : RecipeHelper.getJEIRecipes(access, manager, TinkerRecipeTypes.PART_BUILDER.get(), IDisplayPartBuilderRecipe.class)) {
      registry.addRecipe(new PartBuilderEmiRecipe(PART_BUILDER, recipe));
    }
    for (SeveringRecipe recipe : RecipeHelper.getJEIRecipes(access, manager, TinkerRecipeTypes.SEVERING.get(), SeveringRecipe.class)) {
      registry.addRecipe(new SeveringEmiRecipe(SEVERING, recipe));
    }

    // molding, both tables and basins in one category
    List<MoldingRecipe> molding = new ArrayList<>();
    molding.addAll(RecipeHelper.getJEIRecipes(access, manager, TinkerRecipeTypes.MOLDING_TABLE.get(), MoldingRecipe.class));
    molding.addAll(RecipeHelper.getJEIRecipes(access, manager, TinkerRecipeTypes.MOLDING_BASIN.get(), MoldingRecipe.class));
    for (MoldingRecipe recipe : molding) {
      registry.addRecipe(new MoldingEmiRecipe(MOLDING, recipe));
    }
  }
}
