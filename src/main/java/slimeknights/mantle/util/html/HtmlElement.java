package slimeknights.mantle.util.html;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Stream;

/**
 * A single HTML tag with attributes, inline styles and children.
 *
 * <p>Only the handful of tags the book export needs are exposed as factories; anything else can be
 * built through {@link #tag(String)}. Every setter returns {@code this} so a page's markup reads
 * close to the nesting of the page itself.
 */
public class HtmlElement implements HtmlSerializable {
  /** Tags that never have children and are written as a single token */
  private static final List<String> VOID_TAGS = List.of("br", "hr", "img", "input");

  private final String tag;
  private final List<String> classes = new ArrayList<>();
  private final Map<String,String> styles = new LinkedHashMap<>();
  private final Map<String,String> attributes = new LinkedHashMap<>();
  private final List<HtmlSerializable> children = new ArrayList<>();

  protected HtmlElement(String tag) {
    this.tag = tag;
  }

  /* Factories */

  public static HtmlElement tag(String tag) {
    return new HtmlElement(tag);
  }

  public static HtmlElement div() {
    return new HtmlElement("div");
  }

  public static HtmlElement p() {
    return new HtmlElement("p");
  }

  public static HtmlElement span() {
    return new HtmlElement("span");
  }

  public static HtmlElement ul() {
    return new HtmlElement("ul");
  }

  public static HtmlElement li() {
    return new HtmlElement("li");
  }

  public static HtmlElement br() {
    return new HtmlElement("br");
  }

  public static HtmlElement img(String src) {
    return new HtmlElement("img").attribute("src", src);
  }

  /* Builders */

  /** Adds CSS classes to this element */
  public HtmlElement classes(String... classes) {
    for (String name : classes) {
      if (name != null && !name.isEmpty()) {
        this.classes.add(name);
      }
    }
    return this;
  }

  /** Adds an inline style; integers are treated as pixel lengths */
  public HtmlElement style(String key, Object value) {
    this.styles.put(key, value instanceof Number number ? number.intValue() + "px" : String.valueOf(value));
    return this;
  }

  /** Sets the text color from a packed RGB integer */
  public HtmlElement color(int rgb) {
    return style("color", String.format(Locale.ROOT, "#%06X", rgb & 0xFFFFFF));
  }

  /** Sets an arbitrary attribute */
  public HtmlElement attribute(String key, String value) {
    this.attributes.put(key, value);
    return this;
  }

  /**
   * Attaches a Minecraft-style tooltip to this element.
   *
   * <p>The export's stylesheet shows the {@code minetip} child on hover, which is how a stat line
   * carries the same tooltip it has in the book.
   */
  public HtmlElement minetip(HtmlSerializable tooltip) {
    this.classes.add("has-minetip");
    this.children.add(HtmlElement.span().classes("minetip").add(tooltip));
    return this;
  }

  /* Children */

  /** Adds children; accepts serializables, strings, streams and collections of either */
  public HtmlElement add(@Nullable Object... content) {
    addAll(this.children, content);
    return this;
  }

  /** Adds every element of the stream */
  public HtmlElement add(Stream<? extends HtmlSerializable> content) {
    content.forEach(this.children::add);
    return this;
  }

  /** Adds every element of the collection */
  public HtmlElement add(Collection<? extends HtmlSerializable> content) {
    this.children.addAll(content);
    return this;
  }

  /** Adds a string of plain text */
  public HtmlElement add(String text) {
    this.children.add(new HtmlSerializable.Text(text));
    return this;
  }

  /** Adds a single child */
  public HtmlElement add(HtmlSerializable child) {
    this.children.add(child);
    return this;
  }

  /** Shared child collector, used by both this and {@link HtmlGroup} */
  @SuppressWarnings("unchecked")
  static void addAll(List<HtmlSerializable> target, @Nullable Object[] content) {
    if (content == null) {
      return;
    }
    for (Object object : content) {
      if (object == null) {
        continue;
      }
      if (object instanceof HtmlSerializable serializable) {
        target.add(serializable);
      } else if (object instanceof Stream<?> stream) {
        stream.forEach(child -> addAll(target, new Object[]{child}));
      } else if (object instanceof Collection<?> collection) {
        addAll(target, collection.toArray());
      } else if (object instanceof Object[] array) {
        addAll(target, array);
      } else {
        target.add(new HtmlSerializable.Text(String.valueOf(object)));
      }
    }
  }

  @Override
  public void write(StringBuilder builder, int indent) {
    builder.append('<').append(this.tag);
    if (!this.classes.isEmpty()) {
      builder.append(" class=\"").append(HtmlSerializable.escape(String.join(" ", this.classes))).append('"');
    }
    if (!this.styles.isEmpty()) {
      builder.append(" style=\"");
      this.styles.forEach((key, value) -> builder.append(key).append(':').append(HtmlSerializable.escape(value)).append(';'));
      builder.append('"');
    }
    this.attributes.forEach((key, value) -> builder.append(' ').append(key).append("=\"").append(HtmlSerializable.escape(value)).append('"'));
    if (VOID_TAGS.contains(this.tag)) {
      builder.append("/>");
      return;
    }
    builder.append('>');
    // a single text child stays on one line, anything else nests
    if (this.children.size() == 1 && this.children.get(0) instanceof HtmlSerializable.Text) {
      this.children.get(0).write(builder, indent);
    } else if (!this.children.isEmpty()) {
      builder.append('\n');
      for (HtmlSerializable child : this.children) {
        HtmlSerializable.indent(builder, indent + 1);
        child.write(builder, indent + 1);
        builder.append('\n');
      }
      HtmlSerializable.indent(builder, indent);
    }
    builder.append("</").append(this.tag).append('>');
  }
}
