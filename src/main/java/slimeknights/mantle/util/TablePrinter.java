package slimeknights.mantle.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

/**
 * Builds an ASCII table with one row per added value, mirroring Forge's
 * {@code net.minecraftforge.common.util.TablePrinter} which the report commands print through.
 *
 * <pre>
 * +----+----------+
 * |ID  |Priority  |
 * +----+----------+
 * |a:b |100       |
 * +----+----------+
 * </pre>
 *
 * @param <T> row type
 */
public class TablePrinter<T> {
  private final List<String> headers = new ArrayList<>();
  private final List<Function<T,String>> extractors = new ArrayList<>();
  private final List<T> rows = new ArrayList<>();

  /** Adds a column with the given header, valued per row by the extractor */
  public TablePrinter<T> header(String header, Function<T,String> extractor) {
    headers.add(header);
    extractors.add(extractor);
    return this;
  }

  /** Adds a single row */
  public TablePrinter<T> add(T row) {
    rows.add(row);
    return this;
  }

  /** Adds many rows */
  public TablePrinter<T> add(Collection<? extends T> rows) {
    this.rows.addAll(rows);
    return this;
  }

  /** Removes all rows, keeping the columns */
  public void clearRows() {
    rows.clear();
  }

  /** Writes the table into the given builder */
  public void build(StringBuilder builder) {
    int columns = headers.size();
    // extract all cells up front to size the columns
    List<String[]> cells = new ArrayList<>(rows.size());
    int[] widths = new int[columns];
    for (int c = 0; c < columns; c++) {
      widths[c] = headers.get(c).length();
    }
    for (T row : rows) {
      String[] line = new String[columns];
      for (int c = 0; c < columns; c++) {
        String value = extractors.get(c).apply(row);
        line[c] = value == null ? "" : value;
        widths[c] = Math.max(widths[c], line[c].length());
      }
      cells.add(line);
    }

    StringBuilder separator = new StringBuilder("+");
    for (int width : widths) {
      separator.append("-".repeat(width + 1)).append('+');
    }
    separator.append('\n');

    builder.append(separator);
    appendRow(builder, headers.toArray(new String[0]), widths);
    builder.append(separator);
    for (String[] line : cells) {
      appendRow(builder, line, widths);
    }
    builder.append(separator);
  }

  private static void appendRow(StringBuilder builder, String[] line, int[] widths) {
    builder.append('|');
    for (int c = 0; c < widths.length; c++) {
      builder.append(line[c]).append(" ".repeat(widths[c] - line[c].length() + 1)).append('|');
    }
    builder.append('\n');
  }
}
