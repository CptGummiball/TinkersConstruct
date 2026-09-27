package slimeknights.mantle.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagBuilder;
import net.minecraft.tags.TagFile;
import net.minecraft.tags.TagKey;
import slimeknights.mantle.Mantle;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Tag provider with Forge's writing semantics: cross-mod tag references stay untouched
 * rather than failing validation — Forge checked them against the existing file helper,
 * which saw every loaded datapack including Forge's own conventional tags; on Fabric the
 * conventional layer only exists at runtime, so datagen must trust the reference. Elements
 * added through the typed appenders are real registry objects and correct by construction.
 *
 * <p>Also carries Forge's tag-remove extension ({@link #removeFromTag}) and the item
 * provider's block-tag copying ({@link #copy}).
 */
public abstract class MantleTagsProvider<T> extends TagsProvider<T> {
  protected final String modId;
  private final PackOutput.PathProvider mantlePathProvider;
  private final CompletableFuture<HolderLookup.Provider> mantleLookup;
  private final Map<TagKey<T>, List<ResourceLocation>> removals = new LinkedHashMap<>();
  private final Map<TagKey<?>, TagKey<T>> copies = new LinkedHashMap<>();
  @Nullable
  private final CompletableFuture<TagLookup<?>> copySource;
  /** Completed once this provider built its tags, replacing vanilla's contents future for copy chaining */
  private final CompletableFuture<TagLookup<T>> mantleContents = new CompletableFuture<>();

  public MantleTagsProvider(PackOutput packOutput, ResourceKey<? extends Registry<T>> registryKey, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable CompletableFuture<TagLookup<?>> copySource, String modId, @Nullable ExistingFileHelper existingFileHelper) {
    super(packOutput, registryKey, lookupProvider);
    this.modId = modId;
    this.mantleLookup = lookupProvider;
    this.copySource = copySource;
    this.mantlePathProvider = packOutput.createPathProvider(PackOutput.Target.DATA_PACK, Registries.tagsDirPath(registryKey));
  }

  @Override
  public CompletableFuture<TagLookup<T>> contentsGetter() {
    return mantleContents;
  }

  /** Records entries to remove from the given tag, Forge's tag-format extension. */
  protected void removeAllFromTag(TagKey<T> tag, List<ResourceLocation> values) {
    this.getOrCreateRawBuilder(tag);
    removals.computeIfAbsent(tag, k -> new ArrayList<>()).addAll(values);
  }

  /** Copies all entries of another registry's tag into the given tag, the item provider's block copying */
  protected void copy(TagKey<?> from, TagKey<T> to) {
    if (copySource == null) {
      throw new IllegalStateException("This provider has no copy source");
    }
    copies.put(from, to);
  }

  @Override
  public CompletableFuture<?> run(CachedOutput cache) {
    CompletableFuture<TagLookup<?>> copyFuture = copySource == null ? CompletableFuture.completedFuture(null) : copySource;
    return mantleLookup.thenCombine(copyFuture, (provider, copyLookup) -> new Object[] {provider, copyLookup}).thenCompose(pair -> {
      HolderLookup.Provider provider = (HolderLookup.Provider)pair[0];
      @SuppressWarnings("unchecked")
      TagLookup<Object> copyLookup = (TagLookup<Object>)pair[1];
      this.builders.clear();
      this.addTags(provider);
      // resolve copies now that the source provider built its tags
      copies.forEach((from, to) -> {
        TagBuilder target = this.getOrCreateRawBuilder(to);
        @SuppressWarnings({"unchecked", "rawtypes"})
        java.util.Optional<TagBuilder> source = (java.util.Optional<TagBuilder>)(java.util.Optional)copyLookup.apply((TagKey)from);
        source.orElseThrow(() -> new IllegalStateException("Missing copy source tag " + from)).build().forEach(target::add);
      });
      this.mantleContents.complete(tag -> java.util.Optional.ofNullable(this.builders.get(tag.location())));
      List<CompletableFuture<?>> futures = new ArrayList<>();
      this.builders.forEach((id, builder) -> {
        JsonElement json = TagFile.CODEC.encodeStart(JsonOps.INSTANCE, new TagFile(builder.build(), false)).getOrThrow();
        List<ResourceLocation> removed = removals.get(TagKey.create(this.registryKey, id));
        if (removed != null && !removed.isEmpty()) {
          JsonArray array = new JsonArray();
          removed.forEach(rl -> array.add(rl.toString()));
          JsonObject object = json.getAsJsonObject();
          object.add("remove", array);
          json = object;
        }
        futures.add(DataProvider.saveStable(cache, json, this.mantlePathProvider.json(id)));
      });
      Mantle.logger.debug("Wrote {} tags for {}", this.builders.size(), this.registryKey.location());
      return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
    });
  }
}
