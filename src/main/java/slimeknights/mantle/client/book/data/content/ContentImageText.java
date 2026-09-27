package slimeknights.mantle.client.book.data.content;

import net.minecraft.resources.ResourceLocation;
import slimeknights.mantle.Mantle;
import slimeknights.mantle.client.book.data.BookData;
import slimeknights.mantle.client.book.data.element.ImageData;
import slimeknights.mantle.client.book.data.element.TextData;
import slimeknights.mantle.client.screen.book.BookScreen;
import slimeknights.mantle.client.screen.book.element.BookElement;
import slimeknights.mantle.client.screen.book.element.ImageElement;
import slimeknights.mantle.client.screen.book.element.TextElement;
import slimeknights.mantle.util.html.HtmlElement;
import slimeknights.mantle.util.html.HtmlGroup;
import slimeknights.mantle.util.html.HtmlSerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;

/** Image above, text below. The most common illustrated page in the Tinkers' books */
public class ContentImageText extends PageContent {
  public static final transient ResourceLocation ID = Mantle.getResource("image_text");

  @Nullable
  public String title = null;
  public ImageData image = ImageData.MISSING;
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
    int imageHeight = 0;
    if (this.image != null && this.image.isPresent()) {
      int width = Math.min(this.image.width, BookScreen.PAGE_WIDTH);
      // keep the aspect ratio if the declared width does not fit the page
      int height = this.image.width > 0 ? this.image.height * width / this.image.width : this.image.height;
      list.add(new ImageElement(Math.max(0, (BookScreen.PAGE_WIDTH - width) / 2), y, width, height, this.image));
      imageHeight = height + 4;
    }
    list.add(new TextElement(0, y + imageHeight, BookScreen.PAGE_WIDTH, BookScreen.PAGE_HEIGHT - y - imageHeight, this.text));
  }

  @Override
  public HtmlSerializable toHTML(BookData book) {
    HtmlGroup group = HtmlGroup.indent();
    if (this.title != null && !this.title.isEmpty()) {
      group.add(makeTitleHTML());
    }
    if (this.image != null && this.image.file != null) {
      group.add(HtmlElement.img(this.image.file.toString()).style("width", this.image.width).style("height", this.image.height));
    }
    group.add(HtmlElement.div().add(TextData.toHtml(this.text, book)));
    return group;
  }
}
