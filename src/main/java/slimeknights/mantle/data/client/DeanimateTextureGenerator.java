package slimeknights.mantle.data.client;

import com.google.common.hash.Hashing;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.Util;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.PackOutput;
import net.minecraft.data.PackOutput.Target;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import slimeknights.mantle.Mantle;
import slimeknights.mantle.data.ExistingFileHelper;
import slimeknights.mantle.data.GenericDataProvider;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

/**
 * Data generator writing the first frame of an animated texture to a new location, e.g. the
 * fluid camera overlays. The frame is assumed square (width by width), which holds for every
 * vanilla-style vertical animation strip.
 */
public abstract class DeanimateTextureGenerator extends GenericDataProvider {
  private static final ExistingFileHelper.ResourceType TEXTURE = new ExistingFileHelper.ResourceType(PackType.CLIENT_RESOURCES, ".png", "textures");

  protected final ExistingFileHelper existingFileHelper;
  private final List<CompletableFuture<?>> tasks = new ArrayList<>();
  /** Cache of the running generation, set for the duration of {@link #addTextures()} */
  private CachedOutput cache;

  public DeanimateTextureGenerator(PackOutput packOutput, ExistingFileHelper existingFileHelper) {
    super(packOutput, Target.RESOURCE_PACK, "textures");
    this.existingFileHelper = existingFileHelper;
  }

  /** Implement to add all textures through {@link #deanimate} */
  protected abstract void addTextures();

  @Override
  public CompletableFuture<?> run(CachedOutput cache) {
    this.cache = cache;
    addTextures();
    return CompletableFuture.allOf(tasks.toArray(new CompletableFuture[0]));
  }

  /** Writes the first frame of the source texture to the destination */
  protected void deanimate(ResourceLocation source, ResourceLocation destination) {
    try (InputStream stream = existingFileHelper.getResource(source, TEXTURE).open();
         NativeImage full = NativeImage.read(stream)) {
      int width = full.getWidth();
      NativeImage frame;
      if (full.getHeight() > width) {
        frame = new NativeImage(width, width, false);
        for (int y = 0; y < width; y++) {
          for (int x = 0; x < width; x++) {
            frame.setPixelRGBA(x, y, full.getPixelRGBA(x, y));
          }
        }
      } else {
        frame = full;
      }
      existingFileHelper.trackGenerated(destination, TEXTURE);
      byte[] bytes = frame.asByteArray();
      if (frame != full) {
        frame.close();
      }
      Path path = this.pathProvider.file(destination, "png");
      tasks.add(CompletableFuture.runAsync(() -> {
        try {
          cache.writeIfNeeded(path, bytes, Hashing.sha1().hashBytes(bytes));
        } catch (IOException e) {
          Mantle.logger.error("Couldn't write image for {}", destination, e);
          throw new CompletionException(e);
        }
      }, Util.backgroundExecutor()));
    } catch (IOException e) {
      Mantle.logger.error("Failed to deanimate {} into {}", source, destination, e);
      tasks.add(CompletableFuture.failedFuture(e));
    }
  }
}
