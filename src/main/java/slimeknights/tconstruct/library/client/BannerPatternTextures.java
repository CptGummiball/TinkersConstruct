package slimeknights.tconstruct.library.client;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

/**
 * Lists the banner patterns that have a shield texture.
 *
 * <p>1.20 read {@code Sheets.SHIELD_MATERIALS}, a map built eagerly from the banner pattern
 * registry. 1.21 made banner patterns a datapack registry and fills that map lazily on first
 * render, so it is empty for the whole of resource loading — which is exactly when the atlas is
 * stitched and models are baked. The pack files are listed instead, which has the same content and
 * additionally picks up patterns added by a datapack or resource pack.
 *
 * <p>Shared by the sprite source that generates the modifier textures and by the model that draws
 * them, so both agree on which patterns exist.
 */
public final class BannerPatternTextures {
  private BannerPatternTextures() {}

  /** Where vanilla keeps one texture per banner pattern, named after the pattern's path */
  public static final FileToIdConverter SHIELD_TEXTURES = new FileToIdConverter("textures/entity/shield", ".png");

  /**
   * Lists every banner pattern texture in the given manager.
   * @return  Pairs of texture file id and the pattern asset id its name yields
   */
  public static List<Entry<ResourceLocation,Resource>> listResources(ResourceManager manager) {
    List<Entry<ResourceLocation,Resource>> found = new ArrayList<>();
    for (Entry<ResourceLocation,Resource> entry : SHIELD_TEXTURES.listMatchingResources(manager).entrySet()) {
      // patterns live directly in the folder; anything nested belongs to something else
      if (SHIELD_TEXTURES.fileToId(entry.getKey()).getPath().indexOf('/') == -1) {
        found.add(entry);
      }
    }
    return found;
  }

  /** Asset ids of every banner pattern with a shield texture, read from the client resource manager */
  public static List<ResourceLocation> listPatterns() {
    Minecraft minecraft = Minecraft.getInstance();
    if (minecraft == null) {
      return List.of();
    }
    List<ResourceLocation> patterns = new ArrayList<>();
    for (Entry<ResourceLocation,Resource> entry : listResources(minecraft.getResourceManager())) {
      patterns.add(SHIELD_TEXTURES.fileToId(entry.getKey()));
    }
    return patterns;
  }
}
