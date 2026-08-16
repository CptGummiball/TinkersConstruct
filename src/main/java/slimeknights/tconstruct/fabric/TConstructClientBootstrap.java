package slimeknights.tconstruct.fabric;

import net.fabricmc.api.ClientModInitializer;

/**
 * Fabric {@code client} entrypoint, replacing Forge's {@code FMLClientSetupEvent} and the
 * various {@code Register*Event} client hooks.
 */
public class TConstructClientBootstrap implements ClientModInitializer {

  @Override
  public void onInitializeClient() {
    // Client modules are wired in as each one finishes porting; see PORTING.md.
  }
}
