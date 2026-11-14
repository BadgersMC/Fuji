# Frontier Paper 26.4 — Moonrise integration audit

**Date:** November 13, 2026. **Status: scratch port only, NOT integrated.**

## Scope and grounded results

The Frontier upstream repository contains an unapplied `0003-Moonrise-optimisation-patches.patch` covering **231 source files** (about 1.95 MB). It includes chunk-system/starlight, entity tracking, NBT read/write, collision, bitstorage, biome, and generation changes.

A detached worktree at source commit `4495522` received that patch with `git apply --reject --whitespace=nowarn`. The tool reported **185 cleanly applied file patches**, while **46 files had rejected hunks**. All edits remain in `D:\BadgersMC-Dev\Fuji-26.4-Moonrise-Integration-Scratch`, not in the tracked experimental branch.

## Local resolution progress (checkpointed as a local WIP, not pushed)

Ten rejected files were manually reconciled against 26.4 source, with the corresponding `.rej` files removed:

- `DoubleTag.java`, `FloatTag.java`, `StringTag.java`: reintroduced the Moonrise `FastNBTWrite` serialization methods while preserving Frontier 26.4 record layouts and numeric values.
- `NbtIo.java`: restored Moonrise NBT read/write imports required by the already-applied fast paths.
- `TagKey.java`: interning now uses the actual 26.4 `Identifier.moonrise$intern()` implementation.
- `PoiSection.java`: implemented the Moonrise POI-section interface and cached optional, preserving the 26.4 POI validity test.
- `PalettedContainer.java`: exposed the existing indexed setter needed by optimized palette fills; did not change its logic.
- `ChunkStatusTasks.java`: passed the source chunk position through to 26.4 post-load entity handling, preserving the duplicate-UUID context.
- `PoiManager.java`: reconciled Moonrise POI chunk ownership, dirty tracking, distance propagation, and the updated SectionStorage override signature.
- `AcquirePoi.java`: re-targeted Moonrise nearest-POI lookup while preserving Purpur's configurable search radius and the nearest-five limit.

**Remaining rejected files: 36.** The partially integrated source was committed **locally only** on `wip/frontier-26.4-moonrise-unfinished` at `97c1375e4a1981b59522f38adbf74969fbed2020` with a prominent DO NOT DEPLOY warning. The 36 remaining `.rej` files remain untracked in that worktree. Scratch source `git diff --check` passed before checkpointing. A separate Gradle source-set override diagnostic stalled before printing its source-directory configuration and was terminated; it produced **no valid Moonrise compile result**. Binary NBT parity, chunk lifecycle, packet parity, worldgen parity, cannon gameplay, plugin event order, and data durability are **all unverified**. This local WIP commit was **not pushed** or merged to the Fuji experimental branch; do not deploy it to the test server.

## Remaining incompatible source files

| Source file |
|---|
| `ca/spottedleaf/moonrise/paper/util/BaseChunkSystemHooks.java` |
| `io/papermc/paper/FeatureHooks.java` |
| `net/minecraft/server/level/ChunkHolder.java` |
| `net/minecraft/server/level/ChunkMap.java` |
| `net/minecraft/server/level/DistanceManager.java` |
| `net/minecraft/server/level/ServerChunkCache.java` |
| `net/minecraft/server/level/ServerLevel.java` |
| `net/minecraft/server/level/ServerPlayer.java` |
| `net/minecraft/server/MinecraftServer.java` |
| `net/minecraft/world/entity/Entity.java` |
| `net/minecraft/world/level/biome/BiomeManager.java` |
| `net/minecraft/world/level/block/state/StateHolder.java` |
| `net/minecraft/world/level/chunk/ChunkAccess.java` |
| `net/minecraft/world/level/chunk/ChunkGenerator.java` |
| `net/minecraft/world/level/chunk/ImposterProtoChunk.java` |
| `net/minecraft/world/level/chunk/LevelChunkSection.java` |
| `net/minecraft/world/level/chunk/ProtoChunk.java` |
| `net/minecraft/world/level/chunk/status/ChunkPyramid.java` |
| `net/minecraft/world/level/chunk/storage/SectionStorage.java` |
| `net/minecraft/world/level/chunk/storage/SerializableChunkData.java` |
| `net/minecraft/world/level/Level.java` |
| `net/minecraft/world/level/levelgen/Beardifier.java` |
| `net/minecraft/world/level/levelgen/Heightmap.java` |
| `net/minecraft/world/level/levelgen/material/MaterialRuleContext.java` |
| `net/minecraft/world/level/levelgen/material/MaterialSystem.java` |
| `net/minecraft/world/level/levelgen/NoiseBasedChunkGenerator.java` |
| `net/minecraft/world/level/levelgen/NoiseChunk.java` |
| `net/minecraft/world/level/levelgen/structure/StructureCheck.java` |
| `net/minecraft/world/level/levelgen/structure/templatesystem/AllOfRuleTest.java` |
| `net/minecraft/world/level/levelgen/structure/templatesystem/AnyOfRuleTest.java` |
| `net/minecraft/world/level/material/FlowingFluid.java` |
| `net/minecraft/world/level/material/FluidState.java` |
| `net/minecraft/world/level/portal/PortalForcer.java` |
| `net/minecraft/world/level/ServerExplosion.java` |
| `net/minecraft/world/phys/shapes/Shapes.java` |
| `net/minecraft/world/phys/shapes/VoxelShape.java` |

## Next integration sequence

1. Reconcile core chunk lifecycle and entity tracking as one unit: `ChunkMap`, `ChunkHolder`, `DistanceManager`, `ServerChunkCache`, `ServerLevel`, `MinecraftServer`, `Level`, `Entity`, and Moonrise hooks. Do not stub chunk IO or entity synchronization.
2. Bring world generation, palette, state-holder, fluid, collision, POI, and structure components into 26.4 semantic parity; preserve deterministic seed behavior.
3. Regenerate patches **from applied source** using Paperweight, do not edit existing patch files directly. Keep Moonrise work isolated until all core compile and lifecycle checks pass.
4. Perform serialized NBT byte/read-write/reopen and invalid-data tests in disposable files; test world generation against reference seeds before using any real world.
5. Resolve Paper/Sparkly parallel-ticking dependencies separately; no threading feature activation without ownership and race tests.
6. Verify a clean full source replay, successful Java 25 server JAR build, Velocity join/transfer, Bedrock parity, mob ticking, TNT+concrete cannons, player knockback, and chunk unload/restart.

The experimental main branch still retains the original 325 Minecraft feature patch slots and all original Paper feature patches. The new compatibility adapter patches in that branch are separate from this uncommitted Moonrise scratch port.
