package slimeknights.mantle.transfer.cap;

/**
 * Shim of Forge's capability token. On Forge this keyed a registry of providers; here it is
 * only an identity key the block entities compare against in {@code getCapability} — the
 * outward-facing side of each capability is a Fabric lookup registration instead.
 */
public final class Capability<T> {
  private final String name;

  Capability(String name) {
    this.name = name;
  }

  /** Forge's provider convenience: returns the holder cast to this capability when the tokens match */
  public <R> LazyOptional<R> orEmpty(Capability<R> toCheck, LazyOptional<T> instance) {
    return this == toCheck ? instance.cast() : LazyOptional.empty();
  }

  @Override
  public String toString() {
    return "Capability[" + name + "]";
  }
}
