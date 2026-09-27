package slimeknights.mantle.loot;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntries;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Entries to add into named pools of another loot table, mantle's replacement for Forge's
 * {@code LootTableLoadEvent} wiring. Pool names follow the Forge convention the shipped data
 * uses: the first pool of a vanilla table is {@code main}, later ones {@code pool1},
 * {@code pool2}, ... by index.
 *
 * @param name  ID of the loot table to inject into
 * @param pools The pools to add entries to
 */
public record LootTableInjection(ResourceLocation name, List<InjectedPool> pools) {

  public static final Codec<LootTableInjection> CODEC = RecordCodecBuilder.create(instance -> instance.group(
    ResourceLocation.CODEC.fieldOf("name").forGetter(LootTableInjection::name),
    InjectedPool.CODEC.listOf().fieldOf("pools").forGetter(LootTableInjection::pools)
  ).apply(instance, LootTableInjection::new));

  /** Entries for one named pool of the target table */
  public record InjectedPool(String name, List<LootPoolEntryContainer> entries) {
    public static final Codec<InjectedPool> CODEC = RecordCodecBuilder.create(instance -> instance.group(
      Codec.STRING.fieldOf("name").forGetter(InjectedPool::name),
      LootPoolEntries.CODEC.listOf().fieldOf("entries").forGetter(InjectedPool::entries)
    ).apply(instance, InjectedPool::new));
  }

  /**
   * Resolves a Forge-convention pool name to its index in the target table.
   * @return pool index, or -1 if the name is not index-shaped
   */
  public static int poolIndex(String name) {
    if ("main".equals(name)) {
      return 0;
    }
    if (name.startsWith("pool")) {
      try {
        return Integer.parseInt(name.substring("pool".length()));
      } catch (NumberFormatException ignored) {
        // fall through
      }
    }
    return -1;
  }

  /** Datagen builder */
  public static class Builder {
    private final ResourceLocation name;
    private final Map<String, List<LootPoolEntryContainer>> pools = new LinkedHashMap<>();

    public Builder(ResourceLocation name) {
      this.name = name;
    }

    /** Adds entries to the pool with the given Forge-convention name */
    public Builder addToPool(String pool, LootPoolEntryContainer... entries) {
      List.of(entries); // eager null check
      this.pools.computeIfAbsent(pool, k -> new ArrayList<>()).addAll(List.of(entries));
      return this;
    }

    public LootTableInjection build() {
      return new LootTableInjection(name, pools.entrySet().stream()
        .map(entry -> new InjectedPool(entry.getKey(), List.copyOf(entry.getValue())))
        .toList());
    }
  }
}
