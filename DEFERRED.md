# Fuji — Sakura merge: deferred-patch triage

Status of the Sakura patches that weren't taken verbatim during the merge.

## ✅ Ported

### 1. Lava tick delay — `LavaFluid.java`
- **Resolution:** `getCompensatedTickDelay` now feeds **Sakura's deterministic per-region delay** (`getTickDelay(level, pos)` → `localConfig().lavaFlowSpeed`) as its base, instead of the raw delay.
- **Result:** Sakura's per-region lava-flow-speed API is now respected (it was previously ignored). Deterministic when Leaf's lag-compensation is off (the cannon default); lag-comp still wraps Sakura's base if `LagCompensation.enableForLava` is enabled. Best of both.

## ⛔ Intentionally not porting (non-cannon, Leaf equivalent is fine/better)

### 2. Hopper ticking — `HopperBlockEntity.java`, `Level.java`
Leaf's Lithium "Sleeping Block Entity" hopper optimization is mature and arguably better than Sakura #0025's alternative. No cannon impact. **Keeping Leaf's.**

### 3. Mob spawner behaviour — `BaseSpawner.java`
Leaf's "Spawner Configurations" module already covers configurable spawner behaviour. Non-cannon. **Keeping Leaf's.**

### 4. Cactus / crop growth — `CactusBlock.java`
Leaf's Pluto chunk-lookup optimization vs Sakura's random-chance crop growth. Non-cannon. **Keeping Leaf's.**

## 🔬 Held for cannon-fidelity testing (port only if a discrepancy appears)

These are the two hardest merges in the project, and both tie into Sakura's `mechanicsTarget`
version system (the field + classes + Entity cannon-movement are already merged; these two call
sites aren't). Rather than pay their steep merge cost on a hunch, port them **only if live cannon
testing reveals a behavioural difference** — the symptom points to the fix.

### 5. Entity-collision retrieval limit — `LivingEntity.java` (Sakura #0014)
- `maxEntityCollisions` limit for pushable-entity retrieval. Affects cannon entity stacking.
- Needs the `maxEntityCollisions` field wired into `Entity`.
- **Symptom that would require it:** cannon entity-stacking behaves differently from upstream Sakura.

### 6. Inside-block iteration mechanics — `BlockGetter.java` (Sakura #0026)
- `forEachBlockIntersectedBetween` gains `mechanicsTarget` param + version-accurate `movedThreshold`.
- Leaf already ports the core "optimise check inside blocks"; deferred part is the version-accuracy margin.
- **Symptom that would require it:** entities clip into blocks differently, or cross-version (legacy) cannon mechanics are off.
