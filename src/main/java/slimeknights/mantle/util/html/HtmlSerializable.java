package slimeknights.mantle.util.html;

/**
 * Anything that can be written into the HTML export of a book.
 *
 * <p>Mantle can dump a whole book to a standalone HTML file, which is how the wiki pages for the
 * Tinkers' books are produced. Every page content class contributes its own markup through
 * {@link slimeknights.mantle.client.book.data.content.PageContent#toHTML}, so the export mirrors
 * the in-game layout instead of re-describing it.
 */
public interface HtmlSerializable {
  /** Serializable that writes nothing, for content with no HTML representation */
  HtmlSerializable EMPTY = (builder, indent) -> {};

  /**
   * Writes this element into the given builder
   * @param builder  Output
   * @param indent   Current indent depth, in levels of two spaces
   */
  void write(StringBuilder builder, int indent);

  /** Writes this element to a fresh string */
  default String toHtml() {
    StringBuilder builder = new StringBuilder();
    write(builder, 0);
    return builder.toString();
  }

  /** Writes {@code indent} levels of indentation */
  static void indent(StringBuilder builder, int indent) {
    builder.append("  ".repeat(Math.max(0, indent)));
  }

  /** Escapes the five characters that may not appear literally in HTML text */
  static String escape(String text) {
    return text.replace("&", "&amp;")
               .replace("<", "&lt;")
               .replace(">", "&gt;")
               .replace("\"", "&quot;")
               .replace("'", "&#39;");
  }

  /** Serializable wrapping a literal string of already-escaped markup */
  record Raw(String html) implements HtmlSerializable {
    @Override
    public void write(StringBuilder builder, int indent) {
      builder.append(html);
    }
  }

  /** Serializable wrapping plain text, escaped on write */
  record Text(String text) implements HtmlSerializable {
    @Override
    public void write(StringBuilder builder, int indent) {
      builder.append(escape(text));
    }
  }
}
