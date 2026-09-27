package slimeknights.mantle.item;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Fabric stand-in for Forge's {@code net.minecraftforge.common.ToolAction}.
 *
 * <p>A ToolAction names one thing a tool can do — stripping a log, shearing a beehive,
 * blocking with a shield. Forge used these to let modded tools opt into vanilla behaviours;
 * Fabric has no equivalent concept, so Tinkers carries its own. As in Forge these are
 * interned by name, which is what makes {@code ==} comparison valid across the 37 files
 * that use them.
 */
public final class ToolAction {

  private static final Map<String, ToolAction> ACTIONS = new ConcurrentHashMap<>();

  /** Interns and returns the action with this name, creating it on first request. */
  public static ToolAction get(String name) {
    return ACTIONS.computeIfAbsent(name, ToolAction::new);
  }

  /** All actions requested so far, for debugging and JEI/EMI listings. */
  public static Map<String, ToolAction> getActions() {
    return Collections.unmodifiableMap(ACTIONS);
  }

  private final String name;

  private ToolAction(String name) {
    this.name = name;
  }

  public String name() {
    return name;
  }

  @Override
  public String toString() {
    return name;
  }

  // Identity equality is deliberate: instances are interned by get(String).
}
