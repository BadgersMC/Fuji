# Fuji — Resume State (2026-06-16)

## Where we are
Phase 0 ✅ (Leaf 1.21.11 builds + jar on Windows). Phase 1 ✅ (triage). Phase 2 **in progress** (merging 97 modified MC files + additive classes).

## Mechanism (CRACKED — this is what kills other attempts)
- Work-repo = `leaf-server/src/minecraft/java/.git` (HEAD = base + each Leaf patch as a commit). `core.autocrlf=true` here.
- Workflow: edit materialized source → `git add . && git commit` IN that repo → `./gradlew rebuildPatches` → patches regenerate.
- Restore clean source instantly: `cd leaf-server/src/minecraft/java && git checkout -- .` (NOT a 44-min rebuild).
- **Line-ending trap:** Leaf files are CRLF, Sakura's are LF. Naive 3-way merge → whole-file phantom conflicts. Fix = merge in LF, write back CRLF (`.fuji/merge3.sh`). autocrlf then makes git see only real deltas.
- Merge base (content-identical to Leaf's Paper): `Sakura-donor/.gradle/caches/paperweight/upstreams/server-work/paper/src/minecraft/java`
- Theirs: `Sakura-donor/sakura-server/src/minecraft/java`
- Tooling in `.fuji/`: `merge3.sh` (driver), `resolve.py`, `all97.txt` (file list).

## Done
- Bucket 1: 116 additive Sakura classes copied (92 in `leaf-server/src/main/java/me/samsuik`, 24 in `leaf-server/src/minecraft/java/me/samsuik` incl. `tracking/`).
- 67 MC files auto-merged clean. ~31 conflict files resolved (combine-both / empty-base keep-both / judgment calls).
- 2 deferred (LavaFluid, LiquidBlock — see DEFERRED.md).

## UPDATE 2 — ALL net/minecraft conflicts resolved + committed (797b2bb)
- Every conflict marker cleared across the materialized tree. Work-repo commit `797b2bb` (378 commits total).
- Watchable knot: GRAFTED (keep both) on EntityEquipment, ItemStack, InsideBlockEffectApplier.
- Entity.java (10 hunks): h0 both-protected; h2/h3 Sakura cannon-movement; h4/h5 Leaf-cache + Sakura collision flags/mechanicsTarget; h1/h6/h7/h8 kept Leaf (checkInsideBlocks); h9 public.
- Found & fixed triage gaps: (a) Sakura additive classes also in src/minecraft/me/samsuik (24) — copied; (b) sakura-api module (18 additive classes incl. mechanics API) — copied to leaf-api/src/main/java/me/samsuik; (c) 4 non-net.minecraft modified files (CollisionUtil, EntityLookup, ChunkEntitySlices, RedstoneWireTurbo) — merged.
- Deferred (DEFERRED.md): LavaFluid/LiquidBlock lava-tick; Sakura #0025 hopper; mob-spawner; cactus crop; LivingEntity #0014 pushable-limit; Sakura checkInsideBlocks version-margin.

## UPDATE 3 — COMPILE-INTEGRATION PHASE (work-repo @ 1596b92)
`leaf-api` compiles (added `compileOnly("org.spongepowered:configurate-yaml:4.2.0")` to leaf-api/build.gradle.kts).
`leaf-server` compile reached 28 errors, now reduced. Fixed so far:
- BlockGetter → reverted to Leaf (defer Sakura #0026 forEachBlockIntersectedBetween mechanicsTarget version-margin).
- HopperBlockEntity → reverted to Leaf (defer #0025; Sakura ChangeListener/SlotCountingItemList leaked via clean-merge).
- ChunkEntitySlices → reverted to Leaf (defer #0014 getEntitiesLimited; dup `len` var).

## REMAINING COMPILE ERRORS (run `./gradlew :leaf-server:compileJava`)
**Group A — unmerged api + paper patches (the big one):**
- Sakura's **8 api patches** (`sakura-api/.../paper-patches/`) modify Paper-API `Configurations` base (adds `getWorldConfigurationCurrentVersion()` etc.) → `SakuraConfigurations` won't compile without them. Merge into leaf-api work-repo (`paper-api/.git`).
- Sakura's **16 paper-patches** (`sakura-server/paper-patches/`) — merge against paper-server files (leaf-server paper-patches channel).
- A Sakura **NonNullList** source patch (exposes `defaultValue` protected) needed by `SlotCountingItemList` — check if NonNullList was in our 97; if not, merge it.
- "cannot find symbol" in Sakura command/config classes (DebugLocalConfiguration, MechanicCommand, VisualCommand, TntExplosion, etc.) — likely resolved once api/paper patches land.

**Group B — Entity.checkInsideBlocks consistency (4 errors, lines 2095-2127):**
- Entity's CALLERS use Sakura's 6-arg `checkInsideBlocks(...,ChunkAccess[])` (came via clean-merge) but I kept Leaf's 5-arg def (h6/h7/h8 ours). FIX: re-resolve Entity h6/h7/h8 to THEIRS (Sakura's checkInsideBlocks) to match callers — needs chunk.locX/locZ (Moonrise), getChunkIfLoadedImmediately. (forEachBlockIntersectedBetween at 2127 also expects Sakura sig — but BlockGetter reverted to Leaf, so fix that caller to Leaf's sig too.)

**Group E — distanceToSqr override (2 errors):** FallingBlockEntity:134 / PrimedTnt:97 override `distanceToSqr(Vec3)` incompatibly with Entity. Check Sakura's Entity.distanceToSqr change vs the subclass overrides; align.

## THEN
`./gradlew rebuildPatches` → `createMojmapPaperclipJar` → `runPaperclip` smoke test (/tnttoggle, /sandtoggle, cannon physics, entity merging).

## Tooling recap (.fuji/)
merge3.sh (3-way driver, LF-merge/CRLF-write), resolve.py, all97.txt. Restore any file: `git checkout <leafBaseCommit> -- <path>` in work-repo (leafBase = the commit before 797b2bb).

## OLD remaining list (now resolved) — kept for reference
Run `grep -rl '<<<<<<<' leaf-server/src/minecraft/java --include='*.java'` for live list.
- **THE KNOT — Sakura "state watchers" (#0001) vs Leaf Lithium tracking** (incompatible Map↔array rewrites of same fields):
  - `EntityEquipment.java` (4 hunks) — Leaf `ItemStack[]` array+Lithium vs Sakura `Map`+watchers
  - `InsideBlockEffectApplier.java` (4) — Leaf ordinal arrays vs Sakura Map + `unappliedEffects` flag
  - `ItemStack.java` h1 — Leaf Lithium enchantment subscriber vs Sakura `watchers.changed` in `set()`
  - (interface decls on Container/ItemStack already merged to implement BOTH Lithium + Watchable)
  - watchers used by Sakura #0001/#0002(client-visibility)/#0008(cannon-merge)/#0009(explosion-density) — foundational.
- **Other tractable hunks:**
  - `Entity.java` (10) — biggest, not yet inspected
  - `ServerExplosion.java` (2) — explosion internals (Leaf old-blast-protection vs Sakura specialised/createBlockCache)
  - `HopperBlockEntity.java` (4), `BaseSpawner.java` (4)
  - `Level.java` h1 (hopper-ticking isBlockEntityActive vs SparklyPaper removal)
  - `MinecraftServer.java` (1, levelTickScheduler.tick at tick loop end)
  - `ServerLevel.java` h1 (super(...) constructor — both modified)
  - `BlockGetter.java` h0 (Sakura mechanicVersion gating vs Leaf already-ported iterator)
  - `LivingEntity.java` h1 (pushable retrieval: Leaf OnlyPlayerPushable vs Sakura maxEntityCollision limit)
  - `CactusBlock.java` (1, Pluto chunk-lookup opt vs Sakura random-chance growth)

## Next steps after conflicts cleared
1. `cd leaf-server/src/minecraft/java && git add . && git commit -m "Sakura merge"` then `cd ../../.. && ./gradlew rebuildPatches`
2. Also merge: Sakura 16 paper-patches + 8 api patches (not started).
3. `./gradlew createMojmapPaperclipJar` → fix compile errors → boot test.
