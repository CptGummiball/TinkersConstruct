package slimeknights.mantle.util;

/**
 * Record of which sprite pixels an already-generated item layer covers, so a later layer can skip
 * them instead of drawing a second quad in the same place.
 *
 * <p><b>Inert on this port, and deliberately so.</b> Upstream traced each item layer's silhouette
 * itself, which gave it a per-pixel front face it could trim against this record.
 * {@link slimeknights.mantle.client.model.util.MantleItemLayerModel} instead feeds the sprite to
 * vanilla's {@link net.minecraft.client.renderer.block.model.ItemModelGenerator} — the same code
 * behind {@code builtin/generated} — and that emits <i>one full-size quad</i> for the front and one
 * for the back, letting the texture's own alpha cut the shape at render time. Only the side faces
 * follow the silhouette. There is therefore no per-pixel front face to trim: the thing this record
 * would suppress does not exist as separate geometry.
 *
 * <p>Nor is the suppression needed for a correct picture. Overlapping layers are coplanar, so under
 * {@code GL_LEQUAL} depth testing the fragment drawn last wins, and {@link ReversedListBuilder}
 * exists precisely to guarantee that order: the tool model assembles layers top down so each one
 * could consult this record, then emits them bottom up, so the topmost layer draws last. An upper
 * layer that is partly transparent blends over the layer below, which is what it should do. What is
 * lost is only overdraw — a tool with many modifiers bakes more quads than upstream would. Bakes
 * are cached per material and modifier combination, so this is paid once.
 *
 * <p>The type stays because it is threaded through fifteen model signatures, and because restoring
 * the suppression means replacing the vanilla generator with a span tracer of our own — a change
 * that would touch every item layer in the mod, not just the tool.
 */
public class ItemLayerPixels {
}
