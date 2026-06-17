# Fuji Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax. NOTE: this is an upstream-merge campaign — "tests" are build-green and runtime-smoke checks, not unit tests. Conflict-resolution steps are procedures; the actual Java edits are discovered live.

**Goal:** Produce **Fuji** = Leaf `ver/1.21.11` with all ~33 Sakura `1.21.11` patches folded in as native Leaf patches, building a working paperclip jar.

**Architecture:** Strategy A — materialize Leaf's patched source, replay Sakura's commits onto it with git 3-way merge, resolve conflicts in real Java, regenerate patch files with `rebuildPatches`. Leaf is the permanent base; Sakura becomes ~33 Leaf-owned patches.

**Tech Stack:** Paper/paperweight, Gradle (Kotlin DSL), JDK 21, Git (Git Bash on Windows), MC 1.21.11.

---

## File / dir map

- `D:\BadgersMC-Dev\Fuji` — fresh Leaf clone, working branch `fuji`
- `D:\BadgersMC-Dev\Sakura-donor` — Sakura `1.21.11` checkout (donor source)
- `D:\BadgersMC-Dev\Fuji\DEFERRED.md` — running list of skipped/irreconcilable patches
- `D:\BadgersMC-Dev\Fuji\docs\fuji\` — design spec + this plan copied in after clone

---

## Phase 0 — Environment & baseline gate

### Task 0.1: Verify toolchain
- [ ] **Step 1:** Check JDK 21 + git present.
  Run (Git Bash): `java -version && git --version`
  Expected: java version 21.x; git present.
- [ ] **Step 2:** Enable git long paths.
  Run: `git config --global core.longpaths true`
- [ ] **Step 3:** Verify Windows registry long-paths flag.
  Run (PowerShell): `(Get-ItemProperty 'HKLM:\SYSTEM\CurrentControlSet\Control\FileSystem').LongPathsEnabled`
  Expected: `1`. If `0`, set it (admin): `Set-ItemProperty 'HKLM:\SYSTEM\CurrentControlSet\Control\FileSystem' LongPathsEnabled 1`.

### Task 0.2: Clean stale + clone fresh
- [ ] **Step 1:** Delete stale Leaf (destructive — confirm first).
  Run (PowerShell): `Remove-Item -Recurse -Force D:\BadgersMC-Dev\Leaf`
- [ ] **Step 2:** Clone Leaf 1.21.11 shallow-ish into Fuji.
  Run (Git Bash): `git clone --branch ver/1.21.11 https://github.com/Winds-Studio/Leaf.git /d/BadgersMC-Dev/Fuji`
- [ ] **Step 3:** Create working branch.
  Run: `cd /d/BadgersMC-Dev/Fuji && git checkout -b fuji`
- [ ] **Step 4:** Copy spec + plan into repo and commit.
  Copy `Fuji-plan\*` → `Fuji\docs\fuji\`; `git add docs/fuji && git commit -m "docs: Fuji design + plan"`

### Task 0.3: Baseline build (GATE)
- [ ] **Step 1:** Inspect available gradle tasks (confirm real task names).
  Run: `./gradlew tasks --all | grep -iE "patch|paperclip|jar"`
  Note actual names (e.g. `applyAllPatches`, `rebuildPatches`/`rebuildPatchesFilterFiles`, `createMojmapPaperclipJar`).
- [ ] **Step 2:** Apply all Leaf patches.
  Run: `./gradlew applyAllPatches`
  Expected: BUILD SUCCESSFUL; `leaf-server/src` + `leaf-api/src` populated.
- [ ] **Step 3:** Build the server jar.
  Run: `./gradlew createMojmapPaperclipJar` (or task discovered in Step 1)
  Expected: BUILD SUCCESSFUL; jar under `build/libs`.
- [ ] **Step 4 (GATE):** Do NOT proceed unless Steps 2–3 are green. If red, fix env/toolchain first.

---

## Phase 1 — Triage (anti-spiral gate)

### Task 1.1: Materialize Sakura donor
- [ ] **Step 1:** Clone Sakura donor.
  Run: `git clone --branch 1.21.11 https://github.com/Samsuik/Sakura.git /d/BadgersMC-Dev/Sakura-donor`
- [ ] **Step 2:** Apply Sakura patches to get its source + the patch commit series.
  Run: `cd /d/BadgersMC-Dev/Sakura-donor && ./gradlew applyAllPatches`

### Task 1.2: Inventory + bucket
- [ ] **Step 1:** List Sakura's 33 MC feature patches + 3 paper-patches + API patches (filenames already known from design §1).
- [ ] **Step 2:** For each patch, extract the set of target files (`grep '^+++ ' <patch>` or read the `diff --git` headers).
- [ ] **Step 3:** Build Leaf's touched-file set: list every file modified by Leaf patches (from `leaf-server/minecraft-patches` + materialized diff vs Paper).
- [ ] **Step 4:** Produce `docs/fuji/TRIAGE.md` with a table: patch # | target files | bucket (1 additive / 2 clean / 3 conflict).
- [ ] **Step 5 (GATE):** Present TRIAGE.md to user for review before any merging.
  Commit: `git add docs/fuji/TRIAGE.md && git commit -m "docs: Fuji triage bucket table"`

---

## Phase 2 — Mechanical port (buckets 1 & 2)

### Task 2.1: Additive feature classes (bucket 1)
- [ ] **Step 1:** Copy Sakura's own new classes from `Sakura-donor/sakura-server/src` → `Fuji/leaf-server/src` (and `-api`), preserving package paths. Only NEW files (not modifications to MC/Paper classes).
- [ ] **Step 2:** Fix package/import references and command/config registration hooks.
- [ ] **Step 3 (verify):** `./gradlew applyAllPatches` still applies; compile the server.
  Run: `./gradlew :leaf-server:compileJava` Expected: SUCCESSFUL (or list real errors to fix).
- [ ] **Step 4:** Commit. `git commit -am "feat(fuji): add Sakura feature classes"`

### Task 2.2: Clean-replay patches (bucket 2)
- [ ] **Step 1:** For each bucket-2 Sakura patch, apply its diff to the corresponding file in `leaf-server/src` (the materialized source). Since Leaf doesn't touch these files, it applies cleanly.
  Run per patch: `git apply --3way --directory=leaf-server/src/main/java <sakura-patch>` (path-adjusted) — OR hand-port the hunk.
- [ ] **Step 2 (verify):** `./gradlew :leaf-server:compileJava` Expected: SUCCESSFUL.
- [ ] **Step 3:** Commit per logical group. `git commit -am "feat(fuji): port Sakura bucket-2 patches"`

---

## Phase 3 — Conflict reconciliation (bucket 3)

> Process per overlapping patch. Predicted hot files: explosions (#0006/0009/0010/0011), entity tracker (#0020), hopper (#0025), redstone (#0028), chunk-on-movement (#0003), entity retrieval (#0013/0014).

### Task 3.N (one per bucket-3 patch):
- [ ] **Step 1:** Read the Sakura patch's intent (commit message + diff) and the current Leaf version of each target file.
- [ ] **Step 2:** Apply with 3-way merge so conflicts surface as markers.
  Run: `git apply --3way --directory=leaf-server/src/main/java <sakura-patch>`
  If it conflicts: resolve `<<<<<<</=======/>>>>>>>` markers in real Java, preserving BOTH Leaf's optimization and Sakura's behavior where compatible.
- [ ] **Step 3 (verify):** `./gradlew :leaf-server:compileJava` Expected: SUCCESSFUL.
- [ ] **Step 4 (decision gate):** If irreconcilable, revert this file, append patch # + reason to `DEFERRED.md`, continue. (Per design §7: skip & log.)
- [ ] **Step 5:** Commit. `git commit -am "feat(fuji): reconcile Sakura #NNNN <name>"`

### Task 3.api: API patches + paper-patches
- [ ] Same procedure for the 3 paper-patches and ~5 API patches against `leaf-api`/`paper-patches` targets.

---

## Phase 4 — Regenerate & build

### Task 4.1: Bake into Leaf patch set
- [ ] **Step 1:** Regenerate patch files from merged source.
  Run: `./gradlew rebuildPatches` (real name from Task 0.3 Step 1)
  Expected: new/updated `.patch` files under `leaf-server/minecraft-patches/`.
- [ ] **Step 2:** Verify clean round-trip: `./gradlew applyAllPatches` from a clean state re-applies the regenerated patches.
- [ ] **Step 3:** Commit. `git commit -am "feat(fuji): rebuild patches with Sakura merged"`

### Task 4.2: Full jar build (GATE)
- [ ] **Step 1:** `./gradlew createMojmapPaperclipJar` Expected: BUILD SUCCESSFUL.
- [ ] **Step 2:** If compile errors, fix in source, re-`rebuildPatches`, rebuild. Loop until green.

### Task 4.3 (optional): Rebrand to Fuji
- [ ] Retarget Leaf's `Rebrand` patch (server brand/version string) to "Fuji". Low priority, conflict-prone — do last.

---

## Phase 5 — Runtime verification

### Task 5.1: Boot + smoke test
- [ ] **Step 1:** Run the jar with a test world (`run.sh`/`run.bat` or `java -jar build/libs/<fuji>.jar nogui`).
  Expected: server reaches "Done" and accepts a connection.
- [ ] **Step 2:** Test Sakura features in-game: `/tnttoggle`, `/sandtoggle`, cannon physics, entity merging.
- [ ] **Step 3:** Spot-check Leaf optimizations still active (config flags present, no startup errors reverting them).
- [ ] **Step 4:** Review `DEFERRED.md` with user. Decide reimplement-pass vs accept skips.

---

## Done criteria
- `createMojmapPaperclipJar` green with all non-deferred Sakura patches.
- Server boots; Sakura commands work; Leaf opts intact.
- `DEFERRED.md` reviewed; no silent drops.
