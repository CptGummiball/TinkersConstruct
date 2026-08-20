package slimeknights.mantle.client.book.data.content;

import net.minecraft.resources.ResourceLocation;
import slimeknights.mantle.Mantle;
import slimeknights.mantle.client.book.data.BookData;
import slimeknights.mantle.client.book.data.PageData;
import slimeknights.mantle.client.book.data.element.TextData;
import slimeknights.mantle.client.screen.book.BookScreen;
import slimeknights.mantle.client.screen.book.element.BookElement;
import slimeknights.mantle.client.screen.book.element.TextElement;
import slimeknights.mantle.util.html.HtmlElement;
import slimeknights.mantle.util.html.HtmlGroup;
import slimeknights.mantle.util.html.HtmlSerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * A page of links to other pages, laid out in columns.
 *
 * <p>Entries are added in code rather than read from JSON: the section transformers know which
 * pages exist after tag injection, which the book data cannot.
 */
public class ContentListing extends PageContent {
  public static final transient ResourceLocation ID = Mantle.getResource("listing");

  /** Height of one entry line */
  protected static final transient int LINE_HEIGHT = 9;

  @Nullable
  public String title = null;
  @Nullable
  public String subText = null;
  /** Null means "follow the book's appearance" */
  @Nullable
  public Boolean largeTitle = null;
  @Nullable
  public Boolean centerTitle = null;

  protected final transient List<List<Entry>> columns = new ArrayList<>();

  public ContentListing() {
    this.columns.add(new ArrayList<>());
  }

  @Nonnull
  @Override
  public String getTitle() {
    return this.title == null ? "" : this.title;
  }

  public void setTitle(@Nullable String title) {
    this.title = title;
  }

  /** Adds a link to another page */
  public void addEntry(String text, @Nullable PageData link) {
    this.columns.get(this.columns.size() - 1).add(new Entry(text, link, false));
  }

  /** Adds a group heading, optionally linking to a page of its own */
  public void addGroup(String text, @Nullable PageData link) {
    this.columns.get(this.columns.size() - 1).add(new Entry(text, link, true));
  }

  /** Starts a new column */
  public void addColumn() {
    this.columns.add(new ArrayList<>());
  }

  /** Number of columns this listing holds */
  public int columnCount() {
    return this.columns.size();
  }

  /** Number of entries across every column */
  public int size() {
    return this.columns.stream().mapToInt(List::size).sum();
  }

  public boolean isEmpty() {
    return size() == 0;
  }

  @Override
  protected int getTitleHeight() {
    BookData book = getBook();
    boolean large = this.largeTitle != null ? this.largeTitle : book != null && book.appearance.largePageTitles;
    return large ? LARGE_TITLE_HEIGHT : TITLE_HEIGHT;
  }

  @Override
  public void build(BookData book, ArrayList<BookElement> list, boolean rightSide) {
    int y = 0;
    if (this.title != null && !this.title.isEmpty()) {
      boolean large = this.largeTitle != null ? this.largeTitle : book.appearance.largePageTitles;
      this.addTitle(list, this.title, large);
      y = getTitleHeight();
    }
    if (this.subText != null && !this.subText.isEmpty()) {
      y = this.addText(list, this.subText, rightSide, 0, y) + 8;
    }

    int columnCount = Math.max(1, this.columns.size());
    int columnWidth = BookScreen.PAGE_WIDTH / columnCount;
    for (int column = 0; column < this.columns.size(); column++) {
      int x = column * columnWidth;
      int lineY = y;
      for (Entry entry : this.columns.get(column)) {
        TextData data = new TextData(entry.text());
        if (entry.group()) {
          data.bold = true;
          data.underlined = true;
        }
        if (entry.link() != null) {
          data.action = "mantle:go-to-page-rtn " + entry.link().parent.name + "." + entry.link().name;
        }
        list.add(new TextElement(x, lineY, columnWidth, LINE_HEIGHT, data));
        lineY += LINE_HEIGHT;
        if (lineY > BookScreen.PAGE_HEIGHT - LINE_HEIGHT) {
          break;
        }
      }
    }
  }

  @Override
  public HtmlSerializable toHTML(BookData book) {
    HtmlGroup group = HtmlGroup.indent();
    if (this.title != null && !this.title.isEmpty()) {
      group.add(makeTitleHTML());
    }
    if (this.subText != null && !this.subText.isEmpty()) {
      group.add(HtmlElement.p().add(this.subText));
    }
    HtmlElement row = HtmlElement.div().classes("row");
    for (List<Entry> column : this.columns) {
      HtmlElement list = HtmlElement.ul().classes("index-column");
      for (Entry entry : column) {
        HtmlElement item = HtmlElement.li().add(entry.text());
        if (entry.group()) {
          item.classes("underline").style("font-weight", "bold");
        }
        list.add(item);
      }
      row.add(list);
    }
    group.add(row);
    return group;
  }

  /** One line of the listing */
  protected record Entry(String text, @Nullable PageData link, boolean group) {}
}
