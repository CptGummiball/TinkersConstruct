package slimeknights.mantle.client.book.data.content;

import net.minecraft.resources.ResourceLocation;
import slimeknights.mantle.Mantle;
import slimeknights.mantle.client.book.data.BookData;
import slimeknights.mantle.client.screen.book.element.BookElement;

import java.util.ArrayList;

/**
 * A page that draws nothing.
 *
 * <p>Used to force the following page onto the other half of the spread, most often so a chapter
 * opens on the left.
 */
public class ContentBlank extends PageContent {
  public static final transient ResourceLocation ID = Mantle.getResource("blank");

  @Override
  public void build(BookData book, ArrayList<BookElement> list, boolean rightSide) {}
}
