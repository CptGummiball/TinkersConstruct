package slimeknights.tconstruct.shared.item;

import lombok.Getter;
import net.minecraft.world.item.ItemStack;
import slimeknights.mantle.client.book.BookScreenOpener;
import slimeknights.mantle.item.AbstractBookItem;
import slimeknights.tconstruct.library.client.book.TinkerBook;

/**
 * The six Tinkers' guide books.
 *
 * <p>The book itself lives on the client, so the lookup is deferred into a nested class: this item
 * is registered on a dedicated server too, and touching {@link TinkerBook} there would drag the
 * whole book screen in with it.
 */
public class TinkerBookItem extends AbstractBookItem {
  @Getter
  private final BookType bookType;

  public TinkerBookItem(Properties props, BookType bookType) {
    super(props);
    this.bookType = bookType;
  }

  @Override
  public BookScreenOpener getBook(ItemStack stack) {
    return ClientOnly.getBook(this.bookType);
  }

  /** Keeps the client-only book classes off the resolution path on a server */
  private static final class ClientOnly {
    private ClientOnly() {}

    static BookScreenOpener getBook(BookType type) {
      return TinkerBook.getBook(type);
    }
  }

  /** Simple enum to allow selecting the book on the client */
  public enum BookType {
    MATERIALS_AND_YOU,
    PUNY_SMELTING,
    MIGHTY_SMELTING,
    TINKERS_GADGETRY,
    FANTASTIC_FOUNDRY,
    ENCYCLOPEDIA
  }
}
