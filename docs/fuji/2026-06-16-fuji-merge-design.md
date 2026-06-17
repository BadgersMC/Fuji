# Fuji — Leaf + Sakura Merge Design Spec

**Project name:** **Fuji** 🗻 (藤 = "wisteria" in Japanese, continuing the botanical theme alongside Sakura/cherry-blossom and Wisteria)
**Date:** 2026-06-16
**Goal:** Fold all of Sakura's patches into Leaf, with **Leaf as the permanent base**, producing a fork named **Fuji**. End state is Leaf carrying ~33 extra patches that originated from Sakura — no ongoing Sakura-upstream relationship.

---

## 1. Background & key facts

- **Leaf** (`Winds-Studio/Leaf`) and **Sakura** (`Samsuik/Sakura`) are **both direct Paper forks** using paperweight. Neither is built on Purpur. (The original "start from the Purpur/Tentacles template" premise is wrong and is dropped.)
- Both have matching **Minecraft 1.21.11** branches: Leaf `ver/1.21.11`, Sakura `1.21.11`. Target = **1.21.11**.
- Both use Paper's **modern file-patch layout**:
  - `<fork>-server/src/` — fork's own new Java classes (committed source, additive)
  - `<fork>-server/minecraft-patches/features/*.patch` — **feature patches = git commits** against vanilla MC source
  - `<fork>-server/paper-patches/` — patches against Paper-server source
  - mirrored for `-api`
- **Sakura inventory (1.21.11):** 33 `minecraft-patches/features` commits, 3 paper-patches, ~5 API patches, plus its own `src` feature classes.
- Leaf is a large fork (hundreds of patches); exact overlap is computed during triage.

## 2. Why prior attempts failed

Earlier attempts tried to `git apply` Sakura's `.patch` files directly onto Leaf. Those patches are line-anchored to **Paper's** source; Leaf has rewritten the same files, so anchors are gone → hunks reject → model hand-edits `.patch` headers → silent corruption.

**This design never hand-edits a `.patch` file.** Because Sakura's patches are *commits*, we replay them with git's **3-way merge** (`git am --3way`), resolve conflicts as normal `<<<<<<<` markers in **real Java**, then let Gradle **regenerate** the patch files (`rebuildPatches`).

## 3. Strategy: source-tree merge + rebuildPatches (Strategy A)

Leaf is the base. Sakura's changes become new commits on Leaf's materialized source tree, then are baked into Leaf's own patch set. Each conflict is resolved **once** and is permanently part of Leaf — no recurring maintenance.

## 4. Environment (Windows — mandatory prereqs)

Windows is the one env where paperweight bites (260-char `MAX_PATH`). Before any build:
- `git config --global core.longpaths true`
- Registry: `LongPathsEnabled = 1` (HKLM\SYSTEM\CurrentControlSet\Control\FileSystem)
- JDK 21 installed and on PATH
- Run all `./gradlew` commands from **Git Bash**
- Clone path kept short: `D:\BadgersMC-Dev\Fuji`

## 5. Repo layout

- Fresh clone of Leaf `ver/1.21.11` → `D:\BadgersMC-Dev\Fuji`, work on branch `fuji`.
- Materialized Sakura `1.21.11` checkout as donor (separate dir).
- Stale `D:\BadgersMC-Dev\Leaf` deleted.

## 6. Phase plan

### Phase 0 — Baseline gate
Build Leaf 1.21.11 **unmodified** on this machine: `applyAllPatches` → `createMojmapPaperclipJar`. Proves long-path/JDK setup and gives a known-good baseline. **Do not proceed until green.**

### Phase 1 — Triage (anti-spiral gate)
Materialize both source trees. For each of Sakura's 33 patches, list the MC/Paper files it touches and whether Leaf also touches that file. Produce a **bucket table**:
- **Bucket 1** — additive feature files (no overlap) → mechanical copy
- **Bucket 2** — patches on files Leaf does NOT touch → clean replay
- **Bucket 3** — patches on files Leaf ALSO rewrites → real 3-way merge

### Phase 2 — Mechanical port (buckets 1 & 2)
Copy Sakura feature classes into `leaf-server/src` + `leaf-api/src` (fix packages/imports, wire command registration). Replay non-overlapping commits.

### Phase 3 — Conflict reconciliation (bucket 3)
`git am --3way` each overlapping Sakura commit; resolve markers in Java.
**Predicted hot files:** explosions (#0006/0009/0010/0011), entity tracker (#0020), hopper ticking (#0025), redstone wires (#0028), chunk-on-movement (#0003), entity collision/retrieval (#0013/0014). ~10–15 patches expected here.

### Phase 4 — Regenerate & build
`rebuildPatches` writes merged result into `leaf-server/minecraft-patches/` as Leaf-owned patches. Build paperclip jar; iterate compile errors.

### Phase 5 — Runtime verify
Boot server; test Sakura commands (`/tnttoggle`, `/sandtoggle`, cannon physics, entity merging); confirm Leaf optimizations still active.

## 7. Conflict default

**Skip & log.** A truly-irreconcilable patch goes into `DEFERRED.md` and the campaign continues. One focused reimplementation pass over the skip list at the end. Full skip list reviewed before declaring done — nothing vanishes silently.

## 8. Config integration

Keep Sakura's config namespace **separate** from Leaf's config initially (avoids a whole conflict class). Optional later unification into one file.

## 9. Verification / success criteria

- Leaf 1.21.11 + all non-deferred Sakura patches builds a working paperclip jar.
- Server boots; Sakura commands and cannon mechanics function.
- Leaf's own optimizations remain active (no silent reverts).
- `DEFERRED.md` lists any skipped patches with reason.

## 10. Execution

Driven **interactively in-session**, phase by phase, with user review at each gate (Phase 0 build green, Phase 1 bucket table, Phase 3 conflict decisions, Phase 5 runtime).

## 11. Out of scope

- Maintaining a live Sakura-upstream sync.
- Porting to MC versions other than 1.21.11.
- Unifying config files (deferred, optional).
