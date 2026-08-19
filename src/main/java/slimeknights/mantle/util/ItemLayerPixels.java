package slimeknights.mantle.util;

/**
 * Record of which sprite pixels an already-generated item layer covers, so a later layer can skip
 * them instead of drawing a second quad in the same place.
 *
 * <p>PORT: not implemented, and nothing reachable needs it yet. The z-fighting it prevents only
 * happens when several material layers stack in one item model, which is {@code ToolModel} — still
 * parked on the modifier model tree. It exists as a type because
 * {@link slimeknights.tconstruct.library.client.model.tools.MaterialModel#getQuadsForMaterial} takes
 * one, and that method is the seam {@code ToolModel} calls into; a real implementation belongs with
 * that model, since only then is there a way to tell whether the suppression is correct.
 *
 * <p>{@link slimeknights.mantle.client.model.util.MantleItemLayerModel} states plainly where it
 * ignores the argument.
 */
public class ItemLayerPixels {
}
