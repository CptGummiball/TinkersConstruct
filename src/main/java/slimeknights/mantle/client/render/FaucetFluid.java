package slimeknights.mantle.client.render;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

import javax.annotation.Nullable;

/**
 * Fluid boxes drawn inside the block a faucet or channel pours into, read from
 * {@code mantle/model/faucet_fluid}.
 *
 * <p>Written for this tree; Mantle's client packages were never copied in. Two forms exist in the
 * data: the pair of cuboids spelled out, or the {@code bottom} shorthand naming the height the
 * poured fluid reaches, which is what the channel entries use.
 *
 * @param side    Drawn when the pouring block is beside the receiver, matching a side faucet's spout
 * @param center  Drawn when the pouring block is directly above, matching a downwards faucet
 */
public record FaucetFluid(@Nullable FluidCuboid side, @Nullable FluidCuboid center) {
  /** Registry of receiving block fluids */
  public static final BlockStateDataMap<FaucetFluid> REGISTRY = new BlockStateDataMap<>(
    ResourceLocation.fromNamespaceAndPath("mantle", "faucet_fluid"), "mantle/model/faucet_fluid", FaucetFluid::fromJson);

  /** Registers the reload listener; Forge did this from {@code RegisterClientReloadListenersEvent} */
  public static void init() {
    REGISTRY.init();
  }

  /** Gets the cuboid to draw for fluid arriving from the given direction */
  @Nullable
  public FluidCuboid forDirection(Direction direction) {
    return direction == Direction.DOWN ? center : side;
  }

  /** Builds the pair of cuboids for a receiver whose fluid surface sits at the given height */
  public static FaucetFluid fromBottom(float bottom) {
    return new FaucetFluid(
      FluidCuboid.builder().from(6, bottom, 6).to(10, 16, 8)
                 .face(true, 0, Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST).build(),
      FluidCuboid.builder().from(6, bottom, 6).to(10, 16, 10)
                 .face(true, 0, Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST).build());
  }

  /** Parses one entry */
  public static FaucetFluid fromJson(JsonElement element) {
    JsonObject json = GsonHelper.convertToJsonObject(element, "faucet_fluid");
    if (json.has("bottom")) {
      return fromBottom(GsonHelper.getAsFloat(json, "bottom"));
    }
    return new FaucetFluid(
      json.has("side") ? FluidCuboid.fromJson(GsonHelper.getAsJsonObject(json, "side")) : null,
      json.has("center") ? FluidCuboid.fromJson(GsonHelper.getAsJsonObject(json, "center")) : null);
  }
}
