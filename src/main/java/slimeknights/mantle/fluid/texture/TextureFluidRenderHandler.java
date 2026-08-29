package slimeknights.mantle.fluid.texture;

import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandler;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.FluidState;
import slimeknights.mantle.Mantle;

import javax.annotation.Nullable;

/**
 * Renders a fluid from the textures it declared in {@code mantle/fluid_texture}.
 *
 * <p>Forge read those through a client fluid-type extension; Fabric asks a render handler for
 * sprites and a tint instead. Sprites resolve per call rather than being cached, so a resource
 * reload is picked up without re-registering anything.
 */
public class TextureFluidRenderHandler implements FluidRenderHandler {
  private static final TextureFluidRenderHandler INSTANCE = new TextureFluidRenderHandler();

  private static boolean registered = false;

  private TextureFluidRenderHandler() {}

  /**
   * Registers the handler for every fluid that declared textures.
   *
   * <p>Runs after the first resource load, since which fluids have textures is only known once
   * the manager has read them; the registry is global so registering once is enough.
   */
  public static void register() {
    if (registered) {
      return;
    }
    registered = true;
    int count = 0;
    for (Fluid fluid : BuiltInRegistries.FLUID) {
      if (FluidTextureManager.hasData(fluid)) {
        FluidRenderHandlerRegistry.INSTANCE.register(fluid, INSTANCE);
        count++;
      }
    }
    Mantle.logger.info("Registered fluid rendering for {} fluids", count);
  }

  /** Looks a sprite up on the block atlas */
  private static TextureAtlasSprite sprite(ResourceLocation texture) {
    return Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(texture);
  }

  /** Textures are declared on the source fluid, so flowing variants resolve through it */
  private static Fluid source(Fluid fluid) {
    return fluid instanceof FlowingFluid flowing ? flowing.getSource() : fluid;
  }

  @Override
  public TextureAtlasSprite[] getFluidSprites(@Nullable BlockAndTintGetter view, @Nullable BlockPos pos, FluidState state) {
    FluidTexture texture = FluidTextureManager.getData(source(state.getType()));
    ResourceLocation overlay = texture.overlay();
    if (overlay != null) {
      return new TextureAtlasSprite[] { sprite(texture.still()), sprite(texture.flowing()), sprite(overlay) };
    }
    return new TextureAtlasSprite[] { sprite(texture.still()), sprite(texture.flowing()) };
  }

  @Override
  public int getFluidColor(@Nullable BlockAndTintGetter view, @Nullable BlockPos pos, FluidState state) {
    return FluidTextureManager.getData(source(state.getType())).color();
  }
}
