package slimeknights.mantle.client.render;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

/**
 * Fluid boxes for a channel block, read from {@code mantle/model/channel_fluids}.
 *
 * <p>Written for this tree; Mantle's client packages were never copied in. The seared and scorched
 * channel files in {@code assets/tconstruct} are both {@code {"parent": "tconstruct:channel"}}, so
 * the redirect handling in {@link BlockStateDataMap} is what makes them resolve.
 *
 * @param center  Pool in the middle of the channel, still or flowing
 * @param down    Column poured out of the bottom of the channel
 * @param side    Boxes for each connected side
 */
public record ChannelFluids(Center center, FluidCuboid down, Side side) {
  /** Registry of channel fluids per block */
  public static final BlockStateDataMap<ChannelFluids> REGISTRY = new BlockStateDataMap<>(
    ResourceLocation.fromNamespaceAndPath("mantle", "channel_fluids"), "mantle/model/channel_fluids", ChannelFluids::fromJson);

  /** Registers the reload listener; Forge did this from {@code RegisterClientReloadListenersEvent} */
  public static void init() {
    REGISTRY.init();
  }

  /** Gets the center box, flowing when the channel is moving fluid sideways */
  public FluidCuboid center(boolean flowing) {
    return flowing ? center.flowing() : center.still();
  }

  /** Center of the channel */
  public record Center(FluidCuboid still, FluidCuboid flowing) {}

  /** One side of the channel */
  public record Side(FluidCuboid still, FluidCuboid in, FluidCuboid out, FluidCuboid edge) {
    /** Gets the box for fluid moving through this side */
    public FluidCuboid flow(boolean out) {
      return out ? this.out : this.in;
    }
  }

  /** Parses one entry */
  public static ChannelFluids fromJson(JsonElement element) {
    JsonObject json = GsonHelper.convertToJsonObject(element, "channel_fluids");
    JsonObject center = GsonHelper.getAsJsonObject(json, "center");
    JsonObject side = GsonHelper.getAsJsonObject(json, "side");
    return new ChannelFluids(
      new Center(FluidCuboid.fromJson(GsonHelper.getAsJsonObject(center, "still")),
                 FluidCuboid.fromJson(GsonHelper.getAsJsonObject(center, "flowing"))),
      FluidCuboid.fromJson(GsonHelper.getAsJsonObject(json, "down")),
      new Side(FluidCuboid.fromJson(GsonHelper.getAsJsonObject(side, "still")),
               FluidCuboid.fromJson(GsonHelper.getAsJsonObject(side, "in")),
               FluidCuboid.fromJson(GsonHelper.getAsJsonObject(side, "out")),
               FluidCuboid.fromJson(GsonHelper.getAsJsonObject(side, "edge"))));
  }
}
