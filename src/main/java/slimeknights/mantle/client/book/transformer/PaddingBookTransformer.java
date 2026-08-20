package slimeknights.mantle.client.book.transformer;

import slimeknights.mantle.client.book.data.BookData;
import slimeknights.mantle.client.book.data.PageData;
import slimeknights.mantle.client.book.data.SectionData;
import slimeknights.mantle.client.book.data.content.ContentPadding;

import java.util.Iterator;

/**
 * Drops padding pages that landed on the side they were meant to fill.
 *
 * <p>A padding page exists to push the page after it onto the other half of the spread. Whether it
 * is needed depends on how many pages precede it, which is only known once every other transformer
 * has run — hence this pass going last.
 */
public class PaddingBookTransformer extends BookTransformer {
  public static final PaddingBookTransformer INSTANCE = new PaddingBookTransformer();

  private PaddingBookTransformer() {}

  @Override
  public void transform(BookData book) {
    int pageCount = 0;
    for (SectionData section : book.sections) {
      for (Iterator<PageData> iterator = section.pages.iterator(); iterator.hasNext(); ) {
        PageData page = iterator.next();
        if (page.content instanceof ContentPadding padding) {
          if (padding.isKeep(pageCount % 2 == 1)) {
            pageCount++;
          } else {
            iterator.remove();
          }
        } else {
          pageCount++;
        }
      }
    }
  }
}
