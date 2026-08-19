package slimeknights.mantle.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRendering;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.InventoryMenu;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import slimeknights.mantle.client.render.FluidCuboid.FluidFace;
import slimeknights.mantle.transfer.fluid.FluidStack;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Draws {@link FluidCuboid} boxes into a vertex buffer.
 *
 * <p>Written for this tree; Mantle's client packages were never copied in. Two Forge dependencies
 * had to be replaced:
 * <ul>
 *   <li>Sprites, tint and the gas flag came from {@code IClientFluidTypeExtensions}, which has no
 *   Fabric counterpart. They come from {@link FluidVariantRendering} and {@link FluidVariantAttributes}
 *   here; Fabric's tint is opaque RGB so alpha is forced on, matching the screen side in
 *   {@code GuiUtil}.</li>
 *   <li>{@code VertexConsumer} was renamed wholesale in 1.21: {@code vertex/color/uv/uv2/endVertex}
 *   became {@code addVertex/setColor/setUv/setLight} with no explicit end.</li>
 * </ul>
 *
 * <p>Texture coordinates are taken from the position within the block so fluid tiles across the
 * faces of neighbouring cuboids, which is what lets the smeltery draw one fluid over many blocks.
 */
public final class FluidRenderer {
  private FluidRenderer() {}

  /** Fraction of the flowing sprite used per block, matching vanilla's flowing fluid quads */
  private static final float FLOWING_SCALE = 0.5f;

  /* Fluid properties */

  // PORT: Tinkers' own fluids have no sprites until something registers them with Fabric's
  //   FluidRenderHandlerRegistry. On Forge that came from mantle.fluid.texture.ClientTextureFluidType
  //   reading the generated mantle/fluid_texture data; that package is still parked, so until it
  //   lands these renderers draw nothing for molten fluids (vanilla fluids already have handlers).
  //   Every lookup below is null safe so the gap shows as missing fluid, not as a crash.

  /** Gets the still sprite for a fluid, or null if the fluid has no sprites */
  @Nullable
  public static TextureAtlasSprite getStillSprite(FluidStack fluid) {
    return FluidVariantRendering.getSprite(fluid.getVariant());
  }

  /** Gets the flowing sprite for a fluid, falling back to the still sprite */
  @Nullable
  public static TextureAtlasSprite getFlowingSprite(FluidStack fluid) {
    TextureAtlasSprite[] sprites = FluidVariantRendering.getSprites(fluid.getVariant());
    if (sprites != null && sprites.length > 1 && sprites[1] != null) {
      return sprites[1];
    }
    return getStillSprite(fluid);
  }

  /** Gets a sprite from the block atlas */
  public static TextureAtlasSprite getBlockSprite(ResourceLocation texture) {
    return Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(texture);
  }

  /** Gets the tint color for a fluid; Fabric returns opaque RGB so alpha is forced on */
  public static int getColor(FluidStack fluid) {
    return 0xFF000000 | FluidVariantRendering.getColor(fluid.getVariant());
  }

  /** If true, the fluid floats and so fills its container from the top */
  public static boolean isGas(FluidStack fluid) {
    return FluidVariantAttributes.isLighterThanAir(fluid.getVariant());
  }

  /** Gets the light emitted by a fluid, 0 to 15 */
  public static int getLuminosity(FluidStack fluid) {
    return FluidVariantAttributes.getLuminance(fluid.getVariant());
  }

  /** Raises the block light of the given packed light to at least the given light level */
  public static int withBlockLight(int light, int lightLevel) {
    if (lightLevel == 0) {
      return light;
    }
    return (light & 0xFFFF0000) | Math.max(light & 0xFFFF, lightLevel << 4);
  }


  /* Cuboids */

  /**
   * Renders a list of cuboids filled with the given fluid.
   * @param matrices  Matrix stack instance
   * @param buffer    Vertex buffer, typically {@link MantleRenderTypes#FLUID}
   * @param cubes     Cuboids to render
   * @param fluid     Fluid to fill them with
   * @param light     Packed light
   */
  public static void renderCuboids(PoseStack matrices, VertexConsumer buffer, List<FluidCuboid> cubes, FluidStack fluid, int light) {
    if (fluid.isEmpty() || cubes.isEmpty()) {
      return;
    }
    TextureAtlasSprite still = getStillSprite(fluid);
    if (still == null) {
      return;
    }
    TextureAtlasSprite flowing = getFlowingSprite(fluid);
    int color = getColor(fluid);
    int fluidLight = withBlockLight(light, getLuminosity(fluid));
    boolean gas = isGas(fluid);
    for (FluidCuboid cube : cubes) {
      renderCuboid(matrices, buffer, cube, still, flowing, cube.getFromScaled(), cube.getToScaled(), color, fluidLight, gas);
    }
  }

  /**
   * Renders a single cuboid, offsetting it vertically by the given amount in sixteenths.
   * @param yOffset  Offset in sixteenths of a block, 0 for the cuboid's own position
   */
  public static void renderCuboid(PoseStack matrices, VertexConsumer buffer, FluidCuboid cube, int yOffset,
                                  @Nullable TextureAtlasSprite still, @Nullable TextureAtlasSprite flowing, int color, int light, boolean flipGas) {
    if (still == null) {
      return;
    }
    Vector3f from = cube.getFromScaled();
    Vector3f to = cube.getToScaled();
    if (yOffset != 0) {
      float offset = yOffset / 16f;
      from = new Vector3f(from.x(), from.y() + offset, from.z());
      to = new Vector3f(to.x(), to.y() + offset, to.z());
    }
    renderCuboid(matrices, buffer, cube, still, flowing, from, to, color, light, flipGas);
  }

  /**
   * Renders a single cuboid between the given block scale positions.
   * @param cube     Cuboid providing the faces to draw
   * @param from     Minimum corner in block coordinates
   * @param to       Maximum corner in block coordinates
   * @param flipGas  If true, the fluid is a gas so vertical textures are flipped
   */
  public static void renderCuboid(PoseStack matrices, VertexConsumer buffer, FluidCuboid cube,
                                  @Nullable TextureAtlasSprite still, @Nullable TextureAtlasSprite flowing,
                                  Vector3f from, Vector3f to, int color, int light, boolean flipGas) {
    if (still == null || cube.isEmpty()) {
      return;
    }
    Matrix4f matrix = matrices.last().pose();
    for (Direction direction : Direction.values()) {
      FluidFace face = cube.getFace(flipGas ? flipFace(direction) : direction);
      if (face != null) {
        int rotation = face.rotation();
        if (flipGas && direction.getAxis().isHorizontal()) {
          rotation += 180;
        }
        putTexturedQuad(buffer, matrix, face.flowing() ? (flowing != null ? flowing : still) : still,
                        from, to, direction, color, light, rotation, face.flowing());
      }
    }
  }

  /** Swaps up and down for gasses, which hang from the top of their container */
  private static Direction flipFace(Direction direction) {
    return direction.getAxis() == Direction.Axis.Y ? direction.getOpposite() : direction;
  }

  /**
   * Renders a cuboid scaled to how full its tank is.
   * @param offset    Extra amount to add to the fluid, used by the tank fill animation
   * @param capacity  Tank capacity, the cuboid is full at this amount
   * @param flipGas   If true, gasses fill from the top down instead of the bottom up
   */
  public static void renderScaledCuboid(PoseStack matrices, MultiBufferSource buffer, FluidCuboid cube, FluidStack fluid,
                                        float offset, int capacity, int light, boolean flipGas) {
    if (fluid.isEmpty() || capacity <= 0) {
      return;
    }
    TextureAtlasSprite still = getStillSprite(fluid);
    if (still == null) {
      return;
    }
    // scale the cuboid vertically by how full the tank is
    float percent = Math.min(1f, Math.max(0f, (fluid.getAmount() + offset) / capacity));
    Vector3f from = cube.getFromScaled();
    Vector3f to = cube.getToScaled();
    float minY = from.y();
    float maxY = to.y();
    float height = (maxY - minY) * percent;
    boolean gas = isGas(fluid);
    if (gas && flipGas) {
      minY = maxY - height;
    } else {
      maxY = minY + height;
    }
    renderCuboid(matrices, buffer.getBuffer(MantleRenderTypes.FLUID), cube, still, getFlowingSprite(fluid),
                 new Vector3f(from.x(), minY, from.z()), new Vector3f(to.x(), maxY, to.z()),
                 getColor(fluid), withBlockLight(light, getLuminosity(fluid)), gas && flipGas);
  }


  /* Quads */

  /**
   * Adds a single textured quad to the buffer.
   *
   * <p>Public as the smeltery draws its own multi block fluid rather than a {@link FluidCuboid}.
   *
   * @param face      Side of the cuboid to draw
   * @param rotation  Texture rotation in degrees, a multiple of 90
   * @param flowing   If true, the sprite is a flowing texture and is drawn at half scale
   */
  public static void putTexturedQuad(VertexConsumer buffer, Matrix4f matrix, TextureAtlasSprite sprite,
                                     Vector3f from, Vector3f to, Direction face, int color, int light, int rotation, boolean flowing) {
    float x1 = from.x(), y1 = from.y(), z1 = from.z();
    float x2 = to.x(), y2 = to.y(), z2 = to.z();

    // texture coordinates are relative to the block the face starts in, so fluid tiles across blocks
    float[][] positions;
    float[][] uvs;
    switch (face) {
      case DOWN -> {
        float uMin = local(x1), uMax = uMin + (x2 - x1);
        float vMin = local(z1), vMax = vMin + (z2 - z1);
        positions = new float[][] {{x1, y1, z1}, {x2, y1, z1}, {x2, y1, z2}, {x1, y1, z2}};
        uvs = new float[][] {{uMin, vMin}, {uMax, vMin}, {uMax, vMax}, {uMin, vMax}};
      }
      case UP -> {
        float uMin = local(x1), uMax = uMin + (x2 - x1);
        float vMin = local(z1), vMax = vMin + (z2 - z1);
        positions = new float[][] {{x1, y2, z1}, {x1, y2, z2}, {x2, y2, z2}, {x2, y2, z1}};
        uvs = new float[][] {{uMin, vMin}, {uMin, vMax}, {uMax, vMax}, {uMax, vMin}};
      }
      case NORTH -> {
        float uMin = local(x1), uMax = uMin + (x2 - x1);
        float vBottom = 1 - local(y1), vTop = vBottom - (y2 - y1);
        positions = new float[][] {{x1, y1, z1}, {x1, y2, z1}, {x2, y2, z1}, {x2, y1, z1}};
        uvs = new float[][] {{uMin, vBottom}, {uMin, vTop}, {uMax, vTop}, {uMax, vBottom}};
      }
      case SOUTH -> {
        float uMin = local(x1), uMax = uMin + (x2 - x1);
        float vBottom = 1 - local(y1), vTop = vBottom - (y2 - y1);
        positions = new float[][] {{x1, y1, z2}, {x2, y1, z2}, {x2, y2, z2}, {x1, y2, z2}};
        uvs = new float[][] {{uMin, vBottom}, {uMax, vBottom}, {uMax, vTop}, {uMin, vTop}};
      }
      case WEST -> {
        float uMin = local(z1), uMax = uMin + (z2 - z1);
        float vBottom = 1 - local(y1), vTop = vBottom - (y2 - y1);
        positions = new float[][] {{x1, y1, z1}, {x1, y1, z2}, {x1, y2, z2}, {x1, y2, z1}};
        uvs = new float[][] {{uMin, vBottom}, {uMax, vBottom}, {uMax, vTop}, {uMin, vTop}};
      }
      case EAST -> {
        float uMin = local(z1), uMax = uMin + (z2 - z1);
        float vBottom = 1 - local(y1), vTop = vBottom - (y2 - y1);
        positions = new float[][] {{x2, y1, z1}, {x2, y2, z1}, {x2, y2, z2}, {x2, y1, z2}};
        uvs = new float[][] {{uMin, vBottom}, {uMin, vTop}, {uMax, vTop}, {uMax, vBottom}};
      }
      default -> {
        return;
      }
    }

    // rotate the texture by shifting which corner each UV belongs to
    int shift = Math.floorMod(rotation / 90, 4);

    int alpha = (color >> 24) & 0xFF;
    if (alpha == 0) {
      alpha = 0xFF;
    }
    int red = (color >> 16) & 0xFF;
    int green = (color >> 8) & 0xFF;
    int blue = color & 0xFF;

    for (int i = 0; i < 4; i++) {
      float[] position = positions[i];
      float[] uv = uvs[(i + shift) % 4];
      float u = uv[0];
      float v = uv[1];
      if (flowing) {
        // the flowing texture animates over the whole sprite, so only its middle half is used
        u = 0.25f + (u * FLOWING_SCALE);
        v = 0.25f + (v * FLOWING_SCALE);
      }
      buffer.addVertex(matrix, position[0], position[1], position[2])
            .setColor(red, green, blue, alpha)
            .setUv(sprite.getU(clamp(u)), sprite.getV(clamp(v)))
            .setLight(light);
    }
  }

  /** Gets the position within its block, so textures tile across block borders */
  private static float local(float value) {
    return value - Mth.floor(value);
  }

  /** 1.21 sprite UVs take a 0 to 1 fraction rather than a 0 to 16 coordinate */
  private static float clamp(float value) {
    return value < 0 ? 0 : Math.min(value, 1);
  }
}
