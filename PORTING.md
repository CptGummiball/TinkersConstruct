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

## Settled: registry-aware serialisation

1.21 requires `RegistryFriendlyByteBuf` for anything serialising registry contents, but
Mantle's `Loadable` passed a plain `FriendlyByteBuf`. **Resolved** by widening `encode`/
`decode` across the framework (271 references in 82 files) rather than casting at call
sites, which would have compiled and then failed at runtime on any plain buffer. JSON-side
registry access arrives through the new `ContextKey.REGISTRY_ACCESS`.

That unblocked:

- **`IngredientLoadable`** — now uses `Ingredient.CODEC` with `RegistryOps` and
  `CONTENTS_STREAM_CODEC` on the wire.
- **`ItemStackLoadable`** — the legacy `"nbt"` blob maps to `DataComponents.CUSTOM_DATA`,
  and the wire format is vanilla's `OPTIONAL_STREAM_CODEC`, which carries *all* components
  rather than only the custom blob. Safe here because every `nbt` payload in the data files
  is either a Tinkers key (`tic_materials`, `tic_broken`, `tank`, `Material`, `metal`) or
  `Damage:0`, which is the vanilla default and so loses nothing. Item stacks carrying real
  vanilla component data would need per-component mapping instead.

- **`Loadables.ENCHANTMENT`** — 1.21 moved enchantments into a **datapack** registry, so a
  static `BuiltInRegistries` lookup cannot reach them. Resolved with `DynamicRegistryLoadable`,
  which reads the registry from `ContextKey.REGISTRY_ACCESS` (JSON) or the buffer's own
  registry access (network).

  It yields `Holder<Enchantment>` rather than a bare `Enchantment`. That is not a stylistic
  choice: `getKey` is handed no context, so a bare value could never be turned back into an id
  — a holder carries its own key. It is also what vanilla's 1.21 enchantment APIs expect.
  **The two call sites (`BreakBlockFluidEffect`, `EnchantmentModule`) must therefore be ported
  to holders**, which they need anyway.

## The recipe-ID problem (phase 3)

1.20 handed the recipe id to the serializer (`fromJson(ResourceLocation id, JsonObject)`),
and Mantle exposed it to recipes as `ContextKey.ID`. **1.21 does not**: a recipe is wrapped
in a `RecipeHolder` that carries the id alongside it, and the serializer never sees it.

**68 references across 67 TConstruct files read `ContextKey.ID`.** Every one needs the id
supplied from its `RecipeHolder` instead of from inside the recipe. This is the single
largest mechanical consequence of the recipe rewrite and it lands in phase 3, not in Mantle
— `LoadableRecipeSerializer` simply stops populating that key.

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
- [~] **2 — Mantle-lite.** Ported: `data.loadable` (unblocks 398 dependent files),
      `data.predicate` (98), `registration.object` (92), `data.registry`, `data.gson`, `util`.
      `recipe.container`. Still to do: the rest of `recipe` (see below), fluid +
      `fluid.transfer`, block/inventory/network, client + book, and a Fabric-native
      replacement for `registration.deferred`/`adapter` (Forge's DeferredRegister model has
      no Fabric counterpart — Fabric registers eagerly).

      **`mantle.recipe`, partially done.** `ingredient` (`SizedIngredient`, `FluidIngredient`,
      `EntityIngredient` — 62 of 65 call sites), `helper` (`ItemOutput`, `FluidOutput`,
      `TagPreference`), `container` and `IMultiRecipe` are in. `recipe.cooking` was dropped
      (zero users). What is left splits into two independent design tasks:

      1. **Custom ingredients.** `FluidContainerIngredient`, `ItemIngredient` and the two
         potion ingredients extend Forge's `AbstractIngredient`. Fabric's counterpart is the
         `CustomIngredient` API in `fabric-recipe-api-v1`: implement `CustomIngredient` +
         `CustomIngredientSerializer` and expose the result via `toVanilla()`. Low urgency —
         one TConstruct call site between them.
      2. **The 1.21 recipe-system rewrite — done.** `LoadableRecipeSerializer` (plus
         `TypeAware` and `Deprecated`), `LoggingRecipeSerializer`, `SimpleRecipeSerializer`,
         `RecipeHelper`, `ICommonRecipe` and `ICustomOutputRecipe` are rewired onto
         `LoadableMapCodec`/`LoadableStreamCodec`. `Container` → `RecipeInput`,
         `RegistryAccess` → `HolderLookup.Provider`, holders throughout `RecipeHelper`
         (`byType` went private; `getAllRecipesFor` is the accessor). API consequences for
         phase 3, all stemming from the recipe-ID problem below: `SimpleRecipeSerializer`
         now takes a `Supplier<T>` (2 call sites), `LoggingRecipeSerializer.fromNetworkSafe`
         lost its id parameter (3), and `RecipeHelper.getJEIRecipes` takes a
         `RecipeHolder` stream.
- [ ] **3 — TConstruct core.** `common`, `shared`, `library`: materials, modifiers, recipe —
      and the **NBT → DataComponents migration** of `ToolStack`, the single largest 1.21 change.
- [ ] **4 — Content.** `fluids`, `smeltery`, `tables`, `tools`, `gadgets`, `world`.
- [ ] **5 — Client.** Custom baked models (tool layers, tanks, casting), renderers, screens.
- [ ] **6 — Mod compat.** EMI, Jade, Trinkets, energy, plus cross-mod recipes for GummiCraft.
- [ ] **7 — Datagen & documentation.**

## Access widener

The Forge `accesstransformer.cfg` (294 entries) uses SRG names without field descriptors,
which AccessWidener requires. Entries are therefore migrated per-module alongside the code
that needs them rather than in one unverifiable batch. Migrated so far:
`Entity.wasEyeInWater`, `WoodType.register`.

## Vanilla removals handled

Things 1.21 deleted outright, where the replacement was a judgement call:

| Removed | Replaced with | Note |
|---|---|---|
| `MobType`, `getMobType()` | entity type tags | JSON names kept (`"mobs": "undead"`), so data files need no migration. Tags are also more capable — a mob had one MobType but can have several tags. |
| `DamageSource.isIndirect()` | `getDirectEntity() != getEntity()` | same meaning: an arrow versus its shooter |
| `Fluid.getFluidType()` | `FluidVariantAttributes` | Forge-only concept |
| `HolderSet.Named.contents` | `.stream().toList()` | field went private |
| `hasEffect(MobEffect)` | `hasEffect(Holder<MobEffect>)` | wrapped at the call site |
| `BlockTags/ItemTags.create(rl)` | `TagKey.create(registry, rl)` | those overloads were Forge additions |
| `MissingMappingsEvent` | *nothing* | Forge remapped renamed registry entries on world load; Fabric has no such hook, and this build targets a new pack with no Forge-era saves |
| Forge `FluidType` | `mantle.transfer.fluid.FluidType` | keeps Forge's shape for 47 files and doubles as a Fabric `FluidVariantAttributeHandler`, so temperature and light are visible to other pack mods too |
| `ForgeSpawnEggItem`, `SpawnEggItem.fromEntityType` | `SpawnEggItem`, `SpawnEggItem.byId` | Forge additions with vanilla equivalents |
| `FriendlyByteBuf.write/readItem`, `write/readFluidStack` | the respective stream codecs | Forge buffer extensions |
| Forge tag preference (config + `TagsUpdatedEvent`) | fixed priority: `minecraft` → `tconstruct` → alphabetical | Fabric has neither the event nor the config. Determinism is the point — an unstable pick would silently change recipe outputs between launches. |

## Build

```bash
JAVA_HOME="/c/Program Files/Java/jdk-21" ./gradlew build
```
