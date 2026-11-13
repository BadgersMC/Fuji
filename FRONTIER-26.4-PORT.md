# Fuji experimental port — Frontier Paper 26.4 Snapshot 8

> **Status: BASE WIRED, PATCH PORT NOT STARTED. Not a working or validated server build.**
>
> This branch exists to port Fuji 26.3 (Leaf + Sakura + Fuji) onto Fain's experimental Paper 26.4 fork without touching Fuji's 26.3 release line.

## Exact upstream

- Paper fork: https://github.com/FainNeito/Frontier-Paper-26.4
- Source branch observed: `dev/26.4`
- **Pinned fork commit:** `148a3f6c6cf45100f6379f73744e094a24c90fe8`
- Fork's underlying Paper commit before its Snapshot 8 capture: `806b0db07c2c625f80cf47a1f6cab4d1735d1b8e`
- Target Minecraft version: `26.4-snapshot-8` (experimental), API version: `26.4`
- JDK: **25**.

Fuji's `build.gradle.kts` now sets the Paperweight `upstreams.paper.repo` to Fain's fork; `gradle.properties` pins that fork commit. The existing `26.3` branch and its published builds are independent.

## Independent Fuji Mache

Fuji now maintains [BadgersMC/Fuji-Mache](https://github.com/BadgersMC/Fuji-Mache), pinned here to `8ad2b02dd5f0d4e698182383ef5f227e0f9747f2`. The 101 Mache patches and LGPL license were imported from Fain's Snapshot 8 source. Local Maven publication was successfully verified on November 12, 2026 and created the exact coordinate `io.papermc:mache:26.4-snapshot-8+build.local-SNAPSHOT`.

To reproduce that publication locally using Java 25, run the [installation helper](scripts/install-fuji-mache.ps1) from PowerShell. The script validates the pinned Mache commit before building. On Linux or CI, run `./gradlew ':versions:v26_4-snapshot-8:publishToMavenLocal'` in the pinned Mache repository.

Fuji-Mache's clean Ubuntu CI [run 43346381714](https://github.com/BadgersMC/Fuji-Mache/actions/runs/43346381714) **passed** at commit `8ad2b02dd5f0d4e698182383ef5f227e0f9747f2`: source setup, decompilation, Mache patch application, local Maven publication, version verification, and upload of the Maven artifact/checksum. This validates the independent **Paper build input**, not Fuji's Leaf/Sakura patch compatibility.

## Remaining blockers before the first Fuji build

1. **Minecraft and Paper patch-port incompatibility.** This branch deliberately retains Fuji's **26.3** Leaf/Sakura/Fuji patch files. They are a source inventory, **not** ported 26.4 patches. Do not assume `applyAllPatches` succeeds, and do not publish a JAR from this branch without complete patch reapplication and validation.
2. **API/behavior changes.** Fain's `snapshot-todos.txt` flags API/event conversion, command suggestions, brewing recipes, data-driven sound types, per-block biomes, and registry changes. Independently audit all Leaf, Sakura and Fuji patches touching those areas.
3. **No 26.4 release workflow.** Fuji's existing build/release workflow is limited to the `26.3` branch. This experimental branch intentionally does not enable automated server publication.
4. **First conflict resolved in patch workflow (2026-11-12).** Frontier Paper's `paper-server/build.gradle.kts` moved beyond the 26.3 base. Fuji's applied build script now retains Frontier's Snapshot 8 Mache coordinate and restores Leafclip, `leaf-api`, fork registration, and Leaf dependencies. `rebuildPaperSingleFilePatches` regenerated the patch; a subsequent `applyAllPatches` passed `:applyPaperSingleFilePatches` and the Paper API feature-patch stage. **This is not a complete port or compilation result.**

## Porting checklist

- [x] Create isolated experimental Fuji branch from merged `26.3`.
- [x] Pin Paperweight upstream to Fain's exact fork commit.
- [x] Record Snapshot 8 Mache and compatibility blockers.
- [x] Import and pin independent Fuji-Mache source; publish the exact Snapshot 8 Maven artifact locally.
- [x] Confirm clean-machine CI reproduces Mache publication and full source preparation.
- [ ] Rebase Leaf's 26.3 patch inventory onto the 26.4 fork source.
- [ ] Re-apply and adapt Sakura's cannon entity, explosion, redstone, per-region mechanics and configuration functionality.
- [ ] Re-apply Fuji's maintenance, cannon-height, legacy knockback, instant falling-block stacking, visibility/raytrace, and player-only 20 Hz tracker patches.
- [ ] Recheck packet formats, entity tracking, movement interpolation, and protocol compatibility with 26.4 clients.
- [ ] Perform owner-authorized compilation and test suite runs.
- [ ] Create a **separate** disposable 26.4 test server and run TNT cannon/performance regressions.
- [ ] Only then consider a manual experimental prerelease. No production promotion without explicit approval.

## Guardrails

- Never merge this branch into `26.3` automatically.
- Never overwrite existing Fuji or Frontier server binaries/worlds.
- Preserve original patch authors' licenses and source attribution.
- Follow `AGENTS.md`: owner controls compilation, tests, benchmarking and server startup; patch regeneration follows Paperweight applied-source commits.
- Branch creation and upstream pinning are scaffolding only, **not successful 26.4 compatibility verification**.

## One-week Frontier experiment: release gates

A temporary event can tolerate downtime, **not silent world loss or unintended access to live data**. Before player access:

- Use disposable worlds and separate plugin databases; never upgrade the current 26.3 production worlds in place. Keep offline pre-event backups and verify a restore to an isolated location.
- Require a successfully applied 26.4 patch series, owner-built server JAR, and smoke tests for player joins, chunk save/reload, inventories, guild claims, raid protections, TNT, sand/concrete stacking, redstone, and cross-version networking.
- Observe TPS/MSPT, CPU, memory, network bandwidth, entity counts, error logs, and disk writes under staged cannon loads before broadening access.
- Keep the tested 26.3 server binary untouched and a documented rollback decision. Do not point a 26.3 server at world data saved by 26.4 without a separately validated backward-compatibility path.

**No release or production merge is authorized by this document.**
