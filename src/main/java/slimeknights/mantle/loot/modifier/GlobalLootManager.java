package slimeknights.mantle.loot.modifier;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import slimeknights.mantle.Mantle;
import slimeknights.mantle.data.DatapackRegistries;

import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Loads and applies the global loot modifiers.
 *
 * <p>Forge read an index file naming the active modifiers and applied them to every loot
 * roll. Both halves are reimplemented here: the index and entries are read as plain data
 * (they are not a datapack registry, so no registry plumbing is involved beyond the ops
 * needed to parse ingredients and predicates), and a mixin on {@code LootTable} calls
 * {@link #apply} on the result of every roll.
 */
public class GlobalLootManager {
  public static final GlobalLootManager INSTANCE = new GlobalLootManager();
  /** Forge's index file, kept at its original path so existing packs keep working */
  private static final ResourceLocation INDEX_PATH = ResourceLocation.fromNamespaceAndPath("forge", "loot_modifiers/global_loot_modifiers.json");

  /** Modifiers in the order the index listed them */
  private List<IGlobalLootModifier> modifiers = List.of();
  /** Table currently rolling, for the loot_table_id condition */
  private static final ThreadLocal<ResourceLocation> CURRENT_TABLE = new ThreadLocal<>();

  private GlobalLootManager() {}

  /** Registers the loader; call once from the bootstrap */
  public static void init() {
    IGlobalLootModifier.register(AddEntryLootModifier.ID, AddEntryLootModifier.CODEC);
    IGlobalLootModifier.register(ReplaceItemLootModifier.ID, ReplaceItemLootModifier.CODEC);
    ILootModifierCondition.init();
    // Loading runs off the server lifecycle rather than the resource-reload phase: the
    // entries embed entity and block predicates whose tags are only bound once the reload
    // finishes, so parsing any earlier fails on "Missing tag".
    net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents.SERVER_STARTED.register(
      server -> INSTANCE.load(server.getResourceManager()));
    net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents.END_DATA_PACK_RELOAD.register(
      (server, resources, success) -> {
        if (success) {
          INSTANCE.load(server.getResourceManager());
        }
      });
  }

  public void load(ResourceManager manager) {
    List<IGlobalLootModifier> loaded = new ArrayList<>();
    long time = System.nanoTime();

    // the ops matter: entries embed ingredients and entity predicates, which need registry access
    RegistryOps<JsonElement> ops = registryOps();
    // every pack's copy of the index is read rather than just the winner: the list is
    // additive unless a pack asks to replace it, exactly as Forge treated it
    for (Resource resource : manager.getResourceStack(INDEX_PATH)) {
      try (BufferedReader reader = resource.openAsReader()) {
        JsonObject json = GsonHelper.parse(reader);
        if (GsonHelper.getAsBoolean(json, "replace", false)) {
          loaded.clear();
        }
        for (JsonElement element : GsonHelper.getAsJsonArray(json, "entries")) {
          IGlobalLootModifier modifier = loadModifier(manager, ResourceLocation.parse(element.getAsString()), ops);
          if (modifier != null) {
            loaded.add(modifier);
          }
        }
      } catch (Exception e) {
        Mantle.logger.error("Failed to read global loot modifier index from {}", resource.sourcePackId(), e);
      }
    }

    modifiers = List.copyOf(loaded);
    Mantle.logger.info("Loaded {} global loot modifiers in {} ms", modifiers.size(), (System.nanoTime() - time) / 1000000f);
  }

  /** Reads a single modifier file */
  @Nullable
  private IGlobalLootModifier loadModifier(ResourceManager manager, ResourceLocation id, RegistryOps<JsonElement> ops) {
    ResourceLocation path = ResourceLocation.fromNamespaceAndPath(id.getNamespace(), "loot_modifiers/" + id.getPath() + ".json");
    Optional<Resource> resource = manager.getResource(path);
    if (resource.isEmpty()) {
      Mantle.logger.error("Missing global loot modifier {}", id);
      return null;
    }
    try (BufferedReader reader = resource.get().openAsReader()) {
      JsonElement json = GsonHelper.parse(reader);
      return IGlobalLootModifier.CODEC.parse(ops, json).getOrThrow();
    } catch (Exception e) {
      Mantle.logger.error("Failed to load global loot modifier {}", id, e);
      return null;
    }
  }

  /** Registry-aware ops, falling back to plain JSON before a datapack load has published registries */
  private static RegistryOps<JsonElement> registryOps() {
    net.minecraft.core.HolderLookup.Provider registries = DatapackRegistries.current();
    return registries != null
           ? registries.createSerializationContext(JsonOps.INSTANCE)
           : RegistryOps.create(JsonOps.INSTANCE, net.minecraft.core.RegistryAccess.EMPTY);
  }

  /**
   * Applies every loaded modifier to a table's output.
   * @param tableId  Table being rolled, for the {@code forge:loot_table_id} condition
   */
  public static ObjectArrayList<ItemStack> apply(@Nullable ResourceLocation tableId, ObjectArrayList<ItemStack> loot, LootContext context) {
    List<IGlobalLootModifier> modifiers = INSTANCE.modifiers;
    if (modifiers.isEmpty()) {
      return loot;
    }
    CURRENT_TABLE.set(tableId);
    try {
      for (IGlobalLootModifier modifier : modifiers) {
        loot = modifier.apply(loot, context);
      }
    } finally {
      CURRENT_TABLE.remove();
    }
    return loot;
  }

  /** Id of the table currently rolling, null outside a roll */
  @Nullable
  public static ResourceLocation currentTable() {
    return CURRENT_TABLE.get();
  }
}
