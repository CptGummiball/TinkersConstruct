package slimeknights.mantle;

import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Mantle's shared constants and resource helpers.
 *
 * <p>Upstream this is a Forge {@code @Mod} class that also wires up config, networking,
 * capabilities, commands and datagen. None of that survives the move to Fabric — Tinkers
 * drives its own initialisation from {@code TConstructBootstrap} — and only these five
 * members are actually referenced (30 call sites). The rest is deliberately dropped rather
 * than ported into a mod class Fabric would never load.
 */
public final class Mantle {

  private Mantle() {}

  public static final String modId = "mantle";

  public static final Logger logger = LogManager.getLogger("Mantle");

  /**
   * Namespace for cross-mod convention tags.
   *
   * <p>Forge used {@code forge:}; 1.21 and the Fabric ecosystem use {@code c:} — upstream
   * Mantle's own javadoc notes the same switch. This is what lets Tinkers see other mods'
   * ingots, ores and gems in the GummiCraft pack, so it is not cosmetic: leaving it as
   * "forge" would make every cross-mod material tag silently empty.
   */
  public static final String COMMON = "c";

  /** Resource location in Mantle's own namespace. */
  public static ResourceLocation getResource(String name) {
    return ResourceLocation.fromNamespaceAndPath(modId, name);
  }

  /** Resource location in the convention-tag namespace. */
  public static ResourceLocation commonResource(String name) {
    return ResourceLocation.fromNamespaceAndPath(COMMON, name);
  }

  public static String makeDescriptionId(String base, String name) {
    return Util.makeDescriptionId(base, getResource(name));
  }

  public static MutableComponent makeComponent(String base, String name) {
    return Component.translatable(makeDescriptionId(base, name));
  }
}
