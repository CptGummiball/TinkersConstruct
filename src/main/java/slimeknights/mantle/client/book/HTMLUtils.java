package slimeknights.mantle.client.book;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import slimeknights.mantle.client.ResourceColorManager;
import slimeknights.mantle.client.book.data.BookData;
import slimeknights.mantle.client.book.data.element.TextData;
import slimeknights.mantle.util.html.HtmlElement;
import slimeknights.mantle.util.html.HtmlGroup;
import slimeknights.mantle.util.html.HtmlSerializable;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Colour names and formatting shared by the book screen and the HTML export.
 *
 * <p>Book JSON names colours the way a person would — {@code "dark red"}, {@code "gold"} — rather
 * than with a format code, so the same table has to be readable from both the renderer and the
 * exporter.
 */
public final class HTMLUtils {
  private HTMLUtils() {}

  /** Colour name to packed RGB. Vanilla's sixteen, spelled as the book data spells them */
  private static final Map<String,Integer> COLORS = new HashMap<>();
  static {
    for (ChatFormatting formatting : ChatFormatting.values()) {
      Integer color = formatting.getColor();
      if (color != null) {
        // "dark_red" and "dark red" both appear in the wild
        String name = formatting.getName().toLowerCase(Locale.ROOT);
        COLORS.put(name, color);
        COLORS.put(name.replace('_', ' '), color);
      }
    }
    COLORS.put("grey", ChatFormatting.GRAY.getColor());
    COLORS.put("dark grey", ChatFormatting.DARK_GRAY.getColor());
    COLORS.put("purple", ChatFormatting.LIGHT_PURPLE.getColor());
    COLORS.put("orange", 0xFF9500);
    COLORS.put("brown", 0x8B4513);
  }

  /**
   * Resolves a colour name, {@code #rrggbb} literal or {@code 0x} literal to packed RGB.
   *
   * <p>Unknown names fall through to the resource colour manager, which is how translation-driven
   * colours (material names above all) reach the book.
   */
  public static int getColorCode(@Nullable String color) {
    if (color == null || color.isEmpty()) {
      return 0x000000;
    }
    Integer known = COLORS.get(color.toLowerCase(Locale.ROOT));
    if (known != null) {
      return known;
    }
    String trimmed = color.startsWith("#") ? color.substring(1)
                   : color.toLowerCase(Locale.ROOT).startsWith("0x") ? color.substring(2)
                   : null;
    if (trimmed != null) {
      try {
        return (int)Long.parseLong(trimmed, 16);
      } catch (NumberFormatException e) {
        return 0x000000;
      }
    }
    return ResourceColorManager.getColor(color);
  }

  /** Applies a text run's style to a component */
  public static MutableComponent style(MutableComponent component, TextData data, @Nullable BookData book) {
    Style style = Style.EMPTY
      .withBold(data.bold)
      .withItalic(data.italic)
      .withUnderlined(data.underlined)
      .withStrikethrough(data.strikethrough)
      .withObfuscated(data.obfuscated);
    if (data.color != null && !data.color.isEmpty()) {
      style = style.withColor(TextColor.fromRgb(getColorCode(data.color)));
    }
    return component.withStyle(style);
  }

  /** Renders a component into HTML, carrying its style across as inline CSS */
  public static HtmlSerializable toHtml(@Nullable Component component) {
    if (component == null) {
      return HtmlSerializable.EMPTY;
    }
    HtmlGroup group = HtmlGroup.flat();
    component.visit((style, text) -> {
      group.add(styled(text, style));
      return java.util.Optional.empty();
    }, Style.EMPTY);
    return group;
  }

  /** Builds a span for one styled run */
  private static HtmlElement styled(String text, Style style) {
    HtmlElement span = HtmlElement.span().add(text);
    TextColor color = style.getColor();
    if (color != null) {
      span.color(color.getValue());
    }
    if (style.isBold()) {
      span.style("font-weight", "bold");
    }
    if (style.isItalic()) {
      span.style("font-style", "italic");
    }
    if (style.isUnderlined() && style.isStrikethrough()) {
      span.style("text-decoration", "underline line-through");
    } else if (style.isUnderlined()) {
      span.style("text-decoration", "underline");
    } else if (style.isStrikethrough()) {
      span.style("text-decoration", "line-through");
    }
    return span;
  }

  /**
   * Parses a plain string containing vanilla section-sign codes into HTML.
   *
   * <p>Modifier effect lines and tool property lines are hand-written strings in the book data, and
   * several of them colour themselves with {@code §} codes.
   */
  public static HtmlSerializable parse(@Nullable String text) {
    if (text == null || text.isEmpty()) {
      return HtmlSerializable.EMPTY;
    }
    if (text.indexOf(ChatFormatting.PREFIX_CODE) < 0) {
      return new HtmlSerializable.Text(text);
    }
    HtmlGroup group = HtmlGroup.flat();
    Style style = Style.EMPTY;
    StringBuilder run = new StringBuilder();
    for (int i = 0; i < text.length(); i++) {
      char c = text.charAt(i);
      if (c == ChatFormatting.PREFIX_CODE && i + 1 < text.length()) {
        if (!run.isEmpty()) {
          group.add(styled(run.toString(), style));
          run.setLength(0);
        }
        ChatFormatting formatting = ChatFormatting.getByCode(text.charAt(++i));
        if (formatting == ChatFormatting.RESET || formatting == null) {
          style = Style.EMPTY;
        } else {
          style = style.applyFormat(formatting);
        }
      } else {
        run.append(c);
      }
    }
    if (!run.isEmpty()) {
      group.add(styled(run.toString(), style));
    }
    return group;
  }
}
