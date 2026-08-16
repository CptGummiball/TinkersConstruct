# Tinkers' Construct → Fabric 1.21.1 (GummiCraft)

Port of Tinkers' Construct 3.11.2 (Forge 1.20.1) to **Fabric 1.21.1**, targeted at the
GummiCraft modpack and its server. Not for upstream contribution — deploy to
`CptGummiball/TinkersConstruct` only.

## Scope

| | |
|---|---|
| TConstruct | 1.905 Java files · 187.337 LOC · 12.557 JSON · 13.268 PNG |
| Mantle (bundled, see below) | 593 Java files · 49.710 LOC |
| **Total port surface** | **~237.000 LOC** |
| Forge-coupled files | 547 (1.445 imports) |
| Mantle-coupled files | 955 |

There is no upstream 1.21.x branch (verified against SlimeKnights) — every line is ported here.

## Architecture decisions

These three choices are what keep a port of this size tractable. Changing any of them
multiplies the work, so they are load-bearing:

1. **Mojang official mappings, not Yarn.** Loom's `officialMojangMappings()` means the
   ~187k lines of `net.minecraft.*` references in the Forge sources stay valid. Yarn would
   require renaming all of them.
2. **Mod id and package stay `tconstruct` / `slimeknights.tconstruct`.** All 12.557 JSON and
   13.268 PNG resource paths keep working untouched, and internal imports stay valid.
3. **Mantle is bundled into the jar under its original `slimeknights.mantle` package.** The
   955 files importing it need no import changes, and the pack gets one jar with no extra
   dependency. Only the subset TConstruct actually uses is ported (~45k of 49.7k LOC).

### Unit convention

Forge measures fluids in **mB** (1.000/bucket); Fabric's Transfer API uses **droplets**
(81.000/bucket). The shim keeps **mB internally** and converts at the `Storage` boundary
(`droplets = mB × 81`). This preserves every fluid amount in the existing recipe JSONs
exactly — no data migration, no rounding drift.

## Incremental port gate

A 237k-line rewrite that only compiles at the very end is unverifiable. `ported.gradle`
lists the packages handed to javac; each finished module is appended, so **every step ends
on a compiling build**. `ported-tests.gradle` does the same for `src/test`, and
`unported.gradle` blocks individual files inside an otherwise-ported package.
`-Pport.all=true` ignores all three and attempts a full compile.

## Open decision: buffer type

**This is the next thing to settle — several blocked items all trace back to it.**

1.21 requires `RegistryFriendlyByteBuf` for anything that serialises registry contents, but
Mantle's `Loadable` interface passes a plain `FriendlyByteBuf`. Widening `encode`/`decode`
across the framework is the correct fix; casting at the call site would compile and then
fail at runtime whenever a plain buffer is handed over.

Blocked on it right now:

| Item | Why |
|---|---|
| `IngredientLoadable` | 1.21 ingredients serialise via `CONTENTS_STREAM_CODEC`, registry-aware |
| `ItemStackLoadable` | components replaced NBT; stack codecs are registry-aware |
| `Loadables.ENCHANTMENT` | enchantments moved to a datapack registry, needs `HolderLookup.Provider` (2 call sites) |

## Deferred, tracked so it is not lost

- `JsonHelper.syncPackets`/`sendPackets` were removed when porting `JsonHelper` — they are
  networking, not JSON, and belong with the network module in phase 3.
- Fluid amounts in data files stay in mB; no JSON migration is needed (see Unit convention).
- Data files keep `forge:`-namespaced condition types, read by the ported condition layer.
  Material/ore **tags**, however, now resolve against `c:` — see the `COMMON` note in
  `Mantle.java`. Tag JSONs still referencing `forge:` need a sweep in phase 3.

## Target environment

Fabric Loader 0.19.3 · Minecraft 1.21.1 · Java 21 · Fabric API 0.116.15 · 261 pack mods.

Compat targets present in GummiCraft (these replace the Forge build's assumptions):

| Forge build used | GummiCraft has | Consequence |
|---|---|---|
| JEI | **EMI** 1.1.24 | `plugin/jei` is replaced by an EMI plugin |
| The One Probe | **Jade** 15.10.5 | new Jade provider |
| Forge capabilities | Fabric Transfer API | fluid/item shim layer |
| Forge config | **Forge Config API Port** 21.1.6 | `Config` ports near-unchanged |
| Curios | **Trinkets** 3.10.0 | modifier/accessory hooks |
| Forge Energy | Team Reborn Energy | Oritech · Energized Power · Refined Storage 2 |

## Phases

- [x] **0 — Build foundation.** Loom build, `fabric.mod.json`, access widener, mixin configs,
      entrypoints, port gate.
- [x] **1a — Transfer layer.** `FluidStack` ↔ `FluidVariant`, `IFluidHandler`, `FluidTank`,
      item handlers, both-direction `Storage` bridges, capability lookups via `TransferUtil`.
- [x] **1b — Tool actions & recipe conditions.** `ToolAction`/`ToolActions`;
      condition layer reading the existing `forge:`-namespaced blocks.
- [ ] **1c — Remaining shims.** Event bus → Fabric events + mixins, registry helpers
      (`DeferredRegister`/`RegistryObject`), Forge model loaders, `FluidType` (47 files).
- [~] **2 — Mantle-lite.** `data.loadable` framework ported (398 dependent files unblocked);
      `util`, `data.registry`, `data.gson` ported. Still to do: predicates, registration,
      recipe helpers, fluid, block/inventory/network, client + book.
- [ ] **3 — TConstruct core.** `common`, `shared`, `library`: materials, modifiers, recipe —
      and the **NBT → DataComponents migration** of `ToolStack`, the single largest 1.21 change.
- [ ] **4 — Content.** `fluids`, `smeltery`, `tables`, `tools`, `gadgets`, `world`.
- [ ] **5 — Client.** Custom baked models (tool layers, tanks, casting), renderers, screens.
- [ ] **6 — Mod compat.** EMI, Jade, Trinkets, energy, plus cross-mod recipes for GummiCraft.
- [ ] **7 — Datagen & documentation.**

## Access widener

The Forge `accesstransformer.cfg` (294 entries) uses SRG names without field descriptors,
which AccessWidener requires. Entries are therefore migrated per-module alongside the code
that needs them rather than in one unverifiable batch.

## Build

```bash
JAVA_HOME="/c/Program Files/Java/jdk-21" ./gradlew build
```
