package slimeknights.tconstruct.shared;

import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import slimeknights.tconstruct.common.ClientEventBase;
import slimeknights.tconstruct.library.client.book.TinkerBook;
import slimeknights.tconstruct.shared.client.FluidParticle;

/**
 * Client half of the shared module.
 *
 * <p>Fabric port: the model loader and the resource listener that lived here register from the
 * client entrypoint instead, alongside the other modules' — see {@code TinkerModelLoaders} and
 * {@code DomainDisplayName}. What is left is what only this module knows: the font the books are
 * set in, and the particle that draws a fluid.
 */
public class CommonsClientEvents extends ClientEventBase {
  /** Wires up the pieces that have no home elsewhere */
  public static void init() {
    setBookFont();
    ParticleFactoryRegistry.getInstance().register(TinkerCommons.fluidParticle.get(), new FluidParticle.Factory());
  }

  /**
   * Sets every book in the unicode font.
   *
   * <p>The books are written in eight languages and their pages are laid out against a fixed page
   * width; the default font's per-glyph widths differ enough between scripts to break that layout,
   * while the unicode font is uniform. Upstream made the same choice for the same reason.
   */
  private static void setBookFont() {
    Font unicode = unicodeFontRender();
    TinkerBook.MATERIALS_AND_YOU.fontRenderer = unicode;
    TinkerBook.TINKERS_GADGETRY.fontRenderer = unicode;
    TinkerBook.PUNY_SMELTING.fontRenderer = unicode;
    TinkerBook.MIGHTY_SMELTING.fontRenderer = unicode;
    TinkerBook.FANTASTIC_FOUNDRY.fontRenderer = unicode;
    TinkerBook.ENCYCLOPEDIA.fontRenderer = unicode;
  }

  private static Font unicodeRenderer;

  /** Gets the unicode font renderer */
  public static Font unicodeFontRender() {
    if (unicodeRenderer == null) {
      unicodeRenderer = new Font(rl -> Minecraft.getInstance().fontManager.fontSets.get(Minecraft.UNIFORM_FONT), false);
    }
    return unicodeRenderer;
  }
}
