package slimeknights.mantle.client.screen.book.element;

import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import slimeknights.mantle.client.book.HTMLUtils;
import slimeknights.mantle.client.book.data.element.TextData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/** Draws a run of {@link TextData}, the text form book JSON uses */
public class TextElement extends AbstractTextElement {
  /** Runs to draw; page code mutates this in place, so it is read fresh every frame */
  public TextData[] text;

  public TextElement(int x, int y, int width, int height, String text) {
    this(x, y, width, height, new TextData(text));
  }

  public TextElement(int x, int y, int width, int height, TextData... text) {
    super(x, y, width, height);
    this.text = text;
  }

  public TextElement(int x, int y, int width, int height, Collection<TextData> text) {
    this(x, y, width, height, text.toArray(new TextData[0]));
  }

  @Override
  protected List<Run> buildRuns() {
    List<Run> runs = new ArrayList<>(this.text.length);
    for (TextData data : this.text) {
      if (data == null || data.text == null || data.text.isEmpty()) {
        continue;
      }
      Style style = Style.EMPTY
        .withBold(data.bold)
        .withItalic(data.italic)
        .withUnderlined(data.underlined)
        .withStrikethrough(data.strikethrough)
        .withObfuscated(data.obfuscated);
      int color = data.color != null && !data.color.isEmpty()
                  ? 0xFF000000 | HTMLUtils.getColorCode(data.color)
                  : 0xFF000000 | defaultColor();
      List<Component> tooltip = null;
      if (data.tooltip != null && data.tooltip.length > 0) {
        tooltip = new ArrayList<>(data.tooltip.length);
        for (String line : data.tooltip) {
          tooltip.add(Component.literal(translate(line)));
        }
      }
      runs.add(new Run(translate(data.text), data.scale, color, data.dropshadow, style,
                       tooltip, data.action, data.paragraph, data.linebreak));
    }
    return runs;
  }

  /**
   * Runs text through the language file when it looks like a translation key.
   *
   * <p>Most book text is written out in the book's own language files, but generated pages hand in
   * translation keys, so both have to work.
   */
  private static String translate(String text) {
    if (text.indexOf(' ') < 0 && text.indexOf('.') > 0 && I18n.exists(text)) {
      return I18n.get(text);
    }
    return text;
  }
}
