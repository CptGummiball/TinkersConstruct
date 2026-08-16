package slimeknights.mantle.event;

/**
 * Fabric stand-in for Forge's {@code net.minecraftforge.eventbus.api.Event}.
 *
 * <p>Tinkers both defines its own events (tool events, teleport events, materials-loaded)
 * and cancels/inspects them across ~40 files. Fabric's callback interfaces cannot express
 * that shape source-compatibly, so a minimal bus with the Forge API surface carries those
 * uses: subclass events, {@code @Cancelable}, priorities, post/addListener.
 *
 * <p>Vanilla-behaviour events that Forge fired (break speed, teleports) fire from bridge
 * hooks — Fabric callbacks where they exist, mixins where they do not — wired in the
 * event-layer step tracked in PORTING.md.
 */
public class Event {

  private boolean canceled = false;
  private Result result = Result.DEFAULT;

  /** Whether this event can be canceled, driven by the {@link Cancelable} annotation. */
  public boolean isCancelable() {
    return getClass().isAnnotationPresent(Cancelable.class);
  }

  public boolean isCanceled() {
    return canceled;
  }

  public void setCanceled(boolean canceled) {
    if (canceled && !isCancelable()) {
      throw new UnsupportedOperationException("Attempted to cancel a non-cancelable event " + getClass().getName());
    }
    this.canceled = canceled;
  }

  /** Whether this event uses {@link Result}. */
  public boolean hasResult() {
    return false;
  }

  public Result getResult() {
    return result;
  }

  public void setResult(Result result) {
    this.result = result;
  }

  /** Tri-state outcome used by events that override vanilla decisions. */
  public enum Result {
    DENY,
    DEFAULT,
    ALLOW
  }
}
