# Pack integration (GummiCraft)

How to run this port inside a modpack, written against GummiCraft 1.2.x but applicable to any
Fabric 1.21.1 pack.

## Installing

Drop `TinkersConstruct-Fabric-1.21.1-<version>.jar` into `mods/`. Requirements:

- Fabric API
- Forge Config API Port (supplies the config system)

Optional but supported: EMI (full recipe display), Jade (smeltery/tank overlays), Trinkets,
any Team Reborn Energy mod for the energy-holding modifiers.

## Tag preferences and Unify

When several mods provide the same metal, recipe outputs pick the item whose namespace comes
first in `config/tconstruct-common.toml`:

```toml
[gameplay]
	tagPreferences = ["minecraft", "techreborn", "modern_industrialization", "energizedpower"]
```

Keep this list aligned with the output preferences of the pack's unifier (GummiCraft: Unify).
If Unify prefers a different mod's ingots, put that mod first here too — otherwise the
smeltery casts one mod's ingot while crafting uses another's. The GummiCraft mrpack carries
this file as an override; the phase-6 compat check (`PORTING.md`, slice 14) verified the
steel round-trip against Unify's replacements.

## Compat metals

Melting, casting and alloying for other mods' materials are entirely tag-driven: each compat
metal's recipes load when its `c:` tag is filled (see the conditions column in
[reference/melting.md](reference/melting.md)). The supported compat metals are aluminum,
cadmium, chromium, lead, nickel, osmium, platinum, silver, tin, tungsten, uranium and zinc,
plus the alloy family (bronze, brass, electrum, invar, constantan, pewter, enderium, lumium,
signalum, refined glowstone/obsidian and more — see [reference/alloys.md](reference/alloys.md)).
Whichever of these GummiCraft's tech mods provide light up automatically — no config needed.
Ores melt with byproducts in the foundry and boosted rates in the smeltery.

## Removing vanilla recipes

The port ships four data-driven removal presets and a command that applies them to whatever
recipes are actually loaded (including other mods' recipes of the same types):

```
/mantle remove_recipes preset tconstruct:vanilla_tools
/mantle remove_recipes preset tconstruct:ingot_smelting
/mantle remove_recipes preset tconstruct:nugget_smelting
/mantle remove_recipes preset tconstruct:netherite_smithing
```

- `vanilla_tools` — removes crafting for vanilla-style tools, weapons, armor, shields, bows,
  crossbows, fishing rods, shears, flint and steel and brushes (anything not a tinkers tool).
- `ingot_smelting` / `nugget_smelting` — removes furnace/blast furnace smelting for ingots
  and nuggets that the smeltery can produce, pushing recycling through the smeltery.
- `netherite_smithing` — removes vanilla netherite gear upgrades.

Each run writes disabled-recipe files into the world's `datapacks/SlimeKnightsGenerated`
pack; run `/reload` (or restart) to apply. To ship the removals with the pack, copy that
generated datapack out of the world and distribute it as a global pack — the files are plain
`forge:conditional` stubs this port always understands. Presets are data
(`data/<ns>/mantle/remove_recipes/*.json`), so a pack can add its own.

## Reference

The full generated reference of everything this build ships is in
[reference/](reference/README.md): every melting, alloying and casting recipe with
temperatures and conditions, every material with stats and traits, every modifier recipe
with inputs and slot costs, and every remaining recipe grouped by type.

## Log lines that are fine

- `Multiple fuel recipes for solid fuel ... may cause desyncs` — single-player double
  registration, cosmetic.
- `No data fixer registered for tconstruct:...` — vanilla noting modded block entities have
  no datafixers, standard for all mods.
- EMI `Untranslated tag`/`recipes loaded with the same id` lines — display-side notes from
  EMI's indexing, not load errors.
