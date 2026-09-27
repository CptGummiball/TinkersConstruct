package slimeknights.mantle.client.book.data;

import com.google.gson.JsonParseException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import slimeknights.mantle.Mantle;
import slimeknights.mantle.client.book.BookHelper;
import slimeknights.mantle.client.book.BookLoader;
import slimeknights.mantle.client.book.BookScreenOpener;
import slimeknights.mantle.client.book.repository.BookRepository;
import slimeknights.mantle.client.book.transformer.BookTransformer;
import slimeknights.mantle.client.screen.book.BookScreen;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A whole book: its sections, its look, and the strings it is written in.
 *
 * <p>Assembled once and cached. Loading walks every repository for sections, reads their pages, and
 * then hands the result to the transformers, which is where generated content — the index, the tag
 * injected tool and modifier pages, the material listings — is added. Transformer order matters and
 * is the caller's responsibility; padding always goes last because it counts pages.
 */
public class BookData implements BookScreenOpener {
  /** Id this book was registered under */
  @Nullable
  public ResourceLocation id = null;

  /** Sections in book order; valid after {@link #load()} */
  public final List<SectionData> sections = new ArrayList<>();
  /** How the book looks */
  public BookAppearance appearance = new BookAppearance();
  /** Strings from the book's own language file */
  public final Map<String,String> strings = new HashMap<>();
  /** Font pages measure and draw with */
  public Font fontRenderer;

  private final List<BookRepository> repositories = new ArrayList<>();
  private final List<BookTransformer> transformers = new ArrayList<>();
  private boolean initialized = false;

  /** Adds a source of sections; call before the book is first opened */
  public void addRepository(BookRepository repository) {
    this.repositories.add(repository);
  }

  /** Adds a transformer, which runs after every section has loaded */
  public void addTransformer(BookTransformer transformer) {
    if (!this.transformers.contains(transformer)) {
      this.transformers.add(transformer);
    }
  }

  /** Drops the loaded content so the next open rebuilds it */
  public void reset() {
    this.initialized = false;
    this.sections.clear();
    this.strings.clear();
  }

  /** Rebuilds the book from scratch */
  public void fullReload() {
    this.reset();
    this.load();
  }

  /** Builds the book if it has not been built yet */
  public void load() {
    if (this.initialized) {
      return;
    }
    // set before loading: a page that throws must not leave the book retrying on every open
    this.initialized = true;
    if (this.fontRenderer == null) {
      this.fontRenderer = Minecraft.getInstance().font;
    }

    for (BookRepository repository : this.repositories) {
      loadAppearance(repository);
      loadStrings(repository);
      try {
        for (SectionData section : repository.getSections()) {
          section.parent = this;
          section.update(repository);
          if (section.isConditionFailed()) {
            continue;
          }
          section.load();
          this.sections.add(section);
        }
      } catch (Exception e) {
        Mantle.logger.error("Failed to load sections for book {}", this.id, e);
      }
    }

    for (BookTransformer transformer : this.transformers) {
      try {
        transformer.transform(this);
      } catch (Exception e) {
        Mantle.logger.error("Book transformer {} failed on book {}", transformer.getClass().getSimpleName(), this.id, e);
      }
    }

    Mantle.logger.debug("Loaded book {} with {} sections and {} pages", this.id, this.sections.size(), getPageCount());
  }

  /** Reads appearance.json if the repository has one */
  private void loadAppearance(BookRepository repository) {
    ResourceLocation location = repository.getResourceLocation("appearance.json", true);
    Resource resource = repository.getResource(location);
    if (resource == null) {
      return;
    }
    try {
      BookAppearance appearance = BookLoader.GSON.fromJson(repository.resourceToString(resource), BookAppearance.class);
      if (appearance != null) {
        this.appearance = appearance;
      }
    } catch (JsonParseException e) {
      Mantle.logger.error("Failed to parse appearance for book {}", this.id, e);
    }
  }

  /**
   * Reads the book's language file.
   *
   * <p>Books ship a key/value file per language rather than using the mod's lang file, so section
   * and page titles live next to the pages they name.
   */
  private void loadStrings(BookRepository repository) {
    ResourceLocation location = repository.getResourceLocation("language.lang", true);
    Resource resource = repository.getResource(location);
    if (resource == null) {
      return;
    }
    for (String line : repository.resourceToString(resource, true).split("\n")) {
      String trimmed = line.trim();
      int split = trimmed.indexOf('=');
      if (trimmed.isEmpty() || split <= 0) {
        continue;
      }
      this.strings.put(trimmed.substring(0, split).trim(), trimmed.substring(split + 1));
    }
  }

  /** Looks up a string from the book's language file, falling back to the key */
  public String translate(String key) {
    return this.strings.getOrDefault(key, key);
  }

  /** Finds a section by name */
  @Nullable
  public SectionData findSection(String name) {
    for (SectionData section : this.sections) {
      if (section.name.equalsIgnoreCase(name)) {
        return section;
      }
    }
    return null;
  }

  /** Every page in the book, in reading order */
  public List<PageData> getPages() {
    List<PageData> pages = new ArrayList<>();
    for (SectionData section : this.sections) {
      pages.addAll(section.pages);
    }
    return pages;
  }

  /** Total number of pages */
  public int getPageCount() {
    int count = 0;
    for (SectionData section : this.sections) {
      count += section.pages.size();
    }
    return count;
  }

  /** Index of the given page in the whole book, or -1 */
  public int findPageNumber(PageData page) {
    int index = 0;
    for (SectionData section : this.sections) {
      int inSection = section.pages.indexOf(page);
      if (inSection >= 0) {
        return index + inSection;
      }
      index += section.pages.size();
    }
    return -1;
  }

  /** Resolves a "section.page" reference to a page index, or -1 */
  public int findPageNumber(String location) {
    int split = location.indexOf('.');
    String sectionName = split < 0 ? location : location.substring(0, split);
    SectionData section = findSection(sectionName);
    if (section == null) {
      return -1;
    }
    if (split < 0) {
      return findFirstPageNumber(section);
    }
    PageData page = section.getPage(location.substring(split + 1));
    return page == null ? findFirstPageNumber(section) : findPageNumber(page);
  }

  /** Index of a section's first page */
  public int findFirstPageNumber(SectionData target) {
    int index = 0;
    for (SectionData section : this.sections) {
      if (section == target) {
        return index;
      }
      index += section.pages.size();
    }
    return -1;
  }

  /** Page at the given index, or null */
  @Nullable
  public PageData getPage(int index) {
    if (index < 0) {
      return null;
    }
    for (SectionData section : this.sections) {
      if (index < section.pages.size()) {
        return section.pages.get(index);
      }
      index -= section.pages.size();
    }
    return null;
  }

  /** Title used by the screen and by the HTML export */
  public Component getTitleComponent() {
    if (this.appearance.exportTitle != null && !this.appearance.exportTitle.isEmpty()) {
      return Component.literal(this.appearance.exportTitle);
    }
    return this.id == null ? Component.empty() : Component.translatable("item." + this.id.getNamespace() + "." + this.id.getPath());
  }

  /* Opening */

  @Override
  public void openGui(@Nullable InteractionHand hand, ItemStack item) {
    openScreen(BookHelper.getSavedPage(item), BookScreen.PageUpdater.forHand(hand));
  }

  @Override
  public void openGui(int slot, ItemStack item) {
    openScreen(BookHelper.getSavedPage(item), BookScreen.PageUpdater.forSlot(slot));
  }

  @Override
  public void openGui(BlockPos pos, ItemStack item) {
    openScreen(BookHelper.getSavedPage(item), BookScreen.PageUpdater.forLectern(pos));
  }

  /** Shared open path; loads the book first so a failure shows an empty book rather than a crash */
  private void openScreen(String page, BookScreen.PageUpdater updater) {
    this.load();
    Minecraft.getInstance().setScreen(new BookScreen(getTitleComponent(), this, page, updater));
  }
}
