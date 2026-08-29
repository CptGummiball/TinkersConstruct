package slimeknights.tconstruct.library;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import slimeknights.mantle.client.render.RenderItem;
import slimeknights.tconstruct.TConstruct;

/**
 * Display contexts used by the Tinkers' block entity renderers.
 *
 * <p>Fabric port: Forge let mods add values to {@link ItemDisplayContext} through a registry, so a
 * model could declare a {@code tconstruct:melter} display and be posed for the melter specifically.
 * Fabric has no such hook — the enum is closed — so each context becomes the vanilla one Forge
 * declared as its fallback, and the ids stay registered with {@link RenderItem} so the generated
 * {@code item_lists} data still resolves.
 *
 * <p>The visible loss is per context model tuning: an item shown in the melter now uses the same
 * pose as any other {@link ItemDisplayContext#NONE} render rather than a melter specific one. The
 * placement itself (position, scale, rotation) comes from the item list data and is unaffected.
 */
public class TinkerItemDisplays {
  private TinkerItemDisplays() {}

  /* Ids, used by the item list datagen to name the transform in JSON */
  public static final ResourceLocation MELTER_ID = TConstruct.getResource("melter");
  public static final ResourceLocation TABLE_ID = TConstruct.getResource("table");
  public static final ResourceLocation CASTING_TABLE_ID = TConstruct.getResource("casting_table");
  public static final ResourceLocation CASTING_BASIN_ID = TConstruct.getResource("casting_basin");
  public static final ResourceLocation FLUID_CANNON_ID = TConstruct.getResource("fluid_cannon");
  public static final ResourceLocation THROWN_ID = TConstruct.getResource("thrown");

  /** Used by the melter and smeltery for display of items its melting */
  public static final ItemDisplayContext MELTER = create("melter", ItemDisplayContext.NONE);
  /** Used by the part builder, crafting station, tinkers station, and tinker anvil */
  public static final ItemDisplayContext TABLE = create("table", ItemDisplayContext.NONE);
  /** Used by the casting table for item rendering */
  public static final ItemDisplayContext CASTING_TABLE = create("casting_table", ItemDisplayContext.FIXED);
  /** Used by the casting basin for item rendering */
  public static final ItemDisplayContext CASTING_BASIN = create("casting_basin", ItemDisplayContext.NONE);
  /** Used by the fluid cannon for display of the item in front */
  public static final ItemDisplayContext FLUID_CANNON = create("fluid_cannon", ItemDisplayContext.FIXED);
  /** Used by throwing to allow adjusting the tool position */
  public static final ItemDisplayContext THROWN = create("thrown", ItemDisplayContext.FIXED);

  /**
   * Loads this class so the ids above are known to {@link RenderItem}; safe to call repeatedly.
   * Forge instead added a listener to the mod event bus from the mod constructor.
   */
  public static void init() {}

  /** Registers the id under which the display appears in item list data, returning its behaviour */
  private static ItemDisplayContext create(String name, ItemDisplayContext fallback) {
    RenderItem.registerContext(TConstruct.getResource(name), fallback);
    return fallback;
  }
}
