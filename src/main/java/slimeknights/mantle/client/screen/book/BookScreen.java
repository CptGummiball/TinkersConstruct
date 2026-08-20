package slimeknights.mantle.client.screen.book;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import org.lwjgl.glfw.GLFW;
import slimeknights.mantle.Mantle;
import slimeknights.mantle.client.book.data.BookData;
import slimeknights.mantle.client.book.data.PageData;
import slimeknights.mantle.client.book.data.SectionData;
import slimeknights.mantle.client.screen.book.ArrowButton.ArrowType;
import slimeknights.mantle.client.screen.book.element.BookElement;
import slimeknights.mantle.network.MantleNetwork;
import slimeknights.mantle.network.packet.UpdateHeldPagePacket;
import slimeknights.mantle.network.packet.UpdateLecternPagePacket;
import slimeknights.mantle.network.packet.UpdateSavedPagePacket;

import javax.annotation.Nullable;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * The book itself: a two-page spread with a cover in front of it.
 *
 * <p>Coordinates inside a page are page-local, so a page never knows which half of the spread it
 * landed on; the screen translates before drawing and translates the mouse the same way, which is
 * also what makes tooltips land in the right place without every element correcting for it.
 */
public class BookScreen extends Screen {
  /** Size of the book's texture sheet */
  public static final int TEX_SIZE = 512;

  /* Book frame, as laid out in each book's page texture */
  private static final int BOOK_WIDTH = 412;
  private static final int BOOK_HEIGHT = 200;
  private static final int PAPER_U = 8;
  private static final int PAPER_V = 208;
  private static final int PAPER_WIDTH = 399;
  private static final int PAPER_HEIGHT = 184;
  private static final int PAPER_X = 6;
  private static final int PAPER_Y = 8;
  private static final int COVER_WIDTH = 206;
  private static final int COVER_HEIGHT = 200;

  /** Usable width of one page, and the coordinate space page content is written in */
  public static final int PAGE_WIDTH = 174;
  /** Usable height of one page */
  public static final int PAGE_HEIGHT = 176;
  /** Left page origin within the book */
  private static final int LEFT_PAGE_X = 16;
  /** Right page origin within the book */
  private static final int RIGHT_PAGE_X = 222;
  /** Page origin within the book, vertically */
  private static final int PAGE_Y = 12;

  /** Page index meaning the cover is showing */
  public static final int COVER_PAGE = -1;

  public final BookData book;
  private final PageUpdater updater;

  /** Index of the left page of the current spread; {@link #COVER_PAGE} for the cover */
  private int page = COVER_PAGE;
  /** Pages a link was followed from, so the back arrow can retrace */
  private final Deque<Integer> history = new ArrayDeque<>();

  private final List<BookElement> leftElements = new ArrayList<>();
  private final List<BookElement> rightElements = new ArrayList<>();

  private int bookLeft;
  private int bookTop;

  @Nullable
  private ArrowButton previousButton;
  @Nullable
  private ArrowButton nextButton;
  @Nullable
  private ArrowButton backButton;
  @Nullable
  private ArrowButton indexButton;

  public BookScreen(Component title, BookData book, String page, PageUpdater updater) {
    super(title);
    this.book = book;
    this.updater = updater;
    this.page = book.findPageNumber(page);
    if (this.page >= 0) {
      this.page -= this.page % 2;
    } else {
      this.page = COVER_PAGE;
    }
  }

  @Override
  protected void init() {
    this.bookLeft = (this.width - BOOK_WIDTH) / 2;
    this.bookTop = (this.height - BOOK_HEIGHT) / 2;

    ResourceLocation texture = this.book.appearance.bookTexture;
    int color = this.book.appearance.arrowColor;
    int hover = this.book.appearance.arrowColorHover;

    this.previousButton = this.addRenderableWidget(new ArrowButton(texture,
      this.bookLeft + LEFT_PAGE_X, this.bookTop + PAGE_Y + PAGE_HEIGHT + 2, ArrowType.LEFT, color, hover, b -> previousPage()));
    this.nextButton = this.addRenderableWidget(new ArrowButton(texture,
      this.bookLeft + RIGHT_PAGE_X + PAGE_WIDTH - ArrowType.RIGHT.w, this.bookTop + PAGE_Y + PAGE_HEIGHT + 2, ArrowType.RIGHT, color, hover, b -> nextPage()));
    this.backButton = this.addRenderableWidget(new ArrowButton(texture,
      this.bookLeft + LEFT_PAGE_X + 24, this.bookTop + PAGE_Y + PAGE_HEIGHT + 1, ArrowType.BACK, color, hover, b -> goBack()));
    this.indexButton = this.addRenderableWidget(new ArrowButton(texture,
      this.bookLeft + RIGHT_PAGE_X + PAGE_WIDTH - ArrowType.RIGHT.w - 26, this.bookTop + PAGE_Y + PAGE_HEIGHT + 2, ArrowType.PREV, color, hover, b -> openPage(0, true)));

    buildPages();
  }

  /* Navigation */

  /** Opens the spread holding the given page */
  public void openPage(int pageIndex) {
    openPage(pageIndex, false);
  }

  /**
   * Opens the spread holding the given page.
   * @param remember  Whether the current page is pushed so the back arrow can return to it
   */
  public void openPage(int pageIndex, boolean remember) {
    if (pageIndex < 0 || pageIndex >= this.book.getPageCount()) {
      return;
    }
    int target = pageIndex - pageIndex % 2;
    if (target == this.page) {
      return;
    }
    if (remember) {
      this.history.push(this.page);
    }
    this.page = target;
    buildPages();
    savePage();
  }

  /** Returns to the page a link was last followed from */
  public void goBack() {
    if (this.history.isEmpty()) {
      return;
    }
    this.page = this.history.pop();
    buildPages();
    savePage();
  }

  public void nextPage() {
    if (this.page == COVER_PAGE) {
      this.page = 0;
    } else if (this.page + 2 < this.book.getPageCount()) {
      this.page += 2;
    } else {
      return;
    }
    buildPages();
    savePage();
  }

  public void previousPage() {
    if (this.page == COVER_PAGE) {
      return;
    }
    this.page = this.page < 2 ? COVER_PAGE : this.page - 2;
    buildPages();
    savePage();
  }

  /** Rebuilds the elements of both visible pages */
  private void buildPages() {
    this.leftElements.clear();
    this.rightElements.clear();
    if (this.page == COVER_PAGE) {
      return;
    }
    buildPage(this.book.getPage(this.page), this.leftElements, false);
    buildPage(this.book.getPage(this.page + 1), this.rightElements, true);
  }

  /** Builds one page into the given list */
  private void buildPage(@Nullable PageData data, List<BookElement> target, boolean rightSide) {
    if (data == null || data.content == null) {
      return;
    }
    ArrayList<BookElement> elements = new ArrayList<>();
    try {
      data.content.build(this.book, elements, rightSide);
    } catch (Exception e) {
      Mantle.logger.error("Failed to build book page {}", data, e);
    }
    for (BookElement element : elements) {
      element.parent = this;
      element.parentScreen = this;
    }
    target.addAll(elements);
  }

  /** Writes the current page back onto the book stack */
  private void savePage() {
    PageData data = this.book.getPage(this.page);
    String reference = "";
    if (data != null && data.parent != null) {
      reference = data.parent.name + "." + data.name;
    }
    this.updater.update(reference);
  }

  /* Rendering */

  @Override
  public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
    this.renderBackground(graphics, mouseX, mouseY, partialTicks);
    RenderSystem.enableBlend();

    if (this.page == COVER_PAGE) {
      renderCover(graphics);
    } else {
      renderSpread(graphics, mouseX, mouseY, partialTicks);
    }

    // the arrows are vanilla widgets, so they draw in screen space after the book
    updateButtons();
    super.render(graphics, mouseX, mouseY, partialTicks);

    if (this.page != COVER_PAGE) {
      renderOverlays(graphics, mouseX, mouseY, partialTicks);
    }
  }

  /** Draws the closed book */
  private void renderCover(GuiGraphics graphics) {
    ResourceLocation cover = this.book.appearance.coverTexture;
    if (cover == null) {
      return;
    }
    int color = this.book.appearance.coverColor;
    RenderSystem.setShaderColor(((color >> 16) & 0xFF) / 255F, ((color >> 8) & 0xFF) / 255F, (color & 0xFF) / 255F, 1F);
    graphics.blit(cover, (this.width - COVER_WIDTH) / 2, (this.height - COVER_HEIGHT) / 2, 0, 0,
                  COVER_WIDTH, COVER_HEIGHT, TEX_SIZE, TEX_SIZE);
    RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
  }

  /** Draws the open book and both pages */
  private void renderSpread(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
    ResourceLocation texture = this.book.appearance.bookTexture;
    if (texture != null) {
      RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
      graphics.blit(texture, this.bookLeft, this.bookTop, 0, 0, BOOK_WIDTH, BOOK_HEIGHT, TEX_SIZE, TEX_SIZE);
      graphics.blit(texture, this.bookLeft + PAPER_X, this.bookTop + PAPER_Y, PAPER_U, PAPER_V,
                    PAPER_WIDTH, PAPER_HEIGHT, TEX_SIZE, TEX_SIZE);
    }
    drawPage(graphics, this.leftElements, this.bookLeft + LEFT_PAGE_X, mouseX, mouseY, partialTicks, false);
    drawPage(graphics, this.rightElements, this.bookLeft + RIGHT_PAGE_X, mouseX, mouseY, partialTicks, false);
  }

  /** Second pass for tooltips, which must draw above both pages */
  private void renderOverlays(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
    drawPage(graphics, this.leftElements, this.bookLeft + LEFT_PAGE_X, mouseX, mouseY, partialTicks, true);
    drawPage(graphics, this.rightElements, this.bookLeft + RIGHT_PAGE_X, mouseX, mouseY, partialTicks, true);
  }

  /** Draws one page's elements with the pose translated to the page origin */
  private void drawPage(GuiGraphics graphics, List<BookElement> elements, int originX, int mouseX, int mouseY, float partialTicks, boolean overlay) {
    if (elements.isEmpty()) {
      return;
    }
    int localX = mouseX - originX;
    int localY = mouseY - (this.bookTop + PAGE_Y);
    graphics.pose().pushPose();
    graphics.pose().translate(originX, this.bookTop + PAGE_Y, 0);
    for (BookElement element : elements) {
      try {
        if (overlay) {
          element.drawOverlay(graphics, localX, localY, partialTicks, this.book.fontRenderer);
        } else {
          element.update(localX, localY);
          element.draw(graphics, localX, localY, partialTicks, this.book.fontRenderer);
        }
      } catch (Exception e) {
        Mantle.logger.error("Failed to draw book element {}", element.getClass().getSimpleName(), e);
      }
    }
    graphics.pose().popPose();
    RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
  }

  /** Hides the arrows that would do nothing from where the reader is */
  private void updateButtons() {
    if (this.previousButton != null) {
      this.previousButton.visible = this.page != COVER_PAGE;
    }
    if (this.nextButton != null) {
      this.nextButton.visible = this.page == COVER_PAGE || this.page + 2 < this.book.getPageCount();
    }
    if (this.backButton != null) {
      this.backButton.visible = !this.history.isEmpty();
    }
    if (this.indexButton != null) {
      this.indexButton.visible = this.page > 0;
    }
  }

  /* Input */

  @Override
  public boolean mouseClicked(double mouseX, double mouseY, int button) {
    if (super.mouseClicked(mouseX, mouseY, button)) {
      return true;
    }
    if (this.page == COVER_PAGE) {
      nextPage();
      return true;
    }
    clickPage(this.leftElements, this.bookLeft + LEFT_PAGE_X, mouseX, mouseY, button);
    clickPage(this.rightElements, this.bookLeft + RIGHT_PAGE_X, mouseX, mouseY, button);
    return true;
  }

  /** Forwards a click to one page's elements, in page coordinates */
  private void clickPage(List<BookElement> elements, int originX, double mouseX, double mouseY, int button) {
    double localX = mouseX - originX;
    double localY = mouseY - (this.bookTop + PAGE_Y);
    // iterate a copy: an action may open another page and rebuild the list underneath us
    for (BookElement element : List.copyOf(elements)) {
      try {
        element.mouseClicked(localX, localY, button);
      } catch (Exception e) {
        Mantle.logger.error("Book element {} failed to handle a click", element.getClass().getSimpleName(), e);
      }
    }
  }

  @Override
  public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
    if (scrollY < 0) {
      nextPage();
    } else if (scrollY > 0) {
      previousPage();
    }
    return true;
  }

  @Override
  public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
    switch (keyCode) {
      case GLFW.GLFW_KEY_RIGHT, GLFW.GLFW_KEY_PAGE_DOWN -> {
        nextPage();
        return true;
      }
      case GLFW.GLFW_KEY_LEFT, GLFW.GLFW_KEY_PAGE_UP -> {
        previousPage();
        return true;
      }
      case GLFW.GLFW_KEY_BACKSPACE -> {
        goBack();
        return true;
      }
      default -> {
        return super.keyPressed(keyCode, scanCode, modifiers);
      }
    }
  }

  @Override
  public boolean isPauseScreen() {
    return false;
  }

  /** Section the current spread belongs to, or null on the cover */
  @Nullable
  public SectionData getCurrentSection() {
    PageData data = this.book.getPage(this.page);
    return data == null ? null : data.parent;
  }

  /**
   * Writes the reader's place back to wherever the book lives.
   *
   * <p>Three implementations because a book can be held, sitting in an open container, or on a
   * lectern, and the server needs to be told which before it can write the stack.
   */
  @FunctionalInterface
  public interface PageUpdater {
    void update(String page);

    /** Book held in a hand */
    static PageUpdater forHand(@Nullable InteractionHand hand) {
      if (hand == null) {
        return page -> {};
      }
      return page -> MantleNetwork.INSTANCE.sendToServer(new UpdateHeldPagePacket(hand, page));
    }

    /** Book in an inventory slot of the open container */
    static PageUpdater forSlot(int slot) {
      return page -> MantleNetwork.INSTANCE.sendToServer(new UpdateSavedPagePacket(slot, page));
    }

    /** Book resting on a lectern */
    static PageUpdater forLectern(BlockPos pos) {
      return page -> MantleNetwork.INSTANCE.sendToServer(new UpdateLecternPagePacket(pos, page));
    }
  }
}
