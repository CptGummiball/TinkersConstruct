package slimeknights.mantle.data.loadable.common;

import com.google.gson.JsonSyntaxException;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import slimeknights.mantle.data.loadable.Loadable;
import slimeknights.mantle.data.loadable.mapping.EnumMapLoadable;
import slimeknights.mantle.data.loadable.primitive.ResourceLocationLoadable;
import slimeknights.mantle.util.typed.TypedMap;

import java.util.Map;

/**
 * Loadable for item display contexts.
 *
 * <p>Forge turned {@link ItemDisplayContext} into a registry so mods could add their own
 * perspectives; in vanilla 1.21 it is back to a plain enum. Lookup therefore goes through
 * the enum's serialized names, and ids stay in the {@code minecraft} namespace — which is
 * what the existing model JSONs already write.
 */
public enum DisplayContextLoadable implements ResourceLocationLoadable<ItemDisplayContext> {
  INSTANCE;

  @Override
  public ItemDisplayContext fromKey(ResourceLocation name, String key, TypedMap context) {
    if (name.getNamespace().equals(ResourceLocation.DEFAULT_NAMESPACE)) {
      String path = name.getPath();
      for (ItemDisplayContext value : ItemDisplayContext.values()) {
        if (value.getSerializedName().equals(path)) {
          return value;
        }
      }
    }
    throw new JsonSyntaxException("Unable to parse " + key + " as " + name + " is not a known ItemDisplayContext");
  }

  @Override
  public ResourceLocation getKey(ItemDisplayContext object) {
    return ResourceLocation.withDefaultNamespace(object.getSerializedName());
  }

  @Override
  public ItemDisplayContext decode(RegistryFriendlyByteBuf buffer, TypedMap context) {
    return buffer.readEnum(ItemDisplayContext.class);
  }

  @Override
  public void encode(RegistryFriendlyByteBuf buffer, ItemDisplayContext value) {
    buffer.writeEnum(value);
  }

  @Override
  public <V> Loadable<Map<ItemDisplayContext, V>> mapWithValues(Loadable<V> valueLoadable, int minSize) {
    return new EnumMapLoadable<>(ItemDisplayContext.class, this, valueLoadable, minSize);
  }
}
