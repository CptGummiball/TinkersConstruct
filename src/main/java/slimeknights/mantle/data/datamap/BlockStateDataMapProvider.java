package slimeknights.mantle.data.datamap;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.Property;
import slimeknights.mantle.client.render.BlockStateDataMap;
import slimeknights.mantle.data.GenericDataProvider;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

/**
 * Datagen counterpart of {@link BlockStateDataMap}: writes template files holding a bare value
 * and per-block files holding a {@code variants} map whose values are either inline data or the
 * string id of a template. The runtime reader resolves the references, so seared and scorched
 * variants of a block can share one template file.
 */
public abstract class BlockStateDataMapProvider<T> extends GenericDataProvider {
  private final BlockStateDataMap<T> map;
  protected final String modId;
  private final Map<String, T> templates = new LinkedHashMap<>();
  private final Map<ResourceLocation, BlockBuilder> blocks = new LinkedHashMap<>();

  public BlockStateDataMapProvider(PackOutput packOutput, PackOutput.Target target, BlockStateDataMap<T> map, String modId) {
    super(packOutput, target, map.getFolder());
    this.map = map;
    this.modId = modId;
  }

  /** Adds all entries to the provider */
  protected abstract void addEntries();

  /** Registers a template value under the given key, e.g. {@code templates/tank} */
  protected void entry(String key, T value) {
    if (templates.putIfAbsent(key, value) != null) {
      throw new IllegalArgumentException("Duplicate template " + key);
    }
  }

  /** Starts building variants for the given block */
  protected BlockBuilder block(Block block) {
    return blocks.computeIfAbsent(BuiltInRegistries.BLOCK.getKey(block), id -> new BlockBuilder());
  }

  /** Starts building variants for the given block */
  protected BlockBuilder block(Supplier<? extends Block> block) {
    return block(block.get());
  }

  @Override
  public CompletableFuture<?> run(CachedOutput cache) {
    addEntries();
    List<CompletableFuture<?>> futures = new ArrayList<>();
    templates.forEach((key, value) -> futures.add(saveJson(cache, ResourceLocation.fromNamespaceAndPath(modId, key), map.serialize(value))));
    blocks.forEach((id, builder) -> futures.add(saveJson(cache, id, builder.toJson())));
    return allOf(futures.stream());
  }

  /** Builder for one block's variant list */
  protected class BlockBuilder {
    private final List<VariantBuilder> variants = new ArrayList<>();

    /** Adds a variant referencing a template by key; {@link VariantBuilder#when} narrows the states it applies to. Templates may be registered after being referenced. */
    public VariantBuilder variant(String template) {
      return variant(new JsonPrimitive(modId + ":" + template));
    }

    /** Adds a variant with an inline value; {@link VariantBuilder#when} narrows the states it applies to */
    public VariantBuilder variant(T value) {
      return variant(map.serialize(value));
    }

    private VariantBuilder variant(JsonElement value) {
      VariantBuilder builder = new VariantBuilder(this, value);
      variants.add(builder);
      return builder;
    }

    private JsonObject toJson() {
      JsonObject variantsJson = new JsonObject();
      for (VariantBuilder variant : variants) {
        String key = variant.key();
        if (variantsJson.has(key)) {
          throw new IllegalStateException("Duplicate variant key '" + key + "'");
        }
        variantsJson.add(key, variant.value);
      }
      JsonObject json = new JsonObject();
      json.add("variants", variantsJson);
      return json;
    }
  }

  /** Builder for one variant of a block: the value plus the property predicate selecting it */
  protected class VariantBuilder {
    private final BlockBuilder parent;
    private final JsonElement value;
    private final List<String> conditions = new ArrayList<>();

    private VariantBuilder(BlockBuilder parent, JsonElement value) {
      this.parent = parent;
      this.value = value;
    }

    /** Requires the given property value for this variant */
    public <V extends Comparable<V>> VariantBuilder when(Property<V> property, V value) {
      conditions.add(property.getName() + "=" + property.getName(value));
      return this;
    }

    /** Returns to the block builder */
    public BlockBuilder end() {
      return parent;
    }

    /** Adds a variant referencing a template, shorthand for {@code end().variant(template)} */
    public VariantBuilder variant(String template) {
      return parent.variant(template);
    }

    /** Adds a variant with an inline value, shorthand for {@code end().variant(value)} */
    public VariantBuilder variant(T value) {
      return parent.variant(value);
    }

    private String key() {
      return String.join(",", conditions);
    }
  }
}
