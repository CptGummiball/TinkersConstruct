package slimeknights.mantle.client.book.data;

import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;

/**
 * Everything about how a book looks, read from {@code appearance.json} at the book root.
 *
 * <p>Field names are the JSON keys. Colours are packed RGB and accept both a plain number and an
 * unquoted {@code 0x} literal, which is how the shipped files write them.
 */
public class BookAppearance {
  /** Texture of the closed book shown before the first page */
  @Nullable
  public ResourceLocation coverTexture = null;
  /** Tint applied to the cover */
  public int coverColor = -1;
  /** Texture holding the open book frame, the paper and the arrow sprites */
  @Nullable
  public ResourceLocation bookTexture = null;
  /** Tint applied to the page-turn arrows */
  public int arrowColor = 0xFFFFFF;
  /** Tint applied to the page-turn arrows while hovered */
  public int arrowColorHover = 0xFFDD00;
  /** Tint applied to item slot backgrounds drawn by pages */
  public int slotColor = 0xFFFFFF;
  /** Highlight drawn behind a hovered index icon */
  public int hoverColor = 0xFFFFFF;
  /** Tint of the structure page's rotate and layer buttons */
  public int structureButtonColor = 0xFFFFFF;
  /** Tint of those buttons while hovered */
  public int structureButtonColorHovered = 0xFFDD00;
  /** Colour page text draws in */
  public int textColor = 0x000000;
  /** Whether the index page lists section names as text */
  public boolean drawSectionListText = true;
  /** Whether page titles render at the larger size */
  public boolean largePageTitles = false;
  /** Whether page titles are centred */
  public boolean centerPageTitles = true;
  /** Whether the section index splits into four columns across the spread rather than two */
  public boolean drawFourColumnIndex = false;
  /** Title used by the HTML export */
  @Nullable
  public String exportTitle = null;
}
