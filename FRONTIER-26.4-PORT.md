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

## Known blockers before the first build

1. **Snapshot Mache dependency.** Fain's `FRONTIER-PORT.md` says that the fork references a locally published `io.papermc:mache:26.4-snapshot-8+build.local-SNAPSHOT` artifact. Fain has included its modified Mache sources in `frontier-mache/`, but clean-machine build/publish instructions and reproducibility are not yet verified. Resolve this first; do not assume standard Paper artifacts suffice.
2. **Minecraft and Paper patch-port incompatibility.** This branch deliberately retains Fuji's **26.3** Leaf/Sakura/Fuji patch files. They are a source inventory, **not** ported 26.4 patches. Do not assume `applyAllPatches` succeeds, and do not publish a JAR from this branch without complete patch reapplication and validation.
3. **API/behavior changes.** Fain's `snapshot-todos.txt` flags API/event conversion, command suggestions, brewing recipes, data-driven sound types, per-block biomes, and registry changes. Independently audit all Leaf, Sakura and Fuji patches touching those areas.
4. **No 26.4 release workflow.** Fuji's existing build/release workflow is limited to the `26.3` branch. This experimental branch intentionally does not enable automated server publication.

## Porting checklist

- [x] Create isolated experimental Fuji branch from merged `26.3`.
- [x] Pin Paperweight upstream to Fain's exact fork commit.
- [x] Record Snapshot 8 Mache and compatibility blockers.
- [ ] Establish reproducible local Mache artifact build and dependency resolution from the exact fork revision.
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
