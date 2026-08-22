# Other recipes

Everything not covered by a dedicated page, grouped by recipe serializer: vanilla
crafting for blocks and gadgets, part building, cast creation via molding, tables,
compat and more. Listed so the reference provably covers every shipped recipe.

## `forge:conditional` (63)

| Recipe | Result | Condition |
|---|---|---|
| smeltery/melting/metal/bendalloy/raw |  | tag `c:raw_materials/bendalloy` filled |
| smeltery/melting/metal/bendalloy/raw_block |  | tag `c:storage_blocks/raw_bendalloy` filled |
| smeltery/melting/metal/brass/raw |  | tag `c:raw_materials/brass` filled |
| smeltery/melting/metal/brass/raw_block |  | tag `c:storage_blocks/raw_brass` filled |
| smeltery/melting/metal/cadmium/ore_dense |  | tags `c:ores/cadmium` + `c:ore_rates/dense` filled |
| smeltery/melting/metal/cadmium/ore_singular |  | tags `c` + `:` + `o` + `r` + `e` + `s` + `/` + `c` + `a` + `d` + `m` + `i` + `u` + `m` filled (ignoring `tconstruct:non_singular_ore_rates`) |
| smeltery/melting/metal/cadmium/ore_sparse |  | tags `c:ores/cadmium` + `c:ore_rates/sparse` filled |
| smeltery/melting/metal/cadmium/raw |  | tag `c:raw_materials/cadmium` filled |
| smeltery/melting/metal/cadmium/raw_block |  | tag `c:storage_blocks/raw_cadmium` filled |
| smeltery/melting/metal/chromium/ore_dense |  | tags `c:ores/chromium` + `c:ore_rates/dense` filled |
| smeltery/melting/metal/chromium/ore_singular |  | tags `c` + `:` + `o` + `r` + `e` + `s` + `/` + `c` + `h` + `r` + `o` + `m` + `i` + `u` + `m` filled (ignoring `tconstruct:non_singular_ore_rates`) |
| smeltery/melting/metal/chromium/ore_sparse |  | tags `c:ores/chromium` + `c:ore_rates/sparse` filled |
| smeltery/melting/metal/chromium/raw |  | tag `c:raw_materials/chromium` filled |
| smeltery/melting/metal/chromium/raw_block |  | tag `c:storage_blocks/raw_chromium` filled |
| smeltery/melting/metal/duralumin/raw |  | tag `c:raw_materials/duralumin` filled |
| smeltery/melting/metal/duralumin/raw_block |  | tag `c:storage_blocks/raw_duralumin` filled |
| smeltery/melting/metal/electrum/raw |  | tag `c:raw_materials/electrum` filled |
| smeltery/melting/metal/electrum/raw_block |  | tag `c:storage_blocks/raw_electrum` filled |
| smeltery/melting/metal/lead/ore_dense |  | tags `c:ores/lead` + `c:ore_rates/dense` filled |
| smeltery/melting/metal/lead/ore_singular |  | tags `c` + `:` + `o` + `r` + `e` + `s` + `/` + `l` + `e` + `a` + `d` filled (ignoring `tconstruct:non_singular_ore_rates`) |
| smeltery/melting/metal/lead/ore_sparse |  | tags `c:ores/lead` + `c:ore_rates/sparse` filled |
| smeltery/melting/metal/lead/raw |  | tag `c:raw_materials/lead` filled |
| smeltery/melting/metal/lead/raw_block |  | tag `c:storage_blocks/raw_lead` filled |
| smeltery/melting/metal/nickel/ore_dense |  | tags `c:ores/nickel` + `c:ore_rates/dense` filled |
| smeltery/melting/metal/nickel/ore_singular |  | tags `c` + `:` + `o` + `r` + `e` + `s` + `/` + `n` + `i` + `c` + `k` + `e` + `l` filled (ignoring `tconstruct:non_singular_ore_rates`) |
| smeltery/melting/metal/nickel/ore_sparse |  | tags `c:ores/nickel` + `c:ore_rates/sparse` filled |
| smeltery/melting/metal/nickel/raw |  | tag `c:raw_materials/nickel` filled |
| smeltery/melting/metal/nickel/raw_block |  | tag `c:storage_blocks/raw_nickel` filled |
| smeltery/melting/metal/nicrosil/raw |  | tag `c:raw_materials/nicrosil` filled |
| smeltery/melting/metal/nicrosil/raw_block |  | tag `c:storage_blocks/raw_nicrosil` filled |
| smeltery/melting/metal/pewter/raw |  | tag `c:raw_materials/pewter` filled |
| smeltery/melting/metal/pewter/raw_block |  | tag `c:storage_blocks/raw_pewter` filled |
| smeltery/melting/metal/silver/ore_dense |  | tags `c:ores/silver` + `c:ore_rates/dense` filled |
| smeltery/melting/metal/silver/ore_singular |  | tags `c` + `:` + `o` + `r` + `e` + `s` + `/` + `s` + `i` + `l` + `v` + `e` + `r` filled (ignoring `tconstruct:non_singular_ore_rates`) |
| smeltery/melting/metal/silver/ore_sparse |  | tags `c:ores/silver` + `c:ore_rates/sparse` filled |
| smeltery/melting/metal/silver/raw |  | tag `c:raw_materials/silver` filled |
| smeltery/melting/metal/silver/raw_block |  | tag `c:storage_blocks/raw_silver` filled |
| smeltery/melting/metal/tin/ore_dense |  | tags `c:ores/tin` + `c:ore_rates/dense` filled |
| smeltery/melting/metal/tin/ore_singular |  | tags `c` + `:` + `o` + `r` + `e` + `s` + `/` + `t` + `i` + `n` filled (ignoring `tconstruct:non_singular_ore_rates`) |
| smeltery/melting/metal/tin/ore_sparse |  | tags `c:ores/tin` + `c:ore_rates/sparse` filled |
| smeltery/melting/metal/tin/raw |  | tag `c:raw_materials/tin` filled |
| smeltery/melting/metal/tin/raw_block |  | tag `c:storage_blocks/raw_tin` filled |
| smeltery/melting/metal/tungsten/ore_dense |  | tags `c:ores/tungsten` + `c:ore_rates/dense` filled |
| smeltery/melting/metal/tungsten/ore_singular |  | tags `c` + `:` + `o` + `r` + `e` + `s` + `/` + `t` + `u` + `n` + `g` + `s` + `t` + `e` + `n` filled (ignoring `tconstruct:non_singular_ore_rates`) |
| smeltery/melting/metal/tungsten/ore_sparse |  | tags `c:ores/tungsten` + `c:ore_rates/sparse` filled |
| smeltery/melting/metal/tungsten/raw |  | tag `c:raw_materials/tungsten` filled |
| smeltery/melting/metal/tungsten/raw_block |  | tag `c:storage_blocks/raw_tungsten` filled |
| smeltery/melting/metal/uranium/ore_dense |  | tags `c:ores/uranium` + `c:ore_rates/dense` filled |
| smeltery/melting/metal/uranium/ore_singular |  | tags `c` + `:` + `o` + `r` + `e` + `s` + `/` + `u` + `r` + `a` + `n` + `i` + `u` + `m` filled (ignoring `tconstruct:non_singular_ore_rates`) |
| smeltery/melting/metal/uranium/ore_sparse |  | tags `c:ores/uranium` + `c:ore_rates/sparse` filled |
| smeltery/melting/metal/uranium/raw |  | tag `c:raw_materials/uranium` filled |
| smeltery/melting/metal/uranium/raw_block |  | tag `c:storage_blocks/raw_uranium` filled |
| smeltery/melting/metal/zinc/geore/bud_large |  | tag `c:geore_large_buds/zinc` filled |
| smeltery/melting/metal/zinc/geore/bud_medium |  | tag `c:geore_medium_buds/zinc` filled |
| smeltery/melting/metal/zinc/geore/bud_small |  | tag `c:geore_small_buds/zinc` filled |
| smeltery/melting/metal/zinc/geore/cluster |  | tag `c:geore_clusters/zinc` filled |
| smeltery/melting/metal/zinc/ore_dense |  | tags `c:ores/zinc` + `c:ore_rates/dense` filled |
| smeltery/melting/metal/zinc/ore_singular |  | tags `c` + `:` + `o` + `r` + `e` + `s` + `/` + `z` + `i` + `n` + `c` filled (ignoring `tconstruct:non_singular_ore_rates`) |
| smeltery/melting/metal/zinc/ore_sparse |  | tags `c:ores/zinc` + `c:ore_rates/sparse` filled |
| smeltery/melting/metal/zinc/raw |  | tag `c:raw_materials/zinc` filled |
| smeltery/melting/metal/zinc/raw_block |  | tag `c:storage_blocks/raw_zinc` filled |
| smeltery/scorched/scorched_brick_kiln |  |  |
| smeltery/seared/seared_brick_kiln |  |  |

## `mantle:crafting_shaped_retextured` (15)

| Recipe | Result | Condition |
|---|---|---|
| smeltery/scorched/chute_retextured | `tconstruct:scorched_chute` |  |
| smeltery/scorched/drain_retextured | `tconstruct:scorched_drain` |  |
| smeltery/scorched/duct_retextured | `tconstruct:scorched_duct` |  |
| smeltery/seared/chute_retextured | `tconstruct:seared_chute` |  |
| smeltery/seared/drain_retextured | `tconstruct:seared_drain` |  |
| smeltery/seared/duct_retextured | `tconstruct:seared_duct` |  |
| tables/crafting_station_from_logs | `tconstruct:crafting_station` |  |
| tables/crafting_station_from_tables | `tconstruct:crafting_station` |  |
| tables/modifier_worktable | `tconstruct:modifier_worktable` |  |
| tables/part_builder | `tconstruct:part_builder` |  |
| tables/scorched_anvil | `tconstruct:scorched_anvil` |  |
| tables/scorched_forge | `tconstruct:scorched_anvil` |  |
| tables/tinker_station | `tconstruct:tinker_station` |  |
| tables/tinkers_anvil | `tconstruct:tinkers_anvil` |  |
| tables/tinkers_forge | `tconstruct:tinkers_anvil` |  |

## `minecraft:blasting` (12)

| Recipe | Result | Condition |
|---|---|---|
| common/materials/cobalt_ingot_blasting | `tconstruct:cobalt_ingot` |  |
| common/materials/cobalt_nugget_blasting | `tconstruct:cobalt_nugget` |  |
| common/materials/knightmetal_nugget_blasting | `tconstruct:knightmetal_nugget` |  |
| common/materials/steel_nugget_blasting | `tconstruct:steel_nugget` |  |
| common/slime/earth/crystal_growing | `tconstruct:earth_slime_crystal` |  |
| common/slime/earth/crystal_smelting | `minecraft:slime_ball` |  |
| common/slime/ender/crystal_growing | `tconstruct:ender_slime_crystal` |  |
| common/slime/ender/crystal_smelting | `tconstruct:ender_slime_ball` |  |
| common/slime/ichor/crystal_growing | `tconstruct:ichor_slime_crystal` |  |
| common/slime/ichor/crystal_smelting | `tconstruct:ichor_slime_ball` |  |
| common/slime/sky/crystal_growing | `tconstruct:sky_slime_crystal` |  |
| common/slime/sky/crystal_smelting | `tconstruct:sky_slime_ball` |  |

## `minecraft:crafting_shaped` (243)

| Recipe | Result | Condition |
|---|---|---|
| common/basalt_blast_furnace | `minecraft:blast_furnace` |  |
| common/cheese_block_from_ingot | `tconstruct:cheese_block` |  |
| common/cobalt_platform | 4 `tconstruct:cobalt_platform` |  |
| common/copper_platform | 4 `tconstruct:copper_platform` |  |
| common/firewood/blazewood_fence | 6 `tconstruct:blazewood_fence` |  |
| common/firewood/blazewood_slab | 6 `tconstruct:blazewood_slab` |  |
| common/firewood/blazewood_stairs | 4 `tconstruct:blazewood_stairs` |  |
| common/firewood/nahuatl_fence | 6 `tconstruct:nahuatl_fence` |  |
| common/firewood/nahuatl_slab | 6 `tconstruct:nahuatl_slab` |  |
| common/firewood/nahuatl_stairs | 4 `tconstruct:nahuatl_stairs` |  |
| common/glass/black_clear_stained_glass | 8 `tconstruct:black_clear_stained_glass` |  |
| common/glass/black_clear_stained_glass_pane | 16 `tconstruct:black_clear_stained_glass_pane` |  |
| common/glass/black_clear_stained_glass_pane_from_panes | 8 `tconstruct:black_clear_stained_glass_pane` |  |
| common/glass/blue_clear_stained_glass | 8 `tconstruct:blue_clear_stained_glass` |  |
| common/glass/blue_clear_stained_glass_pane | 16 `tconstruct:blue_clear_stained_glass_pane` |  |
| common/glass/blue_clear_stained_glass_pane_from_panes | 8 `tconstruct:blue_clear_stained_glass_pane` |  |
| common/glass/brown_clear_stained_glass | 8 `tconstruct:brown_clear_stained_glass` |  |
| common/glass/brown_clear_stained_glass_pane | 16 `tconstruct:brown_clear_stained_glass_pane` |  |
| common/glass/brown_clear_stained_glass_pane_from_panes | 8 `tconstruct:brown_clear_stained_glass_pane` |  |
| common/glass/clear_glass_pane | 16 `tconstruct:clear_glass_pane` |  |
| common/glass/cyan_clear_stained_glass | 8 `tconstruct:cyan_clear_stained_glass` |  |
| common/glass/cyan_clear_stained_glass_pane | 16 `tconstruct:cyan_clear_stained_glass_pane` |  |
| common/glass/cyan_clear_stained_glass_pane_from_panes | 8 `tconstruct:cyan_clear_stained_glass_pane` |  |
| common/glass/gray_clear_stained_glass | 8 `tconstruct:gray_clear_stained_glass` |  |
| common/glass/gray_clear_stained_glass_pane | 16 `tconstruct:gray_clear_stained_glass_pane` |  |
| common/glass/gray_clear_stained_glass_pane_from_panes | 8 `tconstruct:gray_clear_stained_glass_pane` |  |
| common/glass/green_clear_stained_glass | 8 `tconstruct:green_clear_stained_glass` |  |
| common/glass/green_clear_stained_glass_pane | 16 `tconstruct:green_clear_stained_glass_pane` |  |
| common/glass/green_clear_stained_glass_pane_from_panes | 8 `tconstruct:green_clear_stained_glass_pane` |  |
| common/glass/light_blue_clear_stained_glass | 8 `tconstruct:light_blue_clear_stained_glass` |  |
| common/glass/light_blue_clear_stained_glass_pane | 16 `tconstruct:light_blue_clear_stained_glass_pane` |  |
| common/glass/light_blue_clear_stained_glass_pane_from_panes | 8 `tconstruct:light_blue_clear_stained_glass_pane` |  |
| common/glass/light_gray_clear_stained_glass | 8 `tconstruct:light_gray_clear_stained_glass` |  |
| common/glass/light_gray_clear_stained_glass_pane | 16 `tconstruct:light_gray_clear_stained_glass_pane` |  |
| common/glass/light_gray_clear_stained_glass_pane_from_panes | 8 `tconstruct:light_gray_clear_stained_glass_pane` |  |
| common/glass/lime_clear_stained_glass | 8 `tconstruct:lime_clear_stained_glass` |  |
| common/glass/lime_clear_stained_glass_pane | 16 `tconstruct:lime_clear_stained_glass_pane` |  |
| common/glass/lime_clear_stained_glass_pane_from_panes | 8 `tconstruct:lime_clear_stained_glass_pane` |  |
| common/glass/magenta_clear_stained_glass | 8 `tconstruct:magenta_clear_stained_glass` |  |
| common/glass/magenta_clear_stained_glass_pane | 16 `tconstruct:magenta_clear_stained_glass_pane` |  |
| common/glass/magenta_clear_stained_glass_pane_from_panes | 8 `tconstruct:magenta_clear_stained_glass_pane` |  |
| common/glass/orange_clear_stained_glass | 8 `tconstruct:orange_clear_stained_glass` |  |
| common/glass/orange_clear_stained_glass_pane | 16 `tconstruct:orange_clear_stained_glass_pane` |  |
| common/glass/orange_clear_stained_glass_pane_from_panes | 8 `tconstruct:orange_clear_stained_glass_pane` |  |
| common/glass/pink_clear_stained_glass | 8 `tconstruct:pink_clear_stained_glass` |  |
| common/glass/pink_clear_stained_glass_pane | 16 `tconstruct:pink_clear_stained_glass_pane` |  |
| common/glass/pink_clear_stained_glass_pane_from_panes | 8 `tconstruct:pink_clear_stained_glass_pane` |  |
| common/glass/purple_clear_stained_glass | 8 `tconstruct:purple_clear_stained_glass` |  |
| common/glass/purple_clear_stained_glass_pane | 16 `tconstruct:purple_clear_stained_glass_pane` |  |
| common/glass/purple_clear_stained_glass_pane_from_panes | 8 `tconstruct:purple_clear_stained_glass_pane` |  |
| common/glass/red_clear_stained_glass | 8 `tconstruct:red_clear_stained_glass` |  |
| common/glass/red_clear_stained_glass_pane | 16 `tconstruct:red_clear_stained_glass_pane` |  |
| common/glass/red_clear_stained_glass_pane_from_panes | 8 `tconstruct:red_clear_stained_glass_pane` |  |
| common/glass/vanilla/beacon | `minecraft:beacon` | config `glass_recipe_fix` |
| common/glass/vanilla/daylight_detector | `minecraft:daylight_detector` | config `glass_recipe_fix` |
| common/glass/vanilla/end_crystal | `minecraft:end_crystal` | config `glass_recipe_fix` |
| common/glass/vanilla/glass_bottle | 3 `minecraft:glass_bottle` | config `glass_recipe_fix` |
| common/glass/white_clear_stained_glass | 8 `tconstruct:white_clear_stained_glass` |  |
| common/glass/white_clear_stained_glass_pane | 16 `tconstruct:white_clear_stained_glass_pane` |  |
| common/glass/white_clear_stained_glass_pane_from_panes | 8 `tconstruct:white_clear_stained_glass_pane` |  |
| common/glass/yellow_clear_stained_glass | 8 `tconstruct:yellow_clear_stained_glass` |  |
| common/glass/yellow_clear_stained_glass_pane | 16 `tconstruct:yellow_clear_stained_glass_pane` |  |
| common/glass/yellow_clear_stained_glass_pane_from_panes | 8 `tconstruct:yellow_clear_stained_glass_pane` |  |
| common/gold_bars | 16 `tconstruct:gold_bars` |  |
| common/gold_platform | 4 `tconstruct:gold_platform` |  |
| common/iron_platform | 4 `tconstruct:iron_platform` |  |
| common/materials/amethyst_bronze_block_from_ingots | `tconstruct:amethyst_bronze_block` |  |
| common/materials/amethyst_bronze_ingot_from_nuggets | `tconstruct:amethyst_bronze_ingot` |  |
| common/materials/cinderslime_block_from_ingots | `tconstruct:cinderslime_block` |  |
| common/materials/cinderslime_ingot_from_nuggets | `tconstruct:cinderslime_ingot` |  |
| common/materials/cobalt_block_from_ingots | `tconstruct:cobalt_block` |  |
| common/materials/cobalt_ingot_from_nuggets | `tconstruct:cobalt_ingot` |  |
| common/materials/copper_ingot_from_nuggets | `minecraft:copper_ingot` |  |
| common/materials/hepatizon_block_from_ingots | `tconstruct:hepatizon_block` |  |
| common/materials/hepatizon_ingot_from_nuggets | `tconstruct:hepatizon_ingot` |  |
| common/materials/knightmetal_block_from_ingots | `tconstruct:knightmetal_block` |  |
| common/materials/knightmetal_ingot_from_nuggets | `tconstruct:knightmetal_ingot` |  |
| common/materials/knightslime_block_from_ingots | `tconstruct:knightslime_block` |  |
| common/materials/knightslime_ingot_from_nuggets | `tconstruct:knightslime_ingot` |  |
| common/materials/manyullyn_block_from_ingots | `tconstruct:manyullyn_block` |  |
| common/materials/manyullyn_ingot_from_nuggets | `tconstruct:manyullyn_ingot` |  |
| common/materials/netherite_ingot_from_nuggets | `minecraft:netherite_ingot` |  |
| common/materials/netherite_scrap_from_nuggets | `minecraft:netherite_scrap` |  |
| common/materials/pig_iron_block_from_ingots | `tconstruct:pig_iron_block` |  |
| common/materials/pig_iron_ingot_from_nuggets | `tconstruct:pig_iron_ingot` |  |
| common/materials/queens_slime_block_from_ingots | `tconstruct:queens_slime_block` |  |
| common/materials/queens_slime_ingot_from_nuggets | `tconstruct:queens_slime_ingot` |  |
| common/materials/raw_cobalt_block_from_raws | `tconstruct:raw_cobalt_block` |  |
| common/materials/rose_gold_block_from_ingots | `tconstruct:rose_gold_block` |  |
| common/materials/rose_gold_ingot_from_nuggets | `tconstruct:rose_gold_ingot` |  |
| common/materials/slimesteel_block_from_ingots | `tconstruct:slimesteel_block` |  |
| common/materials/slimesteel_ingot_from_nuggets | `tconstruct:slimesteel_ingot` |  |
| common/materials/steel_block_from_ingots | `tconstruct:steel_block` |  |
| common/materials/steel_ingot_from_nuggets | `tconstruct:steel_ingot` |  |
| common/slime/earth/congealed | `tconstruct:earth_congealed_slime` |  |
| common/slime/earth/crystal_block | `tconstruct:earth_slime_crystal_block` |  |
| common/slime/ender/congealed | `tconstruct:ender_congealed_slime` |  |
| common/slime/ender/crystal_block | `tconstruct:ender_slime_crystal_block` |  |
| common/slime/ender/slimeblock | `tconstruct:ender_slime` |  |
| common/slime/ichor/congealed | `tconstruct:ichor_congealed_slime` |  |
| common/slime/ichor/crystal_block | `tconstruct:ichor_slime_crystal_block` |  |
| common/slime/ichor/slimeblock | `tconstruct:ichor_slime` |  |
| common/slime/lead | 2 `minecraft:lead` | config `slime_recipe_fix` |
| common/slime/sky/congealed | `tconstruct:sky_congealed_slime` |  |
| common/slime/sky/crystal_block | `tconstruct:sky_slime_crystal_block` |  |
| common/slime/sky/slimeblock | `tconstruct:sky_slime` |  |
| common/slime/sticky_piston | `minecraft:sticky_piston` | config `slime_recipe_fix` |
| gadgets/cake/blood | `tconstruct:blood_cake` |  |
| gadgets/cake/earth | `tconstruct:earth_cake` |  |
| gadgets/cake/ender | `tconstruct:ender_cake` |  |
| gadgets/cake/ichor | `tconstruct:ichor_cake` |  |
| gadgets/cake/magma | `tconstruct:magma_cake` |  |
| gadgets/cake/sky | `tconstruct:sky_cake` |  |
| gadgets/fancy_frame/clear | `tconstruct:clear_item_frame` |  |
| gadgets/frame/diamond | `tconstruct:diamond_item_frame` |  |
| gadgets/frame/gold | `tconstruct:gold_item_frame` |  |
| gadgets/frame/manyullyn | `tconstruct:manyullyn_item_frame` |  |
| gadgets/frame/netherite | `tconstruct:netherite_item_frame` |  |
| gadgets/punji | `tconstruct:punji` |  |
| smeltery/copper_can | 3 `tconstruct:copper_can` |  |
| smeltery/end_fluid_cannon | `tconstruct:end_fluid_cannon` |  |
| smeltery/scorched/alloyer | `tconstruct:scorched_alloyer` |  |
| smeltery/scorched/basin | `tconstruct:scorched_basin` |  |
| smeltery/scorched/channel | 5 `tconstruct:scorched_channel` |  |
| smeltery/scorched/chiseled_scorched_bricks_crafting | `tconstruct:chiseled_scorched_bricks` |  |
| smeltery/scorched/chute | `tconstruct:scorched_chute` |  |
| smeltery/scorched/drain | `tconstruct:scorched_drain` |  |
| smeltery/scorched/duct | `tconstruct:scorched_duct` |  |
| smeltery/scorched/faucet | 3 `tconstruct:scorched_faucet` |  |
| smeltery/scorched/fluid_cannon | `tconstruct:scorched_fluid_cannon` |  |
| smeltery/scorched/fuel_gauge | `tconstruct:scorched_fuel_gauge` |  |
| smeltery/scorched/fuel_tank | `tconstruct:scorched_fuel_tank` |  |
| smeltery/scorched/gauge | 4 `tconstruct:obsidian_gauge` |  |
| smeltery/scorched/ingot_gauge | `tconstruct:scorched_ingot_gauge` |  |
| smeltery/scorched/ingot_tank | `tconstruct:scorched_ingot_tank` |  |
| smeltery/scorched/lantern | 3 `tconstruct:scorched_lantern` |  |
| smeltery/scorched/polished_scorched_stone_crafting | 4 `tconstruct:polished_scorched_stone` |  |
| smeltery/scorched/proxy_tank | `tconstruct:scorched_proxy_tank` |  |
| smeltery/scorched/scorched_bricks_crafting | 4 `tconstruct:scorched_bricks` |  |
| smeltery/scorched/scorched_bricks_fence | 6 `tconstruct:scorched_bricks_fence` |  |
| smeltery/scorched/scorched_bricks_from_brick | `tconstruct:scorched_bricks` |  |
| smeltery/scorched/scorched_bricks_slab | 6 `tconstruct:scorched_bricks_slab` |  |
| smeltery/scorched/scorched_bricks_stairs | 4 `tconstruct:scorched_bricks_stairs` |  |
| smeltery/scorched/scorched_glass | `tconstruct:scorched_glass` |  |
| smeltery/scorched/scorched_glass_pane | 16 `tconstruct:scorched_glass_pane` |  |
| smeltery/scorched/scorched_ladder | 4 `tconstruct:scorched_ladder` |  |
| smeltery/scorched/scorched_lamp | `tconstruct:scorched_lamp` |  |
| smeltery/scorched/scorched_road_slab | 6 `tconstruct:scorched_road_slab` |  |
| smeltery/scorched/scorched_road_stairs | 4 `tconstruct:scorched_road_stairs` |  |
| smeltery/scorched/scorched_soul_glass | `tconstruct:scorched_soul_glass` |  |
| smeltery/scorched/scorched_soul_glass_pane | 16 `tconstruct:scorched_soul_glass_pane` |  |
| smeltery/scorched/scorched_tinted_glass | `tconstruct:scorched_tinted_glass` |  |
| smeltery/scorched/table | `tconstruct:scorched_table` |  |
| smeltery/seared/basin | `tconstruct:seared_basin` |  |
| smeltery/seared/channel | 5 `tconstruct:seared_channel` |  |
| smeltery/seared/chute | `tconstruct:seared_chute` |  |
| smeltery/seared/drain | `tconstruct:seared_drain` |  |
| smeltery/seared/duct | `tconstruct:seared_duct` |  |
| smeltery/seared/faucet | 3 `tconstruct:seared_faucet` |  |
| smeltery/seared/fluid_cannon | `tconstruct:seared_fluid_cannon` |  |
| smeltery/seared/fuel_gauge | `tconstruct:seared_fuel_gauge` |  |
| smeltery/seared/fuel_tank | `tconstruct:seared_fuel_tank` |  |
| smeltery/seared/gauge | 4 `tconstruct:copper_gauge` |  |
| smeltery/seared/heater | `tconstruct:seared_heater` |  |
| smeltery/seared/ingot_gauge | `tconstruct:seared_ingot_gauge` |  |
| smeltery/seared/ingot_tank | `tconstruct:seared_ingot_tank` |  |
| smeltery/seared/lantern | 3 `tconstruct:seared_lantern` |  |
| smeltery/seared/melter | `tconstruct:seared_melter` |  |
| smeltery/seared/seared_bricks_crafting | 4 `tconstruct:seared_bricks` |  |
| smeltery/seared/seared_bricks_from_brick | `tconstruct:seared_bricks` |  |
| smeltery/seared/seared_bricks_slab | 6 `tconstruct:seared_bricks_slab` |  |
| smeltery/seared/seared_bricks_stairs | 4 `tconstruct:seared_bricks_stairs` |  |
| smeltery/seared/seared_bricks_wall | 6 `tconstruct:seared_bricks_wall` |  |
| smeltery/seared/seared_casting_tank | `tconstruct:seared_casting_tank` |  |
| smeltery/seared/seared_cobble_slab | 6 `tconstruct:seared_cobble_slab` |  |
| smeltery/seared/seared_cobble_stairs | 4 `tconstruct:seared_cobble_stairs` |  |
| smeltery/seared/seared_cobble_wall | 6 `tconstruct:seared_cobble_wall` |  |
| smeltery/seared/seared_fancy_bricks_crafting | `tconstruct:seared_fancy_bricks` |  |
| smeltery/seared/seared_glass | `tconstruct:seared_glass` |  |
| smeltery/seared/seared_glass_pane | 16 `tconstruct:seared_glass_pane` |  |
| smeltery/seared/seared_ladder | 4 `tconstruct:seared_ladder` |  |
| smeltery/seared/seared_lamp | `tconstruct:seared_lamp` |  |
| smeltery/seared/seared_paver_slab | 6 `tconstruct:seared_paver_slab` |  |
| smeltery/seared/seared_paver_stairs | 4 `tconstruct:seared_paver_stairs` |  |
| smeltery/seared/seared_soul_glass | `tconstruct:seared_soul_glass` |  |
| smeltery/seared/seared_soul_glass_pane | 16 `tconstruct:seared_soul_glass_pane` |  |
| smeltery/seared/seared_stone_slab | 6 `tconstruct:seared_stone_slab` |  |
| smeltery/seared/seared_stone_stairs | 4 `tconstruct:seared_stone_stairs` |  |
| smeltery/seared/seared_tinted_glass | `tconstruct:seared_tinted_glass` |  |
| smeltery/seared/table | `tconstruct:seared_table` |  |
| tables/cast_chest | `tconstruct:cast_chest` |  |
| tables/crafting_station | `tconstruct:crafting_station` |  |
| tables/part_chest | `tconstruct:part_chest` |  |
| tables/pattern | 6 `tconstruct:pattern` |  |
| tables/tinkers_chest | `tconstruct:tinkers_chest` |  |
| tools/building/earth_staff | `tconstruct:earth_staff` |  |
| tools/building/ender_staff | `tconstruct:ender_staff` |  |
| tools/building/ichor_staff | `tconstruct:ichor_staff` |  |
| tools/building/sky_staff | `tconstruct:sky_staff` |  |
| world/wood/bloodshroom/door | 3 `tconstruct:bloodshroom_door` |  |
| world/wood/bloodshroom/fence | 3 `tconstruct:bloodshroom_fence` |  |
| world/wood/bloodshroom/fence_gate | `tconstruct:bloodshroom_fence_gate` |  |
| world/wood/bloodshroom/hanging_sign | 6 `tconstruct:bloodshroom_hanging_sign` |  |
| world/wood/bloodshroom/log_to_wood | 3 `tconstruct:bloodshroom_wood` |  |
| world/wood/bloodshroom/pressure_plate | `tconstruct:bloodshroom_pressure_plate` |  |
| world/wood/bloodshroom/sign | 3 `tconstruct:bloodshroom_sign` |  |
| world/wood/bloodshroom/slab | 6 `tconstruct:bloodshroom_planks_slab` |  |
| world/wood/bloodshroom/stairs | 4 `tconstruct:bloodshroom_planks_stairs` |  |
| world/wood/bloodshroom/stripped_log_to_wood | 3 `tconstruct:stripped_bloodshroom_wood` |  |
| world/wood/bloodshroom/trapdoor | 2 `tconstruct:bloodshroom_trapdoor` |  |
| world/wood/enderbark/door | 3 `tconstruct:enderbark_door` |  |
| world/wood/enderbark/fence | 3 `tconstruct:enderbark_fence` |  |
| world/wood/enderbark/fence_gate | `tconstruct:enderbark_fence_gate` |  |
| world/wood/enderbark/hanging_sign | 6 `tconstruct:enderbark_hanging_sign` |  |
| world/wood/enderbark/log_to_wood | 3 `tconstruct:enderbark_wood` |  |
| world/wood/enderbark/pressure_plate | `tconstruct:enderbark_pressure_plate` |  |
| world/wood/enderbark/sign | 3 `tconstruct:enderbark_sign` |  |
| world/wood/enderbark/slab | 6 `tconstruct:enderbark_planks_slab` |  |
| world/wood/enderbark/stairs | 4 `tconstruct:enderbark_planks_stairs` |  |
| world/wood/enderbark/stripped_log_to_wood | 3 `tconstruct:stripped_enderbark_wood` |  |
| world/wood/enderbark/trapdoor | 2 `tconstruct:enderbark_trapdoor` |  |
| world/wood/greenheart/door | 3 `tconstruct:greenheart_door` |  |
| world/wood/greenheart/fence | 3 `tconstruct:greenheart_fence` |  |
| world/wood/greenheart/fence_gate | `tconstruct:greenheart_fence_gate` |  |
| world/wood/greenheart/hanging_sign | 6 `tconstruct:greenheart_hanging_sign` |  |
| world/wood/greenheart/log_to_wood | 3 `tconstruct:greenheart_wood` |  |
| world/wood/greenheart/pressure_plate | `tconstruct:greenheart_pressure_plate` |  |
| world/wood/greenheart/sign | 3 `tconstruct:greenheart_sign` |  |
| world/wood/greenheart/slab | 6 `tconstruct:greenheart_planks_slab` |  |
| world/wood/greenheart/stairs | 4 `tconstruct:greenheart_planks_stairs` |  |
| world/wood/greenheart/stripped_log_to_wood | 3 `tconstruct:stripped_greenheart_wood` |  |
| world/wood/greenheart/trapdoor | 2 `tconstruct:greenheart_trapdoor` |  |
| world/wood/skyroot/door | 3 `tconstruct:skyroot_door` |  |
| world/wood/skyroot/fence | 3 `tconstruct:skyroot_fence` |  |
| world/wood/skyroot/fence_gate | `tconstruct:skyroot_fence_gate` |  |
| world/wood/skyroot/hanging_sign | 6 `tconstruct:skyroot_hanging_sign` |  |
| world/wood/skyroot/log_to_wood | 3 `tconstruct:skyroot_wood` |  |
| world/wood/skyroot/pressure_plate | `tconstruct:skyroot_pressure_plate` |  |
| world/wood/skyroot/sign | 3 `tconstruct:skyroot_sign` |  |
| world/wood/skyroot/slab | 6 `tconstruct:skyroot_planks_slab` |  |
| world/wood/skyroot/stairs | 4 `tconstruct:skyroot_planks_stairs` |  |
| world/wood/skyroot/stripped_log_to_wood | 3 `tconstruct:stripped_skyroot_wood` |  |
| world/wood/skyroot/trapdoor | 2 `tconstruct:skyroot_trapdoor` |  |

## `minecraft:crafting_shapeless` (63)

| Recipe | Result | Condition |
|---|---|---|
| common/cheese_ingot_from_block | 4 `tconstruct:cheese_ingot` |  |
| common/copper_platform_waxing_exposed | `tconstruct:waxed_exposed_copper_platform` |  |
| common/copper_platform_waxing_oxidized | `tconstruct:waxed_oxidized_copper_platform` |  |
| common/copper_platform_waxing_unaffected | `tconstruct:waxed_copper_platform` |  |
| common/copper_platform_waxing_weathered | `tconstruct:waxed_weathered_copper_platform` |  |
| common/fantastic_foundry | `tconstruct:fantastic_foundry` |  |
| common/flint | `minecraft:flint` | config `gravel_to_flint` |
| common/materials/amethyst_bronze_ingot_from_block | 9 `tconstruct:amethyst_bronze_ingot` |  |
| common/materials/amethyst_bronze_nugget_from_ingot | 9 `tconstruct:amethyst_bronze_nugget` |  |
| common/materials/cinderslime_ingot_from_block | 9 `tconstruct:cinderslime_ingot` |  |
| common/materials/cinderslime_nugget_from_ingot | 9 `tconstruct:cinderslime_nugget` |  |
| common/materials/cobalt_ingot_from_block | 9 `tconstruct:cobalt_ingot` |  |
| common/materials/cobalt_nugget_from_ingot | 9 `tconstruct:cobalt_nugget` |  |
| common/materials/copper_nugget_from_ingot | 9 `tconstruct:copper_nugget` |  |
| common/materials/debris_nugget_from_ingot | 9 `tconstruct:debris_nugget` |  |
| common/materials/hepatizon_ingot_from_block | 9 `tconstruct:hepatizon_ingot` |  |
| common/materials/hepatizon_nugget_from_ingot | 9 `tconstruct:hepatizon_nugget` |  |
| common/materials/knightmetal_ingot_from_block | 9 `tconstruct:knightmetal_ingot` |  |
| common/materials/knightmetal_nugget_from_ingot | 9 `tconstruct:knightmetal_nugget` |  |
| common/materials/knightslime_ingot_from_block | 9 `tconstruct:knightslime_ingot` |  |
| common/materials/knightslime_nugget_from_ingot | 9 `tconstruct:knightslime_nugget` |  |
| common/materials/manyullyn_ingot_from_block | 9 `tconstruct:manyullyn_ingot` |  |
| common/materials/manyullyn_nugget_from_ingot | 9 `tconstruct:manyullyn_nugget` |  |
| common/materials/netherite_nugget_from_ingot | 9 `tconstruct:netherite_nugget` |  |
| common/materials/pig_iron_ingot_from_block | 9 `tconstruct:pig_iron_ingot` |  |
| common/materials/pig_iron_nugget_from_ingot | 9 `tconstruct:pig_iron_nugget` |  |
| common/materials/queens_slime_ingot_from_block | 9 `tconstruct:queens_slime_ingot` |  |
| common/materials/queens_slime_nugget_from_ingot | 9 `tconstruct:queens_slime_nugget` |  |
| common/materials/raw_cobalt_from_raw_block | 9 `tconstruct:raw_cobalt` |  |
| common/materials/rose_gold_ingot_from_block | 9 `tconstruct:rose_gold_ingot` |  |
| common/materials/rose_gold_nugget_from_ingot | 9 `tconstruct:rose_gold_nugget` |  |
| common/materials/slimesteel_ingot_from_block | 9 `tconstruct:slimesteel_ingot` |  |
| common/materials/slimesteel_nugget_from_ingot | 9 `tconstruct:slimesteel_nugget` |  |
| common/materials/steel_ingot_from_block | 9 `tconstruct:steel_ingot` |  |
| common/materials/steel_nugget_from_ingot | 9 `tconstruct:steel_nugget` |  |
| common/materials_and_you | `tconstruct:materials_and_you` |  |
| common/puny_smelting | `tconstruct:puny_smelting` |  |
| common/slime/earth/slimeball_from_congealed | 4 `minecraft:slime_ball` |  |
| common/slime/ender/slimeball_from_block | 9 `tconstruct:ender_slime_ball` |  |
| common/slime/ender/slimeball_from_congealed | 4 `tconstruct:ender_slime_ball` |  |
| common/slime/ichor/slimeball_from_block | 9 `tconstruct:ichor_slime_ball` |  |
| common/slime/ichor/slimeball_from_congealed | 4 `tconstruct:ichor_slime_ball` |  |
| common/slime/sky/slimeball_from_block | 9 `tconstruct:sky_slime_ball` |  |
| common/slime/sky/slimeball_from_congealed | 4 `tconstruct:sky_slime_ball` |  |
| common/tinkers_gadgetry | `tconstruct:tinkers_gadgetry` |  |
| gadgets/fancy_frame/reversed_gold | `tconstruct:reversed_gold_item_frame` |  |
| gadgets/fancy_frame/reversed_reversed_gold | `tconstruct:gold_item_frame` |  |
| smeltery/red_sand_cast | 4 `tconstruct:blank_red_sand_cast` |  |
| smeltery/sand_cast | 4 `tconstruct:blank_sand_cast` |  |
| smeltery/scorched/nether_grout | 2 `tconstruct:nether_grout` |  |
| smeltery/scorched/nether_grout_multiple | 8 `tconstruct:nether_grout` |  |
| smeltery/seared/grout | 2 `tconstruct:grout` |  |
| smeltery/seared/grout_multiple | 8 `tconstruct:grout` |  |
| tables/book_substitute | `minecraft:book` |  |
| tools/building/flint_and_brick | `tconstruct:flint_and_brick` |  |
| world/wood/bloodshroom/button | `tconstruct:bloodshroom_button` |  |
| world/wood/bloodshroom/planks | 4 `tconstruct:bloodshroom_planks` |  |
| world/wood/enderbark/button | `tconstruct:enderbark_button` |  |
| world/wood/enderbark/planks | 4 `tconstruct:enderbark_planks` |  |
| world/wood/greenheart/button | `tconstruct:greenheart_button` |  |
| world/wood/greenheart/planks | 4 `tconstruct:greenheart_planks` |  |
| world/wood/skyroot/button | `tconstruct:skyroot_button` |  |
| world/wood/skyroot/planks | 4 `tconstruct:skyroot_planks` |  |

## `minecraft:smelting` (8)

| Recipe | Result | Condition |
|---|---|---|
| common/materials/knightmetal_nugget_smelting | `tconstruct:knightmetal_nugget` |  |
| common/materials/steel_nugget_smelting | `tconstruct:steel_nugget` |  |
| smeltery/scorched/scorched_brick | `tconstruct:scorched_brick` |  |
| smeltery/scorched/scorched_road_smelting | `tconstruct:scorched_road` |  |
| smeltery/seared/seared_brick | `tconstruct:seared_brick` |  |
| smeltery/seared/seared_cracked_bricks_smelting | `tconstruct:seared_cracked_bricks` |  |
| smeltery/seared/seared_paver_smelting | `tconstruct:seared_paver` |  |
| smeltery/seared/seared_stone_smelting | `tconstruct:seared_stone` |  |

## `minecraft:stonecutting` (20)

| Recipe | Result | Condition |
|---|---|---|
| smeltery/scorched/chiseled_scorched_bricks_stonecutting | `tconstruct:chiseled_scorched_bricks` |  |
| smeltery/scorched/polished_scorched_stone_stonecutting | `tconstruct:polished_scorched_stone` |  |
| smeltery/scorched/scorched_bricks_slab_stonecutter | 2 `tconstruct:scorched_bricks_slab` |  |
| smeltery/scorched/scorched_bricks_stairs_stonecutter | `tconstruct:scorched_bricks_stairs` |  |
| smeltery/scorched/scorched_bricks_stonecutting | `tconstruct:scorched_bricks` |  |
| smeltery/scorched/scorched_road_slab_stonecutter | 2 `tconstruct:scorched_road_slab` |  |
| smeltery/scorched/scorched_road_stairs_stonecutter | `tconstruct:scorched_road_stairs` |  |
| smeltery/seared/seared_bricks_slab_stonecutter | 2 `tconstruct:seared_bricks_slab` |  |
| smeltery/seared/seared_bricks_stairs_stonecutter | `tconstruct:seared_bricks_stairs` |  |
| smeltery/seared/seared_bricks_stonecutting | `tconstruct:seared_bricks` |  |
| smeltery/seared/seared_bricks_wall_stonecutter | `tconstruct:seared_bricks_wall` |  |
| smeltery/seared/seared_cobble_slab_stonecutter | 2 `tconstruct:seared_cobble_slab` |  |
| smeltery/seared/seared_cobble_stairs_stonecutter | `tconstruct:seared_cobble_stairs` |  |
| smeltery/seared/seared_cobble_wall_stonecutter | `tconstruct:seared_cobble_wall` |  |
| smeltery/seared/seared_fancy_bricks_stonecutting | `tconstruct:seared_fancy_bricks` |  |
| smeltery/seared/seared_paver_slab_stonecutter | 2 `tconstruct:seared_paver_slab` |  |
| smeltery/seared/seared_paver_stairs_stonecutter | `tconstruct:seared_paver_stairs` |  |
| smeltery/seared/seared_stone_slab_stonecutter | 2 `tconstruct:seared_stone_slab` |  |
| smeltery/seared/seared_stone_stairs_stonecutter | `tconstruct:seared_stone_stairs` |  |
| smeltery/seared/seared_triangle_bricks_stonecutting | `tconstruct:seared_triangle_bricks` |  |

## `tconstruct:ageable_severing` (3)

| Recipe | Result | Condition |
|---|---|---|
| tools/severing/chicken_feather |  |  |
| tools/severing/rabbit_foot |  |  |
| tools/severing/turtle_shell |  |  |

## `tconstruct:alloy` (1)

| Recipe | Result | Condition |
|---|---|---|
| compat/ceramics/alloy_porcelain | `?` | mod `ceramics` |

## `tconstruct:armor_dyeing_modifier` (1)

| Recipe | Result | Condition |
|---|---|---|
| tools/modifiers/slotless/dyeing |  |  |

## `tconstruct:armor_trim_modifier` (1)

| Recipe | Result | Condition |
|---|---|---|
| tools/modifiers/slotless/trim |  |  |

## `tconstruct:banner_modifier` (1)

| Recipe | Result | Condition |
|---|---|---|
| tools/modifiers/slotless/banner |  |  |

## `tconstruct:basin_casting_composite` (1)

| Recipe | Result | Condition |
|---|---|---|
| tools/parts/fake_storage_block_composite | `tconstruct:fake_storage_block` |  |

## `tconstruct:basin_casting_material` (1)

| Recipe | Result | Condition |
|---|---|---|
| tools/parts/fake_storage_block_casting | `tconstruct:fake_storage_block` |  |

## `tconstruct:basin_filling` (10)

| Recipe | Result | Condition |
|---|---|---|
| smeltery/casting/filling/scorched_fuel_gauge |  |  |
| smeltery/casting/filling/scorched_fuel_tank |  |  |
| smeltery/casting/filling/scorched_ingot_gauge |  |  |
| smeltery/casting/filling/scorched_ingot_tank |  |  |
| smeltery/casting/filling/scorched_lantern_full |  |  |
| smeltery/casting/filling/seared_fuel_gauge |  |  |
| smeltery/casting/filling/seared_fuel_tank |  |  |
| smeltery/casting/filling/seared_ingot_gauge |  |  |
| smeltery/casting/filling/seared_ingot_tank |  |  |
| smeltery/casting/filling/seared_lantern_full |  |  |

## `tconstruct:basin_tool_casting` (23)

| Recipe | Result | Condition |
|---|---|---|
| tools/armor/slime_skull/blaze | `tconstruct:slime_helmet` |  |
| tools/armor/slime_skull/blazing_bone | `tconstruct:slime_helmet` |  |
| tools/armor/slime_skull/bone | `tconstruct:slime_helmet` |  |
| tools/armor/slime_skull/copper | `tconstruct:slime_helmet` |  |
| tools/armor/slime_skull/darkthread | `tconstruct:slime_helmet` |  |
| tools/armor/slime_skull/dragon_scale | `tconstruct:slime_helmet` |  |
| tools/armor/slime_skull/ender_pearl | `tconstruct:slime_helmet` |  |
| tools/armor/slime_skull/glass | `tconstruct:slime_helmet` |  |
| tools/armor/slime_skull/gold | `tconstruct:slime_helmet` |  |
| tools/armor/slime_skull/ice | `tconstruct:slime_helmet` |  |
| tools/armor/slime_skull/iron | `tconstruct:slime_helmet` |  |
| tools/armor/slime_skull/knightmetal | `tconstruct:slime_helmet` |  |
| tools/armor/slime_skull/leather | `tconstruct:slime_helmet` |  |
| tools/armor/slime_skull/necronium | `tconstruct:slime_helmet` |  |
| tools/armor/slime_skull/necrotic_bone | `tconstruct:slime_helmet` |  |
| tools/armor/slime_skull/pig_iron | `tconstruct:slime_helmet` |  |
| tools/armor/slime_skull/rose_gold | `tconstruct:slime_helmet` |  |
| tools/armor/slime_skull/string | `tconstruct:slime_helmet` |  |
| tools/armor/slime_skull/venombone | `tconstruct:slime_helmet` |  |
| tools/armor/slimelytra | `tconstruct:slime_wings` |  |
| tools/building/slime_boots | `tconstruct:slime_boots` |  |
| tools/building/slimecage | `tconstruct:slimy_chestplate` |  |
| tools/building/slimeshell | `tconstruct:slime_leggings` |  |

## `tconstruct:casting_table_potion` (4)

| Recipe | Result | Condition |
|---|---|---|
| smeltery/casting/filling/bottle | `minecraft:potion` |  |
| smeltery/casting/filling/lingering_bottle | `minecraft:splash_potion` |  |
| smeltery/casting/filling/splash_bottle | `minecraft:lingering_potion` |  |
| smeltery/casting/filling/tipped_arrow | `minecraft:tipped_arrow` |  |

## `tconstruct:casting_table_tipped_clearing` (2)

| Recipe | Result | Condition |
|---|---|---|
| tools/modifiers/slotless/ammo_tip_clearing |  |  |
| tools/modifiers/slotless/fishing_rod_tip_clearing |  |  |

## `tconstruct:casting_table_tipping` (2)

| Recipe | Result | Condition |
|---|---|---|
| tools/modifiers/slotless/ammo_tipping |  |  |
| tools/modifiers/slotless/fishing_rod_tipping |  |  |

## `tconstruct:crafting_overslime_modifier` (4)

| Recipe | Result | Condition |
|---|---|---|
| tools/modifiers/slotless/overslime/earth_bottle_crafting_table |  |  |
| tools/modifiers/slotless/overslime/ender_bottle_crafting_table |  |  |
| tools/modifiers/slotless/overslime/ichor_bottle_crafting_table |  |  |
| tools/modifiers/slotless/overslime/sky_bottle_crafting_table |  |  |

## `tconstruct:crafting_shaped_materials` (10)

| Recipe | Result | Condition |
|---|---|---|
| tables/scorched_anvil_material | `tconstruct:scorched_anvil` |  |
| tables/scorched_forge_material | `tconstruct:scorched_anvil` |  |
| tables/seared_forge_material | `tconstruct:tinkers_anvil` |  |
| tables/tinkers_anvil_material | `tconstruct:tinkers_anvil` |  |
| tools/armor/travelers/boots | `tconstruct:travelers_boots` |  |
| tools/armor/travelers/chestplate | `tconstruct:travelers_chestplate` |  |
| tools/armor/travelers/goggles | `tconstruct:travelers_helmet` |  |
| tools/armor/travelers/pants | `tconstruct:travelers_leggings` |  |
| tools/armor/travelers/shield | `tconstruct:travelers_shield` |  |
| tools/parts/fake_ingot_to_block | `tconstruct:fake_storage_block` |  |

## `tconstruct:crafting_shapeless_materials` (1)

| Recipe | Result | Condition |
|---|---|---|
| tools/parts/fake_block_to_ingots | 9 `tconstruct:fake_ingot` |  |

## `tconstruct:crafting_table_repair` (1)

| Recipe | Result | Condition |
|---|---|---|
| tables/crafting_table_repair |  |  |

## `tconstruct:damagable_melting` (4)

| Recipe | Result | Condition |
|---|---|---|
| compat/ceramics/clay/clay_boots | `?` | mod `ceramics` |
| compat/ceramics/clay/clay_chestplate | `?` | mod `ceramics` |
| compat/ceramics/clay/clay_helmet | `?` | mod `ceramics` |
| compat/ceramics/clay/clay_leggings | `?` | mod `ceramics` |

## `tconstruct:extract_modifier` (10)

| Recipe | Result | Condition |
|---|---|---|
| tools/modifiers/worktable/extract/ability |  |  |
| tools/modifiers/worktable/extract/ability_dagger |  |  |
| tools/modifiers/worktable/extract/defense |  |  |
| tools/modifiers/worktable/extract/defense_dagger |  |  |
| tools/modifiers/worktable/extract/modifier |  |  |
| tools/modifiers/worktable/extract/modifier_dagger |  |  |
| tools/modifiers/worktable/extract/slotless |  |  |
| tools/modifiers/worktable/extract/slotless_dagger |  |  |
| tools/modifiers/worktable/extract/upgrade |  |  |
| tools/modifiers/worktable/extract/upgrade_dagger |  |  |

## `tconstruct:fixed_material_swapping` (19)

| Recipe | Result | Condition |
|---|---|---|
| tools/armor/slime_skull/swapping/blaze |  |  |
| tools/armor/slime_skull/swapping/blazing_bone |  |  |
| tools/armor/slime_skull/swapping/bone |  |  |
| tools/armor/slime_skull/swapping/copper |  |  |
| tools/armor/slime_skull/swapping/darkthread |  |  |
| tools/armor/slime_skull/swapping/dragon_scale |  |  |
| tools/armor/slime_skull/swapping/ender_pearl |  |  |
| tools/armor/slime_skull/swapping/glass |  |  |
| tools/armor/slime_skull/swapping/gold |  |  |
| tools/armor/slime_skull/swapping/ice |  |  |
| tools/armor/slime_skull/swapping/iron |  |  |
| tools/armor/slime_skull/swapping/knightmetal |  |  |
| tools/armor/slime_skull/swapping/leather |  |  |
| tools/armor/slime_skull/swapping/necronium |  |  |
| tools/armor/slime_skull/swapping/necrotic_bone |  |  |
| tools/armor/slime_skull/swapping/pig_iron |  |  |
| tools/armor/slime_skull/swapping/rose_gold |  |  |
| tools/armor/slime_skull/swapping/string |  |  |
| tools/armor/slime_skull/swapping/venombone |  |  |

## `tconstruct:item_part_builder` (116)

| Recipe | Result | Condition |
|---|---|---|
| smeltery/casts/red_sand/builder_block/adze_head | 4 `tconstruct:adze_head_red_sand_cast` |  |
| smeltery/casts/red_sand/builder_block/boots_plating | 4 `tconstruct:boots_plating_red_sand_cast` |  |
| smeltery/casts/red_sand/builder_block/bow_grip | 4 `tconstruct:bow_grip_red_sand_cast` |  |
| smeltery/casts/red_sand/builder_block/bow_limb | 4 `tconstruct:bow_limb_red_sand_cast` |  |
| smeltery/casts/red_sand/builder_block/broad_axe_head | 4 `tconstruct:broad_axe_head_red_sand_cast` |  |
| smeltery/casts/red_sand/builder_block/broad_blade | 4 `tconstruct:broad_blade_red_sand_cast` |  |
| smeltery/casts/red_sand/builder_block/chestplate_plating | 4 `tconstruct:chestplate_plating_red_sand_cast` |  |
| smeltery/casts/red_sand/builder_block/coins | 4 `tconstruct:coin_red_sand_cast` | tag `c:coins` filled |
| smeltery/casts/red_sand/builder_block/gears | 4 `tconstruct:gear_red_sand_cast` | tag `c:gears` filled |
| smeltery/casts/red_sand/builder_block/gems | 4 `tconstruct:gem_red_sand_cast` |  |
| smeltery/casts/red_sand/builder_block/hammer_head | 4 `tconstruct:hammer_head_red_sand_cast` |  |
| smeltery/casts/red_sand/builder_block/helmet_plating | 4 `tconstruct:helmet_plating_red_sand_cast` |  |
| smeltery/casts/red_sand/builder_block/ingots | 4 `tconstruct:ingot_red_sand_cast` |  |
| smeltery/casts/red_sand/builder_block/large_plate | 4 `tconstruct:large_plate_red_sand_cast` |  |
| smeltery/casts/red_sand/builder_block/leggings_plating | 4 `tconstruct:leggings_plating_red_sand_cast` |  |
| smeltery/casts/red_sand/builder_block/maille | 4 `tconstruct:maille_red_sand_cast` |  |
| smeltery/casts/red_sand/builder_block/nuggets | 4 `tconstruct:nugget_red_sand_cast` |  |
| smeltery/casts/red_sand/builder_block/pick_head | 4 `tconstruct:pick_head_red_sand_cast` |  |
| smeltery/casts/red_sand/builder_block/plates | 4 `tconstruct:plate_red_sand_cast` | tag `c:plates` filled |
| smeltery/casts/red_sand/builder_block/repair_kit | 4 `tconstruct:repair_kit_red_sand_cast` |  |
| smeltery/casts/red_sand/builder_block/rods | 4 `tconstruct:rod_red_sand_cast` |  |
| smeltery/casts/red_sand/builder_block/small_axe_head | 4 `tconstruct:small_axe_head_red_sand_cast` |  |
| smeltery/casts/red_sand/builder_block/small_blade | 4 `tconstruct:small_blade_red_sand_cast` |  |
| smeltery/casts/red_sand/builder_block/tool_binding | 4 `tconstruct:tool_binding_red_sand_cast` |  |
| smeltery/casts/red_sand/builder_block/tool_handle | 4 `tconstruct:tool_handle_red_sand_cast` |  |
| smeltery/casts/red_sand/builder_block/tough_binding | 4 `tconstruct:tough_binding_red_sand_cast` |  |
| smeltery/casts/red_sand/builder_block/tough_handle | 4 `tconstruct:tough_handle_red_sand_cast` |  |
| smeltery/casts/red_sand/builder_block/wires | 4 `tconstruct:wire_red_sand_cast` | tag `c:wires` filled |
| smeltery/casts/red_sand/builder_cast/adze_head | `tconstruct:adze_head_red_sand_cast` |  |
| smeltery/casts/red_sand/builder_cast/boots_plating | `tconstruct:boots_plating_red_sand_cast` |  |
| smeltery/casts/red_sand/builder_cast/bow_grip | `tconstruct:bow_grip_red_sand_cast` |  |
| smeltery/casts/red_sand/builder_cast/bow_limb | `tconstruct:bow_limb_red_sand_cast` |  |
| smeltery/casts/red_sand/builder_cast/broad_axe_head | `tconstruct:broad_axe_head_red_sand_cast` |  |
| smeltery/casts/red_sand/builder_cast/broad_blade | `tconstruct:broad_blade_red_sand_cast` |  |
| smeltery/casts/red_sand/builder_cast/chestplate_plating | `tconstruct:chestplate_plating_red_sand_cast` |  |
| smeltery/casts/red_sand/builder_cast/coins | `tconstruct:coin_red_sand_cast` | tag `c:coins` filled |
| smeltery/casts/red_sand/builder_cast/gears | `tconstruct:gear_red_sand_cast` | tag `c:gears` filled |
| smeltery/casts/red_sand/builder_cast/gems | `tconstruct:gem_red_sand_cast` |  |
| smeltery/casts/red_sand/builder_cast/hammer_head | `tconstruct:hammer_head_red_sand_cast` |  |
| smeltery/casts/red_sand/builder_cast/helmet_plating | `tconstruct:helmet_plating_red_sand_cast` |  |
| smeltery/casts/red_sand/builder_cast/ingots | `tconstruct:ingot_red_sand_cast` |  |
| smeltery/casts/red_sand/builder_cast/large_plate | `tconstruct:large_plate_red_sand_cast` |  |
| smeltery/casts/red_sand/builder_cast/leggings_plating | `tconstruct:leggings_plating_red_sand_cast` |  |
| smeltery/casts/red_sand/builder_cast/maille | `tconstruct:maille_red_sand_cast` |  |
| smeltery/casts/red_sand/builder_cast/nuggets | `tconstruct:nugget_red_sand_cast` |  |
| smeltery/casts/red_sand/builder_cast/pick_head | `tconstruct:pick_head_red_sand_cast` |  |
| smeltery/casts/red_sand/builder_cast/plates | `tconstruct:plate_red_sand_cast` | tag `c:plates` filled |
| smeltery/casts/red_sand/builder_cast/repair_kit | `tconstruct:repair_kit_red_sand_cast` |  |
| smeltery/casts/red_sand/builder_cast/rods | `tconstruct:rod_red_sand_cast` |  |
| smeltery/casts/red_sand/builder_cast/small_axe_head | `tconstruct:small_axe_head_red_sand_cast` |  |
| smeltery/casts/red_sand/builder_cast/small_blade | `tconstruct:small_blade_red_sand_cast` |  |
| smeltery/casts/red_sand/builder_cast/tool_binding | `tconstruct:tool_binding_red_sand_cast` |  |
| smeltery/casts/red_sand/builder_cast/tool_handle | `tconstruct:tool_handle_red_sand_cast` |  |
| smeltery/casts/red_sand/builder_cast/tough_binding | `tconstruct:tough_binding_red_sand_cast` |  |
| smeltery/casts/red_sand/builder_cast/tough_handle | `tconstruct:tough_handle_red_sand_cast` |  |
| smeltery/casts/red_sand/builder_cast/wires | `tconstruct:wire_red_sand_cast` | tag `c:wires` filled |
| smeltery/casts/sand/builder_block/adze_head | 4 `tconstruct:adze_head_sand_cast` |  |
| smeltery/casts/sand/builder_block/boots_plating | 4 `tconstruct:boots_plating_sand_cast` |  |
| smeltery/casts/sand/builder_block/bow_grip | 4 `tconstruct:bow_grip_sand_cast` |  |
| smeltery/casts/sand/builder_block/bow_limb | 4 `tconstruct:bow_limb_sand_cast` |  |
| smeltery/casts/sand/builder_block/broad_axe_head | 4 `tconstruct:broad_axe_head_sand_cast` |  |
| smeltery/casts/sand/builder_block/broad_blade | 4 `tconstruct:broad_blade_sand_cast` |  |
| smeltery/casts/sand/builder_block/chestplate_plating | 4 `tconstruct:chestplate_plating_sand_cast` |  |
| smeltery/casts/sand/builder_block/coins | 4 `tconstruct:coin_sand_cast` | tag `c:coins` filled |
| smeltery/casts/sand/builder_block/gears | 4 `tconstruct:gear_sand_cast` | tag `c:gears` filled |
| smeltery/casts/sand/builder_block/gems | 4 `tconstruct:gem_sand_cast` |  |
| smeltery/casts/sand/builder_block/hammer_head | 4 `tconstruct:hammer_head_sand_cast` |  |
| smeltery/casts/sand/builder_block/helmet_plating | 4 `tconstruct:helmet_plating_sand_cast` |  |
| smeltery/casts/sand/builder_block/ingots | 4 `tconstruct:ingot_sand_cast` |  |
| smeltery/casts/sand/builder_block/large_plate | 4 `tconstruct:large_plate_sand_cast` |  |
| smeltery/casts/sand/builder_block/leggings_plating | 4 `tconstruct:leggings_plating_sand_cast` |  |
| smeltery/casts/sand/builder_block/maille | 4 `tconstruct:maille_sand_cast` |  |
| smeltery/casts/sand/builder_block/nuggets | 4 `tconstruct:nugget_sand_cast` |  |
| smeltery/casts/sand/builder_block/pick_head | 4 `tconstruct:pick_head_sand_cast` |  |
| smeltery/casts/sand/builder_block/plates | 4 `tconstruct:plate_sand_cast` | tag `c:plates` filled |
| smeltery/casts/sand/builder_block/repair_kit | 4 `tconstruct:repair_kit_sand_cast` |  |
| smeltery/casts/sand/builder_block/rods | 4 `tconstruct:rod_sand_cast` |  |
| smeltery/casts/sand/builder_block/small_axe_head | 4 `tconstruct:small_axe_head_sand_cast` |  |
| smeltery/casts/sand/builder_block/small_blade | 4 `tconstruct:small_blade_sand_cast` |  |
| smeltery/casts/sand/builder_block/tool_binding | 4 `tconstruct:tool_binding_sand_cast` |  |
| smeltery/casts/sand/builder_block/tool_handle | 4 `tconstruct:tool_handle_sand_cast` |  |
| smeltery/casts/sand/builder_block/tough_binding | 4 `tconstruct:tough_binding_sand_cast` |  |
| smeltery/casts/sand/builder_block/tough_handle | 4 `tconstruct:tough_handle_sand_cast` |  |
| smeltery/casts/sand/builder_block/wires | 4 `tconstruct:wire_sand_cast` | tag `c:wires` filled |
| smeltery/casts/sand/builder_cast/adze_head | `tconstruct:adze_head_sand_cast` |  |
| smeltery/casts/sand/builder_cast/boots_plating | `tconstruct:boots_plating_sand_cast` |  |
| smeltery/casts/sand/builder_cast/bow_grip | `tconstruct:bow_grip_sand_cast` |  |
| smeltery/casts/sand/builder_cast/bow_limb | `tconstruct:bow_limb_sand_cast` |  |
| smeltery/casts/sand/builder_cast/broad_axe_head | `tconstruct:broad_axe_head_sand_cast` |  |
| smeltery/casts/sand/builder_cast/broad_blade | `tconstruct:broad_blade_sand_cast` |  |
| smeltery/casts/sand/builder_cast/chestplate_plating | `tconstruct:chestplate_plating_sand_cast` |  |
| smeltery/casts/sand/builder_cast/coins | `tconstruct:coin_sand_cast` | tag `c:coins` filled |
| smeltery/casts/sand/builder_cast/gears | `tconstruct:gear_sand_cast` | tag `c:gears` filled |
| smeltery/casts/sand/builder_cast/gems | `tconstruct:gem_sand_cast` |  |
| smeltery/casts/sand/builder_cast/hammer_head | `tconstruct:hammer_head_sand_cast` |  |
| smeltery/casts/sand/builder_cast/helmet_plating | `tconstruct:helmet_plating_sand_cast` |  |
| smeltery/casts/sand/builder_cast/ingots | `tconstruct:ingot_sand_cast` |  |
| smeltery/casts/sand/builder_cast/large_plate | `tconstruct:large_plate_sand_cast` |  |
| smeltery/casts/sand/builder_cast/leggings_plating | `tconstruct:leggings_plating_sand_cast` |  |
| smeltery/casts/sand/builder_cast/maille | `tconstruct:maille_sand_cast` |  |
| smeltery/casts/sand/builder_cast/nuggets | `tconstruct:nugget_sand_cast` |  |
| smeltery/casts/sand/builder_cast/pick_head | `tconstruct:pick_head_sand_cast` |  |
| smeltery/casts/sand/builder_cast/plates | `tconstruct:plate_sand_cast` | tag `c:plates` filled |
| smeltery/casts/sand/builder_cast/repair_kit | `tconstruct:repair_kit_sand_cast` |  |
| smeltery/casts/sand/builder_cast/rods | `tconstruct:rod_sand_cast` |  |
| smeltery/casts/sand/builder_cast/small_axe_head | `tconstruct:small_axe_head_sand_cast` |  |
| smeltery/casts/sand/builder_cast/small_blade | `tconstruct:small_blade_sand_cast` |  |
| smeltery/casts/sand/builder_cast/tool_binding | `tconstruct:tool_binding_sand_cast` |  |
| smeltery/casts/sand/builder_cast/tool_handle | `tconstruct:tool_handle_sand_cast` |  |
| smeltery/casts/sand/builder_cast/tough_binding | `tconstruct:tough_binding_sand_cast` |  |
| smeltery/casts/sand/builder_cast/tough_handle | `tconstruct:tough_handle_sand_cast` |  |
| smeltery/casts/sand/builder_cast/wires | `tconstruct:wire_sand_cast` | tag `c:wires` filled |
| tools/parts/builder/boots_plating | `tconstruct:boots_plating_dummy` |  |
| tools/parts/builder/chestplate_plating | `tconstruct:chestplate_plating_dummy` |  |
| tools/parts/builder/helmet_plating | `tconstruct:helmet_plating_dummy` |  |
| tools/parts/builder/leggings_plating | `tconstruct:leggings_plating_dummy` |  |

## `tconstruct:material` (201)

| Recipe | Result | Condition |
|---|---|---|
| tools/materials/aluminum/block |  | tag `c:storage_blocks/aluminum` filled |
| tools/materials/aluminum/ingot |  | tag `c:ingots/aluminum` filled |
| tools/materials/aluminum/nugget |  | tag `c:nuggets/aluminum` filled |
| tools/materials/amethyst |  |  |
| tools/materials/amethyst_bronze/block |  |  |
| tools/materials/amethyst_bronze/ingot |  |  |
| tools/materials/amethyst_bronze/nugget |  |  |
| tools/materials/ancient/ingot |  |  |
| tools/materials/ancient/nugget |  |  |
| tools/materials/blaze |  |  |
| tools/materials/blazewood |  |  |
| tools/materials/blazing_bone |  |  |
| tools/materials/bone |  |  |
| tools/materials/bronze/block |  | tag `c:storage_blocks/bronze` filled |
| tools/materials/bronze/ingot |  | tag `c:ingots/bronze` filled |
| tools/materials/bronze/nugget |  | tag `c:nuggets/bronze` filled |
| tools/materials/cactus |  |  |
| tools/materials/chorus_popped |  |  |
| tools/materials/cinderslime/block |  |  |
| tools/materials/cinderslime/ingot |  |  |
| tools/materials/cinderslime/nugget |  |  |
| tools/materials/clay_ball |  |  |
| tools/materials/clay_block |  |  |
| tools/materials/cobalt/block |  |  |
| tools/materials/cobalt/ingot |  |  |
| tools/materials/cobalt/nugget |  |  |
| tools/materials/constantan/block |  | tag `c:storage_blocks/constantan` filled |
| tools/materials/constantan/ingot |  | tag `c:ingots/constantan` filled |
| tools/materials/constantan/nugget |  | tag `c:nuggets/constantan` filled |
| tools/materials/copper/block |  |  |
| tools/materials/copper/ingot |  |  |
| tools/materials/copper/nugget |  |  |
| tools/materials/copper/oxidized |  |  |
| tools/materials/dragon_scale |  |  |
| tools/materials/earthslime |  |  |
| tools/materials/electrum/block |  | tag `c:storage_blocks/electrum` filled |
| tools/materials/electrum/ingot |  | tag `c:ingots/electrum` filled |
| tools/materials/electrum/nugget |  | tag `c:nuggets/electrum` filled |
| tools/materials/end_rod |  |  |
| tools/materials/ender_pearl |  |  |
| tools/materials/enderslime |  |  |
| tools/materials/enderslime_vine |  |  |
| tools/materials/endstone |  |  |
| tools/materials/feather |  |  |
| tools/materials/fiery/block |  | tag `c:storage_blocks/fiery` filled |
| tools/materials/fiery/ingot |  | tag `c:ingots/fiery` filled |
| tools/materials/fiery/nugget |  | tag `c:nuggets/fiery` filled |
| tools/materials/flint/basalt |  |  |
| tools/materials/flint/deepslate |  |  |
| tools/materials/flint/flint |  |  |
| tools/materials/glass |  |  |
| tools/materials/glass_pane |  |  |
| tools/materials/glowstone/block |  |  |
| tools/materials/glowstone/dust |  |  |
| tools/materials/gold/block |  |  |
| tools/materials/gold/ingot |  |  |
| tools/materials/gold/nugget |  |  |
| tools/materials/gunpowder |  |  |
| tools/materials/hepatizon/block |  |  |
| tools/materials/hepatizon/ingot |  |  |
| tools/materials/hepatizon/nugget |  |  |
| tools/materials/ice/blue |  |  |
| tools/materials/ice/packed |  |  |
| tools/materials/ice/unpacked |  |  |
| tools/materials/ichor |  |  |
| tools/materials/invar/block |  | tag `c:storage_blocks/invar` filled |
| tools/materials/invar/ingot |  | tag `c:ingots/invar` filled |
| tools/materials/invar/nugget |  | tag `c:nuggets/invar` filled |
| tools/materials/iron/block |  |  |
| tools/materials/iron/ingot |  |  |
| tools/materials/iron/nugget |  |  |
| tools/materials/ironwood/block |  | tag `c:storage_blocks/ironwood` filled |
| tools/materials/ironwood/ingot |  | tag `c:ingots/ironwood` filled |
| tools/materials/ironwood/nugget |  | tag `c:nuggets/ironwood` filled |
| tools/materials/knightly |  |  |
| tools/materials/knightmetal/block |  |  |
| tools/materials/knightmetal/ingot |  |  |
| tools/materials/knightmetal/nugget |  |  |
| tools/materials/knightslime/block |  |  |
| tools/materials/knightslime/ingot |  |  |
| tools/materials/knightslime/nugget |  |  |
| tools/materials/kobold |  |  |
| tools/materials/lead/block |  | tag `c:storage_blocks/lead` filled |
| tools/materials/lead/ingot |  | tag `c:ingots/lead` filled |
| tools/materials/lead/nugget |  | tag `c:nuggets/lead` filled |
| tools/materials/leather |  |  |
| tools/materials/leaves |  |  |
| tools/materials/magma |  |  |
| tools/materials/magnetite |  |  |
| tools/materials/manyullyn/block |  |  |
| tools/materials/manyullyn/ingot |  |  |
| tools/materials/manyullyn/nugget |  |  |
| tools/materials/nahuatl |  |  |
| tools/materials/necronium |  | any of(config `force_integration_materials`; tag `c:ingots/uranium` filled) |
| tools/materials/necrotic_bone |  |  |
| tools/materials/nicrosil/block |  | tag `c:storage_blocks/nicrosil` filled |
| tools/materials/nicrosil/ingot |  | tag `c:ingots/nicrosil` filled |
| tools/materials/nicrosil/nugget |  | tag `c:nuggets/nicrosil` filled |
| tools/materials/obsidian |  |  |
| tools/materials/obsidian_pane |  |  |
| tools/materials/osmium/block |  | tag `c:storage_blocks/osmium` filled |
| tools/materials/osmium/ingot |  | tag `c:ingots/osmium` filled |
| tools/materials/osmium/nugget |  | tag `c:nuggets/osmium` filled |
| tools/materials/paper |  |  |
| tools/materials/pewter/block |  | tag `c:storage_blocks/pewter` filled |
| tools/materials/pewter/ingot |  | tag `c:ingots/pewter` filled |
| tools/materials/pewter/nugget |  | tag `c:nuggets/pewter` filled |
| tools/materials/phantom_membrane |  |  |
| tools/materials/pig_iron/block |  |  |
| tools/materials/pig_iron/ingot |  |  |
| tools/materials/pig_iron/nugget |  |  |
| tools/materials/prismarine |  |  |
| tools/materials/quartz/block |  |  |
| tools/materials/quartz/gem |  |  |
| tools/materials/queens_slime/block |  |  |
| tools/materials/queens_slime/ingot |  |  |
| tools/materials/queens_slime/nugget |  |  |
| tools/materials/rabbit_hide |  |  |
| tools/materials/redstone/block |  |  |
| tools/materials/redstone/dust |  |  |
| tools/materials/rock/andesite |  |  |
| tools/materials/rock/blackstone |  |  |
| tools/materials/rock/calcite |  |  |
| tools/materials/rock/diorite |  |  |
| tools/materials/rock/granite |  |  |
| tools/materials/rock/stone |  |  |
| tools/materials/rose_gold/block |  |  |
| tools/materials/rose_gold/ingot |  |  |
| tools/materials/rose_gold/nugget |  |  |
| tools/materials/scorched_stone/block |  |  |
| tools/materials/scorched_stone/brick |  |  |
| tools/materials/seared_stone/block |  |  |
| tools/materials/seared_stone/brick |  |  |
| tools/materials/shulker |  |  |
| tools/materials/silver/block |  | tag `c:storage_blocks/silver` filled |
| tools/materials/silver/ingot |  | tag `c:ingots/silver` filled |
| tools/materials/silver/nugget |  | tag `c:nuggets/silver` filled |
| tools/materials/skyslime |  |  |
| tools/materials/skyslime_vine |  |  |
| tools/materials/slimeball/earth |  |  |
| tools/materials/slimeball/ender |  |  |
| tools/materials/slimeball/ichor |  |  |
| tools/materials/slimeball/sky |  |  |
| tools/materials/slimesteel/block |  |  |
| tools/materials/slimesteel/ingot |  |  |
| tools/materials/slimesteel/nugget |  |  |
| tools/materials/slimewood/bloodshroom_logs |  |  |
| tools/materials/slimewood/bloodshroom_planks |  |  |
| tools/materials/slimewood/enderbark_logs |  |  |
| tools/materials/slimewood/enderbark_planks |  |  |
| tools/materials/slimewood/greenheart_logs |  |  |
| tools/materials/slimewood/greenheart_planks |  |  |
| tools/materials/slimewood/skyroot_logs |  |  |
| tools/materials/slimewood/skyroot_planks |  |  |
| tools/materials/steel/block |  |  |
| tools/materials/steel/ingot |  |  |
| tools/materials/steel/nugget |  |  |
| tools/materials/steeleaf/block |  | tag `c:storage_blocks/steeleaf` filled |
| tools/materials/steeleaf/ingot |  | tag `c:ingots/steeleaf` filled |
| tools/materials/steeleaf/nugget |  | tag `c:nuggets/steeleaf` filled |
| tools/materials/string |  |  |
| tools/materials/treated_wood |  | tag `c:treated_wood` filled |
| tools/materials/turtle_scute |  |  |
| tools/materials/twisting_vine |  |  |
| tools/materials/venombone |  |  |
| tools/materials/vine |  |  |
| tools/materials/weeping_vine |  |  |
| tools/materials/wood/bamboo/block |  |  |
| tools/materials/wood/bamboo/planks |  |  |
| tools/materials/wood/bamboo/stick |  |  |
| tools/materials/wood/logs/acacia |  |  |
| tools/materials/wood/logs/birch |  |  |
| tools/materials/wood/logs/cherry |  |  |
| tools/materials/wood/logs/crimson |  |  |
| tools/materials/wood/logs/dark_oak |  |  |
| tools/materials/wood/logs/default |  | tags `m` + `i` + `n` + `e` + `c` + `r` + `a` + `f` + `t` + `:` + `l` + `o` + `g` + `s` filled (ignoring `tconstruct:wood_variants/logs`) |
| tools/materials/wood/logs/jungle |  |  |
| tools/materials/wood/logs/mangrove |  |  |
| tools/materials/wood/logs/oak |  |  |
| tools/materials/wood/logs/spruce |  |  |
| tools/materials/wood/logs/warped |  |  |
| tools/materials/wood/planks/crimson |  |  |
| tools/materials/wood/planks/default |  | tags `m` + `i` + `n` + `e` + `c` + `r` + `a` + `f` + `t` + `:` + `p` + `l` + `a` + `n` + `k` + `s` filled (ignoring `tconstruct:wood_variants/planks`) |
| tools/materials/wood/planks/warped |  |  |
| tools/materials/wood/sticks |  |  |
| tools/materials/wool/black |  |  |
| tools/materials/wool/blue |  |  |
| tools/materials/wool/brown |  |  |
| tools/materials/wool/cyan |  |  |
| tools/materials/wool/gray |  |  |
| tools/materials/wool/green |  |  |
| tools/materials/wool/light_blue |  |  |
| tools/materials/wool/light_gray |  |  |
| tools/materials/wool/lime |  |  |
| tools/materials/wool/magenta |  |  |
| tools/materials/wool/orange |  |  |
| tools/materials/wool/pink |  |  |
| tools/materials/wool/purple |  |  |
| tools/materials/wool/red |  |  |
| tools/materials/wool/white |  |  |
| tools/materials/wool/yellow |  |  |

## `tconstruct:material_fluid` (70)

| Recipe | Result | Condition |
|---|---|---|
| tools/materials/casting/aluminum | `tconstruct:aluminum` | tag `c:ingots/aluminum` filled |
| tools/materials/casting/amethyst_bronze | `tconstruct:amethyst_bronze` |  |
| tools/materials/casting/bronze | `tconstruct:bronze` | any of(tag `c:ingots/bronze` filled; tag `c:ingots/tin` filled) |
| tools/materials/casting/cinderslime | `tconstruct:cinderslime` |  |
| tools/materials/casting/clay | `tconstruct:clay` |  |
| tools/materials/casting/cobalt | `tconstruct:cobalt` |  |
| tools/materials/casting/constantan | `tconstruct:constantan` | any of(tag `c:ingots/constantan` filled; tag `c:ingots/nickel` filled) |
| tools/materials/casting/copper | `tconstruct:copper` |  |
| tools/materials/casting/earthslime | `tconstruct:earthslime` |  |
| tools/materials/casting/electrum | `tconstruct:electrum` | any of(tag `c:ingots/electrum` filled; tag `c:ingots/silver` filled) |
| tools/materials/casting/ender_pearl | `tconstruct:ender_pearl` |  |
| tools/materials/casting/enderslime | `tconstruct:enderslime` |  |
| tools/materials/casting/glass | `tconstruct:glass` |  |
| tools/materials/casting/gold | `tconstruct:gold` |  |
| tools/materials/casting/hepatizon | `tconstruct:hepatizon` |  |
| tools/materials/casting/ichor | `tconstruct:ichor` |  |
| tools/materials/casting/invar | `tconstruct:invar` | any of(tag `c:ingots/invar` filled; tag `c:ingots/nickel` filled) |
| tools/materials/casting/iron | `tconstruct:iron` |  |
| tools/materials/casting/knightmetal | `tconstruct:knightmetal` |  |
| tools/materials/casting/knightslime | `tconstruct:knightslime` |  |
| tools/materials/casting/lead | `tconstruct:lead` | tag `c:ingots/lead` filled |
| tools/materials/casting/magma | `tconstruct:magma` |  |
| tools/materials/casting/manyullyn | `tconstruct:manyullyn` |  |
| tools/materials/casting/nicrosil | `tconstruct:nicrosil` | any of(tag `c:ingots/nicrosil` filled; tag `c:ingots/tin` filled; tag `c:ingots/nickel` filled; tag `c:ingots/chromium` filled) |
| tools/materials/casting/obsidian | `tconstruct:obsidian` |  |
| tools/materials/casting/osmium | `tconstruct:osmium` | tag `c:ingots/osmium` filled |
| tools/materials/casting/pewter | `tconstruct:pewter` | any of(tag `c:ingots/pewter` filled; tag `c:ingots/tin` filled; tag `c:ingots/lead` filled) |
| tools/materials/casting/pig_iron | `tconstruct:pig_iron` |  |
| tools/materials/casting/queens_slime | `tconstruct:queens_slime` |  |
| tools/materials/casting/rose_gold | `tconstruct:rose_gold` |  |
| tools/materials/casting/scorched_stone | `tconstruct:scorched_stone` |  |
| tools/materials/casting/seared_stone | `tconstruct:seared_stone` |  |
| tools/materials/casting/silver | `tconstruct:silver` | tag `c:ingots/silver` filled |
| tools/materials/casting/skyslime | `tconstruct:skyslime` |  |
| tools/materials/casting/slimesteel | `tconstruct:slimesteel` |  |
| tools/materials/casting/steel | `tconstruct:steel` |  |
| tools/materials/casting/steeleaf | `tconstruct:steeleaf` | tag `c:ingots/steeleaf` filled |
| tools/materials/composite/ancient_hide_cleaning | `tconstruct:leather` |  |
| tools/materials/composite/blazewood | `tconstruct:blazewood` |  |
| tools/materials/composite/blazing_bone | `tconstruct:blazing_bone` |  |
| tools/materials/composite/copper_oxidized | `tconstruct:copper#oxidized` |  |
| tools/materials/composite/darkthread | `tconstruct:darkthread` |  |
| tools/materials/composite/fiery | `tconstruct:fiery` | tag `c:ingots/fiery` filled |
| tools/materials/composite/iron_oxidized | `tconstruct:iron#oxidized` |  |
| tools/materials/composite/jadeite | `tconstruct:jadeite` |  |
| tools/materials/composite/jeweled_hide | `tconstruct:jeweled_hide` |  |
| tools/materials/composite/jeweled_hide_cleaning | `tconstruct:leather` |  |
| tools/materials/composite/nahuatl | `tconstruct:nahuatl` |  |
| tools/materials/composite/necronium | `tconstruct:necronium` | tag `c:ingots/uranium` filled |
| tools/materials/composite/plated_slimewood | `tconstruct:plated_slimewood` | any of(tag `c:ingots/brass` filled; tag `c:ingots/zinc` filled) |
| tools/materials/composite/rose_gold | `tconstruct:rose_gold` |  |
| tools/materials/composite/scorched_stone | `tconstruct:scorched_stone` |  |
| tools/materials/composite/seared_stone | `tconstruct:seared_stone` |  |
| tools/materials/composite/slimewood_composite | `tconstruct:slimewood#composite` |  |
| tools/materials/composite/treated_wood | `tconstruct:treated_wood` | tag `c:creosote` filled |
| tools/materials/composite/venombone | `tconstruct:venombone` |  |
| tools/materials/composite/whitestone_from_aluminum | `tconstruct:whitestone#composite` | tag `c:ingots/aluminum` filled |
| tools/materials/composite/whitestone_from_cadmium | `tconstruct:whitestone#composite` | tag `c:ingots/cadmium` filled |
| tools/materials/composite/whitestone_from_chromium | `tconstruct:whitestone#composite` | tag `c:ingots/chromium` filled |
| tools/materials/composite/whitestone_from_nickel | `tconstruct:whitestone#composite` | tag `c:ingots/nickel` filled |
| tools/materials/composite/whitestone_from_tin | `tconstruct:whitestone#composite` | tag `c:ingots/tin` filled |
| tools/materials/composite/whitestone_from_zinc | `tconstruct:whitestone#composite` | tag `c:ingots/zinc` filled |
| tools/materials/slimeskin/composite/earth | `tconstruct:slimeskin` |  |
| tools/materials/slimeskin/composite/earth_cleaning | `tconstruct:leather` |  |
| tools/materials/slimeskin/composite/ender | `tconstruct:enderslime_vine#slimeskin` |  |
| tools/materials/slimeskin/composite/ender_cleaning | `tconstruct:leather` |  |
| tools/materials/slimeskin/composite/ichor | `tconstruct:ichorskin` |  |
| tools/materials/slimeskin/composite/ichor_cleaning | `tconstruct:leather` |  |
| tools/materials/slimeskin/composite/sky | `tconstruct:skyslime_vine#slimeskin` |  |
| tools/materials/slimeskin/composite/sky_cleaning | `tconstruct:leather` |  |

## `tconstruct:material_melting` (46)

| Recipe | Result | Condition |
|---|---|---|
| tools/materials/melting/aluminum | `#c:molten_aluminum` | tag `c:ingots/aluminum` filled |
| tools/materials/melting/amethyst_bronze | `#c:molten_amethyst_bronze` |  |
| tools/materials/melting/ancient_hide | `?` |  |
| tools/materials/melting/bronze | `#c:molten_bronze` | any of(tag `c:ingots/bronze` filled; tag `c:ingots/tin` filled) |
| tools/materials/melting/cinderslime | `?` |  |
| tools/materials/melting/clay | `?` |  |
| tools/materials/melting/cobalt | `#c:molten_cobalt` |  |
| tools/materials/melting/constantan | `#c:molten_constantan` | any of(tag `c:ingots/constantan` filled; tag `c:ingots/nickel` filled) |
| tools/materials/melting/copper | `#c:molten_copper` |  |
| tools/materials/melting/darkthread | `?` |  |
| tools/materials/melting/earthslime | `#c:slime` |  |
| tools/materials/melting/electrum | `#c:molten_electrum` | any of(tag `c:ingots/electrum` filled; tag `c:ingots/silver` filled) |
| tools/materials/melting/ender_pearl | `#c:ender` |  |
| tools/materials/melting/enderslime | `?` |  |
| tools/materials/melting/fiery | `?` | tag `c:ingots/fiery` filled |
| tools/materials/melting/glass | `?` |  |
| tools/materials/melting/gold | `#c:molten_gold` |  |
| tools/materials/melting/hepatizon | `#c:molten_hepatizon` |  |
| tools/materials/melting/ice | `?` |  |
| tools/materials/melting/ichor | `?` |  |
| tools/materials/melting/invar | `#c:molten_invar` | any of(tag `c:ingots/invar` filled; tag `c:ingots/nickel` filled) |
| tools/materials/melting/iron | `#c:molten_iron` |  |
| tools/materials/melting/ironwood | `#c:molten_iron` | tag `c:ingots/ironwood` filled |
| tools/materials/melting/jeweled_hide | `?` |  |
| tools/materials/melting/knightmetal | `?` |  |
| tools/materials/melting/knightslime | `?` |  |
| tools/materials/melting/lead | `#c:molten_lead` | tag `c:ingots/lead` filled |
| tools/materials/melting/magma | `#c:magma` |  |
| tools/materials/melting/manyullyn | `#c:molten_manyullyn` |  |
| tools/materials/melting/nahuatl | `?` |  |
| tools/materials/melting/necronium | `#c:molten_uranium` | tag `c:ingots/uranium` filled |
| tools/materials/melting/nicrosil | `#c:molten_nicrosil` | any of(tag `c:ingots/nicrosil` filled; tag `c:ingots/tin` filled; tag `c:ingots/nickel` filled; tag `c:ingots/chromium` filled) |
| tools/materials/melting/obsidian | `?` |  |
| tools/materials/melting/osmium | `#c:molten_osmium` | tag `c:ingots/osmium` filled |
| tools/materials/melting/pewter | `#c:molten_pewter` | any of(tag `c:ingots/pewter` filled; tag `c:ingots/tin` filled; tag `c:ingots/lead` filled) |
| tools/materials/melting/pig_iron | `?` |  |
| tools/materials/melting/plated_slimewood | `#c:molten_brass` | any of(tag `c:ingots/brass` filled; tag `c:ingots/zinc` filled) |
| tools/materials/melting/queens_slime | `?` |  |
| tools/materials/melting/rose_gold | `#c:molten_rose_gold` |  |
| tools/materials/melting/scorched_stone | `?` |  |
| tools/materials/melting/seared_stone | `?` |  |
| tools/materials/melting/silver | `#c:molten_silver` | tag `c:ingots/silver` filled |
| tools/materials/melting/skyslime | `?` |  |
| tools/materials/melting/slimesteel | `?` |  |
| tools/materials/melting/steel | `#c:molten_steel` |  |
| tools/materials/melting/steeleaf | `?` | tag `c:ingots/steeleaf` filled |

## `tconstruct:melting` (30)

| Recipe | Result | Condition |
|---|---|---|
| compat/ceramics/clay/block | `?` | mod `ceramics` |
| compat/ceramics/clay/bricks_2 | `?` | mod `ceramics` |
| compat/ceramics/clay/bricks_3 | `?` | mod `ceramics` |
| compat/ceramics/clay/clay_1 | `?` | mod `ceramics` |
| compat/ceramics/clay/clay_2 | `?` | mod `ceramics` |
| compat/ceramics/clay/clay_3 | `?` | mod `ceramics` |
| compat/ceramics/clay/gauge | `?` | mod `ceramics` |
| compat/ceramics/clay/kiln | `?` | mod `ceramics` |
| compat/ceramics/clay/lava_bricks_block | `?` | mod `ceramics` |
| compat/ceramics/clay/lava_bricks_slab | `?` | mod `ceramics` |
| compat/ceramics/porcelain/blocks | `?` | mod `ceramics` |
| compat/ceramics/porcelain/bricks_1 | `?` | mod `ceramics` |
| compat/ceramics/porcelain/bricks_2 | `?` | mod `ceramics` |
| compat/ceramics/porcelain/bricks_3 | `?` | mod `ceramics` |
| compat/ceramics/porcelain/gauge | `?` | mod `ceramics` |
| compat/ceramics/porcelain/golden_bricks_block | `?` | mod `ceramics` |
| compat/ceramics/porcelain/golden_bricks_slab | `?` | mod `ceramics` |
| compat/ceramics/porcelain/unfired_1 | `?` | mod `ceramics` |
| compat/ceramics/porcelain/unfired_2 | `?` | mod `ceramics` |
| compat/ceramics/porcelain/unfired_3 | `?` | mod `ceramics` |
| compat/ceramics/porcelain/unfired_4 | `?` | mod `ceramics` |
| smeltery/entity_melting/heads/blaze | `?` |  |
| smeltery/entity_melting/heads/creeper | `?` |  |
| smeltery/entity_melting/heads/drowned | `#c:molten_copper` |  |
| smeltery/entity_melting/heads/ender_dragon | `#c:ender` |  |
| smeltery/entity_melting/heads/enderman | `#c:ender` |  |
| smeltery/entity_melting/heads/piglin | `#c:molten_gold` |  |
| smeltery/entity_melting/heads/skeleton | `?` |  |
| smeltery/entity_melting/heads/spider | `?` |  |
| smeltery/entity_melting/heads/zombie | `#c:molten_iron` |  |

## `tconstruct:melting_fuel` (3)

| Recipe | Result | Condition |
|---|---|---|
| smeltery/melting/fuel/blaze |  |  |
| smeltery/melting/fuel/lava |  |  |
| smeltery/melting/fuel/solid |  |  |

## `tconstruct:modifier_set_worktable` (2)

| Recipe | Result | Condition |
|---|---|---|
| tools/modifiers/worktable/invisible_ink_adding |  |  |
| tools/modifiers/worktable/invisible_ink_removing |  |  |

## `tconstruct:modifier_sorting` (1)

| Recipe | Result | Condition |
|---|---|---|
| tools/modifiers/worktable/modifier_sorting |  |  |

## `tconstruct:molding_table` (58)

| Recipe | Result | Condition |
|---|---|---|
| smeltery/casts/red_sand/molding/adze_head | `tconstruct:adze_head_red_sand_cast` |  |
| smeltery/casts/red_sand/molding/boots_plating | `tconstruct:boots_plating_red_sand_cast` |  |
| smeltery/casts/red_sand/molding/bow_grip | `tconstruct:bow_grip_red_sand_cast` |  |
| smeltery/casts/red_sand/molding/bow_limb | `tconstruct:bow_limb_red_sand_cast` |  |
| smeltery/casts/red_sand/molding/broad_axe_head | `tconstruct:broad_axe_head_red_sand_cast` |  |
| smeltery/casts/red_sand/molding/broad_blade | `tconstruct:broad_blade_red_sand_cast` |  |
| smeltery/casts/red_sand/molding/chestplate_plating | `tconstruct:chestplate_plating_red_sand_cast` |  |
| smeltery/casts/red_sand/molding/coins | `tconstruct:coin_red_sand_cast` | tag `c:coins` filled |
| smeltery/casts/red_sand/molding/gears | `tconstruct:gear_red_sand_cast` | tag `c:gears` filled |
| smeltery/casts/red_sand/molding/gems | `tconstruct:gem_red_sand_cast` |  |
| smeltery/casts/red_sand/molding/hammer_head | `tconstruct:hammer_head_red_sand_cast` |  |
| smeltery/casts/red_sand/molding/helmet_plating | `tconstruct:helmet_plating_red_sand_cast` |  |
| smeltery/casts/red_sand/molding/ingots | `tconstruct:ingot_red_sand_cast` |  |
| smeltery/casts/red_sand/molding/large_plate | `tconstruct:large_plate_red_sand_cast` |  |
| smeltery/casts/red_sand/molding/leggings_plating | `tconstruct:leggings_plating_red_sand_cast` |  |
| smeltery/casts/red_sand/molding/maille | `tconstruct:maille_red_sand_cast` |  |
| smeltery/casts/red_sand/molding/nuggets | `tconstruct:nugget_red_sand_cast` |  |
| smeltery/casts/red_sand/molding/pick_head | `tconstruct:pick_head_red_sand_cast` |  |
| smeltery/casts/red_sand/molding/plates | `tconstruct:plate_red_sand_cast` | tag `c:plates` filled |
| smeltery/casts/red_sand/molding/repair_kit | `tconstruct:repair_kit_red_sand_cast` |  |
| smeltery/casts/red_sand/molding/rods | `tconstruct:rod_red_sand_cast` |  |
| smeltery/casts/red_sand/molding/small_axe_head | `tconstruct:small_axe_head_red_sand_cast` |  |
| smeltery/casts/red_sand/molding/small_blade | `tconstruct:small_blade_red_sand_cast` |  |
| smeltery/casts/red_sand/molding/tool_binding | `tconstruct:tool_binding_red_sand_cast` |  |
| smeltery/casts/red_sand/molding/tool_handle | `tconstruct:tool_handle_red_sand_cast` |  |
| smeltery/casts/red_sand/molding/tough_binding | `tconstruct:tough_binding_red_sand_cast` |  |
| smeltery/casts/red_sand/molding/tough_handle | `tconstruct:tough_handle_red_sand_cast` |  |
| smeltery/casts/red_sand/molding/wires | `tconstruct:wire_red_sand_cast` | tag `c:wires` filled |
| smeltery/casts/sand/molding/adze_head | `tconstruct:adze_head_sand_cast` |  |
| smeltery/casts/sand/molding/boots_plating | `tconstruct:boots_plating_sand_cast` |  |
| smeltery/casts/sand/molding/bow_grip | `tconstruct:bow_grip_sand_cast` |  |
| smeltery/casts/sand/molding/bow_limb | `tconstruct:bow_limb_sand_cast` |  |
| smeltery/casts/sand/molding/broad_axe_head | `tconstruct:broad_axe_head_sand_cast` |  |
| smeltery/casts/sand/molding/broad_blade | `tconstruct:broad_blade_sand_cast` |  |
| smeltery/casts/sand/molding/chestplate_plating | `tconstruct:chestplate_plating_sand_cast` |  |
| smeltery/casts/sand/molding/coins | `tconstruct:coin_sand_cast` | tag `c:coins` filled |
| smeltery/casts/sand/molding/gears | `tconstruct:gear_sand_cast` | tag `c:gears` filled |
| smeltery/casts/sand/molding/gems | `tconstruct:gem_sand_cast` |  |
| smeltery/casts/sand/molding/hammer_head | `tconstruct:hammer_head_sand_cast` |  |
| smeltery/casts/sand/molding/helmet_plating | `tconstruct:helmet_plating_sand_cast` |  |
| smeltery/casts/sand/molding/ingots | `tconstruct:ingot_sand_cast` |  |
| smeltery/casts/sand/molding/large_plate | `tconstruct:large_plate_sand_cast` |  |
| smeltery/casts/sand/molding/leggings_plating | `tconstruct:leggings_plating_sand_cast` |  |
| smeltery/casts/sand/molding/maille | `tconstruct:maille_sand_cast` |  |
| smeltery/casts/sand/molding/nuggets | `tconstruct:nugget_sand_cast` |  |
| smeltery/casts/sand/molding/pick_head | `tconstruct:pick_head_sand_cast` |  |
| smeltery/casts/sand/molding/plates | `tconstruct:plate_sand_cast` | tag `c:plates` filled |
| smeltery/casts/sand/molding/repair_kit | `tconstruct:repair_kit_sand_cast` |  |
| smeltery/casts/sand/molding/rods | `tconstruct:rod_sand_cast` |  |
| smeltery/casts/sand/molding/small_axe_head | `tconstruct:small_axe_head_sand_cast` |  |
| smeltery/casts/sand/molding/small_blade | `tconstruct:small_blade_sand_cast` |  |
| smeltery/casts/sand/molding/tool_binding | `tconstruct:tool_binding_sand_cast` |  |
| smeltery/casts/sand/molding/tool_handle | `tconstruct:tool_handle_sand_cast` |  |
| smeltery/casts/sand/molding/tough_binding | `tconstruct:tough_binding_sand_cast` |  |
| smeltery/casts/sand/molding/tough_handle | `tconstruct:tough_handle_sand_cast` |  |
| smeltery/casts/sand/molding/wires | `tconstruct:wire_sand_cast` | tag `c:wires` filled |
| smeltery/red_sand_cast_pickup | `tconstruct:blank_red_sand_cast` |  |
| smeltery/sand_cast_pickup | `tconstruct:blank_sand_cast` |  |

## `tconstruct:mooshroom_demushrooming` (1)

| Recipe | Result | Condition |
|---|---|---|
| tools/severing/mooshroom_shroom |  |  |

## `tconstruct:multilevel_incremental_modifier` (2)

| Recipe | Result | Condition |
|---|---|---|
| tools/modifiers/upgrade/leaping_from_block | `tconstruct:leaping` |  |
| tools/modifiers/upgrade/leaping_from_crystal | `tconstruct:leaping` |  |

## `tconstruct:overslime_modifier` (16)

| Recipe | Result | Condition |
|---|---|---|
| tools/modifiers/slotless/overslime/earth_ball |  |  |
| tools/modifiers/slotless/overslime/earth_block |  |  |
| tools/modifiers/slotless/overslime/earth_bottle |  |  |
| tools/modifiers/slotless/overslime/earth_congealed |  |  |
| tools/modifiers/slotless/overslime/ender_ball |  |  |
| tools/modifiers/slotless/overslime/ender_block |  |  |
| tools/modifiers/slotless/overslime/ender_bottle |  |  |
| tools/modifiers/slotless/overslime/ender_congealed |  |  |
| tools/modifiers/slotless/overslime/ichor_ball |  |  |
| tools/modifiers/slotless/overslime/ichor_block |  |  |
| tools/modifiers/slotless/overslime/ichor_bottle |  |  |
| tools/modifiers/slotless/overslime/ichor_congealed |  |  |
| tools/modifiers/slotless/overslime/sky_ball |  |  |
| tools/modifiers/slotless/overslime/sky_block |  |  |
| tools/modifiers/slotless/overslime/sky_bottle |  |  |
| tools/modifiers/slotless/overslime/sky_congealed |  |  |

## `tconstruct:part_builder` (24)

| Recipe | Result | Condition |
|---|---|---|
| tools/parts/builder/adze_head | `tconstruct:adze_head` |  |
| tools/parts/builder/arrow_head | `tconstruct:arrow_head` |  |
| tools/parts/builder/arrow_shaft | `tconstruct:arrow_shaft` |  |
| tools/parts/builder/bow_grip | `tconstruct:bow_grip` |  |
| tools/parts/builder/bow_limb | `tconstruct:bow_limb` |  |
| tools/parts/builder/bowstring | `tconstruct:bowstring` |  |
| tools/parts/builder/broad_axe_head | `tconstruct:broad_axe_head` |  |
| tools/parts/builder/broad_blade | `tconstruct:broad_blade` |  |
| tools/parts/builder/fletching | `tconstruct:fletching` |  |
| tools/parts/builder/hammer_head | `tconstruct:hammer_head` |  |
| tools/parts/builder/laces | `tconstruct:laces` |  |
| tools/parts/builder/large_plate | `tconstruct:large_plate` |  |
| tools/parts/builder/maille | `tconstruct:maille` |  |
| tools/parts/builder/pick_head | `tconstruct:pick_head` |  |
| tools/parts/builder/repair_kit | `tconstruct:repair_kit` |  |
| tools/parts/builder/ribcage | `tconstruct:ribcage` |  |
| tools/parts/builder/shell | `tconstruct:shell` |  |
| tools/parts/builder/shield_core | `tconstruct:shield_core` |  |
| tools/parts/builder/small_axe_head | `tconstruct:small_axe_head` |  |
| tools/parts/builder/small_blade | `tconstruct:small_blade` |  |
| tools/parts/builder/tool_binding | `tconstruct:tool_binding` |  |
| tools/parts/builder/tool_handle | `tconstruct:tool_handle` |  |
| tools/parts/builder/tough_binding | `tconstruct:tough_binding` |  |
| tools/parts/builder/tough_handle | `tconstruct:tough_handle` |  |

## `tconstruct:part_builder_recycling` (37)

| Recipe | Result | Condition |
|---|---|---|
| tables/recycling/bow |  |  |
| tables/recycling/crossbow |  |  |
| tables/recycling/fishing_rod |  |  |
| tables/recycling/flint_and_steel |  |  |
| tables/recycling/leather_boots |  |  |
| tables/recycling/leather_chestplate |  |  |
| tables/recycling/leather_helmet |  |  |
| tables/recycling/leather_leggings |  |  |
| tables/recycling/stone_axe |  |  |
| tables/recycling/stone_shovel |  |  |
| tables/recycling/stone_sword |  |  |
| tables/recycling/turtle_helmet |  |  |
| tables/recycling/twilightforest/arctic_boots |  | mod `twilightforest` |
| tables/recycling/twilightforest/arctic_chestplate |  | mod `twilightforest` |
| tables/recycling/twilightforest/arctic_helmet |  | mod `twilightforest` |
| tables/recycling/twilightforest/arctic_leggings |  | mod `twilightforest` |
| tables/recycling/twilightforest/ironwood_axe |  | mod `twilightforest` |
| tables/recycling/twilightforest/ironwood_boots |  | mod `twilightforest` |
| tables/recycling/twilightforest/ironwood_chestplate |  | mod `twilightforest` |
| tables/recycling/twilightforest/ironwood_helmet |  | mod `twilightforest` |
| tables/recycling/twilightforest/ironwood_leggings |  | mod `twilightforest` |
| tables/recycling/twilightforest/ironwood_shovel |  | mod `twilightforest` |
| tables/recycling/twilightforest/ironwood_sword |  | mod `twilightforest` |
| tables/recycling/twilightforest/naga_chestplate |  | mod `twilightforest` |
| tables/recycling/twilightforest/naga_leggings |  | mod `twilightforest` |
| tables/recycling/twilightforest/yeti_boots |  | mod `twilightforest` |
| tables/recycling/twilightforest/yeti_chestplate |  | mod `twilightforest` |
| tables/recycling/twilightforest/yeti_helmet |  | mod `twilightforest` |
| tables/recycling/twilightforest/yeti_leggings |  | mod `twilightforest` |
| tables/recycling/wooden_axe |  |  |
| tables/recycling/wooden_shovel |  |  |
| tables/recycling/wooden_sword |  |  |
| tools/recycling/earth_staff |  |  |
| tools/recycling/ender_staff |  |  |
| tools/recycling/flint_and_brick |  |  |
| tools/recycling/ichor_staff |  |  |
| tools/recycling/sky_staff |  |  |

## `tconstruct:part_builder_tool_recycling` (10)

| Recipe | Result | Condition |
|---|---|---|
| tools/recycling/battlesign |  |  |
| tools/recycling/dagger |  |  |
| tools/recycling/general |  |  |
| tools/recycling/melting_pan |  |  |
| tools/recycling/minotaur_axe |  | mod `twilightforest` |
| tools/recycling/plate_shield |  |  |
| tools/recycling/swasher |  |  |
| tools/recycling/travelers_gear |  |  |
| tools/recycling/travelers_shield |  |  |
| tools/recycling/war_pick |  |  |

## `tconstruct:player_beheading` (1)

| Recipe | Result | Condition |
|---|---|---|
| tools/severing/player_head |  |  |

## `tconstruct:remove_modifier` (2)

| Recipe | Result | Condition |
|---|---|---|
| tools/modifiers/worktable/remove_modifier_sponge |  |  |
| tools/modifiers/worktable/remove_modifier_venom |  |  |

## `tconstruct:severing` (31)

| Recipe | Result | Condition |
|---|---|---|
| tools/severing/blaze_head | `tconstruct:blaze_head` |  |
| tools/severing/blaze_rod | 2 `minecraft:blaze_rod` |  |
| tools/severing/cave_spider_head | `tconstruct:cave_spider_head` |  |
| tools/severing/cobweb | `minecraft:cobweb` |  |
| tools/severing/creeper_head | `minecraft:creeper_head` |  |
| tools/severing/creeper_tnt | `minecraft:tnt` |  |
| tools/severing/drowned_head | `tconstruct:drowned_head` |  |
| tools/severing/earthslime_ball | `minecraft:slime_ball` |  |
| tools/severing/ender_dragon_head | `minecraft:dragon_head` |  |
| tools/severing/enderman_head | `tconstruct:enderman_head` |  |
| tools/severing/enderslime_ball | `tconstruct:ender_slime_ball` |  |
| tools/severing/guardian_shard | 2 `minecraft:prismarine_shard` |  |
| tools/severing/husk_head | `tconstruct:husk_head` |  |
| tools/severing/iron_golem_head | `minecraft:carved_pumpkin` |  |
| tools/severing/magma_cream | `minecraft:magma_cream` |  |
| tools/severing/phantom_membrane | `minecraft:phantom_membrane` |  |
| tools/severing/piglin_brute_head | `tconstruct:piglin_brute_head` |  |
| tools/severing/piglin_head | `minecraft:piglin_head` |  |
| tools/severing/shulker_shell | `minecraft:shulker_shell` |  |
| tools/severing/skeleton_bone | 2 `minecraft:bone` |  |
| tools/severing/skeleton_skull | `minecraft:skeleton_skull` |  |
| tools/severing/skyslime_ball | `tconstruct:sky_slime_ball` |  |
| tools/severing/spider_eye | `minecraft:spider_eye` |  |
| tools/severing/spider_head | `tconstruct:spider_head` |  |
| tools/severing/stray_head | `tconstruct:stray_head` |  |
| tools/severing/terracube_clay | `minecraft:clay_ball` |  |
| tools/severing/wither_salvage | `minecraft:wither_skeleton_skull` |  |
| tools/severing/wither_skeleton_bone | 2 `tconstruct:necrotic_bone` |  |
| tools/severing/wither_skeleton_skull | `minecraft:wither_skeleton_skull` |  |
| tools/severing/zombie_head | `minecraft:zombie_head` |  |
| tools/severing/zombified_piglin_head | `tconstruct:zombified_piglin_head` |  |

## `tconstruct:sheep_shearing` (1)

| Recipe | Result | Condition |
|---|---|---|
| tools/severing/sheep_wool |  |  |

## `tconstruct:snow_golem_beheading` (1)

| Recipe | Result | Condition |
|---|---|---|
| tools/severing/snow_golem_head |  |  |

## `tconstruct:table_casting_composite` (26)

| Recipe | Result | Condition |
|---|---|---|
| tools/parts/casting/adze_head_composite | `tconstruct:adze_head` |  |
| tools/parts/casting/boots_plating_composite | `tconstruct:boots_plating` |  |
| tools/parts/casting/bow_grip_composite | `tconstruct:bow_grip` |  |
| tools/parts/casting/bow_limb_composite | `tconstruct:bow_limb` |  |
| tools/parts/casting/bowstring_composite | `tconstruct:bowstring` |  |
| tools/parts/casting/broad_axe_head_composite | `tconstruct:broad_axe_head` |  |
| tools/parts/casting/broad_blade_composite | `tconstruct:broad_blade` |  |
| tools/parts/casting/chestplate_plating_composite | `tconstruct:chestplate_plating` |  |
| tools/parts/casting/fake_ingot_composite | `tconstruct:fake_ingot` |  |
| tools/parts/casting/hammer_head_composite | `tconstruct:hammer_head` |  |
| tools/parts/casting/helmet_plating_composite | `tconstruct:helmet_plating` |  |
| tools/parts/casting/laces_composite | `tconstruct:laces` |  |
| tools/parts/casting/large_plate_composite | `tconstruct:large_plate` |  |
| tools/parts/casting/leggings_plating_composite | `tconstruct:leggings_plating` |  |
| tools/parts/casting/maille_composite | `tconstruct:maille` |  |
| tools/parts/casting/pick_head_composite | `tconstruct:pick_head` |  |
| tools/parts/casting/repair_kit_composite | `tconstruct:repair_kit` |  |
| tools/parts/casting/ribcage_composite | `tconstruct:ribcage` |  |
| tools/parts/casting/shell_composite | `tconstruct:shell` |  |
| tools/parts/casting/shield_core_composite | `tconstruct:shield_core` |  |
| tools/parts/casting/small_axe_head_composite | `tconstruct:small_axe_head` |  |
| tools/parts/casting/small_blade_composite | `tconstruct:small_blade` |  |
| tools/parts/casting/tool_binding_composite | `tconstruct:tool_binding` |  |
| tools/parts/casting/tool_handle_composite | `tconstruct:tool_handle` |  |
| tools/parts/casting/tough_binding_composite | `tconstruct:tough_binding` |  |
| tools/parts/casting/tough_handle_composite | `tconstruct:tough_handle` |  |

## `tconstruct:table_casting_material` (42)

| Recipe | Result | Condition |
|---|---|---|
| tools/parts/casting/adze_head_gold_cast | `tconstruct:adze_head` |  |
| tools/parts/casting/adze_head_sand_cast | `tconstruct:adze_head` |  |
| tools/parts/casting/boots_plating_gold_cast | `tconstruct:boots_plating` |  |
| tools/parts/casting/boots_plating_sand_cast | `tconstruct:boots_plating` |  |
| tools/parts/casting/bow_grip_gold_cast | `tconstruct:bow_grip` |  |
| tools/parts/casting/bow_grip_sand_cast | `tconstruct:bow_grip` |  |
| tools/parts/casting/bow_limb_gold_cast | `tconstruct:bow_limb` |  |
| tools/parts/casting/bow_limb_sand_cast | `tconstruct:bow_limb` |  |
| tools/parts/casting/broad_axe_head_gold_cast | `tconstruct:broad_axe_head` |  |
| tools/parts/casting/broad_axe_head_sand_cast | `tconstruct:broad_axe_head` |  |
| tools/parts/casting/broad_blade_gold_cast | `tconstruct:broad_blade` |  |
| tools/parts/casting/broad_blade_sand_cast | `tconstruct:broad_blade` |  |
| tools/parts/casting/chestplate_plating_gold_cast | `tconstruct:chestplate_plating` |  |
| tools/parts/casting/chestplate_plating_sand_cast | `tconstruct:chestplate_plating` |  |
| tools/parts/casting/fake_ingot_gold_cast | `tconstruct:fake_ingot` |  |
| tools/parts/casting/fake_ingot_sand_cast | `tconstruct:fake_ingot` |  |
| tools/parts/casting/hammer_head_gold_cast | `tconstruct:hammer_head` |  |
| tools/parts/casting/hammer_head_sand_cast | `tconstruct:hammer_head` |  |
| tools/parts/casting/helmet_plating_gold_cast | `tconstruct:helmet_plating` |  |
| tools/parts/casting/helmet_plating_sand_cast | `tconstruct:helmet_plating` |  |
| tools/parts/casting/large_plate_gold_cast | `tconstruct:large_plate` |  |
| tools/parts/casting/large_plate_sand_cast | `tconstruct:large_plate` |  |
| tools/parts/casting/leggings_plating_gold_cast | `tconstruct:leggings_plating` |  |
| tools/parts/casting/leggings_plating_sand_cast | `tconstruct:leggings_plating` |  |
| tools/parts/casting/maille_gold_cast | `tconstruct:maille` |  |
| tools/parts/casting/maille_sand_cast | `tconstruct:maille` |  |
| tools/parts/casting/pick_head_gold_cast | `tconstruct:pick_head` |  |
| tools/parts/casting/pick_head_sand_cast | `tconstruct:pick_head` |  |
| tools/parts/casting/repair_kit_gold_cast | `tconstruct:repair_kit` |  |
| tools/parts/casting/repair_kit_sand_cast | `tconstruct:repair_kit` |  |
| tools/parts/casting/small_axe_head_gold_cast | `tconstruct:small_axe_head` |  |
| tools/parts/casting/small_axe_head_sand_cast | `tconstruct:small_axe_head` |  |
| tools/parts/casting/small_blade_gold_cast | `tconstruct:small_blade` |  |
| tools/parts/casting/small_blade_sand_cast | `tconstruct:small_blade` |  |
| tools/parts/casting/tool_binding_gold_cast | `tconstruct:tool_binding` |  |
| tools/parts/casting/tool_binding_sand_cast | `tconstruct:tool_binding` |  |
| tools/parts/casting/tool_handle_gold_cast | `tconstruct:tool_handle` |  |
| tools/parts/casting/tool_handle_sand_cast | `tconstruct:tool_handle` |  |
| tools/parts/casting/tough_binding_gold_cast | `tconstruct:tough_binding` |  |
| tools/parts/casting/tough_binding_sand_cast | `tconstruct:tough_binding` |  |
| tools/parts/casting/tough_handle_gold_cast | `tconstruct:tough_handle` |  |
| tools/parts/casting/tough_handle_sand_cast | `tconstruct:tough_handle` |  |

## `tconstruct:table_casting_part_swapping` (10)

| Recipe | Result | Condition |
|---|---|---|
| tools/armor/plate/boots_swapping |  |  |
| tools/armor/plate/chestplate_swapping |  |  |
| tools/armor/plate/helmet_swapping |  |  |
| tools/armor/plate/leggings_swapping |  |  |
| tools/armor/travelers/boots_leather |  |  |
| tools/armor/travelers/chestplate_leather |  |  |
| tools/armor/travelers/goggles_leather |  |  |
| tools/armor/travelers/pants_leather |  |  |
| tools/armor/travelers/shield_leather |  |  |
| tools/armor/travelers/swapping_metal |  |  |

## `tconstruct:table_duplication` (1)

| Recipe | Result | Condition |
|---|---|---|
| smeltery/casting/diamond/smithing_template |  |  |

## `tconstruct:table_filling` (6)

| Recipe | Result | Condition |
|---|---|---|
| compat/ceramics/filling_clay_bucket |  | mod `ceramics` |
| compat/ceramics/filling_cracked_clay_bucket |  | mod `ceramics` |
| smeltery/casting/filling/bucket |  |  |
| smeltery/casting/filling/copper_can |  |  |
| smeltery/casting/filling/scorched_lantern_pixel |  |  |
| smeltery/casting/filling/seared_lantern_pixel |  |  |

## `tconstruct:table_tool_casting` (1)

| Recipe | Result | Condition |
|---|---|---|
| tools/armor/plate/plate_shield | `tconstruct:plate_shield` |  |

## `tconstruct:tinker_station_damaging` (6)

| Recipe | Result | Condition |
|---|---|---|
| tables/tinker_station_damaging/blazing_bucket |  |  |
| tables/tinker_station_damaging/lava_bucket |  |  |
| tables/tinker_station_damaging/magma_bottle |  |  |
| tables/tinker_station_damaging/magma_bucket |  |  |
| tables/tinker_station_damaging/venom_bottle |  |  |
| tables/tinker_station_damaging/venom_bucket |  |  |

## `tconstruct:tinker_station_part_swapping` (3)

| Recipe | Result | Condition |
|---|---|---|
| tables/ammo_part_swapping |  |  |
| tables/throwing_axe_part_swapping |  |  |
| tables/tinker_station_part_swapping |  |  |

## `tconstruct:tinker_station_repair` (1)

| Recipe | Result | Condition |
|---|---|---|
| tables/tinker_station_repair |  |  |

## `tconstruct:tipped_tool_transform` (1)

| Recipe | Result | Condition |
|---|---|---|
| tools/building/arrow_from_tipped | `tconstruct:arrow` |  |

## `tconstruct:toggle_interaction` (1)

| Recipe | Result | Condition |
|---|---|---|
| tools/modifiers/worktable/toggle_interaction_modifier |  |  |

## `tconstruct:tool_building` (25)

| Recipe | Result | Condition |
|---|---|---|
| tools/armor/plate/plate_boots | `tconstruct:plate_boots` |  |
| tools/armor/plate/plate_chestplate | `tconstruct:plate_chestplate` |  |
| tools/armor/plate/plate_helmet | `tconstruct:plate_helmet` |  |
| tools/armor/plate/plate_leggings | `tconstruct:plate_leggings` |  |
| tools/building/arrow | `tconstruct:arrow` |  |
| tools/building/arrow_from_vanilla | `tconstruct:arrow` |  |
| tools/building/broad_axe | `tconstruct:broad_axe` |  |
| tools/building/cleaver | `tconstruct:cleaver` |  |
| tools/building/crossbow | `tconstruct:crossbow` |  |
| tools/building/dagger | `tconstruct:dagger` |  |
| tools/building/excavator | `tconstruct:excavator` |  |
| tools/building/fishing_rod | `tconstruct:fishing_rod` |  |
| tools/building/hand_axe | `tconstruct:hand_axe` |  |
| tools/building/javelin | `tconstruct:javelin` |  |
| tools/building/kama | `tconstruct:kama` |  |
| tools/building/longbow | `tconstruct:longbow` |  |
| tools/building/mattock | `tconstruct:mattock` |  |
| tools/building/pickadze | `tconstruct:pickadze` |  |
| tools/building/pickaxe | `tconstruct:pickaxe` |  |
| tools/building/scythe | `tconstruct:scythe` |  |
| tools/building/shuriken | `tconstruct:shuriken` |  |
| tools/building/sledge_hammer | `tconstruct:sledge_hammer` |  |
| tools/building/sword | `tconstruct:sword` |  |
| tools/building/throwing_axe | `tconstruct:throwing_axe` |  |
| tools/building/vein_hammer | `tconstruct:vein_hammer` |  |

## `tconstruct:tool_material_swapping` (1)

| Recipe | Result | Condition |
|---|---|---|
| tables/tool_material_swapping |  |  |

