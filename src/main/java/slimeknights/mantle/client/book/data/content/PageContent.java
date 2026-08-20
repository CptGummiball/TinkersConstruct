package slimeknights.mantle.client.book.data.content;

import net.minecraft.client.gui.Font;
import slimeknights.mantle.client.book.data.BookData;
import slimeknights.mantle.client.book.data.PageData;
import slimeknights.mantle.client.book.data.element.TextData;
import slimeknights.mantle.client.screen.book.BookScreen;
import slimeknights.mantle.client.screen.book.element.BookElement;
import slimeknights.mantle.client.screen.book.element.TextElement;
import slimeknights.mantle.util.html.HtmlElement;
import slimeknights.mantle.util.html.HtmlSerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;

/**
 * The body of one page.
 *
 * <p>Instances are deserialized straight out of the page's JSON, so public fields are JSON keys.
 * {@link #load()} runs once when the book is built and may look at the world; {@link #build} runs
 * every time the page is displayed and turns the data into screen elements.
 */
public abstract class PageContent {
  /** Height a normal page title occupies, including the gap below it */
  public static final int TITLE_HEIGHT = 16;
  /** Height a large page title occupies */
  public static final int LARGE_TITLE_HEIGHT = 22;

  /** The page this content belongs to; set while the book loads */
  public transient PageData parent;

  /** Title shown in indexes and at the top of the page */
  @Nonnull
  public String getTitle() {
    return "";
  }

  /**
   * Called once while the book is built, before any page is displayed.
   *
   * <p>The right place for anything that reads the world or the recipe manager, so the cost is
   * paid on book load rather than on every page turn.
   */
  public void load() {}

  /**
   * Turns this content into screen elements.
   * @param book       Book being displayed
   * @param list       Elements for this page; add to it
   * @param rightSide  True if this page is the right half of the spread, which flips outer margins
   */
  public abstract void build(BookData book, ArrayList<BookElement> list, boolean rightSide);

  /** Book this content belongs to, or null while it is still being assembled */
  @Nullable
  protected BookData getBook() {
    if (this.parent != null && this.parent.parent != null) {
      return this.parent.parent.parent;
    }
    return null;
  }

  /** Font the book draws with, falling back to the vanilla font before the book is bound */
  protected Font getFont() {
    BookData book = getBook();
    if (book != null && book.fontRenderer != null) {
      return book.fontRenderer;
    }
    return net.minecraft.client.Minecraft.getInstance().font;
  }

  /** Vertical space the page title takes, so content can start below it */
  protected int getTitleHeight() {
    BookData book = getBook();
    return book != null && book.appearance.largePageTitles ? LARGE_TITLE_HEIGHT : TITLE_HEIGHT;
  }

  /* Title */

  /** Adds the page title using the book's default styling */
  protected void addTitle(ArrayList<BookElement> list, String title) {
    BookData book = getBook();
    this.addTitle(list, title, book != null && book.appearance.largePageTitles);
  }

  /** Adds the page title, choosing whether it renders at the large size */
  protected void addTitle(ArrayList<BookElement> list, String title, boolean large) {
    this.addTitle(list, title, large, 0x000000);
  }

  /** Adds the page title in the given colour */
  protected void addTitle(ArrayList<BookElement> list, String title, boolean large, int color) {
    if (title == null || title.isEmpty()) {
      return;
    }
    BookData book = getBook();
    TextData data = new TextData(title);
    data.underlined = true;
    data.scale = large ? 1.2F : 1.0F;
    data.color = String.format(java.util.Locale.ROOT, "#%06X", color & 0xFFFFFF);

    int width = getFont().width(title);
    if (large) {
      width = (int)(width * data.scale);
    }
    boolean center = book == null || book.appearance.centerPageTitles;
    int x = center ? Math.max(0, (BookScreen.PAGE_WIDTH - width) / 2) : 0;
    list.add(new TextElement(x, 0, BookScreen.PAGE_WIDTH - x, getTitleHeight(), data));
  }

  /* Text */

  /**
   * Adds a block of plain text, returning the y coordinate it ended at.
   * @param list       Elements for this page
   * @param text       Text to add
   * @param rightSide  True if this page is the right half of the spread
   * @param x          Left edge
   * @param y          Top edge
   * @return  Y coordinate below the text
   */
  protected int addText(ArrayList<BookElement> list, String text, boolean rightSide, int x, int y) {
    if (text == null || text.isEmpty()) {
      return y;
    }
    int width = BookScreen.PAGE_WIDTH - x;
    int height = getFont().wordWrapHeight(text, width);
    list.add(new TextElement(x, y, width, height, new TextData(text)));
    return y + height;
  }

  /* HTML export */

  /** Markup for this page in the HTML export; defaults to nothing so pages can opt in */
  public HtmlSerializable toHTML(BookData book) {
    return HtmlSerializable.EMPTY;
  }

  /** Builds the export's heading element for this page */
  protected HtmlElement makeTitleHTML() {
    return HtmlElement.p().classes("page-title").add(getTitle());
  }
}
