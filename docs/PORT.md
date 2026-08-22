# The Fabric port

Tinkers' Construct 3.11.2, ported from Forge 1.20.1 to **Fabric 1.21.1** — a complete port of
the mod and its embedded copy of Mantle, not a shim layer. The full engineering log with every
decision lives in [PORTING.md](../PORTING.md); this page is the distilled map.

## What runs

Everything: all blocks, items, fluids and tools; the smeltery and foundry with their
multiblock logic, melting, alloying, casting and entity melting; the whole tool system
(materials, parts, modifiers, slotless/upgrade/ability slots, tool definitions driven by
data); armor including the slimesuit and plate sets with trims; world generation (slime
islands, geodes, bosses' heads); the puny smelting / mighty smelting / fantastic foundry /
tinkers' gadgetry books; and the pack-facing data systems (recipe conditions, loot
injections, the recipe remover, tag preferences).

Every file under `src/generated/resources` — 3,036 recipes, all tags, loot tables,
advancements, models, 10.6k generated textures, structure NBTs — regenerates on Fabric,
verified byte-for-byte (or provably better, e.g. structure NBTs now carry the 1.21
DataVersion). Datagen runs with `gradlew runDatagen`.

## How Forge concepts were mapped

| Forge | This port |
|---|---|
| Capabilities (items/fluids/energy) | Fabric transfer API through a mantle `transfer` layer; energy via Team Reborn Energy |
| `FluidType` (temperature, viscosity, textures) | mantle `FluidType` + `FluidVariantAttributes`; textures via `mantle/fluid_texture` JSON and a render handler |
| Recipe/advancement `conditions`, `forge:conditional` | evaluated by a `RecipeManager`/advancement mixin before parsing (`ConditionalDataFilter`) |
| Global loot modifiers | plain loot-pool injection (`mantle/loot_injectors`) applied after the loot registry bakes |
| Tag `remove` extension | honoured by a `TagLoader` mixin |
| `BlockEntityType` extension for skulls | `addSupportedBlock` (Fabric API) |
| Custom `ItemDisplayContext` values | ids resolved to vanilla fallbacks at render time; the item-placement data keeps the id |
| Forge model loaders (`connected`, `item_layer`, tool/material/tank models) | mantle geometry loader registry on Fabric's model plugin |
| Forge config | Forge Config API Port (`ForgeConfigSpec` becomes `ModConfigSpec`) |
| Access transformers | `tconstruct.accesswidener` |
| Forge registry events / DeferredRegister | mantle registration adapters over Fabric registries |
| `GatherDataEvent` | `fabric-datagen` entrypoint (`TConstructDataGenerator`), client run |
| Forge tag preference (recipe outputs) | config-driven namespace list `tagPreferences` |
| `forge:` conventional tags | `c:` tags (fabric conventions), with datagen writing the same names the runtime reads |

## Verification harnesses

Loom run configs used throughout the port; all boot a world and exit or are killed after:

- `runClientWorld` — boots into a world, the standard regression gate (recipe/tag/advancement
  parse failures, loot injections, model resolution).
- `runClientBlocks` — block harness: places and exercises machines.
- `runClientCommands` — scripted command harness with PASS/FAIL: gives and modifies a tool,
  dumps tags, generates part textures in game, exports a book to images, runs the recipe
  remover, screenshots the chat.
- `runClientEmi`, `runClientCompat` — EMI recipe display and pack-compat checks.
- `runDatagen` — regenerates everything; `scripts/generate_recipe_docs.py` rebuilds the
  recipe reference from the output.

## Known deviations from Forge

- **Custom item display contexts** are collapsed to their vanilla fallbacks when rendering
  (the enum is closed on Fabric); placement, scale and rotation still come from the item
  lists, so casting tables and melters look right — only per-context model overrides are lost.
- **Fluid fog/overlay tuning** (`ClientTextureFluidType`'s fog shape and screen overlay) has
  no Fabric hook yet; fluids use standard fog. The data carries the values for a later pass.
- **Milk is `tconstruct:milk`**: Fabric has no shared milk fluid, so the port registers its
  own and maps the standard bucket to it in recipes.
- **The recipe remover is a command** (`/mantle remove_recipes`), as upstream intended —
  nothing removes recipes automatically.
- **EMI shows a remainder quirk** on shaped material recipes (cosmetic; the craft itself is
  correct).
- A benign `Multiple fuel recipes for solid fuel` warning appears in single player (the local
  client and server both register the lookup).

## Repository layout notes

- `ported.gradle` / `unported.gradle` — the port gate that carried the migration; with the
  port complete, `ported.gradle` whitelists everything that compiles (which is everything
  except deliberately dropped Forge-only classes listed with reasons in `unported.gradle`).
- `src/main/java/slimeknights/mantle` — Mantle, embedded and ported alongside.
- `src/main/java/slimeknights/tconstruct/fabric` — entrypoints, mixins and dev harnesses
  added by the port.
- `PORTING.md` — the full engineering log, slice by slice.
