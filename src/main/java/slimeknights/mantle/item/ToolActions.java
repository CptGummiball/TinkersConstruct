package slimeknights.mantle.item;

/**
 * The {@link ToolAction} constants Tinkers uses, mirroring Forge's {@code ToolActions}.
 *
 * <p>Only the actions actually referenced are declared — the four Tinkers-specific ones at
 * the bottom never existed in Forge and are defined here for the same reason Forge defined
 * the rest: so tools and modifiers can agree on a name.
 */
public final class ToolActions {

  private ToolActions() {}

  /* Digging */
  public static final ToolAction PICKAXE_DIG = ToolAction.get("pickaxe_dig");
  public static final ToolAction SHOVEL_DIG = ToolAction.get("shovel_dig");
  public static final ToolAction AXE_DIG = ToolAction.get("axe_dig");
  public static final ToolAction HOE_DIG = ToolAction.get("hoe_dig");
  public static final ToolAction SWORD_DIG = ToolAction.get("sword_dig");
  public static final ToolAction SHEARS_DIG = ToolAction.get("shears_dig");

  /* Block interactions */
  public static final ToolAction AXE_STRIP = ToolAction.get("axe_strip");
  public static final ToolAction AXE_SCRAPE = ToolAction.get("axe_scrape");
  public static final ToolAction AXE_WAX_OFF = ToolAction.get("axe_wax_off");
  public static final ToolAction SHOVEL_FLATTEN = ToolAction.get("shovel_flatten");
  public static final ToolAction HOE_TILL = ToolAction.get("hoe_till");

  /* Shears */
  public static final ToolAction SHEARS_HARVEST = ToolAction.get("shears_harvest");
  public static final ToolAction SHEARS_CARVE = ToolAction.get("shears_carve");
  public static final ToolAction SHEARS_DISARM = ToolAction.get("shears_disarm");

  /* Misc vanilla-equivalent */
  public static final ToolAction SHIELD_BLOCK = ToolAction.get("shield_block");
  public static final ToolAction FISHING_ROD_CAST = ToolAction.get("fishing_rod_cast");

  /* Tinkers-specific, no Forge counterpart */
  public static final ToolAction SHIELD_DISABLE = ToolAction.get("shield_disable");
  public static final ToolAction DRILL_ATTACK = ToolAction.get("drill_attack");
  public static final ToolAction GRAPPLE_HOOK = ToolAction.get("grapple_hook");
  public static final ToolAction ITEM_HOOK = ToolAction.get("item_hook");
}
