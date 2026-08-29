package slimeknights.mantle.client.screen.book.element;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import slimeknights.mantle.client.book.action.StringActionProcessor;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Shared text flow for the two text elements.
 *
 * <p>Book text is a sequence of independently styled runs that share one wrapped paragraph, which
 * neither {@code Font#split} nor a single component can express: each run may carry its own colour,
 * scale, tooltip and click action. Words are therefore laid out one at a time and their rectangles
 * kept, which is also what makes a run hoverable.
 */
public abstract class AbstractTextElement extends SizedBookElement {
  /** Height of one unscaled line */
  protected static final int LINE_HEIGHT = 9;

  /** Laid-out word, in element-local coordinates */
  protected record Placed(String text, int x, int y, int width, int height, float scale, int color,
                          boolean dropShadow, Style style, @Nullable List<Component> tooltip, @Nullable String action) {}

  /** One styled run before layout */
  protected record Run(String text, float scale, int color, boolean dropShadow, Style style,
                       @Nullable List<Component> tooltip, @Nullable String action,
                       boolean paragraph, boolean linebreak) {}

  private List<Placed> placed = List.of();
  private int contentHeight = 0;

  public AbstractTextElement(int x, int y, int width, int height) {
    super(x, y, width, height);
  }

  /** Builds the runs to lay out; called every frame so mutable text stays live */
  protected abstract List<Run> buildRuns();

  /** Runs the layout, filling {@link #placed} */
  protected void layout(Font font) {
    List<Placed> placed = new ArrayList<>();
    int lineY = 0;
    int cursor = 0;
    int lineHeight = LINE_HEIGHT;
    boolean lineStart = true;

    for (Run run : buildRuns()) {
      if (run.text() == null) {
        continue;
      }
      if (run.paragraph() && !lineStart) {
        lineY += lineHeight + LINE_HEIGHT / 2;
        cursor = 0;
        lineHeight = LINE_HEIGHT;
        lineStart = true;
      }
      int runHeight = Math.max(LINE_HEIGHT, Math.round(LINE_HEIGHT * run.scale()));
      for (String word : splitWords(run.text())) {
        if (word.equals("\n")) {
          lineY += lineHeight;
          cursor = 0;
          lineHeight = LINE_HEIGHT;
          lineStart = true;
          continue;
        }
        int width = Math.round(font.width(word) * run.scale());
        // a leading space on a fresh line is dropped, matching how paragraphs read
        if (lineStart && word.isBlank()) {
          continue;
        }
        if (cursor + width > this.width && !lineStart) {
          lineY += lineHeight;
          cursor = 0;
          lineHeight = LINE_HEIGHT;
          lineStart = true;
          if (word.isBlank()) {
            continue;
          }
        }
        if (lineY + runHeight > this.height) {
          this.placed = placed;
          this.contentHeight = this.height;
          return;
        }
        if (!word.isBlank()) {
          placed.add(new Placed(word, cursor, lineY, width, runHeight, run.scale(), run.color(),
                                run.dropShadow(), run.style(), run.tooltip(), run.action()));
        }
        cursor += width;
        lineHeight = Math.max(lineHeight, runHeight);
        lineStart = false;
      }
      if (run.linebreak()) {
        lineY += lineHeight;
        cursor = 0;
        lineHeight = LINE_HEIGHT;
        lineStart = true;
      }
    }
    this.placed = placed;
    this.contentHeight = lineStart ? lineY : lineY + lineHeight;
  }

  /**
   * Height the text actually occupies once wrapped.
   *
   * <p>Page code stacks blocks of text and needs to know where one ends. Guessing from the number
   * of entries is what made the material pages draw their stat blocks on top of each other, since
   * a long stat line takes two rows.
   */
  public int measureHeight(Font font) {
    layout(font);
    return this.contentHeight;
  }

  /** Splits text into words, keeping the spaces attached and newlines as their own token */
  private static List<String> splitWords(String text) {
    List<String> words = new ArrayList<>();
    StringBuilder current = new StringBuilder();
    for (int i = 0; i < text.length(); i++) {
      char c = text.charAt(i);
      if (c == '\n') {
        if (!current.isEmpty()) {
          words.add(current.toString());
          current.setLength(0);
        }
        words.add("\n");
      } else if (c == ' ') {
        current.append(c);
        words.add(current.toString());
        current.setLength(0);
      } else {
        current.append(c);
      }
    }
    if (!current.isEmpty()) {
      words.add(current.toString());
    }
    return words;
  }

  @Override
  public void draw(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks, Font fontRenderer) {
    layout(fontRenderer);
    for (Placed word : this.placed) {
      Style style = word.style();
      boolean hovered = word.action() != null && isWordHovered(word, mouseX, mouseY);
      if (hovered) {
        style = style.withUnderlined(true);
      }
      Component component = Component.literal(word.text()).withStyle(style);
      if (word.scale() == 1F) {
        graphics.drawString(fontRenderer, component, this.x + word.x(), this.y + word.y(), word.color(), word.dropShadow());
      } else {
        graphics.pose().pushPose();
        graphics.pose().translate(this.x + word.x(), this.y + word.y(), 0);
        graphics.pose().scale(word.scale(), word.scale(), 1F);
        graphics.drawString(fontRenderer, component, 0, 0, word.color(), word.dropShadow());
        graphics.pose().popPose();
      }
    }
  }

  @Override
  public void drawOverlay(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks, Font fontRenderer) {
    for (Placed word : this.placed) {
      if (word.tooltip() != null && !word.tooltip().isEmpty() && isWordHovered(word, mouseX, mouseY)) {
        this.drawTooltip(graphics, word.tooltip(), mouseX, mouseY, fontRenderer);
        return;
      }
    }
  }

  @Override
  public void mouseClicked(double mouseX, double mouseY, int mouseButton) {
    for (Placed word : this.placed) {
      if (word.action() != null && isWordHovered(word, mouseX, mouseY)) {
        StringActionProcessor.process(word.action(), this.parent);
        return;
      }
    }
  }

  /** True if the pointer is over the given laid-out word */
  private boolean isWordHovered(Placed word, double mouseX, double mouseY) {
    int left = this.x + word.x();
    int top = this.y + word.y();
    return mouseX >= left && mouseX < left + word.width() && mouseY >= top && mouseY < top + word.height();
  }

  /** Default text colour for this element's book */
  protected int defaultColor() {
    return this.parent == null ? 0x000000 : this.parent.book.appearance.textColor;
  }
}
