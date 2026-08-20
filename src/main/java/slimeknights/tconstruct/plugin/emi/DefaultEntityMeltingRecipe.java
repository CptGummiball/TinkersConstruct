package slimeknights.tconstruct.plugin.emi;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import slimeknights.mantle.recipe.helper.FluidOutput;
import slimeknights.mantle.recipe.ingredient.EntityIngredient;
import slimeknights.mantle.util.Lazy;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.smeltery.block.entity.module.EntityMeltingModule;
import slimeknights.tconstruct.library.recipe.entitymelting.EntityMeltingRecipe;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Synthetic recipe for the catch-all: every entity without its own melting recipe melts into the
 * default fluid. Ported from the JEI plugin; the registry walk moved from Forge's wrapper to the
 * vanilla registry.
 */
public class DefaultEntityMeltingRecipe extends EntityMeltingRecipe {
  /** Gets a list of entity types without a recipe */
  private static EntityIngredient getEntityList(List<EntityMeltingRecipe> recipes) {
    Set<EntityType<?>> unusedTypes = new LinkedHashSet<>();
    typeLoop:
    for (EntityType<?> type : BuiltInRegistries.ENTITY_TYPE) {
      // use tag overrides for default recipe
      if (type.is(TinkerTags.EntityTypes.MELTING_HIDE)) continue;
      if (type.getCategory() == MobCategory.MISC && !type.is(TinkerTags.EntityTypes.MELTING_SHOW)) continue;
      for (EntityMeltingRecipe recipe : recipes) {
        if (recipe.matches(type)) {
          continue typeLoop;
        }
      }
      unusedTypes.add(type);
    }
    return EntityIngredient.of(unusedTypes);
  }

  private final Lazy<EntityIngredient> entities;

  public DefaultEntityMeltingRecipe(List<EntityMeltingRecipe> recipes) {
    super(TConstruct.getResource("__default"), EntityIngredient.EMPTY, FluidOutput.fromStack(EntityMeltingModule.getDefaultFluid()), 2);
    entities = Lazy.of(() -> getEntityList(recipes));
  }

  @Override
  public EntityIngredient getIngredient() {
    return entities.get();
  }
}
