# Tinkers' Construct — Fabric 1.21.1 documentation

Documentation for the Fabric port living on the `fabric/1.21.1-gummicraft` branch.

| Page | Contents |
|---|---|
| [PORT.md](PORT.md) | the port itself: what runs, how Forge concepts were mapped, verification, known deviations |
| [PACK-INTEGRATION.md](PACK-INTEGRATION.md) | using the port in a modpack: install, tag preferences/Unify, compat metals, removing vanilla recipes |
| [reference/](reference/README.md) | the complete generated reference: all 3,036 recipes, all materials, modifiers and tools |
| [../PORTING.md](../PORTING.md) | the full engineering log of the port, slice by slice |

The reference pages are generated from the shipped data by
`scripts/generate_recipe_docs.py` — rerun it after datagen changes so they never drift from
what the game actually loads.
