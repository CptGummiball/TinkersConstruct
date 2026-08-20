package slimeknights.tconstruct.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredientSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import slimeknights.tconstruct.TConstruct;
import slimeknights.mantle.fluid.transfer.EmptyFluidContainerTransfer;
import slimeknights.mantle.fluid.transfer.FillFluidContainerTransfer;
import slimeknights.mantle.fluid.transfer.FluidContainerTransferManager;
import slimeknights.mantle.network.MantleNetwork;
import slimeknights.mantle.recipe.condition.TagCombinationCondition;
import slimeknights.mantle.recipe.condition.TagEmptyCondition;
import slimeknights.mantle.recipe.condition.TagFilledCondition;
import slimeknights.mantle.recipe.ingredient.FluidContainerIngredient;
import slimeknights.mantle.recipe.ingredient.PotionDisplayIngredient;
import slimeknights.mantle.recipe.ingredient.PotionIngredient;
import slimeknights.mantle.transfer.fluid.TransferComponents;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.library.json.condition.TagDifferencePresentCondition;
import slimeknights.tconstruct.library.json.condition.TagIntersectionPresentCondition;
import slimeknights.tconstruct.fluids.TinkerFluids;
import slimeknights.tconstruct.shared.TinkerAttributes;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.shared.TinkerEffects;
import slimeknights.tconstruct.shared.TinkerMaterials;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.tables.TinkerTables;
import slimeknights.tconstruct.world.TinkerWorld;

/**
 * Fabric {@code main} entrypoint.
 *
 * <p>Forge drove initialisation through the mod event bus, with each {@code TinkerModule}
 * subscribing to registry events and Forge deciding the order. Fabric has no such bus, so
 * ordering becomes explicit here: registration runs eagerly during construction, in the same
 * order the Forge build registered its modules, because several modules read registry objects
 * created by earlier ones.
 */
public class TConstructBootstrap implements ModInitializer {

  public static final String MOD_ID = "tconstruct";
  public static final Logger LOG = LoggerFactory.getLogger("Tinkers' Construct");

  @Override
  public void onInitialize() {
    LOG.info("Tinkers' Construct (Fabric {}) starting", MOD_ID);

    // Config first: content modules and recipe conditions read config values, and Forge
    // Config API Port loads the file once the spec is registered.
    Config.init();

    // Must run before any FluidStack is built: fluid stacks carry their legacy NBT tag
    // through this component type.
    TransferComponents.register();

    // Mantle infrastructure: packet channel plus the fluid container transfer loader.
    MantleNetwork.registerPackets();
    // Tinkers' own channel; every packet send goes through this instance, so it must exist
    // before any content registers (modifier sync, tool container updates, projectile sync).
    slimeknights.tconstruct.common.network.TinkerNetwork.setup();
    slimeknights.mantle.recipe.MantleRecipes.init();
    FluidContainerTransferManager.INSTANCE.init();
    // transfer types referenced by the shipped data files; Forge Mantle registered these in
    // its mod constructor
    FluidContainerTransferManager.TRANSFER_LOADERS.registerDeserializer(FillFluidContainerTransfer.ID, FillFluidContainerTransfer.DESERIALIZER);
    FluidContainerTransferManager.TRANSFER_LOADERS.registerDeserializer(EmptyFluidContainerTransfer.ID, EmptyFluidContainerTransfer.DESERIALIZER);

    // Mantle's custom ingredients; Forge registered these through CraftingHelper.
    CustomIngredientSerializer.register(FluidContainerIngredient.SERIALIZER);
    CustomIngredientSerializer.register(PotionIngredient.SERIALIZER);
    CustomIngredientSerializer.register(PotionDisplayIngredient.SERIALIZER);

    // Mantle's named predicate vocabulary (mantle:can_protect and friends); the dynamic
    // modifier JSONs parse against these on datapack load. Upstream Mantle's mod class
    // registered them during the recipe-serializer register event.
    slimeknights.mantle.data.predicate.MantlePredicates.init();

    // Global loot modifiers: Forge's mechanism reimplemented, loaded from the same index.
    slimeknights.mantle.loot.modifier.GlobalLootManager.init();
    slimeknights.mantle.loot.modifier.IGlobalLootModifier.register(
      slimeknights.tconstruct.tools.modifiers.ModifierLootModifier.ID,
      slimeknights.tconstruct.tools.modifiers.ModifierLootModifier.CODEC);

    // Tinkers' own recipe conditions; data files reference them, so they must parse
    // before the first datapack load. Forge registered these through CraftingHelper.
    TagDifferencePresentCondition.register();
    TagIntersectionPresentCondition.register();
    // Mantle's tag conditions, same deal (mantle:tag_filled guards most generated recipes).
    TagFilledCondition.SERIALIZER.register();
    TagEmptyCondition.SERIALIZER.register();
    TagCombinationCondition.SERIALIZER.register();
    // tconstruct:tag_not_empty is the same condition under an older name; five book sections
    // guard themselves with it, and an unknown condition type is a hard parse failure.
    new TagFilledCondition.Serializer<>(TConstruct.getResource("tag_not_empty"), TagFilledCondition::new).register();

    // Sound events; Forge registered these through its deferred register. Data files
    // reference them by id (fluid effects above all), so they must exist before datapack load.
    slimeknights.tconstruct.common.Sounds.registerSounds();

    // Content modules, in the Forge build's construction order. Touching each class runs its
    // registrations (eager registers); init() replaces that module's event handlers.
    TinkerAttributes.init();
    TinkerEffects.init();
    TinkerCommons.init();
    TinkerMaterials.init();
    TinkerFluids.init();
    // world: structures/features registered by class-init inside TinkerWorld's registrations
    TinkerWorld.init();
    TinkerTables.init();
    TinkerSmeltery.init();
    slimeknights.tconstruct.tools.TinkerModifiers.init();
    slimeknights.tconstruct.tools.TinkerToolParts.init();
    slimeknights.tconstruct.tools.TinkerTools.init();
    slimeknights.tconstruct.gadgets.TinkerGadgets.init();

    // Datapack loaders and the material registry. Forge registered these from the mod
    // constructor and through AddReloadListenerEvent; without them the material registry stays
    // null, tool definitions and station layouts never load, and nothing that reads a material
    // works — which is invisible until a tool is actually used or a client joins.
    slimeknights.mantle.datagen.MantleTags.init();
    slimeknights.tconstruct.common.TinkerTags.init();
    slimeknights.tconstruct.library.recipe.TinkerRecipeTypes.init();
    slimeknights.tconstruct.library.materials.MaterialRegistry.init();
    slimeknights.tconstruct.library.tools.definition.ToolDefinitionLoader.init();
    slimeknights.tconstruct.library.tools.layout.StationSlotLayoutLoader.init();
    slimeknights.tconstruct.library.utils.DomainDisplayName.init();

    // Event layer: the Fabric-callback side of the bridge, then every gameplay handler's
    // listener registration on the shim bus (their events post from the bridge mixins).
    slimeknights.tconstruct.fabric.events.TinkerEventBridge.init();
    slimeknights.tconstruct.tools.logic.ToolEvents.init();
    slimeknights.tconstruct.tools.logic.ModifierEvents.init();
    slimeknights.tconstruct.tools.logic.EquipmentChangeWatcher.init();
    slimeknights.tconstruct.tools.logic.DoubleJumpHandler.init();
    slimeknights.tconstruct.tools.logic.InteractionHandler.init();
    slimeknights.tconstruct.shared.CommonsEvents.init();
    slimeknights.tconstruct.shared.AchievementEvents.init();
    slimeknights.tconstruct.world.WorldEvents.init();
    slimeknights.tconstruct.library.utils.SlimeBounceHandler.init();

    // Further modules are wired in as each one finishes porting; see PORTING.md.
  }
}
