package slimeknights.mantle.client.screen.book.element;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import slimeknights.mantle.client.book.data.element.TextComponentData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Draws text that is already a {@link Component}.
 *
 * <p>Generated pages — material stats, modifier names, fluid effects — build their text with
 * vanilla styling rather than the book's JSON text model, so their styles are carried through
 * untouched instead of being re-derived from colour names.
 */
public class TextComponentElement extends AbstractTextElement {
  /** Runs to draw */
  public List<TextComponentData> text;

  public TextComponentElement(int x, int y, int width, int height, Component text) {
    this(x, y, width, height, new TextComponentData(text));
  }

  public TextComponentElement(int x, int y, int width, int height, TextComponentData... text) {
    super(x, y, width, height);
    this.text = List.of(text);
  }

  public TextComponentElement(int x, int y, int width, int height, Collection<TextComponentData> text) {
    super(x, y, width, height);
    this.text = new ArrayList<>(text);
  }

  @Override
  protected List<Run> buildRuns() {
    List<Run> runs = new ArrayList<>();
    for (TextComponentData data : this.text) {
      if (data == null || data.text == null) {
        continue;
      }
      // a component may carry several styles; flatten it so each keeps its own
      List<Run> parts = new ArrayList<>();
      List<Component> tooltip = data.tooltips == null ? null : List.of(data.tooltips);
      data.text.visit((style, content) -> {
        if (!content.isEmpty()) {
          parts.add(new Run(content, data.scale, colorOf(style), data.dropShadow, style,
                            tooltip, data.action, false, false));
        }
        return java.util.Optional.empty();
      }, Style.EMPTY);
      if (parts.isEmpty()) {
        continue;
      }
      // the paragraph and line break flags belong to the whole entry, not to each styled part
      Run first = parts.get(0);
      parts.set(0, new Run(first.text(), first.scale(), first.color(), first.dropShadow(), first.style(),
                           first.tooltip(), first.action(), data.paragraph, parts.size() == 1 && data.linebreak));
      if (parts.size() > 1) {
        Run last = parts.get(parts.size() - 1);
        parts.set(parts.size() - 1, new Run(last.text(), last.scale(), last.color(), last.dropShadow(), last.style(),
                                            last.tooltip(), last.action(), false, data.linebreak));
      }
      runs.addAll(parts);
    }
    return runs;
  }

  /** Colour of a style, falling back to the book's text colour */
  private int colorOf(Style style) {
    TextColor color = style.getColor();
    return 0xFF000000 | (color != null ? color.getValue() : defaultColor());
  }
}
