package slimeknights.mantle.client.book.data.content;

import net.minecraft.resources.ResourceLocation;
import slimeknights.mantle.Mantle;
import slimeknights.mantle.client.book.data.BookData;
import slimeknights.mantle.client.book.data.element.ImageData;
import slimeknights.mantle.client.screen.book.BookScreen;
import slimeknights.mantle.client.screen.book.element.BookElement;
import slimeknights.mantle.client.screen.book.element.ImageElement;
import slimeknights.mantle.util.html.HtmlElement;
import slimeknights.mantle.util.html.HtmlGroup;
import slimeknights.mantle.util.html.HtmlSerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;

/** A page holding a single image, centred under the title */
public class ContentImage extends PageContent {
  public static final transient ResourceLocation ID = Mantle.getResource("image");

  @Nullable
  public String title = null;
  public ImageData image = ImageData.MISSING;

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
    if (this.image != null && this.image.isPresent()) {
      int x = Math.max(0, (BookScreen.PAGE_WIDTH - this.image.width) / 2);
      int top = y + Math.max(0, (BookScreen.PAGE_HEIGHT - y - this.image.height) / 2);
      list.add(new ImageElement(x, top, this.image.width, this.image.height, this.image));
    }
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
    return group;
  }
}
