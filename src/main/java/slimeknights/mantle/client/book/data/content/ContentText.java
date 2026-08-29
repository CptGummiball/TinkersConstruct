package slimeknights.mantle.client.book.data.content;

import net.minecraft.resources.ResourceLocation;
import slimeknights.mantle.Mantle;
import slimeknights.mantle.client.book.data.BookData;
import slimeknights.mantle.client.book.data.element.TextData;
import slimeknights.mantle.client.screen.book.BookScreen;
import slimeknights.mantle.client.screen.book.element.BookElement;
import slimeknights.mantle.client.screen.book.element.TextElement;
import slimeknights.mantle.util.html.HtmlElement;
import slimeknights.mantle.util.html.HtmlGroup;
import slimeknights.mantle.util.html.HtmlSerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;

/** A page that is nothing but a title and a block of text */
public class ContentText extends PageContent {
  public static final transient ResourceLocation ID = Mantle.getResource("text");

  @Nullable
  public String title = null;
  public TextData[] text = new TextData[0];

  @Nonnull
  @Override
  public String getTitle() {
    return this.title == null ? "" : this.title;
  }

  @Override
  public void build(BookData book, ArrayList<BookElement> list, boolean rightSide) {
    int y = 0;
    if (this.title != null && !this.title.isEmpty()) {
      this.addTitle(list, this.title);
      y = getTitleHeight();
    }
    list.add(new TextElement(0, y, BookScreen.PAGE_WIDTH, BookScreen.PAGE_HEIGHT - y, this.text));
  }

  @Override
  public HtmlSerializable toHTML(BookData book) {
    HtmlGroup group = HtmlGroup.indent();
    if (this.title != null && !this.title.isEmpty()) {
      group.add(makeTitleHTML());
    }
    group.add(HtmlElement.div().add(TextData.toHtml(this.text, book)));
    return group;
  }
}
