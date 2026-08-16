package slimeknights.tconstruct.shared.item;

import lombok.Getter;
import slimeknights.mantle.item.TooltipItem;

/**
 * The six Tinkers' guide books.
 *
 * <p>Fabric port: on Forge this extended Mantle's {@code AbstractBookItem}, whose
 * {@code use()} opened the Mantle book screen. The book GUI is client work that lands with
 * the book module in phase 5; until then the items exist (recipes, loot, creative tab) as
 * plain tooltip items, and phase 5 hooks the client-side open through {@link #getBookType()}.
 */
public class TinkerBookItem extends TooltipItem {
  @Getter
  private final BookType bookType;
  public TinkerBookItem(Properties props, BookType bookType) {
    super(props);
    this.bookType = bookType;
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
