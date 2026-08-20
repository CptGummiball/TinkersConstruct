package slimeknights.tconstruct.fabric.mixin.client;

import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
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
