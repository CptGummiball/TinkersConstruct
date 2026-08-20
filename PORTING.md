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

## The recipe-ID problem (phase 3) — **RESOLVED in phase 5 slice 5**

Both halves are done: the JSON path publishes the id through `CurrentRecipeId`, and the
network path writes the id into the serializer's own payload. See that slice for why the
network half only surfaced once a client actually joined a world.


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
### Core-slice status (phase 3): **COMPLETE — full build green (compile + remap + jar)**

The slice went 2987 → 0. The library heart (materials, modifiers, json, tools, ToolStack)
compiles and remaps. Load-bearing decisions made on the way down:

- **`TinkerDataCapability`** — weak map keyed by entity identity (runtime-only data).
- **`PersistentDataCapability` / `EntityModifierCapability`** — Cardinal Components entity
  components (`TinkerComponents` registers both for `Entity.class`; persistent data uses
  `RespawnCopyStrategy.ALWAYS_COPY` + explicit join sync; entity modifiers serialize
  nothing while empty, write access stays gated by `supportCapability`).
- **ToolStack write-through**: `TagCompat.getTag` copies out of `minecraft:custom_data`,
  so `ToolStack.from(stack)` binds the stack and every nbt-mutating setter (plus the
  `WriteThroughToolData` persistent-data view) flushes via `writeBack()` →
  `TagCompat.setTag`. `TagCompat.getOrCreateTag` re-anchors a fresh owned tag on every
  call, which keeps the 12 loose `getOrCreateTag().put…` sites live-mutating (the fresh
  `CustomData` instance also keeps vanilla change-detection snapshots valid).
- **`ContentLookups` seam** (`fabric` package): library reads of content-module singletons
  (fishing hook + indestructible entity types, enderference/bleeding effects,
  PROTECTION_CAP attribute, overslime/overworked ids, material/data recipe types and
  serializers, part builder stack, material block entity type, worktable modifier-set
  reader) are lazy by-ID registry lookups — semantically what `RegistryObject` was.
  Phase 4 may revert call sites to the content statics; both read the same entry.
- **`ContainerRecipeInput`**: a type cannot implement both `Container` and `RecipeInput`
  (one named signature maps to two intermediary names — an unfixable remap conflict, found
  by `remapJar`). Containers stay `Container`; `ICommonRecipe<C extends IRecipeContainer>`
  implements `Recipe<ContainerRecipeInput<C>>` and delegates to the 1.20-shaped
  `matches(C, Level)`.
- **Item hook surface**: Forge-only `IForgeItem` methods on `ModifiableItem` /
  `ModifiableLauncherItem` remain as plain Tinkers-called API (no `@Override`);
  `FabricItem` supplies `allowComponentsUpdateAnimation` / `allowContinuingBlockBreaking`.
  Syncing the vanilla damage component for tools is a phase-4 item-settings task.
- **1.21 sweeps landed**: attribute modifiers keyed by `ResourceLocation`
  (AttributeModule + melee/max-armor variants), enchantment hooks on
  `Holder<Enchantment>` with component reads/writes, `ModifierManager` resolves
  enchantment ids lazily against the server registry (datapack registry in 1.21) and
  expands tag mappings on resolve, the harvest-enchant swap runs on the `ENCHANTMENTS`
  component, `ToolActionTransforms` replaces Forge's `getToolModifiedState` (vanilla maps;
  tilling rules mirrored, modded tillables noted as a compat gap), `OffhandCooldownTracker`
  + `SwingArmPacket` ported, `BlockSideHitListener` on `AttackBlockCallback` (break-XP
  bridge waits on the event layer), `LootingLevelEvent`/`LivingEvent`/`Event.HasResult`
  shims added, `DummyArmorMaterial` registers a zeroed `Holder<ArmorMaterial>`.

Still parked in `unported.gradle`, each with its reason: content-bound loot files and
ranged/armor items (phase 4), the tool fluid-capability step (`MobEquipment`), the
event-layer step (explosions, `SlimeBounceHandler`), the energy step,
`ToolStackItemPredicate` (ItemSubPredicate redesign), and commands
(`MaterialRegistry.getTagSource`).

- [~] **2 — Mantle-lite.** Ported: `data.loadable` (unblocks 398 dependent files),
      `data.predicate` (98), `registration.object` (92), `data.registry`, `data.gson`, `util`.
      `recipe.container`, **`fluid` + `fluid.transfer`** (transfer helper, container
      transfers with Fabric-native reload listener, `FluidBuilder`, `ForgeFlowingFluid`
      shim, `FabricFluidHandlerItem`, `FluidType.of()` lookup, `MantleTags`,
      `TranslationHelper`), and both fluid registration objects. Done since: **`block` + `inventory`**, **`item`**, and **`registration.deferred`
      rebuilt Fabric-native** — same API, eager semantics. `SynchronizedDeferredRegister`
      registers immediately and returns filled `RegistryObject`s, which removes the
      empty-holder failure mode entirely. The fluid builder was the tricky case: Forge
      registered bucket/block against `DelayedSupplier`s resolved later, but vanilla's
      `BucketItem`/`LiquidBlock` need their fluid at construction — so the builder now
      stores factories and registers bucket/block *after* the fluids, then wires
      `FluidType.register` for the `of()` lookup and Fabric attribute handlers.
      **Custom ingredients — done.** `ItemIngredient`/`PotionIngredient`/
      `PotionDisplayIngredient`/`FluidContainerIngredient` implement Fabric's
      `CustomIngredient`; `LoadableIngredientSerializer` is a `CustomIngredientSerializer`
      over the loadable codec bridges, registered in the bootstrap under the same
      `mantle:` ids Forge used. The shipped flat `mantle:fluid_container` JSON shape keeps
      parsing via a custom record field mirroring the 1.20 parser. Factories return
      vanilla `Ingredient` via `toVanilla()`. Potion matching runs on the
      `potion_contents` component; `null` replaces the removed empty-potion sentinel.

      **`InvertedFluid` — done.** The feared 1.21 rewrite was unnecessary: the FlowingFluid
      spread refactor landed in 1.21.2, not 1.21.1, so the inverted overrides compile
      against the 1.20-shaped internals with three access-widener entries
      (`getCacheKey`, `canPassThrough`, `isWaterHole`; the last is final in vanilla and
      needed `extendable`). Unblocks ichor and molten cinderslime for phase 4.

      Still to do: client + book (phase 5), `registration.adapter` (unused so far),
      datagen helpers (phase 7).

      **`network` — done and live.** `NetworkWrapper` is Fabric-native
      (`CustomPacketPayload` + per-packet types derived from channel name + registration
      index, which is deterministic for the same reason Forge's numeric ids were) behind
      the Forge-shaped registration/sending API. `NetworkEvent.Context` survives as a shim
      so the 26 handler call sites port by import rewrite; Fabric play handlers already run
      on the game thread, so `enqueueWork` executes immediately and `IThreadsafePacket`
      semantics hold. S2C receivers collect during common init and register from the client
      entrypoint (`NetworkWrapperClient`), since `ClientPlayNetworking` does not exist on a
      dedicated server. The container-item sync moved from `OnDatapackSyncEvent` to the
      Fabric join event. The five lectern/book packets register with the book module
      (phase 5); `SwingArmPacket` waits on `OffhandCooldownTracker` (phase-3 tool logic).

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
- [x] **3 — TConstruct core.** `common`, `shared`, `library`: materials, modifiers, recipe —
      and the **NBT → DataComponents migration** of `ToolStack`, the single largest 1.21 change.
- [~] **4 — Content.** `fluids`, `smeltery`, `tables`, `tools`, `gadgets`, `world`.

### Phase 4, first slice: the shared module — **DONE, server boots clean (Done 0.534s)**

`TinkerCommons`, `TinkerMaterials`, `TinkerEffects`, `TinkerAttributes`, `TinkerFood` and
their block/item/effect/inventory/particle packages register eagerly from the bootstrap
(order: attributes → effects → commons → materials, mirroring Forge's bus order).
Load-bearing decisions:

- **First mixin of the port**: `DefaultAttributesMixin` wraps `DefaultAttributes.getSupplier`
  and merges Tinkers' attributes into every living entity's supplier
  (`TinkerAttributeInjector`, cached per supplier; player-only attributes keyed off
  `EntityType.PLAYER`). Replaces Forge's `EntityAttributeModificationEvent` and is
  order-independent for entities other mods register later. AW opens
  `AttributeSupplier.instances` + its constructor.
- **`TinkerAttributes` hands out `Holder<Attribute>`** (1.21 attribute APIs are
  holder-typed); `AttributeDeferredRegister` returns canonical registry holders.
  New `tconstruct:generic.swim_speed` replaces `forge:swim_speed` (no vanilla equivalent;
  travel hook lands with the event layer). The other Forge attributes mapped to vanilla:
  `block_reach`→`player.block_interaction_range`, `entity_reach`→`player.entity_interaction_range`,
  `entity_gravity`→`generic.gravity`, `step_height_addition`→`generic.step_height` —
  the 9 generated modifier JSONs using them were updated in place (datagen re-emits in phase 7).
- **1.21 `MobEffect` API**: `shouldApplyEffectTickThisTick`, boolean `applyEffectTick`,
  holder-keyed `addAttributeModifier` with `tconstruct:effect.*` ResourceLocation ids,
  `TinkerEffect.holder()` caches the registry holder. The curative-items API is gone:
  `NoMilkEffect` is a marker (`isCuredByMilk()`), consumed by `CheeseItem` now and by the
  milk hook once the event layer lands. Effect visibility (`show` flag) and the helmet
  charge-bar icon are phase-5 client work.
- **Brewing** goes through `FabricBrewingRecipeRegistryBuilder.BUILD`; the congealed-slime
  ingredients are looked up by ID at build time and skipped while the world module is still
  unported (mixes self-activate when it lands).
- **Copper platforms**: the Forge `getToolModifiedState`/`use` overrides are gone — pairs
  register with `OxidizableBlocksRegistry` (scrape/wax/unwax/oxidize all via vanilla logic).
  Knightmetal pathfinding: `LandPathNodeTypesRegistry` (`DAMAGE_OTHER`).
  Beacon tinting: vanilla `BeaconBeamBlock` instead of Forge's color-multiplier hook.
  1.20.5 renames handled: `GlassBlock`→`TransparentBlock` (TintedGlassBlock kept its name),
  `isPathfindable` 2-arg, `getUpdateTag(HolderLookup.Provider)`, `BlockSource` record,
  Forge `ItemTags/BlockTags.create` → `TagKey.create`.
  Known gap: slimesteel piston stickiness (Forge `isSlimeBlock`/`canStickTo`) needs a piston
  mixin — event-layer note.
- **TiC custom ingredients** (`NoContainerIngredient`, `BlockTagIngredient`,
  `MaterialIngredient`, `MaterialValueIngredient`, `ToolHookIngredient`, base
  `NestedIngredient`) on Fabric's `CustomIngredient`, sharing Mantle's
  `LoadableIngredientSerializer`. `NestedIngredientField` keeps the 1.20 flat-or-`match`
  JSON shape and strips `type`/`fabric:type` before re-parsing flat forms (avoids codec
  recursion). Data-file caveat for the recipe round: ingredients in vanilla slots need
  `fabric:type` keys — scripted fixup or datagen re-emit.
- **Loot/conditions on 1.21 MapCodecs**: `ConfigEnabledCondition` (dual recipe+loot),
  `BlockOrEntityCondition`, `HasLootContextSetCondition`, and a fresh `mantle.loot`
  subset (`MantleLoot`: `tag_filled`/`tag_empty` conditions + `tag_preference` entry —
  used by 17 generated loot tables and the lustrous loot modifiers). Mantle's tag recipe
  conditions registered via the new `IConditionSerializer` shim. The deprecated
  `tconstruct:tag_not_empty`/`tag_preference` aliases were **dropped** (zero data uses).
- **`BlockContainerOpenedTrigger`** rewritten as a codec-based `SimpleCriterionTrigger`;
  `FluidParticleData` on `MapCodec` + `StreamCodec` (command-string deserializer removed).
- **Config timing on Fabric**: values are not readable during mod init —
  `Config.init()` registers the specs first in the bootstrap, and the knockback-resistance
  sync tweak moved behind `ForgeModConfigEvents.loading`.
- **`TinkerBookItem`** is a plain tooltip item carrying `BookType` until the book module
  (phase 5) hooks the client open. Commands, `SlimeBounceHandler`, `CommonsEvents`/
  `AchievementEvents` (jump/interact/craft events), datagen and the client classes stay
  parked with notes in `unported.gradle`.
- **Boot noise that is expected to self-heal**: 3 tag errors referencing tables/smeltery
  blocks and 8 fluid-container-transfer entries referencing fluids/gadgets items — both
  resolve as those modules gate in. `FillFluidContainerTransfer`/`Empty…` serializers now
  register in the bootstrap (Forge Mantle did it in its mod constructor).

### Phase 4, second slice: the fluids module — **DONE, server boots clean (Done 0.946s)**

All ~90 fluids register (slimes, foods, stones, ores, alloys, compat metals, potion),
including the two upward-flowing InvertedFluids (ichor, molten cinderslime) enabled by the
earlier access-widener work. The transfer-info boot errors from the shared round are gone —
the bottle items and fluids they referenced now exist. Load-bearing decisions:

- **Item fluid containers on Fabric storage**: `TinkerFluidStorage` registers
  `FullItemFluidStorage` for venom/slime/magma bottles (drain-all → glass bottle) and a
  potion-aware storage for the potion bucket; replaces Forge `initCapabilities` and the
  powdered-snow `AttachCapabilitiesEvent` (now a registration on the vanilla item in
  `FluidEvents.init`). The mantle bridge (`TransferUtil.getFluidHandlerItem`) resolves the
  same lookup, so recipes see identical behavior. `ConstantFluidContainerWrapper` survives
  as a plain `IFluidHandlerItem` for future in-code use.
- **Potion bucket dual representation**: the item carries the vanilla `potion_contents`
  component (vanilla naming/tooltips work), the fluid keeps the legacy `Potion` NBT key the
  recipe data expects; `PotionFluidType` converts at the boundaries. `PotionUtils` is gone.
- **Brewing-stand limitation found**: 1.21's `PotionBrewing.Builder` validates containers
  via `expectPotion` — only `PotionItem`s allowed, so the Forge-era generic
  `BrewingRecipe`s (glass bottle + congealed slime → slime bottle, bottle → splash/lingering
  bottle, magma bottle) cannot register through the vanilla builder. They return with a
  `PotionBrewing` mixin in the event-layer step; `BottleBrewingRecipe` was deleted with the
  rest of the Forge brewing coupling.
- **Fluid shim growth**: `FluidType.Properties` stores the Forge behavior hooks
  (motionScale, canExtinguish, canSwim/canDrown, pathType/adjacentPathType) for the event
  layer; `FluidType` gained stack-sensitive `getDescriptionId/getDescription/getBucket`
  virtuals; `FluidDeferredRegister` gained `invertedFlowing()`; `FluidObject.getCommonTag()`
  exposed. `LiquidBlock` subclasses pass the fluid directly (1.21 constructor); the Forge
  `getFluidTypeHeight` immersion check became a fluid-surface height comparison.
### Phase 4, third slice: the world module — **DONE, server boots clean (Done 0.891s)**

Slime islands (structures + structure sets), slime trees/fungi (features, tree decorator,
root placer), congealed/sticky slime blocks, dirt/grass/foliage/wood sets, geodes, heads,
cobalt ore, and the three slime entities all register and load. Load-bearing decisions:

- **Worldgen datapack restored and migrated**: the phase-3 `processResources` exclude on
  `data/tconstruct/worldgen` was lifted (that exclude — not a loader bug — was why placed
  features were invisible at first; found via a broken-JSON probe). The 1.20-era JSONs needed
  two migrations: the 1.20.5 IntProvider flattening (`{"type":"uniform","value":{...}}` →
  flat keys; geodes, ender trees, clay island) and `minecraft:grass`→`minecraft:short_grass`.
  `trim_material` stays excluded until the tools round.
- **Forge biome modifiers → Fabric BiomeModifications** in `TinkerWorld.init()`: cobalt ore
  (nether, underground decoration), four geodes (overworld/nether/end selectors incl. the
  sky-geode ocean/beach/river exclusion and the ender-geode central-island exclusion), and
  the three mob spawns, mirroring the deleted `forge:biome_modifier` data files.
- **Spawn placements** register through the AW-opened vanilla `SpawnPlacements.register`;
  vanilla slime gets the earth-slime-spawn predicate OR-merged onto its existing rules by
  rebuilding its `SpawnPlacements.Data` record.
- **1.21 API sweeps**: `TreeGrower` is data-driven (no subclassing) — `SlimeTree` became a
  factory, the ender 85/15 tall-tree split maps onto the secondary-tree constructor;
  `FungusBlock` constructor reordered; `SynchedEntityData.Builder`; 4-arg `finalizeSpawn`;
  `dropCustomDeathLoot(ServerLevel,...)` without the looting parameter (bonus moved into the
  enchantment framework); vanishing curse check via `EnchantmentEffectComponents
  .PREVENT_EQUIPMENT_DROP`; ender slime teleport-on-hit moved from the removed
  `doEnchantDamageEffects` to a health-delta check in `dealDamage`; tree decorator/root
  placer/structure types take `MapCodec`s; `BushBlock` subclasses implement `codec()`.
- **Fabric registries replace Forge patches**: composting (`CompostingChanceRegistry`),
  flammability (`FlammableBlockRegistry`), congealed-slime path type (STICKY_HONEY via
  `LandPathNodeTypesRegistry`); firework star shapes + skull block entity injection through
  AW-opened vanilla maps; `hasChunksAt` replaces Forge's `isAreaLoaded`.
- **Known gaps, tracked**: sticky slime piston rules and the enderman-mask head need
  event-layer mixins (predicates kept on the blocks); `WorldEvents` (head drops on charged
  creeper kills, mob-head stealth, wandering trader ancient tools) and
  `AncientToolItemListing` park for the event layer + tools; travelers/plate helmets on
  armored slimes resolve through a `ContentLookups` seam once tools lands. The "No data
  fixer registered" boot lines are standard Fabric noise for modded entities.

- **Misc 1.21**: cauldron interactions return `ItemInteractionResult` and live in
  `map()`-wrapped records; `FluidDataSerializer` is stream-codec based and registers via
  `EntityDataSerializers.registerSerializer`; dispenser bucket behavior on the `BlockSource`
  record with 4-arg `emptyContents`; blazing blood fuel via `FuelRegistry`; creative tab
  lost `withSearchBar` (removed in 1.21). Deferred: `FluidClientEvents` (phase 5),
  `fluids/data` providers (phase 7), copper can/tank tab variants + `TinkerSmeltery`'s
  import of the deleted TiC `EmptyPotionTransfer` alias → smeltery round registers
  mantle's `EmptyPotionTransfer` instead. Forge's milk fluid does not exist on Fabric;
  the milk decision lands with the smeltery recipe pass.

### Phase 4, fourth slice: the recipe tree + tables module — **DONE**

The biggest slice so far: `library/recipe/**` (the full recipe-type tree, 43 runtime recipe
classes) plus `tables/**` (five stations, three chests, menus, packets). Started at 2114
errors, shipped at 0. Load-bearing decisions:

- **The recipe-ID problem**: 1.21 moved recipe ids out of `Recipe` onto `RecipeHolder`.
  Mantle's loadable infrastructure keeps providing `ContextKey.ID`, so loadable-based
  recipes keep their id field (MaterialRecipe pattern). `SimpleRecipeSerializer` became a
  `Supplier`-based codec shim — recipes it builds either need no id
  (`CraftingTableRepairKitRecipe` went no-arg) or get their canonical one
  (`TinkerStationRepairRecipe`). Sync packets carry `holder.id()`; the table BEs cache the
  `RecipeHolder` alongside the recipe for that.
- **`ContainerRecipeInput` bridge**: a container type cannot also implement 1.21's
  `RecipeInput` (remap conflict on `getItem`/`isEmpty`), so Mantle wraps containers for the
  vanilla surface and `ICommonRecipe` keeps the container-typed `matches` for
  implementations. Every own-type `getRecipeFor` call wraps its container; results unwrap
  via `RecipeHolder::value`. `RecipeManager.byType` went private → `getAllRecipesFor`,
  sorted by holder id (part builder button order stays deterministic).
- **CustomRecipe family on `CraftingInput`**: overslime/repair-kit/modifier-repair crafting
  recipes moved to `matches/assemble(CraftingInput, ...)`, remainders via Fabric's
  `getRecipeRemainder()`. `ResultSlot.player/removeCount` went private → CraftingResultSlot
  tracks its own crafter/amount and mirrors the vanilla quick-craft hooks.
- **Menus open by `BlockPos`** through `ExtendedScreenHandlerFactory` (mantle
  `NetworkHooks.openScreen`); all six table packets now `encode(RegistryFriendlyByteBuf)`.
  Slot backgrounds moved from Forge's `setBackground` to vanilla `getNoItemIcon`; armor
  slots check `getEquipmentSlotForItem` and `PREVENT_ARMOR_CHANGE` (binding curse).
- **Block interaction split**: chest insert logic lives in `useItemOn` (falls through to
  `useWithoutItem`, which opens the GUI via mantle `InventoryBlock`); pick-block moved to
  the 3-arg `getCloneItemStack` (the 5-arg player-sensitive form was a Forge hook). The
  dyeable tinkers chest reads/writes the `DYED_COLOR` component; chest inventories persist
  through `minecraft:custom_data` ("TinkerData"), matching the ToolStack tag decision.
- **Fabric storage**: new `ItemStorageBridge` (IItemHandler → `Storage<ItemVariant>`)
  registered for all seven table/chest block entities in `TinkerTables.init()` — hoppers
  and pipes work like the Forge item-handler capabilities did.
- **Seams for unported modules**: ~25 serializer/`getSerializer()` statics from
  `TinkerModifiers`/`TinkerSmeltery` resolve lazily via `ContentLookups.recipeSerializer`;
  toast icons, overslime (`ContentLookups.OVERSLIME` + `LazyModifier`), modifier crystals,
  and the anvil's fake-storage-block item go through the same seam class.
- **Parked**: 35 datagen `*Builder` files (phase 7), six smeltery-bound casting recipes
  (smeltery round), the three `ShapedMaterialRecipe` variants (need a 1.21
  `ShapedRecipePattern` rewrite), client screens/`TableClientEvents` (phase 5).
- **Debug lesson recorded**: a duplicate simple class name (`EmptyItemHandler` twice)
  aborts Lombok for the whole batch and surfaces as ~1300 bogus "constructor missing"
  errors on unrelated files. Found by gate bisection; the collision, not any of the
  reported files, was the root cause.

### Phase 4, fifth slice: the smeltery module — **DONE, server boots clean (Done 0.684s)**

The multiblock heart of the mod: seared/scorched sets, smeltery + foundry controllers,
melter, alloyer, heater, casting table/basin, tanks, faucet, channel, drains/ducts/chutes,
lanterns, fluid cannons, proxy tank, plus the six previously-parked casting recipes and
`common/multiblock` + `library/fluid`. 333 errors at gate-in, shipped at 0. Load-bearing
decisions:

- **The capability shim** (`mantle/transfer/cap`): the smeltery's neighbor caches are built
  on Forge's `LazyOptional` invalidation contract — drains hand out lazy views of the
  controller tank and drop them via listeners when the structure changes. Fabric's lookup
  API has no invalidation callback, so the shim keeps that exact contract internally
  (`LazyOptional`, `Capability`, `ForgeCapabilities`, `ICapabilityProvider` on
  `MantleBlockEntity`, `CapabilityHelper.get` replacing the Forge `BlockEntity` extension),
  and only the outward face becomes Fabric: one namespace-guarded
  `FluidStorage/ItemStorage.SIDED.registerFallback` bridges whatever any smeltery BE
  exposes per side through the same `getCapability` methods. Forge-only listener-removal
  optimizations were dropped (invalidate clears listeners in the shim anyway).
- **Item fluid containers**: new `ItemFluidStorageBridge` presents the tag-driven
  `IFluidHandlerItem`s (tank items, lanterns, cannons, copper can) as Fabric item storages
  with `ContainerItemContext.exchange` semantics — so `FluidTransferHelper` and other mods
  can fill/drain them. Tank stack-size limiting moved to the `MAX_STACK_SIZE` component,
  maintained by the tank setters.
- **1.21 removals handled here**: `handleUpdateTag` is gone — client sync-tag work moved
  into `loadAdditional` (guarded by `level.isClientSide`); `onLoad` → `clearRemoved`;
  `getPistonPushReaction` override → baked into per-block `Properties` (tank registrations
  now build fresh `Properties` per block via suppliers); `AABB(BlockPos,BlockPos)` →
  `encapsulatingFullBlocks`; `NbtUtils.readBlockPos` int-array form for error pos, last
  fuel, and the multiblock position lists; `LevelEvent.PARTICLES_SHOOT` →
  `PARTICLES_SHOOT_SMOKE`; melting inventory NBT now threads `HolderLookup.Provider`
  (ItemStack save/parse).
- **Recipe surface**: `ICommonRecipe` gained the container-first `assemble(C, Provider)`
  default (the vanilla input-typed surface delegates to it), which fixed every
  molding/casting assemble call in one place; `ICastingRecipe` declares `getId()` for the
  active-recipe reload; potion casting reads the fluid's legacy `{Potion: id}` tag into
  the `POTION_CONTENTS` component; alloy/melting lookups wrap in `ContainerRecipeInput`.
- **Tool-part casts** register by name with `ContentLookups.materialItem` seams (21 casts);
  their cost tooltips resolve once the tools module lands. The fluid cannon's projectile
  shot is commented out until tools registers `FluidEffectProjectile`'s entity type.
- **Pathfinding**: the `IN_STRUCTURE ? DAMAGE_FIRE : OPEN` Forge hook became a dynamic
  `LandPathNodeTypesRegistry` provider registered for every seared/controller block.
- **Parked**: `smeltery/client/**` + `SmelteryClientEvents` (phase 5), `smeltery/data/**`
  (phase 7). Milk fluid: Forge's milk still does not exist on Fabric; milk-based recipes
  wait for the data pass to decide between a TiC milk fluid and recipe substitution.

### Phase 4, sixth slice: the tools module — **DONE, server boots clean (Done 0.891s)**

The largest content slice: every tool and armor item, all modifiers (~200 static +
dynamic), tool parts (healing the 21 cast seams and the anvil item), the five projectile
entities, and the module/predicate/loader registries. 322 gate-in errors, then a second
341-error layer once the armor/entity classes unparked; shipped at 0. Key decisions:

- **Roots eager**: TinkerModifiers → TinkerToolParts → TinkerTools init in Forge order;
  registerSerializers/commonSetup bodies inlined; the modifier deferred register listens
  on the shimmed `MinecraftForge.EVENT_BUS` (ModifierRegistrationEvent posts on datapack
  load, unchanged).
- **Eager-registration traps found at boot, not compile**: ToolDefinitions/ArmorDefinitions
  read `TinkerTools.<item>.getId()` while TinkerTools' own class-init was registering that
  item (Forge's deferred timing hid the cycle) — definitions now create from plain
  resource locations. And 1.20.5 added `ArmorItem.Type.BODY`, overflowing every
  4-slot armor array/loop — a shared `HUMANOID_SLOTS` constant now bounds registrations,
  plating parts/casts/dummies, and the station menus.
- **ArmorMaterial became a registry entry**: armor items take `Holder<ArmorMaterial>`,
  resolved through the core slice's DummyArmorMaterial holder; attribute modifiers are
  identified by `tconstruct:armor.<type>` resource locations instead of the vanilla UUID
  map. ITinkerStationDisplay's attribute surface aligned to `Multimap<Holder<Attribute>,...>`.
- **Entities on 1.21**: AbstractArrow ctors carry the firing weapon; vanilla arrows lost
  setKnockback, so arrow/crystalshot implement Tinkers' own `ProjectileWithKnockback`;
  thrown tools save via `pickupItemStack` (AW); unified `Portal` handling; item NBT in
  entities threads `registryAccess()`. Access widener opens ThrownTrident/FishingHook/
  AbstractArrow internals plus the `ApplyBonusCount` formula classes for the loot functions.
- **Loot on MapCodecs**: modifier/chrysophilite bonus functions rebuild vanilla's
  package-private formula dispatch on the widened formula classes, keeping the exact 1.20
  JSON shape; conditions/functions/pool entries follow the MantleLoot pattern. Forge's
  global loot modifier (`modifier_hook`) has no Fabric registry — rewires through
  LootTableEvents in the event layer.
- **Enchantments/components**: enchantment-converting recipes work on `ItemEnchantments`
  holders; banner patterns via `BannerPatternLayers`; player heads via
  `DataComponents.PROFILE`; armor dyeing on `c:dyes` tags and vanilla dye colors; tipped
  arrows on `PotionContents`/potion holders.
- **Deliberate deferrals**, all PORT-marked at their registration sites: tool
  fluid/inventory/energy capability modules (tank, slurping/splashing/bucket, quivers,
  tool belt, shield strap, minimap, overburn — the capability step), gameplay event
  handlers (`tools/logic`, shears, double jump, reflecting, break-speed traits' event
  bridge — the event layer; the shim bus keeps their listeners compiling), datagen
  builders (phase 7), client extensions (armor models, crossbow/charge HUD — phase 5).
- **New mantle shims**: `AbstractProjectileDispenseBehavior` (1.20.5 rebuilt dispensing
  around ProjectileItem), `ForgeEventFactory.onArrowNock/onArrowLoose/onProjectileImpact`,
  `LivingEntityUseItemEvent.Finish`, `LivingDropsEvent`.
- The smeltery round's parked fluid-cannon projectile shot is restored now that
  `fluid_spit` registers.

### Phase 4, seventh slice: the gadgets module — **DONE, server boots clean (Done 0.885s)** — phase 4 content complete

The last content module: punji sticks, seven food cakes, six fancy item frames, the
piggyback pack + carry effect, and the four legacy throwables (glowball, EFLN, two
shurikens) with their entities. Smallest error mountain of the port: **3 compile errors**
(one root cause — `BlockSource` moved to `net.minecraft.core.dispenser`), because every
signature was javap-verified before editing. Key decisions:

- **CustomExplosion unparked with the round** (EFLN needs it): the 1.21 explosion
  carries particles + sound in its constructor, `center()`/`radius()` replace
  `getPosition()`/the radius getter, `getDamageSource()` is gone (AW field read),
  and the blast-protection knockback dampener became the
  `EXPLOSION_KNOCKBACK_RESISTANCE` attribute. The Forge per-entity block-resistance
  hooks in EFLNExplosion collapse into the vanilla `ExplosionDamageCalculator`
  (identical numbers; vanilla wraps the source entity in one). `ClientboundExplodePacket`
  takes interaction + particles + sound now. `ignoreExplosion(Explosion)` forces the
  default entity predicate to be per-instance instead of a static constant.
  **ExplosionFluidEffect + ProjectileExplosionModule unparked alongside** — their
  loaders (`tconstruct:explosion`, `tconstruct:projectile_explosion`) register again.
- **Forge spawn-data surface deleted, not shimmed**: ThrowableItemProjectile syncs its
  item through entity data since 1.20.5, and the item frame's pos/direction ride the
  vanilla spawn packet; `IEntityAdditionalSpawnData`/`NetworkHooks` had nothing left to
  carry.
- **Fancy frames' special rotation survives via `extendable`**: vanilla's
  `ItemFrame.setRotation(int, boolean)` is private; the AW `extendable` entry rewrites
  the internal invokespecial so the 16-step/diamond-capped override is actually virtual
  again. `DATA_ROTATION` opened for the raw write.
- **Piggyback capability → weak map**: the Forge capability never serialized (it only
  deduplicates passenger resync packets), so a `WeakHashMap<Player, List<UUID>>` in
  `PiggybackHandler` replaces the whole attach ceremony; UUID values so entries never
  pin their keys. `PiggybackCapability` parked as superseded glue. Carry effect on the
  1.21 surface (`shouldApplyEffectTickThisTick`, boolean `applyEffectTick`,
  resource-location attribute key, `ADD_MULTIPLIED_TOTAL`); its HUD icons are a phase-5
  PORT note.
- **Cakes on the 1.21 food/use surface**: `FoodProperties` record accessors +
  `PossibleEffect` (probability moved into the record), use split into
  `useItemOn`/`useWithoutItem` — eating deliberately outranks candle placement, as
  upstream's full `use` override did. Composting via Fabric's
  `CompostingChanceRegistry`, punji's `DAMAGE_OTHER` via `LandPathNodeTypesRegistry`.
- **Frame ghost items**: `stack.getTag()` → `DataComponents.ENTITY_DATA` CustomData,
  `updateCustomEntityTag` takes the component now. Frame pick is vanilla `getPickResult()`
  (the Forge HitResult overload is gone).
- **DropperRailBlock parked twice over**: never registered upstream (dead code) *and*
  `onMinecartPass` is a Forge Block extension with no Fabric hook — if it ever becomes
  content it needs an AbstractMinecart mixin plus an entity item-storage bridge.
- **Bonus heal — Mantle's predicate vocabulary**: upstream Mantle's `@Mod` class
  registered the `mantle:` named predicate loaders (can_protect, fire_immune, mob_type,
  eyes_in_water, …); the vendored port had dropped that wiring, so every dynamic
  modifier JSON touching them failed to parse. New `MantlePredicates.init()` replicates
  the upstream table verbatim (fetched from the 1.20 branch) from the bootstrap
  (`fluid_type`/`may_have_transfer` skipped with PORT notes — Forge-capability-bound,
  unreferenced by data). MobTypePredicate's names moved from `c:` to the `minecraft:`
  namespace upstream data actually speaks. **Dynamic modifier failures: 107 → 75.**
- **The remaining 75 dynamic-modifier parse failures are all named later passes**:
  37 × legacy attribute-operation names (`addition`/`multiply_base`/`multiply_total` →
  1.21 renamed the enum; heals in the data migration pass), 12 × enchantment loadable
  (enchantments are a datapack registry now; needs registry-access-aware parsing),
  22 × parked capability/event module loaders (inventory, shears, quiver, overburn, …),
  4 × `tconstruct:tank_capacity` (fluid capability step). Tag/redirect errors in the
  log are downstream of these.

**Phase 4 is content-complete**: shared ✓ fluids ✓ world ✓ tables ✓ smeltery ✓ tools ✓
gadgets ✓. Next up are the cross-cutting passes: event layer, capability step, data
migration (recipes load!), then client, compat, datagen + docs.

### Data migration pass — **DONE: 3375 recipes, 1815 advancements, 0 loot errors (Done 0.879s)**

The 5.5k shipped JSONs moved from the 1.20 Forge dialect to 1.21 Fabric. Before this pass
zero Tinkers recipes loaded (1.21 reads `recipe/`, the data said `recipes/`); after it the
full tree parses except two known deferrals. One migration script (idempotent, scratchpad)
plus a small runtime layer:

- **Folder renames** across every namespace and both resource roots: `recipes`→`recipe`,
  `advancements`→`advancement`, `loot_tables`→`loot_table`, `structures`→`structure`,
  `tags/{blocks,items,fluids,entity_types}`→singular.
- **`forge:` tags → `c:`** (the 1.21 common-tag convention both loaders share): the whole
  `data/forge/tags` tree moved into `data/c` (no collisions), and every `"#forge:` /
  `"tag": "forge:` reference rewrote — this is what makes cross-mod GummiCraft materials
  visible to recipes. `data/forge/loot_modifiers` stays for the event layer's GLM step.
- **Conditions stay Forge-shaped, evaluated at load**: rather than rewriting 1021
  conditional recipes and 416 conditional advancements to Fabric's resource conditions,
  mixins on `RecipeManager`/`ServerAdvancementManager` apply the existing
  `ConditionHelper` dialect before parsing — including unwrapping `forge:conditional`
  recipes (66) and Forge's conditional-advancement wrapper (3). The condition context is
  the real tag data: a `ReloadableServerResources` mixin publishes the `TagManager`
  through `DataConditionContext` (vanilla loads tags before recipes/advancements).
- **The recipe-ID problem, solved for JSON**: ~1700 recipes' loadables require
  `ContextKey.ID`; 1.21 codecs never see the id, and `RecipeManager#fromJson` turned out
  to be dead code (apply parses inline — found via probe stack, not assumption). A
  `@Redirect` on the loop's single `Map.Entry#getValue()` call publishes each id through
  `CurrentRecipeId` (ThreadLocal), and `LoadableRecipeSerializer` feeds it into the
  context. Result: 1722 parse failures → 2. The network path still has no id — client
  phase concern, noted on `CurrentRecipeId`.
- **Vanilla-family formats**: crafting results `{"item"}` → `{"id"}` (321), cooking
  result strings → stacks (22), stonecutting result+count → stack (20), advancement
  icons item→id with the SNBT moved into `minecraft:custom_data` (41).
- **Custom ingredients**: `"type"` → `"fabric:type"` for the seven Tinkers/Mantle custom
  ingredient ids (parent-key guard keeps `tconstruct:material`/`block_tag` recipe ROOTS
  untouched — the ids double as recipe types). Forge compound ingredients became Fabric
  built-ins: `forge:intersection`→`fabric:all` (95), `forge:difference` (60, same
  fields). **Hard-won rule**: vanilla 1.21 ingredient *arrays* hold plain item/tag values
  only — a custom entry inside one never dispatches (Forge's loader allowed it). Arrays
  containing customs are wrapped in `fabric:any` (38) — but NOT `inputs`/`ingredients`
  keys, which are lists of *full* ingredients where customs are legal (first attempt
  broke 15 modifier recipes; unwrapped again).
- **Mantle recipe layer was never gated in**: `MantleRecipes` + `crafting_shaped_retextured`
  (15 recipes) + `crafting_shaped_fallback` ported onto `ShapedRecipePattern`/codecs and
  registered from the bootstrap; the unshipped mantle cooking overrides stay out (never
  vendored, no data uses them). The **ShapedMaterial family** (travelers gear, anvils —
  11 recipes) was unparked and rewritten the same way (subagent; instance-identity dedup
  because Fabric custom ingredients all compare equal under 1.21's `Ingredient#equals`).
- **Loot**: `minecraft:copy_nbt`→`copy_custom_data` (19; targets match the port's
  TagCompat custom-data reads), `forge:can_tool_perform_action(shears_dig)`→
  `match_tool #c:shears` (13), `looting_enchant`→`enchanted_count_increase` with explicit
  looting enchantment (5), and mantle's `fill_retextured_block` loot function was written
  fresh (never vendored) so retextured tables/drains drop with their texture.
- **Attribute operations**: `addition`/`multiply_base`/`multiply_total` →
  `add_value`/`add_multiplied_*` (53), keyed on the sibling `attribute` field so Tinkers'
  own stat-boost ops with the same names stay untouched. Dynamic modifier failures
  75 → 39.
- Odds and ends: `minecraft:scute`→`turtle_scute` (1.20.5 rename), slime dirt joined
  `minecraft:dirt`, tinkers chest joined `minecraft:dyeable`, `trim_material` jar exclude
  lifted (1.20 shape still parses on 1.21.1; ingredients exist since tools), MobType
  predicate names moved to the `minecraft:` namespace data speaks, `MantlePredicates`
  gap-fill from the gadgets round carried the rest.
- **Remaining, all named**: 2 recipes + 2 tag warnings reference a **milk fluid** that
  does not exist on Fabric (skeleton melting, cheese) — phase 6 decides (pack mod or own
  fluid). 39 dynamic modifiers still fail: 22 parked capability/event module loaders,
  12 enchantment-loadable (datapack-registry enchantments need registry-aware parsing —
  enchantment pass), 4 `tank_capacity` (fluid capability step); the 39 tag errors are
  downstream of those.

### Event layer — **DONE, server boots clean (Done 0.768s), 23 mixins apply, 0 failures**

Forge's event bus was the spine of Tinkers' gameplay: mining speed, the damage pipeline,
jumps, shield blocks, projectile impacts, equipment changes. Fabric has callbacks for a
handful of those and nothing for the rest. The port keeps the handlers' source shape and
supplies the events underneath:

- **15 new shim events** joined the existing ones under `mantle/event/`: the three damage
  stages plus death (`LivingDamageEvents`), fall, knockback, the misc family
  (`LivingMiscEvents`: visibility, experience drop, get-projectile, equipment change,
  shield block), `ProjectileImpactEvent` (with Forge's `ImpactResult`), the
  `PlayerInteractEvent` family, `AttackEntityEvent`, `CriticalHitEvent`,
  `BlockEvent.BreakEvent`, `MobEffectEvent.Applicable`, plus `PlayerEvent.StartTracking`
  and `ItemCraftedEvent`. Each surface is cut to exactly what the handlers consume.
- **The bridge is two-sided.** Fabric callbacks (`UseBlock`, `UseEntity`, `AttackBlock`,
  `AttackEntity`, entity tracking) post through `TinkerEventBridge`, which translates
  cancellation back into each callback's return contract. Everything else comes from
  **23 mixins**, one per vanilla class, every injection point javap-verified against the
  mapped jar before it was written.
- **Notable seams**: the damage pipeline is three distinct injections (`hurt` entry,
  `actuallyHurt` for the pre-armor amount, post-armor/magic for the final amount, each
  cancelable like Forge's); shield blocking redirects the block check *and* the shield
  damage so `setShieldTakesDamage(false)` works; break speed pairs a `getDestroySpeed`
  return injection with a `getDestroyProgress` position stash, because half the mining
  modifiers need the block position Forge's overload carried; critical hits combine a
  variable injection on the crit flag with a constant injection on vanilla's 1.5×;
  equipment changes read vanilla's own last-item accessors before they update.
- **Handlers ported 1:1**, `@SubscribeEvent` becoming explicit `init()` registrations that
  preserve every original priority: ToolEvents, ModifierEvents, EquipmentChangeWatcher,
  DoubleJumpHandler, InteractionHandler (516 lines of interaction logic), CommonsEvents,
  AchievementEvents, WorldEvents, SlimeBounceHandler.
- **1.21 API work inside the handlers**: `getDamageProtection` became server-level and
  float-valued; `hurtAndBreak` takes an equipment slot instead of a break-consumer;
  fire resistance is a data component; effects are holder-keyed; `doPostHurtEffects` +
  `doPostDamageEffects` collapsed into `doPostAttackEffects`; gravity moved to an
  attribute. Fabric has no `FakePlayer` in the Forge sense, so the checks became
  "is this exactly a `ServerPlayer`".
- **The shim bus dispatches on exact class**, so the enderference teleport listener
  registers for all 13 concrete teleport events rather than a base type — otherwise
  blocking would silently miss sling and ender-slime teleports.
- **Wandering trades** moved to Fabric's `TradeOfferHelper` (pool 2 mirrors Forge's rare
  list); **shearing** now runs on vanilla's `Shearable` interface (entities spawn their own
  drops, so the fortune bonus no longer applies — Forge-only behavior, noted at the site);
  **looting** is posted from `getEnchantmentLevel`, the single 1.21 funnel every drop path
  uses — without that seam every looting modifier reads zero.
- **Also fixed here**: `Sounds.registerSounds()` was never called, so sound-referencing
  data failed to parse. Two fluid effects and two recipes still fail on the enchantment
  datapack registry (that pass), and `restrict_projectile_angle` + `shears` loaders are
  registered again (dynamic modifier failures 39 → 34).
- **Deliberately deferred**: the global-loot-modifier subsystem (21 entries — lustrous ore
  bonuses, tasty bacon, wither bone, chrysophilite) has no Fabric equivalent and needs its
  own runtime; it is parked with a full description of what that runtime requires. The four
  legacy ability-modifier classes stay parked: zero references, their behavior is datapack
  modules now.

### Capability step — **DONE, server boots clean (Done 0.782s); dynamic modifier failures 34 → 13**

Tools that hold fluid (tanks, spilling, bucketing) or items (quivers, tool belt, shield
strap, sleeves, minimap) were built on Forge's per-stack capability system. The line count
suggested a rewrite; the actual Forge coupling turned out to be **seven imports** across
the whole tree, all covered by the capability shim the smeltery round already built. The
bulk is Tinkers' own hook machinery and ported unchanged.

- **Dispatch, not attachment.** Forge attached a capability provider to every ItemStack via
  an event. Fabric has no such hook, so `ToolCapabilityProvider` became a static lookup
  following the pattern `BlockItemProviderCapability` established earlier in the port. The
  correctness-critical parts stay: the tool tag is refreshed and provider caches cleared on
  every lookup — which is what Forge did anyway, so nothing is staler than before.
- **The outward face is what makes tanks real.** A `FluidStorage.ITEM` fallback exposes any
  modifiable tool carrying tanks through Fabric's transfer API, reusing the smeltery round's
  `ItemFluidStorageBridge` so fill/drain keep proper container-exchange semantics. Other
  GummiCraft mods can now fill and drain a Tinkers tool like any other fluid container.
  Inventories get an equivalent static accessor (`ToolInventoryCapability.getInventory`).
- **21 gameplay modules unparked**; nine needed no changes at all. The rest were ordinary
  1.21 migrations: recipe lookups take an immutable `RecipeInput` and return a
  `RecipeHolder` (so smelting's shared mutable container is gone), stack NBT round-trips
  through `parseOptional`/`save` with registries, `isSameItemSameTags` →
  `isSameItemSameComponents`, map ids are a `MapId` component, and both block-fluid
  interfaces (`canPlaceLiquid`, `pickupBlock`) gained a player parameter.
- **Menus and packets**: the tool container opens through an `ExtendedScreenHandlerFactory`
  with a typed payload reproducing Forge's conditional wire format exactly; the fluid update
  packet follows the port's existing packet conventions.
- **Found and fixed while here**: `TinkerNetwork.setup()` was never called, so *any* packet
  send would have thrown — including the projectile sync and reflecting logic the event
  layer had just landed. It is wired now with all nine ported packets registered. Also
  `minecraft:sweeping` → `sweeping_edge` (1.21 rename) in the enchantment mapping.
- **Vaporization** moved into the FluidType shim: Forge let each fluid type decide, vanilla
  hardcodes the one real case (water in ultrawarm dimensions) inside `BucketItem`.
- **Deferred**: `ToolEnergyCapability` and `EnergyHandlerModifier` (energy step, Team Reborn
  Energy), plus the datagen builders. **All 13 remaining dynamic modifier failures now share
  a single root cause** — enchantments became a datapack registry and the loadable needs
  registry-aware parsing. That is one focused pass, not thirteen.

### Enchantment registry pass — **DONE: the datapack load is now error-free (Done 0.816s)**

Started as one focused fix and turned up two latent bugs that had been quietly costing
content since the tools module landed.

- **The stated goal**: 1.21 moved enchantments into a datapack registry, so the loadable
  that resolves them needs registry access at parse time. The infrastructure was already
  there (`DynamicRegistryLoadable`, `ContextKey.REGISTRY_ACCESS`) — nobody was filling it.
  A new `DatapackRegistries` holder is published from the same `ReloadableServerResources`
  mixin that already publishes the tag manager, and the modifier + fluid-effect managers
  put it into their parse contexts. **13 modifier failures → 0.**
- **Map keys never got the context.** Two fluid effects still failed afterwards, on
  `enchantments's key` — `MapLoadable` threaded the parse context into map *values* but
  called the context-free overload for *keys*. One-line fix, and it would have bitten every
  future map-keyed registry entry.
- **No static modifier was ever registered.** The remaining tag errors pointed at modifiers
  that plainly existed in `TinkerModifiers`. The cause: `ModifierManager.init()` fires the
  registration event immediately (on Forge it came later in startup), but the deferred
  register only started listening three lines *below* that call — so the event fired into
  an empty bus and all 46 static modifiers silently vanished. Swapping the two lines took
  the modifier registry from **122 to 168 entries** and modifier tags from 39 to 55.
  Overslime, parrying, dual wielding, exchanging, enderporting and friends exist again.
- **Convention tags are named differently on Fabric.** The data round moved `forge:` tags
  to the `c:` namespace, but Fabric's conventional names are mostly plural
  (`c:stone` → `c:stones`, likewise cobblestone/gravel/gunpowder/leather/obsidian/string,
  and `c:sandstone` → `c:sandstone/blocks`). Only one surfaced as a tag error; the rest sat
  in **recipes**, which silently never match when their tag does not exist — string,
  gunpowder, obsidian and traveler's-armor leather recipes were all dead. Fixed across 22
  files after diffing every `c:` tag the data uses against Fabric's convention jar. The 472
  other unmatched `c:` tags are deliberate cross-mod hooks (Mekanism armors and the like)
  that stay empty until those mods are present.

The datapack now loads with **zero** modifier, fluid-effect, loot-table, advancement or tag
errors. The only remaining data failures are the two milk-fluid recipes awaiting the phase-6
decision.

### Global loot modifiers — **DONE, all 21 entries load (Done 0.858s)**

Forge's global-loot-modifier mechanism has no Fabric counterpart at all, so the whole thing
is reimplemented in `mantle/loot/modifier`: modifiers post-process the output of *every*
loot roll, which is how lustrous grants ore bonuses, tasty drops bacon, chrysophilite adds
gold and wither skeletons give necrotic bones.

- **Loading** reads Forge's own index (`data/forge/loot_modifiers/global_loot_modifiers.json`)
  and each named entry, so the shipped data and any pack overriding it keep working
  untouched. Every pack's copy of the index is read and combined, honoring `replace` — the
  same additive behavior Forge had.
- **Timing was the one real trap.** Loading from the resource-reload phase failed on
  `Missing tag`: the entries embed entity and block predicates whose tags are only bound
  once the reload completes. The loader therefore runs off the server lifecycle
  (`SERVER_STARTED` + `END_DATA_PACK_RELOAD`), the same hooks the modifier manager already
  uses to resolve its enchantment mappings.
- **Applying** hooks the single private `LootTable#getRandomItems(LootContext)` that every
  public overload funnels through, so block drops, entity drops and chest generation are
  all covered by one seam — the same place Forge patched.
- **Three modifier types** (`mantle:add_entry`, `mantle:replace_item`,
  `tconstruct:modifier_hook`) plus the loot-modifier conditions `mantle:contains_item` and
  `mantle:inverted`, which differ from vanilla loot conditions in that they see the loot the
  table already produced — that is what "only add a nugget if the ore did not drop" needs.
- **Two never-vendored conditions** turned up and were written fresh: `mantle:block_tag`
  (18 uses — vanilla can only match one block, the ore bonuses need "any iron ore" across
  mods) and `forge:loot_table_id`, which reads the rolling table from the manager.
- One more `looting_enchant` → `enchanted_count_increase` rename, in the loot-modifier
  folder the earlier sweep did not walk.

**The datapack now loads completely clean**: zero errors across modifiers, fluid effects,
loot tables, loot modifiers, advancements and tags. The only remaining data failures in the
whole port are the two milk-fluid recipes waiting on the phase-6 decision.

### Energy step — **DONE, boot clean (Done 0.851s); phase 4 and every cross-cutting pass complete**

Forge Energy has no Fabric counterpart, but the ecosystem has settled on Team Reborn's
energy API — and the pack's own tech mod (Oritech) requires it — so that is what tool
energy speaks now.

- **Tinkers' own API is untouched.** The static helpers (`getEnergy`, `setEnergy`,
  `addEnergy`, `checkEnergy`, the capacity stat) stay integer-based exactly as upstream
  wrote them; modules calling them did not change at all. Only the outward face moved.
- **The face is a separate class on purpose.** `ToolEnergyStorage` implements the energy
  API; `ToolEnergyCapability` keeps the helpers and references nothing from it. That split
  is what lets the mod still load with no energy mod installed — the storage class is only
  touched behind a `isModLoaded` guard, matching how the port already handles Ceramics and
  Twilight Forest. Declared as a *suggestion*, not a dependency.
- **Semantics**: Forge was int + a simulate flag, Team Reborn is long + transactions.
  Amounts widen and writes join the caller's transaction, publishing through
  `ContainerItemContext.exchange` on a single-item copy — the same pattern the tool fluid
  handler uses, which is what makes it transactional rather than merely simulated.
- The other two files (`EnergyHandlerModifier`, `EnergyAsCapacityModule`) had no Forge
  coupling at all; they were parked purely because the capability was.

**Everything outside the client is now ported**: phase 4 content complete, and every
cross-cutting pass — data migration, event layer, capabilities, enchantment registry,
global loot modifiers, energy — is done. The datapack loads with zero errors in every
category; the only remaining data failures in the whole port are the two milk-fluid recipes
awaiting the phase-6 decision.

### Phase 5, slice 1: model bridge + screens — **DONE, client launches and loads clean**

The client already started before this slice; what it could not do was render anything
Tinkers-specific or open a single GUI. Both foundations now exist.

- **The geometry bridge.** Forge patched its model deserializer so a `"loader"` key in a
  model JSON dispatched to a registered geometry loader; over 400 of the shipped models
  rely on that. Fabric has no such hook, so the port supplies one: a Forge-shaped shim
  (`IGeometryLoader`, `IUnbakedGeometry`, `IGeometryBakingContext`) over Fabric's model
  API, with the baking context implemented against the vanilla `BlockModel` parsed from the
  same JSON so the geometry classes see exactly what Forge handed them.
  The hard part was recursion: asking the model loader for the id you are currently
  resolving makes Fabric throw. The resolver therefore reads and parses the model files
  itself during the reload's prepare stage, and a substring test skips the JSON parse for
  the ~99% of models that declare no loader. Unknown loader ids fall through to vanilla,
  so nothing regresses.
- **Screens.** All the table GUIs (crafting station, tinker station, part builder, modifier
  worktable, tinkers chest) and the smeltery GUIs (melter, smeltery, alloyer) are live,
  together with their inventory modules, widgets and tank/fuel/melting overlays.
- **The three Forge parent models** (`forge:item/default`, `default-tool`, `bucket_drip`)
  are provided under the forge namespace rather than rewriting the 163 models that
  reference them — the same call taken for the loot-modifier index. They exist to supply
  display transforms, so they map onto `item/generated` and `item/handheld`.
- **Render layers, data-driven.** Forge honored a `render_type` key in the model JSON;
  vanilla and Fabric only know a per-block mapping, and 295 of the shipped models declare
  one. Instead of a hand-kept block list that would drift, the port walks each block's
  blockstate to its models and applies what they declare, so the models stay the single
  source of truth. Where a block's models disagree, the most permissive layer wins.

**The structural finding of this slice**, worth recording because it reshapes the phase:
**this repository's vendored Mantle is server-side only.** Its client model package
(`SimpleBlockModel`, `ColoredBlockModel`, `MantleItemLayerModel`, `RetexturedModel`,
`DynamicBakedWrapper`, `ModelHelper`, `ItemLayerPixels`) and its screen package are not
parked — they were never copied in, and no Mantle source exists on this machine to copy
from. The screen infrastructure was therefore reconstructed from the API surface its
consumers require (eight classes). The client model package still has to be, and that is
what gates the remaining geometry: only `tconstruct:gui` is registered so far, proving the
bridge end to end, while the six real geometry classes — including the 122 tool models and
71 fluid containers — wait on it plus a small Forge client-model compat layer. Each file
now names its own blocker.

Two known gaps carried forward: the block atlas definition fails to parse as a whole
because a custom sprite source (`tconstruct:shield_banner_to_modifier`) is not registered —
it only waits on `MaterialRenderInfo`, so it is worth an early look — and the fluid block
models still miss their textures, which belongs with the fluid rendering slice.

### Phase 5, slice 2: client model foundation + renderers — **DONE, verified in-game**

Slice 1 built the bridge but only one loader could cross it, because this repository's
Mantle has no client model package. This slice wrote that package, and with it most of what
Tinkers actually looks like.

**A regression from slice 1 was the gating item, and it was invisible to the build.** The
client had been discarding the *entire* Tinkers resource pack at model-bake time
(`BlockModel parent has to be a block model` → `removing all selected resourcepacks`), so
nothing rendered at all while every build stayed green. It surfaced only by running the
client and diffing against an earlier run. The cause: 114 of the 402 loader-carrying models
are used as a `parent` by 144 others, and the bridge handed back a plain unbaked model where
vanilla requires a `BlockModel`. The fix makes the geometry model *be* a `BlockModel` whose
parent is the vanilla parse of the same JSON. A second trap sat behind it: vanilla routes
anything whose root is `item/generated` through `ItemModelGenerator` **instead of** calling
bake, which would have silently emptied 170 models — caught by disassembling the bakery.

- **Five more loaders live**: `material` (29 models), `material_block` (3), `fluid_texture`
  (2), `fluid_container` (71), `tank` (8). Only `tconstruct:tool` (122) stays parked; its
  model-side support is complete now, and what remains is the modifier-texture tree.
- **Written from scratch**, because none of it existed: Mantle's client model classes
  (`ColoredBlockModel`, `MantleItemLayerModel`, `RetexturedModel`, `DynamicBakedWrapper`,
  `ModelHelper`, `ExtraTextureContext`) and the Forge client-model compat layer. Two Forge
  concepts collapsed honestly rather than being faked: render-type groups (Fabric assigns
  layers per block, so there is nothing per-model to carry) and `ModelData` *delivery* —
  the map itself is real, but 1.21's `getQuads` has no data parameter, so block-state-driven
  variants render their static form. Item rendering is unaffected, which is what shows tools
  and tanks in inventories.
- **Renderers**: smeltery, casting tables, faucets, channels, tanks, gauges and the
  projectile renderers are live, on a render-helper package that also had to be written
  (fluid cuboids, the fluid renderer, blockstate data maps, item placement).
- **Fluid rendering**: Forge read sprites and tint from a client fluid-type extension; on
  Fabric a render handler supplies them. The manager reads the same 72 generated files, keyed
  by the fluid rather than Forge's fluid-type registry — the names already matched. Forge's
  two client fluid-type classes were dropped rather than ported: their sprite duties moved to
  the handler, and their fog/overlay half has no Fabric hook.
- **The block atlas was failing to parse as a whole** because a custom sprite source was
  unregistered — costing every sprite the definition adds, including the fluid textures.
  Ported to 1.21 (sprite sources now take a `MapCodec`, suppliers take a resource loader,
  the shield material map is keyed by plain ids) and registered. Registration goes into
  `SpriteSources.TYPES` directly rather than through vanilla's `register`, which takes a
  bare name and stamps the minecraft namespace onto it — an id the shipped atlas
  definition does not name, and not even a legal one. The atlas entry itself was migrated
  too: 1.20 nested a custom source's fields under `value`, 1.21 takes a `MapCodec` and
  reads them flat. A single malformed source fails the whole definition, so this cost
  every sprite the file adds, not just the shield banners. The source's own body needed a
  rewrite on top: it read `Sheets.SHIELD_MATERIALS`, which 1.20 built eagerly from the
  banner pattern registry and 1.21 fills lazily on first render — so it is empty while the
  atlas stitches and the port would have generated nothing, silently. It now lists the
  shield texture folder through vanilla's own `FileToIdConverter`, which also picks up
  pattern textures a datapack or resource pack adds — 43 sprites where the registry read
  gave none. Upstream's own source carries a TODO predicting this. A hand-rolled
  `listResources` with a trailing slash was tried first and hung the resource reload
  outright: no exception, no log line, all workers idle and the client stuck on the
  loading overlay forever. Worth remembering that a bad resource path can stall rather
  than throw. Every
  JSON under `generated` and `main` was then swept for the same `{type, value}` nesting;
  the only other hit is a `forge:not` biome modifier, which Fabric never reads.
- **Buckets** get their fluid mask from Tinkers' own potion-bucket contents texture rather
  than vendoring Forge's PNG; Forge's version also draws a drip on the lip, which this lacks.

Confirmed in a running client: 139 material render infos, 139 blocks given their declared
render layer, 72 fluid textures registered for rendering, and zero model, mixin or bake
errors.

One fidelity gap was recorded here and is **closed in slice 3**: vanilla's item renderer
discarded baked vertex colours, so materials that tint a greyscale sprite rendered grey on
items. Reproducing Forge's patch to that call site turned out to be one mixin.

### Phase 5, slice 3: the tool model — **DONE, all 122 tool models resolve in a running client**

`tconstruct:tool` is the loader behind 122 model files and every tool the mod ships. It was the
last of the six geometry consumers still parked, and it does not just place a texture: it bakes
one layer per tool part in the part's material, then one per visible modifier, into four
variants of the same item (right hand, left hand, a small one, and a flat one for the GUI).

- **The last two model utilities** were written: `ReversedListBuilder`, which exists so layers can
  be *assembled* top down and *emitted* bottom up, and `IModelBuilder`, a thin wrapper over
  vanilla's `SimpleBakedModel.Builder`.
- **The modifier model tree** came in whole — both generations of it, since the tool model reads
  both: the live `ModifierModelMapManager` reading `tinkering/modifiers/sprites` (77 files), and
  the deprecated per-tool manager behind it. Its 14 sprite types register from a new
  `ModifierModelLoaders`; Forge did that from `TinkerClient`, which is still unported, and without
  it every entry richer than a bare texture path failed to parse — 105 errors in the first run,
  each one a modifier that would have drawn nothing.
- **Banner patterns** needed two 1.21 changes. `BannerPattern.byHash` is gone along with the hash
  itself, so the model reads the registry id `BannerModule` already writes. And the pattern list
  could not come from `Sheets.SHIELD_MATERIALS`, which is private and empty during resource
  loading — the same finding as slice 2's sprite source, so both now share
  `BannerPatternTextures`.
- **`ItemLayerPixels` is settled rather than deferred.** Upstream traced each layer's silhouette
  itself and trimmed the trace against this record; vanilla's `ItemModelGenerator`, which this port
  feeds instead, draws a layer's front as *one full-size quad* and lets the texture's alpha cut the
  shape. There is no per-pixel face to trim, and the picture is right without it: overlapping
  layers are coplanar, so under `GL_LEQUAL` the last one drawn wins, and `ReversedListBuilder`
  guarantees that is the topmost. The cost is overdraw on a heavily modified tool, paid once per
  cached bake. The class says so in full.

**Two Forge patches to `ItemRenderer` had to be reproduced as mixins**, and each was carrying a
visible feature:

- *Model swapping.* Forge routed every item render through `applyTransform`, which let a model
  hand back a different variant per display context. Vanilla applies `ItemTransforms` inline with
  no such hook, so without it every context drew the large right-handed variant — a tool in the
  inventory would show the side faces of its layers. The mixin swaps the model into the parameter
  at the head of `render` and lets vanilla apply the swapped model's own transforms, which needs
  one injection instead of two. `UniqueGuiModel` had the same override sitting inert since slice 1;
  it fires now too.
- *Baked vertex colours and light.* A `BakedQuad` carries a colour and a lightmap per vertex, and
  vanilla's item path reads neither — `renderQuadList` calls the `putBulkData` overload that passes
  `readExistingColor = false` and overwrites the lightmap outright. This is what slice 2 recorded
  as "materials that tint a greyscale sprite render grey on items". It is more than materials:
  every dyed, potion-tinted and fluid-tinted modifier overlay, and every modifier declaring a
  `luminosity` — fiery, glowing, haste, lightspeed, unbreakable. The redirect passes the baked
  colour through and takes the brighter of the two light levels per channel. Vanilla models bake
  opaque white with a zero lightmap, and both compose, so nothing else changes.

**Item properties** came with it, because they are what the tool models' `overrides` read. Two
1.21 obstacles: `ItemProperties.register` is private (widened, as Forge had an open registration),
and it only accepts a `ClampedItemPropertyFunction`, whose `call` clamps to `[0,1]` — while
`tconstruct:charging` reaches 2.5 and `tconstruct:ammo` reaches 2 to *name* a variant rather than
give a fraction. Clamping would have collapsed those onto one model; overriding `call` keeps them.
Vanilla resolves an unregistered property to zero rather than complaining, so the whole class of
bug is silent: broken tools would have kept their intact texture and a drawn bow never changed
pose.

**A crash the round found by accident, unrelated to models.** One validation run was taken into a
world, and a skeleton ticking there threw `ArrayIndexOutOfBoundsException: Index 6 out of bounds
for length 6` out of `EquipmentContext`. 1.21 added the `BODY` equipment slot for animal armour, taking the count
from six to seven, and four classes sized arrays to a literal six and indexed them by
`EquipmentSlot#getFilterFlag()` — `EquipmentContext`, `ModifierMaxLevel`, `AttributeModule` and
`SlotInChargeModule`. Any armoured wolf, horse or llama in range crashed the server tick. They now
size from `Util.EQUIPMENT_SLOTS`. That fix is verified by inspection only — the acceptance run below
stops at the title screen, so a world run belongs in the next round's checks.

Smaller things settled on the way: the `tconstruct:smashing` tank helper registers (its modifier
model needed it), `ISafeManagerReloadListener` lost the `if (true)` left over from removing
Forge's loading-state guard, and the tool tint handler and the four client reload listeners are
wired.

**Making the result checkable.** The bridge falls through silently for an unregistered loader — by
design, since another mod's `loader` key is none of our business — which is exactly how slice 1's
regression stayed hidden. It now reports what each loader claimed once per reload, so a missing
registration reads as a number rather than as nothing:

```
Resolved 236 models through custom geometry: tconstruct:fluid_container=71,
tconstruct:fluid_texture=2, tconstruct:gui=1, tconstruct:material=29,
tconstruct:material_block=3, tconstruct:tank=8, tconstruct:tool=122
```

All 122 tool models resolve. The rest of the run is clean too: 139 material render infos, 139
blocks given their declared render layer, 43 shield banner sprites, 72 fluids registered for
rendering, and zero hits for parent errors, discarded resource packs, missing models, missing
textures, atlas parse failures, modifier-map parse failures or mixin failures.

### Phase 5, slice 4: armor rendering + the world module's client half — **DONE, 5 armor models load clean**

Tinkers armor is not a texture, it is a stack of layers named by a JSON model — dyed, trimmed,
material-tinted, with an elytra layer on the chest — and the slimeskull additionally draws a second
model under the helmet. Forge reached all of it through `IClientItemExtensions`, a hook on the
item; Fabric has no such hook and does not need one.

- **Fabric's `ArmorRenderer` is the better fit, and simplified the port.** It is registered per item
  and is handed the `MultiBufferSource` directly. Forge's hook only ever saw a single
  `VertexConsumer`, which a layered model cannot use — each layer draws with its own texture —
  so upstream scraped the buffer off `RenderLivingEvent.Pre` into a static field. That event
  and its listener pair are gone; the buffer is set around the one call that reads it. Items say
  which model they want through a new `ArmorModelItem`, and the renderer registration walks the item
  registry, so an addon needs only that interface — as much as Forge's hook asked of it. Fabric's
  own mixin suppresses vanilla's armor layer for registered items, so nothing draws twice.
- **`Model#renderToBuffer` lost its four float colour channels in 1.21**, replaced by one packed
  ARGB int. That reached nine files and the `ArmorTexture` interface; composing a layer's own tint
  over the caller's is now `FastColor.ARGB32.multiply` instead of four multiplications.
- **Armor trims** needed the 1.21 split trim sheet (`armorTrimsSheet(true)` for the decal variant),
  and the whole `getArmorFoilBuffer` call site lost a parameter.
- **The combat fishing bobber** came in with them, having waited on these very texture suppliers
  since the renderer round. Its vertex chain is rewritten for 1.21 — `addVertex`/`setColor`/`setUv`
  and no `endVertex` — and it takes the pose rather than a separate normal matrix. Vanilla's
  `stringVertex` went private, so the bobber's line is drawn through an access widener rather than a
  copy of the geometry.

**The world module's client half came with it**, because the slimeskull's head models are registered
there and the slime renderers draw armor. All of it is live now: the slime and terracube renderers,
the plant colours, the slime particles, and the mob heads.

- **Modded skull types have no hook at all in 1.21.** Forge fired
  `EntityRenderersEvent.CreateSkullModels`; vanilla builds the map in
  `SkullBlockRenderer.createSkullRenderers` from a hardcoded list of its own types, so a
  modded `SkullBlock.Type` renders as nothing. A mixin appends to the returned map from a
  registry filled at client init — registration and baking have to be separate, since the entity
  model set only exists once renderers are being built.
- **`SlimeArmorLayer` needed a real 1.21 rewrite**, not an import swap: it reimplements vanilla's
  humanoid armor layer to put a helmet on a slime, and every piece of that moved. `DyeableLeatherItem`
  is gone (dye is a component), the `_layer_1` plus `_overlay` texture pair became
  `ArmorMaterial.Layer`, the armor location cache went away, and the skull owner moved from a
  `SkullOwner` tag to the profile component. The Tinkers branch now builds the layered model
  directly, since `ForgeHooksClient.getArmorModel` has no counterpart outside an `ArmorRenderer` and
  a slime's helmet is not one.
- Two more private members opened by widener rather than copied: `BreakingItemParticle`'s
  constructor, which the slime splash subclasses, and `SkullBlockRenderer.SKIN_BY_TYPE`, which names
  a texture per skull type. `ItemRenderer.renderModelLists` joins them, for the block-model skull
  that draws an item model inside an entity model — and that skull's camera-transform call became a
  static on `BakedModelWrapper`, the same swap the tool model's mixin performs.
- `ClientEventBase` lost the `BlockColors`/`ItemColors` parameters from all three helpers: Forge
  handed those to the registration event, Fabric registers statically.

Confirmed in a running client: **5 armor models loaded**, no item left naming a model that does not
exist, all 236 custom-geometry models still resolving, and zero hits for missing models, missing
textures, atlas failures, modifier-map failures or mixin failures. What a title-screen run cannot
show is armor actually drawn on a body — that needs a world, and belongs in the next round's checks
alongside the `BODY` equipment slot fix from slice 3.

### Phase 5, slice 5: tool interaction — **DONE; a world can be joined for the first time**

The last of `tools/client`, and the part a player actually touches: the keys that trigger armour
abilities, the double jump, the overlays a modifier draws on the HUD, the area-of-effect preview
under the crosshair, and the tool's own inventory screen.

**Nearly none of it had a Fabric callback.** Forge's client event bus covered all of this with
events; Fabric offers a callback for four of the hooks and nothing for the rest, so five mixins go
in at the same points Forge's patches sat:

| What | Forge event | Here |
| --- | --- | --- |
| Right click into air | `PlayerInteractEvent.RightClickEmpty` | `MinecraftInteractionMixin` on `startUseItem` |
| Left click into air | `PlayerInteractEvent.LeftClickEmpty` | same mixin, on `startAttack` |
| First-person hand | `RenderHandEvent` | `ItemInHandRendererMixin` |
| Field of view | `ComputeFovModifierEvent` | `AbstractClientPlayerFovMixin` |
| Movement while using an item | `MovementInputUpdateEvent` | `LocalPlayerMovementMixin` |

The right-click mixin is placed after both of vanilla's guards and after the right-click delay is
set, so the interaction repeats at vanilla's rate rather than every tick. Cancelling there also
retires a workaround: upstream carried a `cancelNextOffhand` flag because Forge's event could only
suppress the hand it fired for, while cancelling `startUseItem` covers both hands at once. The
left-click mixin lands at the end of the miss branch, so vanilla has already set the miss time and
reset the attack strength; only the swing is skipped, and the interaction does its own.

**Two behaviours were silently dead on the server and are now connected.** `TinkerControlPacket`
carried the client's key presses across, and its handler was a set of empty cases left from the
event-layer round — the client ran the double jump and the helmet/leggings interaction locally, and
the server did nothing with either. Both call into logic that has been ported since.

The rest is a straight mapping: the durability tooltip suppression to `ItemTooltipCallback`, the
disconnect reset to `ClientPlayConnectionEvents`, the shield-strap/sleeves/item-frame/minimap
overlays to `HudRenderCallback`, the AOE outline and break progress to `WorldRenderEvents`, and the
two keys to `KeyBindingHelper` plus a client tick. One fidelity note: Forge drew the overlays right
after the hotbar, Fabric's HUD callback runs after the whole HUD, so they sit above the rest of it.

1.21 drift along the way: the vertex builder chain lost `endVertex` and gained `set*` names, the map
id became a component with its own type, `Slot#getSlotIndex` became `getContainerSlot`,
`SheetedDecalTextureGenerator` takes a pose instead of two matrices, and `MobEffectInstance#getEffect`
returns a holder. Forge's client extension for hiding an effect from the GUI has no counterpart, so
the potion-row offset reads only the instance's own flag.

Widened rather than reimplemented, all of them things the overlays have to draw exactly like vanilla:
`Gui#renderSlot`, `ItemInHandRenderer#renderPlayerArm` and its two map background render types, and
three `LevelRenderer` internals for the AOE preview — the buffer sources, the per-block outline, and
the map of blocks being broken.

Also settled: `RayTracer`'s reach calculation collapses to `Player#blockInteractionRange`, since 1.21
made reach a vanilla attribute and the client/server split it needed is gone.

**The round's real find, and it took a world to see it.** Every validation run so far has stopped at
the title screen. A `runClientWorld` task was added to join a singleplayer world straight from the
launch, and the first thing it produced was this:

```
Couldn't place player in world
java.lang.IllegalArgumentException: Packet class ...UpdateModifiersPacket is not registered on channel tconstruct:network
```

**Nineteen of the mod's packets were never registered.** `TinkerNetwork` carried a note saying each
module would register its own from its bootstrap; none ever did, and the list had grown to cover
only the eleven whose classes happened to exist when it was written. The consequence is not subtle:
an unregistered packet throws when sent, and the first packet a joining player receives is the
modifier sync — so **no player could enter a world at all**. Behind that sat the whole datapack
sync (materials, material stats and traits, modifiers, tool definitions, slot layouts, fluid
effects) and every tables and smeltery GUI packet.

Two things kept this hidden. Title-screen runs never open a connection. And the one earlier run that
did reach a world reached it with the mod's datapack missing from that world's `level.dat`, so the
managers had nothing to sync and never tried. The registration is one block again, with the note
rewritten to say why.

Found alongside it: `UpdateStationScreenPacket`'s handler was still an empty stub from before the
screens were ported, so a tinker station never refreshed on the client. It calls
`BaseTabbedScreen#updateDisplay` again.

**And behind that, a second one.** With the packets registered the player joined — and was
immediately disconnected by `Failed to encode packet 'clientbound/minecraft:update_recipes'`, caused
by `ItemStack cannot be empty` inside an `ItemCastingRecipe`. The serializer's error named the
loadable but not the recipe, so it now names the recipe id too; that pointed straight at
`tconstruct:smeltery/casting/amethyst/block`.

Its output is the tag `c:storage_blocks/amethyst`, which nothing fills. Tinkers melts vanilla
amethyst and quartz blocks and casts them back, naming the convention tag on both sides; on Forge
that tag came from Forge's own tag data, and Fabric's convention tags do not carry either. An
unfillable output is not a dead recipe — it is a stack that cannot be written to the network, which
takes the connection down with it.

A sweep over every item-output recipe against the tags this environment actually defines (Fabric's
convention tags plus the mod's own) found exactly these two — amethyst and quartz — and nothing
else; 161 other output tags are either defined or guarded by a `tag_filled` condition. Both tags are
now shipped, item and block, and the datagen provider carries a note to emit them in phase 7.

**And a third, one layer deeper.** With the tags shipped the player joined and the recipes encoded —
and the *client* then failed to decode them: `Unable to fetch id from context` out of
`ModifierSalvage`. This is the "recipe-ID problem" PORTING.md has carried since phase 3, and it had
only ever been half solved. 1.21 moved a recipe's id out of `Recipe` and onto `RecipeHolder`, which
writes it separately; the JSON path was patched to publish the id being parsed, but the network path
had none at all, so the 67 recipe classes that declare the id as a required context field threw on
arrival.

The serializer now writes the id into its own payload and reads it back — a nullable resource
location per recipe. Finding the id to write needs one reflective lookup per recipe class, cached:
the recipes keep it in a Lombok-generated getter rather than behind a shared interface.

The three findings stack: each one only became visible once the one before it was fixed, and none of
them can be seen without joining a world.

**Then the deepest one: seven registrations that nothing called.** With the recipes decoding, the
client threw `MaterialRegistry.INSTANCE is null` — because `MaterialRegistry.init()` had never been
invoked. A sweep for every `public static void init/setup/register()` in the tree that no other file
calls found ten, of which seven are live code and are now wired into the bootstrap:

| Missing | What was silently absent |
| --- | --- |
| `MaterialRegistry.init()` | the entire material registry and its three datapack loaders |
| `ToolDefinitionLoader.init()` | tool definitions — every tool's stats and modules |
| `StationSlotLayoutLoader.init()` | the tinker station's slot layouts |
| `TinkerRecipeTypes.init()` | the recipe types themselves |
| `TinkerTags.init()`, `MantleTags.init()` | the tag holder classes |
| `DomainDisplayName.init()` | the mod-name cache's reload hook |

The remaining three are Forge-only classes still parked (`PiggybackCapability`, `TConstructCommand`)
or an empty body (`OffhandCooldownTracker`).

This is the fourth time in this port a registration method that compiles has turned out to be called
by nobody, so the sweep is written down as a script rather than repeated by hand. It is worth
re-running whenever a slice adds an `init()`.

**With the loaders running, three more things became visible** — they had been failing silently
because the code that reads them had never been registered:

- 18 of the 23 station slot layouts failed to parse — two bugs stacked. Their icon is a tool with
  display NBT written in 1.20's `{"item", "nbt"}` shape, while 1.21's stack codec spells the item
  `id` and takes components; the deserializer now reads the legacy pair directly and routes the tag
  through `TagCompat`, the way a real tool carries it. That exposed the second: `NBTLoadable` parsed
  a string entry by serialising the JSON element *back* to JSON, which re-quotes and escapes it, so
  the tag parser rejected every string-form NBT in the mod. It reads the string's value now.
- The melting pan's tool definition named `tconstruct:melting_fluid_effective`, a module whose
  registration was commented out during the fluid capability step. The class was ported; the line
  was not.
- Two entity melting recipes still fail on `minecraft:milk`, which is the milk-fluid decision phase 6
  owes. Unchanged, and now the only recipe errors in the log.

Final state of a run that boots, joins and stays: **77 materials, 45 tool definitions, 23 station
slot layouts, 3375 recipes**, five client mixins applied, and the player does not disconnect. The
only remaining errors in the log are the two milk recipes and vanilla's own data-fixer notices.

This run also clears two things earlier slices could not check: the `BODY` equipment slot fix from
slice 3 (nothing throws while entities tick) and the armour renderers from slice 4 loading against a
real player.

### Phase 5, slice 6: the guide books — **DONE; all 1043 pages build and render**

The six guide books are the largest single piece of content Tinkers' ships: 4.566 JSON files across
eight languages, and the only in-game documentation the mod has. They are also the one part of this
port that could not be *ported*, because the framework behind them was never vendored.

**Mantle's book framework did not exist here and is written from scratch.** Every earlier round
copied Mantle's classes and adjusted them; `slimeknights.mantle.client.book` had no counterpart in
the tree, since nothing before this slice referenced it. The 32 files in
`library/client/book` are TConstruct's own and port normally, but they are written against ~30
Mantle types that had to be reconstructed. Two sources pinned the shape precisely enough to do that
without guessing:

- **The call sites.** The TConstruct pages name every method and field they use, down to
  `PageContent#addTitle(list, title, large, color)` and `ItemElement.ITEM_SIZE_HARDCODED`. The
  compiler is a complete specification of the API surface.
- **The data.** 4.566 page files say exactly which keys each content type reads, which page types
  exist (`mantle:text`, `image_text`, `text_image`, `showcase`, `crafting`, `index`, `blank`,
  `structure`, `right_padding`), and which shapes the item references come in.

What the shipped data does *not* pin is layout — page size, margins, where the paper sits inside the
frame. Those came out of the book textures themselves: each book's page texture holds the open-book
frame at 412×200, the paper spread at 399×184, and a strip of arrow sprites down its right edge, all
measurable from the alpha channel. `BookScreen` is laid out against those measurements, and the
arrows are blitted from that strip, which is why each book's arrows match its own palette.

| Piece | Notes |
| --- | --- |
| `data` | `BookData`, `SectionData`, `PageData`, `BookAppearance`, and the element model (`TextData`, `TextComponentData`, `ImageData`, `ItemStackData`) |
| `data.content` | 13 page types: text, image, image_text, text_image, showcase, crafting, index, listing, structure, page_icon_list, blank, left/right padding |
| `repository` | `BookRepository` + `FileRepository`, with the language fallback chain |
| `transformer` | `BookTransformer`, `SectionTransformer`, `ContentGroupingSectionTransformer`, the index pass and the padding pass |
| `screen.book` | `BookScreen`, `ArrowButton` and ten elements |
| `util.html` | `HtmlSerializable`/`HtmlElement`/`HtmlGroup` for the book export |

A few places where the reconstruction had to make a decision rather than copy one:

- **Colours in `appearance.json` are written as bare `0xE5C682`.** That is not JSON, and Gson in
  lenient mode hands it over as a *string*, which the default integer adapter rejects outright. The
  book Gson installs its own integer adapter that takes both forms; the files have been written that
  way since 1.12 and are not worth migrating.
- **`Component.Serializer` is no longer a Gson adapter in 1.21.** `TinkerBook` registered it for the
  tooltip strings on showcase pages; Mantle now supplies the equivalent and registers it by default.
- **63 item references use Forge's `forge:nbt` ingredient** to pin a tool's materials or a creative
  slot's type. That serializer does not exist here and 1.21 moved stack NBT into components, so the
  `nbt` block is applied as `custom_data` — where this port already keeps tool data — with `Damage`
  lifted out to its own component. Every existing reference keeps working.
- **Slot backgrounds are drawn rather than blitted.** Forge Mantle had its own GUI sheet for them;
  drawing them from primitives avoids shipping a second set of assets and lets each book's
  `slotColor` tint the fill instead of a fixed grey sprite.
- **The multiblock preview draws the structure block by block** through the normal block renderer,
  with rotate and layer controls. The two structures are a few dozen blocks each, so the simple
  path costs nothing and every block looks exactly as it does in the world.
- **Generated listings split themselves across pages.** A section index is built from whatever tag
  injection produced — the encyclopedia's upgrade listing runs to seventy-odd entries — so the
  listing measures the page and opens a second column, then a second page, instead of running off
  the bottom. Column breaks and extra headings named by an index page's `operations` still apply.

**The three book items are unblocked.** `AbstractBookItem`/`LecternBookItem`/`ILecternBookItem` were
parked on "needs the book module and its packets". The lectern hook moved from Forge's
`RightClickBlock` event to Fabric's `UseBlockCallback`, `Slot#getSlotIndex` became
`getContainerSlot`, `TooltipContext` lost its side flag (asking the client for its player is the
same test), and `Player#closeContainer` is access-widened. Four packets carry the reader's place:
three client→server, one per way a book can be held, plus the lectern open packet. `TinkerBookItem`
extends `AbstractBookItem` again, with the book lookup behind a nested class so a dedicated server
never resolves the screen classes.

#### A harness, because a book only fails once it is read

`runClientBook` joins a world, loads all six books, **builds every page**, checks every item a page
wants to draw for a missing model, screenshots thirteen representative spreads and quits. Building
all thousand pages is the part that matters: a page's content class only runs when the page is
opened, so nothing short of opening all of them proves anything. It found five failures that a
compiling build and a title screen both missed:

1. **`ArmorItem.Type.BODY` again** — the fifth appearance of 1.21's wolf-armour slot. The armour
   material pages walked every `ArmorItem.Type` and matched the shield plating stat against BODY by
   ordinal, then asked the plating item map for a slot Tinkers never registers. Every armour
   material page in every book threw. It walks the four humanoid slots now.
2. **The fluid effects section was empty.** Upstream `return`s instead of `continue`s when an effect
   resolves to no fluids — which happens for every compat metal whose tag is empty. With 45 effects
   shipped, the first one is enough to drop the whole section; the encyclopedia's 46 fluid pages were
   simply absent.
3. **Every filled bucket and can rendered as the missing texture.** Not a book bug — a model bug the
   book made visible, and it had two independent causes. The client fluid extension asked Fabric's
   render handler for the fluid's sprite, but a render handler only knows *baked* sprites, which do
   not exist while item models bake; the declared texture from the fluid texture data is asked for
   first now. And `FluidTextureManager` loaded as an ordinary reload listener, which runs after model
   baking, so it also loads in the model-loading preparation stage — the same fix
   `MaterialRenderInfoLoader` already uses.
4. **`createUnbakedItemMaskElements` was not actually masking.** It routed to vanilla's
   `ItemModelGenerator` like its sibling, and that generator always emits one full-size front quad,
   letting the sprite's own alpha cut the shape. Correct when the quad is textured with the sprite
   that was traced — and wrong for a mask, because the caller textures these quads with a *different*
   sprite, the fluid inside the container. The fluid was painted over the whole item. It now covers
   the mask's opaque pixels with real rectangles, merged greedily into a handful of boxes.
5. **Stat blocks drew on top of each other.** Upstream estimates a text block's height as one row per
   entry, with a `TODO: calculate actual height to properly wrap long lines?` next to it. A plating
   durability line lists five numbers and wraps, so the next block started too high. The text
   elements measure themselves now and the material pages ask.

Findings 3 and 4 are outside this slice — they affect all 71 filled buckets, the copper can and the
potion bucket everywhere in the game, not just in the book — but the harness found them here and
they are fixed here.

Final state: **1.043 pages across six books, 0 failures to build**, every page type rendered and
looked at, the item path exercised end to end (right-click the book, turn a page, the page is
written back through the server). The only recipe errors left in the log are still the two
`minecraft:milk` ones phase 6 owes.

### Phase 5, slice 7: model data delivery — **DONE; 175 models were baking wrong**

Forge asked every block entity for its `ModelData` during a chunk rebuild and threaded the result
through `BakedModel.getQuads`. Vanilla 1.21.1 has no such parameter, so every data-driven block in
the mod had been rendering its base variant since the model slice: a retextured crafting station
showed oak, a seared drain showed seared brick, a tank showed nothing inside.

**Fabric calls the same idea a render attachment**, and reads it through the block view during the
chunk rebuild. Three pieces connect the two ends:

- `MantleBlockEntity` gains `getModelData()` back under its Forge name, and implements Fabric's
  `RenderDataBlockEntity` to hand the same value over as the attachment. Every block entity in the
  mod descends from it, so the seven that carry model data — tanks, drains, ducts, the smeltery and
  foundry controllers, the retextured tables, the material blocks — port unchanged.
- `BakedModelWrapper` declares itself a non-vanilla model and takes over `emitBlockQuads`, which is
  the callback that *does* get the block view. The renderer only knows how to ask a model the
  vanilla way, so the data is bound to a small view of the wrapper that answers with it.
- `requestModelDataUpdate()` returns as a section rebuild. Forge tracked model data separately from
  the chunk mesh and could refresh just that; Fabric reads the attachment while the mesh is built,
  so the only way to pick a change up is to rebuild.

That put the data in front of the models — and then two more things had to be true before any of it
showed.

#### `mantle:retextured` had no geometry at all

Mantle's model loaders were never vendored: only the support classes Tinkers' own geometry called
were written. Two of those — the texture-name expansion and the baking context that performs the
swap — are the hard half of `mantle:retextured`, so the geometry around them is written here: an
unbaked model that reads the `retextured` name list, and a baked one that rebakes itself once per
distinct texture and keeps the result. Seven models name it directly and twenty more inherit it.

Still unwritten, and still falling through to the vanilla parse of their JSON: `mantle:connected`
(135 models), `mantle:item_layer` (18), `mantle:nbt_key` (2), `mantle:colored_block` (1).

#### A child model does not inherit its parent's geometry — and 175 models are children

This was recorded as a known limit when the geometry bridge was built, with the note that the only
models affected were 16 tool pose variants. The count was wrong, and the survey that produced it
predates most of the loaders being registered. Walking every model's parent chain gives:

| Loader | Declares it | Inherits it |
| --- | --- | --- |
| `tconstruct:tank` | 8 | **44** |
| `tconstruct:tool` | 122 | **68** |
| `mantle:connected` | 135 | 25 |
| `mantle:retextured` | 7 | **20** |
| `tconstruct:fluid_texture` | 2 | **10** |
| others | — | 8 |

Every tank and gauge in the mod is a child of `tconstruct:block/template/tank`, and every seared
component is a child of `template/io`. None of them ran their loader. A `BlockModel.bake` mixin now
walks the parent chain for a geometry model and bakes through it, building the context from the
model being baked so the child's own textures and transforms are the ones used — which is what
Forge's patched `BlockModel.bake` did, for the same reason.

#### The rest of the client

- **`CommonsClientEvents`**: the books are set in the unicode font. They are written in eight
  languages against a fixed page width, and the default font's per-glyph widths differ enough
  between scripts to break the layout. Also the fluid particle, ported off Forge's client fluid
  extension.
- **`FluidClientEvents`**: eleven fluids draw translucent, through `BlockRenderLayerMap` rather than
  `ItemBlockRenderTypes`, which is not safe to mutate on Fabric. The potion bucket's tint reads the
  `potion_contents` component.
- **`TableClientEvents`**: the tinkers' chest colours, the last thing waiting on "the colour slice".
  The item reads `dyed_color`, which is where 1.21 moved what `DyeableLeatherItem` used to answer.
- **`getRenderBoundingBox`** is resolved as *not needed*, in four places. It widened the box a
  per-block-entity frustum test used; vanilla has no such test, so every block entity in a visible
  section renders however far outside its own block it draws.

#### Verified by building the scene and looking at it

`runClientBlocks` clears a room in the sky, places a retextured crafting station, a retextured
seared drain and a filled tank, points the camera at them and photographs the result. Model data is
delivered while a chunk mesh is built, so nothing about it shows up in a compile or a log — a tank
with the wiring broken renders as an empty tank. The station's legs are gold and the drain is
diamond, which is the whole chain end to end: block entity → render attachment → `emitBlockQuads` →
rebaked model.

**One thing the harness found and this slice does not fix**: the tank draws no fluid, and neither do
the faucets, channels or casting basins. It is not model data — the renderer runs, finds its fluid
cuboid, and resolves the fluid's sprite; the quads simply never reach the screen. Handed to the next
slice with `mantle_fluid`, the custom render type, named as the suspect. **It was not the render
type** — see slice 8.

### Phase 5, slice 8: render layers — **DONE; 42 blocks were on the wrong one**

Every fluid a block entity draws was invisible: tanks, gauges, the smeltery and foundry contents,
faucets, channels, casting tables and basins. Slice 7 handed this over with the custom
`mantle_fluid` render type named as the suspect, on the grounds that a custom type goes into the
shared buffer rather than a fixed one. That was wrong, and worth writing down as a lesson: the
suspect was the piece of the port that looked least like vanilla, which is a bias, not evidence.

Two experiments closed it in one sitting:

1. **Drawing the same quads through a vanilla type of the same vertex format** — `RenderType.text`,
   which shares `POSITION_COLOR_TEX_LIGHTMAP`, the translucent transparency and the lightmap, and is
   known to flush in that pass because name tags use it. Still nothing. The render type was
   exonerated.
2. **Drawing the same cuboid three blocks wide, floating above the tank.** It rendered perfectly.
   So the pipeline, shader, flush, culling, colour and light were all fine, and the fluid was simply
   invisible *inside the block* — which turns the question from "why is nothing drawn" into "what is
   in front of it".

What was in front of it was the tank's own window. The clue had been in every screenshot: the window
was **pure black**, which is what a see-through texture looks like when its block is on the solid
layer.

Forge let a model JSON carry `"render_type"` and honored it. Vanilla and Fabric only know a per-block
mapping, so `BlockRenderTypes` walks each block's blockstate to the models it names and applies what
they declare — the models stay the single source of truth. But it read only the leaf model, on a
comment's assurance that *"following parents is unnecessary as the generator writes it on the leaf"*.

It does not. A tank's blockstate names `seared_fuel_tank`, which is nothing but a parent link to
`block/template/tank`, and the template is where `render_type` is written. **42 blocks** were
therefore left on the solid layer: every tank and gauge, every drain, duct, chute and faucet, the
alloyer, the fluid cannons, the foundry controller, the clear glass and panes, the platforms. Their
transparent texels rendered opaque black and hid whatever was behind them — the fluid included.

The resolver follows the parent chain now, capped at eight links. Blocks receiving a declared render
type went from 139 to 181, exactly the 42 the scan predicted.

**This is the third time the same mistake has surfaced in this port**, and the pattern is worth
naming: *most of Tinkers' models are a texture override on top of a shared template, so anything
read off "the model" has to be read off the parent chain.* It cost the custom geometry (175 models,
slice 7), the render types (42 blocks, here), and before those the fluid textures baked into the
bucket models. Any future code that reads a key out of a model JSON should walk parents by default.

The harness scene grew to cover it: a casting basin with fluid, which exercises a different block
entity renderer, and two panes of clear glass, which exercise the cutout layer with no renderer at
all. Both were opaque black before this and are correct now.


### Phase 5, slice 9: the last five model loaders — **DONE, all 159 models claimed**

Five loader ids still fell through to the vanilla parse of their JSON: `mantle:connected`
(135 models), `mantle:item_layer` (18), `mantle:nbt_key` (2), `mantle:colored_block` (1) and
`forge:composite` (3). Falling through was survivable but wrong in five different ways: connected
glass wore a full frame on every block, the slime-metal storage blocks were entirely invisible
(their JSON has no top-level elements, only children), the glowing items neither glowed nor
carried their baked colour, and the creative slot and crystalshot ignored their data-driven
texture variants.

What went in, from upstream Mantle 1.20 where a source existed and from scratch where not:

- **`ConnectedModel` + `ConnectedModelRegistry`** (new files): the borderless-glass machinery.
  The base model bakes normally and 63 connection variants bake lazily by rewriting faces to
  suffixed textures and rebaking the same elements. The Fabric twist: Forge delivered the
  connection bits through `getModelData`; here `Baked.emitBlockQuads` gets the world directly and
  computes them from the neighbours on the spot. A blockstate-property fallback serves callers
  without a world. Panes work through Fabric API's multipart forwarding plus the `pane` predicate.
- **`ModelTextureIteratable`** (new file): walks a model's texture maps up the parent chain,
  because `getMaterial` collapses `#name` hops that connected and NBT-key need to see. Needed two
  access widener entries (`BlockModel.textureMap`, `BlockModel.parent`).
- **`NBTKeyModel`** (new file): the name and the JSON's `nbt_key` survive from 1.20, but the data
  now lives in `minecraft:custom_data`, read zero-copy via `CustomData.getUnsafe()` since
  `ItemOverrides.resolve` runs every frame.
- **`MantleItemLayerModel.Geometry`** (added to the existing static-helper class): layers with
  baked colour, glow and tint opt-out. Every declaring model in the tree is single-layer.
- **`ColoredBlockModel`** was already complete; it just had no registered loader — and its class
  doc claimed nothing needs one, which was wrong: the queen's slime storage block does.
- **`CompositeModel.Geometry`** (added): `forge:composite` for the three storage blocks pairing an
  opaque frame with a translucent overlay. Children bake independently and merge by delegation so
  their culled faces survive. Per-child `render_type` collapses on Fabric, so `BlockRenderTypes`
  now lifts the most permissive child layer onto the whole block.
- **`SimpleBlockModel.bakeWithElements`** (new method, overridden in `ColoredBlockModel`): the
  rebake path the connected variants use, keeping per-element colours — which is why a stained
  glass wall stays stained after its borders vanish.

**Validation.** The loader plugin's per-reload log is the metric: models resolved through custom
geometry went 243 → 402, and the per-loader counts match the tree exactly — connected=135,
item_layer=18, nbt_key=2, colored_block=1, composite=3. The harness scene grew a 2×2 clear glass
wall and a 2×2 blue stained one (both now seamless with only an outer rim), a two-high pane column
(no middle seam), and the three storage blocks (visible at all, textured, queen's slime with its
baked glow). Zero model errors in the log.

**For the next slice**: `NBTKeyModel.registerExtraTexture` and `ConnectedModelRegistry`'s register
methods are API surface addons use; nothing in this tree calls them beyond the built-ins, so no
init hook was added for them. If an addon compat phase needs them, wire the calls into the client
entrypoint before the first resource reload.


### Phase 5, slice 10: the command layer — **DONE; phase 5 complete**

The last parked pieces of phase 5: `/tconstruct` with its argument types and subcommands, the
Mantle command framework it builds on, the client commands, the part texture generator they feed,
and the two client hooks that had no home (block overlay, recipe cache reload).

**Server commands.** `/tconstruct` registers modifiers/materials/tool_stats/slots/durability, the
two report tables and `generate part_textures` + `generate hidden_fluids_tag`;
`generate melting_recipes` waits for phase 7, since it serializes through the datagen recipe
builders. `/mantle tags view|entries|dump|for|preference` and `/mantle sources data` came with the
framework: TagSource/RegistryTagSource/TagSourceArgument port nearly clean, and Tinkers plugs its
material and modifier registries in as custom tag sources — `MaterialRegistry.getTagSource()` is
restored for exactly that. Forge's `TablePrinter` and `ModIdArgument` were rewritten
(`slimeknights.mantle.util` / `.command`). Registration moved to `CommandRegistrationCallback`;
the deferred argument-type register was already Fabric-shaped.

**The hard-won lesson of the slice**: the server syncs its command tree to every joining client,
and each argument type in the tree must be in the argument type registry — an unregistered one
does not fail at registration but disconnects the client on join with the opaque message
*"Invalid player data"* / *"Couldn't place player in world: Unrecognized argument type"*. The
mod_id argument hit it; anything adding argument types must register them.

**Client commands** live under `/mantle_client` rather than upstream's `/mantle`: Fabric consumes
any typed command whose root literal exists in the client dispatcher, so sharing the root would
swallow the server half. `book open`, `book export_images [scale]` (per book or per domain) and
`clear_book_cache` are in; `export_html` stayed out — the reconstructed book elements carry no
HTML serialization, and that export exists to feed SlimeKnights' website.

**The book image export** could not keep upstream's shape. Upstream rendered every spread into an
offscreen target in one call; on 1.21 too much of the gui pipeline fights that —
`Screen.renderBlurredBackground` re-binds the main target *unconditionally* (blurred or not),
every `GuiGraphics.blit` resets the blend function, and the net effect was pages accumulating at
quarter alpha (measured: paper pixel 174 = texture 232 × 0.25 + backdrop 153 × 0.75, exactly).
The port drives the export across real frames instead: open the screen, capture the freshly
rendered window cropped to the book rectangle at a pinned gui scale, turn the page, repeat. The
result is pixel-identical with the game; the trade is the world showing through the book's
rounded corners and a few frames per spread.

**Part texture generation** works end to end: `/tconstruct generate part_textures all` sends
`GeneratePartTexturesPacket` (registered PLAY_TO_CLIENT), and the client generator reads the
render info generators, traces the grey sprites and writes a complete resource pack — 10 427
textures in ~5.5 s. That pulled the runtime slice of `library/client/data` through the gate
(sprite transformers, sprite readers, `MaterialPartTextureGenerator`, `MaterialGeneratorInfo`)
with a minimal `ExistingFileHelper` shim for the datagen signatures phase 7 will flesh out.
`assets/mantle/lang/en_us.json` came over wholesale — the port had no Mantle lang at all, which
showed as raw keys in command feedback.

**TinkerClient** shrank to what nothing else had claimed: the sprite transformer serializers, and
the transparent block overlay, now fed by `ScreenEffectRendererMixin`. 1.21's
`getViewBlockingState` drops the block position, so the redirect repeats vanilla's eight-point
scan keeping the position; soul glass from inside shows the glass texture instead of vanilla's
opaque wall (harness-verified). `ClientPacketListenerRecipesUpdatedMixin` replaces
`RecipesUpdatedEvent` for the recipe cache reload.

**Validation.** New `runClientCommands` harness joins a world and proves five things in one run:
both command roots registered; `/tconstruct modifiers @s add tconstruct:haste 1` lands on the held
tool (asserted on the stack, not just chat); `/mantle tags view minecraft:item minecraft:planks`
lists the tag; the part texture pipeline writes its 10 427 files; the book export writes 98
readable spreads. The block harness gained the soul-glass overlay shot. Chat protocol screenshot
kept as the visual record.


- [x] **5 — Client.** Custom baked models (tool layers, tanks, casting), renderers, screens.
### Phase 6, slice 11: EMI, wave 1 — the smeltery family, plus three creative-tab crashes

EMI 1.1.24 is the pack's recipe viewer, so the Forge build's JEI plugin (49 classes) is being
rewritten against EMI's API in two waves. Wave 1 is the smeltery family: **casting table, casting
basin, melting, foundry, alloying, entity melting and molding**, with the fuel display (fluid fuels
per temperature, the solid-fuel slot when the melter could run on coal), byproducts cycling through
the foundry tank, workstations, and the catch-all default entity melting recipe. The categories
draw with the same background textures the JEI plugin shipped, so the cards look the way Forge
players know them; amounts convert at 81 droplets per millibucket. The plugin loads through the
`emi` entrypoint, so a pack without EMI never touches the classes; `runClientEmi` joins a world,
opens all seven categories through `EmiApi.displayRecipeCategory` and photographs each.

**EMI's index build surfaced three crashes that had been waiting in the creative tabs** — nothing
had opened a creative inventory since phase 3:

- **`TagCompat.getOrCreateTag` silently discarded every mutation.** `CustomData.of` copies its
  argument (verified in the bytecode: `CompoundTag.copy` before the constructor), so the "anchored
  live tag" contract the port documented was never true. Every creative slot variant came out
  identical and vanilla's duplicate check tripped. The method now returns the component's own tree
  via `getUnsafe()`. Anything that mutates after `getOrCreateTag` was affected — the crystalshot
  variants among them.
- **The potion bucket had been air since phase 3.** Registration is eager on Fabric, so
  `unplacable()` constructed the fluid before assigning the builder's bucket supplier;
  `flowing()` had the delayed-supplier wiring for exactly this and `unplacable()` did not. The
  potion bucket item existed but no fluid knew about it.
- **Two order-sensitive duplicate tab entries**: the arrow cast (gold only, no sand forms) was
  accepted once per cast-variant pass, and two tool materials can collapse to the same single-material
  build when each only fits parts the other does not — which build the fallback picks depends on
  material registry order, so upstream never saw it. Tool variants deduplicate now.

**Held for wave 2**: the tables family (modifiers, worktable, part builder, tool building,
severing), recipe ids on the EMI recipes (mantle's recipe helper drops holders), stack comparisons
for tools/parts, recipe-fill handlers, fluid-unit tooltips (ingots/blocks) on the tanks, the
`tag.*` translations EMI's dev mode lists as missing, and a look at why the first casting-table
card renders its tank empty (likely the retextured table recipes).


### Phase 6, slice 12: EMI, wave 2 — the tables family; the JEI replacement is complete

The remaining five categories: **modifiers** (273 pages — the five station inputs with their
purpose icons when empty, the modifier named and iconed at the top, level bounds, the slot cost
drawn from the same data-keyed model sprites the creative slot item uses, requirement errors as
tooltips, tool before and after), **modifier worktable**, **tool building** (the station layout's
part slots over the giant translucent tool render, materials cycling in sync with the result),
**part builder** (pattern face drawn from its texture, material cost, colored material name) and
**severing** (the spinning entity box, shared with entity melting through a base-class helper).

Modifiers are first-class EMI stacks now (`ModifierEmiStack`): keyed by modifier id, rendered
through the modifier icon manager, named with the level — so they work as outputs, as cycling
worktable options, and as the workstation icons of the severing and melting categories.
Tools, tool parts and potion buckets get `Comparison.compareComponents`, so material variants
stay distinct in the index instead of collapsing onto one entry.

Two early-init traps worth remembering: EMI scans entrypoints long before datapacks load, so
category icons must not call `getRenderTool` in static init (it caches whatever it built first —
the icons resolve lazily on first draw); and the worktable/severing display interfaces carry no
recipe ids on 1.21, so those EMI recipes go id-less.

**Still open on EMI**, none blocking the pack: recipe-fill handlers for the tinker station and
crafting station, ids for the recipes mantle's helper strips holders from, the empty-looking
first casting card (retextured table recipes), and `tag.*` translations (the convention postdates
upstream's lang file; EMI shows raw ids in dev mode).


### Phase 6, slice 13: the block transfer bridge, milk, and Jade

**The gap that mattered: nothing exposed Tinkers' blocks to Fabric's storage lookups.** The
`FluidStorageBridge` (mantle handler → Fabric `Storage<FluidVariant>`) existed since the transfer
layer port and was never registered for blocks — items had their wiring, blocks had none, so
pipes, pumps and Jade saw no storage on tanks, melters, smelteries or casting blocks.
`TinkerFluidStorage.registerBlockBridges()` adds sided fallbacks for both fluids and items,
gated on mantle's `ICapabilityProvider` so only Tinkers/Mantle block entities answer, and firing
only where no block-specific handler already matched. Verified end to end with Jade 15.10.5 in
the dev runtime: the harness stands before the filled fuel tank and Jade's overlay reads
*Molten Iron 3B* off the bridge (one F1 lesson included: the harness had `hideGui` on, which
hides Jade too).

**Milk.** Forge registered a milk fluid under the vanilla namespace and upstream's two skeleton
melting recipes output `minecraft:milk`; they had failed to parse since phase 4. Fabric has no
standard milk, so the port registers `tconstruct:milk` — an unplaceable fluid whose bucket form
is the *vanilla* milk bucket (the delayed-supplier bucket wiring from slice 11 handles the
existing-item case), tagged `c:milk` for whatever milk other pack mods bring, with hand-made
white textures until the phase-7 datagen owns them. The two generated recipe JSONs now point at
it; the phase-7 datagen provider must carry the same substitution. Recipe parse errors on world
load: two → zero.

**Unify, located.** The mod is `unify-1.21.1-0.0.5.jar` and lives only in the user's test
environment — the mrpack in the repo root (`GummiCraft 1.2.81.mrpack`), not in the installed
instance and not in the release pack. The steel-ingot verification stays scheduled for the
pack-boot slice, against the mrpack's mod set.


- [ ] **6 — Mod compat.** EMI, Jade, Trinkets, energy, plus cross-mod recipes for GummiCraft.
  **Unify is in the pack** (user note, 2026-08-20): it rewrites recipe *outputs* to the
  pack-preferred item per tag. Expected to just work, but must be verified against Tinkers,
  because casting/melting are custom recipe types Unify may not see. Agreed check: Unify
  replaces the Oritech steel ingot with the Energized Power one — cast a steel ingot in the
  smeltery and confirm which mod's ingot comes out. That single test suffices.
- [ ] **7 — Datagen & documentation.**

## Access widener

The Forge `accesstransformer.cfg` (294 entries) uses SRG names without field descriptors,
which AccessWidener requires. Entries are therefore migrated per-module alongside the code
that needs them rather than in one unverifiable batch. Migrated so far:
`Entity.wasEyeInWater`, `WoodType.register`, `BucketItem.content`, and six `FlowingFluid`
spread internals.

### InvertedFluid needs a 1.21 rewrite (before phase 4)

`InvertedFluid` copies FlowingFluid's private 1.20.1 spread machinery and inverts it for
upward-flowing fluids. 1.21 refactored those internals — `getCacheKey` and
`canPassThrough` no longer exist — so this is a rewrite against the new base class, not a
patch. It matters: **ichor and molten cinderslime are upward-flowing core fluids**
(negative density). Tracked in `unported.gradle`.

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
| `Fluid.getFluidType()` (23 files) | `FluidType.of(fluid)` static lookup | filled by `FluidType.register` at fluid registration |
| `BucketItem.getFluid()` | access-widened `content` field | Forge getter over a private vanilla field |
| `ItemStack.getCraftingRemainingItem()` | Fabric's `getRecipeRemainder()` | stack-aware remainder |
| `ForgeI18n` | vanilla `Language.getInstance()` | server-safe language table |
| `PotionUtils.getPotion` | `POTION_CONTENTS` component | potion transfers rebuild the legacy `{Potion: id}` tag so the potion fluid's format is unchanged |
| `AddReloadListenerEvent` / `OnDatapackSyncEvent` | Fabric `ResourceManagerHelper` / join-sync via network module | `FluidContainerTransferManager.init()` |

## Runtime verification

A Loom dev **server boots to "Done" with the ported jar** — entrypoints, component
registration, the network channel and the fluid-transfer reload listener all run. The
transfer manager loaded its JSONs and rejected exactly those whose items are not yet
registered (loudly, as designed). Remaining log noise is data referencing phase-4 content.

Temporarily excluded from the jar until their registration code is ported (fatal registry
errors otherwise — see the note in `build.gradle`): `data/tconstruct/worldgen`,
`data/tconstruct/trim_material`.

## Build

```bash
JAVA_HOME="/c/Program Files/Java/jdk-21" ./gradlew build
```
