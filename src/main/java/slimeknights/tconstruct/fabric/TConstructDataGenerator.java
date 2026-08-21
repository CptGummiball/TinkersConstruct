package slimeknights.tconstruct.fabric;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import slimeknights.tconstruct.gadgets.data.GadgetRecipeProvider;
import slimeknights.tconstruct.shared.data.CommonRecipeProvider;
import slimeknights.tconstruct.smeltery.data.SmelteryRecipeProvider;
import slimeknights.tconstruct.tables.data.TableRecipeProvider;
import slimeknights.tconstruct.tools.data.ModifierRecipeProvider;
import slimeknights.tconstruct.tools.data.ToolsRecipeProvider;
import slimeknights.tconstruct.tools.data.material.MaterialRecipeProvider;
import slimeknights.tconstruct.world.data.WorldRecipeProvider;

/**
 * Datagen entry, replacing Forge's {@code GatherDataEvent} wiring in each module class.
 * Runs through {@code gradlew runDatagen}; mod init has completed by the time providers
 * run, so recipe serializers and conditions are all registered.
 *
 * <p>Phase 7 gates providers in slice by slice; this currently carries the recipe layer.
 * Tags, loot, advancements, worldgen and the client asset providers follow.
 */
public class TConstructDataGenerator implements DataGeneratorEntrypoint {

  @Override
  public void onInitializeDataGenerator(FabricDataGenerator generator) {
    FabricDataGenerator.Pack pack = generator.createPack();
    // recipe providers, one per module, matching the Forge event order
    pack.addProvider((output, registries) -> new CommonRecipeProvider(output, registries));
    pack.addProvider((output, registries) -> new SmelteryRecipeProvider(output, registries));
    pack.addProvider((output, registries) -> new TableRecipeProvider(output, registries));
    pack.addProvider((output, registries) -> new GadgetRecipeProvider(output, registries));
    pack.addProvider((output, registries) -> new WorldRecipeProvider(output, registries));
    pack.addProvider((output, registries) -> new ToolsRecipeProvider(output, registries));
    pack.addProvider((output, registries) -> new ModifierRecipeProvider(output, registries));
    pack.addProvider((output, registries) -> new MaterialRecipeProvider(output, registries));
  }
}
