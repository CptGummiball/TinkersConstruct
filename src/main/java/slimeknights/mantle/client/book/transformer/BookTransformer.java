package slimeknights.mantle.client.book.transformer;

import slimeknights.mantle.client.book.data.BookData;

/**
 * A pass over a book after its files have loaded.
 *
 * <p>Everything a book cannot know from its own files is added here: which tools exist, which
 * modifiers a tag holds, which materials the datapacks defined. Transformers run in the order they
 * were added.
 */
public abstract class BookTransformer {
  /** Runs this pass over the book */
  public abstract void transform(BookData book);

  /** Adds the section index at the front of the book */
  public static BookTransformer indexTranformer() {
    return IndexTransformer.INSTANCE;
  }

  /** Removes padding pages that turned out to be unnecessary; must run last */
  public static BookTransformer paddingTransformer() {
    return PaddingBookTransformer.INSTANCE;
  }
}
