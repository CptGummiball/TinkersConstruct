package slimeknights.mantle.datagen;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

/**
 * Shim for Forge's {@code Tags} constant class, covering the constants this codebase's
 * datagen actually references. Forge's {@code forge:} namespace became the {@code c:}
 * convention on Fabric; the paths are unchanged, matching how the data migration rewrote
 * every existing tag reference, so datagen output lines up with the shipped data files.
 */
@SuppressWarnings("unused")  // API
public class Tags {

  private static <T> TagKey<T> tag(ResourceKey<? extends Registry<T>> registry, String path) {
    return TagKey.create(registry, ResourceLocation.fromNamespaceAndPath("c", path));
  }

  public static class Items {
    public static final TagKey<Item> ARMORS = tag(Registries.ITEM, "armors");
    public static final TagKey<Item> ARMORS_BOOTS = tag(Registries.ITEM, "armors/boots");
    public static final TagKey<Item> ARMORS_CHESTPLATES = tag(Registries.ITEM, "armors/chestplates");
    public static final TagKey<Item> ARMORS_HELMETS = tag(Registries.ITEM, "armors/helmets");
    public static final TagKey<Item> ARMORS_LEGGINGS = tag(Registries.ITEM, "armors/leggings");
    public static final TagKey<Item> BONES = tag(Registries.ITEM, "bones");
    public static final TagKey<Item> CHESTS_WOODEN = tag(Registries.ITEM, "chests/wooden");
    public static final TagKey<Item> COBBLESTONE = tag(Registries.ITEM, "cobblestones");
    public static final TagKey<Item> CROPS_CARROT = tag(Registries.ITEM, "crops/carrot");
    public static final TagKey<Item> DUSTS_GLOWSTONE = tag(Registries.ITEM, "dusts/glowstone");
    public static final TagKey<Item> DUSTS_PRISMARINE = tag(Registries.ITEM, "dusts/prismarine");
    public static final TagKey<Item> DUSTS_REDSTONE = tag(Registries.ITEM, "dusts/redstone");
    public static final TagKey<Item> DYES = tag(Registries.ITEM, "dyes");
    public static final TagKey<Item> ENDER_PEARLS = tag(Registries.ITEM, "ender_pearls");
    public static final TagKey<Item> END_STONES = tag(Registries.ITEM, "end_stones");
    public static final TagKey<Item> FEATHERS = tag(Registries.ITEM, "feathers");
    public static final TagKey<Item> FENCES_WOODEN = tag(Registries.ITEM, "fences/wooden");
    public static final TagKey<Item> FENCE_GATES_WOODEN = tag(Registries.ITEM, "fence_gates/wooden");
    public static final TagKey<Item> GEMS = tag(Registries.ITEM, "gems");
    public static final TagKey<Item> GEMS_AMETHYST = tag(Registries.ITEM, "gems/amethyst");
    public static final TagKey<Item> GEMS_DIAMOND = tag(Registries.ITEM, "gems/diamond");
    public static final TagKey<Item> GEMS_EMERALD = tag(Registries.ITEM, "gems/emerald");
    public static final TagKey<Item> GEMS_LAPIS = tag(Registries.ITEM, "gems/lapis");
    public static final TagKey<Item> GEMS_QUARTZ = tag(Registries.ITEM, "gems/quartz");
    public static final TagKey<Item> GLASS = tag(Registries.ITEM, "glass");
    public static final TagKey<Item> GLASS_COLORLESS = tag(Registries.ITEM, "glass/colorless");
    public static final TagKey<Item> GLASS_PANES = tag(Registries.ITEM, "glass_panes");
    public static final TagKey<Item> GLASS_PANES_COLORLESS = tag(Registries.ITEM, "glass_panes/colorless");
    public static final TagKey<Item> GLASS_PANES_SILICA = tag(Registries.ITEM, "glass_panes/silica");
    public static final TagKey<Item> GLASS_SILICA = tag(Registries.ITEM, "glass/silica");
    public static final TagKey<Item> GLASS_TINTED = tag(Registries.ITEM, "glass/tinted");
    public static final TagKey<Item> GRAVEL = tag(Registries.ITEM, "gravel");
    public static final TagKey<Item> GUNPOWDER = tag(Registries.ITEM, "gunpowders");
    public static final TagKey<Item> HEADS = tag(Registries.ITEM, "heads");
    public static final TagKey<Item> INGOTS = tag(Registries.ITEM, "ingots");
    public static final TagKey<Item> INGOTS_COPPER = tag(Registries.ITEM, "ingots/copper");
    public static final TagKey<Item> INGOTS_GOLD = tag(Registries.ITEM, "ingots/gold");
    public static final TagKey<Item> INGOTS_IRON = tag(Registries.ITEM, "ingots/iron");
    public static final TagKey<Item> INGOTS_NETHERITE = tag(Registries.ITEM, "ingots/netherite");
    public static final TagKey<Item> LEATHER = tag(Registries.ITEM, "leathers");
    public static final TagKey<Item> MUSHROOMS = tag(Registries.ITEM, "mushrooms");
    public static final TagKey<Item> NUGGETS = tag(Registries.ITEM, "nuggets");
    public static final TagKey<Item> NUGGETS_GOLD = tag(Registries.ITEM, "nuggets/gold");
    public static final TagKey<Item> NUGGETS_IRON = tag(Registries.ITEM, "nuggets/iron");
    public static final TagKey<Item> OBSIDIAN = tag(Registries.ITEM, "obsidians");
    public static final TagKey<Item> ORES = tag(Registries.ITEM, "ores");
    public static final TagKey<Item> ORES_DIAMOND = tag(Registries.ITEM, "ores/diamond");
    public static final TagKey<Item> ORES_EMERALD = tag(Registries.ITEM, "ores/emerald");
    public static final TagKey<Item> ORES_IN_GROUND_NETHERRACK = tag(Registries.ITEM, "ores_in_ground/netherrack");
    public static final TagKey<Item> ORES_NETHERITE_SCRAP = tag(Registries.ITEM, "ores/netherite_scrap");
    public static final TagKey<Item> ORE_RATES_DENSE = tag(Registries.ITEM, "ore_rates/dense");
    public static final TagKey<Item> ORE_RATES_SINGULAR = tag(Registries.ITEM, "ore_rates/singular");
    public static final TagKey<Item> ORE_RATES_SPARSE = tag(Registries.ITEM, "ore_rates/sparse");
    public static final TagKey<Item> RAW_MATERIALS = tag(Registries.ITEM, "raw_materials");
    public static final TagKey<Item> RODS = tag(Registries.ITEM, "rods");
    public static final TagKey<Item> RODS_BLAZE = tag(Registries.ITEM, "rods/blaze");
    public static final TagKey<Item> RODS_WOODEN = tag(Registries.ITEM, "rods/wooden");
    public static final TagKey<Item> SANDSTONE = tag(Registries.ITEM, "sandstone");
    public static final TagKey<Item> SAND_COLORLESS = tag(Registries.ITEM, "sand/colorless");
    public static final TagKey<Item> SAND_RED = tag(Registries.ITEM, "sand/red");
    public static final TagKey<Item> SEEDS = tag(Registries.ITEM, "seeds");
    public static final TagKey<Item> SHEARS = tag(Registries.ITEM, "shears");
    public static final TagKey<Item> SLIMEBALLS = tag(Registries.ITEM, "slimeballs");
    public static final TagKey<Item> STAINED_GLASS = tag(Registries.ITEM, "stained_glass");
    public static final TagKey<Item> STAINED_GLASS_PANES = tag(Registries.ITEM, "stained_glass_panes");
    public static final TagKey<Item> STONE = tag(Registries.ITEM, "stones");
    public static final TagKey<Item> STORAGE_BLOCKS = tag(Registries.ITEM, "storage_blocks");
    public static final TagKey<Item> STORAGE_BLOCKS_COAL = tag(Registries.ITEM, "storage_blocks/coal");
    public static final TagKey<Item> STORAGE_BLOCKS_COPPER = tag(Registries.ITEM, "storage_blocks/copper");
    public static final TagKey<Item> STORAGE_BLOCKS_IRON = tag(Registries.ITEM, "storage_blocks/iron");
    public static final TagKey<Item> STORAGE_BLOCKS_LAPIS = tag(Registries.ITEM, "storage_blocks/lapis");
    public static final TagKey<Item> STORAGE_BLOCKS_QUARTZ = tag(Registries.ITEM, "storage_blocks/quartz");
    public static final TagKey<Item> STORAGE_BLOCKS_REDSTONE = tag(Registries.ITEM, "storage_blocks/redstone");
    public static final TagKey<Item> STRING = tag(Registries.ITEM, "strings");
    public static final TagKey<Item> TOOLS_BOWS = tag(Registries.ITEM, "tools/bows");
    public static final TagKey<Item> TOOLS_CROSSBOWS = tag(Registries.ITEM, "tools/crossbows");
    public static final TagKey<Item> TOOLS_FISHING_RODS = tag(Registries.ITEM, "tools/fishing_rods");
    public static final TagKey<Item> TOOLS_SHIELDS = tag(Registries.ITEM, "tools/shields");
    public static final TagKey<Item> TOOLS_TRIDENTS = tag(Registries.ITEM, "tools/tridents");
  }

  public static class Blocks {
    public static final TagKey<Block> FENCES = tag(Registries.BLOCK, "fences");
    public static final TagKey<Block> FENCES_WOODEN = tag(Registries.BLOCK, "fences/wooden");
    public static final TagKey<Block> FENCE_GATES_WOODEN = tag(Registries.BLOCK, "fence_gates/wooden");
    public static final TagKey<Block> GLASS_COLORLESS = tag(Registries.BLOCK, "glass/colorless");
    public static final TagKey<Block> GLASS_PANES = tag(Registries.BLOCK, "glass_panes");
    public static final TagKey<Block> GLASS_PANES_COLORLESS = tag(Registries.BLOCK, "glass_panes/colorless");
    public static final TagKey<Block> GLASS_PANES_SILICA = tag(Registries.BLOCK, "glass_panes/silica");
    public static final TagKey<Block> GLASS_SILICA = tag(Registries.BLOCK, "glass/silica");
    public static final TagKey<Block> GLASS_TINTED = tag(Registries.BLOCK, "glass/tinted");
    public static final TagKey<Block> NEEDS_GOLD_TOOL = tag(Registries.BLOCK, "needs_gold_tool");
    public static final TagKey<Block> NEEDS_NETHERITE_TOOL = tag(Registries.BLOCK, "needs_netherite_tool");
    public static final TagKey<Block> ORES = tag(Registries.BLOCK, "ores");
    public static final TagKey<Block> ORES_GOLD = tag(Registries.BLOCK, "ores/gold");
    public static final TagKey<Block> ORES_IN_GROUND_NETHERRACK = tag(Registries.BLOCK, "ores_in_ground/netherrack");
    public static final TagKey<Block> ORE_RATES_SINGULAR = tag(Registries.BLOCK, "ore_rates/singular");
    public static final TagKey<Block> STAINED_GLASS = tag(Registries.BLOCK, "stained_glass");
    public static final TagKey<Block> STAINED_GLASS_PANES = tag(Registries.BLOCK, "stained_glass_panes");
    public static final TagKey<Block> STONE = tag(Registries.BLOCK, "stones");
    public static final TagKey<Block> STORAGE_BLOCKS = tag(Registries.BLOCK, "storage_blocks");
    public static final TagKey<Block> STORAGE_BLOCKS_NETHERITE = tag(Registries.BLOCK, "storage_blocks/netherite");
  }

  public static class Fluids {
    public static final TagKey<Fluid> MILK = tag(Registries.FLUID, "milk");
  }
}
