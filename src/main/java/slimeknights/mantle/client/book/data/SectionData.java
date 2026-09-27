package slimeknights.mantle.client.book.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import slimeknights.mantle.Mantle;
import slimeknights.mantle.client.book.BookLoader;
import slimeknights.mantle.client.book.data.element.ItemStackData;
import slimeknights.mantle.client.book.repository.BookRepository;
import slimeknights.mantle.recipe.condition.ICondition;
import slimeknights.mantle.util.DataLoadedConditionContext;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** One chapter of a book: a name, an icon for the index, and a list of pages */
public class SectionData {
  /** Section name, used for links and as the language file key */
  public String name = "";
  /** Path to the file listing this section's pages, relative to the book root */
  public String data = "";
  /** Icon shown for this section on the book's index */
  @Nullable
  public ItemStackData icon = null;
  /** Guard deciding whether this section appears at all */
  @Nullable
  public ICondition condition = null;
  /** Arbitrary per-section data read by transformers */
  public Map<ResourceLocation,JsonElement> extraData = new HashMap<>();

  /** Pages in this section, in order */
  public final transient List<PageData> pages = new ArrayList<>();
  /** Book this section belongs to */
  public transient BookData parent;
  /** Where this section's files come from */
  public transient BookRepository source;

  public SectionData() {}

  public SectionData(String name) {
    this.name = name;
  }

  /** Binds this section to a repository before it loads */
  public void update(BookRepository source) {
    this.source = source;
  }

  /** True if this section's condition rejects it */
  public boolean isConditionFailed() {
    return this.condition != null && !this.condition.test(DataLoadedConditionContext.INSTANCE);
  }

  /** Reads this section's page list and loads every page */
  public void load() {
    if (this.source == null) {
      this.source = BookRepository.DUMMY;
    }
    if (this.data != null && !this.data.isEmpty() && !PageData.NO_LOAD.equals(this.data)) {
      ResourceLocation location = this.source.getResourceLocation(this.data);
      Resource resource = location == null ? null : this.source.getResource(location);
      if (resource == null) {
        Mantle.logger.error("Missing section file {} for section {}", this.data, this.name);
      } else {
        try {
          PageData[] loaded = BookLoader.GSON.fromJson(this.source.resourceToString(resource), PageData[].class);
          if (loaded != null) {
            this.pages.addAll(Arrays.asList(loaded));
          }
        } catch (JsonParseException e) {
          Mantle.logger.error("Failed to parse section {}", location, e);
        }
      }
    }
    for (PageData page : this.pages) {
      page.parent = this;
      if (page.source == null) {
        page.source = this.source;
      }
      page.load();
    }
  }

  /** Display name of this section, from the book's language file */
  public String getTitle() {
    return this.parent == null ? this.name : this.parent.translate(this.name);
  }

  /** Looks up a string scoped to this section */
  public String translate(String key) {
    return this.parent == null ? key : this.parent.translate(this.name + "." + key);
  }

  /** Index of the given page within this section, or -1 */
  public int getPageNumber(PageData page) {
    return this.pages.indexOf(page);
  }

  /** Finds a page by name */
  @Nullable
  public PageData getPage(String name) {
    for (PageData page : this.pages) {
      if (page.name.equals(name)) {
        return page;
      }
    }
    return null;
  }

  /** True if this section has nothing to show */
  public boolean isUnloaded() {
    return this.pages.isEmpty();
  }

  @Override
  public String toString() {
    return "SectionData(" + this.name + ")";
  }
}
