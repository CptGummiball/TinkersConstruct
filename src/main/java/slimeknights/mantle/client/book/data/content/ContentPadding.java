package slimeknights.mantle.client.book.data.content;

import net.minecraft.resources.ResourceLocation;
import slimeknights.mantle.Mantle;
import slimeknights.mantle.client.book.data.BookData;
import slimeknights.mantle.client.screen.book.element.BookElement;

import java.util.ArrayList;

/**
 * Blank pages inserted purely to control which half of the spread the next page lands on.
 *
 * <p>The padding transformer inserts these after the book is otherwise complete, since inserting
 * or removing any page before that point would change every later page's parity.
 */
public abstract class ContentPadding extends PageContent {
  @Override
  public void build(BookData book, ArrayList<BookElement> list, boolean rightSide) {}

  /** True if this padding is still needed given which side it landed on */
  public abstract boolean isKeep(boolean rightSide);

  /** Occupies a left page, so the page after it opens on the right */
  public static class ContentLeftPadding extends ContentPadding {
    public static final transient ResourceLocation ID = Mantle.getResource("left_padding");

    @Override
    public boolean isKeep(boolean rightSide) {
      return !rightSide;
    }
  }

  /** Occupies a right page, so the page after it opens on the left */
  public static class ContentRightPadding extends ContentPadding {
    public static final transient ResourceLocation ID = Mantle.getResource("right_padding");

    @Override
    public boolean isKeep(boolean rightSide) {
      return rightSide;
    }
  }
}
