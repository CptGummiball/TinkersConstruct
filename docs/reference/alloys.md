# Alloys

Alloying combines fluids inside the smeltery or alloyer. The temperature is the
minimum the structure must reach.

| Alloy | Inputs | Result | Temp (C) | Condition |
|---|---|---|---|---|
| molten_amethyst_bronze | 90 mb `#c:molten_copper` + 100 mb `#tconstruct:molten_amethyst` | 90 mb `#c:molten_amethyst_bronze` | 820 |  |
| molten_bendalloy | 180 mb `#c:molten_tin` + 90 mb `#c:molten_lead` + 90 mb `#c:molten_cadmium` | 360 mb `#c:molten_bendalloy` | 100 | tag `c:ingots/bendalloy` filled; tag `c:ingots/tin` filled; tag `c:ingots/lead` filled; tag `c:ingots/cadmium` filled |
| molten_brass | 90 mb `#c:molten_copper` + 90 mb `#c:molten_zinc` | 180 mb `#c:molten_brass` | 605 | tag `c:ingots/zinc` filled; any of(config `allow_ingotless_alloys`; tag `c:ingots/brass` filled) |
| molten_bronze | 270 mb `#c:molten_copper` + 90 mb `#c:molten_tin` | 360 mb `#c:molten_bronze` | 700 | tag `c:ingots/tin` filled; any of(config `allow_ingotless_alloys`; tag `c:ingots/bronze` filled) |
| molten_cinderslime | 90 mb `#c:molten_gold` + 250 mb `#tconstruct:ichor` + 250 mb `#tconstruct:scorched_stone` | 180 mb `tconstruct:molten_cinderslime` | 1050 |  |
| molten_constantan | 90 mb `#c:molten_copper` + 90 mb `#c:molten_nickel` | 180 mb `#c:molten_constantan` | 920 | tag `c:ingots/nickel` filled; any of(config `allow_ingotless_alloys`; tag `c:ingots/constantan` filled) |
| molten_duralumin | 270 mb `#c:molten_aluminum` + 90 mb `#c:molten_copper` | 360 mb `#c:molten_duralumin` | 625 | tag `c:ingots/duralumin` filled; tag `c:ingots/aluminum` filled |
| molten_electrum | 90 mb `#c:molten_gold` + 90 mb `#c:molten_silver` | 180 mb `#c:molten_electrum` | 760 | tag `c:ingots/silver` filled; any of(config `allow_ingotless_alloys`; tag `c:ingots/electrum` filled) |
| molten_enderium | 270 mb `#c:molten_lead` + 100 mb `#tconstruct:molten_diamond` + 500 mb `#c:ender` | 180 mb `#c:molten_enderium` | 1350 | tag `c:ingots/enderium` filled; tag `c:ingots/lead` filled |
| molten_hepatizon | 180 mb `#c:molten_copper` + 90 mb `#c:molten_cobalt` + 100 mb `#tconstruct:molten_quartz` | 180 mb `#c:molten_hepatizon` | 1400 |  |
| molten_invar | 180 mb `#c:molten_iron` + 90 mb `#c:molten_nickel` | 270 mb `#c:molten_invar` | 900 | tag `c:ingots/nickel` filled; any of(config `allow_ingotless_alloys`; tag `c:ingots/invar` filled) |
| molten_knightslime | 90 mb `#c:molten_cobalt` + 250 mb `#tconstruct:ender_slime` + 250 mb `#tconstruct:molten_obsidian` | 180 mb `tconstruct:molten_knightslime` | 1125 |  |
| molten_lumium | 270 mb `#c:molten_tin` + 90 mb `#c:molten_silver` + 500 mb `#c:glowstone` | 360 mb `#c:molten_lumium` | 1050 | tag `c:ingots/lumium` filled; tag `c:ingots/tin` filled; tag `c:ingots/silver` filled; tag `c:glowstone` filled |
| molten_manyullyn | 270 mb `#c:molten_cobalt` + 90 mb `#tconstruct:molten_debris` | 360 mb `#c:molten_manyullyn` | 1200 |  |
| molten_netherite |  | `None` |  |  |
| molten_nicrosil |  | `None` |  |  |
| molten_obsidian | 250 mb `minecraft:water` + 100 mb `minecraft:lava` | 100 mb `tconstruct:molten_obsidian` | 1000 |  |
| molten_pewter |  | `None` |  | any of(config `allow_ingotless_alloys`; tag `c:ingots/pewter` filled) |
| molten_pig_iron | 90 mb `#c:molten_iron` + 500 mb `#tconstruct:meat_soup` + 250 mb `#c:honey` | 180 mb `tconstruct:molten_pig_iron` | 811 |  |
| molten_queens_slime | 90 mb `#c:molten_cobalt` + 90 mb `#c:molten_gold` + 250 mb `#c:magma` | 180 mb `tconstruct:molten_queens_slime` | 1150 |  |
| molten_refined_obsidian | 250 mb `#tconstruct:molten_obsidian` + 100 mb `#tconstruct:molten_diamond` + 90 mb `#c:molten_osmium` | 90 mb `#c:molten_refined_obsidian` | 1475 | tag `c:ingots/refined_obsidian` filled; tag `c:ingots/osmium` filled |
| molten_rose_gold | 90 mb `#c:molten_copper` + 90 mb `#c:molten_gold` | 180 mb `#c:molten_rose_gold` | 550 |  |
| molten_signalum | 270 mb `#c:molten_copper` + 90 mb `#c:molten_silver` + 400 mb `#c:redstone` | 360 mb `#c:molten_signalum` | 999 | tag `c:ingots/signalum` filled; tag `c:ingots/copper` filled; tag `c:ingots/silver` filled; tag `c:redstone` filled |
| molten_slimesteel | 90 mb `#c:molten_iron` + 250 mb `#tconstruct:sky_slime` + 250 mb `#tconstruct:seared_stone` | 180 mb `tconstruct:molten_slimesteel` | 900 |  |
