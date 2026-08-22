# Melting

Everything the smeltery and foundry melt down. Amounts are millibuckets; 90 mb is one
ingot, 810 mb one block, 10 mb one nugget. `#` marks a tag. Ore melting multiplies its
output by the ore rate config (default: smeltery 12/9, foundry 9/9 with byproducts).
A condition means the recipe only loads when it holds, which is how the compat metals
switch on when another mod provides them.

## amethyst

| Recipe | Input | Result | Temp (C) | Time | Notes |
|---|---|---|---|---|---|
| amethyst/block | `#c:storage_blocks/amethyst` | 400 mb `tconstruct:molten_amethyst` | 950 | 130 |  |
| amethyst/bud_large | `minecraft:large_amethyst_bud` | 300 mb `tconstruct:molten_amethyst` | 950 | 194 | ore rate: gem; byproduct 300 mb `tconstruct:molten_quartz` |
| amethyst/bud_medium | `minecraft:medium_amethyst_bud` | 200 mb `tconstruct:molten_amethyst` | 950 | 130 | ore rate: gem; byproduct 200 mb `tconstruct:molten_quartz` |
| amethyst/bud_small | `minecraft:small_amethyst_bud` | 100 mb `tconstruct:molten_amethyst` | 950 | 65 | ore rate: gem; byproduct 100 mb `tconstruct:molten_quartz` |
| amethyst/cluster | `minecraft:amethyst_cluster` | 400 mb `tconstruct:molten_amethyst` | 950 | 259 | ore rate: gem; byproduct 400 mb `tconstruct:molten_quartz` |
| amethyst/gem | `#c:gems/amethyst` | 100 mb `tconstruct:molten_amethyst` | 950 | 65 |  |
| amethyst/spyglass | `minecraft:spyglass` | 100 mb `tconstruct:molten_amethyst` | 950 | 68 | byproduct 180 mb `#c:molten_copper` |
| amethyst/tinted_glass | `minecraft:tinted_glass` / `tconstruct:clear_tinted_glass` | 200 mb `tconstruct:molten_amethyst` | 950 | 97 | byproduct 500 mb `tconstruct:molten_glass` |

## clay

| Recipe | Input | Result | Temp (C) | Time | Notes |
|---|---|---|---|---|---|
| clay/ball | `minecraft:clay_ball` | 250 mb `tconstruct:molten_clay` | 450 | 24 |  |
| clay/block | `minecraft:clay` | 1000 mb `tconstruct:molten_clay` | 450 | 48 |  |
| clay/brick | `minecraft:brick` / `#minecraft:decorated_pot_sherds` | 250 mb `tconstruct:molten_clay` | 450 | 48 |  |
| clay/brick_slab | `minecraft:brick_slab` | 500 mb `tconstruct:molten_clay` | 450 | 72 |  |
| clay/plate | `#c:plates/brick` | 250 mb `tconstruct:molten_clay` | 450 | 48 | requires tag `c:plates/brick` filled |
| clay/pot | `minecraft:flower_pot` | 750 mb `tconstruct:molten_clay` | 450 | 96 |  |
| clay/terracotta | `minecraft:terracotta` / `minecraft:bricks` / `minecraft:brick_wall` / `minecraft:brick_stairs` / `minecraft:white_terracotta` / `minecraft:orange_terracotta` / `minecraft:magenta_terracotta` / `minecraft:light_blue_terracotta` / `minecraft:yellow_terracotta` / `minecraft:lime_terracotta` / `minecraft:pink_terracotta` / `minecraft:gray_terracotta` / `minecraft:light_gray_terracotta` / `minecraft:cyan_terracotta` / `minecraft:purple_terracotta` / `minecraft:blue_terracotta` / `minecraft:brown_terracotta` / `minecraft:green_terracotta` / `minecraft:red_terracotta` / `minecraft:black_terracotta` / `minecraft:white_glazed_terracotta` / `minecraft:orange_glazed_terracotta` / `minecraft:magenta_glazed_terracotta` / `minecraft:light_blue_glazed_terracotta` / `minecraft:yellow_glazed_terracotta` / `minecraft:lime_glazed_terracotta` / `minecraft:pink_glazed_terracotta` / `minecraft:gray_glazed_terracotta` / `minecraft:light_gray_glazed_terracotta` / `minecraft:cyan_glazed_terracotta` / `minecraft:purple_glazed_terracotta` / `minecraft:blue_glazed_terracotta` / `minecraft:brown_glazed_terracotta` / `minecraft:green_glazed_terracotta` / `minecraft:red_glazed_terracotta` / `minecraft:black_glazed_terracotta` / `minecraft:decorated_pot` | 1000 mb `tconstruct:molten_clay` | 450 | 96 |  |

## diamond

| Recipe | Input | Result | Temp (C) | Time | Notes |
|---|---|---|---|---|---|
| diamond/axes | `#tconstruct:melting/diamond/tools_costing_3` | 300 mb `tconstruct:molten_diamond` | 1450 | 137 | scales with damage |
| diamond/block | `#c:storage_blocks/diamond` | 900 mb `tconstruct:molten_diamond` | 1450 | 237 |  |
| diamond/boots | `#tconstruct:melting/diamond/tools_costing_4` | 400 mb `tconstruct:molten_diamond` | 1450 | 158 | scales with damage |
| diamond/chestplate | `minecraft:diamond_chestplate` | 800 mb `tconstruct:molten_diamond` | 1450 | 223 | scales with damage |
| diamond/dust | `#c:dusts/diamond` | 100 mb `tconstruct:molten_diamond` | 1450 | 59 | requires tag `c:dusts/diamond` filled |
| diamond/enchanting_table | `minecraft:enchanting_table` | 200 mb `tconstruct:molten_diamond` | 1450 | 118 | byproduct 4000 mb `tconstruct:molten_obsidian` |
| diamond/gear | `#c:gears/diamond` | 400 mb `tconstruct:molten_diamond` | 1450 | 158 | requires tag `c:gears/diamond` filled |
| diamond/gem | `#c:gems/diamond` | 100 mb `tconstruct:molten_diamond` | 1450 | 79 |  |
| diamond/geore/block | `#c:geore_blocks/diamond` | 400 mb `tconstruct:molten_diamond` | 1450 | 158 | requires tag `c:geore_blocks/diamond` filled |
| diamond/geore/bud_large | `#c:geore_large_buds/diamond` | 300 mb `tconstruct:molten_diamond` | 1450 | 158 | ore rate: gem; byproduct 270 mb `tconstruct:molten_debris`; requires tag `c:geore_large_buds/diamond` filled |
| diamond/geore/bud_medium | `#c:geore_medium_buds/diamond` | 200 mb `tconstruct:molten_diamond` | 1450 | 118 | ore rate: gem; byproduct 180 mb `tconstruct:molten_debris`; requires tag `c:geore_medium_buds/diamond` filled |
| diamond/geore/bud_small | `#c:geore_small_buds/diamond` | 100 mb `tconstruct:molten_diamond` | 1450 | 79 | ore rate: gem; byproduct 90 mb `tconstruct:molten_debris`; requires tag `c:geore_small_buds/diamond` filled |
| diamond/geore/cluster | `#c:geore_clusters/diamond` | 400 mb `tconstruct:molten_diamond` | 1450 | 197 | ore rate: gem; byproduct 360 mb `tconstruct:molten_debris`; requires tag `c:geore_clusters/diamond` filled |
| diamond/geore/shard | `#c:geore_shards/diamond` | 100 mb `tconstruct:molten_diamond` | 1450 | 79 | requires tag `c:geore_shards/diamond` filled |
| diamond/helmet | `minecraft:diamond_helmet` | 500 mb `tconstruct:molten_diamond` | 1450 | 176 | scales with damage |
| diamond/horse_armor | `minecraft:diamond_horse_armor` | 700 mb `tconstruct:molten_diamond` | 1450 | 220 |  |
| diamond/jukebox | `minecraft:jukebox` | 100 mb `tconstruct:molten_diamond` | 1450 | 83 |  |
| diamond/leggings | `#tconstruct:melting/diamond/tools_costing_7` | 700 mb `tconstruct:molten_diamond` | 1450 | 209 | scales with damage |
| diamond/ore_dense | `{"fabric:type":"fabric:all","ingredients":[{"tag":"c:ores/diamond"},{"tag":"c:ore_rates/dense"}]}` | 300 mb `tconstruct:molten_diamond` | 1450 | 355 | ore rate: gem; byproduct 270 mb `tconstruct:molten_debris`; requires tags `c:ores/diamond` + `c:ore_rates/dense` filled |
| diamond/ore_singular | `{"fabric:type":"fabric:difference","base":{"tag":"c:ores/diamond"},"subtracted":{"tag":"tconstruct:non_singular_ore_rates"}}` | 100 mb `tconstruct:molten_diamond` | 1450 | 197 | ore rate: gem; byproduct 90 mb `tconstruct:molten_debris`; requires tags `c` + `:` + `o` + `r` + `e` + `s` + `/` + `d` + `i` + `a` + `m` + `o` + `n` + `d` filled (ignoring `tconstruct:non_singular_ore_rates`) |
| diamond/ore_sparse | `{"fabric:type":"fabric:all","ingredients":[{"tag":"c:ores/diamond"},{"tag":"c:ore_rates/sparse"}]}` | 50 mb `tconstruct:molten_diamond` | 1450 | 118 | ore rate: gem; byproduct 45 mb `tconstruct:molten_debris`; requires tags `c:ores/diamond` + `c:ore_rates/sparse` filled |
| diamond/shovel | `#tconstruct:melting/diamond/tools_costing_1` | 100 mb `tconstruct:molten_diamond` | 1450 | 79 | scales with damage |
| diamond/smithing_template | `minecraft:netherite_upgrade_smithing_template` / `#minecraft:trim_templates` | 500 mb `tconstruct:molten_diamond` | 1450 | 186 |  |
| diamond/sword | `#tconstruct:melting/diamond/tools_costing_2` | 200 mb `tconstruct:molten_diamond` | 1450 | 112 | scales with damage |
| diamond/tools_complement_hammer | `tools_complement:diamond_hammer` | 1300 mb `tconstruct:molten_diamond` | 1450 | 285 | scales with damage; requires item `tools_complement:diamond_hammer` |
| diamond/tools_costing_11 | `#tconstruct:melting/diamond/tools_costing_11` | 1100 mb `tconstruct:molten_diamond` | 1450 | 262 | scales with damage; requires tag `tconstruct:melting/diamond/tools_costing_11` filled |

## emerald

| Recipe | Input | Result | Temp (C) | Time | Notes |
|---|---|---|---|---|---|
| emerald/block | `#c:storage_blocks/emerald` | 900 mb `tconstruct:molten_emerald` | 934 | 193 |  |
| emerald/dust | `#c:dusts/emerald` | 100 mb `tconstruct:molten_emerald` | 934 | 48 | requires tag `c:dusts/emerald` filled |
| emerald/gear | `#c:gears/emerald` | 400 mb `tconstruct:molten_emerald` | 934 | 129 | requires tag `c:gears/emerald` filled |
| emerald/gem | `#c:gems/emerald` | 100 mb `tconstruct:molten_emerald` | 934 | 64 |  |
| emerald/geore/block | `#c:geore_blocks/emerald` | 400 mb `tconstruct:molten_emerald` | 934 | 129 | requires tag `c:geore_blocks/emerald` filled |
| emerald/geore/bud_large | `#c:geore_large_buds/emerald` | 300 mb `tconstruct:molten_emerald` | 934 | 129 | ore rate: gem; byproduct 300 mb `tconstruct:molten_diamond`; requires tag `c:geore_large_buds/emerald` filled |
| emerald/geore/bud_medium | `#c:geore_medium_buds/emerald` | 200 mb `tconstruct:molten_emerald` | 934 | 96 | ore rate: gem; byproduct 200 mb `tconstruct:molten_diamond`; requires tag `c:geore_medium_buds/emerald` filled |
| emerald/geore/bud_small | `#c:geore_small_buds/emerald` | 100 mb `tconstruct:molten_emerald` | 934 | 64 | ore rate: gem; byproduct 100 mb `tconstruct:molten_diamond`; requires tag `c:geore_small_buds/emerald` filled |
| emerald/geore/cluster | `#c:geore_clusters/emerald` | 400 mb `tconstruct:molten_emerald` | 934 | 161 | ore rate: gem; byproduct 400 mb `tconstruct:molten_diamond`; requires tag `c:geore_clusters/emerald` filled |
| emerald/geore/shard | `#c:geore_shards/emerald` | 100 mb `tconstruct:molten_emerald` | 934 | 64 | requires tag `c:geore_shards/emerald` filled |
| emerald/ore_dense | `{"fabric:type":"fabric:all","ingredients":[{"tag":"c:ores/emerald"},{"tag":"c:ore_rates/dense"}]}` | 300 mb `tconstruct:molten_emerald` | 934 | 289 | ore rate: gem; byproduct 300 mb `tconstruct:molten_diamond`; requires tags `c:ores/emerald` + `c:ore_rates/dense` filled |
| emerald/ore_singular | `{"fabric:type":"fabric:difference","base":{"tag":"c:ores/emerald"},"subtracted":{"tag":"tconstruct:non_singular_ore_rates"}}` | 100 mb `tconstruct:molten_emerald` | 934 | 161 | ore rate: gem; byproduct 100 mb `tconstruct:molten_diamond`; requires tags `c` + `:` + `o` + `r` + `e` + `s` + `/` + `e` + `m` + `e` + `r` + `a` + `l` + `d` filled (ignoring `tconstruct:non_singular_ore_rates`) |
| emerald/ore_sparse | `{"fabric:type":"fabric:all","ingredients":[{"tag":"c:ores/emerald"},{"tag":"c:ore_rates/sparse"}]}` | 50 mb `tconstruct:molten_emerald` | 934 | 96 | ore rate: gem; byproduct 50 mb `tconstruct:molten_diamond`; requires tags `c:ores/emerald` + `c:ore_rates/sparse` filled |

## ender

| Recipe | Input | Result | Temp (C) | Time | Notes |
|---|---|---|---|---|---|
| ender/end_crystal | `minecraft:end_crystal` | 250 mb `#c:ender` | 477 | 82 | byproduct 7000 mb `tconstruct:molten_glass` |
| ender/pearl | `#c:ender_pearls` / `minecraft:ender_eye` | 250 mb `#c:ender` | 477 | 49 |  |

## fiery

| Recipe | Input | Result | Temp (C) | Time | Notes |
|---|---|---|---|---|---|
| fiery/block | `#c:storage_blocks/fiery` | 2250 mb `tconstruct:fiery_liquid` | 1500 | 241 | byproduct 810 mb `#c:molten_iron`; requires tag `c:storage_blocks/fiery` filled |
| fiery/ingot | `#c:ingots/fiery` | 250 mb `tconstruct:fiery_liquid` | 1500 | 80 | byproduct 90 mb `#c:molten_iron`; requires tag `c:ingots/fiery` filled |
| fiery/twilightforest_boots | `twilightforest:fiery_boots` | 1000 mb `tconstruct:fiery_liquid` | 1500 | 160 | scales with damage; byproduct 360 mb `#c:molten_iron`; requires item `twilightforest:fiery_boots` |
| fiery/twilightforest_chestplate | `twilightforest:fiery_chestplate` | 2000 mb `tconstruct:fiery_liquid` | 1500 | 227 | scales with damage; byproduct 720 mb `#c:molten_iron`; requires item `twilightforest:fiery_chestplate` |
| fiery/twilightforest_helmet | `twilightforest:fiery_helmet` | 1250 mb `tconstruct:fiery_liquid` | 1500 | 179 | scales with damage; byproduct 450 mb `#c:molten_iron`; requires item `twilightforest:fiery_helmet` |
| fiery/twilightforest_leggings | `twilightforest:fiery_leggings` | 1750 mb `tconstruct:fiery_liquid` | 1500 | 212 | scales with damage; byproduct 630 mb `#c:molten_iron`; requires item `twilightforest:fiery_leggings` |
| fiery/twilightforest_pickaxe | `twilightforest:fiery_pickaxe` | 750 mb `tconstruct:fiery_liquid` | 1500 | 139 | scales with damage; byproduct 270 mb `#c:molten_iron`; requires item `twilightforest:fiery_pickaxe` |
| fiery/twilightforest_sword | `twilightforest:fiery_sword` | 500 mb `tconstruct:fiery_liquid` | 1500 | 113 | scales with damage; byproduct 180 mb `#c:molten_iron`; requires item `twilightforest:fiery_sword` |

## glass

| Recipe | Input | Result | Temp (C) | Time | Notes |
|---|---|---|---|---|---|
| glass/block | `#c:glass/silica` | 1000 mb `tconstruct:molten_glass` | 750 | 59 |  |
| glass/bottle | `minecraft:glass_bottle` / `#c:bottles/splash` / `#c:bottles/lingering` | 1000 mb `tconstruct:molten_glass` | 750 | 73 |  |
| glass/pane | `#c:glass_panes/silica` | 250 mb `tconstruct:molten_glass` | 750 | 29 |  |
| glass/sand | `#minecraft:smelts_to_glass` | 1000 mb `tconstruct:molten_glass` | 750 | 88 |  |
| glass/sand_cast | `tconstruct:blank_sand_cast` / `tconstruct:blank_red_sand_cast` | 250 mb `tconstruct:molten_glass` | 750 | 44 |  |

## ironwood

| Recipe | Input | Result | Temp (C) | Time | Notes |
|---|---|---|---|---|---|
| ironwood/axes | `#tconstruct:melting/ironwood/tools_costing_3` | 270 mb `#c:molten_iron` | 800 | 104 | scales with damage; byproduct 30 mb `#c:molten_gold`; requires tag `tconstruct:melting/ironwood/tools_costing_3` filled |
| ironwood/block | `#c:storage_blocks/ironwood` | 810 mb `#c:molten_iron` | 800 | 180 | byproduct 90 mb `#c:molten_gold`; requires tag `c:storage_blocks/ironwood` filled |
| ironwood/ingot | `#c:ingots/ironwood` | 90 mb `#c:molten_iron` | 800 | 60 | byproduct 10 mb `#c:molten_gold`; requires tag `c:ingots/ironwood` filled |
| ironwood/raw | `#c:raw_materials/ironwood` | 90 mb `#c:molten_iron` | 800 | 60 | byproduct 10 mb `#c:molten_gold`; requires tag `c:raw_materials/ironwood` filled |
| ironwood/sword | `#tconstruct:melting/ironwood/tools_costing_2` | 180 mb `#c:molten_iron` | 800 | 85 | scales with damage; byproduct 20 mb `#c:molten_gold`; requires tag `tconstruct:melting/ironwood/tools_costing_2` filled |
| ironwood/twilightforest_boots | `twilightforest:ironwood_boots` | 360 mb `#c:molten_iron` | 800 | 120 | scales with damage; byproduct 40 mb `#c:molten_gold`; requires item `twilightforest:ironwood_boots` |
| ironwood/twilightforest_chestplate | `twilightforest:ironwood_chestplate` | 720 mb `#c:molten_iron` | 800 | 170 | scales with damage; byproduct 80 mb `#c:molten_gold`; requires item `twilightforest:ironwood_chestplate` |
| ironwood/twilightforest_helmet | `twilightforest:ironwood_helmet` | 450 mb `#c:molten_iron` | 800 | 134 | scales with damage; byproduct 50 mb `#c:molten_gold`; requires item `twilightforest:ironwood_helmet` |
| ironwood/twilightforest_leggings | `twilightforest:ironwood_leggings` | 630 mb `#c:molten_iron` | 800 | 159 | scales with damage; byproduct 70 mb `#c:molten_gold`; requires item `twilightforest:ironwood_leggings` |
| ironwood/twilightforest_shovel | `twilightforest:ironwood_shovel` | 90 mb `#c:molten_iron` | 800 | 60 | scales with damage; byproduct 10 mb `#c:molten_gold`; requires item `twilightforest:ironwood_shovel` |

## metal

| Recipe | Input | Result | Temp (C) | Time | Notes |
|---|---|---|---|---|---|
| metal/aluminum/allomancy_flakes | `allomancy:aluminum_flakes` | 30 mb `#c:molten_aluminum` | 425 | 27 | requires item `allomancy:aluminum_flakes` |
| metal/aluminum/block | `#c:storage_blocks/aluminum` | 810 mb `#c:molten_aluminum` | 425 | 141 | requires tag `c:storage_blocks/aluminum` filled |
| metal/aluminum/coin | `#c:coins/aluminum` | 30 mb `#c:molten_aluminum` | 425 | 31 | requires tag `c:coins/aluminum` filled |
| metal/aluminum/dust | `#c:dusts/aluminum` | 90 mb `#c:molten_aluminum` | 425 | 35 | requires tag `c:dusts/aluminum` filled |
| metal/aluminum/gear | `#c:gears/aluminum` | 360 mb `#c:molten_aluminum` | 425 | 94 | requires tag `c:gears/aluminum` filled |
| metal/aluminum/ingot | `#c:ingots/aluminum` | 90 mb `#c:molten_aluminum` | 425 | 47 | requires tag `c:ingots/aluminum` filled |
| metal/aluminum/nugget | `#c:nuggets/aluminum` | 10 mb `#c:molten_aluminum` | 425 | 16 | requires tag `c:nuggets/aluminum` filled |
| metal/aluminum/ore_dense | `{"fabric:type":"fabric:all","ingredients":[{"tag":"c:ores/aluminum"},{"tag":"c:ore_rates/dense"}]}` | 540 mb `#c:molten_aluminum` | 425 | 212 | ore rate: metal; byproduct 540 mb `#c:molten_iron`; requires tags `c:ores/aluminum` + `c:ore_rates/dense` filled |
| metal/aluminum/ore_singular | `{"fabric:type":"fabric:difference","base":{"tag":"c:ores/aluminum"},"subtracted":{"tag":"tconstruct:non_singular_ore_rates"}}` | 180 mb `#c:molten_aluminum` | 425 | 118 | ore rate: metal; byproduct 180 mb `#c:molten_iron`; requires tags `c` + `:` + `o` + `r` + `e` + `s` + `/` + `a` + `l` + `u` + `m` + `i` + `n` + `u` + `m` filled (ignoring `tconstruct:non_singular_ore_rates`) |
| metal/aluminum/ore_sparse | `{"fabric:type":"fabric:all","ingredients":[{"tag":"c:ores/aluminum"},{"tag":"c:ore_rates/sparse"}]}` | 90 mb `#c:molten_aluminum` | 425 | 71 | ore rate: metal; byproduct 90 mb `#c:molten_iron`; requires tags `c:ores/aluminum` + `c:ore_rates/sparse` filled |
| metal/aluminum/oreberry | `oreberriesreplanted:aluminum_oreberry` | 10 mb `#c:molten_aluminum` | 425 | 16 | requires item `oreberriesreplanted:aluminum_oreberry` |
| metal/aluminum/plate | `#c:plates/aluminum` | 90 mb `#c:molten_aluminum` | 425 | 47 | requires tag `c:plates/aluminum` filled |
| metal/aluminum/raw | `#c:raw_materials/aluminum` | 90 mb `#c:molten_aluminum` | 425 | 71 | ore rate: metal; byproduct 90 mb `#c:molten_iron`; requires tag `c:raw_materials/aluminum` filled |
| metal/aluminum/raw_block | `#c:storage_blocks/raw_aluminum` | 810 mb `#c:molten_aluminum` | 425 | 283 | ore rate: metal; byproduct 810 mb `#c:molten_iron`; requires tag `c:storage_blocks/raw_aluminum` filled |
| metal/aluminum/rod | `#c:rods/aluminum` | 45 mb `#c:molten_aluminum` | 425 | 9 | requires tag `c:rods/aluminum` filled |
| metal/aluminum/sheetmetal | `#c:sheetmetals/aluminum` | 90 mb `#c:molten_aluminum` | 425 | 47 | requires tag `c:sheetmetals/aluminum` filled |
| metal/aluminum/wire | `#c:wires/aluminum` | 45 mb `#c:molten_aluminum` | 425 | 9 | requires tag `c:wires/aluminum` filled |
| metal/amethyst_bronze/block | `#c:storage_blocks/amethyst_bronze` | 810 mb `#c:molten_amethyst_bronze` | 820 | 182 |  |
| metal/amethyst_bronze/dust | `#c:dusts/amethyst_bronze` | 90 mb `#c:molten_amethyst_bronze` | 820 | 46 | requires tag `c:dusts/amethyst_bronze` filled |
| metal/amethyst_bronze/ingot | `#c:ingots/amethyst_bronze` | 90 mb `#c:molten_amethyst_bronze` | 820 | 61 |  |
| metal/amethyst_bronze/nugget | `#c:nuggets/amethyst_bronze` | 10 mb `#c:molten_amethyst_bronze` | 820 | 20 |  |
| metal/bendalloy/allomancy_flakes | `allomancy:bendalloy_flakes` | 30 mb `#c:molten_bendalloy` | 100 | 19 | requires item `allomancy:bendalloy_flakes` |
| metal/bendalloy/block | `#c:storage_blocks/bendalloy` | 810 mb `#c:molten_bendalloy` | 100 | 100 | requires tag `c:storage_blocks/bendalloy` filled |
| metal/bendalloy/dust | `#c:dusts/bendalloy` | 90 mb `#c:molten_bendalloy` | 100 | 25 | requires tag `c:dusts/bendalloy` filled |
| metal/bendalloy/ingot | `#c:ingots/bendalloy` | 90 mb `#c:molten_bendalloy` | 100 | 33 | requires tag `c:ingots/bendalloy` filled |
| metal/bendalloy/nugget | `#c:nuggets/bendalloy` | 10 mb `#c:molten_bendalloy` | 100 | 11 | requires tag `c:nuggets/bendalloy` filled |
| metal/brass/allomancy_flakes | `allomancy:brass_flakes` | 30 mb `#c:molten_brass` | 605 | 31 | requires item `allomancy:brass_flakes` |
| metal/brass/block | `#c:storage_blocks/brass` | 810 mb `#c:molten_brass` | 605 | 161 | requires tag `c:storage_blocks/brass` filled |
| metal/brass/dust | `#c:dusts/brass` | 90 mb `#c:molten_brass` | 605 | 40 | requires tag `c:dusts/brass` filled |
| metal/brass/gear | `#c:gears/brass` | 360 mb `#c:molten_brass` | 605 | 107 | requires tag `c:gears/brass` filled |
| metal/brass/ingot | `#c:ingots/brass` | 90 mb `#c:molten_brass` | 605 | 54 | requires tag `c:ingots/brass` filled |
| metal/brass/nugget | `#c:nuggets/brass` | 10 mb `#c:molten_brass` | 605 | 18 | requires tag `c:nuggets/brass` filled |
| metal/brass/plate | `#c:plates/brass` | 90 mb `#c:molten_brass` | 605 | 54 | requires tag `c:plates/brass` filled |
| metal/bronze/allomancy_flakes | `allomancy:bronze_flakes` | 30 mb `#c:molten_bronze` | 700 | 33 | requires item `allomancy:bronze_flakes` |
| metal/bronze/axes | `#tconstruct:melting/bronze/tools_costing_3` | 270 mb `#c:molten_bronze` | 700 | 99 | scales with damage; requires tag `tconstruct:melting/bronze/tools_costing_3` filled |
| metal/bronze/block | `#c:storage_blocks/bronze` | 810 mb `#c:molten_bronze` | 700 | 171 | requires tag `c:storage_blocks/bronze` filled |
| metal/bronze/boots | `#c:armors/boots/bronze` | 360 mb `#c:molten_bronze` | 700 | 114 | scales with damage; requires tag `c:armors/boots/bronze` filled |
| metal/bronze/chestplate | `#c:armors/chestplates/bronze` | 720 mb `#c:molten_bronze` | 700 | 161 | scales with damage; requires tag `c:armors/chestplates/bronze` filled |
| metal/bronze/coin | `#c:coins/bronze` | 30 mb `#c:molten_bronze` | 700 | 38 | requires tag `c:coins/bronze` filled |
| metal/bronze/dust | `#c:dusts/bronze` | 90 mb `#c:molten_bronze` | 700 | 43 | requires tag `c:dusts/bronze` filled |
| metal/bronze/gear | `#c:gears/bronze` | 360 mb `#c:molten_bronze` | 700 | 114 | requires tag `c:gears/bronze` filled |
| metal/bronze/helmet | `#c:armors/helmets/bronze` | 450 mb `#c:molten_bronze` | 700 | 127 | scales with damage; requires tag `c:armors/helmets/bronze` filled |
| metal/bronze/ingot | `#c:ingots/bronze` | 90 mb `#c:molten_bronze` | 700 | 57 | requires tag `c:ingots/bronze` filled |
| metal/bronze/leggings | `#tconstruct:melting/bronze/tools_costing_7` | 630 mb `#c:molten_bronze` | 700 | 151 | scales with damage; requires tag `tconstruct:melting/bronze/tools_costing_7` filled |
| metal/bronze/mekanism_shield | `mekanism:bronze_shield` | 540 mb `#c:molten_bronze` | 700 | 139 | scales with damage; requires item `mekanism:bronze_shield` |
| metal/bronze/nugget | `#c:nuggets/bronze` | 10 mb `#c:molten_bronze` | 700 | 19 | requires tag `c:nuggets/bronze` filled |
| metal/bronze/plate | `#c:plates/bronze` | 90 mb `#c:molten_bronze` | 700 | 57 | requires tag `c:plates/bronze` filled |
| metal/bronze/raw | `#c:raw_materials/bronze` | 90 mb `#c:molten_bronze` | 700 | 85 | ore rate: metal; byproduct 90 mb `#c:molten_copper`; requires tag `c:raw_materials/bronze` filled |
| metal/bronze/raw_block | `#c:storage_blocks/raw_bronze` | 810 mb `#c:molten_bronze` | 700 | 341 | ore rate: metal; byproduct 810 mb `#c:molten_copper`; requires tag `c:storage_blocks/raw_bronze` filled |
| metal/bronze/shovel | `#tconstruct:melting/bronze/tools_costing_1` | 90 mb `#c:molten_bronze` | 700 | 57 | scales with damage; requires tag `tconstruct:melting/bronze/tools_costing_1` filled |
| metal/bronze/sword | `#tconstruct:melting/bronze/tools_costing_2` | 180 mb `#c:molten_bronze` | 700 | 80 | scales with damage; requires tag `tconstruct:melting/bronze/tools_costing_2` filled |
| metal/bronze/tools_complement_excavator | `tools_complement:bronze_excavator` | 990 mb `#c:molten_bronze` | 700 | 189 | scales with damage; requires item `tools_complement:bronze_excavator` |
| metal/bronze/tools_complement_hammer | `tools_complement:bronze_hammer` | 1170 mb `#c:molten_bronze` | 700 | 205 | scales with damage; requires item `tools_complement:bronze_hammer` |
| metal/cadmium/allomancy_flakes | `allomancy:cadmium_flakes` | 30 mb `#c:molten_cadmium` | 294 | 24 | requires item `allomancy:cadmium_flakes` |
| metal/cadmium/block | `#c:storage_blocks/cadmium` | 810 mb `#c:molten_cadmium` | 294 | 126 | requires tag `c:storage_blocks/cadmium` filled |
| metal/cadmium/dust | `#c:dusts/cadmium` | 90 mb `#c:molten_cadmium` | 294 | 31 | requires tag `c:dusts/cadmium` filled |
| metal/cadmium/ingot | `#c:ingots/cadmium` | 90 mb `#c:molten_cadmium` | 294 | 42 | requires tag `c:ingots/cadmium` filled |
| metal/cadmium/nugget | `#c:nuggets/cadmium` | 10 mb `#c:molten_cadmium` | 294 | 14 | requires tag `c:nuggets/cadmium` filled |
| metal/chromium/allomancy_flakes | `allomancy:chromium_flakes` | 30 mb `#c:molten_chromium` | 900 | 37 | requires item `allomancy:chromium_flakes` |
| metal/chromium/block | `#c:storage_blocks/chromium` | 810 mb `#c:molten_chromium` | 900 | 190 | requires tag `c:storage_blocks/chromium` filled |
| metal/chromium/dust | `#c:dusts/chromium` | 90 mb `#c:molten_chromium` | 900 | 47 | requires tag `c:dusts/chromium` filled |
| metal/chromium/ingot | `#c:ingots/chromium` | 90 mb `#c:molten_chromium` | 900 | 63 | requires tag `c:ingots/chromium` filled |
| metal/chromium/nugget | `#c:nuggets/chromium` | 10 mb `#c:molten_chromium` | 900 | 21 | requires tag `c:nuggets/chromium` filled |
| metal/cinderslime/block | `#c:storage_blocks/cinderslime` | 810 mb `tconstruct:molten_cinderslime` | 1050 | 203 |  |
| metal/cinderslime/ingot | `#c:ingots/cinderslime` | 90 mb `tconstruct:molten_cinderslime` | 1050 | 68 |  |
| metal/cinderslime/nugget | `#c:nuggets/cinderslime` | 10 mb `tconstruct:molten_cinderslime` | 1050 | 23 |  |
| metal/cobalt/block | `#c:storage_blocks/cobalt` | 810 mb `#c:molten_cobalt` | 950 | 194 |  |
| metal/cobalt/cluster | `tconstruct:cobalt_cluster` | 40 mb `#c:molten_cobalt` | 950 | 162 |  |
| metal/cobalt/dust | `#c:dusts/cobalt` | 90 mb `#c:molten_cobalt` | 950 | 49 | requires tag `c:dusts/cobalt` filled |
| metal/cobalt/ingot | `#c:ingots/cobalt` | 90 mb `#c:molten_cobalt` | 950 | 65 |  |
| metal/cobalt/nugget | `#c:nuggets/cobalt` | 10 mb `#c:molten_cobalt` | 950 | 22 |  |
| metal/cobalt/ore_dense | `{"fabric:type":"fabric:all","ingredients":[{"tag":"c:ores/cobalt"},{"tag":"c:ore_rates/dense"}]}` | 540 mb `#c:molten_cobalt` | 950 | 292 | ore rate: metal; byproduct 150 mb `tconstruct:molten_diamond`; requires tags `c:ores/cobalt` + `c:ore_rates/dense` filled |
| metal/cobalt/ore_singular | `{"fabric:type":"fabric:difference","base":{"tag":"c:ores/cobalt"},"subtracted":{"tag":"tconstruct:non_singular_ore_rates"}}` | 180 mb `#c:molten_cobalt` | 950 | 162 | ore rate: metal; byproduct 50 mb `tconstruct:molten_diamond`; requires tags `c` + `:` + `o` + `r` + `e` + `s` + `/` + `c` + `o` + `b` + `a` + `l` + `t` filled (ignoring `tconstruct:non_singular_ore_rates`) |
| metal/cobalt/ore_sparse | `{"fabric:type":"fabric:all","ingredients":[{"tag":"c:ores/cobalt"},{"tag":"c:ore_rates/sparse"}]}` | 90 mb `#c:molten_cobalt` | 950 | 97 | ore rate: metal; byproduct 25 mb `tconstruct:molten_diamond`; requires tags `c:ores/cobalt` + `c:ore_rates/sparse` filled |
| metal/cobalt/platform | `tconstruct:cobalt_platform` | 100 mb `#c:molten_cobalt` | 950 | 68 |  |
| metal/cobalt/raw | `#c:raw_materials/cobalt` | 90 mb `#c:molten_cobalt` | 950 | 97 | ore rate: metal; byproduct 25 mb `tconstruct:molten_diamond` |
| metal/cobalt/raw_block | `#c:storage_blocks/raw_cobalt` | 810 mb `#c:molten_cobalt` | 950 | 389 | ore rate: metal; byproduct 225 mb `tconstruct:molten_diamond` |
| metal/cobalt/raw_nugget | `#c:raw_nuggets/cobalt` | 10 mb `#c:molten_cobalt` | 950 | 32 |  |
| metal/cobalt/reinforcement | `tconstruct:cobalt_reinforcement` | 90 mb `#c:molten_cobalt` | 950 | 65 |  |
| metal/cobalt/scorched_duct | `tconstruct:scorched_duct` | 180 mb `#c:molten_gold` | 700 | 142 | byproduct 1000 mb `tconstruct:scorched_stone` |
| metal/cobalt/seared_duct | `tconstruct:seared_duct` | 180 mb `#c:molten_gold` | 700 | 142 | byproduct 1000 mb `tconstruct:seared_stone` |
| metal/constantan/axes | `#tconstruct:melting/constantan/tools_costing_3` | 270 mb `#c:molten_constantan` | 920 | 111 | scales with damage; requires tag `tconstruct:melting/constantan/tools_costing_3` filled |
| metal/constantan/block | `#c:storage_blocks/constantan` | 810 mb `#c:molten_constantan` | 920 | 192 | requires tag `c:storage_blocks/constantan` filled |
| metal/constantan/boots | `#c:armors/boots/constantan` | 360 mb `#c:molten_constantan` | 920 | 128 | scales with damage; requires tag `c:armors/boots/constantan` filled |
| metal/constantan/chestplate | `#c:armors/chestplates/constantan` | 720 mb `#c:molten_constantan` | 920 | 181 | scales with damage; requires tag `c:armors/chestplates/constantan` filled |
| metal/constantan/coin | `#c:coins/constantan` | 30 mb `#c:molten_constantan` | 920 | 43 | requires tag `c:coins/constantan` filled |
| metal/constantan/dust | `#c:dusts/constantan` | 90 mb `#c:molten_constantan` | 920 | 48 | requires tag `c:dusts/constantan` filled |
| metal/constantan/gear | `#c:gears/constantan` | 360 mb `#c:molten_constantan` | 920 | 128 | requires tag `c:gears/constantan` filled |
| metal/constantan/helmet | `#c:armors/helmets/constantan` | 450 mb `#c:molten_constantan` | 920 | 143 | scales with damage; requires tag `c:armors/helmets/constantan` filled |
| metal/constantan/ingot | `#c:ingots/constantan` | 90 mb `#c:molten_constantan` | 920 | 64 | requires tag `c:ingots/constantan` filled |
| metal/constantan/leggings | `#c:armors/leggings/constantan` | 630 mb `#c:molten_constantan` | 920 | 169 | scales with damage; requires tag `c:armors/leggings/constantan` filled |
| metal/constantan/nugget | `#c:nuggets/constantan` | 10 mb `#c:molten_constantan` | 920 | 21 | requires tag `c:nuggets/constantan` filled |
| metal/constantan/plate | `#c:plates/constantan` | 90 mb `#c:molten_constantan` | 920 | 64 | requires tag `c:plates/constantan` filled |
| metal/constantan/sheetmetal | `#c:sheetmetals/constantan` | 90 mb `#c:molten_constantan` | 920 | 64 | requires tag `c:sheetmetals/constantan` filled |
| metal/constantan/shovel | `#tconstruct:melting/constantan/tools_costing_1` | 90 mb `#c:molten_constantan` | 920 | 64 | scales with damage; requires tag `tconstruct:melting/constantan/tools_costing_1` filled |
| metal/constantan/sword | `#tconstruct:melting/constantan/tools_costing_2` | 180 mb `#c:molten_constantan` | 920 | 90 | scales with damage; requires tag `tconstruct:melting/constantan/tools_costing_2` filled |
| metal/constantan/tools_complement_excavator | `tools_complement:constantan_excavator` | 990 mb `#c:molten_constantan` | 920 | 212 | scales with damage; requires item `tools_complement:constantan_excavator` |
| metal/constantan/tools_complement_hammer | `tools_complement:constantan_hammer` | 1170 mb `#c:molten_constantan` | 920 | 230 | scales with damage; requires item `tools_complement:constantan_hammer` |
| metal/copper/allomancy_flakes | `allomancy:copper_flakes` | 30 mb `#c:molten_copper` | 500 | 29 | requires item `allomancy:copper_flakes` |
| metal/copper/axes | `#tconstruct:melting/copper/tools_costing_3` | 270 mb `#c:molten_copper` | 500 | 86 | scales with damage; requires tag `tconstruct:melting/copper/tools_costing_3` filled |
| metal/copper/block | `#c:storage_blocks/copper` | 810 mb `#c:molten_copper` | 500 | 150 |  |
| metal/copper/boots | `#c:armors/boots/copper` | 360 mb `#c:molten_copper` | 500 | 100 | scales with damage; requires tag `c:armors/boots/copper` filled |
| metal/copper/can | `tconstruct:copper_can` | 90 mb `#c:molten_copper` | 500 | 50 |  |
| metal/copper/chestplate | `#c:armors/chestplates/copper` | 720 mb `#c:molten_copper` | 500 | 141 | scales with damage; requires tag `c:armors/chestplates/copper` filled |
| metal/copper/coin | `#c:coins/copper` | 30 mb `#c:molten_copper` | 500 | 33 | requires tag `c:coins/copper` filled |
| metal/copper/cut_block | `minecraft:cut_copper` / `minecraft:exposed_cut_copper` / `minecraft:weathered_cut_copper` / `minecraft:oxidized_cut_copper` / `minecraft:cut_copper_stairs` / `minecraft:exposed_cut_copper_stairs` / `minecraft:weathered_cut_copper_stairs` / `minecraft:oxidized_cut_copper_stairs` / `minecraft:waxed_cut_copper` / `minecraft:waxed_exposed_cut_copper` / `minecraft:waxed_weathered_cut_copper` / `minecraft:waxed_oxidized_cut_copper` / `minecraft:waxed_cut_copper_stairs` / `minecraft:waxed_exposed_cut_copper_stairs` / `minecraft:waxed_weathered_cut_copper_stairs` / `minecraft:waxed_oxidized_cut_copper_stairs` | 200 mb `#c:molten_copper` | 500 | 74 |  |
| metal/copper/cut_slab | `minecraft:cut_copper_slab` / `minecraft:exposed_cut_copper_slab` / `minecraft:weathered_cut_copper_slab` / `minecraft:oxidized_cut_copper_slab` / `minecraft:waxed_cut_copper_slab` / `minecraft:waxed_exposed_cut_copper_slab` / `minecraft:waxed_weathered_cut_copper_slab` / `minecraft:waxed_oxidized_cut_copper_slab` | 100 mb `#c:molten_copper` | 500 | 53 |  |
| metal/copper/decorative_block | `minecraft:exposed_copper` / `minecraft:weathered_copper` / `minecraft:oxidized_copper` / `minecraft:waxed_copper_block` / `minecraft:waxed_exposed_copper` / `minecraft:waxed_weathered_copper` / `minecraft:waxed_oxidized_copper` | 810 mb `#c:molten_copper` | 500 | 150 |  |
| metal/copper/dust | `#c:dusts/copper` | 90 mb `#c:molten_copper` | 500 | 37 | requires tag `c:dusts/copper` filled |
| metal/copper/gauge | `tconstruct:copper_gauge` | 90 mb `#c:molten_copper` | 500 | 50 | byproduct 50 mb `tconstruct:molten_glass` |
| metal/copper/gear | `#c:gears/copper` | 360 mb `#c:molten_copper` | 500 | 100 | requires tag `c:gears/copper` filled |
| metal/copper/geore/block | `#c:geore_blocks/copper` | 360 mb `#c:molten_copper` | 500 | 100 | requires tag `c:geore_blocks/copper` filled |
| metal/copper/geore/bud_large | `#c:geore_large_buds/copper` | 270 mb `#c:molten_copper` | 500 | 100 | ore rate: metal; byproduct 90 mb `#c:molten_gold`; requires tag `c:geore_large_buds/copper` filled |
| metal/copper/geore/bud_medium | `#c:geore_medium_buds/copper` | 180 mb `#c:molten_copper` | 500 | 75 | ore rate: metal; byproduct 60 mb `#c:molten_gold`; requires tag `c:geore_medium_buds/copper` filled |
| metal/copper/geore/bud_small | `#c:geore_small_buds/copper` | 90 mb `#c:molten_copper` | 500 | 50 | ore rate: metal; byproduct 30 mb `#c:molten_gold`; requires tag `c:geore_small_buds/copper` filled |
| metal/copper/geore/cluster | `#c:geore_clusters/copper` | 360 mb `#c:molten_copper` | 500 | 125 | ore rate: metal; byproduct 120 mb `#c:molten_gold`; requires tag `c:geore_clusters/copper` filled |
| metal/copper/geore/shard | `#c:geore_shards/copper` | 90 mb `#c:molten_copper` | 500 | 50 | requires tag `c:geore_shards/copper` filled |
| metal/copper/helmet | `#c:armors/helmets/copper` | 450 mb `#c:molten_copper` | 500 | 112 | scales with damage; requires tag `c:armors/helmets/copper` filled |
| metal/copper/ingot | `#c:ingots/copper` | 90 mb `#c:molten_copper` | 500 | 50 |  |
| metal/copper/leggings | `#c:armors/leggings/copper` | 630 mb `#c:molten_copper` | 500 | 132 | scales with damage; requires tag `c:armors/leggings/copper` filled |
| metal/copper/lightning_rod | `minecraft:lightning_rod` | 270 mb `#c:molten_copper` | 500 | 86 |  |
| metal/copper/nugget | `#c:nuggets/copper` | 10 mb `#c:molten_copper` | 500 | 17 |  |
| metal/copper/ore_dense | `{"fabric:type":"fabric:all","ingredients":[{"tag":"c:ores/copper"},{"tag":"c:ore_rates/dense"}]}` | 540 mb `#c:molten_copper` | 500 | 225 | ore rate: metal; byproduct 180 mb `#c:molten_gold`; requires tags `c:ores/copper` + `c:ore_rates/dense` filled |
| metal/copper/ore_singular | `{"fabric:type":"fabric:difference","base":{"tag":"c:ores/copper"},"subtracted":{"tag":"tconstruct:non_singular_ore_rates"}}` | 180 mb `#c:molten_copper` | 500 | 125 | ore rate: metal; byproduct 60 mb `#c:molten_gold`; requires tags `c` + `:` + `o` + `r` + `e` + `s` + `/` + `c` + `o` + `p` + `p` + `e` + `r` filled (ignoring `tconstruct:non_singular_ore_rates`) |
| metal/copper/ore_sparse | `{"fabric:type":"fabric:all","ingredients":[{"tag":"c:ores/copper"},{"tag":"c:ore_rates/sparse"}]}` | 90 mb `#c:molten_copper` | 500 | 75 | ore rate: metal; byproduct 30 mb `#c:molten_gold`; requires tags `c:ores/copper` + `c:ore_rates/sparse` filled |
| metal/copper/oreberry | `oreberriesreplanted:copper_oreberry` | 10 mb `#c:molten_copper` | 500 | 17 | requires item `oreberriesreplanted:copper_oreberry` |
| metal/copper/plate | `#c:plates/copper` | 90 mb `#c:molten_copper` | 500 | 50 | requires tag `c:plates/copper` filled |
| metal/copper/platform | `#tconstruct:copper_platforms` | 100 mb `#c:molten_copper` | 500 | 53 |  |
| metal/copper/raw | `#c:raw_materials/copper` | 90 mb `#c:molten_copper` | 500 | 75 | ore rate: metal; byproduct 30 mb `#c:molten_gold` |
| metal/copper/raw_block | `#c:storage_blocks/raw_copper` | 810 mb `#c:molten_copper` | 500 | 300 | ore rate: metal; byproduct 270 mb `#c:molten_gold` |
| metal/copper/sheetmetal | `#c:sheetmetals/copper` | 90 mb `#c:molten_copper` | 500 | 50 | requires tag `c:sheetmetals/copper` filled |
| metal/copper/shovel | `#tconstruct:melting/copper/tools_costing_1` | 90 mb `#c:molten_copper` | 500 | 50 | scales with damage |
| metal/copper/smeltery_controller | `tconstruct:smeltery_controller` | 360 mb `#c:molten_copper` | 500 | 175 | byproduct 1000 mb `tconstruct:seared_stone` |
| metal/copper/smeltery_io | `tconstruct:seared_drain` / `tconstruct:seared_chute` | 180 mb `#c:molten_copper` | 500 | 125 | byproduct 1000 mb `tconstruct:seared_stone` |
| metal/copper/sword | `#tconstruct:melting/copper/tools_costing_2` | 180 mb `#c:molten_copper` | 500 | 71 | scales with damage; requires tag `tconstruct:melting/copper/tools_costing_2` filled |
| metal/copper/tools_complement_excavator | `tools_complement:copper_excavator` | 990 mb `#c:molten_copper` | 500 | 166 | scales with damage; requires item `tools_complement:copper_excavator` |
| metal/copper/tools_complement_hammer | `tools_complement:copper_hammer` | 1170 mb `#c:molten_copper` | 500 | 180 | scales with damage; requires item `tools_complement:copper_hammer` |
| metal/copper/wire | `#c:wires/copper` | 45 mb `#c:molten_copper` | 500 | 10 | requires tag `c:wires/copper` filled |
| metal/dawnstone/block | `#c:storage_blocks/dawnstone` | 810 mb `#c:molten_dawnstone` | 900 | 190 | requires tag `c:storage_blocks/dawnstone` filled; tag `c:molten_dawnstone` filled |
| metal/dawnstone/ingot | `#c:ingots/dawnstone` | 90 mb `#c:molten_dawnstone` | 900 | 63 | requires tag `c:ingots/dawnstone` filled; tag `c:molten_dawnstone` filled |
| metal/dawnstone/nugget | `#c:nuggets/dawnstone` | 10 mb `#c:molten_dawnstone` | 900 | 21 | requires tag `c:nuggets/dawnstone` filled; tag `c:molten_dawnstone` filled |
| metal/dawnstone/plate | `#c:plates/dawnstone` | 90 mb `#c:molten_dawnstone` | 900 | 63 | requires tag `c:plates/dawnstone` filled; tag `c:molten_dawnstone` filled |
| metal/duralumin/allomancy_flakes | `allomancy:duralumin_flakes` | 30 mb `#c:molten_duralumin` | 625 | 31 | requires item `allomancy:duralumin_flakes` |
| metal/duralumin/block | `#c:storage_blocks/duralumin` | 810 mb `#c:molten_duralumin` | 625 | 163 | requires tag `c:storage_blocks/duralumin` filled |
| metal/duralumin/dust | `#c:dusts/duralumin` | 90 mb `#c:molten_duralumin` | 625 | 41 | requires tag `c:dusts/duralumin` filled |
| metal/duralumin/ingot | `#c:ingots/duralumin` | 90 mb `#c:molten_duralumin` | 625 | 54 | requires tag `c:ingots/duralumin` filled |
| metal/duralumin/nugget | `#c:nuggets/duralumin` | 10 mb `#c:molten_duralumin` | 625 | 18 | requires tag `c:nuggets/duralumin` filled |
| metal/electrum/allomancy_flakes | `allomancy:electrum_flakes` | 30 mb `#c:molten_electrum` | 760 | 34 | requires item `allomancy:electrum_flakes` |
| metal/electrum/axes | `#tconstruct:melting/electrum/tools_costing_3` | 270 mb `#c:molten_electrum` | 760 | 102 | scales with damage; requires tag `tconstruct:melting/electrum/tools_costing_3` filled |
| metal/electrum/block | `#c:storage_blocks/electrum` | 810 mb `#c:molten_electrum` | 760 | 177 | requires tag `c:storage_blocks/electrum` filled |
| metal/electrum/boots | `#c:armors/boots/electrum` | 360 mb `#c:molten_electrum` | 760 | 118 | scales with damage; requires tag `c:armors/boots/electrum` filled |
| metal/electrum/chestplate | `#c:armors/chestplates/electrum` | 720 mb `#c:molten_electrum` | 760 | 166 | scales with damage; requires tag `c:armors/chestplates/electrum` filled |
| metal/electrum/coin | `#c:coins/electrum` | 30 mb `#c:molten_electrum` | 760 | 39 | requires tag `c:coins/electrum` filled |
| metal/electrum/dust | `#c:dusts/electrum` | 90 mb `#c:molten_electrum` | 760 | 44 | requires tag `c:dusts/electrum` filled |
| metal/electrum/gear | `#c:gears/electrum` | 360 mb `#c:molten_electrum` | 760 | 118 | requires tag `c:gears/electrum` filled |
| metal/electrum/helmet | `#c:armors/helmets/electrum` | 450 mb `#c:molten_electrum` | 760 | 132 | scales with damage; requires tag `c:armors/helmets/electrum` filled |
| metal/electrum/ingot | `#c:ingots/electrum` | 90 mb `#c:molten_electrum` | 760 | 59 | requires tag `c:ingots/electrum` filled |
| metal/electrum/leggings | `#c:armors/leggings/electrum` | 630 mb `#c:molten_electrum` | 760 | 156 | scales with damage; requires tag `c:armors/leggings/electrum` filled |
| metal/electrum/nugget | `#c:nuggets/electrum` | 10 mb `#c:molten_electrum` | 760 | 20 | requires tag `c:nuggets/electrum` filled |
| metal/electrum/plate | `#c:plates/electrum` | 90 mb `#c:molten_electrum` | 760 | 59 | requires tag `c:plates/electrum` filled |
| metal/electrum/sheetmetal | `#c:sheetmetals/electrum` | 90 mb `#c:molten_electrum` | 760 | 59 | requires tag `c:sheetmetals/electrum` filled |
| metal/electrum/shovel | `#tconstruct:melting/electrum/tools_costing_1` | 90 mb `#c:molten_electrum` | 760 | 59 | scales with damage; requires tag `tconstruct:melting/electrum/tools_costing_1` filled |
| metal/electrum/sword | `#tconstruct:melting/electrum/tools_costing_2` | 180 mb `#c:molten_electrum` | 760 | 83 | scales with damage; requires tag `tconstruct:melting/electrum/tools_costing_2` filled |
| metal/electrum/tools_complement_excavator | `tools_complement:electrum_excavator` | 990 mb `#c:molten_electrum` | 760 | 195 | scales with damage; requires item `tools_complement:electrum_excavator` |
| metal/electrum/tools_complement_hammer | `tools_complement:electrum_hammer` | 1170 mb `#c:molten_electrum` | 760 | 212 | scales with damage; requires item `tools_complement:electrum_hammer` |
| metal/electrum/wire | `#c:wires/electrum` | 45 mb `#c:molten_electrum` | 760 | 12 | requires tag `c:wires/electrum` filled |
| metal/emerald/reinforcement | `tconstruct:emerald_reinforcement` | 25 mb `tconstruct:molten_emerald` | 934 | 34 | byproduct 250 mb `tconstruct:molten_obsidian` |
| metal/enderium/block | `#c:storage_blocks/enderium` | 810 mb `#c:molten_enderium` | 1350 | 229 | requires tag `c:storage_blocks/enderium` filled |
| metal/enderium/coin | `#c:coins/enderium` | 30 mb `#c:molten_enderium` | 1350 | 51 | requires tag `c:coins/enderium` filled |
| metal/enderium/dust | `#c:dusts/enderium` | 90 mb `#c:molten_enderium` | 1350 | 57 | requires tag `c:dusts/enderium` filled |
| metal/enderium/gear | `#c:gears/enderium` | 360 mb `#c:molten_enderium` | 1350 | 152 | requires tag `c:gears/enderium` filled |
| metal/enderium/ingot | `#c:ingots/enderium` | 90 mb `#c:molten_enderium` | 1350 | 76 | requires tag `c:ingots/enderium` filled |
| metal/enderium/nugget | `#c:nuggets/enderium` | 10 mb `#c:molten_enderium` | 1350 | 25 | requires tag `c:nuggets/enderium` filled |
| metal/enderium/plate | `#c:plates/enderium` | 90 mb `#c:molten_enderium` | 1350 | 76 | requires tag `c:plates/enderium` filled |
| metal/gold/allomancy_flakes | `allomancy:gold_flakes` | 30 mb `#c:molten_gold` | 700 | 33 | requires item `allomancy:gold_flakes` |
| metal/gold/apple | `minecraft:golden_apple` | 720 mb `#c:molten_gold` | 700 | 161 |  |
| metal/gold/axes | `#tconstruct:melting/gold/tools_costing_3` | 270 mb `#c:molten_gold` | 700 | 99 | scales with damage |
| metal/gold/bell | `minecraft:bell` | 360 mb `#c:molten_gold` | 700 | 114 |  |
| metal/gold/block | `#c:storage_blocks/gold` | 810 mb `#c:molten_gold` | 700 | 171 |  |
| metal/gold/boots | `#tconstruct:melting/gold/tools_costing_4` | 360 mb `#c:molten_gold` | 700 | 114 | scales with damage |
| metal/gold/cast | `#tconstruct:casts/gold` | 90 mb `#c:molten_gold` | 700 | 57 |  |
| metal/gold/chestplate | `minecraft:golden_chestplate` | 720 mb `#c:molten_gold` | 700 | 161 | scales with damage |
| metal/gold/clock | `minecraft:clock` | 360 mb `#c:molten_gold` | 700 | 114 |  |
| metal/gold/coin | `#c:coins/gold` | 30 mb `#c:molten_gold` | 700 | 38 | requires tag `c:coins/gold` filled |
| metal/gold/dust | `#c:dusts/gold` | 90 mb `#c:molten_gold` | 700 | 43 | requires tag `c:dusts/gold` filled |
| metal/gold/enchanted_apple | `minecraft:enchanted_golden_apple` | 6480 mb `#c:molten_gold` | 700 | 483 |  |
| metal/gold/gear | `#c:gears/gold` | 360 mb `#c:molten_gold` | 700 | 114 | requires tag `c:gears/gold` filled |
| metal/gold/geore/block | `#c:geore_blocks/gold` | 360 mb `#c:molten_gold` | 700 | 114 | requires tag `c:geore_blocks/gold` filled |
| metal/gold/geore/bud_large | `#c:geore_large_buds/gold` | 270 mb `#c:molten_gold` | 700 | 114 | ore rate: metal; byproduct 270 mb `#c:molten_cobalt`; requires tag `c:geore_large_buds/gold` filled |
| metal/gold/geore/bud_medium | `#c:geore_medium_buds/gold` | 180 mb `#c:molten_gold` | 700 | 85 | ore rate: metal; byproduct 180 mb `#c:molten_cobalt`; requires tag `c:geore_medium_buds/gold` filled |
| metal/gold/geore/bud_small | `#c:geore_small_buds/gold` | 90 mb `#c:molten_gold` | 700 | 57 | ore rate: metal; byproduct 90 mb `#c:molten_cobalt`; requires tag `c:geore_small_buds/gold` filled |
| metal/gold/geore/cluster | `#c:geore_clusters/gold` | 360 mb `#c:molten_gold` | 700 | 142 | ore rate: metal; byproduct 360 mb `#c:molten_cobalt`; requires tag `c:geore_clusters/gold` filled |
| metal/gold/geore/shard | `#c:geore_shards/gold` | 90 mb `#c:molten_gold` | 700 | 57 | requires tag `c:geore_shards/gold` filled |
| metal/gold/gilded_blackstone | `minecraft:gilded_blackstone` | 30 mb `#c:molten_gold` | 700 | 33 | ore rate: metal; byproduct 90 mb `#c:molten_copper` |
| metal/gold/helmet | `minecraft:golden_helmet` | 450 mb `#c:molten_gold` | 700 | 127 | scales with damage |
| metal/gold/horse_armor | `minecraft:golden_horse_armor` | 630 mb `#c:molten_gold` | 700 | 151 |  |
| metal/gold/ingot | `#c:ingots/gold` | 90 mb `#c:molten_gold` | 700 | 57 |  |
| metal/gold/leggings | `#tconstruct:melting/gold/tools_costing_7` | 630 mb `#c:molten_gold` | 700 | 151 | scales with damage |
| metal/gold/nether_gold_ore | `minecraft:nether_gold_ore` | 90 mb `#c:molten_gold` | 700 | 57 | ore rate: metal; byproduct 90 mb `#c:molten_copper` |
| metal/gold/nugget | `#c:nuggets/gold` | 10 mb `#c:molten_gold` | 700 | 19 |  |
| metal/gold/nugget_3 | `tconstruct:gold_bars` | 30 mb `#c:molten_gold` | 700 | 33 |  |
| metal/gold/ore_dense | `{"fabric:type":"fabric:all","ingredients":[{"tag":"c:ores/gold"},{"tag":"c:ore_rates/dense"}]}` | 540 mb `#c:molten_gold` | 700 | 256 | ore rate: metal; byproduct 540 mb `#c:molten_cobalt`; requires tags `c:ores/gold` + `c:ore_rates/dense` filled |
| metal/gold/ore_singular | `{"fabric:type":"fabric:difference","base":{"tag":"c:ores/gold"},"subtracted":{"tag":"tconstruct:non_singular_ore_rates"}}` | 180 mb `#c:molten_gold` | 700 | 142 | ore rate: metal; byproduct 180 mb `#c:molten_cobalt`; requires tags `c` + `:` + `o` + `r` + `e` + `s` + `/` + `g` + `o` + `l` + `d` filled (ignoring `tconstruct:non_singular_ore_rates`) |
| metal/gold/oreberry | `oreberriesreplanted:gold_oreberry` | 10 mb `#c:molten_gold` | 700 | 19 | requires item `oreberriesreplanted:gold_oreberry` |
| metal/gold/plate | `#c:plates/gold` | 90 mb `#c:molten_gold` | 700 | 57 | requires tag `c:plates/gold` filled |
| metal/gold/platform | `tconstruct:gold_platform` | 100 mb `#c:molten_gold` | 700 | 60 |  |
| metal/gold/powered_rail | `minecraft:powered_rail` | 90 mb `#c:molten_gold` | 700 | 57 |  |
| metal/gold/pressure_plate | `minecraft:light_weighted_pressure_plate` | 180 mb `#c:molten_gold` | 700 | 80 |  |
| metal/gold/produce | `minecraft:glistering_melon_slice` / `minecraft:golden_carrot` | 80 mb `#c:molten_gold` | 700 | 54 |  |
| metal/gold/raw | `#c:raw_materials/gold` | 90 mb `#c:molten_gold` | 700 | 85 | ore rate: metal; byproduct 90 mb `#c:molten_cobalt` |
| metal/gold/raw_block | `#c:storage_blocks/raw_gold` | 810 mb `#c:molten_gold` | 700 | 341 | ore rate: metal; byproduct 810 mb `#c:molten_cobalt` |
| metal/gold/reinforcement | `tconstruct:gold_reinforcement` | 90 mb `#c:molten_gold` | 700 | 57 |  |
| metal/gold/sheetmetal | `#c:sheetmetals/gold` | 90 mb `#c:molten_gold` | 700 | 57 | requires tag `c:sheetmetals/gold` filled |
| metal/gold/shovel | `#tconstruct:melting/gold/tools_costing_1` | 90 mb `#c:molten_gold` | 700 | 57 | scales with damage |
| metal/gold/sword | `#tconstruct:melting/gold/tools_costing_2` | 180 mb `#c:molten_gold` | 700 | 80 | scales with damage |
| metal/gold/tools_complement_excavator | `tools_complement:gold_excavator` | 990 mb `#c:molten_gold` | 700 | 189 | scales with damage; requires item `tools_complement:gold_excavator` |
| metal/gold/tools_complement_hammer | `tools_complement:gold_hammer` | 1170 mb `#c:molten_gold` | 700 | 205 | scales with damage; requires item `tools_complement:gold_hammer` |
| metal/hepatizon/block | `#c:storage_blocks/hepatizon` | 810 mb `#c:molten_hepatizon` | 1400 | 233 |  |
| metal/hepatizon/ingot | `#c:ingots/hepatizon` | 90 mb `#c:molten_hepatizon` | 1400 | 78 |  |
| metal/hepatizon/nugget | `#c:nuggets/hepatizon` | 10 mb `#c:molten_hepatizon` | 1400 | 26 |  |
| metal/invar/axes | `#tconstruct:melting/invar/tools_costing_3` | 270 mb `#c:molten_invar` | 900 | 110 | scales with damage; requires tag `tconstruct:melting/invar/tools_costing_3` filled |
| metal/invar/block | `#c:storage_blocks/invar` | 810 mb `#c:molten_invar` | 900 | 190 | requires tag `c:storage_blocks/invar` filled |
| metal/invar/boots | `#c:armors/boots/invar` | 360 mb `#c:molten_invar` | 900 | 127 | scales with damage; requires tag `c:armors/boots/invar` filled |
| metal/invar/chestplate | `#c:armors/chestplates/invar` | 720 mb `#c:molten_invar` | 900 | 179 | scales with damage; requires tag `c:armors/chestplates/invar` filled |
| metal/invar/coin | `#c:coins/invar` | 30 mb `#c:molten_invar` | 900 | 42 | requires tag `c:coins/invar` filled |
| metal/invar/dust | `#c:dusts/invar` | 90 mb `#c:molten_invar` | 900 | 47 | requires tag `c:dusts/invar` filled |
| metal/invar/gear | `#c:gears/invar` | 360 mb `#c:molten_invar` | 900 | 127 | requires tag `c:gears/invar` filled |
| metal/invar/helmet | `#c:armors/helmets/invar` | 450 mb `#c:molten_invar` | 900 | 142 | scales with damage; requires tag `c:armors/helmets/invar` filled |
| metal/invar/ingot | `#c:ingots/invar` | 90 mb `#c:molten_invar` | 900 | 63 | requires tag `c:ingots/invar` filled |
| metal/invar/leggings | `#c:armors/leggings/invar` | 630 mb `#c:molten_invar` | 900 | 167 | scales with damage; requires tag `c:armors/leggings/invar` filled |
| metal/invar/nugget | `#c:nuggets/invar` | 10 mb `#c:molten_invar` | 900 | 21 | requires tag `c:nuggets/invar` filled |
| metal/invar/plate | `#c:plates/invar` | 90 mb `#c:molten_invar` | 900 | 63 | requires tag `c:plates/invar` filled |
| metal/invar/shovel | `#tconstruct:melting/invar/tools_costing_1` | 90 mb `#c:molten_invar` | 900 | 63 | scales with damage; requires tag `tconstruct:melting/invar/tools_costing_1` filled |
| metal/invar/sword | `#tconstruct:melting/invar/tools_costing_2` | 180 mb `#c:molten_invar` | 900 | 90 | scales with damage; requires tag `tconstruct:melting/invar/tools_costing_2` filled |
| metal/invar/tools_complement_excavator | `tools_complement:invar_excavator` | 990 mb `#c:molten_invar` | 900 | 210 | scales with damage; requires item `tools_complement:invar_excavator` |
| metal/invar/tools_complement_hammer | `tools_complement:invar_hammer` | 1170 mb `#c:molten_invar` | 900 | 228 | scales with damage; requires item `tools_complement:invar_hammer` |
| metal/iron/allomancy_flakes | `allomancy:iron_flakes` | 30 mb `#c:molten_iron` | 800 | 35 | requires item `allomancy:iron_flakes` |
| metal/iron/anvil | `minecraft:anvil` / `minecraft:chipped_anvil` / `minecraft:damaged_anvil` | 2790 mb `#c:molten_iron` | 800 | 335 |  |
| metal/iron/axes | `#tconstruct:melting/iron/tools_costing_3` | 270 mb `#c:molten_iron` | 800 | 104 | scales with damage |
| metal/iron/block | `#c:storage_blocks/iron` | 810 mb `#c:molten_iron` | 800 | 180 |  |
| metal/iron/boots | `minecraft:iron_boots` | 360 mb `#c:molten_iron` | 800 | 120 | scales with damage |
| metal/iron/bucket | `minecraft:bucket` | 270 mb `#c:molten_iron` | 800 | 104 |  |
| metal/iron/cauldron | `minecraft:cauldron` | 630 mb `#c:molten_iron` | 800 | 159 |  |
| metal/iron/chain | `minecraft:chain` | 110 mb `#c:molten_iron` | 800 | 66 |  |
| metal/iron/chain_boots | `minecraft:chainmail_boots` | 240 mb `#c:molten_iron` | 800 | 98 | scales with damage; byproduct 120 mb `#c:molten_steel` |
| metal/iron/chain_chestplate | `minecraft:chainmail_chestplate` | 480 mb `#c:molten_iron` | 800 | 139 | scales with damage; byproduct 240 mb `#c:molten_steel` |
| metal/iron/chain_helmet | `minecraft:chainmail_helmet` | 300 mb `#c:molten_iron` | 800 | 110 | scales with damage; byproduct 150 mb `#c:molten_steel` |
| metal/iron/chain_leggings | `minecraft:chainmail_leggings` | 420 mb `#c:molten_iron` | 800 | 130 | scales with damage; byproduct 210 mb `#c:molten_steel` |
| metal/iron/chestplate | `minecraft:iron_chestplate` | 720 mb `#c:molten_iron` | 800 | 170 | scales with damage |
| metal/iron/coin | `#c:coins/iron` | 30 mb `#c:molten_iron` | 800 | 40 | requires tag `c:coins/iron` filled |
| metal/iron/crossbow | `minecraft:crossbow` | 130 mb `#c:molten_iron` | 800 | 72 | scales with damage |
| metal/iron/dust | `#c:dusts/iron` | 90 mb `#c:molten_iron` | 800 | 45 | requires tag `c:dusts/iron` filled |
| metal/iron/gear | `#c:gears/iron` | 360 mb `#c:molten_iron` | 800 | 120 | requires tag `c:gears/iron` filled |
| metal/iron/geore/block | `#c:geore_blocks/iron` | 360 mb `#c:molten_iron` | 800 | 120 | requires tag `c:geore_blocks/iron` filled |
| metal/iron/geore/bud_large | `#c:geore_large_buds/iron` | 270 mb `#c:molten_iron` | 800 | 120 | ore rate: metal; byproduct 270 mb `#c:molten_steel`; requires tag `c:geore_large_buds/iron` filled |
| metal/iron/geore/bud_medium | `#c:geore_medium_buds/iron` | 180 mb `#c:molten_iron` | 800 | 90 | ore rate: metal; byproduct 180 mb `#c:molten_steel`; requires tag `c:geore_medium_buds/iron` filled |
| metal/iron/geore/bud_small | `#c:geore_small_buds/iron` | 90 mb `#c:molten_iron` | 800 | 60 | ore rate: metal; byproduct 90 mb `#c:molten_steel`; requires tag `c:geore_small_buds/iron` filled |
| metal/iron/geore/cluster | `#c:geore_clusters/iron` | 360 mb `#c:molten_iron` | 800 | 150 | ore rate: metal; byproduct 360 mb `#c:molten_steel`; requires tag `c:geore_clusters/iron` filled |
| metal/iron/geore/shard | `#c:geore_shards/iron` | 90 mb `#c:molten_iron` | 800 | 60 | requires tag `c:geore_shards/iron` filled |
| metal/iron/helmet | `minecraft:iron_helmet` | 450 mb `#c:molten_iron` | 800 | 134 | scales with damage |
| metal/iron/horse_armor | `minecraft:iron_horse_armor` | 630 mb `#c:molten_iron` | 800 | 159 |  |
| metal/iron/ingot | `#c:ingots/iron` | 90 mb `#c:molten_iron` | 800 | 60 |  |
| metal/iron/ingot_1 | `minecraft:activator_rail` / `minecraft:detector_rail` / `minecraft:stonecutter` / `minecraft:piston` / `minecraft:sticky_piston` | 90 mb `#c:molten_iron` | 800 | 60 |  |
| metal/iron/ingot_2 | `minecraft:heavy_weighted_pressure_plate` / `minecraft:iron_door` / `minecraft:smithing_table` | 180 mb `#c:molten_iron` | 800 | 85 |  |
| metal/iron/ingot_4 | `minecraft:compass` / `minecraft:iron_trapdoor` | 360 mb `#c:molten_iron` | 800 | 120 |  |
| metal/iron/ingot_5 | `minecraft:blast_furnace` / `minecraft:hopper` / `minecraft:minecart` | 450 mb `#c:molten_iron` | 800 | 134 |  |
| metal/iron/lantern | `minecraft:lantern` / `minecraft:soul_lantern` | 80 mb `#c:molten_iron` | 800 | 57 |  |
| metal/iron/leggings | `#tconstruct:melting/iron/tools_costing_7` | 630 mb `#c:molten_iron` | 800 | 159 | scales with damage |
| metal/iron/nugget | `#c:nuggets/iron` | 10 mb `#c:molten_iron` | 800 | 20 |  |
| metal/iron/nugget_3 | `minecraft:iron_bars` / `minecraft:rail` | 30 mb `#c:molten_iron` | 800 | 35 |  |
| metal/iron/ore_dense | `{"fabric:type":"fabric:all","ingredients":[{"tag":"c:ores/iron"},{"tag":"c:ore_rates/dense"}]}` | 540 mb `#c:molten_iron` | 800 | 271 | ore rate: metal; byproduct 540 mb `#c:molten_steel`; requires tags `c:ores/iron` + `c:ore_rates/dense` filled |
| metal/iron/ore_singular | `{"fabric:type":"fabric:difference","base":{"tag":"c:ores/iron"},"subtracted":{"tag":"tconstruct:non_singular_ore_rates"}}` | 180 mb `#c:molten_iron` | 800 | 150 | ore rate: metal; byproduct 180 mb `#c:molten_steel`; requires tags `c` + `:` + `o` + `r` + `e` + `s` + `/` + `i` + `r` + `o` + `n` filled (ignoring `tconstruct:non_singular_ore_rates`) |
| metal/iron/ore_sparse | `{"fabric:type":"fabric:all","ingredients":[{"tag":"c:ores/iron"},{"tag":"c:ore_rates/sparse"}]}` | 90 mb `#c:molten_iron` | 800 | 90 | ore rate: metal; byproduct 90 mb `#c:molten_steel`; requires tags `c:ores/iron` + `c:ore_rates/sparse` filled |
| metal/iron/oreberry | `oreberriesreplanted:iron_oreberry` | 10 mb `#c:molten_iron` | 800 | 20 | requires item `oreberriesreplanted:iron_oreberry` |
| metal/iron/plate | `#c:plates/iron` | 90 mb `#c:molten_iron` | 800 | 60 | requires tag `c:plates/iron` filled |
| metal/iron/platform | `tconstruct:iron_platform` | 100 mb `#c:molten_iron` | 800 | 63 |  |
| metal/iron/raw | `#c:raw_materials/iron` | 90 mb `#c:molten_iron` | 800 | 90 | ore rate: metal; byproduct 90 mb `#c:molten_steel` |
| metal/iron/raw_block | `#c:storage_blocks/raw_iron` | 810 mb `#c:molten_iron` | 800 | 361 | ore rate: metal; byproduct 810 mb `#c:molten_steel` |
| metal/iron/reinforcement | `tconstruct:iron_reinforcement` | 90 mb `#c:molten_iron` | 800 | 60 |  |
| metal/iron/rod | `#c:rods/iron` | 45 mb `#c:molten_iron` | 800 | 12 | requires tag `c:rods/iron` filled |
| metal/iron/sheetmetal | `#c:sheetmetals/iron` | 90 mb `#c:molten_iron` | 800 | 60 | requires tag `c:sheetmetals/iron` filled |
| metal/iron/shovel | `#tconstruct:melting/iron/tools_costing_1` | 90 mb `#c:molten_iron` | 800 | 60 | scales with damage |
| metal/iron/sword | `#tconstruct:melting/iron/tools_costing_2` | 180 mb `#c:molten_iron` | 800 | 85 | scales with damage |
| metal/iron/tools_complement_hammer | `tools_complement:iron_hammer` | 1170 mb `#c:molten_iron` | 800 | 217 | scales with damage; requires item `tools_complement:iron_hammer` |
| metal/iron/tools_costing_11 | `#tconstruct:melting/iron/tools_costing_11` | 990 mb `#c:molten_iron` | 800 | 199 | scales with damage; requires tag `tconstruct:melting/iron/tools_costing_11` filled |
| metal/iron/tripwire | `minecraft:tripwire_hook` | 40 mb `#c:molten_iron` | 800 | 40 |  |
| metal/knightmetal/axes | `#tconstruct:melting/knightmetal/tools_costing_3` | 270 mb `tconstruct:molten_knightmetal` | 1300 | 130 | scales with damage; requires tag `tconstruct:melting/knightmetal/tools_costing_3` filled |
| metal/knightmetal/block | `#c:storage_blocks/knightmetal` | 810 mb `tconstruct:molten_knightmetal` | 1300 | 225 |  |
| metal/knightmetal/cluster | `tconstruct:knightmetal_cluster` | 40 mb `tconstruct:molten_knightmetal` | 1300 | 187 |  |
| metal/knightmetal/fluid_cannon | `tconstruct:end_fluid_cannon` | 450 mb `tconstruct:molten_knightmetal` | 1300 | 187 |  |
| metal/knightmetal/ingot | `#c:ingots/knightmetal` | 90 mb `tconstruct:molten_knightmetal` | 1300 | 75 |  |
| metal/knightmetal/leggings | `#tconstruct:melting/knightmetal/tools_costing_7` | 630 mb `tconstruct:molten_knightmetal` | 1300 | 198 | scales with damage; requires tag `tconstruct:melting/knightmetal/tools_costing_7` filled |
| metal/knightmetal/nugget | `#c:nuggets/knightmetal` | 10 mb `tconstruct:molten_knightmetal` | 1300 | 25 |  |
| metal/knightmetal/raw | `#c:raw_materials/knightmetal` | 90 mb `tconstruct:molten_knightmetal` | 1300 | 75 | requires tag `c:raw_materials/knightmetal` filled |
| metal/knightmetal/raw_nugget | `#c:raw_nuggets/knightmetal` | 10 mb `tconstruct:molten_knightmetal` | 1300 | 37 |  |
| metal/knightmetal/twilightforest_block_and_chain | `twilightforest:block_and_chain` | 1440 mb `tconstruct:molten_knightmetal` | 1300 | 300 | scales with damage; requires item `twilightforest:block_and_chain` |
| metal/knightmetal/twilightforest_boots | `twilightforest:knightmetal_boots` | 360 mb `tconstruct:molten_knightmetal` | 1300 | 150 | scales with damage; requires item `twilightforest:knightmetal_boots` |
| metal/knightmetal/twilightforest_chestplate | `twilightforest:knightmetal_chestplate` | 720 mb `tconstruct:molten_knightmetal` | 1300 | 212 | scales with damage; requires item `twilightforest:knightmetal_chestplate` |
| metal/knightmetal/twilightforest_helmet | `twilightforest:knightmetal_helmet` | 450 mb `tconstruct:molten_knightmetal` | 1300 | 167 | scales with damage; requires item `twilightforest:knightmetal_helmet` |
| metal/knightmetal/twilightforest_ring | `twilightforest:knightmetal_ring` | 360 mb `tconstruct:molten_knightmetal` | 1300 | 150 | requires item `twilightforest:knightmetal_ring` |
| metal/knightmetal/twilightforest_sword | `twilightforest:knightmetal_sword` | 180 mb `tconstruct:molten_knightmetal` | 1300 | 106 | scales with damage; requires item `twilightforest:knightmetal_sword` |
| metal/knightslime/block | `#c:storage_blocks/knightslime` | 810 mb `tconstruct:molten_knightslime` | 1125 | 210 |  |
| metal/knightslime/ingot | `#c:ingots/knightslime` | 90 mb `tconstruct:molten_knightslime` | 1125 | 70 |  |
| metal/knightslime/nugget | `#c:nuggets/knightslime` | 10 mb `tconstruct:molten_knightslime` | 1125 | 23 |  |
| metal/lead/allomancy_flakes | `allomancy:lead_flakes` | 30 mb `#c:molten_lead` | 330 | 25 | requires item `allomancy:lead_flakes` |
| metal/lead/axes | `#tconstruct:melting/lead/tools_costing_3` | 270 mb `#c:molten_lead` | 330 | 75 | scales with damage; requires tag `tconstruct:melting/lead/tools_costing_3` filled |
| metal/lead/block | `#c:storage_blocks/lead` | 810 mb `#c:molten_lead` | 330 | 130 | requires tag `c:storage_blocks/lead` filled |
| metal/lead/boots | `#c:armors/boots/lead` | 360 mb `#c:molten_lead` | 330 | 87 | scales with damage; requires tag `c:armors/boots/lead` filled |
| metal/lead/chestplate | `#c:armors/chestplates/lead` | 720 mb `#c:molten_lead` | 330 | 123 | scales with damage; requires tag `c:armors/chestplates/lead` filled |
| metal/lead/coin | `#c:coins/lead` | 30 mb `#c:molten_lead` | 330 | 29 | requires tag `c:coins/lead` filled |
| metal/lead/dust | `#c:dusts/lead` | 90 mb `#c:molten_lead` | 330 | 33 | requires tag `c:dusts/lead` filled |
| metal/lead/gear | `#c:gears/lead` | 360 mb `#c:molten_lead` | 330 | 87 | requires tag `c:gears/lead` filled |
| metal/lead/helmet | `#c:armors/helmets/lead` | 450 mb `#c:molten_lead` | 330 | 97 | scales with damage; requires tag `c:armors/helmets/lead` filled |
| metal/lead/ingot | `#c:ingots/lead` | 90 mb `#c:molten_lead` | 330 | 43 | requires tag `c:ingots/lead` filled |
| metal/lead/leggings | `#c:armors/leggings/lead` | 630 mb `#c:molten_lead` | 330 | 115 | scales with damage; requires tag `c:armors/leggings/lead` filled |
| metal/lead/nugget | `#c:nuggets/lead` | 10 mb `#c:molten_lead` | 330 | 14 | requires tag `c:nuggets/lead` filled |
| metal/lead/oreberry | `oreberriesreplanted:lead_oreberry` | 10 mb `#c:molten_lead` | 330 | 14 | requires item `oreberriesreplanted:lead_oreberry` |
| metal/lead/plate | `#c:plates/lead` | 90 mb `#c:molten_lead` | 330 | 43 | requires tag `c:plates/lead` filled |
| metal/lead/sheetmetal | `#c:sheetmetals/lead` | 90 mb `#c:molten_lead` | 330 | 43 | requires tag `c:sheetmetals/lead` filled |
| metal/lead/shovel | `#tconstruct:melting/lead/tools_costing_1` | 90 mb `#c:molten_lead` | 330 | 43 | scales with damage; requires tag `tconstruct:melting/lead/tools_costing_1` filled |
| metal/lead/sword | `#tconstruct:melting/lead/tools_costing_2` | 180 mb `#c:molten_lead` | 330 | 61 | scales with damage; requires tag `tconstruct:melting/lead/tools_costing_2` filled |
| metal/lead/tools_complement_excavator | `tools_complement:lead_excavator` | 990 mb `#c:molten_lead` | 330 | 144 | scales with damage; requires item `tools_complement:lead_excavator` |
| metal/lead/tools_complement_hammer | `tools_complement:lead_hammer` | 1170 mb `#c:molten_lead` | 330 | 157 | scales with damage; requires item `tools_complement:lead_hammer` |
| metal/lead/wire | `#c:wires/lead` | 45 mb `#c:molten_lead` | 330 | 9 | requires tag `c:wires/lead` filled |
| metal/lumium/block | `#c:storage_blocks/lumium` | 810 mb `#c:molten_lumium` | 1050 | 203 | requires tag `c:storage_blocks/lumium` filled |
| metal/lumium/coin | `#c:coins/lumium` | 30 mb `#c:molten_lumium` | 1050 | 45 | requires tag `c:coins/lumium` filled |
| metal/lumium/dust | `#c:dusts/lumium` | 90 mb `#c:molten_lumium` | 1050 | 51 | requires tag `c:dusts/lumium` filled |
| metal/lumium/gear | `#c:gears/lumium` | 360 mb `#c:molten_lumium` | 1050 | 136 | requires tag `c:gears/lumium` filled |
| metal/lumium/ingot | `#c:ingots/lumium` | 90 mb `#c:molten_lumium` | 1050 | 68 | requires tag `c:ingots/lumium` filled |
| metal/lumium/nugget | `#c:nuggets/lumium` | 10 mb `#c:molten_lumium` | 1050 | 23 | requires tag `c:nuggets/lumium` filled |
| metal/lumium/plate | `#c:plates/lumium` | 90 mb `#c:molten_lumium` | 1050 | 68 | requires tag `c:plates/lumium` filled |
| metal/manyullyn/block | `#c:storage_blocks/manyullyn` | 810 mb `#c:molten_manyullyn` | 1200 | 216 |  |
| metal/manyullyn/ingot | `#c:ingots/manyullyn` | 90 mb `#c:molten_manyullyn` | 1200 | 72 |  |
| metal/manyullyn/nugget | `#c:nuggets/manyullyn` | 10 mb `#c:molten_manyullyn` | 1200 | 24 |  |
| metal/molten_debris/debris_nugget | `#c:nuggets/netherite_scrap` | 10 mb `tconstruct:molten_debris` | 1175 | 24 |  |
| metal/molten_debris/ore | `#c:ores/netherite_scrap` | 90 mb `tconstruct:molten_debris` | 1175 | 143 | ore rate: metal; byproduct 30 mb `#c:molten_netherite` |
| metal/molten_debris/scrap | `#c:ingots/netherite_scrap` | 90 mb `tconstruct:molten_debris` | 1175 | 71 |  |
| metal/netherite/axes | `#tconstruct:melting/netherite/tools_costing_3` | 90 mb `#c:molten_netherite` | 1250 | 74 | scales with damage; byproduct 300 mb `tconstruct:molten_diamond` |
| metal/netherite/block | `#c:storage_blocks/netherite` | 810 mb `#c:molten_netherite` | 1250 | 221 |  |
| metal/netherite/boots | `minecraft:netherite_boots` | 90 mb `#c:molten_netherite` | 1250 | 74 | scales with damage; byproduct 400 mb `tconstruct:molten_diamond` |
| metal/netherite/chestplate | `minecraft:netherite_chestplate` | 90 mb `#c:molten_netherite` | 1250 | 74 | scales with damage; byproduct 800 mb `tconstruct:molten_diamond` |
| metal/netherite/coin | `#c:coins/netherite` | 30 mb `#c:molten_netherite` | 1250 | 49 | requires tag `c:coins/netherite` filled |
| metal/netherite/dust | `#c:dusts/netherite` | 90 mb `#c:molten_netherite` | 1250 | 55 | requires tag `c:dusts/netherite` filled |
| metal/netherite/excavator | `tools_complement:netherite_excavator` | 90 mb `#c:molten_netherite` | 1250 | 74 | scales with damage; byproduct 1100 mb `tconstruct:molten_diamond`; requires item `tools_complement:netherite_excavator` |
| metal/netherite/gear | `#c:gears/netherite` | 360 mb `#c:molten_netherite` | 1250 | 147 | requires tag `c:gears/netherite` filled |
| metal/netherite/hammer | `tools_complement:netherite_hammer` | 90 mb `#c:molten_netherite` | 1250 | 74 | scales with damage; byproduct 1300 mb `tconstruct:molten_diamond`; requires item `tools_complement:netherite_hammer` |
| metal/netherite/helmet | `minecraft:netherite_helmet` | 90 mb `#c:molten_netherite` | 1250 | 74 | scales with damage; byproduct 500 mb `tconstruct:molten_diamond` |
| metal/netherite/ingot | `#c:ingots/netherite` | 90 mb `#c:molten_netherite` | 1250 | 74 |  |
| metal/netherite/leggings | `#tconstruct:melting/netherite/tools_costing_7` | 90 mb `#c:molten_netherite` | 1250 | 74 | scales with damage; byproduct 700 mb `tconstruct:molten_diamond` |
| metal/netherite/lodestone | `minecraft:lodestone` | 90 mb `#c:molten_netherite` | 1250 | 74 |  |
| metal/netherite/nugget | `#c:nuggets/netherite` | 10 mb `#c:molten_netherite` | 1250 | 25 |  |
| metal/netherite/plate | `#c:plates/netherite` | 90 mb `#c:molten_netherite` | 1250 | 74 | requires tag `c:plates/netherite` filled |
| metal/netherite/shovel | `#tconstruct:melting/netherite/tools_costing_1` | 90 mb `#c:molten_netherite` | 1250 | 74 | scales with damage; byproduct 100 mb `tconstruct:molten_diamond` |
| metal/netherite/sword | `#tconstruct:melting/netherite/tools_costing_2` | 90 mb `#c:molten_netherite` | 1250 | 74 | scales with damage; byproduct 200 mb `tconstruct:molten_diamond` |
| metal/nickel/axes | `#tconstruct:melting/nickel/tools_costing_3` | 270 mb `#c:molten_nickel` | 950 | 112 | scales with damage; requires tag `tconstruct:melting/nickel/tools_costing_3` filled |
| metal/nickel/block | `#c:storage_blocks/nickel` | 810 mb `#c:molten_nickel` | 950 | 194 | requires tag `c:storage_blocks/nickel` filled |
| metal/nickel/boots | `#c:armors/boots/nickel` | 360 mb `#c:molten_nickel` | 950 | 130 | scales with damage; requires tag `c:armors/boots/nickel` filled |
| metal/nickel/chestplate | `#c:armors/chestplates/nickel` | 720 mb `#c:molten_nickel` | 950 | 183 | scales with damage; requires tag `c:armors/chestplates/nickel` filled |
| metal/nickel/coin | `#c:coins/nickel` | 30 mb `#c:molten_nickel` | 950 | 43 | requires tag `c:coins/nickel` filled |
| metal/nickel/dust | `#c:dusts/nickel` | 90 mb `#c:molten_nickel` | 950 | 49 | requires tag `c:dusts/nickel` filled |
| metal/nickel/gear | `#c:gears/nickel` | 360 mb `#c:molten_nickel` | 950 | 130 | requires tag `c:gears/nickel` filled |
| metal/nickel/helmet | `#c:armors/helmets/nickel` | 450 mb `#c:molten_nickel` | 950 | 145 | scales with damage; requires tag `c:armors/helmets/nickel` filled |
| metal/nickel/ingot | `#c:ingots/nickel` | 90 mb `#c:molten_nickel` | 950 | 65 | requires tag `c:ingots/nickel` filled |
| metal/nickel/leggings | `#c:armors/leggings/nickel` | 630 mb `#c:molten_nickel` | 950 | 171 | scales with damage; requires tag `c:armors/leggings/nickel` filled |
| metal/nickel/nugget | `#c:nuggets/nickel` | 10 mb `#c:molten_nickel` | 950 | 22 | requires tag `c:nuggets/nickel` filled |
| metal/nickel/oreberry | `oreberriesreplanted:nickel_oreberry` | 10 mb `#c:molten_nickel` | 950 | 22 | requires item `oreberriesreplanted:nickel_oreberry` |
| metal/nickel/plate | `#c:plates/nickel` | 90 mb `#c:molten_nickel` | 950 | 65 | requires tag `c:plates/nickel` filled |
| metal/nickel/sheetmetal | `#c:sheetmetals/nickel` | 90 mb `#c:molten_nickel` | 950 | 65 | requires tag `c:sheetmetals/nickel` filled |
| metal/nickel/shovel | `#tconstruct:melting/nickel/tools_costing_1` | 90 mb `#c:molten_nickel` | 950 | 65 | scales with damage; requires tag `tconstruct:melting/nickel/tools_costing_1` filled |
| metal/nickel/sword | `#tconstruct:melting/nickel/tools_costing_2` | 180 mb `#c:molten_nickel` | 950 | 92 | scales with damage; requires tag `tconstruct:melting/nickel/tools_costing_2` filled |
| metal/nickel/tools_complement_excavator | `tools_complement:nickel_excavator` | 990 mb `#c:molten_nickel` | 950 | 215 | scales with damage; requires item `tools_complement:nickel_excavator` |
| metal/nickel/tools_complement_hammer | `tools_complement:nickel_hammer` | 1170 mb `#c:molten_nickel` | 950 | 234 | scales with damage; requires item `tools_complement:nickel_hammer` |
| metal/nicrosil/allomancy_flakes | `allomancy:nicrosil_flakes` | 30 mb `#c:molten_nicrosil` | 1100 | 40 | requires item `allomancy:nicrosil_flakes` |
| metal/nicrosil/block | `#c:storage_blocks/nicrosil` | 810 mb `#c:molten_nicrosil` | 1100 | 208 | requires tag `c:storage_blocks/nicrosil` filled |
| metal/nicrosil/dust | `#c:dusts/nicrosil` | 90 mb `#c:molten_nicrosil` | 1100 | 52 | requires tag `c:dusts/nicrosil` filled |
| metal/nicrosil/ingot | `#c:ingots/nicrosil` | 90 mb `#c:molten_nicrosil` | 1100 | 69 | requires tag `c:ingots/nicrosil` filled |
| metal/nicrosil/nugget | `#c:nuggets/nicrosil` | 10 mb `#c:molten_nicrosil` | 1100 | 23 | requires tag `c:nuggets/nicrosil` filled |
| metal/obsidian/reinforcement | `tconstruct:obsidian_reinforcement` | 1000 mb `tconstruct:molten_obsidian` | 1000 | 221 |  |
| metal/osmium/axes | `#tconstruct:melting/osmium/tools_costing_3` | 270 mb `#c:molten_osmium` | 975 | 114 | scales with damage; requires tag `tconstruct:melting/osmium/tools_costing_3` filled |
| metal/osmium/block | `#c:storage_blocks/osmium` | 810 mb `#c:molten_osmium` | 975 | 197 | requires tag `c:storage_blocks/osmium` filled |
| metal/osmium/boots | `#c:armors/boots/osmium` | 360 mb `#c:molten_osmium` | 975 | 131 | scales with damage; requires tag `c:armors/boots/osmium` filled |
| metal/osmium/chestplate | `#c:armors/chestplates/osmium` | 720 mb `#c:molten_osmium` | 975 | 185 | scales with damage; requires tag `c:armors/chestplates/osmium` filled |
| metal/osmium/dust | `#c:dusts/osmium` | 90 mb `#c:molten_osmium` | 975 | 49 | requires tag `c:dusts/osmium` filled |
| metal/osmium/helmet | `#c:armors/helmets/osmium` | 450 mb `#c:molten_osmium` | 975 | 147 | scales with damage; requires tag `c:armors/helmets/osmium` filled |
| metal/osmium/ingot | `#c:ingots/osmium` | 90 mb `#c:molten_osmium` | 975 | 66 | requires tag `c:ingots/osmium` filled |
| metal/osmium/leggings | `#tconstruct:melting/osmium/tools_costing_7` | 630 mb `#c:molten_osmium` | 975 | 173 | scales with damage; requires tag `tconstruct:melting/osmium/tools_costing_7` filled |
| metal/osmium/mekanism_shield | `mekanism:osmium_shield` | 540 mb `#c:molten_osmium` | 975 | 161 | scales with damage; requires item `mekanism:osmium_shield` |
| metal/osmium/nugget | `#c:nuggets/osmium` | 10 mb `#c:molten_osmium` | 975 | 22 | requires tag `c:nuggets/osmium` filled |
| metal/osmium/ore_dense | `{"fabric:type":"fabric:all","ingredients":[{"tag":"c:ores/osmium"},{"tag":"c:ore_rates/dense"}]}` | 540 mb `#c:molten_osmium` | 975 | 295 | ore rate: metal; byproduct 540 mb `#c:molten_iron`; requires tags `c:ores/osmium` + `c:ore_rates/dense` filled |
| metal/osmium/ore_singular | `{"fabric:type":"fabric:difference","base":{"tag":"c:ores/osmium"},"subtracted":{"tag":"tconstruct:non_singular_ore_rates"}}` | 180 mb `#c:molten_osmium` | 975 | 164 | ore rate: metal; byproduct 180 mb `#c:molten_iron`; requires tags `c` + `:` + `o` + `r` + `e` + `s` + `/` + `o` + `s` + `m` + `i` + `u` + `m` filled (ignoring `tconstruct:non_singular_ore_rates`) |
| metal/osmium/ore_sparse | `{"fabric:type":"fabric:all","ingredients":[{"tag":"c:ores/osmium"},{"tag":"c:ore_rates/sparse"}]}` | 90 mb `#c:molten_osmium` | 975 | 98 | ore rate: metal; byproduct 90 mb `#c:molten_iron`; requires tags `c:ores/osmium` + `c:ore_rates/sparse` filled |
| metal/osmium/oreberry | `oreberriesreplanted:osmium_oreberry` | 10 mb `#c:molten_osmium` | 975 | 22 | requires item `oreberriesreplanted:osmium_oreberry` |
| metal/osmium/raw | `#c:raw_materials/osmium` | 90 mb `#c:molten_osmium` | 975 | 98 | ore rate: metal; byproduct 90 mb `#c:molten_iron`; requires tag `c:raw_materials/osmium` filled |
| metal/osmium/raw_block | `#c:storage_blocks/raw_osmium` | 810 mb `#c:molten_osmium` | 975 | 393 | ore rate: metal; byproduct 810 mb `#c:molten_iron`; requires tag `c:storage_blocks/raw_osmium` filled |
| metal/osmium/shovel | `#c:tools/shovels/osmium` | 90 mb `#c:molten_osmium` | 975 | 66 | scales with damage; requires tag `c:tools/shovels/osmium` filled |
| metal/osmium/sword | `#tconstruct:melting/osmium/tools_costing_2` | 180 mb `#c:molten_osmium` | 975 | 93 | scales with damage; requires tag `tconstruct:melting/osmium/tools_costing_2` filled |
| metal/pewter/allomancy_flakes | `allomancy:pewter_flakes` | 30 mb `#c:molten_pewter` | 400 | 27 | requires item `allomancy:pewter_flakes` |
| metal/pewter/block | `#c:storage_blocks/pewter` | 810 mb `#c:molten_pewter` | 400 | 139 | requires tag `c:storage_blocks/pewter` filled |
| metal/pewter/dust | `#c:dusts/pewter` | 90 mb `#c:molten_pewter` | 400 | 35 | requires tag `c:dusts/pewter` filled |
| metal/pewter/ingot | `#c:ingots/pewter` | 90 mb `#c:molten_pewter` | 400 | 46 | requires tag `c:ingots/pewter` filled |
| metal/pewter/nugget | `#c:nuggets/pewter` | 10 mb `#c:molten_pewter` | 400 | 15 | requires tag `c:nuggets/pewter` filled |
| metal/pig_iron/block | `#c:storage_blocks/pig_iron` | 810 mb `tconstruct:molten_pig_iron` | 811 | 181 |  |
| metal/pig_iron/ingot | `#c:ingots/pig_iron` | 90 mb `tconstruct:molten_pig_iron` | 811 | 60 |  |
| metal/pig_iron/nugget | `#c:nuggets/pig_iron` | 10 mb `tconstruct:molten_pig_iron` | 811 | 20 |  |
| metal/platinum/block | `#c:storage_blocks/platinum` | 810 mb `#c:molten_platinum` | 970 | 196 | requires tag `c:storage_blocks/platinum` filled |
| metal/platinum/dust | `#c:dusts/platinum` | 90 mb `#c:molten_platinum` | 970 | 49 | requires tag `c:dusts/platinum` filled |
| metal/platinum/ingot | `#c:ingots/platinum` | 90 mb `#c:molten_platinum` | 970 | 65 | requires tag `c:ingots/platinum` filled |
| metal/platinum/nugget | `#c:nuggets/platinum` | 10 mb `#c:molten_platinum` | 970 | 22 | requires tag `c:nuggets/platinum` filled |
| metal/platinum/ore_dense | `{"fabric:type":"fabric:all","ingredients":[{"tag":"c:ores/platinum"},{"tag":"c:ore_rates/dense"}]}` | 540 mb `#c:molten_platinum` | 970 | 294 | ore rate: metal; byproduct 540 mb `#c:molten_gold`; requires tags `c:ores/platinum` + `c:ore_rates/dense` filled |
| metal/platinum/ore_singular | `{"fabric:type":"fabric:difference","base":{"tag":"c:ores/platinum"},"subtracted":{"tag":"tconstruct:non_singular_ore_rates"}}` | 180 mb `#c:molten_platinum` | 970 | 164 | ore rate: metal; byproduct 180 mb `#c:molten_gold`; requires tags `c` + `:` + `o` + `r` + `e` + `s` + `/` + `p` + `l` + `a` + `t` + `i` + `n` + `u` + `m` filled (ignoring `tconstruct:non_singular_ore_rates`) |
| metal/platinum/ore_sparse | `{"fabric:type":"fabric:all","ingredients":[{"tag":"c:ores/platinum"},{"tag":"c:ore_rates/sparse"}]}` | 90 mb `#c:molten_platinum` | 970 | 98 | ore rate: metal; byproduct 90 mb `#c:molten_gold`; requires tags `c:ores/platinum` + `c:ore_rates/sparse` filled |
| metal/platinum/raw | `#c:raw_materials/platinum` | 90 mb `#c:molten_platinum` | 970 | 98 | ore rate: metal; byproduct 90 mb `#c:molten_gold`; requires tag `c:raw_materials/platinum` filled |
| metal/platinum/raw_block | `#c:storage_blocks/raw_platinum` | 810 mb `#c:molten_platinum` | 970 | 393 | ore rate: metal; byproduct 810 mb `#c:molten_gold`; requires tag `c:storage_blocks/raw_platinum` filled |
| metal/queens_slime/block | `#c:storage_blocks/queens_slime` | 810 mb `tconstruct:molten_queens_slime` | 1150 | 212 |  |
| metal/queens_slime/ingot | `#c:ingots/queens_slime` | 90 mb `tconstruct:molten_queens_slime` | 1150 | 71 |  |
| metal/queens_slime/nugget | `#c:nuggets/queens_slime` | 10 mb `tconstruct:molten_queens_slime` | 1150 | 24 |  |
| metal/refined_glowstone/axes | `#tconstruct:melting/refined_glowstone/tools_costing_3` | 270 mb `#c:molten_refined_glowstone` | 825 | 106 | scales with damage; requires tag `tconstruct:melting/refined_glowstone/tools_costing_3` filled |
| metal/refined_glowstone/block | `#c:storage_blocks/refined_glowstone` | 810 mb `#c:molten_refined_glowstone` | 825 | 183 | requires tag `c:storage_blocks/refined_glowstone` filled |
| metal/refined_glowstone/boots | `#c:armors/boots/refined_glowstone` | 360 mb `#c:molten_refined_glowstone` | 825 | 122 | scales with damage; requires tag `c:armors/boots/refined_glowstone` filled |
| metal/refined_glowstone/chestplate | `#c:armors/chestplates/refined_glowstone` | 720 mb `#c:molten_refined_glowstone` | 825 | 172 | scales with damage; requires tag `c:armors/chestplates/refined_glowstone` filled |
| metal/refined_glowstone/helmet | `#c:armors/helmets/refined_glowstone` | 450 mb `#c:molten_refined_glowstone` | 825 | 136 | scales with damage; requires tag `c:armors/helmets/refined_glowstone` filled |
| metal/refined_glowstone/ingot | `#c:ingots/refined_glowstone` | 90 mb `#c:molten_refined_glowstone` | 825 | 61 | requires tag `c:ingots/refined_glowstone` filled |
| metal/refined_glowstone/leggings | `#tconstruct:melting/refined_glowstone/tools_costing_7` | 630 mb `#c:molten_refined_glowstone` | 825 | 161 | scales with damage; requires tag `tconstruct:melting/refined_glowstone/tools_costing_7` filled |
| metal/refined_glowstone/mekanism_shield | `mekanism:refined_glowstone_shield` | 540 mb `#c:molten_refined_glowstone` | 825 | 149 | scales with damage; requires item `mekanism:refined_glowstone_shield` |
| metal/refined_glowstone/nugget | `#c:nuggets/refined_glowstone` | 10 mb `#c:molten_refined_glowstone` | 825 | 20 | requires tag `c:nuggets/refined_glowstone` filled |
| metal/refined_glowstone/shovel | `#c:tools/shovels/refined_glowstone` | 90 mb `#c:molten_refined_glowstone` | 825 | 61 | scales with damage; requires tag `c:tools/shovels/refined_glowstone` filled |
| metal/refined_glowstone/sword | `#tconstruct:melting/refined_glowstone/tools_costing_2` | 180 mb `#c:molten_refined_glowstone` | 825 | 86 | scales with damage; requires tag `tconstruct:melting/refined_glowstone/tools_costing_2` filled |
| metal/refined_obsidian/axes | `#tconstruct:melting/refined_obsidian/tools_costing_3` | 270 mb `#c:molten_refined_obsidian` | 1475 | 138 | scales with damage; requires tag `tconstruct:melting/refined_obsidian/tools_costing_3` filled |
| metal/refined_obsidian/block | `#c:storage_blocks/refined_obsidian` | 810 mb `#c:molten_refined_obsidian` | 1475 | 239 | requires tag `c:storage_blocks/refined_obsidian` filled |
| metal/refined_obsidian/boots | `#c:armors/boots/refined_obsidian` | 360 mb `#c:molten_refined_obsidian` | 1475 | 159 | scales with damage; requires tag `c:armors/boots/refined_obsidian` filled |
| metal/refined_obsidian/chestplate | `#c:armors/chestplates/refined_obsidian` | 720 mb `#c:molten_refined_obsidian` | 1475 | 225 | scales with damage; requires tag `c:armors/chestplates/refined_obsidian` filled |
| metal/refined_obsidian/helmet | `#c:armors/helmets/refined_obsidian` | 450 mb `#c:molten_refined_obsidian` | 1475 | 178 | scales with damage; requires tag `c:armors/helmets/refined_obsidian` filled |
| metal/refined_obsidian/ingot | `#c:ingots/refined_obsidian` | 90 mb `#c:molten_refined_obsidian` | 1475 | 80 | requires tag `c:ingots/refined_obsidian` filled |
| metal/refined_obsidian/leggings | `#tconstruct:melting/refined_obsidian/tools_costing_7` | 630 mb `#c:molten_refined_obsidian` | 1475 | 211 | scales with damage; requires tag `tconstruct:melting/refined_obsidian/tools_costing_7` filled |
| metal/refined_obsidian/mekanism_shield | `mekanism:refined_obsidian_shield` | 540 mb `#c:molten_refined_obsidian` | 1475 | 195 | scales with damage; requires item `mekanism:refined_obsidian_shield` |
| metal/refined_obsidian/nugget | `#c:nuggets/refined_obsidian` | 10 mb `#c:molten_refined_obsidian` | 1475 | 27 | requires tag `c:nuggets/refined_obsidian` filled |
| metal/refined_obsidian/shovel | `#c:tools/shovels/refined_obsidian` | 90 mb `#c:molten_refined_obsidian` | 1475 | 80 | scales with damage; requires tag `c:tools/shovels/refined_obsidian` filled |
| metal/refined_obsidian/sword | `#tconstruct:melting/refined_obsidian/tools_costing_2` | 180 mb `#c:molten_refined_obsidian` | 1475 | 113 | scales with damage; requires tag `tconstruct:melting/refined_obsidian/tools_costing_2` filled |
| metal/rose_gold/block | `#c:storage_blocks/rose_gold` | 810 mb `#c:molten_rose_gold` | 550 | 155 |  |
| metal/rose_gold/coin | `#c:coins/rose_gold` | 30 mb `#c:molten_rose_gold` | 550 | 34 | requires tag `c:coins/rose_gold` filled |
| metal/rose_gold/dust | `#c:dusts/rose_gold` | 90 mb `#c:molten_rose_gold` | 550 | 39 | requires tag `c:dusts/rose_gold` filled |
| metal/rose_gold/gear | `#c:gears/rose_gold` | 360 mb `#c:molten_rose_gold` | 550 | 103 | requires tag `c:gears/rose_gold` filled |
| metal/rose_gold/ingot | `#c:ingots/rose_gold` | 90 mb `#c:molten_rose_gold` | 550 | 52 |  |
| metal/rose_gold/nugget | `#c:nuggets/rose_gold` | 10 mb `#c:molten_rose_gold` | 550 | 17 |  |
| metal/rose_gold/plate | `#c:plates/rose_gold` | 90 mb `#c:molten_rose_gold` | 550 | 52 | requires tag `c:plates/rose_gold` filled |
| metal/rose_gold/silky_cloth | `tconstruct:silky_cloth` | 90 mb `#c:molten_rose_gold` | 550 | 52 |  |
| metal/signalum/block | `#c:storage_blocks/signalum` | 810 mb `#c:molten_signalum` | 999 | 199 | requires tag `c:storage_blocks/signalum` filled |
| metal/signalum/coin | `#c:coins/signalum` | 30 mb `#c:molten_signalum` | 999 | 44 | requires tag `c:coins/signalum` filled |
| metal/signalum/dust | `#c:dusts/signalum` | 90 mb `#c:molten_signalum` | 999 | 50 | requires tag `c:dusts/signalum` filled |
| metal/signalum/gear | `#c:gears/signalum` | 360 mb `#c:molten_signalum` | 999 | 133 | requires tag `c:gears/signalum` filled |
| metal/signalum/ingot | `#c:ingots/signalum` | 90 mb `#c:molten_signalum` | 999 | 66 | requires tag `c:ingots/signalum` filled |
| metal/signalum/nugget | `#c:nuggets/signalum` | 10 mb `#c:molten_signalum` | 999 | 22 | requires tag `c:nuggets/signalum` filled |
| metal/signalum/plate | `#c:plates/signalum` | 90 mb `#c:molten_signalum` | 999 | 66 | requires tag `c:plates/signalum` filled |
| metal/silver/allomancy_flakes | `allomancy:silver_flakes` | 30 mb `#c:molten_silver` | 790 | 35 | requires item `allomancy:silver_flakes` |
| metal/silver/axes | `#tconstruct:melting/silver/tools_costing_3` | 270 mb `#c:molten_silver` | 790 | 104 | scales with damage; requires tag `tconstruct:melting/silver/tools_costing_3` filled |
| metal/silver/block | `#c:storage_blocks/silver` | 810 mb `#c:molten_silver` | 790 | 179 | requires tag `c:storage_blocks/silver` filled |
| metal/silver/boots | `#c:armors/boots/silver` | 360 mb `#c:molten_silver` | 790 | 120 | scales with damage; requires tag `c:armors/boots/silver` filled |
| metal/silver/chestplate | `#c:armors/chestplates/silver` | 720 mb `#c:molten_silver` | 790 | 169 | scales with damage; requires tag `c:armors/chestplates/silver` filled |
| metal/silver/coin | `#c:coins/silver` | 30 mb `#c:molten_silver` | 790 | 40 | requires tag `c:coins/silver` filled |
| metal/silver/dust | `#c:dusts/silver` | 90 mb `#c:molten_silver` | 790 | 45 | requires tag `c:dusts/silver` filled |
| metal/silver/gear | `#c:gears/silver` | 360 mb `#c:molten_silver` | 790 | 120 | requires tag `c:gears/silver` filled |
| metal/silver/helmet | `#c:armors/helmets/silver` | 450 mb `#c:molten_silver` | 790 | 134 | scales with damage; requires tag `c:armors/helmets/silver` filled |
| metal/silver/ingot | `#c:ingots/silver` | 90 mb `#c:molten_silver` | 790 | 60 | requires tag `c:ingots/silver` filled |
| metal/silver/leggings | `#c:armors/leggings/silver` | 630 mb `#c:molten_silver` | 790 | 158 | scales with damage; requires tag `c:armors/leggings/silver` filled |
| metal/silver/nugget | `#c:nuggets/silver` | 10 mb `#c:molten_silver` | 790 | 20 | requires tag `c:nuggets/silver` filled |
| metal/silver/oreberry | `oreberriesreplanted:silver_oreberry` | 10 mb `#c:molten_silver` | 790 | 20 | requires item `oreberriesreplanted:silver_oreberry` |
| metal/silver/plate | `#c:plates/silver` | 90 mb `#c:molten_silver` | 790 | 60 | requires tag `c:plates/silver` filled |
| metal/silver/sheetmetal | `#c:sheetmetals/silver` | 90 mb `#c:molten_silver` | 790 | 60 | requires tag `c:sheetmetals/silver` filled |
| metal/silver/shovel | `#tconstruct:melting/silver/tools_costing_1` | 90 mb `#c:molten_silver` | 790 | 60 | scales with damage; requires tag `tconstruct:melting/silver/tools_costing_1` filled |
| metal/silver/sword | `#tconstruct:melting/silver/tools_costing_2` | 180 mb `#c:molten_silver` | 790 | 85 | scales with damage; requires tag `tconstruct:melting/silver/tools_costing_2` filled |
| metal/silver/tools_complement_excavator | `tools_complement:silver_excavator` | 990 mb `#c:molten_silver` | 790 | 198 | scales with damage; requires item `tools_complement:silver_excavator` |
| metal/silver/tools_complement_hammer | `tools_complement:silver_hammer` | 1170 mb `#c:molten_silver` | 790 | 216 | scales with damage; requires item `tools_complement:silver_hammer` |
| metal/slimesteel/block | `#c:storage_blocks/slimesteel` | 810 mb `tconstruct:molten_slimesteel` | 900 | 190 |  |
| metal/slimesteel/ingot | `#c:ingots/slimesteel` | 90 mb `tconstruct:molten_slimesteel` | 900 | 63 |  |
| metal/slimesteel/nugget | `#c:nuggets/slimesteel` | 10 mb `tconstruct:molten_slimesteel` | 900 | 21 |  |
| metal/slimesteel/reinforcement | `tconstruct:slimesteel_reinforcement` | 30 mb `tconstruct:molten_slimesteel` | 900 | 37 | byproduct 250 mb `tconstruct:molten_obsidian` |
| metal/steel/allomancy_flakes | `allomancy:steel_flakes` | 30 mb `#c:molten_steel` | 950 | 37 | requires item `allomancy:steel_flakes` |
| metal/steel/axes | `#tconstruct:melting/steel/tools_costing_3` | 270 mb `#c:molten_steel` | 950 | 112 | scales with damage; requires tag `tconstruct:melting/steel/tools_costing_3` filled |
| metal/steel/block | `#c:storage_blocks/steel` | 810 mb `#c:molten_steel` | 950 | 194 |  |
| metal/steel/boots | `#tconstruct:melting/steel/tools_costing_4` | 360 mb `#c:molten_steel` | 950 | 130 | scales with damage; requires tag `tconstruct:melting/steel/tools_costing_4` filled |
| metal/steel/chestplate | `#tconstruct:melting/steel/tools_costing_8` | 720 mb `#c:molten_steel` | 950 | 183 | scales with damage; requires tag `tconstruct:melting/steel/tools_costing_8` filled |
| metal/steel/cluster | `tconstruct:steel_cluster` | 40 mb `#c:molten_steel` | 950 | 162 |  |
| metal/steel/coin | `#c:coins/steel` | 30 mb `#c:molten_steel` | 950 | 43 | requires tag `c:coins/steel` filled |
| metal/steel/dust | `#c:dusts/steel` | 90 mb `#c:molten_steel` | 950 | 49 | requires tag `c:dusts/steel` filled |
| metal/steel/gear | `#c:gears/steel` | 360 mb `#c:molten_steel` | 950 | 130 | requires tag `c:gears/steel` filled |
| metal/steel/helmet | `#tconstruct:melting/steel/tools_costing_5` | 450 mb `#c:molten_steel` | 950 | 145 | scales with damage; requires tag `tconstruct:melting/steel/tools_costing_5` filled |
| metal/steel/ingot | `#c:ingots/steel` | 90 mb `#c:molten_steel` | 950 | 65 |  |
| metal/steel/leggings | `#tconstruct:melting/steel/tools_costing_7` | 630 mb `#c:molten_steel` | 950 | 171 | scales with damage; requires tag `tconstruct:melting/steel/tools_costing_7` filled |
| metal/steel/mekanism_shield | `mekanism:steel_shield` | 540 mb `#c:molten_steel` | 950 | 159 | scales with damage; requires item `mekanism:steel_shield` |
| metal/steel/nugget | `#c:nuggets/steel` | 10 mb `#c:molten_steel` | 950 | 22 |  |
| metal/steel/plate | `#c:plates/steel` | 90 mb `#c:molten_steel` | 950 | 65 | requires tag `c:plates/steel` filled |
| metal/steel/railcraft_spike_maul | `railcraft:steel_spike_maul` | 990 mb `#c:molten_steel` | 950 | 215 | scales with damage; requires item `railcraft:steel_spike_maul` |
| metal/steel/raw | `#c:raw_materials/steel` | 90 mb `#c:molten_steel` | 950 | 97 | ore rate: metal; byproduct 90 mb `#c:molten_iron` |
| metal/steel/raw_block | `#c:storage_blocks/raw_steel` | 810 mb `#c:molten_steel` | 950 | 389 | ore rate: metal; byproduct 810 mb `#c:molten_iron` |
| metal/steel/raw_nugget | `#c:raw_nuggets/steel` | 10 mb `#c:molten_steel` | 950 | 32 |  |
| metal/steel/rod | `#c:rods/steel` | 45 mb `#c:molten_steel` | 950 | 13 | requires tag `c:rods/steel` filled |
| metal/steel/sheetmetal | `#c:sheetmetals/steel` | 90 mb `#c:molten_steel` | 950 | 65 | requires tag `c:sheetmetals/steel` filled |
| metal/steel/shovel | `#tconstruct:melting/steel/tools_costing_1` | 90 mb `#c:molten_steel` | 950 | 65 | scales with damage; requires tag `tconstruct:melting/steel/tools_costing_1` filled |
| metal/steel/sword | `#tconstruct:melting/steel/tools_costing_2` | 180 mb `#c:molten_steel` | 950 | 92 | scales with damage; requires tag `tconstruct:melting/steel/tools_costing_2` filled |
| metal/steel/wire | `#c:wires/steel` | 45 mb `#c:molten_steel` | 950 | 13 | requires tag `c:wires/steel` filled |
| metal/steeleaf/axes | `#tconstruct:melting/steeleaf/tools_costing_3` | 270 mb `tconstruct:molten_steeleaf` | 934 | 111 | scales with damage; requires tag `tconstruct:melting/steeleaf/tools_costing_3` filled |
| metal/steeleaf/block | `#c:storage_blocks/steeleaf` | 810 mb `tconstruct:molten_steeleaf` | 934 | 193 | requires tag `c:storage_blocks/steeleaf` filled |
| metal/steeleaf/ingot | `#c:ingots/steeleaf` | 90 mb `tconstruct:molten_steeleaf` | 934 | 64 | requires tag `c:ingots/steeleaf` filled |
| metal/steeleaf/nugget | `#c:nuggets/steeleaf` | 10 mb `tconstruct:molten_steeleaf` | 934 | 21 | requires tag `c:nuggets/steeleaf` filled |
| metal/steeleaf/sword | `#tconstruct:melting/steeleaf/tools_costing_2` | 180 mb `tconstruct:molten_steeleaf` | 934 | 91 | scales with damage; requires tag `tconstruct:melting/steeleaf/tools_costing_2` filled |
| metal/steeleaf/twilightforest_boots | `twilightforest:steeleaf_boots` | 360 mb `tconstruct:molten_steeleaf` | 934 | 129 | scales with damage; requires item `twilightforest:steeleaf_boots` |
| metal/steeleaf/twilightforest_chestplate | `twilightforest:steeleaf_chestplate` | 720 mb `tconstruct:molten_steeleaf` | 934 | 182 | scales with damage; requires item `twilightforest:steeleaf_chestplate` |
| metal/steeleaf/twilightforest_helmet | `twilightforest:steeleaf_helmet` | 450 mb `tconstruct:molten_steeleaf` | 934 | 144 | scales with damage; requires item `twilightforest:steeleaf_helmet` |
| metal/steeleaf/twilightforest_leggings | `twilightforest:steeleaf_leggings` | 630 mb `tconstruct:molten_steeleaf` | 934 | 170 | scales with damage; requires item `twilightforest:steeleaf_leggings` |
| metal/steeleaf/twilightforest_shovel | `twilightforest:steeleaf_shovel` | 90 mb `tconstruct:molten_steeleaf` | 934 | 64 | scales with damage; requires item `twilightforest:steeleaf_shovel` |
| metal/tin/allomancy_flakes | `allomancy:tin_flakes` | 30 mb `#c:molten_tin` | 225 | 23 | requires item `allomancy:tin_flakes` |
| metal/tin/axes | `#tconstruct:melting/tin/tools_costing_3` | 270 mb `#c:molten_tin` | 225 | 68 | scales with damage; requires tag `tconstruct:melting/tin/tools_costing_3` filled |
| metal/tin/block | `#c:storage_blocks/tin` | 810 mb `#c:molten_tin` | 225 | 117 | requires tag `c:storage_blocks/tin` filled |
| metal/tin/boots | `#c:armors/boots/tin` | 360 mb `#c:molten_tin` | 225 | 78 | scales with damage; requires tag `c:armors/boots/tin` filled |
| metal/tin/chestplate | `#c:armors/chestplates/tin` | 720 mb `#c:molten_tin` | 225 | 110 | scales with damage; requires tag `c:armors/chestplates/tin` filled |
| metal/tin/coin | `#c:coins/tin` | 30 mb `#c:molten_tin` | 225 | 26 | requires tag `c:coins/tin` filled |
| metal/tin/dust | `#c:dusts/tin` | 90 mb `#c:molten_tin` | 225 | 29 | requires tag `c:dusts/tin` filled |
| metal/tin/gear | `#c:gears/tin` | 360 mb `#c:molten_tin` | 225 | 78 | requires tag `c:gears/tin` filled |
| metal/tin/helmet | `#c:armors/helmets/tin` | 450 mb `#c:molten_tin` | 225 | 87 | scales with damage; requires tag `c:armors/helmets/tin` filled |
| metal/tin/ingot | `#c:ingots/tin` | 90 mb `#c:molten_tin` | 225 | 39 | requires tag `c:ingots/tin` filled |
| metal/tin/leggings | `#c:armors/leggings/tin` | 630 mb `#c:molten_tin` | 225 | 103 | scales with damage; requires tag `c:armors/leggings/tin` filled |
| metal/tin/nugget | `#c:nuggets/tin` | 10 mb `#c:molten_tin` | 225 | 13 | requires tag `c:nuggets/tin` filled |
| metal/tin/oreberry | `oreberriesreplanted:tin_oreberry` | 10 mb `#c:molten_tin` | 225 | 13 | requires item `oreberriesreplanted:tin_oreberry` |
| metal/tin/plate | `#c:plates/tin` | 90 mb `#c:molten_tin` | 225 | 39 | requires tag `c:plates/tin` filled |
| metal/tin/shovel | `#tconstruct:melting/tin/tools_costing_1` | 90 mb `#c:molten_tin` | 225 | 39 | scales with damage; requires tag `tconstruct:melting/tin/tools_costing_1` filled |
| metal/tin/sword | `#tconstruct:melting/tin/tools_costing_2` | 180 mb `#c:molten_tin` | 225 | 55 | scales with damage; requires tag `tconstruct:melting/tin/tools_costing_2` filled |
| metal/tin/tools_complement_excavator | `tools_complement:tin_excavator` | 990 mb `#c:molten_tin` | 225 | 129 | scales with damage; requires item `tools_complement:tin_excavator` |
| metal/tin/tools_complement_hammer | `tools_complement:tin_hammer` | 1170 mb `#c:molten_tin` | 225 | 141 | scales with damage; requires item `tools_complement:tin_hammer` |
| metal/tungsten/block | `#c:storage_blocks/tungsten` | 810 mb `#c:molten_tungsten` | 950 | 194 | requires tag `c:storage_blocks/tungsten` filled |
| metal/tungsten/dust | `#c:dusts/tungsten` | 90 mb `#c:molten_tungsten` | 950 | 49 | requires tag `c:dusts/tungsten` filled |
| metal/tungsten/ingot | `#c:ingots/tungsten` | 90 mb `#c:molten_tungsten` | 950 | 65 | requires tag `c:ingots/tungsten` filled |
| metal/tungsten/nugget | `#c:nuggets/tungsten` | 10 mb `#c:molten_tungsten` | 950 | 22 | requires tag `c:nuggets/tungsten` filled |
| metal/uranium/block | `#c:storage_blocks/uranium` | 810 mb `#c:molten_uranium` | 830 | 183 | requires tag `c:storage_blocks/uranium` filled |
| metal/uranium/coin | `#c:coins/uranium` | 30 mb `#c:molten_uranium` | 830 | 41 | requires tag `c:coins/uranium` filled |
| metal/uranium/dust | `#c:dusts/uranium` | 90 mb `#c:molten_uranium` | 830 | 46 | requires tag `c:dusts/uranium` filled |
| metal/uranium/gear | `#c:gears/uranium` | 360 mb `#c:molten_uranium` | 830 | 122 | requires tag `c:gears/uranium` filled |
| metal/uranium/ingot | `#c:ingots/uranium` | 90 mb `#c:molten_uranium` | 830 | 61 | requires tag `c:ingots/uranium` filled |
| metal/uranium/nugget | `#c:nuggets/uranium` | 10 mb `#c:molten_uranium` | 830 | 20 | requires tag `c:nuggets/uranium` filled |
| metal/uranium/oreberry | `oreberriesreplanted:uranium_oreberry` | 10 mb `#c:molten_uranium` | 830 | 20 | requires item `oreberriesreplanted:uranium_oreberry` |
| metal/uranium/plate | `#c:plates/uranium` | 90 mb `#c:molten_uranium` | 830 | 61 | requires tag `c:plates/uranium` filled |
| metal/uranium/sheetmetal | `#c:sheetmetals/uranium` | 90 mb `#c:molten_uranium` | 830 | 61 | requires tag `c:sheetmetals/uranium` filled |
| metal/zinc/allomancy_flakes | `allomancy:zinc_flakes` | 30 mb `#c:molten_zinc` | 420 | 27 | requires item `allomancy:zinc_flakes` |
| metal/zinc/block | `#c:storage_blocks/zinc` | 810 mb `#c:molten_zinc` | 420 | 141 | requires tag `c:storage_blocks/zinc` filled |
| metal/zinc/dust | `#c:dusts/zinc` | 90 mb `#c:molten_zinc` | 420 | 35 | requires tag `c:dusts/zinc` filled |
| metal/zinc/gear | `#c:gears/zinc` | 360 mb `#c:molten_zinc` | 420 | 94 | requires tag `c:gears/zinc` filled |
| metal/zinc/geore/block | `#c:geore_blocks/zinc` | 360 mb `#c:molten_zinc` | 420 | 94 | requires tag `c:geore_blocks/zinc` filled |
| metal/zinc/geore/shard | `#c:geore_shards/zinc` | 90 mb `#c:molten_zinc` | 420 | 47 | requires tag `c:geore_shards/zinc` filled |
| metal/zinc/ingot | `#c:ingots/zinc` | 90 mb `#c:molten_zinc` | 420 | 47 | requires tag `c:ingots/zinc` filled |
| metal/zinc/nugget | `#c:nuggets/zinc` | 10 mb `#c:molten_zinc` | 420 | 16 | requires tag `c:nuggets/zinc` filled |
| metal/zinc/oreberry | `oreberriesreplanted:zinc_oreberry` | 10 mb `#c:molten_zinc` | 420 | 16 | requires item `oreberriesreplanted:zinc_oreberry` |
| metal/zinc/plate | `#c:plates/zinc` | 90 mb `#c:molten_zinc` | 420 | 47 | requires tag `c:plates/zinc` filled |

## obsidian

| Recipe | Input | Result | Temp (C) | Time | Notes |
|---|---|---|---|---|---|
| obsidian/beacon | `minecraft:beacon` | 3000 mb `tconstruct:molten_obsidian` | 1000 | 383 | byproduct 5000 mb `tconstruct:molten_glass` |
| obsidian/block | `#c:obsidians` | 1000 mb `tconstruct:molten_obsidian` | 1000 | 133 |  |
| obsidian/chest | `minecraft:ender_chest` | 8000 mb `tconstruct:molten_obsidian` | 1000 | 332 | byproduct 250 mb `#c:ender` |
| obsidian/dust | `#c:dusts/obsidian` | 250 mb `tconstruct:molten_obsidian` | 1000 | 66 | requires tag `c:dusts/obsidian` filled |
| obsidian/foundry_controller | `tconstruct:foundry_controller` | 1000 mb `tconstruct:molten_obsidian` | 1000 | 232 | byproduct 1000 mb `tconstruct:scorched_stone` |
| obsidian/foundry_io | `tconstruct:scorched_drain` / `tconstruct:scorched_chute` | 500 mb `tconstruct:molten_obsidian` | 1000 | 166 | byproduct 1000 mb `tconstruct:scorched_stone` |
| obsidian/gauge | `tconstruct:obsidian_gauge` | 250 mb `tconstruct:molten_obsidian` | 1000 | 166 | byproduct 50 mb `tconstruct:molten_glass` |
| obsidian/pane | `tconstruct:obsidian_pane` | 250 mb `tconstruct:molten_obsidian` | 1000 | 99 |  |

## quartz

| Recipe | Input | Result | Temp (C) | Time | Notes |
|---|---|---|---|---|---|
| quartz/block | `#c:storage_blocks/quartz` | 400 mb `tconstruct:molten_quartz` | 637 | 110 |  |
| quartz/daylight_detector | `minecraft:daylight_detector` | 300 mb `tconstruct:molten_quartz` | 637 | 100 | byproduct 3000 mb `tconstruct:molten_glass` |
| quartz/decorative_block | `minecraft:smooth_quartz` / `minecraft:quartz_pillar` / `minecraft:quartz_bricks` / `minecraft:chiseled_quartz_block` / `minecraft:quartz_stairs` / `minecraft:smooth_quartz_stairs` | 400 mb `tconstruct:molten_quartz` | 637 | 110 |  |
| quartz/dust | `#c:dusts/quartz` | 100 mb `tconstruct:molten_quartz` | 637 | 41 | requires tag `c:dusts/quartz` filled |
| quartz/gear | `#c:gears/quartz` | 400 mb `tconstruct:molten_quartz` | 637 | 110 | requires tag `c:gears/quartz` filled |
| quartz/gem | `#c:gems/quartz` | 100 mb `tconstruct:molten_quartz` | 637 | 55 |  |
| quartz/gem_1 | `minecraft:observer` / `minecraft:comparator` / `tconstruct:quartz_shuriken` | 100 mb `tconstruct:molten_quartz` | 637 | 58 |  |
| quartz/geore/block | `#c:geore_blocks/quartz` | 400 mb `tconstruct:molten_quartz` | 637 | 110 | requires tag `c:geore_blocks/quartz` filled |
| quartz/geore/bud_large | `#c:geore_large_buds/quartz` | 300 mb `tconstruct:molten_quartz` | 637 | 110 | ore rate: gem; byproduct 270 mb `#c:molten_iron`; requires tag `c:geore_large_buds/quartz` filled |
| quartz/geore/bud_medium | `#c:geore_medium_buds/quartz` | 200 mb `tconstruct:molten_quartz` | 637 | 82 | ore rate: gem; byproduct 180 mb `#c:molten_iron`; requires tag `c:geore_medium_buds/quartz` filled |
| quartz/geore/bud_small | `#c:geore_small_buds/quartz` | 100 mb `tconstruct:molten_quartz` | 637 | 55 | ore rate: gem; byproduct 90 mb `#c:molten_iron`; requires tag `c:geore_small_buds/quartz` filled |
| quartz/geore/cluster | `#c:geore_clusters/quartz` | 400 mb `tconstruct:molten_quartz` | 637 | 137 | ore rate: gem; byproduct 360 mb `#c:molten_iron`; requires tag `c:geore_clusters/quartz` filled |
| quartz/geore/shard | `#c:geore_shards/quartz` | 100 mb `tconstruct:molten_quartz` | 637 | 55 | requires tag `c:geore_shards/quartz` filled |
| quartz/ore_dense | `{"fabric:type":"fabric:all","ingredients":[{"tag":"c:ores/quartz"},{"tag":"c:ore_rates/dense"}]}` | 300 mb `tconstruct:molten_quartz` | 637 | 246 | ore rate: gem; byproduct 270 mb `#c:molten_iron`; requires tags `c:ores/quartz` + `c:ore_rates/dense` filled |
| quartz/ore_singular | `{"fabric:type":"fabric:difference","base":{"tag":"c:ores/quartz"},"subtracted":{"tag":"tconstruct:non_singular_ore_rates"}}` | 100 mb `tconstruct:molten_quartz` | 637 | 137 | ore rate: gem; byproduct 90 mb `#c:molten_iron`; requires tags `c` + `:` + `o` + `r` + `e` + `s` + `/` + `q` + `u` + `a` + `r` + `t` + `z` filled (ignoring `tconstruct:non_singular_ore_rates`) |
| quartz/ore_sparse | `{"fabric:type":"fabric:all","ingredients":[{"tag":"c:ores/quartz"},{"tag":"c:ore_rates/sparse"}]}` | 50 mb `tconstruct:molten_quartz` | 637 | 82 | ore rate: gem; byproduct 45 mb `#c:molten_iron`; requires tags `c:ores/quartz` + `c:ore_rates/sparse` filled |
| quartz/slab | `minecraft:quartz_slab` / `minecraft:smooth_quartz_slab` | 200 mb `tconstruct:molten_quartz` | 637 | 82 |  |

## scorched

| Recipe | Input | Result | Temp (C) | Time | Notes |
|---|---|---|---|---|---|
| scorched/block | `#tconstruct:scorched_blocks` / `tconstruct:scorched_ladder` / `tconstruct:scorched_lamp` / `tconstruct:scorched_bricks_stairs` / `tconstruct:scorched_road_stairs` | 1000 mb `tconstruct:scorched_stone` | 500 | 100 |  |
| scorched/brick | `tconstruct:scorched_brick` | 250 mb `tconstruct:scorched_stone` | 500 | 50 |  |
| scorched/casting_basin | `tconstruct:scorched_basin` | 1750 mb `tconstruct:scorched_stone` | 500 | 125 | byproduct 100 mb `#c:molten_gold` |
| scorched/casting_table | `tconstruct:scorched_table` | 1750 mb `tconstruct:scorched_stone` | 500 | 125 | byproduct 40 mb `#c:molten_gold` |
| scorched/faucet | `tconstruct:scorched_faucet` / `tconstruct:scorched_channel` | 250 mb `tconstruct:scorched_stone` | 500 | 75 |  |
| scorched/fence | `tconstruct:scorched_bricks_fence` | 750 mb `tconstruct:scorched_stone` | 500 | 50 |  |
| scorched/fluid_cannon | `tconstruct:scorched_fluid_cannon` | 450 mb `#c:molten_cobalt` | 950 | 227 | byproduct 1000 mb `tconstruct:scorched_stone`; byproduct 500 mb `tconstruct:molten_quartz` |
| scorched/fuel_tank | `tconstruct:scorched_fuel_tank` | 2000 mb `tconstruct:scorched_stone` | 500 | 150 | byproduct 100 mb `tconstruct:molten_quartz` |
| scorched/gauge | `{"fabric:type":"tconstruct:no_container","match":[{"item":"tconstruct:scorched_fuel_gauge"},{"item":"tconstruct:scorched_ingot_gauge"}]}` | 1000 mb `tconstruct:scorched_stone` | 500 | 100 | byproduct 500 mb `tconstruct:molten_quartz` |
| scorched/glass | `tconstruct:scorched_glass` | 1000 mb `tconstruct:scorched_stone` | 500 | 100 | byproduct 100 mb `tconstruct:molten_quartz` |
| scorched/glass_soul | `tconstruct:scorched_soul_glass` | 1000 mb `tconstruct:scorched_stone` | 500 | 100 | byproduct 1000 mb `tconstruct:liquid_soul` |
| scorched/glass_tinted | `tconstruct:scorched_tinted_glass` | 1000 mb `tconstruct:scorched_stone` | 500 | 100 | byproduct 1000 mb `tconstruct:molten_glass`; byproduct 200 mb `tconstruct:molten_amethyst` |
| scorched/grout | `tconstruct:nether_grout` | 500 mb `tconstruct:scorched_stone` | 500 | 75 |  |
| scorched/ingot_tank | `tconstruct:scorched_ingot_tank` | 1500 mb `tconstruct:scorched_stone` | 500 | 125 | byproduct 300 mb `tconstruct:molten_quartz` |
| scorched/lantern | `tconstruct:scorched_lantern` | 500 mb `tconstruct:scorched_stone` | 500 | 50 | byproduct 25 mb `tconstruct:molten_quartz`; byproduct 30 mb `#c:molten_iron` |
| scorched/melter | `tconstruct:scorched_alloyer` | 2250 mb `tconstruct:scorched_stone` | 500 | 175 | byproduct 500 mb `tconstruct:molten_quartz` |
| scorched/pane | `tconstruct:scorched_glass_pane` | 250 mb `tconstruct:scorched_stone` | 500 | 50 | byproduct 25 mb `tconstruct:molten_quartz` |
| scorched/pane_soul | `tconstruct:scorched_soul_glass_pane` | 250 mb `tconstruct:scorched_stone` | 500 | 50 | byproduct 250 mb `tconstruct:liquid_soul` |
| scorched/proxy_tank | `tconstruct:scorched_proxy_tank` | 500 mb `tconstruct:molten_obsidian` | 1000 | 166 | byproduct 1000 mb `tconstruct:scorched_stone`; byproduct 300 mb `tconstruct:molten_quartz` |
| scorched/slab | `tconstruct:scorched_bricks_slab` / `tconstruct:scorched_bricks_slab` / `tconstruct:scorched_road_slab` | 500 mb `tconstruct:scorched_stone` | 500 | 75 |  |

## seared

| Recipe | Input | Result | Temp (C) | Time | Notes |
|---|---|---|---|---|---|
| seared/block | `#tconstruct:seared_blocks` / `tconstruct:seared_ladder` / `tconstruct:seared_lamp` / `tconstruct:seared_cobble_wall` / `tconstruct:seared_bricks_wall` / `tconstruct:seared_cobble_stairs` / `tconstruct:seared_stone_stairs` / `tconstruct:seared_bricks_stairs` / `tconstruct:seared_paver_stairs` | 1000 mb `tconstruct:seared_stone` | 600 | 107 |  |
| seared/brick | `tconstruct:seared_brick` | 250 mb `tconstruct:seared_stone` | 600 | 53 |  |
| seared/casting | `tconstruct:seared_basin` / `tconstruct:seared_table` | 1750 mb `tconstruct:seared_stone` | 600 | 134 |  |
| seared/faucet | `tconstruct:seared_faucet` / `tconstruct:seared_channel` | 250 mb `tconstruct:seared_stone` | 600 | 80 |  |
| seared/fluid_cannon | `tconstruct:seared_fluid_cannon` | 450 mb `#c:molten_copper` | 500 | 125 | byproduct 1000 mb `tconstruct:seared_stone`; byproduct 1250 mb `tconstruct:molten_glass` |
| seared/fuel_tank | `tconstruct:seared_fuel_tank` | 2000 mb `tconstruct:seared_stone` | 600 | 160 | byproduct 1000 mb `tconstruct:molten_glass` |
| seared/gauge | `{"fabric:type":"tconstruct:no_container","match":[{"item":"tconstruct:seared_fuel_gauge"},{"item":"tconstruct:seared_ingot_gauge"}]}` | 1000 mb `tconstruct:seared_stone` | 600 | 107 | byproduct 5000 mb `tconstruct:molten_glass` |
| seared/glass | `tconstruct:seared_glass` | 1000 mb `tconstruct:seared_stone` | 600 | 107 | byproduct 1000 mb `tconstruct:molten_glass` |
| seared/glass_soul | `tconstruct:seared_soul_glass` | 1000 mb `tconstruct:seared_stone` | 600 | 107 | byproduct 1000 mb `tconstruct:liquid_soul` |
| seared/glass_tinted | `tconstruct:seared_tinted_glass` | 1000 mb `tconstruct:seared_stone` | 600 | 107 | byproduct 1000 mb `tconstruct:molten_glass`; byproduct 200 mb `tconstruct:molten_amethyst` |
| seared/grout | `tconstruct:grout` | 500 mb `tconstruct:seared_stone` | 600 | 80 |  |
| seared/heater | `tconstruct:seared_heater` | 2000 mb `tconstruct:seared_stone` | 600 | 160 |  |
| seared/ingot_tank | `tconstruct:seared_ingot_tank` | 1500 mb `tconstruct:seared_stone` | 600 | 134 | byproduct 3000 mb `tconstruct:molten_glass` |
| seared/lantern | `tconstruct:seared_lantern` | 500 mb `tconstruct:seared_stone` | 600 | 53 | byproduct 250 mb `tconstruct:molten_glass`; byproduct 30 mb `#c:molten_iron` |
| seared/melter | `tconstruct:seared_melter` | 2250 mb `tconstruct:seared_stone` | 600 | 187 | byproduct 1250 mb `tconstruct:molten_glass` |
| seared/pane | `tconstruct:seared_glass_pane` | 250 mb `tconstruct:seared_stone` | 600 | 53 | byproduct 250 mb `tconstruct:molten_glass` |
| seared/pane_soul | `tconstruct:seared_soul_glass_pane` | 250 mb `tconstruct:seared_stone` | 600 | 53 | byproduct 250 mb `tconstruct:liquid_soul` |
| seared/seared_casting_tank | `tconstruct:seared_casting_tank` | 180 mb `#c:molten_copper` | 500 | 125 | byproduct 1000 mb `tconstruct:seared_stone`; byproduct 3000 mb `tconstruct:molten_glass` |
| seared/slab | `tconstruct:seared_cobble_slab` / `tconstruct:seared_stone_slab` / `tconstruct:seared_bricks_slab` / `tconstruct:seared_paver_slab` | 500 mb `tconstruct:seared_stone` | 600 | 80 |  |

## slime

| Recipe | Input | Result | Temp (C) | Time | Notes |
|---|---|---|---|---|---|
| slime/beetroot_soup | `minecraft:beetroot` | 50 mb `#c:beetroot_soup` | 100 | 33 |  |
| slime/earth/ball | `#c:slimeball/earth` | 250 mb `#c:slime` | 50 | 31 |  |
| slime/earth/block | `minecraft:slime_block` | 2250 mb `#c:slime` | 50 | 92 |  |
| slime/earth/bud_cluster | `tconstruct:earth_slime_crystal_cluster` | 1000 mb `#c:slime` | 50 | 77 | ore rate: gem |
| slime/earth/bud_large | `tconstruct:large_earth_slime_crystal_bud` | 750 mb `#c:slime` | 50 | 62 | ore rate: gem |
| slime/earth/bud_medium | `tconstruct:medium_earth_slime_crystal_bud` | 500 mb `#c:slime` | 50 | 46 | ore rate: gem |
| slime/earth/bud_small | `tconstruct:small_earth_slime_crystal_bud` | 250 mb `#c:slime` | 50 | 31 | ore rate: gem |
| slime/earth/congealed | `tconstruct:earth_congealed_slime` | 1000 mb `#c:slime` | 50 | 62 |  |
| slime/earth/crystal | `tconstruct:earth_slime_crystal` | 250 mb `#c:slime` | 50 | 31 |  |
| slime/earth/crystal_block | `tconstruct:earth_slime_crystal_block` | 1000 mb `#c:slime` | 50 | 62 |  |
| slime/earth/sapling | `tconstruct:earth_slime_sapling` | 250 mb `#c:slime` | 50 | 51 |  |
| slime/ender/ball | `#c:slimeball/ender` | 250 mb `tconstruct:ender_slime` | 70 | 32 |  |
| slime/ender/block | `tconstruct:ender_slime` | 2250 mb `tconstruct:ender_slime` | 70 | 95 |  |
| slime/ender/bud_cluster | `tconstruct:ender_slime_crystal_cluster` | 1000 mb `tconstruct:ender_slime` | 70 | 79 | ore rate: gem |
| slime/ender/bud_large | `tconstruct:large_ender_slime_crystal_bud` | 750 mb `tconstruct:ender_slime` | 70 | 64 | ore rate: gem |
| slime/ender/bud_medium | `tconstruct:medium_ender_slime_crystal_bud` | 500 mb `tconstruct:ender_slime` | 70 | 48 | ore rate: gem |
| slime/ender/bud_small | `tconstruct:small_ender_slime_crystal_bud` | 250 mb `tconstruct:ender_slime` | 70 | 32 | ore rate: gem |
| slime/ender/congealed | `tconstruct:ender_congealed_slime` | 1000 mb `tconstruct:ender_slime` | 70 | 64 |  |
| slime/ender/crystal | `tconstruct:ender_slime_crystal` | 250 mb `tconstruct:ender_slime` | 70 | 32 |  |
| slime/ender/crystal_block | `tconstruct:ender_slime_crystal_block` | 1000 mb `tconstruct:ender_slime` | 70 | 64 |  |
| slime/ender/sapling | `tconstruct:ender_slime_sapling` | 250 mb `tconstruct:ender_slime` | 70 | 53 |  |
| slime/honey_block | `minecraft:honey_block` | 1000 mb `#c:honey` | 1 | 94 |  |
| slime/ichor/ball | `#c:slimeball/ichor` | 10 mb `tconstruct:blazing_blood` | 1500 | 80 | byproduct 200 mb `tconstruct:ichor` |
| slime/ichor/block | `tconstruct:ichor_slime` | 90 mb `tconstruct:blazing_blood` | 1500 | 241 | byproduct 1800 mb `tconstruct:ichor` |
| slime/ichor/bud_cluster | `tconstruct:ichor_slime_crystal_cluster` | 40 mb `tconstruct:blazing_blood` | 1500 | 201 | ore rate: gem; byproduct 800 mb `tconstruct:ichor` |
| slime/ichor/bud_large | `tconstruct:large_ichor_slime_crystal_bud` | 30 mb `tconstruct:blazing_blood` | 1500 | 160 | ore rate: gem; byproduct 600 mb `tconstruct:ichor` |
| slime/ichor/bud_medium | `tconstruct:medium_ichor_slime_crystal_bud` | 20 mb `tconstruct:blazing_blood` | 1500 | 120 | ore rate: gem; byproduct 400 mb `tconstruct:ichor` |
| slime/ichor/bud_small | `tconstruct:small_ichor_slime_crystal_bud` | 10 mb `tconstruct:blazing_blood` | 1500 | 80 | ore rate: gem; byproduct 200 mb `tconstruct:ichor` |
| slime/ichor/congealed | `tconstruct:ichor_congealed_slime` | 40 mb `tconstruct:blazing_blood` | 1500 | 160 | byproduct 800 mb `tconstruct:ichor` |
| slime/ichor/crystal | `tconstruct:ichor_slime_crystal` | 10 mb `tconstruct:blazing_blood` | 1500 | 80 | byproduct 200 mb `tconstruct:ichor` |
| slime/ichor/crystal_block | `tconstruct:ichor_slime_crystal_block` | 40 mb `tconstruct:blazing_blood` | 1500 | 160 | byproduct 800 mb `tconstruct:ichor` |
| slime/magma/ball | `minecraft:magma_cream` | 250 mb `#c:magma` | 300 | 42 |  |
| slime/magma/block | `minecraft:magma_block` | 1000 mb `#c:magma` | 300 | 127 |  |
| slime/mushroom_stew | `#c:mushrooms` | 125 mb `#c:mushroom_stew` | 100 | 33 |  |
| slime/sky/ball | `#c:slimeball/sky` | 250 mb `tconstruct:sky_slime` | 10 | 29 |  |
| slime/sky/block | `tconstruct:sky_slime` | 2250 mb `tconstruct:sky_slime` | 10 | 86 |  |
| slime/sky/bud_cluster | `tconstruct:sky_slime_crystal_cluster` | 1000 mb `tconstruct:sky_slime` | 10 | 72 | ore rate: gem |
| slime/sky/bud_large | `tconstruct:large_sky_slime_crystal_bud` | 750 mb `tconstruct:sky_slime` | 10 | 57 | ore rate: gem |
| slime/sky/bud_medium | `tconstruct:medium_sky_slime_crystal_bud` | 500 mb `tconstruct:sky_slime` | 10 | 43 | ore rate: gem |
| slime/sky/bud_small | `tconstruct:small_sky_slime_crystal_bud` | 250 mb `tconstruct:sky_slime` | 10 | 29 | ore rate: gem |
| slime/sky/congealed | `tconstruct:sky_congealed_slime` | 1000 mb `tconstruct:sky_slime` | 10 | 57 |  |
| slime/sky/crystal | `tconstruct:sky_slime_crystal` | 250 mb `tconstruct:sky_slime` | 10 | 29 |  |
| slime/sky/crystal_block | `tconstruct:sky_slime_crystal_block` | 1000 mb `tconstruct:sky_slime` | 10 | 57 |  |
| slime/sky/sapling | `tconstruct:sky_slime_sapling` | 250 mb `tconstruct:sky_slime` | 10 | 48 |  |

## soul

| Recipe | Input | Result | Temp (C) | Time | Notes |
|---|---|---|---|---|---|
| soul/glass | `tconstruct:soul_glass` | 1000 mb `tconstruct:liquid_soul` | 400 | 46 |  |
| soul/pane | `tconstruct:soul_glass_pane` | 250 mb `tconstruct:liquid_soul` | 400 | 23 |  |
| soul/sand | `minecraft:soul_sand` / `minecraft:soul_soil` | 1000 mb `tconstruct:liquid_soul` | 400 | 69 |  |

## venom

| Recipe | Input | Result | Temp (C) | Time | Notes |
|---|---|---|---|---|---|
| venom/eye | `minecraft:spider_eye` | 250 mb `tconstruct:venom` | 10 | 29 |  |
| venom/fermented_eye | `minecraft:fermented_spider_eye` | 500 mb `tconstruct:venom` | 10 | 29 |  |

## water

| Recipe | Input | Result | Temp (C) | Time | Notes |
|---|---|---|---|---|---|
| water/blue_ice | `minecraft:blue_ice` | 81000 mb `minecraft:water` | 0 | 253 |  |
| water/ice | `minecraft:ice` | 1000 mb `minecraft:water` | 0 | 28 |  |
| water/packed_ice | `minecraft:packed_ice` | 9000 mb `minecraft:water` | 0 | 84 |  |
| water/snow_block | `minecraft:snow_block` | 500 mb `minecraft:water` | 0 | 21 |  |
| water/snow_layer | `minecraft:snow` | 125 mb `minecraft:water` | 0 | 14 |  |
| water/snowball | `minecraft:snowball` | 125 mb `minecraft:water` | 0 | 14 |  |

