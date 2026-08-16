package slimeknights.tconstruct.library.utils;

import net.fabricmc.fabric.api.event.lifecycle.v1.CommonLifecycleEvents;
import net.minecraft.locale.Language;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.commons.lang3.text.WordUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Logic to get the display name for a resource domain
 */
public class DomainDisplayName {
  private DomainDisplayName() {}

  /** Map of domain name to display name */
  private static final Map<String,String> DISPLAY_NAME_LOOKUP = new HashMap<>();
  /** Cached pattern for matching a dash or underscore */
  private static final Pattern DASH_UNDERSCORE = Pattern.compile("[_-]");
  /** Reload listener to clear names on resource pack reload */

  /**
   * Formats a domain name into title case. For example, "my_pack" becomes "My Pack"
   * @param domain  Domain name to format
   * @return  Formatted domain name
   */
  private static String formatDomainName(String domain) {
    return WordUtils.capitalize(DASH_UNDERSCORE.matcher(domain).replaceAll(" "));
  }

  /** Gets the name for a mod ID, uncached */
  private static String nameForUncached(String domain) {
    // first, check if the resource pack translated the thing
    String langKey = "domain." + domain + ".display_name";
    String translated = Language.getInstance().getOrDefault(langKey);
    if (!translated.equals(langKey)) {
      return translated;
    }

    // that failed? try a mod container lookup
    return FabricLoader.getInstance().getModContainer(domain)
                  .map(container -> container.getMetadata().getName())
                  .orElseGet(() -> formatDomainName(domain));
  }

  /**
   * Gets the name for a resource domain
   * @param domain  Resource domain
   * @return Display name
   */
  public static String nameFor(String domain) {
    return DISPLAY_NAME_LOOKUP.computeIfAbsent(domain, DomainDisplayName::nameForUncached);
  }

  /**
   * Registers cache invalidation. Forge cleared through a client resource reload listener;
   * the language table this cache reads refreshes on tag/datapack reloads too, and the tags
   * event fires on both sides, so it covers resource pack changes without client-only API.
   */
  public static void init() {
    CommonLifecycleEvents.TAGS_LOADED.register((registries, client) -> DISPLAY_NAME_LOOKUP.clear());
  }
}
