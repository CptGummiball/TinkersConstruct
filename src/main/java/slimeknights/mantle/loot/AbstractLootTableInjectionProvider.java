package slimeknights.mantle.loot;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import slimeknights.mantle.data.GenericDataProvider;
import slimeknights.mantle.recipe.condition.ConditionHelper;
import slimeknights.mantle.recipe.condition.ICondition;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Datagen for {@code mantle/loot_injectors} files: entries merged into named pools of other
 * tables at load, see {@link LootTableInjector}. Registry ops because loot entries can carry
 * holders.
 */
public abstract class AbstractLootTableInjectionProvider extends GenericDataProvider {
  private final String modId;
  private final CompletableFuture<HolderLookup.Provider> registries;
  private final List<Entry> entries = new ArrayList<>();

  public AbstractLootTableInjectionProvider(PackOutput packOutput, String modId, CompletableFuture<HolderLookup.Provider> registries) {
    super(packOutput, PackOutput.Target.DATA_PACK, "mantle/loot_injectors");
    this.modId = modId;
    this.registries = registries;
  }

  /** Adds all injections via the inject methods */
  protected abstract void addTables();

  @Override
  public CompletableFuture<?> run(CachedOutput cache) {
    return registries.thenCompose(provider -> {
      RegistryOps<JsonElement> ops = provider.createSerializationContext(JsonOps.INSTANCE);
      this.entries.clear();
      addTables();
      return allOf(entries.stream().map(entry -> {
        JsonObject json = LootTableInjection.CODEC.encodeStart(ops, entry.builder.build()).getOrThrow().getAsJsonObject();
        if (entry.conditions.length > 0) {
          json.add("conditions", ConditionHelper.serialize(entry.conditions));
        }
        return saveJson(cache, ResourceLocation.fromNamespaceAndPath(modId, entry.fileName), json);
      }));
    });
  }

  /** Starts a new injection into the given table */
  protected LootTableInjection.Builder inject(String fileName, ResourceLocation table, ICondition... conditions) {
    LootTableInjection.Builder builder = new LootTableInjection.Builder(table);
    this.entries.add(new Entry(fileName, builder, conditions));
    return builder;
  }

  /** Starts a new injection into the given vanilla table */
  protected LootTableInjection.Builder inject(String fileName, String table, ICondition... conditions) {
    return inject(fileName, ResourceLocation.parse(table), conditions);
  }

  /** Starts a new injection into a vanilla chest table */
  protected LootTableInjection.Builder injectChest(String name, ICondition... conditions) {
    return inject(name, "chests/" + name, conditions);
  }

  /** Starts a new injection into a vanilla gameplay table */
  protected LootTableInjection.Builder injectGameplay(String name, ICondition... conditions) {
    return inject(name, "gameplay/" + name, conditions);
  }

  private record Entry(String fileName, LootTableInjection.Builder builder, ICondition[] conditions) {}
}
