package slimeknights.mantle.client.book.data.content;

import net.minecraft.resources.ResourceLocation;
import slimeknights.mantle.Mantle;
import slimeknights.mantle.client.book.data.BookData;
import slimeknights.mantle.client.book.data.element.TextData;
import slimeknights.mantle.client.screen.book.BookScreen;
import slimeknights.mantle.client.screen.book.element.BookElement;
import slimeknights.mantle.client.screen.book.element.ImageElement;
import slimeknights.mantle.util.html.HtmlElement;
import slimeknights.mantle.util.html.HtmlGroup;
import slimeknights.mantle.util.html.HtmlSerializable;

import java.util.ArrayList;

/** Text above, image below; the mirror of {@link ContentImageText} */
public class ContentTextImage extends ContentImageText {
  public static final transient ResourceLocation ID = Mantle.getResource("text_image");

  @Override
  public void build(BookData book, ArrayList<BookElement> list, boolean rightSide) {
    int y = 0;
    if (this.title != null && !this.title.isEmpty()) {
      this.addTitle(list, this.title);
      y = getTitleHeight();
    }
    int imageHeight = 0;
    int width = 0;
    if (this.image != null && this.image.isPresent()) {
      width = Math.min(this.image.width, BookScreen.PAGE_WIDTH);
      imageHeight = this.image.width > 0 ? this.image.height * width / this.image.width : this.image.height;
    }
    int textHeight = BookScreen.PAGE_HEIGHT - y - imageHeight - (imageHeight > 0 ? 4 : 0);
    list.add(new slimeknights.mantle.client.screen.book.element.TextElement(0, y, BookScreen.PAGE_WIDTH, textHeight, this.text));
    if (imageHeight > 0) {
      list.add(new ImageElement(Math.max(0, (BookScreen.PAGE_WIDTH - width) / 2), y + textHeight + 4, width, imageHeight, this.image));
    }
  }

  @Override
  public HtmlSerializable toHTML(BookData book) {
    HtmlGroup group = HtmlGroup.indent();
    if (this.title != null && !this.title.isEmpty()) {
      group.add(makeTitleHTML());
    }
    group.add(HtmlElement.div().add(TextData.toHtml(this.text, book)));
    if (this.image != null && this.image.file != null) {
      group.add(HtmlElement.img(this.image.file.toString()).style("width", this.image.width).style("height", this.image.height));
    }
    return group;
  }
}
