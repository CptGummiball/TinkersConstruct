package slimeknights.mantle.network.packet;

/** Shared limits for the book page packets, so both ends agree on the cap */
final class BookPageLimits {
  private BookPageLimits() {}

  /**
   * Longest page reference accepted from a client.
   *
   * <p>A reference is {@code section.page}; both come from book data, so this is generous. The cap
   * exists so a hostile client cannot write an unbounded string into an item on the server.
   */
  static final int MAX_PAGE_LENGTH = 256;
}
