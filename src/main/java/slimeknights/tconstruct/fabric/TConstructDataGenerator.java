package slimeknights.tconstruct.fabric;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import slimeknights.mantle.data.ExistingFileHelper;
import slimeknights.tconstruct.common.data.AdvancementsProvider;
import slimeknights.tconstruct.common.data.ConfigurationDataProvider;
import slimeknights.tconstruct.common.data.DamageTypeProvider;
import slimeknights.tconstruct.common.data.loot.GlobalLootModifiersProvider;
import slimeknights.tconstruct.common.data.loot.LootTableInjectionProvider;
import slimeknights.tconstruct.common.data.loot.TConstructLootTableProvider;
import slimeknights.tconstruct.common.data.tags.BiomeTagProvider;
import slimeknights.tconstruct.common.data.tags.BlockEntityTypeTagProvider;
import slimeknights.tconstruct.common.data.tags.BlockTagProvider;
import slimeknights.tconstruct.common.data.tags.DamageTypeTagProvider;
import slimeknights.tconstruct.common.data.tags.EnchantmentTagProvider;
import slimeknights.tconstruct.common.data.tags.EntityTypeTagProvider;
import slimeknights.tconstruct.common.data.tags.FluidTagProvider;
import slimeknights.tconstruct.common.data.tags.ItemTagProvider;
import slimeknights.tconstruct.common.data.tags.MaterialTagProvider;
import slimeknights.tconstruct.common.data.tags.MenuTypeTagProvider;
import slimeknights.tconstruct.common.data.tags.ModifierTagProvider;
import slimeknights.tconstruct.common.data.tags.PotionTagProvider;
import slimeknights.tconstruct.fluids.data.FluidTooltipProvider;
import slimeknights.tconstruct.gadgets.data.GadgetRecipeProvider;
import slimeknights.tconstruct.shared.data.CommonRecipeProvider;
import slimeknights.tconstruct.smeltery.data.FluidContainerTransferProvider;
import slimeknights.tconstruct.smeltery.data.SmelteryRecipeProvider;
import slimeknights.tconstruct.tables.data.TableRecipeProvider;
import slimeknights.tconstruct.tools.data.EnchantmentToModifierProvider;
import slimeknights.tconstruct.tools.data.FluidEffectProvider;
import slimeknights.tconstruct.tools.data.ModifierProvider;
import slimeknights.tconstruct.tools.data.ModifierRecipeProvider;
import slimeknights.tconstruct.tools.data.StationSlotLayoutProvider;
import slimeknights.tconstruct.tools.data.ToolDefinitionDataProvider;
import slimeknights.tconstruct.tools.data.ToolsRecipeProvider;
import slimeknights.tconstruct.tools.data.material.MaterialDataProvider;
import slimeknights.tconstruct.tools.data.material.MaterialRecipeProvider;
import slimeknights.tconstruct.tools.data.material.MaterialStatsDataProvider;
import slimeknights.tconstruct.tools.data.material.MaterialTraitsDataProvider;
import slimeknights.tconstruct.world.data.MobEquipmentProvider;
import slimeknights.tconstruct.world.data.WorldRecipeProvider;
import slimeknights.tconstruct.world.data.WorldgenProvider;

/**
 * Datagen entry, replacing Forge's {@code GatherDataEvent} wiring in each module class.
 * Runs through {@code gradlew runDatagen}; mod init has completed by the time providers
 * run, so recipe serializers, conditions and loot types are all registered.
 *
 * <p>Phase 7 gates providers in slice by slice; this carries the server-data layer.
 * The client asset providers (models, blockstates, sprites) follow. Fabric constructs
 * each provider immediately inside {@code addProvider}, which is what lets the block tag
 * and material providers hand their lookups to the providers registered after them.
 */
public class TConstructDataGenerator implements DataGeneratorEntrypoint {

  @Override
  public void onInitializeDataGenerator(FabricDataGenerator generator) {
    FabricDataGenerator.Pack pack = generator.createPack();
    // no resource lookup in fabric datagen; validation runs against the registries instead
    ExistingFileHelper existingFileHelper = new ExistingFileHelper(null, null);

    // recipe providers, one per module, matching the Forge event order
    pack.addProvider((output, registries) -> new CommonRecipeProvider(output, registries));
    pack.addProvider((output, registries) -> new SmelteryRecipeProvider(output, registries));
    pack.addProvider((output, registries) -> new TableRecipeProvider(output, registries));
    pack.addProvider((output, registries) -> new GadgetRecipeProvider(output, registries));
    pack.addProvider((output, registries) -> new WorldRecipeProvider(output, registries));
    pack.addProvider((output, registries) -> new ToolsRecipeProvider(output, registries));
    pack.addProvider((output, registries) -> new ModifierRecipeProvider(output, registries));
    pack.addProvider((output, registries) -> new MaterialRecipeProvider(output, registries));

    // dynamic registries: worldgen and damage types, built by buildRegistry below
    pack.addProvider((output, registries) -> new FabricDynamicRegistryProvider(output, registries) {
      @Override
      protected void configure(HolderLookup.Provider registries, Entries entries) {
        entries.addAll(registries.lookupOrThrow(Registries.CONFIGURED_FEATURE));
        entries.addAll(registries.lookupOrThrow(Registries.PLACED_FEATURE));
        entries.addAll(registries.lookupOrThrow(Registries.STRUCTURE));
        entries.addAll(registries.lookupOrThrow(Registries.STRUCTURE_SET));
        entries.addAll(registries.lookupOrThrow(Registries.DAMAGE_TYPE));
      }

      @Override
      public String getName() {
        return "Tinkers' Construct Dynamic Registries";
      }
    });

    // tags; the item provider copies from block tags
    BlockTagProvider[] blockTags = new BlockTagProvider[1];
    pack.addProvider((output, registries) -> blockTags[0] = new BlockTagProvider(output, registries, existingFileHelper));
    pack.addProvider((output, registries) -> new ItemTagProvider(output, registries, blockTags[0].contentsGetter(), existingFileHelper));
    pack.addProvider((output, registries) -> new FluidTagProvider(output, registries, existingFileHelper));
    pack.addProvider((output, registries) -> new EntityTypeTagProvider(output, registries, existingFileHelper));
    pack.addProvider((output, registries) -> new BlockEntityTypeTagProvider(output, registries, existingFileHelper));
    pack.addProvider((output, registries) -> new BiomeTagProvider(output, registries, existingFileHelper));
    pack.addProvider((output, registries) -> new EnchantmentTagProvider(output, registries, existingFileHelper));
    pack.addProvider((output, registries) -> new MenuTypeTagProvider(output, registries, existingFileHelper));
    pack.addProvider((output, registries) -> new PotionTagProvider(output, registries, existingFileHelper));
    pack.addProvider((output, registries) -> new DamageTypeTagProvider(output, registries, existingFileHelper));
    pack.addProvider((output, registries) -> new ModifierTagProvider(output, existingFileHelper));
    pack.addProvider((output, registries) -> new MaterialTagProvider(output, existingFileHelper));

    // loot and advancements
    pack.addProvider((output, registries) -> new TConstructLootTableProvider(output, registries));
    pack.addProvider((output, registries) -> new AdvancementsProvider(output, registries));
    pack.addProvider((output, registries) -> new GlobalLootModifiersProvider(output, registries));
    pack.addProvider((output, registries) -> new LootTableInjectionProvider(output, registries));
    pack.addProvider((output, registries) -> new ConfigurationDataProvider(output));

    // tinkering data
    pack.addProvider((output, registries) -> new ModifierProvider(output, registries));
    pack.addProvider((output, registries) -> new FluidEffectProvider(output, registries));
    pack.addProvider((output, registries) -> new EnchantmentToModifierProvider(output));
    pack.addProvider((output, registries) -> new ToolDefinitionDataProvider(output));
    pack.addProvider((output, registries) -> new StationSlotLayoutProvider(output));
    MaterialDataProvider[] materials = new MaterialDataProvider[1];
    pack.addProvider((output, registries) -> materials[0] = new MaterialDataProvider(output));
    pack.addProvider((output, registries) -> new MaterialStatsDataProvider(output, materials[0]));
    pack.addProvider((output, registries) -> new MaterialTraitsDataProvider(output, materials[0]));
    pack.addProvider((output, registries) -> new MobEquipmentProvider(output));
    pack.addProvider((output, registries) -> new FluidContainerTransferProvider(output));
    pack.addProvider((output, registries) -> new FluidTooltipProvider(output));
  }

  @Override
  public void buildRegistry(RegistrySetBuilder registryBuilder) {
    WorldgenProvider.register(registryBuilder);
    DamageTypeProvider.register(registryBuilder);
  }
}
