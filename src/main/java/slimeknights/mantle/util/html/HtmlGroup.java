package slimeknights.mantle.util.html;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

/**
 * A list of HTML children with no tag of its own.
 *
 * <p>Used where a page contributes several sibling elements at once; {@link #indent()} bumps the
 * children one level so the export stays readable without adding a wrapper div to the output.
 */
public class HtmlGroup implements HtmlSerializable {
  private final List<HtmlSerializable> children = new ArrayList<>();
  private final boolean indent;

  protected HtmlGroup(boolean indent) {
    this.indent = indent;
  }

  /** Creates a group whose children are indented one extra level */
  public static HtmlGroup indent() {
    return new HtmlGroup(true);
  }

  /** Creates a group whose children write at the group's own level */
  public static HtmlGroup flat() {
    return new HtmlGroup(false);
  }

  /** Adds children; accepts serializables, strings, streams and collections of either */
  public HtmlGroup add(@Nullable Object... content) {
    HtmlElement.addAll(this.children, content);
    return this;
  }

  /** Adds every element of the stream */
  public HtmlGroup add(Stream<? extends HtmlSerializable> content) {
    content.forEach(this.children::add);
    return this;
  }

  /** Adds every element of the collection */
  public HtmlGroup add(Collection<? extends HtmlSerializable> content) {
    this.children.addAll(content);
    return this;
  }

  /** Adds a string of plain text */
  public HtmlGroup add(String text) {
    this.children.add(new HtmlSerializable.Text(text));
    return this;
  }

  /** Adds a single child */
  public HtmlGroup add(HtmlSerializable child) {
    this.children.add(child);
    return this;
  }

  public boolean isEmpty() {
    return this.children.isEmpty();
  }

  @Override
  public void write(StringBuilder builder, int indent) {
    int childIndent = this.indent ? indent + 1 : indent;
    for (HtmlSerializable child : this.children) {
      HtmlSerializable.indent(builder, childIndent);
      child.write(builder, childIndent);
      builder.append('\n');
    }
  }
}
