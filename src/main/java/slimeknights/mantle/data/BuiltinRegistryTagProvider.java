package slimeknights.mantle.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider.IntrinsicTagAppender;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * {@link MantleTagsProvider} over a builtin registry, adding the intrinsic value appenders:
 * vanilla's intrinsic provider with the Forge constructor shape and writing semantics.
 */
public abstract class BuiltinRegistryTagProvider<T> extends MantleTagsProvider<T> {
  private final Function<T, net.minecraft.resources.ResourceKey<T>> keyExtractor;

  @SuppressWarnings("deprecation")  // the registry is needed for the intrinsic lookup
  public BuiltinRegistryTagProvider(PackOutput packOutput, Registry<T> registry, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable CompletableFuture<TagLookup<?>> copySource, String modId, @Nullable ExistingFileHelper existingFileHelper) {
    super(packOutput, registry.key(), lookupProvider, copySource, modId, existingFileHelper);
    this.keyExtractor = value -> registry.getResourceKey(value).orElseThrow();
  }

  public BuiltinRegistryTagProvider(PackOutput packOutput, Registry<T> registry, CompletableFuture<HolderLookup.Provider> lookupProvider, String modId, @Nullable ExistingFileHelper existingFileHelper) {
    this(packOutput, registry, lookupProvider, null, modId, existingFileHelper);
  }

  @Override
  protected IntrinsicTagAppender<T> tag(TagKey<T> tag) {
    return new IntrinsicTagAppender<>(this.getOrCreateRawBuilder(tag), keyExtractor);
  }

  /** Records entries to remove from the given tag, Forge's tag-format extension. */
  @SuppressWarnings("unchecked")
  protected void removeFromTag(TagKey<T> tag, T... values) {
    removeAllFromTag(tag, Arrays.stream(values).map(value -> keyExtractor.apply(value).location()).toList());
  }
}
