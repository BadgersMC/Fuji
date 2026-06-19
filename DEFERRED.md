# Fuji — Sakura merge: deferred-patch triage

Status of the Sakura patches that weren't taken verbatim during the merge.

## ✅ Ported

### Lava tick delay — `LavaFluid.java`
- **Resolution:** `getCompensatedTickDelay` now feeds **Sakura's deterministic per-region delay** (`getTickDelay(level, pos)` → `localConfig().lavaFlowSpeed`) as its base, instead of the raw delay.
- **Result:** Sakura's per-region lava-flow-speed API is now respected (it was previously ignored). Deterministic when Leaf's lag-compensation is off (the cannon default); lag-comp still wraps Sakura's base if `LagCompensation.enableForLava` is enabled. Best of both.

### Inside-block iteration mechanics — `BlockGetter.java`, `Entity.java` (Sakura #0026)
- **Resolution (patch 0345):** restored Sakura's two-overload version-accurate `forEachBlockIntersectedBetween` — the `mechanicsTarget` parameter, the `movedThreshold` version branches, and Sakura's block-pos iterators — and threaded `mechanicsTarget` (plus the version-dependent margin/flag) through `Entity.checkInsideBlocks`, while keeping Leaf's chunk-cache optimization in the visitor body.
- **Result:** entity block-traversal now matches upstream Sakura at every `mechanicsTarget`, not just the hardcoded-modern path Leaf shipped. At modern mechanics the margin/flag equal the prior values (no change for normal play); the legacy and single-axis paths cannons rely on are now version-accurate. Ported during cannon testing — the 160-wall stress test passed with it in the build.

## ⛔ Intentionally not porting (non-cannon, Leaf equivalent is fine/better)

### Hopper ticking — `HopperBlockEntity.java`, `Level.java` (#0025)
Leaf's Lithium "Sleeping Block Entity" hopper optimization is mature and arguably better than Sakura's alternative. No cannon impact. **Keeping Leaf's.**

### Mob spawner behaviour — `BaseSpawner.java`
Leaf's "Spawner Configurations" module already covers configurable spawner behaviour. Non-cannon. **Keeping Leaf's.**

### Cactus / crop growth — `CactusBlock.java`
Leaf's Pluto chunk-lookup optimization vs Sakura's random-chance crop growth. Non-cannon. **Keeping Leaf's.**

## 🔬 Held (low priority — port only if a discrepancy appears)

### Entity-collision retrieval limit — `LivingEntity.java` (Sakura #0014)
- `maxEntityCollisions` limit for pushable-entity retrieval.
- Only touches `LivingEntity.pushEntities` (mobs); falling-block/TNT cannon entities never run it, so it does **not** affect sand/TNT stacking (confirmed during stacker testing — the 384 stacker works without it). Held as a low-priority fidelity item.
- **Symptom that would require it:** mob-based cannon mechanics differ from upstream Sakura.
