package slimeknights.mantle.client.book.data.element;

import net.minecraft.network.chat.Component;
import slimeknights.mantle.client.book.HTMLUtils;
import slimeknights.mantle.client.book.data.BookData;
import slimeknights.mantle.util.html.HtmlElement;
import slimeknights.mantle.util.html.HtmlGroup;
import slimeknights.mantle.util.html.HtmlSerializable;

import javax.annotation.Nullable;

/**
 * A run of styled text inside a page.
 *
 * <p>Deserialized straight out of the page JSON, so the field names are the JSON keys. Runs flow
 * into each other like HTML inline text: {@link #paragraph} starts a new block with a blank line
 * before it, {@link #linebreak} only wraps to the next line.
 */
public class TextData {
  /** Shared instance for a plain line break between two runs */
  public static final TextData LINEBREAK = new TextData("\n");

  /** The text itself, run through the language file if it names a translation key */
  public String text = "";
  /** Colour name understood by {@link HTMLUtils#getColorCode(String)}, or a {@code #rrggbb} literal */
  @Nullable
  public String color = null;
  public boolean bold = false;
  public boolean italic = false;
  public boolean underlined = false;
  public boolean strikethrough = false;
  public boolean obfuscated = false;
  /** Renders with the usual GUI text shadow */
  public boolean dropshadow = false;
  /** Multiplier on the font size */
  public float scale = 1.0F;
  /** Starts a new paragraph, adding a blank line above this run */
  public boolean paragraph = false;
  /** Wraps to the next line after this run without the paragraph spacing */
  public boolean linebreak = false;
  /** Lines shown when hovering the run */
  @Nullable
  public String[] tooltip = null;
  /** Action fired when the run is clicked, see {@link slimeknights.mantle.client.book.action.StringActionProcessor} */
  @Nullable
  public String action = null;

  public TextData() {}

  public TextData(String text) {
    this.text = text;
  }

  public TextData(String text, String color) {
    this(text);
    this.color = color;
  }

  /** Sets the line break flag, returning this for chaining */
  public TextData linebreak(boolean linebreak) {
    this.linebreak = linebreak;
    return this;
  }

  /** Sets the paragraph flag, returning this for chaining */
  public TextData paragraph(boolean paragraph) {
    this.paragraph = paragraph;
    return this;
  }

  /** True if this run draws nothing */
  public boolean isEmpty() {
    return this.text == null || this.text.isEmpty();
  }

  /** Builds the style string this run contributes to an HTML export */
  private HtmlElement toHtmlElement() {
    HtmlElement span = HtmlElement.span().add(this.text == null ? "" : this.text);
    if (this.color != null) {
      span.color(HTMLUtils.getColorCode(this.color));
    }
    if (this.bold) {
      span.style("font-weight", "bold");
    }
    if (this.italic) {
      span.style("font-style", "italic");
    }
    if (this.underlined && this.strikethrough) {
      span.style("text-decoration", "underline line-through");
    } else if (this.underlined) {
      span.style("text-decoration", "underline");
    } else if (this.strikethrough) {
      span.style("text-decoration", "line-through");
    }
    if (this.scale != 1.0F) {
      span.style("font-size", Math.round(this.scale * 100) + "%");
    }
    if (this.tooltip != null && this.tooltip.length > 0) {
      HtmlGroup tooltip = HtmlGroup.indent();
      for (String line : this.tooltip) {
        tooltip.add(HtmlElement.p().add(line));
      }
      span.minetip(tooltip);
    }
    return span;
  }

  /** Converts an array of runs into HTML, honouring the paragraph and line break flags */
  public static HtmlSerializable toHtml(@Nullable TextData[] text, BookData book) {
    if (text == null || text.length == 0) {
      return HtmlSerializable.EMPTY;
    }
    HtmlGroup group = HtmlGroup.flat();
    HtmlElement paragraph = null;
    for (TextData data : text) {
      if (data == null) {
        continue;
      }
      // a paragraph flag opens a fresh block, matching the blank line the screen draws
      if (paragraph == null || data.paragraph) {
        paragraph = HtmlElement.p();
        group.add(paragraph);
      }
      paragraph.add(data.toHtmlElement());
      if (data.linebreak) {
        paragraph.add(HtmlElement.br());
      }
    }
    return group;
  }

  /** Converts this run to a component with its style applied, for the vanilla text renderer */
  public Component toComponent(BookData book) {
    return HTMLUtils.style(Component.literal(this.text == null ? "" : this.text), this, book);
  }
}
