package slimeknights.mantle.data.listener;

import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;

/**
 * Same as {@link ResourceManagerReloadListener}, but named for the guard it used to carry.
 *
 * <p>On Forge this skipped the reload when {@code ModLoader.isLoadingStateValid()} was false, so a
 * client resource listener would not throw on top of an earlier error and bury it in the crash
 * report. Fabric has no partially-loaded state to guard against — a failed entrypoint aborts
 * startup — so the guard is gone and the interface is kept for the name its implementors use.
 */
public interface ISafeManagerReloadListener extends ResourceManagerReloadListener {
  @Override
  default void onResourceManagerReload(ResourceManager resourceManager) {
    onReloadSafe(resourceManager);
  }

  /**
   * Handle a resource manager reload
   * @param resourceManager  Resource manager
   */
  void onReloadSafe(ResourceManager resourceManager);
}
