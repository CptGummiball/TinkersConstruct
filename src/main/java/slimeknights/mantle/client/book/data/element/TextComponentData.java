package slimeknights.mantle.client.book.data.element;

import net.minecraft.network.chat.Component;
import slimeknights.mantle.client.book.HTMLUtils;
import slimeknights.mantle.client.book.data.BookData;
import slimeknights.mantle.util.html.HtmlElement;
import slimeknights.mantle.util.html.HtmlGroup;
import slimeknights.mantle.util.html.HtmlSerializable;

import javax.annotation.Nullable;
import java.util.List;

/**
 * The {@link TextData} equivalent for text that is already a {@link Component}.
 *
 * <p>Pages built from game data — material stats, modifier names, fluid effects — have their text
 * assembled as components with vanilla styling, so they skip the JSON text model entirely and only
 * carry the layout flags the screen needs.
 */
public class TextComponentData {
  /** Shared instance for a plain line break between two runs */
  public static final TextComponentData LINEBREAK = new TextComponentData("\n");

  /** The component to draw */
  public Component text;
  /** Lines shown when hovering the run */
  @Nullable
  public Component[] tooltips = null;
  /** Action fired when the run is clicked */
  @Nullable
  public String action = null;
  public boolean dropShadow = false;
  public float scale = 1.0F;
  public boolean paragraph = false;
  public boolean linebreak = false;
  /** True if the component brings its own colour, so the page colour must not override it */
  public boolean isParagraph = false;

  public TextComponentData(Component text) {
    this.text = text;
  }

  public TextComponentData(String text) {
    this(Component.literal(text));
  }

  /** Sets the line break flag, returning this for chaining */
  public TextComponentData linebreak(boolean linebreak) {
    this.linebreak = linebreak;
    return this;
  }

  /** Sets the paragraph flag, returning this for chaining */
  public TextComponentData paragraph(boolean paragraph) {
    this.paragraph = paragraph;
    return this;
  }

  /** True if this run draws nothing */
  public boolean isEmpty() {
    return this.text == null || this.text.getString().isEmpty();
  }

  /** Converts a list of runs into HTML, honouring the paragraph and line break flags */
  public static HtmlSerializable toHTML(@Nullable List<TextComponentData> text, BookData book) {
    if (text == null || text.isEmpty()) {
      return HtmlSerializable.EMPTY;
    }
    HtmlGroup group = HtmlGroup.flat();
    HtmlElement paragraph = null;
    for (TextComponentData data : text) {
      if (data == null || data.text == null) {
        continue;
      }
      if (paragraph == null || data.paragraph) {
        paragraph = HtmlElement.p();
        group.add(paragraph);
      }
      HtmlElement span = HtmlElement.span().add(HTMLUtils.toHtml(data.text));
      if (data.scale != 1.0F) {
        span.style("font-size", Math.round(data.scale * 100) + "%");
      }
      if (data.tooltips != null && data.tooltips.length > 0) {
        HtmlGroup tooltip = HtmlGroup.indent();
        for (Component line : data.tooltips) {
          tooltip.add(HtmlElement.p().add(HTMLUtils.toHtml(line)));
        }
        span.minetip(tooltip);
      }
      paragraph.add(span);
      if (data.linebreak) {
        paragraph.add(HtmlElement.br());
      }
    }
    return group;
  }
}
