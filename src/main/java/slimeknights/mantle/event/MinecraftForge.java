package slimeknights.mantle.event;

/**
 * Namesake holder for the game-wide event bus, so `MinecraftForge.EVENT_BUS.post(...)` and
 * `addListener(...)` calls port as an import rewrite. See {@link Event} for scope.
 */
public final class MinecraftForge {

  private MinecraftForge() {}

  public static final EventBus EVENT_BUS = new EventBus();
}
