package slimeknights.mantle.loot;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import slimeknights.mantle.Mantle;
import slimeknights.mantle.recipe.condition.ConditionHelper;
import slimeknights.mantle.util.DataLoadedConditionContext;

import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Applies {@code mantle/loot_injectors} data files: extra entries merged into named pools of
 * other tables. On Forge this rode {@code LootTableLoadEvent}; loot tables are a datapack
 * registry in 1.21, so a mixin on {@code ReloadableServerRegistries.reload} calls this once
 * the registry is baked and the parsed tables are patched in place through the widened
 * pool internals.
 */
public class LootTableInjector {

  private LootTableInjector() {}

  private static final String FOLDER = "mantle/loot_injectors";

  /** Applies every injector the resource manager can see to the freshly loaded tables */
  public static void apply(HolderLookup.Provider registries, ResourceManager resourceManager) {
    HolderLookup.RegistryLookup<LootTable> tables;
    try {
      tables = registries.lookupOrThrow(Registries.LOOT_TABLE);
    } catch (IllegalStateException e) {
      Mantle.logger.error("Loot table registry missing, skipping loot injections", e);
      return;
    }
    RegistryOps<JsonElement> ops = registries.createSerializationContext(JsonOps.INSTANCE);
    FileToIdConverter converter = FileToIdConverter.json(FOLDER);
    int applied = 0;
    for (Map.Entry<ResourceLocation, Resource> entry : converter.listMatchingResources(resourceManager).entrySet()) {
      ResourceLocation file = converter.fileToId(entry.getKey());
      try (Reader reader = entry.getValue().openAsReader()) {
        JsonObject json = GsonHelper.convertToJsonObject(JsonParser.parseReader(reader), "loot injector");
        // same condition dialect as recipes; unknown conditions fail loudly there too
        if (!ConditionHelper.processConditions(json, "conditions", DataLoadedConditionContext.INSTANCE)) {
          continue;
        }
        json.remove("conditions");
        LootTableInjection injection = LootTableInjection.CODEC.parse(ops, json).getOrThrow();
        Optional<net.minecraft.core.Holder.Reference<LootTable>> holder = tables.get(ResourceKey.create(Registries.LOOT_TABLE, injection.name()));
        if (holder.isEmpty()) {
          Mantle.logger.warn("Loot injector {} targets missing table {}", file, injection.name());
          continue;
        }
        inject(holder.get().value(), injection, file);
        applied++;
      } catch (Exception e) {
        Mantle.logger.error("Failed to apply loot injector {}", file, e);
      }
    }
    if (applied > 0) {
      Mantle.logger.info("Applied {} loot table injections", applied);
    }
  }

  /** Merges one injection into its parsed table */
  private static void inject(LootTable table, LootTableInjection injection, ResourceLocation file) {
    List<LootPool> pools = new ArrayList<>(table.pools);
    boolean changed = false;
    for (LootTableInjection.InjectedPool injectedPool : injection.pools()) {
      int index = LootTableInjection.poolIndex(injectedPool.name());
      if (index < 0 || index >= pools.size()) {
        Mantle.logger.warn("Loot injector {} references missing pool '{}' in {}", file, injectedPool.name(), injection.name());
        continue;
      }
      LootPool pool = pools.get(index);
      List<LootPoolEntryContainer> entries = new ArrayList<>(pool.entries);
      entries.addAll(injectedPool.entries());
      pools.set(index, new LootPool(List.copyOf(entries), pool.conditions, pool.functions, pool.rolls, pool.bonusRolls));
      changed = true;
    }
    if (changed) {
      table.pools = List.copyOf(pools);
    }
  }
}
