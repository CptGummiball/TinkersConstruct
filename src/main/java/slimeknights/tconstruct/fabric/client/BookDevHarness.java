package slimeknights.tconstruct.fabric.client;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import slimeknights.mantle.client.book.data.BookData;
import slimeknights.mantle.client.book.data.PageData;
import slimeknights.mantle.client.book.data.SectionData;
import slimeknights.mantle.client.screen.book.BookScreen;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.client.book.TinkerBook;
import slimeknights.tconstruct.shared.item.TinkerBookItem.BookType;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Development harness that loads every book and screenshots a few spreads, then quits.
 *
 * <p>A book only fails once it is read, and a book has hundreds of pages: the only honest way to
 * check a page renders is to render it. Enabled with {@code -Dtconstruct.bookHarness=true}.
 */
public final class BookDevHarness {
  private BookDevHarness() {}

  /** System property gating the harness */
  private static final String PROPERTY = "tconstruct.bookHarness";

  private static final List<Runnable> STEPS = new ArrayList<>();
  private static int delay = 0;
  private static int step = 0;
  private static boolean started = false;

  /** Registers the harness if the property is set */
  public static void init() {
    if (!Boolean.getBoolean(PROPERTY)) {
      return;
    }
    ClientTickEvents.END_CLIENT_TICK.register(BookDevHarness::tick);
  }

  private static void tick(Minecraft minecraft) {
    if (minecraft.level == null || minecraft.player == null) {
      return;
    }
    if (!started) {
      // give the world a moment to settle so recipes and datapack registries are in
      if (++delay < 60) {
        return;
      }
      started = true;
      // the menu blur post-pass smears whatever is already in the render target, which makes a
      // screenshot useless for judging the layout
      minecraft.options.menuBackgroundBlurriness().set(0);
      buildSteps();
    }
    if (--delay > 0) {
      return;
    }
    if (step >= STEPS.size()) {
      TConstruct.LOG.info("[book harness] done");
      minecraft.stop();
      return;
    }
    STEPS.get(step++).run();
    delay = 20;
  }

  /** One spread worth photographing: which book, which page reference, what to call the file */
  private record Shot(BookType book, String page, String label) {}

  /**
   * Spreads chosen to cover every page type the books actually use.
   *
   * <p>One of each kind is enough: the harness already builds all thousand pages, so what a
   * screenshot adds is whether a given layout looks right, not whether it throws.
   */
  private static final List<Shot> SHOTS = List.of(
    new Shot(BookType.MATERIALS_AND_YOU, "", "index"),
    new Shot(BookType.MATERIALS_AND_YOU, "intro.pattern_craft", "crafting"),
    new Shot(BookType.MATERIALS_AND_YOU, "intro.partbuilder", "image_text"),
    new Shot(BookType.MATERIALS_AND_YOU, "tools.index", "tool_listing"),
    new Shot(BookType.MATERIALS_AND_YOU, "tools.tconstruct.pickaxe", "tool"),
    new Shot(BookType.MATERIALS_AND_YOU, "melee_harvest_materials.tconstruct:iron", "material"),
    new Shot(BookType.MIGHTY_SMELTING, "smeltery.structure", "structure"),
    new Shot(BookType.PUNY_SMELTING, "upgrades.index", "modifier_listing"),
    new Shot(BookType.PUNY_SMELTING, "armor_materials.tconstruct:iron", "armor_material"),
    new Shot(BookType.ENCYCLOPEDIA, "fluid_effects.tconstruct.water", "fluid_effect"),
    new Shot(BookType.ENCYCLOPEDIA, "fluid_effects.tconstruct.molten_iron", "fluid_effect_molten"),
    new Shot(BookType.ENCYCLOPEDIA, "materials_skull.tconstruct:blaze", "skull")
  );

  /** Builds the list of things to check, one per tick batch */
  private static void buildSteps() {
    for (BookType type : BookType.values()) {
      BookData book = TinkerBook.getBook(type);
      STEPS.add(() -> report(type, book));
    }
    // the path a player actually takes: right-click the item, then turn a page, which writes the
    // page back to the stack through the server
    STEPS.add(BookDevHarness::openThroughItem);
    STEPS.add(() -> shoot("held_item"));
    for (Shot shot : SHOTS) {
      BookData book = TinkerBook.getBook(shot.book());
      STEPS.add(() -> {
        int page = book.findPageNumber(shot.page());
        if (page < 0 && !shot.page().isEmpty()) {
          TConstruct.LOG.warn("[book harness] {}: page reference {} did not resolve", shot.label(), shot.page());
        }
        open(book, page, shot.label());
      });
      STEPS.add(() -> shoot(shot.label()));
    }
  }

  /** Loads a book and logs what it ended up with */
  private static void report(BookType type, BookData book) {
    try {
      book.load();
    } catch (Exception e) {
      TConstruct.LOG.error("[book harness] {} failed to load", type, e);
      return;
    }
    int pages = book.getPageCount();
    StringBuilder sections = new StringBuilder();
    for (SectionData section : book.sections) {
      sections.append(sections.isEmpty() ? "" : ", ").append(section.name).append('=').append(section.pages.size());
    }
    TConstruct.LOG.info("[book harness] {}: {} sections, {} pages ({})", type, book.sections.size(), pages, sections);

    // building every page is what actually exercises the content classes
    int failed = 0;
    java.util.Set<String> missingModels = new java.util.TreeSet<>();
    for (int i = 0; i < pages; i++) {
      PageData page = book.getPage(i);
      if (page == null || page.content == null) {
        continue;
      }
      try {
        ArrayList<slimeknights.mantle.client.screen.book.element.BookElement> elements = new ArrayList<>();
        page.content.build(book, elements, i % 2 == 1);
        checkModels(elements, missingModels);
      } catch (Exception e) {
        failed++;
        TConstruct.LOG.error("[book harness] {} page {}.{} ({}) failed to build",
                             type, page.parent == null ? "?" : page.parent.name, page.name, page.type, e);
      }
    }
    TConstruct.LOG.info("[book harness] {}: {} of {} pages failed to build", type, failed, pages);
    if (!missingModels.isEmpty()) {
      // the second check reads the model's particle sprite, which a few vanilla models leave
      // unset, so this is a "look at these" list rather than a failure
      TConstruct.LOG.warn("[book harness] {}: items whose model may lack a texture: {}", type, String.join(", ", missingModels));
    }
  }

  /** Flags any item a page wants to draw that has no model, which would show as a magenta checker */
  private static void checkModels(List<slimeknights.mantle.client.screen.book.element.BookElement> elements,
                                  java.util.Set<String> missing) {
    Minecraft minecraft = Minecraft.getInstance();
    net.minecraft.client.resources.model.BakedModel missingModel = minecraft.getModelManager().getMissingModel();
    for (slimeknights.mantle.client.screen.book.element.BookElement element : elements) {
      if (element instanceof slimeknights.mantle.client.screen.book.element.ItemElement item) {
        // check the whole cycle, not just what happens to be showing this tick
        for (ItemStack stack : item.getStacks()) {
          if (stack.isEmpty()) {
            continue;
          }
          net.minecraft.client.resources.model.BakedModel model = minecraft.getItemRenderer().getModel(stack, minecraft.level, minecraft.player, 0);
          if (model == missingModel || model.getParticleIcon().contents().name().equals(net.minecraft.client.renderer.texture.MissingTextureAtlasSprite.getLocation())) {
            missing.add(net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(stack.getItem()).toString());
          }
        }
      }
    }
  }

  /** Opens a book the way the item does, then turns a page so the save packet is exercised */
  private static void openThroughItem() {
    net.minecraft.world.item.Item item = net.minecraft.core.registries.BuiltInRegistries.ITEM.get(
      slimeknights.tconstruct.library.TinkerBookIDs.MATERIALS_BOOK_ID);
    if (!(item instanceof slimeknights.mantle.item.AbstractBookItem book)) {
      TConstruct.LOG.error("[book harness] {} is not a book item", slimeknights.tconstruct.library.TinkerBookIDs.MATERIALS_BOOK_ID);
      return;
    }
    try {
      ItemStack stack = new ItemStack(item);
      book.getBook(stack).openGui(net.minecraft.world.InteractionHand.MAIN_HAND, stack);
      if (Minecraft.getInstance().screen instanceof BookScreen screen) {
        screen.nextPage();
        screen.nextPage();
      } else {
        TConstruct.LOG.error("[book harness] the item did not open a book screen");
      }
    } catch (Exception e) {
      TConstruct.LOG.error("[book harness] opening through the item failed", e);
    }
  }

  /** Opens a book screen at the given page */
  private static void open(BookData book, int page, String label) {
    try {
      Minecraft.getInstance().setScreen(new BookScreen(Component.literal(label), book, "", p -> {}));
      BookScreen screen = (BookScreen)Minecraft.getInstance().screen;
      if (screen != null) {
        screen.openPage(Math.max(0, page));
      }
    } catch (Exception e) {
      TConstruct.LOG.error("[book harness] failed to open {}", label, e);
    }
  }

  /** Grabs a screenshot of whatever is on screen */
  private static void shoot(String label) {
    Minecraft minecraft = Minecraft.getInstance();
    Screenshot.grab(minecraft.gameDirectory, "book_" + label + ".png",
                    minecraft.getMainRenderTarget(), message -> TConstruct.LOG.info("[book harness] {}", message.getString()));
  }
}
