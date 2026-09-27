package slimeknights.mantle.command.client;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import slimeknights.mantle.Mantle;
import slimeknights.mantle.client.book.BookLoader;
import slimeknights.mantle.client.book.data.BookData;
import slimeknights.mantle.client.screen.book.BookScreen;
import slimeknights.mantle.command.GeneratePackHelper;

import javax.annotation.Nullable;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Book utilities: open any registered book, or render every spread to PNG files.
 *
 * <p>PORT: upstream also exported the pages as HTML for SlimeKnights' online book viewer. The
 * reconstructed book elements carry no HTML serialization, and the export exists to feed that
 * website, so the {@code export_html} subcommand stayed out; {@code export_images} is complete.
 */
public class BookCommand {
  private static final String BOOK_NOT_FOUND = "command.mantle.book_test.not_found";
  private static final String EXPORT_SUCCESS = "command.mantle.book.export.success";
  private static final String EXPORT_FAIL = "command.mantle.book.export.error_generic";
  private static final String EXPORT_FAIL_IO = "command.mantle.book.export.error_io";

  private static final int DEFAULT_SCALE = 2;

  /**
   * Registers this sub command with the root command
   * @param subCommand  Command builder
   */
  public static void register(LiteralArgumentBuilder<FabricClientCommandSource> subCommand) {
    subCommand
      // mantle book open <id>
      .then(ClientCommandManager.literal("open")
        .then(ClientCommandManager.argument("id", ResourceLocationArgument.id()).suggests(MantleClientCommand.REGISTERED_BOOKS)
          .executes(BookCommand::openBook)))
      // mantle book export_images domain <domain> [scale] | export_images <id> [scale]
      .then(ClientCommandManager.literal("export_images")
        .then(ClientCommandManager.literal("domain")
          .then(ClientCommandManager.argument("domain", StringArgumentType.word()).suggests(MantleClientCommand.REGISTERED_BOOK_DOMAINS)
            .then(ClientCommandManager.argument("scale", IntegerArgumentType.integer(1, 16))
              .executes(context -> exportDomainImages(context, IntegerArgumentType.getInteger(context, "scale"))))
            .executes(context -> exportDomainImages(context, DEFAULT_SCALE))))
        .then(ClientCommandManager.argument("id", ResourceLocationArgument.id()).suggests(MantleClientCommand.REGISTERED_BOOKS)
          .then(ClientCommandManager.argument("scale", IntegerArgumentType.integer(1, 16))
            .executes(context -> exportImages(context, IntegerArgumentType.getInteger(context, "scale"))))
          .executes(context -> exportImages(context, DEFAULT_SCALE))));
  }

  /** Opens the specified book */
  private static int openBook(CommandContext<FabricClientCommandSource> context) {
    ResourceLocation book = context.getArgument("id", ResourceLocation.class);
    BookData bookData = BookLoader.getBook(book);
    if (bookData != null) {
      // delay execution to ensure the chat window is closed first
      Minecraft.getInstance().tell(() -> {
        bookData.load();
        Minecraft.getInstance().setScreen(new BookScreen(Component.literal("Book"), bookData, "", page -> {}));
      });
      return 0;
    }
    bookNotFound(book);
    return 1;
  }

  /** Renders all spreads of the given book to files */
  private static int exportImages(CommandContext<FabricClientCommandSource> context, int scale) {
    ResourceLocation book = context.getArgument("id", ResourceLocation.class);
    return doExport(book, scale);
  }

  /** Renders all spreads of every book in the given domain to files */
  private static int exportDomainImages(CommandContext<FabricClientCommandSource> context, int scale) {
    String domain = StringArgumentType.getString(context, "domain");
    for (ResourceLocation book : BookLoader.getAllBooks()) {
      if (domain.equals(book.getNamespace())) {
        int code = doExport(book, scale);
        if (code != 0) {
          return code;
        }
      }
    }
    return 0;
  }

  /** Renders all spreads of the book into screenshots/mantle_book/namespace/path/ */
  private static int doExport(ResourceLocation book, int scale) {
    BookData bookData = BookLoader.getBook(book);
    if (bookData == null) {
      bookNotFound(book);
      return 1;
    }
    Path gameDirectory = Minecraft.getInstance().gameDirectory.toPath();
    Path screenshotDir = Paths.get(gameDirectory.toString(), Screenshot.SCREENSHOT_DIR, "mantle_book", book.getNamespace(), book.getPath());
    if (!screenshotDir.toFile().mkdirs() && !screenshotDir.toFile().exists()) {
      error(Component.translatable(EXPORT_FAIL_IO, screenshotDir.toString()));
      return 1;
    }
    Exporter.start(bookData, screenshotDir, scale);
    return 0;
  }

  /**
   * Drives the export across real frames: the book screen is opened like any other, each spread
   * is captured out of the freshly rendered window framebuffer cropped to the book's rectangle,
   * then the page turns and the next frame is captured.
   *
   * <p>PORT: upstream rendered every spread into its own offscreen target in a single call. On
   * 1.21 too much of the gui pipeline re-binds the main target and resets blend state mid-render
   * for that to survive faithfully (Screen.renderBlurredBackground does so unconditionally, every
   * blit resets the blend function), so the pages washed out. Capturing rendered frames uses the
   * exact pipeline the player sees, so the export is pixel-identical with the game; the trade is
   * that the page background is the world rather than transparency, and the export takes a couple
   * of frames per spread.
   */
  private static class Exporter {
    private static Exporter active;

    static {
      net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.END_CLIENT_TICK.register(minecraft -> {
        if (active != null) {
          active.tick(minecraft);
        }
      });
    }

    private final BookData bookData;
    private final Path screenshotDir;
    private final int guiScale;
    private final int oldGuiScale;
    private BookScreen screen;
    private int settleTicks = 0;
    private int saved = 0;

    private Exporter(BookData bookData, Path screenshotDir, int guiScale) {
      this.bookData = bookData;
      this.screenshotDir = screenshotDir;
      this.guiScale = guiScale;
      this.oldGuiScale = Minecraft.getInstance().options.guiScale().get();
    }

    /** Begins an export, replacing any still running */
    static void start(BookData bookData, Path screenshotDir, int scale) {
      Exporter exporter = new Exporter(bookData, screenshotDir, scale);
      Minecraft minecraft = Minecraft.getInstance();
      minecraft.tell(() -> {
        // a fixed gui scale makes the crop exact and the output size stable
        minecraft.options.guiScale().set(scale);
        minecraft.resizeDisplay();
        bookData.load();
        exporter.screen = new BookScreen(Component.literal("Book"), bookData, "", page -> {});
        exporter.screen.drawArrows = false;
        // a toast sliding in would photobomb every captured spread
        minecraft.getToasts().clear();
        minecraft.setScreen(exporter.screen);
        active = exporter;
      });
    }

    private void finish(Minecraft minecraft, boolean success) {
      active = null;
      minecraft.options.guiScale().set(oldGuiScale);
      minecraft.resizeDisplay();
      if (minecraft.screen == screen) {
        minecraft.setScreen(null);
      }
      if (success) {
        Player player = minecraft.player;
        if (player != null) {
          player.displayClientMessage(Component.translatable(EXPORT_SUCCESS, GeneratePackHelper.getOutputComponent(screenshotDir)), false);
        }
        Mantle.logger.info("Exported {} book spreads to {}", saved, screenshotDir);
      }
    }

    private void tick(Minecraft minecraft) {
      if (minecraft.screen != screen) {
        finish(minecraft, false); // closed by the user or another screen took over
        return;
      }
      // let the freshly opened or freshly turned page render before grabbing it
      if (++settleTicks < 3) {
        return;
      }
      settleTicks = 0;

      int page = screen.getPageForExport();
      boolean cover = page < 0;
      // book bounds in gui units, mirroring the screen's own layout
      int width = cover ? BookScreen.PAGE_WIDTH_UNSCALED : BookScreen.PAGE_WIDTH_UNSCALED * 2;
      int height = BookScreen.PAGE_HEIGHT_UNSCALED;
      int left = (minecraft.getWindow().getGuiScaledWidth() - width) / 2;
      int top = (minecraft.getWindow().getGuiScaledHeight() - height) / 2;
      double windowScale = minecraft.getWindow().getGuiScale();

      try (NativeImage frame = Screenshot.takeScreenshot(minecraft.getMainRenderTarget())) {
        int px = (int) (left * windowScale);
        int py = (int) (top * windowScale);
        int pw = (int) (width * windowScale);
        int ph = (int) (height * windowScale);
        try (NativeImage crop = new NativeImage(frame.format(), pw, ph, false)) {
          frame.copyRect(crop, px, py, 0, 0, pw, ph, false, false);
          crop.writeToFile(screenshotDir.resolve((cover ? "cover" : "page_" + page) + ".png"));
          saved++;
        }
      } catch (Exception e) {
        Mantle.logger.error("Failed to save book page screenshot", e);
        error(Component.translatable(EXPORT_FAIL));
        finish(minecraft, false);
        return;
      }

      if (!screen.advancePageForExport()) {
        finish(minecraft, true);
      }
    }
  }

  /** Sends an error message to the player */
  private static void error(Component message) {
    Player player = Minecraft.getInstance().player;
    if (player != null) {
      player.displayClientMessage(message.copy().withStyle(ChatFormatting.RED), false);
    }
  }

  /** Sends the book-not-found error for the given id */
  public static void bookNotFound(ResourceLocation book) {
    error(Component.translatable(BOOK_NOT_FOUND, book));
  }
}
