package slimeknights.mantle.data;

import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.client.renderer.texture.atlas.SpriteSources;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Provider for atlas configuration files ({@code assets/<namespace>/atlases}), port of Forge's
 * {@code net.minecraftforge.common.data.SpriteSourceProvider} on vanilla's sprite source codec.
 */
public abstract class SpriteSourceProvider implements DataProvider {
  protected static final ResourceLocation BLOCKS_ATLAS = ResourceLocation.withDefaultNamespace("blocks");

  private final PackOutput output;
  private final ExistingFileHelper fileHelper;
  private final String modId;
  private final Map<ResourceLocation, SourceList> atlases = new LinkedHashMap<>();

  public SpriteSourceProvider(PackOutput output, ExistingFileHelper fileHelper, String modId) {
    this.output = output;
    this.fileHelper = fileHelper;
    this.modId = modId;
  }

  /** Adds all sources through {@link #atlas} */
  protected abstract void addSources();

  /** Gets or creates the source list for the given atlas */
  protected SourceList atlas(ResourceLocation atlas) {
    return atlases.computeIfAbsent(atlas, $ -> new SourceList());
  }

  @Override
  public CompletableFuture<?> run(CachedOutput cache) {
    atlases.clear();
    addSources();
    PackOutput.PathProvider pathProvider = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "atlases");
    List<CompletableFuture<?>> futures = new ArrayList<>();
    atlases.forEach((atlas, sources) -> {
      JsonElement json = SpriteSources.FILE_CODEC.encodeStart(JsonOps.INSTANCE, sources.sources).getOrThrow();
      futures.add(DataProvider.saveStable(cache, json, pathProvider.json(atlas)));
    });
    return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
  }

  @Override
  public String getName() {
    return "Sprite Source Provider: " + modId;
  }

  protected static final class SourceList {
    private final List<SpriteSource> sources = new ArrayList<>();

    /** Adds the given source to this atlas */
    public SourceList addSource(SpriteSource source) {
      sources.add(source);
      return this;
    }
  }
}
