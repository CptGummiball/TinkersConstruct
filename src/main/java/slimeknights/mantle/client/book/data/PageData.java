package slimeknights.mantle.client.book.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import slimeknights.mantle.Mantle;
import slimeknights.mantle.client.book.BookLoader;
import slimeknights.mantle.client.book.data.content.ContentBlank;
import slimeknights.mantle.client.book.data.content.PageContent;
import slimeknights.mantle.client.book.repository.BookRepository;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/** One page of a section: which content class to build and where its data lives */
public class PageData {
  /** Data value meaning "this page has no file", used by pages that only exist to carry extraData */
  public static final String NO_LOAD = "no-load";

  /** True for pages built in code rather than read from a section file */
  public final transient boolean custom;

  /** Page name, unique within its section; used by links and by the language file */
  public String name = "";
  /** Registered page content type */
  @Nullable
  public ResourceLocation type = null;
  /** Path to this page's data file, relative to the book root */
  public String data = "";
  /** Arbitrary per-page data read by transformers */
  public Map<ResourceLocation,JsonElement> extraData = new HashMap<>();

  /** Deserialized content; null until {@link #load()} runs */
  public transient PageContent content;
  /** Section this page belongs to */
  public transient SectionData parent;
  /** Where this page's files come from */
  public transient BookRepository source;
  /** Cached title, resolved on first use */
  private transient String title;

  public PageData() {
    this(false);
  }

  public PageData(boolean custom) {
    this.custom = custom;
  }

  /**
   * Builds this page's content.
   *
   * <p>Pages created by transformers already carry their content, so this only deserializes when
   * there is a file to read; either way the content is bound to the page and given a chance to
   * look at the world.
   */
  public void load() {
    if (this.content == null) {
      Class<? extends PageContent> contentClass = this.type == null ? null : BookLoader.getPageType(this.type);
      if (contentClass == null) {
        if (this.type != null) {
          Mantle.logger.error("Unknown book page type {} on page {}", this.type, this.name);
        }
        this.content = new ContentBlank();
      } else if (this.data == null || this.data.isEmpty() || NO_LOAD.equals(this.data)) {
        // a page with a registered type but no file still gets its content, some fill themselves in
        try {
          this.content = contentClass.getConstructor().newInstance();
        } catch (ReflectiveOperationException e) {
          Mantle.logger.error("Failed to instantiate book page type {}", this.type, e);
          this.content = new ContentBlank();
        }
      } else {
        this.content = readContent(contentClass);
      }
    }
    this.content.parent = this;
    try {
      this.content.load();
    } catch (Exception e) {
      Mantle.logger.error("Failed to load book page {} of type {}", this.name, this.type, e);
    }
  }

  /** Reads this page's data file into a content instance */
  private PageContent readContent(Class<? extends PageContent> contentClass) {
    ResourceLocation location = this.source == null ? null : this.source.getResourceLocation(this.data);
    Resource resource = location == null ? null : this.source.getResource(location);
    if (resource == null) {
      Mantle.logger.error("Missing book page file {} for page {}", this.data, this.name);
      return new ContentBlank();
    }
    try {
      PageContent content = BookLoader.GSON.fromJson(this.source.resourceToString(resource), contentClass);
      return content == null ? new ContentBlank() : content;
    } catch (JsonParseException e) {
      Mantle.logger.error("Failed to parse book page {}", location, e);
      return new ContentBlank();
    }
  }

  /**
   * Title shown in indexes.
   *
   * <p>The language file wins over the content's own title: that is how the encyclopedia shortens
   * "Earthslime Staff" to "Earthslime" in its tool index without touching the item name.
   */
  public String getTitle() {
    if (this.title == null) {
      String key = this.parent != null ? this.parent.name + "." + this.name : this.name;
      if (this.parent != null && this.parent.parent != null && this.parent.parent.strings.containsKey(key)) {
        this.title = this.parent.parent.strings.get(key);
      } else if (this.content != null) {
        this.title = this.content.getTitle();
      } else {
        this.title = "";
      }
    }
    return this.title;
  }

  /** True if this page carries no file of its own */
  public boolean isUnloaded() {
    return this.data == null || this.data.isEmpty() || NO_LOAD.equals(this.data);
  }

  @Override
  public String toString() {
    return "PageData(" + (this.parent == null ? "?" : this.parent.name) + "." + this.name + ")";
  }
}
