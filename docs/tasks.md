# Tasks

## Raytrace Entity Tracker Stabilization

---

### TDD-001 — Replace block-change async firehose with dirty flag
**Tag:** TDD
**References:** REQ-001, `CullTask.java:onBlockChange`, `Level.java` patch

Replace `CompletableFuture.runAsync()` in `CullTask.onBlockChange()` with an `AtomicBoolean dirty` flag set per-nearby-player. The existing `ScheduledExecutorService` tick checks `dirty.compareAndSet(true, false)` to trigger cache reset.

**Evidence:**

---

### TDD-002 — AtomicReference for CullTask thread safety
**Tag:** TDD
**References:** REQ-002, `Player.java` patch line 112, `CullTask.java`, patch 0293

Replace plain `cullTask` field in `Player.java` patch with `AtomicReference<CullTask>`. Verify that `Entity.isCulled(player)` reads through `AtomicReference.get()` and handles null correctly.

**Evidence:**

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
