package slimeknights.mantle.client;

import net.minecraft.client.renderer.RenderType;

import javax.annotation.Nullable;

/**
 * Pair of render types (chunk pass and entity pass) a group of quads should draw with. Shim for
 * {@code net.minecraftforge.client.RenderTypeGroup}.
 *
 * <p>PORT: this concept collapses on Fabric, and the collapse is behaviour-preserving. Forge let a
 * <em>model</em> pick its render layer through the {@code "render_type"} key, so one baked model
 * could mix a solid and a translucent group. Fabric keeps the layer on the block —
 * {@code BlockRenderLayerMap} registers it per {@code Block}/{@code Fluid}, and item rendering
 * chooses between the solid and translucent atlas shaders from the model's own transparency — so
 * there is nothing per-model to carry. The type is kept because five geometry classes thread it
 * through their bake methods; every one of them ends up passing it to a builder that ignores it, so
 * dropping the plumbing would mean editing five call chains to no effect.
 *
 * <p>Tinkers' models declaring {@code "render_type": "minecraft:cutout"} get that layer from the
 * corresponding {@code BlockRenderLayerMap} registration in the module client-event classes, which
 * is where the Fabric equivalent lives.
 */
public record RenderTypeGroup(@Nullable RenderType block, @Nullable RenderType entity) {
  /** Group carrying no explicit types, meaning "let the renderer decide". */
  public static final RenderTypeGroup EMPTY = new RenderTypeGroup(null, null);

  /** Checks whether this group names any render type. */
  public boolean isEmpty() {
    return block == null && entity == null;
  }
}
