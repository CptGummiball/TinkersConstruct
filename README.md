# Tinkers' Construct — Fabric 1.21.1 port

This repository contains the community-maintained **Fabric 1.21.1 port of Tinkers'
Construct 3.11.2**, built for the GummiCraft modpack. It is based on the official Forge
1.20.1 sources from [SlimeKnights/TinkersConstruct](https://github.com/SlimeKnights/TinkersConstruct),
but it is not an official SlimeKnights release and is not supported by the upstream team.

The active port lives on the
[`fabric/1.21.1-gummicraft`](https://github.com/CptGummiball/TinkersConstruct/tree/fabric/1.21.1-gummicraft)
branch. Mantle is ported and bundled in the same jar, so a separate Mantle jar is not used.

## Target

| Component | Version |
|---|---|
| Minecraft | 1.21.1 |
| Fabric Loader | 0.16.0 or newer |
| Fabric API | 0.116.0 or newer for 1.21.1 |
| Java | 21 |
| Tinkers' Construct base | 3.11.2 for Forge 1.20.1 |

This is a full source port rather than a compatibility wrapper. The tool system, smeltery,
foundry, melting, alloying, casting, fluids, books, world generation, generated resources,
EMI integration and pack-facing data systems have all been migrated to Fabric APIs. The
complete implementation status and known deviations are documented in
[docs/PORT.md](docs/PORT.md).

## Installing

For GummiCraft, use the jar produced from this branch and keep the pack's matching dependency
versions. A standalone installation needs at least:

- Fabric Loader and Fabric API
- Forge Config API Port

The port also integrates with the GummiCraft versions of EMI, Jade, Trinkets, Cardinal
Components and Team Reborn Energy. See [docs/PACK-INTEGRATION.md](docs/PACK-INTEGRATION.md)
for modpack setup, tag preferences, compat metals and recipe-removal presets.

## Building from source

Git and a Java 21 JDK are required.

```bash
git clone --branch fabric/1.21.1-gummicraft https://github.com/CptGummiball/TinkersConstruct.git
cd TinkersConstruct
./gradlew build
```

The distributable jar is written to `build/libs/`. Useful development tasks include:

```bash
./gradlew runClientWorld   # normal world boot/regression run
./gradlew runClientBlocks  # machine, transfer and recipe harness
./gradlew runClientEmi     # EMI recipe-category checks
./gradlew runDatagen       # regenerate data and assets
```

More documentation is indexed in [docs/README.md](docs/README.md). The generated reference
under [docs/reference](docs/reference/README.md) lists the recipes, materials, modifiers and
tools shipped by this branch. The detailed engineering history is kept in
[PORTING.md](PORTING.md).

## Reporting issues

Report Fabric-port bugs in this fork's
[issue tracker](https://github.com/CptGummiball/TinkersConstruct/issues), not in the upstream
SlimeKnights tracker. Include:

- the exact commit or jar version;
- Minecraft, Fabric Loader and Fabric API versions;
- relevant mod versions and whether the issue also occurs without the full modpack;
- reproduction steps; and
- `latest.log` plus the crash report when applicable.

## Credits and license

Tinkers' Construct and Mantle were created by the SlimeKnights team. This Fabric port is
maintained for GummiCraft by CptGummiball and contributors. Code, assets and text remain
available under the repository's [MIT License](LICENSE).

Modpacks may include builds of this port, but the modpack or build distributor is responsible
for supporting those builds. Please keep the unofficial-port notice and upstream attribution
when redistributing it.
