# Casting

Casting pours a fluid onto a table (with or without a cast) or into a basin.
Multi-use casts survive the pour; sand casts are single-use. Material and part
casting (tool parts from any castable material) is table-driven per material and
covered in [materials.md](materials.md); the entries here are the fixed recipes.

## amethyst

| Recipe | Where | Fluid | Cast | Result | Cooling | Notes |
|---|---|---|---|---|---|---|
| block | basin | 400 mb `#tconstruct:molten_amethyst` |  | `#c:storage_blocks/amethyst` | 137 |  |
| gem_gold_cast | table | 100 mb `#tconstruct:molten_amethyst` | `#tconstruct:casts/multi_use/gem` | `#c:gems/amethyst` | 68 |  |
| gem_sand_cast | table | 100 mb `#tconstruct:molten_amethyst` | `#tconstruct:casts/single_use/gem` (consumed) | `#c:gems/amethyst` | 68 |  |
| glass | basin | 200 mb `#tconstruct:molten_amethyst` | `#c:glass/colorless` (consumed) | `tconstruct:clear_tinted_glass` | 97 |  |

## blaze

| Recipe | Where | Fluid | Cast | Result | Cooling | Notes |
|---|---|---|---|---|---|---|
| bone | table | 200 mb `#tconstruct:blazing_blood` | `#c:wither_bones` (consumed) | `tconstruct:blazing_bone` | 120 |  |
| congealed | basin | 200 mb `#tconstruct:blazing_blood` | `#tconstruct:congealed_slime` (consumed) | `minecraft:magma_block` | 120 |  |
| cream | table | 50 mb `#tconstruct:blazing_blood` | `#c:slimeball/earth` (consumed) | `minecraft:magma_cream` | 60 |  |
| rod_gold_cast | table | 100 mb `#tconstruct:blazing_blood` | `#tconstruct:casts/multi_use/rod` | `minecraft:blaze_rod` | 85 |  |
| rod_sand_cast | table | 100 mb `#tconstruct:blazing_blood` | `#tconstruct:casts/single_use/rod` (consumed) | `minecraft:blaze_rod` | 85 |  |
| skull | basin | 500 mb `#tconstruct:blazing_blood` | `minecraft:wither_skeleton_skull` (consumed) | `tconstruct:blazing_bone_head` | 189 |  |

## clay

| Recipe | Where | Fluid | Cast | Result | Cooling | Notes |
|---|---|---|---|---|---|---|
| block | basin | 1000 mb `#tconstruct:molten_clay` |  | `minecraft:terracotta` | 160 |  |
| brick_gold_cast | table | 250 mb `#tconstruct:molten_clay` | `#tconstruct:casts/multi_use/ingot` | `minecraft:brick` | 80 |  |
| brick_sand_cast | table | 250 mb `#tconstruct:molten_clay` | `#tconstruct:casts/single_use/ingot` (consumed) | `minecraft:brick` | 80 |  |
| plate_gold_cast | table | 250 mb `#tconstruct:molten_clay` | `#tconstruct:casts/multi_use/plate` | `#c:plates/brick` | 80 | requires tag `c:plates/brick` filled |
| plate_sand_cast | table | 250 mb `#tconstruct:molten_clay` | `#tconstruct:casts/single_use/plate` (consumed) | `#c:plates/brick` | 80 | requires tag `c:plates/brick` filled |

## common/encyclopedia.json

| Recipe | Where | Fluid | Cast | Result | Cooling | Notes |
|---|---|---|---|---|---|---|
| encyclopedia | table | 90 mb `#c:molten_gold` | `minecraft:book` (consumed) | `tconstruct:encyclopedia` | 57 |  |

## common/mighty_smelting.json

| Recipe | Where | Fluid | Cast | Result | Cooling | Notes |
|---|---|---|---|---|---|---|
| mighty_smelting | table | 250 mb `#tconstruct:seared_stone` | `minecraft:book` (consumed) | `tconstruct:mighty_smelting` | 89 |  |

## compat/ceramics

| Recipe | Where | Fluid | Cast | Result | Cooling | Notes |
|---|---|---|---|---|---|---|
| golden_bricks | basin | 10 mb `#c:molten_gold` | `ceramics:porcelain_bricks` (consumed) | `ceramics:golden_bricks` | 19 | requires mod `ceramics` |
| golden_bricks_slab | basin | 5 mb `#c:molten_gold` | `ceramics:porcelain_bricks_slab` (consumed) | `ceramics:golden_bricks_slab` | 13 | requires mod `ceramics` |
| golden_bricks_stairs | basin | 10 mb `#c:molten_gold` | `ceramics:porcelain_bricks_stairs` (consumed) | `ceramics:golden_bricks_stairs` | 19 | requires mod `ceramics` |
| golden_bricks_wall | basin | 10 mb `#c:molten_gold` | `ceramics:porcelain_bricks_wall` (consumed) | `ceramics:golden_bricks_wall` | 19 | requires mod `ceramics` |
| lava_bricks | basin | 100 mb `minecraft:lava` | `minecraft:bricks` (consumed) | `ceramics:lava_bricks` | 70 | requires mod `ceramics` |
| lava_bricks_slab | basin | 50 mb `minecraft:lava` | `minecraft:brick_slab` (consumed) | `ceramics:lava_bricks_slab` | 49 | requires mod `ceramics` |
| lava_bricks_stairs | basin | 100 mb `minecraft:lava` | `minecraft:brick_stairs` (consumed) | `ceramics:lava_bricks_stairs` | 70 | requires mod `ceramics` |
| lava_bricks_wall | basin | 100 mb `minecraft:lava` | `minecraft:brick_wall` (consumed) | `ceramics:lava_bricks_wall` | 70 | requires mod `ceramics` |
| porcelain | basin | 1000 mb `#tconstruct:molten_porcelain` |  | `ceramics:white_porcelain` | 190 | requires mod `ceramics` |
| porcelain_brick_gold_cast | table | 250 mb `#tconstruct:molten_porcelain` | `#tconstruct:casts/multi_use/ingot` | `ceramics:porcelain_brick` | 95 | requires mod `ceramics` |
| porcelain_brick_sand_cast | table | 250 mb `#tconstruct:molten_porcelain` | `#tconstruct:casts/single_use/ingot` (consumed) | `ceramics:porcelain_brick` | 95 | requires mod `ceramics` |

## compat/create

| Recipe | Where | Fluid | Cast | Result | Cooling | Notes |
|---|---|---|---|---|---|---|
| andesite_alloy_iron | basin | 10 mb `#c:molten_iron` | `minecraft:andesite` (consumed) | `create:andesite_alloy` | 20 | requires mod `create` |
| andesite_alloy_zinc | basin | 10 mb `#c:molten_zinc` | `minecraft:andesite` (consumed) | `create:andesite_alloy` | 16 | requires mod `create` |

## compat/necronium_bone.json

| Recipe | Where | Fluid | Cast | Result | Cooling | Notes |
|---|---|---|---|---|---|---|
| necronium_bone | table | 90 mb `#c:molten_uranium` | `#c:wither_bones` (consumed) | `tconstruct:necronium_bone` | 61 | requires tag `c:ingots/uranium` filled |

## compat/necronium_skull.json

| Recipe | Where | Fluid | Cast | Result | Cooling | Notes |
|---|---|---|---|---|---|---|
| necronium_skull | table | 270 mb `#c:molten_uranium` | `minecraft:wither_skeleton_skull` (consumed) | `tconstruct:necronium_head` | 106 | requires tag `c:ingots/uranium` filled |

## compat/refined_glowstone_ingot.json

| Recipe | Where | Fluid | Cast | Result | Cooling | Notes |
|---|---|---|---|---|---|---|
| refined_glowstone_ingot | table | 90 mb `#c:molten_osmium` | `#c:dusts/glowstone` (consumed) | `#c:ingots/refined_glowstone` | 66 | requires tag `c:ingots/refined_glowstone` filled; tag `c:ingots/osmium` filled |

## compat/refined_obsidian_ingot.json

| Recipe | Where | Fluid | Cast | Result | Cooling | Notes |
|---|---|---|---|---|---|---|
| refined_obsidian_ingot | table | 90 mb `#c:molten_osmium` | `#c:dusts/refined_obsidian` (consumed) | `#c:ingots/refined_obsidian` | 66 | requires tag `c:ingots/refined_obsidian` filled; tag `c:ingots/osmium` filled |

## compat/treated_wood.json

| Recipe | Where | Fluid | Cast | Result | Cooling | Notes |
|---|---|---|---|---|---|---|
| treated_wood | basin | 125 mb `#c:creosote` | `#minecraft:planks` (consumed) | `#c:treated_wood` | 100 | requires tag `c:treated_wood` filled; tag `c:creosote` filled |

## compat/wheat_dough.json

| Recipe | Where | Fluid | Cast | Result | Cooling | Notes |
|---|---|---|---|---|---|---|
| wheat_dough | table | 250 mb `#mantle:water` | `minecraft:wheat` (consumed) | `farmersdelight:wheat_dough` | 50 | requires item `farmersdelight:wheat_dough` |

## diamond

| Recipe | Where | Fluid | Cast | Result | Cooling | Notes |
|---|---|---|---|---|---|---|
| block | basin | 900 mb `#tconstruct:molten_diamond` |  | `#c:storage_blocks/diamond` | 250 |  |
| gear_gold_cast | table | 400 mb `#tconstruct:molten_diamond` | `#tconstruct:casts/multi_use/gear` | `#c:gears/diamond` | 166 | requires tag `c:gears/diamond` filled |
| gear_sand_cast | table | 400 mb `#tconstruct:molten_diamond` | `#tconstruct:casts/single_use/gear` (consumed) | `#c:gears/diamond` | 166 | requires tag `c:gears/diamond` filled |
| gem_gold_cast | table | 100 mb `#tconstruct:molten_diamond` | `#tconstruct:casts/multi_use/gem` | `#c:gems/diamond` | 83 |  |
| gem_sand_cast | table | 100 mb `#tconstruct:molten_diamond` | `#tconstruct:casts/single_use/gem` (consumed) | `#c:gems/diamond` | 83 |  |

## emerald

| Recipe | Where | Fluid | Cast | Result | Cooling | Notes |
|---|---|---|---|---|---|---|
| block | basin | 900 mb `#tconstruct:molten_emerald` |  | `#c:storage_blocks/emerald` | 203 |  |
| gear_gold_cast | table | 400 mb `#tconstruct:molten_emerald` | `#tconstruct:casts/multi_use/gear` | `#c:gears/emerald` | 136 | requires tag `c:gears/emerald` filled |
| gear_sand_cast | table | 400 mb `#tconstruct:molten_emerald` | `#tconstruct:casts/single_use/gear` (consumed) | `#c:gears/emerald` | 136 | requires tag `c:gears/emerald` filled |
| gem_gold_cast | table | 100 mb `#tconstruct:molten_emerald` | `#tconstruct:casts/multi_use/gem` | `#c:gems/emerald` | 68 |  |
| gem_sand_cast | table | 100 mb `#tconstruct:molten_emerald` | `#tconstruct:casts/single_use/gem` (consumed) | `#c:gems/emerald` | 68 |  |

## ender

| Recipe | Where | Fluid | Cast | Result | Cooling | Notes |
|---|---|---|---|---|---|---|
| eye | table | 250 mb `#c:ender` | `minecraft:blaze_powder` (consumed) | `minecraft:ender_eye` | 82 |  |
| pearl | table | 250 mb `#c:ender` |  | `minecraft:ender_pearl` | 82 |  |

## fiery

| Recipe | Where | Fluid | Cast | Result | Cooling | Notes |
|---|---|---|---|---|---|---|
| block | basin | 2250 mb `#tconstruct:fiery_liquid` | `#c:storage_blocks/iron` (consumed) | `#c:storage_blocks/fiery` | 401 | requires tag `c:storage_blocks/fiery` filled |
| ingot | table | 250 mb `#tconstruct:fiery_liquid` | `#c:ingots/iron` (consumed) | `#c:ingots/fiery` | 134 | requires tag `c:ingots/fiery` filled |

## filling

| Recipe | Where | Fluid | Cast | Result | Cooling | Notes |
|---|---|---|---|---|---|---|
| tipped_arrow_clean | table | 50 mb `#mantle:water` | `minecraft:tipped_arrow` (consumed) | `minecraft:arrow` | 1 |  |

## gadgets/piggy_backpack.json

| Recipe | Where | Fluid | Cast | Result | Cooling | Notes |
|---|---|---|---|---|---|---|
| piggy_backpack | table | 1000 mb `#tconstruct:sky_slime` | `minecraft:saddle` (consumed) | `tconstruct:piggy_backpack` | 96 |  |

## glass

| Recipe | Where | Fluid | Cast | Result | Cooling | Notes |
|---|---|---|---|---|---|---|
| block | basin | 1000 mb `#tconstruct:molten_glass` |  | `tconstruct:clear_glass` | 195 |  |
| pane | table | 250 mb `#tconstruct:molten_glass` |  | `tconstruct:clear_glass_pane` | 98 |  |

## honey

| Recipe | Where | Fluid | Cast | Result | Cooling | Notes |
|---|---|---|---|---|---|---|
| block | basin | 1000 mb `#c:honey` |  | `minecraft:honey_block` | 94 |  |
| bottle | table | 250 mb `#c:honey` | `minecraft:glass_bottle` (consumed) | `minecraft:honey_bottle` | 1 |  |

## metal

| Recipe | Where | Fluid | Cast | Result | Cooling | Notes |
|---|---|---|---|---|---|---|
| block | basin | 810 mb `#c:molten_aluminum` |  | `#c:storage_blocks/aluminum` | 141 | requires tag `c:storage_blocks/aluminum` filled |
| coin_gold_cast | table | 30 mb `#c:molten_aluminum` | `#tconstruct:casts/multi_use/coin` | `#c:coins/aluminum` | 27 | requires tag `c:coins/aluminum` filled |
| coin_sand_cast | table | 30 mb `#c:molten_aluminum` | `#tconstruct:casts/single_use/coin` (consumed) | `#c:coins/aluminum` | 27 | requires tag `c:coins/aluminum` filled |
| gear_gold_cast | table | 360 mb `#c:molten_aluminum` | `#tconstruct:casts/multi_use/gear` | `#c:gears/aluminum` | 94 | requires tag `c:gears/aluminum` filled |
| gear_sand_cast | table | 360 mb `#c:molten_aluminum` | `#tconstruct:casts/single_use/gear` (consumed) | `#c:gears/aluminum` | 94 | requires tag `c:gears/aluminum` filled |
| ingot_gold_cast | table | 90 mb `#c:molten_aluminum` | `#tconstruct:casts/multi_use/ingot` | `#c:ingots/aluminum` | 47 | requires tag `c:ingots/aluminum` filled |
| ingot_sand_cast | table | 90 mb `#c:molten_aluminum` | `#tconstruct:casts/single_use/ingot` (consumed) | `#c:ingots/aluminum` | 47 | requires tag `c:ingots/aluminum` filled |
| nugget_gold_cast | table | 10 mb `#c:molten_aluminum` | `#tconstruct:casts/multi_use/nugget` | `#c:nuggets/aluminum` | 16 | requires tag `c:nuggets/aluminum` filled |
| nugget_sand_cast | table | 10 mb `#c:molten_aluminum` | `#tconstruct:casts/single_use/nugget` (consumed) | `#c:nuggets/aluminum` | 16 | requires tag `c:nuggets/aluminum` filled |
| plate_gold_cast | table | 90 mb `#c:molten_aluminum` | `#tconstruct:casts/multi_use/plate` | `#c:plates/aluminum` | 47 | requires tag `c:plates/aluminum` filled |
| plate_sand_cast | table | 90 mb `#c:molten_aluminum` | `#tconstruct:casts/single_use/plate` (consumed) | `#c:plates/aluminum` | 47 | requires tag `c:plates/aluminum` filled |
| rod_gold_cast | table | 45 mb `#c:molten_aluminum` | `#tconstruct:casts/multi_use/rod` | `#c:rods/aluminum` | 33 | requires tag `c:rods/aluminum` filled |
| rod_sand_cast | table | 45 mb `#c:molten_aluminum` | `#tconstruct:casts/single_use/rod` (consumed) | `#c:rods/aluminum` | 33 | requires tag `c:rods/aluminum` filled |
| wire_gold_cast | table | 45 mb `#c:molten_aluminum` | `#tconstruct:casts/multi_use/wire` | `#c:wires/aluminum` | 33 | requires tag `c:wires/aluminum` filled |
| wire_sand_cast | table | 45 mb `#c:molten_aluminum` | `#tconstruct:casts/single_use/wire` (consumed) | `#c:wires/aluminum` | 33 | requires tag `c:wires/aluminum` filled |
| block | basin | 810 mb `#c:molten_amethyst_bronze` |  | `#c:storage_blocks/amethyst_bronze` | 182 |  |
| ingot_gold_cast | table | 90 mb `#c:molten_amethyst_bronze` | `#tconstruct:casts/multi_use/ingot` | `#c:ingots/amethyst_bronze` | 61 |  |
| ingot_sand_cast | table | 90 mb `#c:molten_amethyst_bronze` | `#tconstruct:casts/single_use/ingot` (consumed) | `#c:ingots/amethyst_bronze` | 61 |  |
| nugget_gold_cast | table | 10 mb `#c:molten_amethyst_bronze` | `#tconstruct:casts/multi_use/nugget` | `#c:nuggets/amethyst_bronze` | 20 |  |
| nugget_sand_cast | table | 10 mb `#c:molten_amethyst_bronze` | `#tconstruct:casts/single_use/nugget` (consumed) | `#c:nuggets/amethyst_bronze` | 20 |  |
| block | basin | 810 mb `#c:molten_bendalloy` |  | `#c:storage_blocks/bendalloy` | 100 | requires tag `c:storage_blocks/bendalloy` filled |
| ingot_gold_cast | table | 90 mb `#c:molten_bendalloy` | `#tconstruct:casts/multi_use/ingot` | `#c:ingots/bendalloy` | 33 | requires tag `c:ingots/bendalloy` filled |
| ingot_sand_cast | table | 90 mb `#c:molten_bendalloy` | `#tconstruct:casts/single_use/ingot` (consumed) | `#c:ingots/bendalloy` | 33 | requires tag `c:ingots/bendalloy` filled |
| nugget_gold_cast | table | 10 mb `#c:molten_bendalloy` | `#tconstruct:casts/multi_use/nugget` | `#c:nuggets/bendalloy` | 11 | requires tag `c:nuggets/bendalloy` filled |
| nugget_sand_cast | table | 10 mb `#c:molten_bendalloy` | `#tconstruct:casts/single_use/nugget` (consumed) | `#c:nuggets/bendalloy` | 11 | requires tag `c:nuggets/bendalloy` filled |
| block | basin | 810 mb `#c:molten_brass` |  | `#c:storage_blocks/brass` | 161 | requires tag `c:storage_blocks/brass` filled |
| gear_gold_cast | table | 360 mb `#c:molten_brass` | `#tconstruct:casts/multi_use/gear` | `#c:gears/brass` | 107 | requires tag `c:gears/brass` filled |
| gear_sand_cast | table | 360 mb `#c:molten_brass` | `#tconstruct:casts/single_use/gear` (consumed) | `#c:gears/brass` | 107 | requires tag `c:gears/brass` filled |
| ingot_gold_cast | table | 90 mb `#c:molten_brass` | `#tconstruct:casts/multi_use/ingot` | `#c:ingots/brass` | 54 | requires tag `c:ingots/brass` filled |
| ingot_sand_cast | table | 90 mb `#c:molten_brass` | `#tconstruct:casts/single_use/ingot` (consumed) | `#c:ingots/brass` | 54 | requires tag `c:ingots/brass` filled |
| nugget_gold_cast | table | 10 mb `#c:molten_brass` | `#tconstruct:casts/multi_use/nugget` | `#c:nuggets/brass` | 18 | requires tag `c:nuggets/brass` filled |
| nugget_sand_cast | table | 10 mb `#c:molten_brass` | `#tconstruct:casts/single_use/nugget` (consumed) | `#c:nuggets/brass` | 18 | requires tag `c:nuggets/brass` filled |
| plate_gold_cast | table | 90 mb `#c:molten_brass` | `#tconstruct:casts/multi_use/plate` | `#c:plates/brass` | 54 | requires tag `c:plates/brass` filled |
| plate_sand_cast | table | 90 mb `#c:molten_brass` | `#tconstruct:casts/single_use/plate` (consumed) | `#c:plates/brass` | 54 | requires tag `c:plates/brass` filled |
| block | basin | 810 mb `#c:molten_bronze` |  | `#c:storage_blocks/bronze` | 171 | requires tag `c:storage_blocks/bronze` filled |
| coin_gold_cast | table | 30 mb `#c:molten_bronze` | `#tconstruct:casts/multi_use/coin` | `#c:coins/bronze` | 33 | requires tag `c:coins/bronze` filled |
| coin_sand_cast | table | 30 mb `#c:molten_bronze` | `#tconstruct:casts/single_use/coin` (consumed) | `#c:coins/bronze` | 33 | requires tag `c:coins/bronze` filled |
| gear_gold_cast | table | 360 mb `#c:molten_bronze` | `#tconstruct:casts/multi_use/gear` | `#c:gears/bronze` | 114 | requires tag `c:gears/bronze` filled |
| gear_sand_cast | table | 360 mb `#c:molten_bronze` | `#tconstruct:casts/single_use/gear` (consumed) | `#c:gears/bronze` | 114 | requires tag `c:gears/bronze` filled |
| ingot_gold_cast | table | 90 mb `#c:molten_bronze` | `#tconstruct:casts/multi_use/ingot` | `#c:ingots/bronze` | 57 | requires tag `c:ingots/bronze` filled |
| ingot_sand_cast | table | 90 mb `#c:molten_bronze` | `#tconstruct:casts/single_use/ingot` (consumed) | `#c:ingots/bronze` | 57 | requires tag `c:ingots/bronze` filled |
| nugget_gold_cast | table | 10 mb `#c:molten_bronze` | `#tconstruct:casts/multi_use/nugget` | `#c:nuggets/bronze` | 19 | requires tag `c:nuggets/bronze` filled |
| nugget_sand_cast | table | 10 mb `#c:molten_bronze` | `#tconstruct:casts/single_use/nugget` (consumed) | `#c:nuggets/bronze` | 19 | requires tag `c:nuggets/bronze` filled |
| plate_gold_cast | table | 90 mb `#c:molten_bronze` | `#tconstruct:casts/multi_use/plate` | `#c:plates/bronze` | 57 | requires tag `c:plates/bronze` filled |
| plate_sand_cast | table | 90 mb `#c:molten_bronze` | `#tconstruct:casts/single_use/plate` (consumed) | `#c:plates/bronze` | 57 | requires tag `c:plates/bronze` filled |
| block | basin | 810 mb `#c:molten_cadmium` |  | `#c:storage_blocks/cadmium` | 126 | requires tag `c:storage_blocks/cadmium` filled |
| ingot_gold_cast | table | 90 mb `#c:molten_cadmium` | `#tconstruct:casts/multi_use/ingot` | `#c:ingots/cadmium` | 42 | requires tag `c:ingots/cadmium` filled |
| ingot_sand_cast | table | 90 mb `#c:molten_cadmium` | `#tconstruct:casts/single_use/ingot` (consumed) | `#c:ingots/cadmium` | 42 | requires tag `c:ingots/cadmium` filled |
| nugget_gold_cast | table | 10 mb `#c:molten_cadmium` | `#tconstruct:casts/multi_use/nugget` | `#c:nuggets/cadmium` | 14 | requires tag `c:nuggets/cadmium` filled |
| nugget_sand_cast | table | 10 mb `#c:molten_cadmium` | `#tconstruct:casts/single_use/nugget` (consumed) | `#c:nuggets/cadmium` | 14 | requires tag `c:nuggets/cadmium` filled |
| block | basin | 810 mb `#c:molten_chromium` |  | `#c:storage_blocks/chromium` | 190 | requires tag `c:storage_blocks/chromium` filled |
| ingot_gold_cast | table | 90 mb `#c:molten_chromium` | `#tconstruct:casts/multi_use/ingot` | `#c:ingots/chromium` | 63 | requires tag `c:ingots/chromium` filled |
| ingot_sand_cast | table | 90 mb `#c:molten_chromium` | `#tconstruct:casts/single_use/ingot` (consumed) | `#c:ingots/chromium` | 63 | requires tag `c:ingots/chromium` filled |
| nugget_gold_cast | table | 10 mb `#c:molten_chromium` | `#tconstruct:casts/multi_use/nugget` | `#c:nuggets/chromium` | 21 | requires tag `c:nuggets/chromium` filled |
| nugget_sand_cast | table | 10 mb `#c:molten_chromium` | `#tconstruct:casts/single_use/nugget` (consumed) | `#c:nuggets/chromium` | 21 | requires tag `c:nuggets/chromium` filled |
| block | basin | 810 mb `#tconstruct:molten_cinderslime` |  | `#c:storage_blocks/cinderslime` | 203 |  |
| ingot_gold_cast | table | 90 mb `#tconstruct:molten_cinderslime` | `#tconstruct:casts/multi_use/ingot` | `#c:ingots/cinderslime` | 68 |  |
| ingot_sand_cast | table | 90 mb `#tconstruct:molten_cinderslime` | `#tconstruct:casts/single_use/ingot` (consumed) | `#c:ingots/cinderslime` | 68 |  |
| nugget_gold_cast | table | 10 mb `#tconstruct:molten_cinderslime` | `#tconstruct:casts/multi_use/nugget` | `#c:nuggets/cinderslime` | 23 |  |
| nugget_sand_cast | table | 10 mb `#tconstruct:molten_cinderslime` | `#tconstruct:casts/single_use/nugget` (consumed) | `#c:nuggets/cinderslime` | 23 |  |
| block | basin | 810 mb `#c:molten_cobalt` |  | `#c:storage_blocks/cobalt` | 194 |  |
| ingot_gold_cast | table | 90 mb `#c:molten_cobalt` | `#tconstruct:casts/multi_use/ingot` | `#c:ingots/cobalt` | 65 |  |
| ingot_sand_cast | table | 90 mb `#c:molten_cobalt` | `#tconstruct:casts/single_use/ingot` (consumed) | `#c:ingots/cobalt` | 65 |  |
| nugget_gold_cast | table | 10 mb `#c:molten_cobalt` | `#tconstruct:casts/multi_use/nugget` | `#c:nuggets/cobalt` | 22 |  |
| nugget_sand_cast | table | 10 mb `#c:molten_cobalt` | `#tconstruct:casts/single_use/nugget` (consumed) | `#c:nuggets/cobalt` | 22 |  |
| block | basin | 810 mb `#c:molten_constantan` |  | `#c:storage_blocks/constantan` | 192 | requires tag `c:storage_blocks/constantan` filled |
| coin_gold_cast | table | 30 mb `#c:molten_constantan` | `#tconstruct:casts/multi_use/coin` | `#c:coins/constantan` | 37 | requires tag `c:coins/constantan` filled |
| coin_sand_cast | table | 30 mb `#c:molten_constantan` | `#tconstruct:casts/single_use/coin` (consumed) | `#c:coins/constantan` | 37 | requires tag `c:coins/constantan` filled |
| gear_gold_cast | table | 360 mb `#c:molten_constantan` | `#tconstruct:casts/multi_use/gear` | `#c:gears/constantan` | 128 | requires tag `c:gears/constantan` filled |
| gear_sand_cast | table | 360 mb `#c:molten_constantan` | `#tconstruct:casts/single_use/gear` (consumed) | `#c:gears/constantan` | 128 | requires tag `c:gears/constantan` filled |
| ingot_gold_cast | table | 90 mb `#c:molten_constantan` | `#tconstruct:casts/multi_use/ingot` | `#c:ingots/constantan` | 64 | requires tag `c:ingots/constantan` filled |
| ingot_sand_cast | table | 90 mb `#c:molten_constantan` | `#tconstruct:casts/single_use/ingot` (consumed) | `#c:ingots/constantan` | 64 | requires tag `c:ingots/constantan` filled |
| nugget_gold_cast | table | 10 mb `#c:molten_constantan` | `#tconstruct:casts/multi_use/nugget` | `#c:nuggets/constantan` | 21 | requires tag `c:nuggets/constantan` filled |
| nugget_sand_cast | table | 10 mb `#c:molten_constantan` | `#tconstruct:casts/single_use/nugget` (consumed) | `#c:nuggets/constantan` | 21 | requires tag `c:nuggets/constantan` filled |
| plate_gold_cast | table | 90 mb `#c:molten_constantan` | `#tconstruct:casts/multi_use/plate` | `#c:plates/constantan` | 64 | requires tag `c:plates/constantan` filled |
| plate_sand_cast | table | 90 mb `#c:molten_constantan` | `#tconstruct:casts/single_use/plate` (consumed) | `#c:plates/constantan` | 64 | requires tag `c:plates/constantan` filled |
| block | basin | 810 mb `#c:molten_copper` |  | `#c:storage_blocks/copper` | 150 |  |
| coin_gold_cast | table | 30 mb `#c:molten_copper` | `#tconstruct:casts/multi_use/coin` | `#c:coins/copper` | 29 | requires tag `c:coins/copper` filled |
| coin_sand_cast | table | 30 mb `#c:molten_copper` | `#tconstruct:casts/single_use/coin` (consumed) | `#c:coins/copper` | 29 | requires tag `c:coins/copper` filled |
| gear_gold_cast | table | 360 mb `#c:molten_copper` | `#tconstruct:casts/multi_use/gear` | `#c:gears/copper` | 100 | requires tag `c:gears/copper` filled |
| gear_sand_cast | table | 360 mb `#c:molten_copper` | `#tconstruct:casts/single_use/gear` (consumed) | `#c:gears/copper` | 100 | requires tag `c:gears/copper` filled |
| ingot_gold_cast | table | 90 mb `#c:molten_copper` | `#tconstruct:casts/multi_use/ingot` | `#c:ingots/copper` | 50 |  |
| ingot_sand_cast | table | 90 mb `#c:molten_copper` | `#tconstruct:casts/single_use/ingot` (consumed) | `#c:ingots/copper` | 50 |  |
| nugget_gold_cast | table | 10 mb `#c:molten_copper` | `#tconstruct:casts/multi_use/nugget` | `#c:nuggets/copper` | 17 |  |
| nugget_sand_cast | table | 10 mb `#c:molten_copper` | `#tconstruct:casts/single_use/nugget` (consumed) | `#c:nuggets/copper` | 17 |  |
| plate_gold_cast | table | 90 mb `#c:molten_copper` | `#tconstruct:casts/multi_use/plate` | `#c:plates/copper` | 50 | requires tag `c:plates/copper` filled |
| plate_sand_cast | table | 90 mb `#c:molten_copper` | `#tconstruct:casts/single_use/plate` (consumed) | `#c:plates/copper` | 50 | requires tag `c:plates/copper` filled |
| wire_gold_cast | table | 45 mb `#c:molten_copper` | `#tconstruct:casts/multi_use/wire` | `#c:wires/copper` | 35 | requires tag `c:wires/copper` filled |
| wire_sand_cast | table | 45 mb `#c:molten_copper` | `#tconstruct:casts/single_use/wire` (consumed) | `#c:wires/copper` | 35 | requires tag `c:wires/copper` filled |
| block | basin | 810 mb `#c:molten_dawnstone` |  | `#c:storage_blocks/dawnstone` | 190 | requires tag `c:storage_blocks/dawnstone` filled; tag `c:molten_dawnstone` filled |
| ingot_gold_cast | table | 90 mb `#c:molten_dawnstone` | `#tconstruct:casts/multi_use/ingot` | `#c:ingots/dawnstone` | 63 | requires tag `c:ingots/dawnstone` filled; tag `c:molten_dawnstone` filled |
| ingot_sand_cast | table | 90 mb `#c:molten_dawnstone` | `#tconstruct:casts/single_use/ingot` (consumed) | `#c:ingots/dawnstone` | 63 | requires tag `c:ingots/dawnstone` filled; tag `c:molten_dawnstone` filled |
| nugget_gold_cast | table | 10 mb `#c:molten_dawnstone` | `#tconstruct:casts/multi_use/nugget` | `#c:nuggets/dawnstone` | 21 | requires tag `c:nuggets/dawnstone` filled; tag `c:molten_dawnstone` filled |
| nugget_sand_cast | table | 10 mb `#c:molten_dawnstone` | `#tconstruct:casts/single_use/nugget` (consumed) | `#c:nuggets/dawnstone` | 21 | requires tag `c:nuggets/dawnstone` filled; tag `c:molten_dawnstone` filled |
| plate_gold_cast | table | 90 mb `#c:molten_dawnstone` | `#tconstruct:casts/multi_use/plate` | `#c:plates/dawnstone` | 63 | requires tag `c:plates/dawnstone` filled; tag `c:molten_dawnstone` filled |
| plate_sand_cast | table | 90 mb `#c:molten_dawnstone` | `#tconstruct:casts/single_use/plate` (consumed) | `#c:plates/dawnstone` | 63 | requires tag `c:plates/dawnstone` filled; tag `c:molten_dawnstone` filled |
| block | basin | 810 mb `#c:molten_duralumin` |  | `#c:storage_blocks/duralumin` | 163 | requires tag `c:storage_blocks/duralumin` filled |
| ingot_gold_cast | table | 90 mb `#c:molten_duralumin` | `#tconstruct:casts/multi_use/ingot` | `#c:ingots/duralumin` | 54 | requires tag `c:ingots/duralumin` filled |
| ingot_sand_cast | table | 90 mb `#c:molten_duralumin` | `#tconstruct:casts/single_use/ingot` (consumed) | `#c:ingots/duralumin` | 54 | requires tag `c:ingots/duralumin` filled |
| nugget_gold_cast | table | 10 mb `#c:molten_duralumin` | `#tconstruct:casts/multi_use/nugget` | `#c:nuggets/duralumin` | 18 | requires tag `c:nuggets/duralumin` filled |
| nugget_sand_cast | table | 10 mb `#c:molten_duralumin` | `#tconstruct:casts/single_use/nugget` (consumed) | `#c:nuggets/duralumin` | 18 | requires tag `c:nuggets/duralumin` filled |
| block | basin | 810 mb `#c:molten_electrum` |  | `#c:storage_blocks/electrum` | 177 | requires tag `c:storage_blocks/electrum` filled |
| coin_gold_cast | table | 30 mb `#c:molten_electrum` | `#tconstruct:casts/multi_use/coin` | `#c:coins/electrum` | 34 | requires tag `c:coins/electrum` filled |
| coin_sand_cast | table | 30 mb `#c:molten_electrum` | `#tconstruct:casts/single_use/coin` (consumed) | `#c:coins/electrum` | 34 | requires tag `c:coins/electrum` filled |
| gear_gold_cast | table | 360 mb `#c:molten_electrum` | `#tconstruct:casts/multi_use/gear` | `#c:gears/electrum` | 118 | requires tag `c:gears/electrum` filled |
| gear_sand_cast | table | 360 mb `#c:molten_electrum` | `#tconstruct:casts/single_use/gear` (consumed) | `#c:gears/electrum` | 118 | requires tag `c:gears/electrum` filled |
| ingot_gold_cast | table | 90 mb `#c:molten_electrum` | `#tconstruct:casts/multi_use/ingot` | `#c:ingots/electrum` | 59 | requires tag `c:ingots/electrum` filled |
| ingot_sand_cast | table | 90 mb `#c:molten_electrum` | `#tconstruct:casts/single_use/ingot` (consumed) | `#c:ingots/electrum` | 59 | requires tag `c:ingots/electrum` filled |
| nugget_gold_cast | table | 10 mb `#c:molten_electrum` | `#tconstruct:casts/multi_use/nugget` | `#c:nuggets/electrum` | 20 | requires tag `c:nuggets/electrum` filled |
| nugget_sand_cast | table | 10 mb `#c:molten_electrum` | `#tconstruct:casts/single_use/nugget` (consumed) | `#c:nuggets/electrum` | 20 | requires tag `c:nuggets/electrum` filled |
| plate_gold_cast | table | 90 mb `#c:molten_electrum` | `#tconstruct:casts/multi_use/plate` | `#c:plates/electrum` | 59 | requires tag `c:plates/electrum` filled |
| plate_sand_cast | table | 90 mb `#c:molten_electrum` | `#tconstruct:casts/single_use/plate` (consumed) | `#c:plates/electrum` | 59 | requires tag `c:plates/electrum` filled |
| wire_gold_cast | table | 45 mb `#c:molten_electrum` | `#tconstruct:casts/multi_use/wire` | `#c:wires/electrum` | 42 | requires tag `c:wires/electrum` filled |
| wire_sand_cast | table | 45 mb `#c:molten_electrum` | `#tconstruct:casts/single_use/wire` (consumed) | `#c:wires/electrum` | 42 | requires tag `c:wires/electrum` filled |
| block | basin | 810 mb `#c:molten_enderium` |  | `#c:storage_blocks/enderium` | 229 | requires tag `c:storage_blocks/enderium` filled |
| coin_gold_cast | table | 30 mb `#c:molten_enderium` | `#tconstruct:casts/multi_use/coin` | `#c:coins/enderium` | 44 | requires tag `c:coins/enderium` filled |
| coin_sand_cast | table | 30 mb `#c:molten_enderium` | `#tconstruct:casts/single_use/coin` (consumed) | `#c:coins/enderium` | 44 | requires tag `c:coins/enderium` filled |
| gear_gold_cast | table | 360 mb `#c:molten_enderium` | `#tconstruct:casts/multi_use/gear` | `#c:gears/enderium` | 152 | requires tag `c:gears/enderium` filled |
| gear_sand_cast | table | 360 mb `#c:molten_enderium` | `#tconstruct:casts/single_use/gear` (consumed) | `#c:gears/enderium` | 152 | requires tag `c:gears/enderium` filled |
| ingot_gold_cast | table | 90 mb `#c:molten_enderium` | `#tconstruct:casts/multi_use/ingot` | `#c:ingots/enderium` | 76 | requires tag `c:ingots/enderium` filled |
| ingot_sand_cast | table | 90 mb `#c:molten_enderium` | `#tconstruct:casts/single_use/ingot` (consumed) | `#c:ingots/enderium` | 76 | requires tag `c:ingots/enderium` filled |
| nugget_gold_cast | table | 10 mb `#c:molten_enderium` | `#tconstruct:casts/multi_use/nugget` | `#c:nuggets/enderium` | 25 | requires tag `c:nuggets/enderium` filled |
| nugget_sand_cast | table | 10 mb `#c:molten_enderium` | `#tconstruct:casts/single_use/nugget` (consumed) | `#c:nuggets/enderium` | 25 | requires tag `c:nuggets/enderium` filled |
| plate_gold_cast | table | 90 mb `#c:molten_enderium` | `#tconstruct:casts/multi_use/plate` | `#c:plates/enderium` | 76 | requires tag `c:plates/enderium` filled |
| plate_sand_cast | table | 90 mb `#c:molten_enderium` | `#tconstruct:casts/single_use/plate` (consumed) | `#c:plates/enderium` | 76 | requires tag `c:plates/enderium` filled |
| apple | table | 720 mb `#c:molten_gold` | `minecraft:apple` (consumed) | `minecraft:golden_apple` | 161 |  |
| bars | table | 30 mb `#c:molten_gold` |  | `tconstruct:gold_bars` | 33 |  |
| block | basin | 810 mb `#c:molten_gold` |  | `#c:storage_blocks/gold` | 171 |  |
| carrot | table | 80 mb `#c:molten_gold` | `minecraft:carrot` (consumed) | `minecraft:golden_carrot` | 54 |  |
| clock | table | 360 mb `#c:molten_gold` | `minecraft:redstone` (consumed) | `minecraft:clock` | 114 |  |
| coin_gold_cast | table | 30 mb `#c:molten_gold` | `#tconstruct:casts/multi_use/coin` | `#c:coins/gold` | 33 | requires tag `c:coins/gold` filled |
| coin_sand_cast | table | 30 mb `#c:molten_gold` | `#tconstruct:casts/single_use/coin` (consumed) | `#c:coins/gold` | 33 | requires tag `c:coins/gold` filled |
| gear_gold_cast | table | 360 mb `#c:molten_gold` | `#tconstruct:casts/multi_use/gear` | `#c:gears/gold` | 114 | requires tag `c:gears/gold` filled |
| gear_sand_cast | table | 360 mb `#c:molten_gold` | `#tconstruct:casts/single_use/gear` (consumed) | `#c:gears/gold` | 114 | requires tag `c:gears/gold` filled |
| ingot_gold_cast | table | 90 mb `#c:molten_gold` | `#tconstruct:casts/multi_use/ingot` | `#c:ingots/gold` | 57 |  |
| ingot_sand_cast | table | 90 mb `#c:molten_gold` | `#tconstruct:casts/single_use/ingot` (consumed) | `#c:ingots/gold` | 57 |  |
| melon | table | 80 mb `#c:molten_gold` | `minecraft:melon_slice` (consumed) | `minecraft:glistering_melon_slice` | 54 |  |
| nugget_gold_cast | table | 10 mb `#c:molten_gold` | `#tconstruct:casts/multi_use/nugget` | `#c:nuggets/gold` | 19 |  |
| nugget_sand_cast | table | 10 mb `#c:molten_gold` | `#tconstruct:casts/single_use/nugget` (consumed) | `#c:nuggets/gold` | 19 |  |
| plate_gold_cast | table | 90 mb `#c:molten_gold` | `#tconstruct:casts/multi_use/plate` | `#c:plates/gold` | 57 | requires tag `c:plates/gold` filled |
| plate_sand_cast | table | 90 mb `#c:molten_gold` | `#tconstruct:casts/single_use/plate` (consumed) | `#c:plates/gold` | 57 | requires tag `c:plates/gold` filled |
| block | basin | 810 mb `#c:molten_hepatizon` |  | `#c:storage_blocks/hepatizon` | 233 |  |
| ingot_gold_cast | table | 90 mb `#c:molten_hepatizon` | `#tconstruct:casts/multi_use/ingot` | `#c:ingots/hepatizon` | 78 |  |
| ingot_sand_cast | table | 90 mb `#c:molten_hepatizon` | `#tconstruct:casts/single_use/ingot` (consumed) | `#c:ingots/hepatizon` | 78 |  |
| nugget_gold_cast | table | 10 mb `#c:molten_hepatizon` | `#tconstruct:casts/multi_use/nugget` | `#c:nuggets/hepatizon` | 26 |  |
| nugget_sand_cast | table | 10 mb `#c:molten_hepatizon` | `#tconstruct:casts/single_use/nugget` (consumed) | `#c:nuggets/hepatizon` | 26 |  |
| block | basin | 810 mb `#c:molten_invar` |  | `#c:storage_blocks/invar` | 190 | requires tag `c:storage_blocks/invar` filled |
| coin_gold_cast | table | 30 mb `#c:molten_invar` | `#tconstruct:casts/multi_use/coin` | `#c:coins/invar` | 37 | requires tag `c:coins/invar` filled |
| coin_sand_cast | table | 30 mb `#c:molten_invar` | `#tconstruct:casts/single_use/coin` (consumed) | `#c:coins/invar` | 37 | requires tag `c:coins/invar` filled |
| gear_gold_cast | table | 360 mb `#c:molten_invar` | `#tconstruct:casts/multi_use/gear` | `#c:gears/invar` | 127 | requires tag `c:gears/invar` filled |
| gear_sand_cast | table | 360 mb `#c:molten_invar` | `#tconstruct:casts/single_use/gear` (consumed) | `#c:gears/invar` | 127 | requires tag `c:gears/invar` filled |
| ingot_gold_cast | table | 90 mb `#c:molten_invar` | `#tconstruct:casts/multi_use/ingot` | `#c:ingots/invar` | 63 | requires tag `c:ingots/invar` filled |
| ingot_sand_cast | table | 90 mb `#c:molten_invar` | `#tconstruct:casts/single_use/ingot` (consumed) | `#c:ingots/invar` | 63 | requires tag `c:ingots/invar` filled |
| nugget_gold_cast | table | 10 mb `#c:molten_invar` | `#tconstruct:casts/multi_use/nugget` | `#c:nuggets/invar` | 21 | requires tag `c:nuggets/invar` filled |
| nugget_sand_cast | table | 10 mb `#c:molten_invar` | `#tconstruct:casts/single_use/nugget` (consumed) | `#c:nuggets/invar` | 21 | requires tag `c:nuggets/invar` filled |
| plate_gold_cast | table | 90 mb `#c:molten_invar` | `#tconstruct:casts/multi_use/plate` | `#c:plates/invar` | 63 | requires tag `c:plates/invar` filled |
| plate_sand_cast | table | 90 mb `#c:molten_invar` | `#tconstruct:casts/single_use/plate` (consumed) | `#c:plates/invar` | 63 | requires tag `c:plates/invar` filled |
| bars | table | 30 mb `#c:molten_iron` |  | `minecraft:iron_bars` | 35 |  |
| block | basin | 810 mb `#c:molten_iron` |  | `#c:storage_blocks/iron` | 180 |  |
| coin_gold_cast | table | 30 mb `#c:molten_iron` | `#tconstruct:casts/multi_use/coin` | `#c:coins/iron` | 35 | requires tag `c:coins/iron` filled |
| coin_sand_cast | table | 30 mb `#c:molten_iron` | `#tconstruct:casts/single_use/coin` (consumed) | `#c:coins/iron` | 35 | requires tag `c:coins/iron` filled |
| compass | table | 360 mb `#c:molten_iron` | `minecraft:redstone` (consumed) | `minecraft:compass` | 120 |  |
| gear_gold_cast | table | 360 mb `#c:molten_iron` | `#tconstruct:casts/multi_use/gear` | `#c:gears/iron` | 120 | requires tag `c:gears/iron` filled |
| gear_sand_cast | table | 360 mb `#c:molten_iron` | `#tconstruct:casts/single_use/gear` (consumed) | `#c:gears/iron` | 120 | requires tag `c:gears/iron` filled |
| ingot_gold_cast | table | 90 mb `#c:molten_iron` | `#tconstruct:casts/multi_use/ingot` | `#c:ingots/iron` | 60 |  |
| ingot_sand_cast | table | 90 mb `#c:molten_iron` | `#tconstruct:casts/single_use/ingot` (consumed) | `#c:ingots/iron` | 60 |  |
| lantern | table | 80 mb `#c:molten_iron` | `minecraft:torch` (consumed) | `minecraft:lantern` | 57 |  |
| nugget_gold_cast | table | 10 mb `#c:molten_iron` | `#tconstruct:casts/multi_use/nugget` | `#c:nuggets/iron` | 20 |  |
| nugget_sand_cast | table | 10 mb `#c:molten_iron` | `#tconstruct:casts/single_use/nugget` (consumed) | `#c:nuggets/iron` | 20 |  |
| plate_gold_cast | table | 90 mb `#c:molten_iron` | `#tconstruct:casts/multi_use/plate` | `#c:plates/iron` | 60 | requires tag `c:plates/iron` filled |
| plate_sand_cast | table | 90 mb `#c:molten_iron` | `#tconstruct:casts/single_use/plate` (consumed) | `#c:plates/iron` | 60 | requires tag `c:plates/iron` filled |
| rod_gold_cast | table | 45 mb `#c:molten_iron` | `#tconstruct:casts/multi_use/rod` | `#c:rods/iron` | 43 | requires tag `c:rods/iron` filled |
| rod_sand_cast | table | 45 mb `#c:molten_iron` | `#tconstruct:casts/single_use/rod` (consumed) | `#c:rods/iron` | 43 | requires tag `c:rods/iron` filled |
| soul_lantern | table | 80 mb `#c:molten_iron` | `minecraft:soul_torch` (consumed) | `minecraft:soul_lantern` | 57 |  |
| block | basin | 810 mb `#tconstruct:molten_knightmetal` |  | `#c:storage_blocks/knightmetal` | 225 |  |
| ingot_gold_cast | table | 90 mb `#tconstruct:molten_knightmetal` | `#tconstruct:casts/multi_use/ingot` | `#c:ingots/knightmetal` | 75 |  |
| ingot_sand_cast | table | 90 mb `#tconstruct:molten_knightmetal` | `#tconstruct:casts/single_use/ingot` (consumed) | `#c:ingots/knightmetal` | 75 |  |
| nugget_gold_cast | table | 10 mb `#tconstruct:molten_knightmetal` | `#tconstruct:casts/multi_use/nugget` | `#c:nuggets/knightmetal` | 25 |  |
| nugget_sand_cast | table | 10 mb `#tconstruct:molten_knightmetal` | `#tconstruct:casts/single_use/nugget` (consumed) | `#c:nuggets/knightmetal` | 25 |  |
| block | basin | 810 mb `#tconstruct:molten_knightslime` |  | `#c:storage_blocks/knightslime` | 210 |  |
| ingot_gold_cast | table | 90 mb `#tconstruct:molten_knightslime` | `#tconstruct:casts/multi_use/ingot` | `#c:ingots/knightslime` | 70 |  |
| ingot_sand_cast | table | 90 mb `#tconstruct:molten_knightslime` | `#tconstruct:casts/single_use/ingot` (consumed) | `#c:ingots/knightslime` | 70 |  |
| nugget_gold_cast | table | 10 mb `#tconstruct:molten_knightslime` | `#tconstruct:casts/multi_use/nugget` | `#c:nuggets/knightslime` | 23 |  |
| nugget_sand_cast | table | 10 mb `#tconstruct:molten_knightslime` | `#tconstruct:casts/single_use/nugget` (consumed) | `#c:nuggets/knightslime` | 23 |  |
| block | basin | 810 mb `#c:molten_lead` |  | `#c:storage_blocks/lead` | 130 | requires tag `c:storage_blocks/lead` filled |
| coin_gold_cast | table | 30 mb `#c:molten_lead` | `#tconstruct:casts/multi_use/coin` | `#c:coins/lead` | 25 | requires tag `c:coins/lead` filled |
| coin_sand_cast | table | 30 mb `#c:molten_lead` | `#tconstruct:casts/single_use/coin` (consumed) | `#c:coins/lead` | 25 | requires tag `c:coins/lead` filled |
| gear_gold_cast | table | 360 mb `#c:molten_lead` | `#tconstruct:casts/multi_use/gear` | `#c:gears/lead` | 87 | requires tag `c:gears/lead` filled |
| gear_sand_cast | table | 360 mb `#c:molten_lead` | `#tconstruct:casts/single_use/gear` (consumed) | `#c:gears/lead` | 87 | requires tag `c:gears/lead` filled |
| ingot_gold_cast | table | 90 mb `#c:molten_lead` | `#tconstruct:casts/multi_use/ingot` | `#c:ingots/lead` | 43 | requires tag `c:ingots/lead` filled |
| ingot_sand_cast | table | 90 mb `#c:molten_lead` | `#tconstruct:casts/single_use/ingot` (consumed) | `#c:ingots/lead` | 43 | requires tag `c:ingots/lead` filled |
| nugget_gold_cast | table | 10 mb `#c:molten_lead` | `#tconstruct:casts/multi_use/nugget` | `#c:nuggets/lead` | 14 | requires tag `c:nuggets/lead` filled |
| nugget_sand_cast | table | 10 mb `#c:molten_lead` | `#tconstruct:casts/single_use/nugget` (consumed) | `#c:nuggets/lead` | 14 | requires tag `c:nuggets/lead` filled |
| plate_gold_cast | table | 90 mb `#c:molten_lead` | `#tconstruct:casts/multi_use/plate` | `#c:plates/lead` | 43 | requires tag `c:plates/lead` filled |
| plate_sand_cast | table | 90 mb `#c:molten_lead` | `#tconstruct:casts/single_use/plate` (consumed) | `#c:plates/lead` | 43 | requires tag `c:plates/lead` filled |
| wire_gold_cast | table | 45 mb `#c:molten_lead` | `#tconstruct:casts/multi_use/wire` | `#c:wires/lead` | 31 | requires tag `c:wires/lead` filled |
| wire_sand_cast | table | 45 mb `#c:molten_lead` | `#tconstruct:casts/single_use/wire` (consumed) | `#c:wires/lead` | 31 | requires tag `c:wires/lead` filled |
| block | basin | 810 mb `#c:molten_lumium` |  | `#c:storage_blocks/lumium` | 203 | requires tag `c:storage_blocks/lumium` filled |
| coin_gold_cast | table | 30 mb `#c:molten_lumium` | `#tconstruct:casts/multi_use/coin` | `#c:coins/lumium` | 39 | requires tag `c:coins/lumium` filled |
| coin_sand_cast | table | 30 mb `#c:molten_lumium` | `#tconstruct:casts/single_use/coin` (consumed) | `#c:coins/lumium` | 39 | requires tag `c:coins/lumium` filled |
| gear_gold_cast | table | 360 mb `#c:molten_lumium` | `#tconstruct:casts/multi_use/gear` | `#c:gears/lumium` | 136 | requires tag `c:gears/lumium` filled |
| gear_sand_cast | table | 360 mb `#c:molten_lumium` | `#tconstruct:casts/single_use/gear` (consumed) | `#c:gears/lumium` | 136 | requires tag `c:gears/lumium` filled |
| ingot_gold_cast | table | 90 mb `#c:molten_lumium` | `#tconstruct:casts/multi_use/ingot` | `#c:ingots/lumium` | 68 | requires tag `c:ingots/lumium` filled |
| ingot_sand_cast | table | 90 mb `#c:molten_lumium` | `#tconstruct:casts/single_use/ingot` (consumed) | `#c:ingots/lumium` | 68 | requires tag `c:ingots/lumium` filled |
| nugget_gold_cast | table | 10 mb `#c:molten_lumium` | `#tconstruct:casts/multi_use/nugget` | `#c:nuggets/lumium` | 23 | requires tag `c:nuggets/lumium` filled |
| nugget_sand_cast | table | 10 mb `#c:molten_lumium` | `#tconstruct:casts/single_use/nugget` (consumed) | `#c:nuggets/lumium` | 23 | requires tag `c:nuggets/lumium` filled |
| plate_gold_cast | table | 90 mb `#c:molten_lumium` | `#tconstruct:casts/multi_use/plate` | `#c:plates/lumium` | 68 | requires tag `c:plates/lumium` filled |
| plate_sand_cast | table | 90 mb `#c:molten_lumium` | `#tconstruct:casts/single_use/plate` (consumed) | `#c:plates/lumium` | 68 | requires tag `c:plates/lumium` filled |
| block | basin | 810 mb `#c:molten_manyullyn` |  | `#c:storage_blocks/manyullyn` | 216 |  |
| ingot_gold_cast | table | 90 mb `#c:molten_manyullyn` | `#tconstruct:casts/multi_use/ingot` | `#c:ingots/manyullyn` | 72 |  |
| ingot_sand_cast | table | 90 mb `#c:molten_manyullyn` | `#tconstruct:casts/single_use/ingot` (consumed) | `#c:ingots/manyullyn` | 72 |  |
| nugget_gold_cast | table | 10 mb `#c:molten_manyullyn` | `#tconstruct:casts/multi_use/nugget` | `#c:nuggets/manyullyn` | 24 |  |
| nugget_sand_cast | table | 10 mb `#c:molten_manyullyn` | `#tconstruct:casts/single_use/nugget` (consumed) | `#c:nuggets/manyullyn` | 24 |  |
| ancient_repair_kit_gold_cast | table | 180 mb `#tconstruct:molten_debris` | `#tconstruct:casts/multi_use/repair_kit` | `tconstruct:repair_kit` | 101 |  |
| ancient_repair_kit_sand_cast | table | 180 mb `#tconstruct:molten_debris` | `#tconstruct:casts/single_use/repair_kit` (consumed) | `tconstruct:repair_kit` | 101 |  |
| block | basin | 810 mb `#c:molten_netherite` |  | `#c:storage_blocks/netherite` | 221 |  |
| coin_gold_cast | table | 30 mb `#c:molten_netherite` | `#tconstruct:casts/multi_use/coin` | `#c:coins/netherite` | 42 | requires tag `c:coins/netherite` filled |
| coin_sand_cast | table | 30 mb `#c:molten_netherite` | `#tconstruct:casts/single_use/coin` (consumed) | `#c:coins/netherite` | 42 | requires tag `c:coins/netherite` filled |
| debris_nugget_gold_cast | table | 10 mb `#tconstruct:molten_debris` | `#tconstruct:casts/multi_use/nugget` | `#c:nuggets/netherite_scrap` | 24 |  |
| debris_nugget_sand_cast | table | 10 mb `#tconstruct:molten_debris` | `#tconstruct:casts/single_use/nugget` (consumed) | `#c:nuggets/netherite_scrap` | 24 |  |
| gear_gold_cast | table | 360 mb `#c:molten_netherite` | `#tconstruct:casts/multi_use/gear` | `#c:gears/netherite` | 147 | requires tag `c:gears/netherite` filled |
| gear_sand_cast | table | 360 mb `#c:molten_netherite` | `#tconstruct:casts/single_use/gear` (consumed) | `#c:gears/netherite` | 147 | requires tag `c:gears/netherite` filled |
| ingot_gold_cast | table | 90 mb `#c:molten_netherite` | `#tconstruct:casts/multi_use/ingot` | `#c:ingots/netherite` | 74 |  |
| ingot_sand_cast | table | 90 mb `#c:molten_netherite` | `#tconstruct:casts/single_use/ingot` (consumed) | `#c:ingots/netherite` | 74 |  |
| nugget_gold_cast | table | 10 mb `#c:molten_netherite` | `#tconstruct:casts/multi_use/nugget` | `#c:nuggets/netherite` | 25 |  |
| nugget_sand_cast | table | 10 mb `#c:molten_netherite` | `#tconstruct:casts/single_use/nugget` (consumed) | `#c:nuggets/netherite` | 25 |  |
| plate_gold_cast | table | 90 mb `#c:molten_netherite` | `#tconstruct:casts/multi_use/plate` | `#c:plates/netherite` | 74 | requires tag `c:plates/netherite` filled |
| plate_sand_cast | table | 90 mb `#c:molten_netherite` | `#tconstruct:casts/single_use/plate` (consumed) | `#c:plates/netherite` | 74 | requires tag `c:plates/netherite` filled |
| scrap_gold_cast | table | 90 mb `#tconstruct:molten_debris` | `#tconstruct:casts/multi_use/ingot` | `minecraft:netherite_scrap` | 71 |  |
| scrap_sand_cast | table | 90 mb `#tconstruct:molten_debris` | `#tconstruct:casts/single_use/ingot` (consumed) | `minecraft:netherite_scrap` | 71 |  |
| block | basin | 810 mb `#c:molten_nickel` |  | `#c:storage_blocks/nickel` | 194 | requires tag `c:storage_blocks/nickel` filled |
| coin_gold_cast | table | 30 mb `#c:molten_nickel` | `#tconstruct:casts/multi_use/coin` | `#c:coins/nickel` | 37 | requires tag `c:coins/nickel` filled |
| coin_sand_cast | table | 30 mb `#c:molten_nickel` | `#tconstruct:casts/single_use/coin` (consumed) | `#c:coins/nickel` | 37 | requires tag `c:coins/nickel` filled |
| gear_gold_cast | table | 360 mb `#c:molten_nickel` | `#tconstruct:casts/multi_use/gear` | `#c:gears/nickel` | 130 | requires tag `c:gears/nickel` filled |
| gear_sand_cast | table | 360 mb `#c:molten_nickel` | `#tconstruct:casts/single_use/gear` (consumed) | `#c:gears/nickel` | 130 | requires tag `c:gears/nickel` filled |
| ingot_gold_cast | table | 90 mb `#c:molten_nickel` | `#tconstruct:casts/multi_use/ingot` | `#c:ingots/nickel` | 65 | requires tag `c:ingots/nickel` filled |
| ingot_sand_cast | table | 90 mb `#c:molten_nickel` | `#tconstruct:casts/single_use/ingot` (consumed) | `#c:ingots/nickel` | 65 | requires tag `c:ingots/nickel` filled |
| nugget_gold_cast | table | 10 mb `#c:molten_nickel` | `#tconstruct:casts/multi_use/nugget` | `#c:nuggets/nickel` | 22 | requires tag `c:nuggets/nickel` filled |
| nugget_sand_cast | table | 10 mb `#c:molten_nickel` | `#tconstruct:casts/single_use/nugget` (consumed) | `#c:nuggets/nickel` | 22 | requires tag `c:nuggets/nickel` filled |
| plate_gold_cast | table | 90 mb `#c:molten_nickel` | `#tconstruct:casts/multi_use/plate` | `#c:plates/nickel` | 65 | requires tag `c:plates/nickel` filled |
| plate_sand_cast | table | 90 mb `#c:molten_nickel` | `#tconstruct:casts/single_use/plate` (consumed) | `#c:plates/nickel` | 65 | requires tag `c:plates/nickel` filled |
| block | basin | 810 mb `#c:molten_nicrosil` |  | `#c:storage_blocks/nicrosil` | 208 | requires tag `c:storage_blocks/nicrosil` filled |
| ingot_gold_cast | table | 90 mb `#c:molten_nicrosil` | `#tconstruct:casts/multi_use/ingot` | `#c:ingots/nicrosil` | 69 | requires tag `c:ingots/nicrosil` filled |
| ingot_sand_cast | table | 90 mb `#c:molten_nicrosil` | `#tconstruct:casts/single_use/ingot` (consumed) | `#c:ingots/nicrosil` | 69 | requires tag `c:ingots/nicrosil` filled |
| nugget_gold_cast | table | 10 mb `#c:molten_nicrosil` | `#tconstruct:casts/multi_use/nugget` | `#c:nuggets/nicrosil` | 23 | requires tag `c:nuggets/nicrosil` filled |
| nugget_sand_cast | table | 10 mb `#c:molten_nicrosil` | `#tconstruct:casts/single_use/nugget` (consumed) | `#c:nuggets/nicrosil` | 23 | requires tag `c:nuggets/nicrosil` filled |
| block | basin | 810 mb `#c:molten_osmium` |  | `#c:storage_blocks/osmium` | 197 | requires tag `c:storage_blocks/osmium` filled |
| ingot_gold_cast | table | 90 mb `#c:molten_osmium` | `#tconstruct:casts/multi_use/ingot` | `#c:ingots/osmium` | 66 | requires tag `c:ingots/osmium` filled |
| ingot_sand_cast | table | 90 mb `#c:molten_osmium` | `#tconstruct:casts/single_use/ingot` (consumed) | `#c:ingots/osmium` | 66 | requires tag `c:ingots/osmium` filled |
| nugget_gold_cast | table | 10 mb `#c:molten_osmium` | `#tconstruct:casts/multi_use/nugget` | `#c:nuggets/osmium` | 22 | requires tag `c:nuggets/osmium` filled |
| nugget_sand_cast | table | 10 mb `#c:molten_osmium` | `#tconstruct:casts/single_use/nugget` (consumed) | `#c:nuggets/osmium` | 22 | requires tag `c:nuggets/osmium` filled |
| block | basin | 810 mb `#c:molten_pewter` |  | `#c:storage_blocks/pewter` | 139 | requires tag `c:storage_blocks/pewter` filled |
| ingot_gold_cast | table | 90 mb `#c:molten_pewter` | `#tconstruct:casts/multi_use/ingot` | `#c:ingots/pewter` | 46 | requires tag `c:ingots/pewter` filled |
| ingot_sand_cast | table | 90 mb `#c:molten_pewter` | `#tconstruct:casts/single_use/ingot` (consumed) | `#c:ingots/pewter` | 46 | requires tag `c:ingots/pewter` filled |
| nugget_gold_cast | table | 10 mb `#c:molten_pewter` | `#tconstruct:casts/multi_use/nugget` | `#c:nuggets/pewter` | 15 | requires tag `c:nuggets/pewter` filled |
| nugget_sand_cast | table | 10 mb `#c:molten_pewter` | `#tconstruct:casts/single_use/nugget` (consumed) | `#c:nuggets/pewter` | 15 | requires tag `c:nuggets/pewter` filled |
| block | basin | 810 mb `#tconstruct:molten_pig_iron` |  | `#c:storage_blocks/pig_iron` | 181 |  |
| ingot_gold_cast | table | 90 mb `#tconstruct:molten_pig_iron` | `#tconstruct:casts/multi_use/ingot` | `#c:ingots/pig_iron` | 60 |  |
| ingot_sand_cast | table | 90 mb `#tconstruct:molten_pig_iron` | `#tconstruct:casts/single_use/ingot` (consumed) | `#c:ingots/pig_iron` | 60 |  |
| nugget_gold_cast | table | 10 mb `#tconstruct:molten_pig_iron` | `#tconstruct:casts/multi_use/nugget` | `#c:nuggets/pig_iron` | 20 |  |
| nugget_sand_cast | table | 10 mb `#tconstruct:molten_pig_iron` | `#tconstruct:casts/single_use/nugget` (consumed) | `#c:nuggets/pig_iron` | 20 |  |
| block | basin | 810 mb `#c:molten_platinum` |  | `#c:storage_blocks/platinum` | 196 | requires tag `c:storage_blocks/platinum` filled |
| ingot_gold_cast | table | 90 mb `#c:molten_platinum` | `#tconstruct:casts/multi_use/ingot` | `#c:ingots/platinum` | 65 | requires tag `c:ingots/platinum` filled |
| ingot_sand_cast | table | 90 mb `#c:molten_platinum` | `#tconstruct:casts/single_use/ingot` (consumed) | `#c:ingots/platinum` | 65 | requires tag `c:ingots/platinum` filled |
| nugget_gold_cast | table | 10 mb `#c:molten_platinum` | `#tconstruct:casts/multi_use/nugget` | `#c:nuggets/platinum` | 22 | requires tag `c:nuggets/platinum` filled |
| nugget_sand_cast | table | 10 mb `#c:molten_platinum` | `#tconstruct:casts/single_use/nugget` (consumed) | `#c:nuggets/platinum` | 22 | requires tag `c:nuggets/platinum` filled |
| block | basin | 810 mb `#tconstruct:molten_queens_slime` |  | `#c:storage_blocks/queens_slime` | 212 |  |
| ingot_gold_cast | table | 90 mb `#tconstruct:molten_queens_slime` | `#tconstruct:casts/multi_use/ingot` | `#c:ingots/queens_slime` | 71 |  |
| ingot_sand_cast | table | 90 mb `#tconstruct:molten_queens_slime` | `#tconstruct:casts/single_use/ingot` (consumed) | `#c:ingots/queens_slime` | 71 |  |
| nugget_gold_cast | table | 10 mb `#tconstruct:molten_queens_slime` | `#tconstruct:casts/multi_use/nugget` | `#c:nuggets/queens_slime` | 24 |  |
| nugget_sand_cast | table | 10 mb `#tconstruct:molten_queens_slime` | `#tconstruct:casts/single_use/nugget` (consumed) | `#c:nuggets/queens_slime` | 24 |  |
| block | basin | 810 mb `#c:molten_refined_glowstone` |  | `#c:storage_blocks/refined_glowstone` | 183 | requires tag `c:storage_blocks/refined_glowstone` filled |
| ingot_gold_cast | table | 90 mb `#c:molten_refined_glowstone` | `#tconstruct:casts/multi_use/ingot` | `#c:ingots/refined_glowstone` | 61 | requires tag `c:ingots/refined_glowstone` filled |
| ingot_sand_cast | table | 90 mb `#c:molten_refined_glowstone` | `#tconstruct:casts/single_use/ingot` (consumed) | `#c:ingots/refined_glowstone` | 61 | requires tag `c:ingots/refined_glowstone` filled |
| nugget_gold_cast | table | 10 mb `#c:molten_refined_glowstone` | `#tconstruct:casts/multi_use/nugget` | `#c:nuggets/refined_glowstone` | 20 | requires tag `c:nuggets/refined_glowstone` filled |
| nugget_sand_cast | table | 10 mb `#c:molten_refined_glowstone` | `#tconstruct:casts/single_use/nugget` (consumed) | `#c:nuggets/refined_glowstone` | 20 | requires tag `c:nuggets/refined_glowstone` filled |
| block | basin | 810 mb `#c:molten_refined_obsidian` |  | `#c:storage_blocks/refined_obsidian` | 239 | requires tag `c:storage_blocks/refined_obsidian` filled |
| ingot_gold_cast | table | 90 mb `#c:molten_refined_obsidian` | `#tconstruct:casts/multi_use/ingot` | `#c:ingots/refined_obsidian` | 80 | requires tag `c:ingots/refined_obsidian` filled |
| ingot_sand_cast | table | 90 mb `#c:molten_refined_obsidian` | `#tconstruct:casts/single_use/ingot` (consumed) | `#c:ingots/refined_obsidian` | 80 | requires tag `c:ingots/refined_obsidian` filled |
| nugget_gold_cast | table | 10 mb `#c:molten_refined_obsidian` | `#tconstruct:casts/multi_use/nugget` | `#c:nuggets/refined_obsidian` | 27 | requires tag `c:nuggets/refined_obsidian` filled |
| nugget_sand_cast | table | 10 mb `#c:molten_refined_obsidian` | `#tconstruct:casts/single_use/nugget` (consumed) | `#c:nuggets/refined_obsidian` | 27 | requires tag `c:nuggets/refined_obsidian` filled |
| block | basin | 810 mb `#c:molten_rose_gold` |  | `#c:storage_blocks/rose_gold` | 155 |  |
| coin_gold_cast | table | 30 mb `#c:molten_rose_gold` | `#tconstruct:casts/multi_use/coin` | `#c:coins/rose_gold` | 30 | requires tag `c:coins/rose_gold` filled |
| coin_sand_cast | table | 30 mb `#c:molten_rose_gold` | `#tconstruct:casts/single_use/coin` (consumed) | `#c:coins/rose_gold` | 30 | requires tag `c:coins/rose_gold` filled |
| gear_gold_cast | table | 360 mb `#c:molten_rose_gold` | `#tconstruct:casts/multi_use/gear` | `#c:gears/rose_gold` | 103 | requires tag `c:gears/rose_gold` filled |
| gear_sand_cast | table | 360 mb `#c:molten_rose_gold` | `#tconstruct:casts/single_use/gear` (consumed) | `#c:gears/rose_gold` | 103 | requires tag `c:gears/rose_gold` filled |
| ingot_gold_cast | table | 90 mb `#c:molten_rose_gold` | `#tconstruct:casts/multi_use/ingot` | `#c:ingots/rose_gold` | 52 |  |
| ingot_sand_cast | table | 90 mb `#c:molten_rose_gold` | `#tconstruct:casts/single_use/ingot` (consumed) | `#c:ingots/rose_gold` | 52 |  |
| nugget_gold_cast | table | 10 mb `#c:molten_rose_gold` | `#tconstruct:casts/multi_use/nugget` | `#c:nuggets/rose_gold` | 17 |  |
| nugget_sand_cast | table | 10 mb `#c:molten_rose_gold` | `#tconstruct:casts/single_use/nugget` (consumed) | `#c:nuggets/rose_gold` | 17 |  |
| plate_gold_cast | table | 90 mb `#c:molten_rose_gold` | `#tconstruct:casts/multi_use/plate` | `#c:plates/rose_gold` | 52 | requires tag `c:plates/rose_gold` filled |
| plate_sand_cast | table | 90 mb `#c:molten_rose_gold` | `#tconstruct:casts/single_use/plate` (consumed) | `#c:plates/rose_gold` | 52 | requires tag `c:plates/rose_gold` filled |
| block | basin | 810 mb `#c:molten_signalum` |  | `#c:storage_blocks/signalum` | 199 | requires tag `c:storage_blocks/signalum` filled |
| coin_gold_cast | table | 30 mb `#c:molten_signalum` | `#tconstruct:casts/multi_use/coin` | `#c:coins/signalum` | 38 | requires tag `c:coins/signalum` filled |
| coin_sand_cast | table | 30 mb `#c:molten_signalum` | `#tconstruct:casts/single_use/coin` (consumed) | `#c:coins/signalum` | 38 | requires tag `c:coins/signalum` filled |
| gear_gold_cast | table | 360 mb `#c:molten_signalum` | `#tconstruct:casts/multi_use/gear` | `#c:gears/signalum` | 133 | requires tag `c:gears/signalum` filled |
| gear_sand_cast | table | 360 mb `#c:molten_signalum` | `#tconstruct:casts/single_use/gear` (consumed) | `#c:gears/signalum` | 133 | requires tag `c:gears/signalum` filled |
| ingot_gold_cast | table | 90 mb `#c:molten_signalum` | `#tconstruct:casts/multi_use/ingot` | `#c:ingots/signalum` | 66 | requires tag `c:ingots/signalum` filled |
| ingot_sand_cast | table | 90 mb `#c:molten_signalum` | `#tconstruct:casts/single_use/ingot` (consumed) | `#c:ingots/signalum` | 66 | requires tag `c:ingots/signalum` filled |
| nugget_gold_cast | table | 10 mb `#c:molten_signalum` | `#tconstruct:casts/multi_use/nugget` | `#c:nuggets/signalum` | 22 | requires tag `c:nuggets/signalum` filled |
| nugget_sand_cast | table | 10 mb `#c:molten_signalum` | `#tconstruct:casts/single_use/nugget` (consumed) | `#c:nuggets/signalum` | 22 | requires tag `c:nuggets/signalum` filled |
| plate_gold_cast | table | 90 mb `#c:molten_signalum` | `#tconstruct:casts/multi_use/plate` | `#c:plates/signalum` | 66 | requires tag `c:plates/signalum` filled |
| plate_sand_cast | table | 90 mb `#c:molten_signalum` | `#tconstruct:casts/single_use/plate` (consumed) | `#c:plates/signalum` | 66 | requires tag `c:plates/signalum` filled |
| block | basin | 810 mb `#c:molten_silver` |  | `#c:storage_blocks/silver` | 179 | requires tag `c:storage_blocks/silver` filled |
| coin_gold_cast | table | 30 mb `#c:molten_silver` | `#tconstruct:casts/multi_use/coin` | `#c:coins/silver` | 35 | requires tag `c:coins/silver` filled |
| coin_sand_cast | table | 30 mb `#c:molten_silver` | `#tconstruct:casts/single_use/coin` (consumed) | `#c:coins/silver` | 35 | requires tag `c:coins/silver` filled |
| gear_gold_cast | table | 360 mb `#c:molten_silver` | `#tconstruct:casts/multi_use/gear` | `#c:gears/silver` | 120 | requires tag `c:gears/silver` filled |
| gear_sand_cast | table | 360 mb `#c:molten_silver` | `#tconstruct:casts/single_use/gear` (consumed) | `#c:gears/silver` | 120 | requires tag `c:gears/silver` filled |
| ingot_gold_cast | table | 90 mb `#c:molten_silver` | `#tconstruct:casts/multi_use/ingot` | `#c:ingots/silver` | 60 | requires tag `c:ingots/silver` filled |
| ingot_sand_cast | table | 90 mb `#c:molten_silver` | `#tconstruct:casts/single_use/ingot` (consumed) | `#c:ingots/silver` | 60 | requires tag `c:ingots/silver` filled |
| nugget_gold_cast | table | 10 mb `#c:molten_silver` | `#tconstruct:casts/multi_use/nugget` | `#c:nuggets/silver` | 20 | requires tag `c:nuggets/silver` filled |
| nugget_sand_cast | table | 10 mb `#c:molten_silver` | `#tconstruct:casts/single_use/nugget` (consumed) | `#c:nuggets/silver` | 20 | requires tag `c:nuggets/silver` filled |
| plate_gold_cast | table | 90 mb `#c:molten_silver` | `#tconstruct:casts/multi_use/plate` | `#c:plates/silver` | 60 | requires tag `c:plates/silver` filled |
| plate_sand_cast | table | 90 mb `#c:molten_silver` | `#tconstruct:casts/single_use/plate` (consumed) | `#c:plates/silver` | 60 | requires tag `c:plates/silver` filled |
| block | basin | 810 mb `#tconstruct:molten_slimesteel` |  | `#c:storage_blocks/slimesteel` | 190 |  |
| ingot_gold_cast | table | 90 mb `#tconstruct:molten_slimesteel` | `#tconstruct:casts/multi_use/ingot` | `#c:ingots/slimesteel` | 63 |  |
| ingot_sand_cast | table | 90 mb `#tconstruct:molten_slimesteel` | `#tconstruct:casts/single_use/ingot` (consumed) | `#c:ingots/slimesteel` | 63 |  |
| nugget_gold_cast | table | 10 mb `#tconstruct:molten_slimesteel` | `#tconstruct:casts/multi_use/nugget` | `#c:nuggets/slimesteel` | 21 |  |
| nugget_sand_cast | table | 10 mb `#tconstruct:molten_slimesteel` | `#tconstruct:casts/single_use/nugget` (consumed) | `#c:nuggets/slimesteel` | 21 |  |
| block | basin | 810 mb `#c:molten_steel` |  | `#c:storage_blocks/steel` | 194 |  |
| coin_gold_cast | table | 30 mb `#c:molten_steel` | `#tconstruct:casts/multi_use/coin` | `#c:coins/steel` | 37 | requires tag `c:coins/steel` filled |
| coin_sand_cast | table | 30 mb `#c:molten_steel` | `#tconstruct:casts/single_use/coin` (consumed) | `#c:coins/steel` | 37 | requires tag `c:coins/steel` filled |
| gear_gold_cast | table | 360 mb `#c:molten_steel` | `#tconstruct:casts/multi_use/gear` | `#c:gears/steel` | 130 | requires tag `c:gears/steel` filled |
| gear_sand_cast | table | 360 mb `#c:molten_steel` | `#tconstruct:casts/single_use/gear` (consumed) | `#c:gears/steel` | 130 | requires tag `c:gears/steel` filled |
| ingot_gold_cast | table | 90 mb `#c:molten_steel` | `#tconstruct:casts/multi_use/ingot` | `#c:ingots/steel` | 65 |  |
| ingot_sand_cast | table | 90 mb `#c:molten_steel` | `#tconstruct:casts/single_use/ingot` (consumed) | `#c:ingots/steel` | 65 |  |
| nugget_gold_cast | table | 10 mb `#c:molten_steel` | `#tconstruct:casts/multi_use/nugget` | `#c:nuggets/steel` | 22 |  |
| nugget_sand_cast | table | 10 mb `#c:molten_steel` | `#tconstruct:casts/single_use/nugget` (consumed) | `#c:nuggets/steel` | 22 |  |
| plate_gold_cast | table | 90 mb `#c:molten_steel` | `#tconstruct:casts/multi_use/plate` | `#c:plates/steel` | 65 | requires tag `c:plates/steel` filled |
| plate_sand_cast | table | 90 mb `#c:molten_steel` | `#tconstruct:casts/single_use/plate` (consumed) | `#c:plates/steel` | 65 | requires tag `c:plates/steel` filled |
| rod_gold_cast | table | 45 mb `#c:molten_steel` | `#tconstruct:casts/multi_use/rod` | `#c:rods/steel` | 46 | requires tag `c:rods/steel` filled |
| rod_sand_cast | table | 45 mb `#c:molten_steel` | `#tconstruct:casts/single_use/rod` (consumed) | `#c:rods/steel` | 46 | requires tag `c:rods/steel` filled |
| wire_gold_cast | table | 45 mb `#c:molten_steel` | `#tconstruct:casts/multi_use/wire` | `#c:wires/steel` | 46 | requires tag `c:wires/steel` filled |
| wire_sand_cast | table | 45 mb `#c:molten_steel` | `#tconstruct:casts/single_use/wire` (consumed) | `#c:wires/steel` | 46 | requires tag `c:wires/steel` filled |
| block | basin | 810 mb `#tconstruct:molten_steeleaf` |  | `#c:storage_blocks/steeleaf` | 193 | requires tag `c:storage_blocks/steeleaf` filled |
| ingot_gold_cast | table | 90 mb `#tconstruct:molten_steeleaf` | `#tconstruct:casts/multi_use/ingot` | `#c:ingots/steeleaf` | 64 | requires tag `c:ingots/steeleaf` filled |
| ingot_sand_cast | table | 90 mb `#tconstruct:molten_steeleaf` | `#tconstruct:casts/single_use/ingot` (consumed) | `#c:ingots/steeleaf` | 64 | requires tag `c:ingots/steeleaf` filled |
| nugget_gold_cast | table | 10 mb `#tconstruct:molten_steeleaf` | `#tconstruct:casts/multi_use/nugget` | `#c:nuggets/steeleaf` | 21 | requires tag `c:nuggets/steeleaf` filled |
| nugget_sand_cast | table | 10 mb `#tconstruct:molten_steeleaf` | `#tconstruct:casts/single_use/nugget` (consumed) | `#c:nuggets/steeleaf` | 21 | requires tag `c:nuggets/steeleaf` filled |
| block | basin | 810 mb `#c:molten_tin` |  | `#c:storage_blocks/tin` | 117 | requires tag `c:storage_blocks/tin` filled |
| coin_gold_cast | table | 30 mb `#c:molten_tin` | `#tconstruct:casts/multi_use/coin` | `#c:coins/tin` | 23 | requires tag `c:coins/tin` filled |
| coin_sand_cast | table | 30 mb `#c:molten_tin` | `#tconstruct:casts/single_use/coin` (consumed) | `#c:coins/tin` | 23 | requires tag `c:coins/tin` filled |
| gear_gold_cast | table | 360 mb `#c:molten_tin` | `#tconstruct:casts/multi_use/gear` | `#c:gears/tin` | 78 | requires tag `c:gears/tin` filled |
| gear_sand_cast | table | 360 mb `#c:molten_tin` | `#tconstruct:casts/single_use/gear` (consumed) | `#c:gears/tin` | 78 | requires tag `c:gears/tin` filled |
| ingot_gold_cast | table | 90 mb `#c:molten_tin` | `#tconstruct:casts/multi_use/ingot` | `#c:ingots/tin` | 39 | requires tag `c:ingots/tin` filled |
| ingot_sand_cast | table | 90 mb `#c:molten_tin` | `#tconstruct:casts/single_use/ingot` (consumed) | `#c:ingots/tin` | 39 | requires tag `c:ingots/tin` filled |
| nugget_gold_cast | table | 10 mb `#c:molten_tin` | `#tconstruct:casts/multi_use/nugget` | `#c:nuggets/tin` | 13 | requires tag `c:nuggets/tin` filled |
| nugget_sand_cast | table | 10 mb `#c:molten_tin` | `#tconstruct:casts/single_use/nugget` (consumed) | `#c:nuggets/tin` | 13 | requires tag `c:nuggets/tin` filled |
| plate_gold_cast | table | 90 mb `#c:molten_tin` | `#tconstruct:casts/multi_use/plate` | `#c:plates/tin` | 39 | requires tag `c:plates/tin` filled |
| plate_sand_cast | table | 90 mb `#c:molten_tin` | `#tconstruct:casts/single_use/plate` (consumed) | `#c:plates/tin` | 39 | requires tag `c:plates/tin` filled |
| block | basin | 810 mb `#c:molten_tungsten` |  | `#c:storage_blocks/tungsten` | 194 | requires tag `c:storage_blocks/tungsten` filled |
| ingot_gold_cast | table | 90 mb `#c:molten_tungsten` | `#tconstruct:casts/multi_use/ingot` | `#c:ingots/tungsten` | 65 | requires tag `c:ingots/tungsten` filled |
| ingot_sand_cast | table | 90 mb `#c:molten_tungsten` | `#tconstruct:casts/single_use/ingot` (consumed) | `#c:ingots/tungsten` | 65 | requires tag `c:ingots/tungsten` filled |
| nugget_gold_cast | table | 10 mb `#c:molten_tungsten` | `#tconstruct:casts/multi_use/nugget` | `#c:nuggets/tungsten` | 22 | requires tag `c:nuggets/tungsten` filled |
| nugget_sand_cast | table | 10 mb `#c:molten_tungsten` | `#tconstruct:casts/single_use/nugget` (consumed) | `#c:nuggets/tungsten` | 22 | requires tag `c:nuggets/tungsten` filled |
| block | basin | 810 mb `#c:molten_uranium` |  | `#c:storage_blocks/uranium` | 183 | requires tag `c:storage_blocks/uranium` filled |
| coin_gold_cast | table | 30 mb `#c:molten_uranium` | `#tconstruct:casts/multi_use/coin` | `#c:coins/uranium` | 35 | requires tag `c:coins/uranium` filled |
| coin_sand_cast | table | 30 mb `#c:molten_uranium` | `#tconstruct:casts/single_use/coin` (consumed) | `#c:coins/uranium` | 35 | requires tag `c:coins/uranium` filled |
| gear_gold_cast | table | 360 mb `#c:molten_uranium` | `#tconstruct:casts/multi_use/gear` | `#c:gears/uranium` | 122 | requires tag `c:gears/uranium` filled |
| gear_sand_cast | table | 360 mb `#c:molten_uranium` | `#tconstruct:casts/single_use/gear` (consumed) | `#c:gears/uranium` | 122 | requires tag `c:gears/uranium` filled |
| ingot_gold_cast | table | 90 mb `#c:molten_uranium` | `#tconstruct:casts/multi_use/ingot` | `#c:ingots/uranium` | 61 | requires tag `c:ingots/uranium` filled |
| ingot_sand_cast | table | 90 mb `#c:molten_uranium` | `#tconstruct:casts/single_use/ingot` (consumed) | `#c:ingots/uranium` | 61 | requires tag `c:ingots/uranium` filled |
| nugget_gold_cast | table | 10 mb `#c:molten_uranium` | `#tconstruct:casts/multi_use/nugget` | `#c:nuggets/uranium` | 20 | requires tag `c:nuggets/uranium` filled |
| nugget_sand_cast | table | 10 mb `#c:molten_uranium` | `#tconstruct:casts/single_use/nugget` (consumed) | `#c:nuggets/uranium` | 20 | requires tag `c:nuggets/uranium` filled |
| plate_gold_cast | table | 90 mb `#c:molten_uranium` | `#tconstruct:casts/multi_use/plate` | `#c:plates/uranium` | 61 | requires tag `c:plates/uranium` filled |
| plate_sand_cast | table | 90 mb `#c:molten_uranium` | `#tconstruct:casts/single_use/plate` (consumed) | `#c:plates/uranium` | 61 | requires tag `c:plates/uranium` filled |
| block | basin | 810 mb `#c:molten_zinc` |  | `#c:storage_blocks/zinc` | 141 | requires tag `c:storage_blocks/zinc` filled |
| gear_gold_cast | table | 360 mb `#c:molten_zinc` | `#tconstruct:casts/multi_use/gear` | `#c:gears/zinc` | 94 | requires tag `c:gears/zinc` filled |
| gear_sand_cast | table | 360 mb `#c:molten_zinc` | `#tconstruct:casts/single_use/gear` (consumed) | `#c:gears/zinc` | 94 | requires tag `c:gears/zinc` filled |
| ingot_gold_cast | table | 90 mb `#c:molten_zinc` | `#tconstruct:casts/multi_use/ingot` | `#c:ingots/zinc` | 47 | requires tag `c:ingots/zinc` filled |
| ingot_sand_cast | table | 90 mb `#c:molten_zinc` | `#tconstruct:casts/single_use/ingot` (consumed) | `#c:ingots/zinc` | 47 | requires tag `c:ingots/zinc` filled |
| nugget_gold_cast | table | 10 mb `#c:molten_zinc` | `#tconstruct:casts/multi_use/nugget` | `#c:nuggets/zinc` | 16 | requires tag `c:nuggets/zinc` filled |
| nugget_sand_cast | table | 10 mb `#c:molten_zinc` | `#tconstruct:casts/single_use/nugget` (consumed) | `#c:nuggets/zinc` | 16 | requires tag `c:nuggets/zinc` filled |
| plate_gold_cast | table | 90 mb `#c:molten_zinc` | `#tconstruct:casts/multi_use/plate` | `#c:plates/zinc` | 47 | requires tag `c:plates/zinc` filled |
| plate_sand_cast | table | 90 mb `#c:molten_zinc` | `#tconstruct:casts/single_use/plate` (consumed) | `#c:plates/zinc` | 47 | requires tag `c:plates/zinc` filled |

## obsidian

| Recipe | Where | Fluid | Cast | Result | Cooling | Notes |
|---|---|---|---|---|---|---|
| block | basin | 1000 mb `#tconstruct:molten_obsidian` |  | `minecraft:obsidian` | 221 |  |
| chest | basin | 8000 mb `#tconstruct:molten_obsidian` | `minecraft:ender_eye` (consumed) | `minecraft:ender_chest` | 625 |  |
| nahuatl | basin | 250 mb `#tconstruct:molten_obsidian` | `#minecraft:planks` (consumed) | `tconstruct:nahuatl` | 111 |  |
| nahuatl_fence | basin | 250 mb `#tconstruct:molten_obsidian` | `#minecraft:wooden_fences` (consumed) | `tconstruct:nahuatl_fence` | 111 |  |
| nahuatl_slab | basin | 125 mb `#tconstruct:molten_obsidian` | `#minecraft:wooden_slabs` (consumed) | `tconstruct:nahuatl_slab` | 78 |  |
| nahuatl_stairs | basin | 250 mb `#tconstruct:molten_obsidian` | `#minecraft:wooden_stairs` (consumed) | `tconstruct:nahuatl_stairs` | 111 |  |
| pane | table | 250 mb `#tconstruct:molten_obsidian` |  | `tconstruct:obsidian_pane` | 111 |  |

## quartz

| Recipe | Where | Fluid | Cast | Result | Cooling | Notes |
|---|---|---|---|---|---|---|
| andesite | basin | 50 mb `#tconstruct:molten_quartz` | `#c:cobblestones` (consumed) | `minecraft:andesite` | 41 |  |
| block | basin | 400 mb `#tconstruct:molten_quartz` |  | `#c:storage_blocks/quartz` | 115 |  |
| diorite | basin | 50 mb `#tconstruct:molten_quartz` | `minecraft:andesite` (consumed) | `minecraft:diorite` | 41 |  |
| gear_gold_cast | table | 400 mb `#tconstruct:molten_quartz` | `#tconstruct:casts/multi_use/gear` | `#c:gears/quartz` | 115 | requires tag `c:gears/quartz` filled |
| gear_sand_cast | table | 400 mb `#tconstruct:molten_quartz` | `#tconstruct:casts/single_use/gear` (consumed) | `#c:gears/quartz` | 115 | requires tag `c:gears/quartz` filled |
| gem_gold_cast | table | 100 mb `#tconstruct:molten_quartz` | `#tconstruct:casts/multi_use/gem` | `#c:gems/quartz` | 58 |  |
| gem_sand_cast | table | 100 mb `#tconstruct:molten_quartz` | `#tconstruct:casts/single_use/gem` (consumed) | `#c:gems/quartz` | 58 |  |
| granite | basin | 100 mb `#tconstruct:molten_quartz` | `minecraft:diorite` (consumed) | `minecraft:granite` | 58 |  |

## scorched

| Recipe | Where | Fluid | Cast | Result | Cooling | Notes |
|---|---|---|---|---|---|---|
| brick_composite | table | 125 mb `#c:magma` | `minecraft:flint` (consumed) | `tconstruct:scorched_brick` | 50 |  |
| brick_gold_cast | table | 250 mb `#tconstruct:scorched_stone` | `#tconstruct:casts/multi_use/ingot` | `tconstruct:scorched_brick` | 83 |  |
| brick_sand_cast | table | 250 mb `#tconstruct:scorched_stone` | `#tconstruct:casts/single_use/ingot` (consumed) | `tconstruct:scorched_brick` | 83 |  |
| foundry_controller | basin | 1000 mb `#tconstruct:molten_obsidian` | `#tconstruct:foundry_bricks` (consumed) | `tconstruct:foundry_controller` | 221 |  |
| glass | basin | 100 mb `#tconstruct:molten_quartz` | `tconstruct:scorched_bricks` (consumed) | `tconstruct:scorched_glass` | 58 |  |
| glass_pane | table | 25 mb `#tconstruct:molten_quartz` | `tconstruct:scorched_brick` (consumed) | `tconstruct:scorched_glass_pane` | 29 |  |
| glass_pane_soul | table | 250 mb `#tconstruct:scorched_stone` | `tconstruct:soul_glass_pane` (consumed) | `tconstruct:scorched_soul_glass_pane` | 83 |  |
| glass_soul | basin | 1000 mb `#tconstruct:scorched_stone` | `tconstruct:soul_glass` (consumed) | `tconstruct:scorched_soul_glass` | 166 |  |
| glass_tinted | basin | 1000 mb `#tconstruct:scorched_stone` | `#c:glass/tinted` (consumed) | `tconstruct:scorched_tinted_glass` | 166 |  |
| lamp | basin | 1000 mb `#tconstruct:scorched_stone` | `minecraft:glowstone` (consumed) | `tconstruct:scorched_lamp` | 166 |  |
| polished_from_magma | basin | 500 mb `#c:magma` | `minecraft:polished_basalt` (consumed) | `tconstruct:polished_scorched_stone` | 99 |  |
| stone_from_magma | basin | 500 mb `#c:magma` | `minecraft:basalt` / `minecraft:gravel` (consumed) | `tconstruct:scorched_stone` | 99 |  |
| stone_from_scorched | basin | 1000 mb `#tconstruct:scorched_stone` |  | `tconstruct:scorched_stone` | 166 |  |

## seared

| Recipe | Where | Fluid | Cast | Result | Cooling | Notes |
|---|---|---|---|---|---|---|
| brick_composite | table | 125 mb `#tconstruct:molten_clay` | `minecraft:flint` (consumed) | `tconstruct:seared_brick` | 57 |  |
| brick_gold_cast | table | 250 mb `#tconstruct:seared_stone` | `#tconstruct:casts/multi_use/ingot` | `tconstruct:seared_brick` | 89 |  |
| brick_sand_cast | table | 250 mb `#tconstruct:seared_stone` | `#tconstruct:casts/single_use/ingot` (consumed) | `tconstruct:seared_brick` | 89 |  |
| block | basin | 500 mb `#tconstruct:molten_clay` | `minecraft:stone_bricks` (consumed) | `tconstruct:seared_bricks` | 113 |  |
| slab | basin | 250 mb `#tconstruct:molten_clay` | `minecraft:stone_brick_slab` (consumed) | `tconstruct:seared_bricks_slab` | 80 |  |
| stairs | basin | 500 mb `#tconstruct:molten_clay` | `minecraft:stone_brick_stairs` (consumed) | `tconstruct:seared_bricks_stairs` | 113 |  |
| wall | basin | 500 mb `#tconstruct:molten_clay` | `minecraft:stone_brick_wall` (consumed) | `tconstruct:seared_bricks_wall` | 113 |  |
| chiseled | basin | 500 mb `#tconstruct:molten_clay` | `minecraft:chiseled_stone_bricks` (consumed) | `tconstruct:seared_fancy_bricks` | 113 |  |
| block | basin | 500 mb `#tconstruct:molten_clay` | `#c:cobblestones` / `minecraft:gravel` (consumed) | `tconstruct:seared_cobble` | 113 |  |
| slab | basin | 250 mb `#tconstruct:molten_clay` | `minecraft:cobblestone_slab` (consumed) | `tconstruct:seared_cobble_slab` | 80 |  |
| stairs | basin | 500 mb `#tconstruct:molten_clay` | `minecraft:cobblestone_stairs` (consumed) | `tconstruct:seared_cobble_stairs` | 113 |  |
| wall | basin | 500 mb `#tconstruct:molten_clay` | `minecraft:cobblestone_wall` (consumed) | `tconstruct:seared_cobble_wall` | 113 |  |
| cracked | basin | 500 mb `#tconstruct:molten_clay` | `minecraft:cracked_stone_bricks` (consumed) | `tconstruct:seared_cracked_bricks` | 113 |  |
| glass | basin | 1000 mb `#tconstruct:seared_stone` | `#c:glass/colorless` (consumed) | `tconstruct:seared_glass` | 178 |  |
| glass_pane | table | 250 mb `#tconstruct:seared_stone` | `#c:glass_panes/colorless` (consumed) | `tconstruct:seared_glass_pane` | 89 |  |
| glass_pane_soul | table | 250 mb `#tconstruct:seared_stone` | `tconstruct:soul_glass_pane` (consumed) | `tconstruct:seared_soul_glass_pane` | 89 |  |
| glass_soul | basin | 1000 mb `#tconstruct:seared_stone` | `tconstruct:soul_glass` (consumed) | `tconstruct:seared_soul_glass` | 178 |  |
| glass_tinted | basin | 1000 mb `#tconstruct:seared_stone` | `#c:glass/tinted` (consumed) | `tconstruct:seared_tinted_glass` | 178 |  |
| lamp | basin | 1000 mb `#tconstruct:seared_stone` | `minecraft:glowstone` (consumed) | `tconstruct:seared_lamp` | 178 |  |
| paver | basin | 500 mb `#tconstruct:molten_clay` | `minecraft:smooth_stone` (consumed) | `tconstruct:seared_paver` | 113 |  |
| smeltery_controller | basin | 360 mb `#c:molten_copper` | `#tconstruct:smeltery_bricks` (consumed) | `tconstruct:smeltery_controller` | 100 |  |
| block_from_clay | basin | 500 mb `#tconstruct:molten_clay` | `#c:stones` (consumed) | `tconstruct:seared_stone` | 113 |  |
| block_from_seared | basin | 1000 mb `#tconstruct:seared_stone` |  | `tconstruct:seared_stone` | 178 |  |
| slab | basin | 250 mb `#tconstruct:molten_clay` | `minecraft:stone_slab` (consumed) | `tconstruct:seared_stone_slab` | 80 |  |
| stairs | basin | 500 mb `#tconstruct:molten_clay` | `minecraft:stone_stairs` (consumed) | `tconstruct:seared_stone_stairs` | 113 |  |

## slime

| Recipe | Where | Fluid | Cast | Result | Cooling | Notes |
|---|---|---|---|---|---|---|
| block | basin | 1250 mb `#c:slime` | `tconstruct:earth_congealed_slime` (consumed) | `minecraft:slime_block` | 115 |  |
| bottle | table | 250 mb `#c:slime` | `minecraft:glass_bottle` (consumed) | `tconstruct:earth_slime_bottle` | 1 |  |
| congealed | basin | 1000 mb `#c:slime` |  | `tconstruct:earth_congealed_slime` | 103 |  |
| dirt | basin | 500 mb `#c:slime` | `minecraft:dirt` (consumed) | `tconstruct:earth_slime_dirt` | 73 |  |
| roots | basin | 1000 mb `#c:slime` | `tconstruct:enderbark_roots` (consumed) | `tconstruct:earth_enderbark_roots` | 103 |  |
| slimeball | table | 250 mb `#c:slime` |  | `minecraft:slime_ball` | 51 |  |
| block | basin | 1250 mb `#tconstruct:ender_slime` | `tconstruct:ender_congealed_slime` (consumed) | `tconstruct:ender_slime` | 119 |  |
| bottle | table | 250 mb `#tconstruct:ender_slime` | `minecraft:glass_bottle` (consumed) | `tconstruct:ender_slime_bottle` | 1 |  |
| congealed | basin | 1000 mb `#tconstruct:ender_slime` |  | `tconstruct:ender_congealed_slime` | 106 |  |
| dirt | basin | 500 mb `#tconstruct:ender_slime` | `minecraft:dirt` (consumed) | `tconstruct:ender_slime_dirt` | 75 |  |
| roots | basin | 1000 mb `#tconstruct:ender_slime` | `tconstruct:enderbark_roots` (consumed) | `tconstruct:ender_enderbark_roots` | 106 |  |
| slimeball | table | 250 mb `#tconstruct:ender_slime` |  | `tconstruct:ender_slime_ball` | 53 |  |
| block | basin | 1250 mb `#tconstruct:ichor` | `tconstruct:ichor_congealed_slime` (consumed) | `tconstruct:ichor_slime` | 212 |  |
| bottle | table | 250 mb `#tconstruct:ichor` | `minecraft:glass_bottle` (consumed) | `tconstruct:ichor_slime_bottle` | 1 |  |
| congealed | basin | 1000 mb `#tconstruct:ichor` |  | `tconstruct:ichor_congealed_slime` | 190 |  |
| dirt | basin | 500 mb `#tconstruct:ichor` | `minecraft:dirt` (consumed) | `tconstruct:ichor_slime_dirt` | 134 |  |
| roots | basin | 1000 mb `#tconstruct:ichor` | `tconstruct:enderbark_roots` (consumed) | `tconstruct:ichor_enderbark_roots` | 190 |  |
| slimeball | table | 250 mb `#tconstruct:ichor` |  | `tconstruct:ichor_slime_ball` | 95 |  |
| magma_block | basin | 1000 mb `#c:magma` |  | `minecraft:magma_block` | 141 |  |
| magma_bottle | table | 250 mb `#c:magma` | `minecraft:glass_bottle` (consumed) | `tconstruct:magma_bottle` | 1 |  |
| block | basin | 1250 mb `#tconstruct:sky_slime` | `tconstruct:sky_congealed_slime` (consumed) | `tconstruct:sky_slime` | 107 |  |
| bottle | table | 250 mb `#tconstruct:sky_slime` | `minecraft:glass_bottle` (consumed) | `tconstruct:sky_slime_bottle` | 1 |  |
| congealed | basin | 1000 mb `#tconstruct:sky_slime` |  | `tconstruct:sky_congealed_slime` | 96 |  |
| dirt | basin | 500 mb `#tconstruct:sky_slime` | `minecraft:dirt` (consumed) | `tconstruct:sky_slime_dirt` | 68 |  |
| roots | basin | 1000 mb `#tconstruct:sky_slime` | `tconstruct:enderbark_roots` (consumed) | `tconstruct:sky_enderbark_roots` | 96 |  |
| slimeball | table | 250 mb `#tconstruct:sky_slime` |  | `tconstruct:sky_slime_ball` | 48 |  |
| bone | table | 250 mb `#tconstruct:venom` | `#c:bones` (consumed) | `tconstruct:venombone` | 48 |  |
| bottle | table | 250 mb `#tconstruct:venom` | `minecraft:glass_bottle` (consumed) | `tconstruct:venom_bottle` | 1 |  |
| skull | basin | 1000 mb `#tconstruct:venom` | `minecraft:skeleton_skull` (consumed) | `tconstruct:venombone_head` | 96 |  |

## smeltery/casting

| Recipe | Where | Fluid | Cast | Result | Cooling | Notes |
|---|---|---|---|---|---|---|
| blazewood | basin | 200 mb `#tconstruct:blazing_blood` | `tconstruct:bloodshroom_planks` (consumed) | `tconstruct:blazewood` | 120 |  |
| blazewood_fence | basin | 200 mb `#tconstruct:blazing_blood` | `tconstruct:bloodshroom_fence` (consumed) | `tconstruct:blazewood_fence` | 120 |  |
| blazewood_slab | basin | 100 mb `#tconstruct:blazing_blood` | `tconstruct:bloodshroom_planks_slab` (consumed) | `tconstruct:blazewood_slab` | 85 |  |
| blazewood_stairs | basin | 200 mb `#tconstruct:blazing_blood` | `tconstruct:bloodshroom_planks_stairs` (consumed) | `tconstruct:blazewood_stairs` | 120 |  |
| bone_purifying | table | 200 mb `#c:milk` | `#c:wither_bones` (consumed) | `minecraft:bone` | 50 |  |
| cheese_block | basin | 1000 mb `#c:milk` |  | `tconstruct:cheese_block` | 6000 |  |
| cheese_ingot_gold_cast | table | 250 mb `#c:milk` | `#tconstruct:casts/multi_use/ingot` | `tconstruct:cheese_ingot` | 2400 |  |
| cheese_ingot_sand_cast | table | 250 mb `#c:milk` | `#tconstruct:casts/single_use/ingot` (consumed) | `tconstruct:cheese_ingot` | 2400 |  |

## smeltery/casts

| Recipe | Where | Fluid | Cast | Result | Cooling | Notes |
|---|---|---|---|---|---|---|
| adze_head | table | 90 mb `#c:molten_gold` | `tconstruct:adze_head` (consumed) | `tconstruct:adze_head_cast` | 57 | switch slots |
| arrow | table | 90 mb `#c:molten_gold` | `#minecraft:arrows` (consumed) | `tconstruct:arrow_cast` | 57 |  |
| boots_plating | table | 90 mb `#c:molten_gold` | `{"fabric:type":"fabric:any","ingredients":[{"item":"tconstruct:boots_plating_dummy"},{"fabric:type":"tconstruct:material","item":"tconstruct:boots_plating"}]}` (consumed) | `tconstruct:boots_plating_cast` | 57 | switch slots |
| bow_grip | table | 90 mb `#c:molten_gold` | `tconstruct:bow_grip` (consumed) | `tconstruct:bow_grip_cast` | 57 | switch slots |
| bow_limb | table | 90 mb `#c:molten_gold` | `tconstruct:bow_limb` (consumed) | `tconstruct:bow_limb_cast` | 57 | switch slots |
| broad_axe_head | table | 90 mb `#c:molten_gold` | `tconstruct:broad_axe_head` (consumed) | `tconstruct:broad_axe_head_cast` | 57 | switch slots |
| broad_blade | table | 90 mb `#c:molten_gold` | `tconstruct:broad_blade` (consumed) | `tconstruct:broad_blade_cast` | 57 | switch slots |
| chestplate_plating | table | 90 mb `#c:molten_gold` | `{"fabric:type":"fabric:any","ingredients":[{"item":"tconstruct:chestplate_plating_dummy"},{"fabric:type":"tconstruct:material","item":"tconstruct:chestplate_plating"}]}` (consumed) | `tconstruct:chestplate_plating_cast` | 57 | switch slots |
| coins | table | 90 mb `#c:molten_gold` | `#c:coins` (consumed) | `tconstruct:coin_cast` | 57 | switch slots; requires tag `c:coins` filled |
| gears | table | 90 mb `#c:molten_gold` | `#c:gears` (consumed) | `tconstruct:gear_cast` | 57 | switch slots; requires tag `c:gears` filled |
| gems | table | 90 mb `#c:molten_gold` | `#c:gems` (consumed) | `tconstruct:gem_cast` | 57 | switch slots |
| hammer_head | table | 90 mb `#c:molten_gold` | `tconstruct:hammer_head` (consumed) | `tconstruct:hammer_head_cast` | 57 | switch slots |
| helmet_plating | table | 90 mb `#c:molten_gold` | `{"fabric:type":"fabric:any","ingredients":[{"item":"tconstruct:helmet_plating_dummy"},{"fabric:type":"tconstruct:material","item":"tconstruct:helmet_plating"}]}` (consumed) | `tconstruct:helmet_plating_cast` | 57 | switch slots |
| ingots | table | 90 mb `#c:molten_gold` | `{"fabric:type":"fabric:any","ingredients":[{"fabric:type":"fabric:difference","base":{"tag":"c:ingots"},"subtracted":{"item":"tconstruct:fake_ingot"}},{"fabric:type":"tconstruct:material","item":"tconstruct:fake_ingot"}]}` (consumed) | `tconstruct:ingot_cast` | 57 | switch slots |
| large_plate | table | 90 mb `#c:molten_gold` | `tconstruct:large_plate` (consumed) | `tconstruct:large_plate_cast` | 57 | switch slots |
| leggings_plating | table | 90 mb `#c:molten_gold` | `{"fabric:type":"fabric:any","ingredients":[{"item":"tconstruct:leggings_plating_dummy"},{"fabric:type":"tconstruct:material","item":"tconstruct:leggings_plating"}]}` (consumed) | `tconstruct:leggings_plating_cast` | 57 | switch slots |
| maille | table | 90 mb `#c:molten_gold` | `tconstruct:maille` (consumed) | `tconstruct:maille_cast` | 57 | switch slots |
| nuggets | table | 90 mb `#c:molten_gold` | `#c:nuggets` (consumed) | `tconstruct:nugget_cast` | 57 | switch slots |
| pick_head | table | 90 mb `#c:molten_gold` | `tconstruct:pick_head` (consumed) | `tconstruct:pick_head_cast` | 57 | switch slots |
| plates | table | 90 mb `#c:molten_gold` | `#c:plates` (consumed) | `tconstruct:plate_cast` | 57 | switch slots; requires tag `c:plates` filled |
| repair_kit | table | 90 mb `#c:molten_gold` | `tconstruct:repair_kit` (consumed) | `tconstruct:repair_kit_cast` | 57 | switch slots |
| rods | table | 90 mb `#c:molten_gold` | `#c:rods` (consumed) | `tconstruct:rod_cast` | 57 | switch slots |
| small_axe_head | table | 90 mb `#c:molten_gold` | `tconstruct:small_axe_head` (consumed) | `tconstruct:small_axe_head_cast` | 57 | switch slots |
| small_blade | table | 90 mb `#c:molten_gold` | `tconstruct:small_blade` (consumed) | `tconstruct:small_blade_cast` | 57 | switch slots |
| tool_binding | table | 90 mb `#c:molten_gold` | `tconstruct:tool_binding` (consumed) | `tconstruct:tool_binding_cast` | 57 | switch slots |
| tool_handle | table | 90 mb `#c:molten_gold` | `tconstruct:tool_handle` (consumed) | `tconstruct:tool_handle_cast` | 57 | switch slots |
| tough_binding | table | 90 mb `#c:molten_gold` | `tconstruct:tough_binding` (consumed) | `tconstruct:tough_binding_cast` | 57 | switch slots |
| tough_handle | table | 90 mb `#c:molten_gold` | `tconstruct:tough_handle` (consumed) | `tconstruct:tough_handle_cast` | 57 | switch slots |
| wires | table | 90 mb `#c:molten_gold` | `#c:wires` (consumed) | `tconstruct:wire_cast` | 57 | switch slots; requires tag `c:wires` filled |

## soul

| Recipe | Where | Fluid | Cast | Result | Cooling | Notes |
|---|---|---|---|---|---|---|
| glass | basin | 1000 mb `#tconstruct:liquid_soul` |  | `tconstruct:soul_glass` | 154 |  |
| pane | table | 250 mb `#tconstruct:liquid_soul` |  | `tconstruct:soul_glass_pane` | 77 |  |

## soup

| Recipe | Where | Fluid | Cast | Result | Cooling | Notes |
|---|---|---|---|---|---|---|
| beetroot | table | 250 mb `#c:beetroot_soup` | `minecraft:bowl` (consumed) | `minecraft:beetroot_soup` | 1 |  |
| meat | table | 250 mb `#tconstruct:meat_soup` | `minecraft:bowl` (consumed) | `tconstruct:meat_soup` | 1 |  |
| mushroom | table | 250 mb `#c:mushroom_stew` | `minecraft:bowl` (consumed) | `minecraft:mushroom_stew` | 1 |  |
| rabbit | table | 250 mb `#c:rabbit_stew` | `minecraft:bowl` (consumed) | `minecraft:rabbit_stew` | 1 |  |

## tools/modifiers

| Recipe | Where | Fluid | Cast | Result | Cooling | Notes |
|---|---|---|---|---|---|---|
| cobalt_reinforcement | table | 90 mb `#c:molten_cobalt` | `tconstruct:pattern` (consumed) | `tconstruct:cobalt_reinforcement` | 65 |  |
| emerald_reinforcement | table | 25 mb `#tconstruct:molten_emerald` | `tconstruct:obsidian_pane` (consumed) | `tconstruct:emerald_reinforcement` | 34 |  |
| gold_reinforcement | table | 90 mb `#c:molten_gold` | `tconstruct:pattern` (consumed) | `tconstruct:gold_reinforcement` | 57 |  |
| iron_reinforcement | table | 90 mb `#c:molten_iron` | `tconstruct:pattern` (consumed) | `tconstruct:iron_reinforcement` | 60 |  |
| jeweled_apple | table | 200 mb `#tconstruct:molten_diamond` | `minecraft:apple` (consumed) | `tconstruct:jeweled_apple` | 118 |  |
| obsidian_reinforcement | table | 1000 mb `#tconstruct:molten_obsidian` | `tconstruct:pattern` (consumed) | `tconstruct:obsidian_reinforcement` | 221 |  |
| seared_reinforcement | table | 250 mb `#tconstruct:seared_stone` / 250 mb `#tconstruct:scorched_stone` | `tconstruct:pattern` (consumed) | `tconstruct:seared_reinforcement` | 89 |  |
| silky_cloth | table | 90 mb `#c:molten_rose_gold` | `minecraft:cobweb` (consumed) | `tconstruct:silky_cloth` | 52 |  |
| slimesteel_reinforcement | table | 30 mb `#tconstruct:molten_slimesteel` | `tconstruct:obsidian_pane` (consumed) | `tconstruct:slimesteel_reinforcement` | 37 |  |

## water

| Recipe | Where | Fluid | Cast | Result | Cooling | Notes |
|---|---|---|---|---|---|---|
| black_concrete | basin | 100 mb `minecraft:water` | `minecraft:black_concrete_powder` (consumed) | `minecraft:black_concrete` | 30 |  |
| blue_concrete | basin | 100 mb `minecraft:water` | `minecraft:blue_concrete_powder` (consumed) | `minecraft:blue_concrete` | 30 |  |
| bottle | table | 500 mb `#mantle:water` | `minecraft:glass_bottle` (consumed) | `minecraft:potion` | 1 |  |
| brown_concrete | basin | 100 mb `minecraft:water` | `minecraft:brown_concrete_powder` (consumed) | `minecraft:brown_concrete` | 30 |  |
| cyan_concrete | basin | 100 mb `minecraft:water` | `minecraft:cyan_concrete_powder` (consumed) | `minecraft:cyan_concrete` | 30 |  |
| gray_concrete | basin | 100 mb `minecraft:water` | `minecraft:gray_concrete_powder` (consumed) | `minecraft:gray_concrete` | 30 |  |
| green_concrete | basin | 100 mb `minecraft:water` | `minecraft:green_concrete_powder` (consumed) | `minecraft:green_concrete` | 30 |  |
| light_blue_concrete | basin | 100 mb `minecraft:water` | `minecraft:light_blue_concrete_powder` (consumed) | `minecraft:light_blue_concrete` | 30 |  |
| light_gray_concrete | basin | 100 mb `minecraft:water` | `minecraft:light_gray_concrete_powder` (consumed) | `minecraft:light_gray_concrete` | 30 |  |
| lime_concrete | basin | 100 mb `minecraft:water` | `minecraft:lime_concrete_powder` (consumed) | `minecraft:lime_concrete` | 30 |  |
| lingering | table | 500 mb `#mantle:water` | `#c:bottles/lingering` (consumed) | `minecraft:lingering_potion` | 1 |  |
| magenta_concrete | basin | 100 mb `minecraft:water` | `minecraft:magenta_concrete_powder` (consumed) | `minecraft:magenta_concrete` | 30 |  |
| mud | basin | 250 mb `minecraft:water` | `#minecraft:convertable_to_mud` (consumed) | `minecraft:mud` | 47 |  |
| orange_concrete | basin | 100 mb `minecraft:water` | `minecraft:orange_concrete_powder` (consumed) | `minecraft:orange_concrete` | 30 |  |
| pink_concrete | basin | 100 mb `minecraft:water` | `minecraft:pink_concrete_powder` (consumed) | `minecraft:pink_concrete` | 30 |  |
| purple_concrete | basin | 100 mb `minecraft:water` | `minecraft:purple_concrete_powder` (consumed) | `minecraft:purple_concrete` | 30 |  |
| red_concrete | basin | 100 mb `minecraft:water` | `minecraft:red_concrete_powder` (consumed) | `minecraft:red_concrete` | 30 |  |
| splash | table | 500 mb `#mantle:water` | `#c:bottles/splash` (consumed) | `minecraft:splash_potion` | 1 |  |
| wet_sponge | basin | 250 mb `minecraft:water` | `minecraft:sponge` (consumed) | `minecraft:wet_sponge` | 1 |  |
| white_concrete | basin | 100 mb `minecraft:water` | `minecraft:white_concrete_powder` (consumed) | `minecraft:white_concrete` | 30 |  |
| yellow_concrete | basin | 100 mb `minecraft:water` | `minecraft:yellow_concrete_powder` (consumed) | `minecraft:yellow_concrete` | 30 |  |

