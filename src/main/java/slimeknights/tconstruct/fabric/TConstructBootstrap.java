package slimeknights.tconstruct.fabric;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import slimeknights.mantle.fluid.transfer.FluidContainerTransferManager;
import slimeknights.mantle.network.MantleNetwork;
import slimeknights.mantle.transfer.fluid.TransferComponents;
import slimeknights.tconstruct.library.json.condition.TagDifferencePresentCondition;
import slimeknights.tconstruct.library.json.condition.TagIntersectionPresentCondition;

/**
 * Fabric {@code main} entrypoint.
 *
 * <p>Forge drove initialisation through the mod event bus, with each {@code TinkerModule}
 * subscribing to registry events and Forge deciding the order. Fabric has no such bus, so
 * ordering becomes explicit here: registration runs eagerly during construction, in the same
 * order the Forge build registered its modules, because several modules read registry objects
 * created by earlier ones.
 */
public class TConstructBootstrap implements ModInitializer {

  public static final String MOD_ID = "tconstruct";
  public static final Logger LOG = LoggerFactory.getLogger("Tinkers' Construct");

  @Override
  public void onInitialize() {
    LOG.info("Tinkers' Construct (Fabric {}) starting", MOD_ID);

    // Must run before any FluidStack is built: fluid stacks carry their legacy NBT tag
    // through this component type.
    TransferComponents.register();

    // Mantle infrastructure: packet channel plus the fluid container transfer loader.
    MantleNetwork.registerPackets();
    FluidContainerTransferManager.INSTANCE.init();

    // Tinkers' own recipe conditions; data files reference them, so they must parse
    // before the first datapack load. Forge registered these through CraftingHelper.
    TagDifferencePresentCondition.register();
    TagIntersectionPresentCondition.register();

    // Further modules are wired in as each one finishes porting; see PORTING.md.
  }
}
