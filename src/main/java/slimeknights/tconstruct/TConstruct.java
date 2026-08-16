package slimeknights.tconstruct;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Locale;
import java.util.Random;

/**
 * TConstruct, the tool mod. Craft your tools with style, then modify until the original is gone!
 *
 * <p>On Forge this was the {@code @Mod} entry class, driving module registration through the
 * event bus, datagen through {@code GatherDataEvent} and world migrations through
 * {@code MissingMappingsEvent}. On Fabric none of those mechanisms exist: initialisation
 * lives in {@code slimeknights.tconstruct.fabric.TConstructBootstrap}, datagen in the Fabric
 * datagen entrypoint, and there are no Forge-era worlds to migrate. What remains here is what
 * the other 1900 files actually reference — the mod id and the static helpers.
 *
 * <p>{@code createKey}/{@code ComputableDataKey} return with the TinkerData port (the Forge
 * entity capability that backs modifier markers); tracked in PORTING.md.
 *
 * @author mDiyo
 */
public class TConstruct {

  public static final String MOD_ID = "tconstruct";
  public static final Logger LOG = LogManager.getLogger(MOD_ID);
  public static final Random RANDOM = new Random();

  /**
   * Gets a resource location for Tinkers
   * @param name  Resource path
   * @return  Location for tinkers
   */
  public static ResourceLocation getResource(String name) {
    return ResourceLocation.fromNamespaceAndPath(MOD_ID, name);
  }

  /**
   * Returns the given Resource prefixed with tinkers resource location. Use this function instead of hardcoding
   * resource locations.
   */
  public static String resourceString(String res) {
    return String.format("%s:%s", MOD_ID, res);
  }

  /**
   * Prefixes the given unlocalized name with tinkers prefix. Use this when passing unlocalized names for a uniform
   * namespace.
   */
  public static String prefix(String name) {
    return MOD_ID + "." + name.toLowerCase(Locale.US);
  }

  /** Makes a Tinker's description ID */
  public static String makeDescriptionId(String type, String name) {
    return type + "." + MOD_ID + "." + name;
  }

  /**
   * Makes a translation key for the given name
   * @param base  Base name, such as "block" or "gui"
   * @param name  Object name
   * @return  Translation key
   */
  public static String makeTranslationKey(String base, String name) {
    return net.minecraft.Util.makeDescriptionId(base, getResource(name));
  }

  /**
   * Makes a translation text component for the given name
   * @param base  Base name, such as "block" or "gui"
   * @param name  Object name
   * @return  Translation key
   */
  public static MutableComponent makeTranslation(String base, String name) {
    return Component.translatable(makeTranslationKey(base, name));
  }

  /**
   * Makes a translation text component for the given name
   * @param base       Base name, such as "block" or "gui"
   * @param name       Object name
   * @param arguments  Additional arguments to the translation
   * @return  Translation key
   */
  public static MutableComponent makeTranslation(String base, String name, Object... arguments) {
    return Component.translatable(makeTranslationKey(base, name), arguments);
  }

  /**
   * This function is called in the constructor in some internal classes that are a common target for addons to wrongly extend.
   * These classes will cause issues if blindly used by the addon, and are typically trivial for the addon to implement
   * the parts they need if they just put in some effort understanding the code they are copying.
   *
   * As a reminder for addon devs, anything that is not in the library package can and will change arbitrarily. If you need to use a feature outside library, request it on our github.
   * @param self  Class to validate
   */
  public static void sealTinkersClass(Object self, String base, String solution) {
    // note for future maintainers: this does not use Java 9's sealed classes as unless you use modules those are restricted to the same package.
    // Dumb restriction but not like we can change it.
    String name = self.getClass().getName();
    if (!name.startsWith("slimeknights.tconstruct.")) {
      throw new IllegalStateException(base + " being extended improperly by " + name + ". " + solution + ". For any questions, discuss with us on Discord or GitHub.");
    }
  }

  /** Returns the value, or the fallback when null. */
  public static <T> T defaultIfNull(@javax.annotation.Nullable T value, T fallback) {
    return value == null ? fallback : value;
  }
}
