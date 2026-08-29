# Modifier recipes

Recipes applied at the tinker station or anvil. Slots name the slot type consumed
per level. Incremental modifiers accept partial inputs until the amount per level is
reached. Salvage rows are omitted: every modifier here has a matching salvage recipe
returning its slots unless noted.

## modifiers

| Recipe | Modifier | Levels | Inputs | Slots | Tools | Condition |
|---|---|---|---|---|---|---|
| tasty | `` |  | `tconstruct:bacon` |  |  |  |

## modifiers/ability

| Recipe | Modifier | Levels | Inputs | Slots | Tools | Condition |
|---|---|---|---|---|---|---|
| ambidextrous | `tconstruct:ambidextrous` | 1 | `minecraft:leather`; `#c:gems/diamond`; `minecraft:leather`; `#c:strings`; `#c:strings` | 1 abilities | `#tconstruct:modifiable/melee/unarmed` |  |
| aqua_affinity | `tconstruct:aqua_affinity` | 1 | `minecraft:prismarine_bricks`; `minecraft:heart_of_the_sea`; `minecraft:prismarine_bricks`; `minecraft:dark_prismarine`; `minecraft:dark_prismarine` | 1 abilities | `#tconstruct:modifiable/armor/helmets` |  |
| autosmelt | `tconstruct:autosmelt` | 1 | `#c:raw_materials`; `minecraft:blast_furnace`; `#c:ingots`; `#c:storage_blocks/coal`; `#c:storage_blocks/coal` | 1 abilities | `#tconstruct:modifiable/harvest` / `#tconstruct:modifiable/fishing_rods` |  |
| ballista | `tconstruct:ballista` | 1 | `#c:ingots/hepatizon`; `minecraft:chain`; `#c:ingots/hepatizon` | 1 abilities | `#tconstruct:modifiable/ranged/ballistas` |  |
| blocking | `tconstruct:blocking` | 1 | `#minecraft:planks`; `#c:ingots/steel`; `#minecraft:planks`; `#minecraft:planks`; `#minecraft:planks` | 1 abilities | `{"fabric:type":"fabric:difference","base":{"fabric:type":"fabric:all","ingredients":[{"tag":"tconstruct:modifiable/interactable/charge"},{"tag":"tconstruct:modifiable/durability"}]},"subtracted":[{"tag":"tconstruct:modifiable/melee/parry"},{"tag":"tconstruct:modifiable/shields"}]}` |  |
| bonking | `tconstruct:bonking` | 1 | `minecraft:weeping_vines`; `tconstruct:ichor_slime_crystal`; `minecraft:weeping_vines`; `tconstruct:ichor_congealed_slime`; `tconstruct:ichor_congealed_slime` | 1 abilities | `{"fabric:type":"fabric:all","ingredients":[{"tag":"tconstruct:modifiable/durability"},{"tag":"tconstruct:modifiable/interactable/charge"}]}` |  |
| bouncy | `tconstruct:bouncy` | 1 | `tconstruct:sky_congealed_slime`; `tconstruct:ichor_congealed_slime`; `tconstruct:sky_congealed_slime`; `tconstruct:earth_congealed_slime`; `tconstruct:earth_congealed_slime` | 1 abilities | `#tconstruct:modifiable/armor/boots` |  |
| boundless | `tconstruct:boundless` | 1 | `tconstruct:obsidian_pane`; `minecraft:writable_book`; `tconstruct:obsidian_pane`; `tconstruct:ichor_slime_crystal`; `tconstruct:ichor_slime_crystal` | 1 abilities | `#tconstruct:modifiable/shields` |  |
| brushing | `tconstruct:brushing` | 1 | `#c:feathers`; `#c:ingots/copper` | 1 abilities | `{"fabric:type":"fabric:all","ingredients":[{"tag":"tconstruct:modifiable/durability"},{"tag":"tconstruct:modifiable/interactable/right"}]}` |  |
| bucketing | `tconstruct:bucketing` | 1 | `{"ingredient":[{"item":"tconstruct:seared_faucet"},{"item":"tconstruct:scorched_faucet"}]}`; `minecraft:bucket`; `{"ingredient":[{"item":"tconstruct:seared_faucet"},{"item":"tconstruct:scorched_faucet"}]}`; `#c:ingots/steel`; `#c:ingots/steel` | 1 abilities | `#tconstruct:modifiable/interactable` |  |
| bulk_quiver | `tconstruct:bulk_quiver` |  | `minecraft:leather`; `tconstruct:sky_slime_vine`; `minecraft:leather`; `tconstruct:sky_slime_vine`; `tconstruct:sky_slime_vine` | 1 abilities | `#tconstruct:modifiable/ranged/bows` |  |
| bursting | `tconstruct:bursting` |  | `minecraft:cactus`; `#tconstruct:tanks`; `minecraft:cactus`; `#c:ingots/copper`; `#c:ingots/copper` | 1 abilities | `#tconstruct:modifiable/armor/chestplate` / `#tconstruct:modifiable/shields` |  |
| channeling | `tconstruct:channeling` | 1 | `minecraft:lightning_rod`; `minecraft:creeper_head`; `minecraft:lightning_rod`; `minecraft:lightning_rod`; `minecraft:lightning_rod` | 1 abilities | `#tconstruct:modifiable/melee/weapon` / `#tconstruct:modifiable/fishing_rods` |  |
| crafting_table | `tconstruct:crafting_table` | 1 | `minecraft:leather`; `tconstruct:crafting_station`; `minecraft:leather` | 1 abilities | `#tconstruct:modifiable/armor/leggings` |  |
| crystalshot_amethyst | `tconstruct:crystalshot` |  | `minecraft:amethyst_cluster`; `minecraft:blaze_rod`; `minecraft:amethyst_cluster`; `#c:ingots/manyullyn`; `#c:ingots/manyullyn` | 1 abilities | `#tconstruct:modifiable/ranged/bows` |  |
| crystalshot_earthslime | `tconstruct:crystalshot` |  | `tconstruct:earth_slime_crystal_cluster`; `minecraft:blaze_rod`; `tconstruct:earth_slime_crystal_cluster`; `#c:ingots/manyullyn`; `#c:ingots/manyullyn` | 1 abilities | `#tconstruct:modifiable/ranged/bows` |  |
| crystalshot_enderslime | `tconstruct:crystalshot` |  | `tconstruct:ender_slime_crystal_cluster`; `minecraft:blaze_rod`; `tconstruct:ender_slime_crystal_cluster`; `#c:ingots/manyullyn`; `#c:ingots/manyullyn` | 1 abilities | `#tconstruct:modifiable/ranged/bows` |  |
| crystalshot_ichor | `tconstruct:crystalshot` |  | `tconstruct:ichor_slime_crystal_cluster`; `minecraft:blaze_rod`; `tconstruct:ichor_slime_crystal_cluster`; `#c:ingots/manyullyn`; `#c:ingots/manyullyn` | 1 abilities | `#tconstruct:modifiable/ranged/bows` |  |
| crystalshot_quartz | `tconstruct:crystalshot` |  | `minecraft:nether_quartz_ore`; `minecraft:blaze_rod`; `minecraft:nether_quartz_ore`; `#c:ingots/manyullyn`; `#c:ingots/manyullyn` | 1 abilities | `#tconstruct:modifiable/ranged/bows` |  |
| crystalshot_random | `tconstruct:crystalshot` |  | `{"ingredient":[{"item":"tconstruct:earth_slime_crystal_cluster"},{"item":"tconstruct:sky_slime_crystal_cluster"}]}`; `{"ingredient":[{"item":"minecraft:amethyst_cluster"},{"item":"minecraft:nether_quartz_ore"}]}`; `{"ingredient":[{"item":"tconstruct:ichor_slime_crystal_cluster"},{"item":"tconstruct:ender_slime_crystal_cluster"}]}`; `#c:ingots/manyullyn`; `#c:ingots/manyullyn` | 1 abilities | `#tconstruct:modifiable/ranged/bows` |  |
| crystalshot_skyslime | `tconstruct:crystalshot` |  | `tconstruct:sky_slime_crystal_cluster`; `minecraft:blaze_rod`; `tconstruct:sky_slime_crystal_cluster`; `#c:ingots/manyullyn`; `#c:ingots/manyullyn` | 1 abilities | `#tconstruct:modifiable/ranged/bows` |  |
| double_jump | `tconstruct:double_jump` | 1-2 | `minecraft:piston`; `tconstruct:sky_slime`; `minecraft:piston`; `minecraft:phantom_membrane`; `minecraft:phantom_membrane` | 1 abilities | `#tconstruct:modifiable/armor/boots` |  |
| drill_attack | `tconstruct:drill_attack` | 1 | `tconstruct:blazing_bone`; `minecraft:pointed_dripstone`; `tconstruct:blazing_bone` | 1 abilities | `#tconstruct:modifiable/interactable/charge` / `#tconstruct:modifiable/fishing_rods` |  |
| dual_wielding | `tconstruct:dual_wielding` | 1 | `#c:ingots/slimesteel`; `minecraft:nautilus_shell`; `#c:ingots/slimesteel` | 1 abilities | `{"fabric:type":"fabric:difference","base":{"fabric:type":"fabric:all","ingredients":[{"tag":"tconstruct:modifiable/melee/weapon"},{"tag":"tconstruct:modifiable/interactable/right"}]},"subtracted":{"item":"tconstruct:dagger"}}` |  |
| exchanging | `tconstruct:exchanging` | 1 | `minecraft:sticky_piston`; `#c:ingots/hepatizon`; `minecraft:sticky_piston`; `#c:ender_pearls`; `#c:ender_pearls` | 1 abilities | `#tconstruct:modifiable/harvest` |  |
| expanded | `tconstruct:expanded` |  | `minecraft:piston`; `#c:ingots/amethyst_bronze`; `minecraft:piston`; `#c:slimeball/ichor`; `#c:slimeball/ichor` | 1 abilities | `#tconstruct:modifiable/aoe` |  |
| firestarter | `tconstruct:firestarter` | 1 | `#c:ingots/steel`; `minecraft:flint` | 1 abilities | `{"fabric:type":"fabric:all","ingredients":[{"tag":"tconstruct:modifiable/durability"},{"tag":"tconstruct:modifiable/interactable"}]}` |  |
| flamewake | `tconstruct:flamewake` | 1 | `minecraft:flint`; `#c:ingots/netherite_scrap`; `minecraft:flint`; `minecraft:flint`; `minecraft:flint` | 1 abilities | `{"fabric:type":"fabric:all","ingredients":[{"tag":"tconstruct:modifiable/armor/boots"},{"tag":"tconstruct:modifiable/durability"}]}` |  |
| flinging | `tconstruct:flinging` | 1 | `minecraft:vine`; `tconstruct:earth_slime_crystal`; `minecraft:vine`; `tconstruct:earth_congealed_slime`; `tconstruct:earth_congealed_slime` | 1 abilities | `{"fabric:type":"fabric:all","ingredients":[{"tag":"tconstruct:modifiable/durability"},{"tag":"tconstruct:modifiable/interactable/charge"}]}` |  |
| frost_walker | `tconstruct:frost_walker` | 1 | `minecraft:blue_ice`; `tconstruct:stray_head`; `minecraft:blue_ice`; `minecraft:blue_ice`; `minecraft:blue_ice` | 1 abilities | `#tconstruct:modifiable/armor/boots` |  |
| gilded | `tconstruct:gilded` |  | `minecraft:gilded_blackstone` | 1 abilities | `#tconstruct:modifiable/bonus_slots` |  |
| glowing | `tconstruct:glowing` | 1 | `minecraft:glowstone`; `minecraft:daylight_detector`; `minecraft:shroomlight` | 1 abilities | `{"fabric:type":"fabric:all","ingredients":[{"tag":"tconstruct:modifiable/durability"},[{"tag":"tconstruct:modifiable/interactable"},{"tag":"tconstruct:modifiable/armor/boots"}]]}` |  |
| grapple | `tconstruct:grapple` | 1 | `minecraft:chain`; `minecraft:chain`; `#c:ingots/slimesteel` | 1 abilities | `#tconstruct:modifiable/fishing_rods` |  |
| luck_level_1 | `tconstruct:luck` | 1 | `#c:ingots/copper`; `{"ingredient":[{"item":"minecraft:cornflower"},{"item":"minecraft:blue_orchid"}]}`; `#c:ingots/copper`; `#c:storage_blocks/lapis`; `#c:storage_blocks/lapis` | 1 abilities | `#tconstruct:modifiable/melee/weapon` / `#tconstruct:modifiable/harvest` / `#tconstruct:modifiable/ranged/launcher` |  |
| luck_level_2 | `tconstruct:luck` | 2 | `#c:ingots/gold`; `minecraft:golden_carrot`; `#c:ingots/gold`; `#c:ender_pearls`; `#c:ender_pearls` |  | `#tconstruct:modifiable/melee/weapon` / `#tconstruct:modifiable/harvest` / `#tconstruct:modifiable/ranged/launcher` |  |
| luck_level_3 | `tconstruct:luck` | 3 | `#c:ingots/rose_gold`; `minecraft:rabbit_foot`; `#c:ingots/rose_gold`; `#c:gems/diamond`; `minecraft:name_tag` |  | `#tconstruct:modifiable/melee/weapon` / `#tconstruct:modifiable/harvest` / `#tconstruct:modifiable/ranged/launcher` |  |
| melting | `tconstruct:melting` | 1 | `minecraft:blaze_rod`; `{"ingredient":[{"item":"tconstruct:seared_melter"},{"item":"tconstruct:smeltery_controller"},{"item":"tconstruct:foundry_controller"}]}`; `minecraft:blaze_rod`; `minecraft:lava_bucket`; `minecraft:lava_bucket` | 1 abilities | `#tconstruct:modifiable/melee` / `#tconstruct:modifiable/harvest` |  |
| multishot | `tconstruct:multishot` |  | `minecraft:piston`; `#c:ingots/steel`; `minecraft:piston`; `#c:slimeball/ichor`; `#c:slimeball/ichor` | 1 abilities | `#tconstruct:modifiable/ranged/crossbows` |  |
| parrying | `tconstruct:parrying` | 1 | `#minecraft:planks`; `#c:ingots/steel`; `#minecraft:planks` | 1 abilities | `#tconstruct:modifiable/melee/parry` |  |
| pathing | `tconstruct:pathing` | 1 | `tconstruct:adze_head`; `#c:ingots/steel`; `tconstruct:tool_binding` | 1 abilities | `{"fabric:type":"fabric:all","ingredients":[{"tag":"tconstruct:modifiable/durability"},[{"tag":"tconstruct:modifiable/interactable"},{"tag":"tconstruct:modifiable/armor/boots"}]]}` |  |
| pockets | `tconstruct:pockets` | 1-2 | `minecraft:shulker_shell`; `#c:ingots/iron`; `minecraft:shulker_shell`; `minecraft:leather`; `minecraft:leather` | 1 abilities | `#tconstruct:modifiable/armor/leggings` |  |
| protection | `tconstruct:protection` | 1 | `tconstruct:gold_reinforcement`; `tconstruct:seared_reinforcement`; `tconstruct:obsidian_reinforcement`; `tconstruct:iron_reinforcement`; `tconstruct:cobalt_reinforcement` | 1 abilities | `#tconstruct:modifiable/armor` |  |
| reach | `tconstruct:reach` | 1-2 | `minecraft:piston`; `#c:ingots/queens_slime`; `minecraft:piston`; `#c:slimeball/ender`; `#c:slimeball/ender` | 1 abilities | `#tconstruct:modifiable/armor/chestplate` |  |
| reflecting | `tconstruct:reflecting` |  | `tconstruct:sky_congealed_slime`; `tconstruct:ichor_congealed_slime`; `tconstruct:sky_congealed_slime`; `tconstruct:earth_congealed_slime`; `tconstruct:earth_congealed_slime` |  | `#tconstruct:modifiable/shields` |  |
| returning | `tconstruct:returning` |  | `minecraft:ender_pearl`; `minecraft:clock`; `minecraft:ender_pearl` |  | `#tconstruct:modifiable/melee/weapon` / `#tconstruct:modifiable/harvest` |  |
| silky | `tconstruct:silky` | 1 | `tconstruct:silky_cloth`; `tconstruct:silky_cloth`; `tconstruct:silky_cloth`; `tconstruct:silky_cloth`; `tconstruct:silky_cloth` | 1 abilities | `#tconstruct:modifiable/harvest` |  |
| slimeball | `tconstruct:slimeball` | 1 | `tconstruct:sky_slime_vine`; `#c:ingots/slimesteel`; `tconstruct:sky_slime_vine`; `#tconstruct:slimy_logs`; `#tconstruct:slimy_logs` | 1 abilities | `#tconstruct:modifiable/staffs` |  |
| sliver | `tconstruct:sliver` |  | `tconstruct:silky_cloth`; `tconstruct:sky_slime_vine`; `tconstruct:silky_cloth`; `tconstruct:sky_slime_vine`; `tconstruct:sky_slime_vine` | 1 abilities | `#tconstruct:modifiable/staffs` |  |
| slurping | `tconstruct:slurping` |  | `minecraft:glass_bottle`; `#tconstruct:tanks`; `minecraft:glass_bottle`; `#c:ingots/copper`; `#c:ingots/copper` | 1 abilities | `#tconstruct:modifiable/armor/helmets` / `#tconstruct:modifiable/interactable/charge` |  |
| snowdrift | `tconstruct:snowdrift` | 1 | `minecraft:snow_block`; `minecraft:carved_pumpkin`; `minecraft:snow_block`; `minecraft:snow_block`; `minecraft:snow_block` | 1 abilities | `#tconstruct:modifiable/armor/boots` |  |
| soul_belt | `tconstruct:soul_belt` | 1 | `minecraft:leather`; `minecraft:recovery_compass`; `minecraft:leather` | 1 abilities | `#tconstruct:modifiable/armor/leggings` |  |
| spilling | `tconstruct:spilling` |  | `{"ingredient":[{"item":"tconstruct:seared_channel"},{"item":"tconstruct:scorched_channel"}]}`; `#tconstruct:tanks`; `{"ingredient":[{"item":"tconstruct:seared_channel"},{"item":"tconstruct:scorched_channel"}]}`; `#c:ingots/copper`; `#c:ingots/copper` | 1 abilities | `#tconstruct:modifiable/melee` |  |
| spitting | `tconstruct:spitting` | 1-3 | `tconstruct:bow_limb`; `tconstruct:seared_fluid_cannon`; `tconstruct:bow_limb` | 1 abilities | `{"fabric:type":"fabric:all","ingredients":[{"tag":"tconstruct:modifiable/durability"},{"tag":"tconstruct:modifiable/interactable/charge/modifier"}]}` |  |
| splashing | `tconstruct:splashing` |  | `#c:bottles/splash`; `#tconstruct:tanks`; `#c:bottles/splash`; `#c:ingots/copper`; `#c:ingots/copper` | 1 abilities | `{"fabric:type":"fabric:all","ingredients":[{"tag":"tconstruct:modifiable/durability"},{"tag":"tconstruct:modifiable/interactable"}]}` |  |
| springing | `tconstruct:springing` | 1 | `tconstruct:sky_slime_vine`; `tconstruct:sky_slime_crystal`; `tconstruct:sky_slime_vine`; `tconstruct:sky_congealed_slime`; `tconstruct:sky_congealed_slime` | 1 abilities | `{"fabric:type":"fabric:all","ingredients":[{"tag":"tconstruct:modifiable/durability"},{"tag":"tconstruct:modifiable/interactable/charge"}]}` |  |
| strength | `tconstruct:strength` | 1-2 | `tconstruct:ichor_slime_crystal`; 72x, 1 per item | 1 abilities | `#tconstruct:modifiable/armor/chestplate` |  |
| stripping | `tconstruct:stripping` | 1 | `tconstruct:small_axe_head`; `#c:ingots/steel`; `tconstruct:tool_binding` | 1 abilities | `{"fabric:type":"fabric:all","ingredients":[{"tag":"tconstruct:modifiable/durability"},{"tag":"tconstruct:modifiable/interactable"}]}` |  |
| throwing | `tconstruct:throwing` | 1 | `tconstruct:bow_limb`; `#c:ingots/cinderslime`; `tconstruct:bow_grip` | 1 abilities | `{"fabric:type":"fabric:all","ingredients":[{"tag":"tconstruct:modifiable/durability"},{"tag":"tconstruct:modifiable/interactable/charge"},[{"tag":"tconstruct:modifiable/melee/weapon"},{"tag":"tconstruct:modifiable/harvest"}]]}` |  |
| tilling | `tconstruct:tilling` | 1 | `tconstruct:small_blade`; `#c:ingots/steel`; `tconstruct:tool_binding` | 1 abilities | `{"fabric:type":"fabric:all","ingredients":[{"tag":"tconstruct:modifiable/durability"},[{"tag":"tconstruct:modifiable/interactable"},{"tag":"tconstruct:modifiable/armor/boots"}]]}` |  |
| tool_belt_1 | `tconstruct:tool_belt` | 1 | `minecraft:leather`; `#c:ingots/iron`; `minecraft:leather` | 1 abilities | `#tconstruct:modifiable/armor/leggings` |  |
| tool_belt_2 | `tconstruct:tool_belt` | 2 | `minecraft:leather`; `#c:ingots/gold`; `minecraft:leather` |  | `#tconstruct:modifiable/armor/leggings` |  |
| tool_belt_3 | `tconstruct:tool_belt` | 3 | `minecraft:leather`; `#c:ingots/rose_gold`; `minecraft:leather` |  | `#tconstruct:modifiable/armor/leggings` |  |
| tool_belt_4 | `tconstruct:tool_belt` | 4 | `minecraft:leather`; `#c:ingots/cobalt`; `minecraft:leather` |  | `#tconstruct:modifiable/armor/leggings` |  |
| tool_belt_5 | `tconstruct:tool_belt` | 5 | `minecraft:leather`; `#c:ingots/hepatizon`; `minecraft:leather` |  | `#tconstruct:modifiable/armor/leggings` |  |
| tool_belt_6 | `tconstruct:tool_belt` | 6 | `minecraft:leather`; `#c:ingots/manyullyn`; `minecraft:leather` |  | `#tconstruct:modifiable/armor/leggings` |  |
| trick_quiver | `tconstruct:trick_quiver` |  | `tconstruct:silky_cloth`; `tconstruct:sky_slime_vine`; `tconstruct:silky_cloth`; `tconstruct:sky_slime_vine`; `tconstruct:sky_slime_vine` | 1 abilities | `{"fabric:type":"fabric:all","ingredients":[{"tag":"tconstruct:modifiable/ranged/bows"},{"tag":"tconstruct:modifiable/interactable"}]}` |  |
| unbreakable | `tconstruct:unbreakable` | 1 | `minecraft:shulker_shell`; `minecraft:dragon_breath`; `minecraft:shulker_shell`; `#c:ingots/netherite`; `#c:ingots/netherite` | 1 abilities | `#tconstruct:modifiable/durability` |  |
| warping | `tconstruct:warping` | 1 | `tconstruct:ender_slime_vine`; `tconstruct:ender_slime_crystal`; `tconstruct:ender_slime_vine`; `tconstruct:ender_congealed_slime`; `tconstruct:ender_congealed_slime` | 1 abilities | `{"fabric:type":"fabric:all","ingredients":[{"tag":"tconstruct:modifiable/durability"},{"tag":"tconstruct:modifiable/interactable/charge"}]}` |  |
| wetting | `tconstruct:wetting` |  | `#c:dusts/redstone`; `#tconstruct:tanks`; `#c:dusts/redstone`; `#c:ingots/copper`; `#c:ingots/copper` | 1 abilities | `#tconstruct:modifiable/armor/leggings` / `#tconstruct:modifiable/shields` |  |
| wings | `tconstruct:wings` | 1 | `minecraft:elytra` | 2 abilities | `#tconstruct:modifiable/armor/chestplate` |  |

## modifiers/compat

| Recipe | Modifier | Levels | Inputs | Slots | Tools | Condition |
|---|---|---|---|---|---|---|
| headlight_10 | `tconstruct:headlight` |  | `minecraft:leather`; `minecraft:soul_lantern`; `minecraft:leather` | 1 upgrades | `#tconstruct:modifiable/armor/helmets` | mod `headlight` |
| headlight_15 | `tconstruct:headlight` |  | `minecraft:leather`; `minecraft:lantern`; `minecraft:leather` | 1 upgrades | `#tconstruct:modifiable/armor/helmets` | mod `headlight` |
| headlight_5 | `tconstruct:headlight` |  | `minecraft:leather`; `#minecraft:candles`; `minecraft:leather` | 1 upgrades | `#tconstruct:modifiable/armor/helmets` | mod `headlight` |
| the_one_probe | `tconstruct:the_one_probe` | 1 | `theoneprobe:probe` | 1 upgrades | `#tconstruct:modifiable/armor/helmets` / `#tconstruct:modifiable/held` | mod `theoneprobe` |

## modifiers/defense

| Recipe | Modifier | Levels | Inputs | Slots | Tools | Condition |
|---|---|---|---|---|---|---|
| blast_protection | `tconstruct:blast_protection` |  | `tconstruct:obsidian_reinforcement`; 5x, 1 per item | 1 defense | `#tconstruct:modifiable/armor` / `#tconstruct:modifiable/held` |  |
| dragonborn | `tconstruct:dragonborn` |  | `tconstruct:dragon_scale`; 5x, 1 per item | 1 defense | `#tconstruct:modifiable/armor` |  |
| fire_protection | `tconstruct:fire_protection` |  | `tconstruct:seared_reinforcement`; 5x, 1 per item | 1 defense | `#tconstruct:modifiable/armor` / `#tconstruct:modifiable/held` |  |
| golden | `tconstruct:golden` | 1 | `#c:ingots/gold`; `#c:ingots/gold`; `#c:ingots/gold` | 1 defense | `#tconstruct:modifiable/armor/golden` | tag `tconstruct:modifiable/armor/golden` filled |
| knockback_resistance | `tconstruct:knockback_resistance` | 1 | `{"ingredient":[{"item":"minecraft:anvil"},{"item":"minecraft:chipped_anvil"},{"item":"minecraft:damaged_anvil"}]}` | 1 defense | `#tconstruct:modifiable/armor` |  |
| magic_protection | `tconstruct:magic_protection` |  | `tconstruct:gold_reinforcement`; 5x, 1 per item | 1 defense | `#tconstruct:modifiable/armor` / `#tconstruct:modifiable/held` |  |
| melee_protection | `tconstruct:melee_protection` |  | `tconstruct:cobalt_reinforcement`; 5x, 1 per item | 1 defense | `#tconstruct:modifiable/armor` / `#tconstruct:modifiable/held` |  |
| projectile_protection | `tconstruct:projectile_protection` |  | `tconstruct:iron_reinforcement`; 5x, 1 per item | 1 defense | `#tconstruct:modifiable/armor` / `#tconstruct:modifiable/held` |  |
| revitalizing | `tconstruct:revitalizing` |  | `tconstruct:jeweled_apple`; 2x, 1 per item | 1 defense | `#tconstruct:modifiable/armor/worn` |  |
| shulking | `tconstruct:shulking` |  | `minecraft:shulker_shell`; 3x, 1 per item | 1 defense | `#tconstruct:modifiable/armor` |  |
| turtle_shell | `tconstruct:turtle_shell` |  | `minecraft:turtle_scute`; 5x, 1 per item | 1 defense | `#tconstruct:modifiable/armor` |  |

## modifiers/slotless

| Recipe | Modifier | Levels | Inputs | Slots | Tools | Condition |
|---|---|---|---|---|---|---|
| barebow | `tconstruct:barebow` | 1 | `#c:strings`; `#c:rods/wooden`; `#c:strings` |  | `#tconstruct:modifiable/ranged/bows` |  |
| blindshot | `tconstruct:blindshot` |  | `minecraft:dirt`; 10x, 1 per item |  | `#tconstruct:modifiable/ranged` |  |
| blunted | `tconstruct:blunted` | 1 | `minecraft:honeycomb`; `minecraft:feather`; `minecraft:honeycomb` |  | `#tconstruct:modifiable/interactable/left` |  |
| draconic_for_skull | `tconstruct:draconic` | 1 | `minecraft:dragon_head`; `#c:slimeballs` |  | `{"fabric:type":"fabric:all","ingredients":[{"tag":"tconstruct:modifiable/bonus_slots"},{"tag":"tconstruct:modifiable/skulls"}]}` |  |
| draconic_from_head | `tconstruct:draconic` | 1 | `minecraft:dragon_head` |  | `{"fabric:type":"fabric:difference","base":{"tag":"tconstruct:modifiable/bonus_slots"},"subtracted":{"tag":"tconstruct:modifiable/skulls"}}` |  |
| draconic_from_scales | `tconstruct:draconic` | 1 | `minecraft:dragon_breath`; `tconstruct:dragon_scale`; `minecraft:dragon_breath`; `tconstruct:dragon_scale`; `tconstruct:dragon_scale` |  | `#tconstruct:modifiable/bonus_slots` |  |
| embossed | `tconstruct:embossed` | 1 | `#tconstruct:boss_trophies` |  | `#tconstruct:modifiable/bonus_slots` | tag `tconstruct:boss_trophies` filled |
| farsighted | `tconstruct:farsighted` |  | `#c:crops/carrot`; 45x, 1 per item |  | `#tconstruct:modifiable/held` / `#tconstruct:modifiable/armor` |  |
| forecast | `tconstruct:forecast` | 1 | `{"ingredient":[{"tag":"c:ores/diamond"},{"tag":"c:ores/emerald"},{"tag":"c:ores/cobalt"}]}` |  | `#tconstruct:modifiable/bonus_slots` |  |
| harmonious | `tconstruct:harmonious` | 1 | `#minecraft:music_discs` |  | `#tconstruct:modifiable/bonus_slots` |  |
| nearsighted | `tconstruct:nearsighted` |  | `minecraft:ink_sac`; 45x, 1 per item |  | `#tconstruct:modifiable/held` / `#tconstruct:modifiable/armor` |  |
| offhanded | `tconstruct:offhanded` | 1-2 | `minecraft:leather`; `minecraft:fire_charge`; `#c:slimeball/ichor` |  | `#tconstruct:modifiable/interactable/charge/modifier` |  |
| rebalanced_abilities | `tconstruct:rebalanced` |  | `#c:nuggets/queens_slime`; `minecraft:end_crystal`; `#c:nuggets/queens_slime`; `tconstruct:ichor_slime_crystal_block`; `tconstruct:ichor_slime_crystal_block` |  | `#tconstruct:modifiable/bonus_slots` |  |
| rebalanced_defense | `tconstruct:rebalanced` |  | `#c:nuggets/cobalt`; `minecraft:end_crystal`; `#c:nuggets/cobalt`; `tconstruct:earth_slime_crystal_block`; `tconstruct:earth_slime_crystal_block` |  | `{"fabric:type":"fabric:all","ingredients":[[{"tag":"tconstruct:modifiable/armor"},{"tag":"tconstruct:modifiable/held"}],{"tag":"tconstruct:modifiable/bonus_slots"}]}` |  |
| rebalanced_traits | `tconstruct:rebalanced` |  | `#c:nuggets/manyullyn`; `minecraft:end_crystal`; `#c:nuggets/manyullyn`; `tconstruct:ender_slime_crystal_block`; `tconstruct:ender_slime_crystal_block` |  | `{"fabric:type":"tconstruct:tool_hook","hook":"tconstruct:rebalanced_trait","tag":"tconstruct:modifiable/bonus_slots"}` |  |
| rebalanced_upgrades | `tconstruct:rebalanced` |  | `#c:nuggets/rose_gold`; `minecraft:end_crystal`; `#c:nuggets/rose_gold`; `tconstruct:sky_slime_crystal_block`; `tconstruct:sky_slime_crystal_block` |  | `#tconstruct:modifiable/bonus_slots` |  |
| recapitated | `tconstruct:recapitated` | 1 | `{"fabric:type":"fabric:difference","base":{"tag":"c:heads"},"subtracted":{"item":"minecraft:dragon_head"}}` |  | `{"fabric:type":"fabric:difference","base":{"tag":"tconstruct:modifiable/bonus_slots"},"subtracted":{"tag":"tconstruct:modifiable/skulls"}}` |  |
| recapitated_for_skull | `tconstruct:recapitated` | 1 | `{"fabric:type":"fabric:difference","base":{"tag":"c:heads"},"subtracted":{"item":"minecraft:dragon_head"}}`; `#c:slimeballs` |  | `{"fabric:type":"fabric:all","ingredients":[{"tag":"tconstruct:modifiable/bonus_slots"},{"tag":"tconstruct:modifiable/skulls"}]}` |  |
| redirected | `tconstruct:redirected` |  | `minecraft:dragon_breath` |  | `{"fabric:type":"tconstruct:tool_hook","hook":"tconstruct:rebalanced_trait","tag":"tconstruct:modifiable/ammo"}` |  |
| shiny | `tconstruct:shiny` | 1 | `{"ingredient":[{"item":"minecraft:enchanted_golden_apple"},{"item":"minecraft:nether_star"}]}` |  | `#tconstruct:modifiable` |  |
| soulbound_ammo | `tconstruct:soulbound` | 1 | `minecraft:sculk_vein` |  | `#tconstruct:modifiable/single_use` |  |
| worldbound | `tconstruct:worldbound` | 1 | `#c:ingots/netherite_scrap` |  | `{"fabric:type":"fabric:difference","base":{"tag":"tconstruct:modifiable"},"subtracted":{"tag":"tconstruct:modifiable/single_use"}}` |  |
| writable | `tconstruct:writable` | 1 | `minecraft:writable_book` |  | `#tconstruct:modifiable/bonus_slots` |  |

## modifiers/slotless/embellishment/slime

| Recipe | Modifier | Levels | Inputs | Slots | Tools | Condition |
|---|---|---|---|---|---|---|
| bloodshroom | `tconstruct:embellishment` |  | `tconstruct:bloodshroom_planks`; `tconstruct:blood_slime_sapling`; `tconstruct:bloodshroom_planks` |  | `#tconstruct:modifiable/embellishment/slime` |  |
| clay | `tconstruct:embellishment` |  | `minecraft:clay`; `minecraft:clay_ball`; `minecraft:clay` |  | `#tconstruct:modifiable/embellishment/slime` |  |
| earth | `tconstruct:embellishment` |  | `tconstruct:earth_congealed_slime`; `minecraft:slime_ball`; `tconstruct:earth_congealed_slime` |  | `#tconstruct:modifiable/embellishment/slime` |  |
| ender | `tconstruct:embellishment` |  | `tconstruct:ender_congealed_slime`; `tconstruct:ender_slime_ball`; `tconstruct:ender_congealed_slime` |  | `#tconstruct:modifiable/embellishment/slime` |  |
| enderbark | `tconstruct:embellishment` |  | `tconstruct:enderbark_planks`; `tconstruct:ender_slime_sapling`; `tconstruct:enderbark_planks` |  | `#tconstruct:modifiable/embellishment/slime` |  |
| greenheart | `tconstruct:embellishment` |  | `tconstruct:greenheart_planks`; `tconstruct:earth_slime_sapling`; `tconstruct:greenheart_planks` |  | `#tconstruct:modifiable/embellishment/slime` |  |
| honey | `tconstruct:embellishment` |  | `minecraft:honey_block`; `minecraft:honey_bottle`; `minecraft:honey_block` |  | `#tconstruct:modifiable/embellishment/slime` |  |
| ichor | `tconstruct:embellishment` |  | `tconstruct:ichor_congealed_slime`; `tconstruct:ichor_slime_ball`; `tconstruct:ichor_congealed_slime` |  | `#tconstruct:modifiable/embellishment/slime` |  |
| magma | `tconstruct:embellishment` |  | `minecraft:magma_block`; `minecraft:magma_cream`; `minecraft:magma_block` |  | `#tconstruct:modifiable/embellishment/slime` |  |
| sky | `tconstruct:embellishment` |  | `tconstruct:sky_congealed_slime`; `tconstruct:sky_slime_ball`; `tconstruct:sky_congealed_slime` |  | `#tconstruct:modifiable/embellishment/slime` |  |
| skyroot | `tconstruct:embellishment` |  | `tconstruct:skyroot_planks`; `tconstruct:sky_slime_sapling`; `tconstruct:skyroot_planks` |  | `#tconstruct:modifiable/embellishment/slime` |  |

## modifiers/slotless/embellishment/wood

| Recipe | Modifier | Levels | Inputs | Slots | Tools | Condition |
|---|---|---|---|---|---|---|
| bamboo | `tconstruct:embellishment` |  | `minecraft:bamboo`; `tconstruct:pattern`; `minecraft:bamboo` |  | `#tconstruct:modifiable/embellishment/wood` |  |
| blazewood | `tconstruct:embellishment` |  | `tconstruct:blazewood`; `tconstruct:pattern`; `tconstruct:blazewood` |  | `#tconstruct:modifiable/embellishment/wood` |  |
| cactus | `tconstruct:embellishment` |  | `minecraft:cactus`; `tconstruct:pattern`; `minecraft:cactus` |  | `#tconstruct:modifiable/embellishment/wood` |  |
| ironwood | `tconstruct:embellishment` |  | `#c:ingots/ironwood`; `tconstruct:pattern`; `#c:ingots/ironwood` |  | `#tconstruct:modifiable/embellishment/wood` | tag `c:ingots/ironwood` filled |
| nahuatl | `tconstruct:embellishment` |  | `tconstruct:nahuatl`; `tconstruct:pattern`; `tconstruct:nahuatl` |  | `#tconstruct:modifiable/embellishment/wood` |  |
| slimewood_bloodshroom | `tconstruct:embellishment` |  | `tconstruct:bloodshroom_planks`; `tconstruct:pattern`; `tconstruct:bloodshroom_planks` |  | `#tconstruct:modifiable/embellishment/wood` |  |
| slimewood_enderbark | `tconstruct:embellishment` |  | `tconstruct:enderbark_planks`; `tconstruct:pattern`; `tconstruct:enderbark_planks` |  | `#tconstruct:modifiable/embellishment/wood` |  |
| slimewood_greenheart | `tconstruct:embellishment` |  | `tconstruct:greenheart_planks`; `tconstruct:pattern`; `tconstruct:greenheart_planks` |  | `#tconstruct:modifiable/embellishment/wood` |  |
| slimewood_skyroot | `tconstruct:embellishment` |  | `tconstruct:skyroot_planks`; `tconstruct:pattern`; `tconstruct:skyroot_planks` |  | `#tconstruct:modifiable/embellishment/wood` |  |
| treated | `tconstruct:embellishment` |  | `#c:treated_wood`; `tconstruct:pattern`; `#c:treated_wood` |  | `#tconstruct:modifiable/embellishment/wood` | tag `c:treated_wood` filled |
| wood_crimson | `tconstruct:embellishment` |  | `minecraft:crimson_planks`; `tconstruct:pattern`; `minecraft:crimson_planks` |  | `#tconstruct:modifiable/embellishment/wood` |  |
| wood_warped | `tconstruct:embellishment` |  | `minecraft:warped_planks`; `tconstruct:pattern`; `minecraft:warped_planks` |  | `#tconstruct:modifiable/embellishment/wood` |  |

## modifiers/upgrade

| Recipe | Modifier | Levels | Inputs | Slots | Tools | Condition |
|---|---|---|---|---|---|---|
| antiaquatic | `tconstruct:antiaquatic` | 1-5 | `minecraft:pufferfish`; 5x, 1 per item | 1 upgrades | `#tconstruct:modifiable/melee` |  |
| bane_of_sssss | `tconstruct:bane_of_sssss` | 1-5 | `minecraft:fermented_spider_eye`; 15x, 1 per item | 1 upgrades | `#tconstruct:modifiable/melee` |  |
| blasting | `tconstruct:blasting` | 1-5 | `#c:gunpowders`; 20x, 1 per item | 1 upgrades | `#tconstruct:modifiable/harvest/stone` |  |
| blockade | `tconstruct:blockade` | 1-3 | `tconstruct:obsidian_pane`; 10x, 1 per item | 1 upgrades | `{"fabric:type":"fabric:all","ingredients":[{"tag":"tconstruct:modifiable/interactable/charge"},{"tag":"tconstruct:modifiable/durability"}]}` |  |
| bounce | `tconstruct:bounce` | 1-3 | `minecraft:piston`; `tconstruct:ichor_slime` | 1 upgrades | `#tconstruct:modifiable/ranged/bounce` |  |
| collecting | `tconstruct:collecting` | 1 | `minecraft:hopper` | 1 upgrades | `#tconstruct:modifiable/fishing_rods` |  |
| cooling | `tconstruct:cooling` | 1-5 | `minecraft:prismarine_crystals`; 25x, 1 per item | 1 upgrades | `#tconstruct:modifiable/melee` |  |
| depth_strider | `tconstruct:depth_strider` | 1-3 | `#minecraft:fishes`; `minecraft:prismarine_bricks`; `#minecraft:fishes` | 1 upgrades | `#tconstruct:modifiable/armor/boots` |  |
| diamond | `tconstruct:diamond` | 1 | `#c:gems/diamond` | 1 upgrades | `#tconstruct:modifiable/durability` |  |
| emerald | `tconstruct:emerald` | 1 | `#c:gems/emerald` | 1 upgrades | `#tconstruct:modifiable/durability` |  |
| experienced | `tconstruct:experienced` | 1-5 | `minecraft:experience_bottle`; `minecraft:experience_bottle`; `minecraft:experience_bottle` | 1 upgrades | `#tconstruct:modifiable/melee` / `#tconstruct:modifiable/harvest` / `#tconstruct:modifiable/ranged/launcher` / `#tconstruct:modifiable/armor/leggings` |  |
| feather_falling | `tconstruct:feather_falling` | 1-2 | `minecraft:feather`; 25x, 1 per item | 1 upgrades | `#tconstruct:modifiable/armor/boots` |  |
| fiery | `tconstruct:fiery` | 1-5 | `minecraft:blaze_powder`; 25x, 1 per item | 1 upgrades | `#tconstruct:modifiable/melee` / `#tconstruct:modifiable/ranged/bows` / `#tconstruct:modifiable/fishing_rods` / `#tconstruct:modifiable/armor/worn` / `#tconstruct:modifiable/shields` |  |
| fins | `tconstruct:fins` | 1 | `#minecraft:fishes`; `minecraft:prismarine_bricks`; `#minecraft:fishes` | 1 upgrades | `#tconstruct:modifiable/melee/weapon` |  |
| fireprimer | `tconstruct:fireprimer` | 1 | `#c:ingots/steel`; `minecraft:flint` | 1 upgrades | `tconstruct:flint_and_brick` |  |
| freezing | `tconstruct:freezing` | 1-3 | `minecraft:powder_snow_bucket` | 1 upgrades | `#tconstruct:modifiable/melee` / `#tconstruct:modifiable/ranged/bows` / `#tconstruct:modifiable/fishing_rods` / `#tconstruct:modifiable/armor/worn` / `#tconstruct:modifiable/shields` |  |
| haste_from_block | `tconstruct:haste` | 1-5 | `#c:storage_blocks/redstone`; 45x, 9 per item | 1 upgrades | `#tconstruct:modifiable/harvest` / `#tconstruct:modifiable/armor/chestplate` |  |
| haste_from_dust | `tconstruct:haste` | 1-5 | `#c:dusts/redstone`; 45x, 1 per item | 1 upgrades | `#tconstruct:modifiable/harvest` / `#tconstruct:modifiable/armor/chestplate` |  |
| hydraulic_from_block | `tconstruct:hydraulic` | 1-5 | `minecraft:prismarine`; 36x, 4 per item | 1 upgrades | `#tconstruct:modifiable/harvest` |  |
| hydraulic_from_bricks | `tconstruct:hydraulic` | 1-5 | `minecraft:prismarine_bricks`; 36x, 9 per item | 1 upgrades | `#tconstruct:modifiable/harvest` |  |
| hydraulic_from_shard | `tconstruct:hydraulic` | 1-5 | `#c:dusts/prismarine`; 36x, 1 per item | 1 upgrades | `#tconstruct:modifiable/harvest` |  |
| impaling | `tconstruct:impaling` | 1-4 | `minecraft:pointed_dripstone`; `minecraft:pointed_dripstone`; `minecraft:pointed_dripstone` | 1 upgrades | `#tconstruct:modifiable/ranged/crossbows` |  |
| item_frame | `tconstruct:item_frame` |  | `{"ingredient":[{"item":"tconstruct:reversed_gold_item_frame"},{"item":"tconstruct:diamond_item_frame"},{"item":"tconstruct:manyullyn_item_frame"},{"item":"tconstruct:gold_item_frame"},{"item":"tconstruct:netherite_item_frame"}]}` | 1 upgrades | `#tconstruct:modifiable/armor/helmets` |  |
| killager_from_block | `tconstruct:killager` | 1-5 | `#c:storage_blocks/lapis`; 45x, 9 per item | 1 upgrades | `#tconstruct:modifiable/melee` |  |
| killager_from_dust | `tconstruct:killager` | 1-5 | `#c:gems/lapis`; 45x, 1 per item | 1 upgrades | `#tconstruct:modifiable/melee` |  |
| knockback | `tconstruct:knockback` | 1-3 | `minecraft:piston`; `minecraft:slime_block` | 1 upgrades | `#tconstruct:modifiable/melee` / `#tconstruct:modifiable/armor/chestplate` |  |
| lightspeed_boots_from_block | `tconstruct:lightspeed` | 1-3 | `minecraft:glowstone`; 64x, 4 per item | 1 upgrades | `#tconstruct:modifiable/armor/boots` |  |
| lightspeed_boots_from_dust | `tconstruct:lightspeed` | 1-3 | `#c:dusts/glowstone`; 64x, 1 per item | 1 upgrades | `#tconstruct:modifiable/armor/boots` |  |
| lightspeed_harvest_from_block | `tconstruct:lightspeed` | 1-5 | `minecraft:glowstone`; 64x, 4 per item | 1 upgrades | `#tconstruct:modifiable/harvest` |  |
| lightspeed_harvest_from_dust | `tconstruct:lightspeed` | 1-5 | `#c:dusts/glowstone`; 64x, 1 per item | 1 upgrades | `#tconstruct:modifiable/harvest` |  |
| long_fall | `tconstruct:long_fall` | 1 | `minecraft:piston`; `minecraft:phantom_membrane`; `minecraft:piston`; `tconstruct:ichor_slime`; `tconstruct:ichor_slime` | 1 upgrades | `#tconstruct:modifiable/armor/boots` |  |
| lure | `tconstruct:lure` | 1-3 | `tconstruct:cheese_ingot`; `tconstruct:cheese_ingot`; `tconstruct:cheese_ingot` | 1 upgrades | `#tconstruct:modifiable/fishing_rods` |  |
| magnetic | `tconstruct:magnetic` | 1-5 | `minecraft:compass` | 1 upgrades | `#tconstruct:modifiable/melee/weapon` / `#tconstruct:modifiable/harvest` |  |
| magnetic_armor | `tconstruct:magnetic` | 1 | `minecraft:compass` | 1 upgrades | `#tconstruct:modifiable/armor/worn` |  |
| minimap | `tconstruct:minimap` |  | `minecraft:compass`; `#c:slimeballs`; `minecraft:paper` | 1 upgrades | `#tconstruct:modifiable/armor/helmets` |  |
| necrotic | `tconstruct:necrotic` | 1-5 | `#c:wither_bones`; `tconstruct:ichor_congealed_slime`; `minecraft:ghast_tear` | 1 upgrades | `#tconstruct:modifiable/melee` / `#tconstruct:modifiable/ranged/bows` / `#tconstruct:modifiable/fishing_rods` |  |
| netherite | `tconstruct:netherite` | 1 | `minecraft:netherite_upgrade_smithing_template`; `#c:ingots/netherite` | 1 upgrades | `#tconstruct:modifiable/durability` |  |
| overforced | `tconstruct:overforced` | 1-5 | `tconstruct:slimesteel_reinforcement`; 4x, 1 per item | 1 upgrades | `#tconstruct:modifiable/durability` |  |
| padded | `tconstruct:padded` | 1-3 | `minecraft:leather`; `#minecraft:wool`; `minecraft:leather` | 1 upgrades | `#tconstruct:modifiable/melee` |  |
| pierce | `tconstruct:pierce` | 1-3 | `tconstruct:punji`; 10x, 1 per item | 1 upgrades | `#tconstruct:modifiable/melee` / `#tconstruct:modifiable/ranged/launcher` |  |
| power | `tconstruct:power` | 1-5 | `tconstruct:ichor_slime_crystal`; 72x, 1 per item | 1 upgrades | `#tconstruct:modifiable/ranged/power` |  |
| punch | `tconstruct:punch` | 1-3 | `minecraft:piston`; `tconstruct:sky_slime` | 1 upgrades | `#tconstruct:modifiable/ranged/launcher` |  |
| quick_charge | `tconstruct:quick_charge` | 1-4 | `minecraft:magma_cream`; 5x, 1 per item | 1 upgrades | `#tconstruct:modifiable/ranged/quick_charge` |  |
| reinforced | `tconstruct:reinforced` | 1-5 | `tconstruct:emerald_reinforcement`; 4x, 1 per item | 1 upgrades | `#tconstruct:modifiable/durability` |  |
| respiration | `tconstruct:respiration` | 1-3 | `#minecraft:fishes`; `#c:glass/colorless`; `#minecraft:fishes`; `minecraft:kelp`; `minecraft:kelp` | 1 upgrades | `#tconstruct:modifiable/armor/helmets` |  |
| ricochet | `tconstruct:ricochet` | 1-2 | `minecraft:piston`; `tconstruct:sky_slime` | 1 upgrades | `#tconstruct:modifiable/armor/worn` / `#tconstruct:modifiable/shields` |  |
| scope | `tconstruct:scope` | 1 | `minecraft:sugar`; `minecraft:spyglass`; `minecraft:sugar` | 1 upgrades | `#tconstruct:modifiable/interactable/charge` |  |
| severing | `tconstruct:severing` | 1-3 | `#c:wither_bones`; `minecraft:lightning_rod`; `#c:wither_bones`; `minecraft:tnt` | 1 upgrades | `#tconstruct:modifiable/melee` / `#tconstruct:modifiable/ranged/launcher` |  |
| sharpness_from_block | `tconstruct:sharpness` | 1-5 | `#c:storage_blocks/quartz`; 36x, 4 per item | 1 upgrades | `#tconstruct:modifiable/melee` |  |
| sharpness_from_shard | `tconstruct:sharpness` | 1-5 | `#c:gems/quartz`; 36x, 1 per item | 1 upgrades | `#tconstruct:modifiable/melee` |  |
| shield_strap | `tconstruct:shield_strap` |  | `tconstruct:sky_slime_vine`; `#c:ingots/slimesteel`; `tconstruct:sky_slime_vine` | 1 upgrades | `#tconstruct:modifiable/armor/leggings` |  |
| sinistral | `tconstruct:sinistral` | 1 | `#c:ingots/slimesteel`; `minecraft:nautilus_shell`; `#c:ingots/slimesteel` | 1 upgrades | `{"fabric:type":"fabric:all","ingredients":[{"tag":"tconstruct:modifiable/ranged/crossbows"},{"tag":"tconstruct:modifiable/interactable/left"}]}` |  |
| sleeves | `tconstruct:sleeves` | 1-3 | `tconstruct:silky_cloth`; `#c:ingots/cinderslime`; `tconstruct:silky_cloth` | 1 upgrades | `#tconstruct:modifiable/armor/chestplate` |  |
| smelting | `tconstruct:smelting` | 1-4 | `minecraft:campfire` | 1 upgrades | `#tconstruct:modifiable/interactable` / `#tconstruct:modifiable/armor/worn` |  |
| smite | `tconstruct:smite` | 1-5 | `minecraft:glistering_melon_slice`; 5x, 1 per item | 1 upgrades | `#tconstruct:modifiable/melee` |  |
| soulbound | `tconstruct:soulbound` | 1 | `minecraft:echo_shard` | 1 upgrades | `{"fabric:type":"fabric:difference","base":{"tag":"tconstruct:modifiable"},"subtracted":{"tag":"tconstruct:modifiable/single_use"}}` |  |
| soulspeed | `tconstruct:soulspeed` | 1-3 | `minecraft:magma_block`; `minecraft:crying_obsidian`; `minecraft:magma_block` | 1 upgrades | `#tconstruct:modifiable/armor/boots` |  |
| speedy_from_block | `tconstruct:speedy` | 1-3 | `#c:storage_blocks/redstone`; 45x, 9 per item | 1 upgrades | `#tconstruct:modifiable/armor/leggings` |  |
| speedy_from_dust | `tconstruct:speedy` | 1-3 | `#c:dusts/redstone`; 45x, 1 per item | 1 upgrades | `#tconstruct:modifiable/armor/leggings` |  |
| springy | `tconstruct:springy` | 1-3 | `minecraft:piston`; `tconstruct:ichor_slime` | 1 upgrades | `#tconstruct:modifiable/armor/worn` / `#tconstruct:modifiable/shields` |  |
| step_up | `tconstruct:step_up` | 1-2 | `minecraft:leather`; `minecraft:golden_carrot`; `minecraft:leather`; `minecraft:scaffolding`; `minecraft:scaffolding` | 1 upgrades | `#tconstruct:modifiable/armor/leggings` |  |
| sweeping_edge | `tconstruct:sweeping_edge` | 1-3 | `minecraft:chain`; 5x, 1 per item | 1 upgrades | `#tconstruct:modifiable/melee/sword` |  |
| swift_sneak | `tconstruct:swift_sneak` | 1-5 | `minecraft:sculk_sensor` | 1 upgrades | `#tconstruct:modifiable/armor/leggings` |  |
| swiftstrike_from_block | `tconstruct:swiftstrike` | 1-5 | `minecraft:amethyst_block`; 72x, 4 per item | 1 upgrades | `#tconstruct:modifiable/melee/weapon` |  |
| swiftstrike_from_shard | `tconstruct:swiftstrike` | 1-5 | `minecraft:amethyst_shard`; 72x, 1 per item | 1 upgrades | `#tconstruct:modifiable/melee/weapon` |  |
| tank | `tconstruct:tank` |  | `#tconstruct:tanks` | 1 upgrades | `#tconstruct:modifiable/held` / `#tconstruct:modifiable/armor` |  |
| thorns | `tconstruct:thorns` | 1-3 | `minecraft:cactus`; 25x, 1 per item | 1 upgrades | `#tconstruct:modifiable/armor/worn` / `#tconstruct:modifiable/shields` |  |
| trueshot | `tconstruct:trueshot` | 1-3 | `minecraft:target`; 10x, 1 per item | 1 upgrades | `#tconstruct:modifiable/ranged` |  |
| workbench | `tconstruct:workbench` | 1 | `minecraft:leather`; `minecraft:crafting_table`; `minecraft:leather` |  | `#tconstruct:modifiable/armor/leggings` |  |
| zoom | `tconstruct:zoom` | 1 | `#c:strings`; `minecraft:spyglass`; `#c:strings` | 1 upgrades | `#tconstruct:modifiable/armor/helmets` / `#tconstruct:modifiable/interactable/charge` |  |

## modifiers/worktable/enchantment_converting

| Recipe | Modifier | Levels | Inputs | Slots | Tools | Condition |
|---|---|---|---|---|---|---|
| ability_book | `` |  | `tconstruct:ichor_slime_crystal`; `#c:gems/diamond` |  |  |  |
| ability_tool | `` |  | `tconstruct:ichor_slime_crystal`; `#c:gems/diamond` |  |  |  |
| defense_book | `` |  | `tconstruct:earth_slime_crystal`; `#c:ingots/gold` |  |  |  |
| defense_tool | `` |  | `tconstruct:earth_slime_crystal`; `#c:ingots/gold` |  |  |  |
| slotless_book | `` |  | `minecraft:amethyst_shard` |  |  |  |
| slotless_tool | `` |  | `minecraft:amethyst_shard` |  |  |  |
| unenchant_book | `` |  | `tconstruct:ender_slime_crystal`; `minecraft:dragon_breath` |  |  |  |
| unenchant_tool | `` |  | `tconstruct:ender_slime_crystal`; `minecraft:dragon_breath` |  |  |  |
| upgrade_book | `` |  | `tconstruct:sky_slime_crystal`; `#c:gems/lapis` |  |  |  |
| upgrade_tool | `` |  | `tconstruct:sky_slime_crystal`; `#c:gems/lapis` |  |  |  |

_133 salvage recipes accompany the modifiers above._
