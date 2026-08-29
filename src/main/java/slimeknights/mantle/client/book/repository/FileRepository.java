package slimeknights.mantle.client.book.repository;

import com.google.gson.JsonParseException;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import slimeknights.mantle.Mantle;
import slimeknights.mantle.client.book.BookLoader;
import slimeknights.mantle.client.book.data.SectionData;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * Reads a book out of the resource pack tree rooted at one location.
 *
 * <p>The tree is {@code <root>/index.json}, {@code <root>/appearance.json},
 * {@code <root>/sections/*.json} and one folder per language holding the page files. A path from
 * the book data is looked up in the active language first, then in {@code en_us}, then at the book
 * root — which is how the shared {@code sections} folder and the translated page folders coexist
 * without either having to name the other.
 */
public class FileRepository extends BookRepository {
  /** Language every book is written in, used when the active language has no copy of a page */
  public static final String DEFAULT_LANGUAGE = "en_us";

  private final ResourceLocation root;

  public FileRepository(ResourceLocation root) {
    this.root = root;
  }

  public FileRepository(String root) {
    this(ResourceLocation.parse(root));
  }

  /** Root of this book in the resource tree */
  public ResourceLocation getRoot() {
    return this.root;
  }

  @Override
  public List<SectionData> getSections() {
    List<SectionData> sections = new ArrayList<>();
    try {
      ResourceLocation index = getResourceLocation("index.json");
      if (index == null || !resourceExists(index)) {
        return sections;
      }
      SectionData[] loaded = BookLoader.GSON.fromJson(resourceToString(getResource(index)), SectionData[].class);
      if (loaded != null) {
        sections.addAll(Arrays.asList(loaded));
      }
    } catch (JsonParseException e) {
      Mantle.logger.error("Failed to load index for book {}", this.root, e);
    }
    return sections;
  }

  @Nullable
  @Override
  public ResourceLocation getResourceLocation(@Nullable String path, boolean safe) {
    if (path == null) {
      return null;
    }
    // a fully qualified id points outside the book, used by structure pages
    if (path.indexOf(':') >= 0) {
      ResourceLocation direct = ResourceLocation.tryParse(path);
      if (direct == null && !safe) {
        Mantle.logger.error("Invalid book resource path {}", path);
      }
      return direct;
    }
    String language = currentLanguage();
    ResourceLocation localized = child(language + "/" + path);
    if (localized != null && resourceExists(localized)) {
      return localized;
    }
    if (!DEFAULT_LANGUAGE.equals(language)) {
      ResourceLocation fallback = child(DEFAULT_LANGUAGE + "/" + path);
      if (fallback != null && resourceExists(fallback)) {
        return fallback;
      }
    }
    // shared files (index, appearance, section descriptors) live above the language folders
    ResourceLocation shared = child(path);
    if (shared != null && resourceExists(shared)) {
      return shared;
    }
    // nothing exists; hand back the localized path so callers report the language they wanted
    return localized != null ? localized : shared;
  }

  /** Builds a location under this book's root */
  @Nullable
  private ResourceLocation child(String path) {
    return ResourceLocation.tryBuild(this.root.getNamespace(), this.root.getPath() + "/" + path);
  }

  /** Language code the client is currently using */
  private static String currentLanguage() {
    Minecraft minecraft = Minecraft.getInstance();
    if (minecraft == null || minecraft.getLanguageManager() == null) {
      return DEFAULT_LANGUAGE;
    }
    return minecraft.getLanguageManager().getSelected().toLowerCase(Locale.ROOT);
  }

  /** Client resource manager, or null before the game is up */
  @Nullable
  private static ResourceManager manager() {
    Minecraft minecraft = Minecraft.getInstance();
    return minecraft == null ? null : minecraft.getResourceManager();
  }

  @Override
  public boolean resourceExists(@Nullable ResourceLocation location) {
    ResourceManager manager = manager();
    return location != null && manager != null && manager.getResource(location).isPresent();
  }

  @Nullable
  @Override
  public Resource getResource(@Nullable ResourceLocation location) {
    ResourceManager manager = manager();
    if (location == null || manager == null) {
      return null;
    }
    Optional<Resource> resource = manager.getResource(location);
    return resource.orElse(null);
  }
}
