# Tasks

## Raytrace Entity Tracker Stabilization

---

### TDD-001 — Replace block-change async firehose with dirty flag [x]
**Tag:** TDD
**References:** REQ-001, `CullTask.java:onBlockChange`, `Level.java` patch

Replace `CompletableFuture.runAsync()` in `CullTask.onBlockChange()` with an `AtomicBoolean dirty` flag set per-nearby-player. The existing `ScheduledExecutorService` tick checks `dirty.compareAndSet(true, false)` to trigger cache reset.

**Evidence:**
- `leaf-server/src/main/java/dev/tr7zw/entityculling/CullTask.java:152-179` — `onBlockChange()` spawns unbounded `CompletableFuture.runAsync()`
- `leaf-server/src/main/java/dev/tr7zw/entityculling/CullTask.java:35-37` — instance fields for existing timer tick
- `leaf-server/minecraft-patches/features/0350-Raytrace-Entity-Tracker.patch:150-181` — `Level.setBlock` hook
- `java/util/concurrent/atomic/AtomicBoolean` — JDK 21 stdlib (CAS operations)
- **Completed:** `onBlockChange()` replaced `CompletableFuture.runAsync()` with `AtomicBoolean.dirty.set(true)`. Periodic tick in `run()` checks `dirty.compareAndSet(true, false)` to coalesce bursts. Zero async tasks, O(players) lock-free. Design verified by `DirtyFlagProof.java`.

---

### TDD-002 — AtomicReference for CullTask thread safety [x]
**Tag:** TDD
**References:** REQ-002, `Player.java` patch line 112, `CullTask.java`, patch 0293

Replace plain `cullTask` field in `Player.java` patch with `AtomicReference<CullTask>`. Verify that `Entity.isCulled(player)` reads through `AtomicReference.get()` and handles null correctly.

**Evidence:**
- `leaf-server/minecraft-patches/features/0350-Raytrace-Entity-Tracker.patch:112` — `public CullTask cullTask = null;` plain field
- `leaf-server/minecraft-patches/features/0350-Raytrace-Entity-Tracker.patch:125-132` — CullTask lifecycle (creation, setup, removal)
- `leaf-server/src/main/java/dev/tr7zw/entityculling/versionless/access/Cullable.java` — `isCulled(Player)` reads `player.cullTask`
- `leaf-server/minecraft-patches/features/0350-Raytrace-Entity-Tracker.patch:74,84` — `Entity.java` `isCulled()` and `setCulled()` methods
- `leaf-server/minecraft-patches/features/0293` — multithreaded tracker (ChunkMap tracking runs on separate thread)
- `java/util/concurrent/atomic/AtomicReference` — JDK 21 stdlib
- **Completed:** `Player.cullTask` changed from plain field to `AtomicReference<CullTask>` in patch 0350. All accesses updated to `.get()` / `.set()`: Entity.setCulled, Entity.isCulled, Player tick lifecycle, Player.remove, CullTask.onBlockChange. Visibility guaranteed for MT tracker (patch 0293).

---

### TDD-003 — Fix isForcedVisible dead code
**Tag:** TDD
**References:** REQ-003, `CullTask.java:115`, `Entity.java` patch

Remove `|| true` debug remnant from `isForcedVisible()` condition. Verify the grace period prevents flickering at visibility boundaries.

**Evidence:**

---

### TDD-004 — Fix inverted ray-AABB intersection
**Tag:** TDD
**References:** REQ-004, `OcclusionCullingInstance.java:234-254`

Fix inverted condition: change `if (tmax > 0) return false` to `if (tmax < 0) return false`. Verify ray marches skip hit blocks correctly.

**Evidence:**

---

### TDD-005 — Config value bounds validation
**Tag:** TDD
**References:** REQ-005, `RaytraceTracker.java`

Add bounds checks to all numeric config fields: `traceInterval >= 0`, `maxTraceDistance > 0`, `boundingBoxLimit >= 0`, `boundingBoxExpansion >= 0`. Clamp out-of-range values, log a warning.

**Evidence:**

---

### TDD-006 — Correct armor-stand marker check
**Tag:** TDD
**References:** REQ-006, `CullTask.java:196-198`

Replace `entity.isInvisible()` with `entity instanceof ArmorStand as && as.isMarker()` in `isSkippableArmorstand()`. Rename config key to match behavior if needed.

**Evidence:**

---

### TDD-007 — Rate-limited error logging
**Tag:** TDD
**References:** REQ-007, `OcclusionCullingInstance.java:143-147`

Replace `catch(Throwable) { t.printStackTrace(); }` with `catch(Exception e) { Logger.warn(...) }` using a rate-limited guard (e.g., `RateLimiter` or last-logged map with 5s cooldown).

**Evidence:**

---

### DOC-008 — Include EntityCulling license
**Tag:** DOC
**References:** REQ-008, tr7zw/EntityCulling

Download or document the EntityCulling "Custom License" text. Add to `leaf-server/` or root as `LICENSE-EntityCulling.txt`.

**Evidence:**

---

### TDD-009 — Immutable Vec3d record
**Tag:** TDD
**References:** `Vec3d.java`, `OcclusionCullingInstance.java`, implementation.md

Replace mutable `Vec3d` class with `record Vec3d(double x, double y, double z)`. Update all call sites. Make `OcclusionCullingInstance.isAABBVisible()` accept parameters instead of mutating instance fields.

**Evidence:**

---

### TDD-010 — Regenerate patch and build
**Tag:** INFRA
**References:** All REQs, paper-fork-patch-development skill

Run `applyAllPatches` with corrected sources, then `rebuildPatches` to regenerate 0350 with valid blob hashes. Run `jar` to produce build artifact. Verify Sakura cannoning patches (0342-0346) still apply cleanly.

**Evidence:**

---
