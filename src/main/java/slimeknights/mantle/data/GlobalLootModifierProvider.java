package slimeknights.mantle.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import slimeknights.mantle.loot.modifier.IGlobalLootModifier;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Shim for Forge's {@code GlobalLootModifierProvider}. Writes each modifier through the
 * runtime's {@link IGlobalLootModifier#CODEC} — the same codec the manager parses with — to
 * {@code data/<modid>/loot_modifiers/<name>.json}, plus the {@code forge:} index file the
 * manager reads. Registry ops because loot conditions can carry holders (item predicates,
 * enchantments).
 */
public abstract class GlobalLootModifierProvider implements DataProvider {
  private final PackOutput.PathProvider pathProvider;
  private final PackOutput output;
  private final String modId;
  private final CompletableFuture<HolderLookup.Provider> registriesFuture;
  /** Registry access, available while {@link #start()} runs */
  protected HolderLookup.Provider registries;
  private final Map<String, IGlobalLootModifier> toSerialize = new LinkedHashMap<>();

  public GlobalLootModifierProvider(PackOutput output, String modId, CompletableFuture<HolderLookup.Provider> registries) {
    this.output = output;
    this.modId = modId;
    this.registriesFuture = registries;
    this.pathProvider = output.createPathProvider(PackOutput.Target.DATA_PACK, "loot_modifiers");
  }

  /** Adds all modifiers via {@link #add} */
  protected abstract void start();

  /** Adds a modifier to be saved */
  protected <T extends IGlobalLootModifier> T add(String modifier, T instance) {
    this.toSerialize.put(modifier, instance);
    return instance;
  }

  @Override
  public CompletableFuture<?> run(CachedOutput cache) {
    return registriesFuture.thenCompose(provider -> {
      this.registries = provider;
      RegistryOps<JsonElement> ops = provider.createSerializationContext(JsonOps.INSTANCE);
      this.toSerialize.clear();
      this.start();

      List<CompletableFuture<?>> futures = new ArrayList<>();
      JsonArray entries = new JsonArray();
      this.toSerialize.forEach((name, modifier) -> {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(modId, name);
        entries.add(id.toString());
        JsonElement json = IGlobalLootModifier.CODEC.encodeStart(ops, modifier).getOrThrow();
        futures.add(DataProvider.saveStable(cache, json, this.pathProvider.json(id)));
      });

      // the forge-namespaced index listing every modifier to load
      JsonObject index = new JsonObject();
      index.add("entries", entries);
      index.addProperty("replace", false);
      futures.add(DataProvider.saveStable(cache, index,
        this.output.getOutputFolder(PackOutput.Target.DATA_PACK).resolve("forge/loot_modifiers/global_loot_modifiers.json")));
      return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
    });
  }

  @Override
  public String getName() {
    return "Global Loot Modifiers : " + modId;
  }
}
