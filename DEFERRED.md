# Fuji — Deferred Sakura patches

Patches not yet applied; require a decision or deeper rework. Each kept Leaf's behavior to stay compiling.

## 1. Lava tick delay — `LavaFluid.java`, `LiquidBlock.java`
- **Leaf:** `getCompensatedTickDelay(level, pos)` — TT20 lag-compensation.
- **Sakura:** `getTickDelay(level, pos)` — deterministic lava-flow-speed API (important for cannon timing).
- **Conflict:** philosophically opposed (lag-comp vs deterministic). For a cannon/faction server you likely want Sakura's deterministic timing.
- **Status:** kept Leaf's `getCompensatedTickDelay`. **Needs user decision**: switch to Sakura's deterministic API, or make Leaf's compensation wrap Sakura's configurable base delay.

## 2. Hopper ticking — `HopperBlockEntity.java`, `Level.java`
- **Leaf:** Lithium "Sleeping Block Entity" hopper optimization (mature).
- **Sakura #0025:** alternative hopper opt (`sakura$getAttachedContainer`, `SlotCountingItemList`, `isBlockEntityActive`).
- **Status:** kept Leaf's Lithium hopper. Sakura #0025 deferred (competing approach to same goal). Non-cannon-blocking.

## 3. Mob spawner behaviour — `BaseSpawner.java`
- **Leaf:** "Spawner Configurations" module. **Sakura:** "configure mob spawner behaviour".
- **Status:** kept Leaf's. Sakura spawner config deferred (overlapping feature, non-cannon).

## 4. Cactus/crop growth — `CactusBlock.java`
- **Leaf:** Pluto "decrease chunk/block lookups" opt. **Sakura:** random-chance crop growth.
- **Status:** kept Leaf's. Sakura crop-growth deferred (non-cannon).

## 5. Entity pushable retrieval limit — `LivingEntity.java`
- **Sakura #0014:** use `maxEntityCollisions` limit for pushable-entity retrieval (cannon-relevant).
- **Status:** kept Leaf's OnlyPlayerPushable path. Sakura's needs the `maxEntityCollisions` field wired into Entity; deferred. Revisit when wiring #0013/#0014.

## 6. Inside-block iteration mechanics — `BlockGetter.java`
- **Sakura #0026:** `forEachBlockIntersectedBetween` gains `mechanicsTarget` param + version-accurate `movedThreshold` + own SimpleBlockPosIterator.
- **Leaf:** already ports "optimise check inside blocks" (its own iterator).
- **Status:** kept Leaf's version (avoids method-signature cascade; consistent with keeping Leaf's `Entity.checkInsideBlocks`). Sakura #0026 BlockGetter version-margin deferred.
