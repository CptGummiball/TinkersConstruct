package slimeknights.mantle.data.loadable.common;

import com.google.gson.JsonSyntaxException;
import io.netty.handler.codec.DecoderException;
import lombok.RequiredArgsConstructor;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import slimeknights.mantle.data.loadable.field.ContextKey;
import slimeknights.mantle.data.loadable.primitive.ResourceLocationLoadable;
import slimeknights.mantle.util.typed.TypedMap;

/**
 * Loadable for entries of a <b>datapack</b> registry, such as enchantments in 1.21.
 *
 * <p>{@link RegistryLoadable} and {@link LazyRegistryLoadable} both resolve through a
 * context-free {@code registry()} call, which only works for registries that exist statically.
 * Datapack registries are built per-world, so the registry has to arrive with the call.
 *
 * <p>This yields {@link Holder} rather than the bare value, which is what makes it work at all:
 * {@code getKey} is handed no context, so a bare value could never be turned back into an id.
 * A holder carries its own key. It is also the idiomatic 1.21 shape — vanilla's enchantment
 * APIs all take holders now.
 *
 * @see ContextKey#REGISTRY_ACCESS
 */
@RequiredArgsConstructor
public class DynamicRegistryLoadable<T> implements ResourceLocationLoadable<Holder<T>> {

  private final ResourceKey<? extends Registry<T>> registryKey;

  @Override
  public Holder<T> fromKey(ResourceLocation name, String key, TypedMap context) {
    HolderLookup.Provider provider = context.get(ContextKey.REGISTRY_ACCESS);
    if (provider == null) {
      throw new JsonSyntaxException(
        "Unable to parse " + key + ": " + registryKey.location()
          + " is a datapack registry and no registry access was supplied");
    }
    @SuppressWarnings("unchecked")  // the key identifies the registry's own element type
    ResourceKey<T> entryKey = ResourceKey.create((ResourceKey<Registry<T>>) registryKey, name);
    return provider.lookupOrThrow(registryKey).get(entryKey)
      .orElseThrow(() -> new JsonSyntaxException(
        "Unable to parse " + key + " as registry " + registryKey.location()
          + " does not contain ID " + name));
  }

  @Override
  public ResourceLocation getKey(Holder<T> object) {
    // No registry needed: a holder knows its own key.
    return object.unwrapKey()
      .orElseThrow(() -> new RuntimeException(
        "Cannot serialize an unregistered " + registryKey.location() + " entry"))
      .location();
  }

  @Override
  public Holder<T> decode(RegistryFriendlyByteBuf buffer, TypedMap context) {
    ResourceLocation name = buffer.readResourceLocation();
    @SuppressWarnings("unchecked")
    ResourceKey<T> entryKey = ResourceKey.create((ResourceKey<Registry<T>>) registryKey, name);
    // RegistryFriendlyByteBuf carries registry access, so no context is needed here.
    return buffer.registryAccess().lookupOrThrow(registryKey).get(entryKey)
      .orElseThrow(() -> new DecoderException(
        "Registry " + registryKey.location() + " does not contain ID " + name));
  }

  @Override
  public void encode(RegistryFriendlyByteBuf buffer, Holder<T> object) {
    buffer.writeResourceLocation(getKey(object));
  }
}
