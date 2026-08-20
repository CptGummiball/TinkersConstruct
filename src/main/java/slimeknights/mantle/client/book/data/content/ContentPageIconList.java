package slimeknights.mantle.client.book.data.content;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import slimeknights.mantle.Mantle;
import slimeknights.mantle.client.book.data.BookData;
import slimeknights.mantle.client.book.data.PageData;
import slimeknights.mantle.client.book.data.SectionData;
import slimeknights.mantle.client.book.data.content.ContentPadding.ContentRightPadding;
import slimeknights.mantle.client.screen.book.BookScreen;
import slimeknights.mantle.client.screen.book.element.BookElement;
import slimeknights.mantle.client.screen.book.element.ItemElement;
import slimeknights.mantle.client.screen.book.element.PageIconLinkElement;
import slimeknights.mantle.client.screen.book.element.SizedBookElement;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * A grid of item icons, each linking to a page.
 *
 * <p>This is how the material sections open: one icon per material, sized down until they all fit.
 * The page count has to be known before the pages themselves are built, so
 * {@link #getPagesNeededForItemCount} creates the index pages first and hands them back to be
 * filled.
 */
public class ContentPageIconList extends PageContent {
  public static final transient ResourceLocation ID = Mantle.getResource("page_icon_list");

  protected final transient int width;
  protected final transient int height;

  @Nullable
  public String title;
  @Nullable
  public String subText;
  public float maxScale = 2.5F;

  protected final transient List<PageIconLinkElement> elements = new ArrayList<>();

  public ContentPageIconList() {
    this(20);
  }

  public ContentPageIconList(int size) {
    this(size, size);
  }

  public ContentPageIconList(int width, int height) {
    this.width = width;
    this.height = height;
  }

  @Override
  public String getTitle() {
    return this.title == null ? "" : this.title;
  }

  /** Adds a link; returns false when the page is full and the caller should move to the next one */
  public boolean addLink(SizedBookElement element, Component name, PageData pageData) {
    if (this.elements.size() >= this.getMaxIconCount()) {
      return false;
    }
    this.elements.add(new PageIconLinkElement(0, 0, element, name, pageData));
    return true;
  }

  public int getMaxIconCount() {
    return this.getMaxColumns() * this.getMaxRows();
  }

  public int getMaxRows() {
    int totalHeight = BookScreen.PAGE_HEIGHT;
    if (this.title != null) {
      totalHeight -= getTitleHeight();
    }
    if (this.subText != null) {
      totalHeight -= 16 + getFont().wordWrapHeight(this.subText, BookScreen.PAGE_WIDTH) * 12 / 9;
    }
    return Math.max(1, totalHeight / this.height);
  }

  public int getMaxColumns() {
    return Math.max(1, (BookScreen.PAGE_WIDTH - 30) / this.width);
  }

  @Override
  public void build(BookData book, ArrayList<BookElement> list, boolean rightSide) {
    int yOff = 0;
    if (this.title != null) {
      this.addTitle(list, this.title, false);
      yOff = getTitleHeight();
    }
    if (this.subText != null) {
      yOff = this.addText(list, this.subText, false, 0, yOff) + 16;
    }

    int offset = 15;
    int x = offset;
    int y = yOff;
    int pageW = BookScreen.PAGE_WIDTH - 2 * offset;
    int pageH = BookScreen.PAGE_HEIGHT - yOff;

    // shrink the icons until the whole set fits on one page
    float scale = this.maxScale;
    int scaledWidth = this.width;
    int scaledHeight = this.height;
    boolean fits = false;
    while (!fits && scale > 1F) {
      scale -= 0.25F;
      scaledWidth = (int)(this.width * scale);
      scaledHeight = (int)(this.height * scale);
      int rows = pageW / scaledWidth;
      int cols = pageH / scaledHeight;
      fits = rows * cols >= this.elements.size();
    }

    for (PageIconLinkElement element : this.elements) {
      element.x = x;
      element.y = y;
      element.displayElement.x = x + (int)(scale * (this.width - element.displayElement.width) / 2);
      element.displayElement.y = y + (int)(scale * (this.height - element.displayElement.height) / 2);

      element.width = scaledWidth;
      element.height = scaledHeight;
      if (element.displayElement instanceof ItemElement item) {
        item.scale = scale;
      }

      list.add(element);

      x += scaledWidth;
      if (x > BookScreen.PAGE_WIDTH - offset - scaledWidth) {
        x = offset;
        y += scaledHeight;
        // do not draw over the page
        if (y > BookScreen.PAGE_HEIGHT - scaledHeight) {
          break;
        }
      }
    }
  }

  /**
   * Creates enough index pages to hold the given number of icons, adding them to the section.
   *
   * <p>Returns the content instances so the caller can fill them; the pages are already in place,
   * which is what lets the caller add the linked pages behind them without renumbering.
   */
  public static List<ContentPageIconList> getPagesNeededForItemCount(int count, SectionData data, String title, @Nullable String subText) {
    List<ContentPageIconList> listPages = new ArrayList<>();
    List<PageData> newPages = new ArrayList<>();
    while (count > 0) {
      ContentPageIconList overview = new ContentPageIconList();
      PageData page = new PageData(true);
      page.source = data.source;
      page.parent = data;
      page.content = overview;
      page.load();

      // wait to add to the page list until after we added the padding page
      newPages.add(page);

      overview.title = title;
      overview.subText = subText;

      listPages.add(overview);

      count -= overview.getMaxIconCount();
    }

    // ensure same size for all
    if (listPages.size() > 1) {
      listPages.forEach(page -> page.maxScale = 1F);
    }

    // add a padding page if we have an even number of index pages, so you see both together
    if (listPages.size() % 2 == 0) {
      PageData padding = new PageData(true);
      padding.source = data.source;
      padding.parent = data;
      padding.content = new ContentRightPadding();
      padding.load();
      // hack: add padding to the previous section so section links start at the index
      int sectionIndex = data.parent.sections.indexOf(data);
      if (data.pages.isEmpty() && sectionIndex > 0) {
        data.parent.sections.get(sectionIndex - 1).pages.add(padding);
      } else {
        data.pages.add(padding);
      }
    }

    // padding done, can add new pages
    data.pages.addAll(newPages);

    return listPages;
  }

  /** Pairs a page with the icon that links to it */
  public record PageWithIcon(SizedBookElement icon, PageData page) {}

  /** Fills the given index pages with links, moving to the next page as each fills up */
  public static void addPages(SectionData section, List<ContentPageIconList> listPages, List<PageWithIcon> pages) {
    if (listPages.isEmpty()) {
      return;
    }
    java.util.ListIterator<ContentPageIconList> iterator = listPages.listIterator();
    ContentPageIconList overview = iterator.next();
    List<PageData> newPages = new ArrayList<>(pages.size());
    for (PageWithIcon pair : pages) {
      newPages.add(pair.page());
      while (!overview.addLink(pair.icon(), Component.literal(pair.page().getTitle()), pair.page()) && iterator.hasNext()) {
        overview = iterator.next();
      }
    }
    // insert after the index pages so the links land in front of anything the section already had
    section.pages.addAll(Math.min(listPages.size(), section.pages.size()), newPages);
  }
}
