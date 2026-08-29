package slimeknights.mantle.client.extensions;

import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRendering;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributes;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import slimeknights.mantle.transfer.fluid.FluidStack;

/**
 * Client-side render data for a fluid: which sprites draw it and what colour tints them. Shim for
 * {@code net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions}.
 *
 * <p>Forge hung these off the fluid type as a client-only extension object. Fabric splits the same
 * information across two registries — {@code FluidVariantRendering} for sprites and tint,
 * {@code FluidVariantAttributes} for the physical properties — and keys both on a
 * {@link FluidVariant} rather than a fluid. This class is the adapter: {@link #of(Fluid)} returns a
 * view bound to one fluid, and each accessor converts the stack it is given into a variant and asks
 * Fabric.
 *
 * <p>Going through Fabric's registries rather than a Tinkers-local table is the point: fluids from
 * other mods in the pack render correctly in Tinkers' tanks and buckets, and Tinkers' own fluids
 * render correctly in theirs. {@code slimeknights.mantle.transfer.fluid.FluidType} already doubles
 * as the {@code FluidVariantAttributeHandler} for Tinkers' fluids, so the two halves line up.
 *
 * <p>One difference worth naming: Forge's tint was ARGB and {@code -1} meant "untinted", while
 * Fabric's is opaque RGB. Forcing the alpha on maps white to {@code 0xFFFFFFFF}, which <em>is</em>
 * {@code -1}, so the untinted check the consumers already perform keeps working unchanged.
 */
public interface IClientFluidTypeExtensions {
  /** Gets the client extensions for the given fluid. */
  static IClientFluidTypeExtensions of(Fluid fluid) {
    return new FabricFluidRendering(fluid);
  }

  /** Texture of the fluid at rest, used for tank contents and bucket overlays. */
  ResourceLocation getStillTexture(FluidStack stack);

  /** Texture of the fluid in motion, used for the sides of a partially filled tank. */
  ResourceLocation getFlowingTexture(FluidStack stack);

  /** ARGB tint multiplied over the sprite, or {@code -1} for no tint. */
  int getTintColor(FluidStack stack);

  /** True if the fluid rises, which flips the model vertically. */
  boolean isLighterThanAir(FluidStack stack);

  /** Implementation backed by Fabric's fluid render/attribute registries. */
  record FabricFluidRendering(Fluid fluid) implements IClientFluidTypeExtensions {
    /** Index into {@code FluidVariantRendering.getSprites}: 0 is still, 1 is flowing. */
    private static final int STILL = 0;
    private static final int FLOWING = 1;

    /** Resolves the stack to a variant, falling back to this extension's own fluid when empty. */
    private FluidVariant variant(FluidStack stack) {
      return stack.isEmpty() ? FluidVariant.of(fluid) : stack.getVariant();
    }

    /**
     * Texture name for one of the fluid's two sprites.
     *
     * <p>A fluid that declared its own textures is answered from that data rather than from
     * Fabric's render handler. The handler only knows <em>baked</em> sprites, which do not exist
     * while item models bake — every filled bucket and can would bake against the missing texture
     * and stay that way until the next resource reload. The declared texture is a plain location
     * and is available whenever the data has loaded.
     */
    private ResourceLocation sprite(FluidStack stack, int index) {
      Fluid actual = stack.isEmpty() ? this.fluid : stack.getFluid();
      if (slimeknights.mantle.fluid.texture.FluidTextureManager.hasData(actual)) {
        return index == STILL
               ? slimeknights.mantle.fluid.texture.FluidTextureManager.getStillTexture(actual)
               : slimeknights.mantle.fluid.texture.FluidTextureManager.getFlowingTexture(actual);
      }
      TextureAtlasSprite[] sprites = FluidVariantRendering.getSprites(variant(stack));
      if (sprites == null || sprites.length <= index || sprites[index] == null) {
        return MissingTextureAtlasSprite.getLocation();
      }
      return sprites[index].contents().name();
    }

    @Override
    public ResourceLocation getStillTexture(FluidStack stack) {
      return sprite(stack, STILL);
    }

    @Override
    public ResourceLocation getFlowingTexture(FluidStack stack) {
      return sprite(stack, FLOWING);
    }

    @Override
    public int getTintColor(FluidStack stack) {
      // Fabric's colour has no alpha channel; forcing it opaque turns white into -1, which is
      // exactly the value the consumers test for to skip tinting
      return 0xFF000000 | FluidVariantRendering.getColor(variant(stack));
    }

    @Override
    public boolean isLighterThanAir(FluidStack stack) {
      return FluidVariantAttributes.isLighterThanAir(variant(stack));
    }
  }
}
