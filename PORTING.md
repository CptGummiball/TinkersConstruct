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

- [ ] **5 — Client.** Custom baked models (tool layers, tanks, casting), renderers, screens.
- [ ] **6 — Mod compat.** EMI, Jade, Trinkets, energy, plus cross-mod recipes for GummiCraft.
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
