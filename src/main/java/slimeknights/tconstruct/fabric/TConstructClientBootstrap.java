package slimeknights.tconstruct.fabric;

import net.fabricmc.api.ClientModInitializer;
import slimeknights.mantle.network.NetworkWrapperClient;

/**
 * Fabric {@code client} entrypoint, replacing Forge's {@code FMLClientSetupEvent} and the
 * various {@code Register*Event} client hooks.
 */
public class TConstructClientBootstrap implements ClientModInitializer {

  @Override
  public void onInitializeClient() {
    // Server->client packet receivers collect during common init and register here,
    // since ClientPlayNetworking does not exist on a dedicated server.
    NetworkWrapperClient.init();

    // Further client modules are wired in as each one finishes porting; see PORTING.md.
  }
}
