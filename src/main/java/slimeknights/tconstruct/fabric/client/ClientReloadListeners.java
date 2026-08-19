package slimeknights.tconstruct.fabric.client;

import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;

/**
 * Registers a plain reload listener with the client resource manager.
 *
 * <p>Forge's {@code RegisterClientReloadListenersEvent} took any {@code PreparableReloadListener};
 * Fabric wants an id alongside it so reloads can be ordered, which several of Tinkers' listeners —
 * the cache invalidators, written as lambdas — have no place to carry. This pairs the two.
 */
public final class ClientReloadListeners {
  private ClientReloadListeners() {}

  /** Registers a listener under the given id */
  public static void register(ResourceLocation id, ResourceManagerReloadListener listener) {
    ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
      @Override
      public ResourceLocation getFabricId() {
        return id;
      }

      @Override
      public void onResourceManagerReload(ResourceManager manager) {
        listener.onResourceManagerReload(manager);
      }
    });
  }
}
