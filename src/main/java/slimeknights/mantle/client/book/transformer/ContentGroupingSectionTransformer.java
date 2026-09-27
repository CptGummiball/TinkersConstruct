package slimeknights.mantle.client.book.transformer;

import slimeknights.mantle.client.book.data.BookData;
import slimeknights.mantle.client.book.data.PageData;
import slimeknights.mantle.client.book.data.SectionData;
import slimeknights.mantle.client.book.data.content.ContentIndex;
import slimeknights.mantle.client.book.data.content.ContentListing;
import slimeknights.mantle.client.screen.book.BookScreen;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Builds a section's opening listing out of the pages the section ended up with.
 *
 * <p>The listing cannot be written by hand because most of these sections are filled by tag
 * injection: which tools and modifiers exist is a datapack question. Subclasses decide, per page,
 * whether it becomes a link, a group heading, or nothing at all.
 */
public abstract class ContentGroupingSectionTransformer extends SectionTransformer {
  /** Height of one listing line */
  private static final int LINE_HEIGHT = 9;
  /** Rough height the subtext takes when a section has one */
  private static final int SUBTEXT_HEIGHT = 40;

  @Nullable
  private final Boolean largeTitle;
  @Nullable
  private final Boolean centerTitle;

  public ContentGroupingSectionTransformer(String sectionName, @Nullable Boolean largeTitle, @Nullable Boolean centerTitle) {
    super(sectionName);
    this.largeTitle = largeTitle;
    this.centerTitle = centerTitle;
  }

  /**
   * Adds one page to the listing.
   * @return  false to drop the page from the section entirely, which is how group markers with no
   *          content of their own disappear once their heading has been recorded
   */
  protected abstract boolean processPage(BookData book, GroupingBuilder builder, PageData page);

  /** Title for the generated listing */
  protected String getTitle(BookData book, SectionData section) {
    return section.getTitle();
  }

  @Override
  public void transform(BookData book, SectionData section) {
    // an index page written by hand configures the listing and is then replaced by it
    ContentIndex config = null;
    for (PageData page : section.pages) {
      if (page.content instanceof ContentIndex index) {
        config = index;
        break;
      }
    }

    GroupingBuilder builder = new GroupingBuilder(config);
    for (Iterator<PageData> iterator = section.pages.iterator(); iterator.hasNext(); ) {
      PageData page = iterator.next();
      if (page.content instanceof ContentIndex) {
        // the configuring page is consumed by the listing that replaces it
        iterator.remove();
        continue;
      }
      builder.beforePage(page.name);
      if (config != null && config.isHidden(page.name)) {
        continue;
      }
      if (!this.processPage(book, builder, page)) {
        iterator.remove();
      }
    }
    if (builder.isEmpty()) {
      return;
    }

    String title = config != null && config.title != null ? config.title : getTitle(book, section);
    String subText = config != null && config.subText != null ? config.subText : book.strings.get(section.name + ".subtext");
    List<ContentListing> listings = builder.build(book, title, subText, this.largeTitle, this.centerTitle);

    // insert in reverse so the first listing ends up first
    for (int i = listings.size() - 1; i >= 0; i--) {
      PageData page = new PageData(true);
      page.parent = section;
      page.source = section.source;
      page.name = i == 0 ? "index" : "index" + (i + 1);
      page.content = listings.get(i);
      page.load();
      section.pages.add(0, page);
    }
  }

  /**
   * Collects listing entries while the section is walked.
   *
   * <p>Column breaks and extra headings named by the index page's {@code operations} are applied
   * as their anchor page comes past, so authors can reshape a generated listing without knowing
   * what tag injection produced.
   */
  public static class GroupingBuilder {
    private final List<Entry> entries = new ArrayList<>();
    @Nullable
    private final ContentIndex config;

    GroupingBuilder(@Nullable ContentIndex config) {
      this.config = config;
    }

    /** Applies any operations anchored before the named page */
    void beforePage(String name) {
      if (this.config == null || this.config.operations == null) {
        return;
      }
      for (ContentIndex.Operation operation : this.config.operations) {
        if (!operation.before().equals(name)) {
          continue;
        }
        switch (operation.action()) {
          case ADD_GROUP -> {
            if (operation.data() != null) {
              this.entries.add(new Entry(operation.data(), null, true, false));
            }
          }
          case COLUMN_BREAK -> columnBreak();
        }
      }
    }

    /** Adds a link to a page */
    public void addPage(String title, PageData page) {
      this.entries.add(new Entry(title, page, false, false));
    }

    /** Adds a group heading, optionally linking to a page of its own */
    public void addGroup(String title, @Nullable PageData page) {
      this.entries.add(new Entry(title, page, true, false));
    }

    /** Starts a new column at the current position */
    public void columnBreak() {
      this.entries.add(new Entry("", null, false, true));
    }

    boolean isEmpty() {
      return this.entries.stream().noneMatch(entry -> !entry.columnBreak());
    }

    /**
     * Splits the collected entries into as many listing pages as they need.
     *
     * <p>A generated listing has no idea how long it will be — the encyclopedia's upgrade section
     * runs to seventy-odd entries — so the split happens here rather than being written into the
     * book data, and a listing that overflows spills onto another page instead of being cut off.
     */
    List<ContentListing> build(BookData book, String title, @Nullable String subText,
                               @Nullable Boolean largeTitle, @Nullable Boolean centerTitle) {
      boolean large = largeTitle != null ? largeTitle : book.appearance.largePageTitles;
      int titleHeight = large ? 22 : 16;
      int firstPageRows = Math.max(1, (BookScreen.PAGE_HEIGHT - titleHeight
                                       - (subText != null && !subText.isEmpty() ? SUBTEXT_HEIGHT : 0)) / LINE_HEIGHT);
      int laterPageRows = Math.max(1, (BookScreen.PAGE_HEIGHT - titleHeight) / LINE_HEIGHT);

      // one column reads better; a second is opened only when the entries do not fit in one
      int visible = (int)this.entries.stream().filter(entry -> !entry.columnBreak()).count();
      int columnsPerPage = book.appearance.drawFourColumnIndex || visible > firstPageRows ? 2 : 1;

      List<ContentListing> listings = new ArrayList<>();
      ContentListing current = newListing(title, subText, largeTitle, centerTitle);
      listings.add(current);
      int rows = firstPageRows;
      int column = 1;
      int row = 0;
      for (Entry entry : this.entries) {
        boolean full = row >= rows;
        if (entry.columnBreak() || full) {
          if (column >= columnsPerPage) {
            current = newListing(title, null, largeTitle, centerTitle);
            listings.add(current);
            rows = laterPageRows;
            column = 1;
          } else {
            current.addColumn();
            column++;
          }
          row = 0;
          if (entry.columnBreak()) {
            continue;
          }
        }
        if (entry.group()) {
          current.addGroup(entry.title(), entry.page());
        } else {
          current.addEntry(entry.title(), entry.page());
        }
        row++;
      }
      return listings;
    }

    /** Creates one listing page with the shared styling */
    private static ContentListing newListing(String title, @Nullable String subText,
                                             @Nullable Boolean largeTitle, @Nullable Boolean centerTitle) {
      ContentListing listing = new ContentListing();
      listing.title = title;
      listing.subText = subText;
      listing.largeTitle = largeTitle;
      listing.centerTitle = centerTitle;
      return listing;
    }

    private record Entry(String title, @Nullable PageData page, boolean group, boolean columnBreak) {}
  }
}
