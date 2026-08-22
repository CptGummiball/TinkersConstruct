package slimeknights.tconstruct.fabric.mixin.client;

import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import slimeknights.mantle.client.model.geometry.GeometryUnbakedModel;

import java.util.function.Function;

/**
 * Lets a model inherit its custom geometry from its parent.
 *
 * <p>A model that only overrides textures — {@code {"parent": "tconstruct:block/template/tank",
 * "textures": {...}}} — is what most of Tinkers' blocks actually are, and vanilla bakes it through
 * its own {@code BlockModel.bake}, which knows nothing about the parent's loader. 175 models in the
 * mod are shaped that way, including every tank and gauge and 68 tool pose variants; without this
 * they bake from the template's static elements and the loader never runs. Forge patched
 * {@code BlockModel.bake} to check for geometry first, for exactly the same reason.
 *
 * <p>The geometry comes from the ancestor, but the baking context is built from the model being
 * baked, so the child's own textures and display transforms are the ones used.
 */
@Mixin(BlockModel.class)
public abstract class BlockModelInheritedGeometryMixin {
  @Shadow
  protected BlockModel parent;

  /**
   * Finishes parent resolution for geometry ancestors.
   *
   * <p>Vanilla resolves parents only for top-level models (those a blockstate or item points
   * at); a template is only ever someone's parent, so its own {@code resolveParents} — which
   * is what links the wrapped vanilla parse to <em>its</em> parent ({@code block/block} and
   * the display transforms that live there) — never runs. The vanilla chain walk also stops
   * at the wrapper, whose own parent link is pre-filled. So after a model resolves, resolve
   * any geometry ancestors it reached; the call is idempotent.
   */
  @Inject(method = "resolveParents(Ljava/util/function/Function;)V", at = @At("TAIL"))
  private void tconstruct$resolveGeometryAncestors(Function<ResourceLocation,UnbakedModel> modelGetter, CallbackInfo callback) {
    for (BlockModel ancestor = this.parent; ancestor != null; ancestor = ((BlockModelInheritedGeometryMixin)(Object)ancestor).parent) {
      if (ancestor instanceof GeometryUnbakedModel geometry) {
        geometry.resolveParents(modelGetter);
      }
    }
  }

  @Inject(
    method = "bake(Lnet/minecraft/client/resources/model/ModelBaker;Ljava/util/function/Function;Lnet/minecraft/client/resources/model/ModelState;)Lnet/minecraft/client/resources/model/BakedModel;",
    at = @At("HEAD"), cancellable = true)
  private void tconstruct$bakeInheritedGeometry(ModelBaker baker, Function<Material,TextureAtlasSprite> spriteGetter, ModelState state, CallbackInfoReturnable<BakedModel> callback) {
    // the geometry model bakes through its own override; only its children come through here
    BlockModel self = (BlockModel)(Object)this;
    if (self instanceof GeometryUnbakedModel) {
      return;
    }
    for (BlockModel ancestor = this.parent; ancestor != null; ancestor = ((BlockModelInheritedGeometryMixin)(Object)ancestor).parent) {
      if (ancestor instanceof GeometryUnbakedModel geometry) {
        callback.setReturnValue(geometry.bakeChild(self, baker, spriteGetter, state));
        return;
      }
    }
  }
}
