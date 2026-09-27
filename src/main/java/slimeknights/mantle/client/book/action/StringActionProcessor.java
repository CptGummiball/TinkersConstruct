package slimeknights.mantle.client.book.action;

import net.minecraft.client.Minecraft;
import slimeknights.mantle.Mantle;
import slimeknights.mantle.client.screen.book.BookScreen;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Runs the {@code action} strings that book elements carry.
 *
 * <p>Actions are strings rather than code because they are written in page JSON: a link in a book
 * is {@code "mantle:go-to-page-rtn intro.welcome"}. Handlers are registered by protocol name so a
 * mod can add its own without touching the elements that fire them.
 */
public final class StringActionProcessor {
  private StringActionProcessor() {}

  private static final Map<String,ActionProtocol> PROTOCOLS = new HashMap<>();

  static {
    register("mantle:go-to-page", (screen, data) -> goToPage(screen, data, false));
    register("mantle:go-to-page-rtn", (screen, data) -> goToPage(screen, data, true));
    register("mantle:go-back", (screen, data) -> screen.goBack());
    register("mantle:close-gui", (screen, data) -> Minecraft.getInstance().setScreen(null));
  }

  /** Adds a protocol handler */
  public static void register(String protocol, ActionProtocol handler) {
    PROTOCOLS.put(protocol.toLowerCase(Locale.ROOT), handler);
  }

  /**
   * Runs an action string.
   * @param action  Whole action, protocol first, arguments after the first space
   * @param screen  Screen the action was fired from
   */
  public static void process(@Nullable String action, @Nullable BookScreen screen) {
    if (action == null || action.isEmpty() || screen == null) {
      return;
    }
    int split = action.indexOf(' ');
    String protocol = (split < 0 ? action : action.substring(0, split)).toLowerCase(Locale.ROOT);
    String data = split < 0 ? "" : action.substring(split + 1).trim();
    ActionProtocol handler = PROTOCOLS.get(protocol);
    if (handler == null) {
      Mantle.logger.warn("Unknown book action protocol {}", protocol);
      return;
    }
    try {
      handler.process(screen, data);
    } catch (Exception e) {
      Mantle.logger.error("Book action {} failed", action, e);
    }
  }

  /** Jumps to the page named by a {@code section.page} reference */
  private static void goToPage(BookScreen screen, String data, boolean remember) {
    int page = screen.book.findPageNumber(data);
    if (page < 0) {
      Mantle.logger.warn("Book link points at unknown page {}", data);
      return;
    }
    screen.openPage(page, remember);
  }

  /** Handler for one protocol */
  @FunctionalInterface
  public interface ActionProtocol {
    void process(BookScreen screen, String data);
  }
}
