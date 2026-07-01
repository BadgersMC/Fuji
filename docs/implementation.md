# Implementation Plan

## Layer Dependency Rules

Fuji is a Minecraft server fork — not a layered application. The following conventions apply:

- **Fork Java sources** (`leaf-server/src/main/java/`) may reference any Minecraft server class
- **MC patches** (`minecraft-patches/features/`) inject code directly into Minecraft server classes
- **External dependencies** (EntityCulling, OcclusionCulling) live as fork Java sources

No traditional domain/application/infrastructure layers exist. The "domain" IS the Minecraft server.

## Forbidden Domain Annotations

```yaml
forbidden: []
```

No annotation restrictions apply to this project (it is not a layered Spring/DI application).

## Codebase Structure

```
leaf-server/
  src/main/java/
    dev/tr7zw/entityculling/          # EntityCulling (custom license)
    com/logisticscraft/occlusionculling/  # OcclusionCulling (MIT)
    org/dreeam/leaf/config/           # Fork configuration
  minecraft-patches/
    features/
      0350-Raytrace-Entity-Tracker.patch  # MC source modifications
```

## Patch System

Patches are applied in numeric order by Paperweight patcher. Each patch modifies decompiled Minecraft server classes. Patch changes are validated by `applyAllPatches` — blob hash mismatches require `rebuildPatches` workflow.

## Key MC Classes Touched

| Class | Patch changes |
|-------|--------------|
| `ChunkMap.java` | Add `!entity.isCulled(player)` to tracking range check |
| `Entity.java` | Add `Cullable` interface, `lastTime` field, culling methods |
| `EntityType.java` | Add `skipRaytraceCheck` field |
| `Player.java` | Add `cullTask` field, CullTask lifecycle |
| `Level.java` | Add block change notification hook |

## Stabilization Architecture

### Block-change processing (REQ-001)
Replace `CompletableFuture.runAsync()` firehose with `AtomicBoolean dirty` flag + periodic reset in existing timer tick. Zero async tasks, O(players) lock-free CAS.

### Thread safety (REQ-002)
`Player.cullTask` → `AtomicReference<CullTask>`. Immutable reference after construction, safe cross-thread read.

### Visibility model (REQ-003, REQ-004)
Fix inverted ray-AABB test. Re-enable short-circuit `isForcedVisible()` by removing `|| true`.

### Error handling (REQ-007)
Replace `catch(Throwable)` + `printStackTrace()` with `Logger.warn()` + rate-limited guard (at most 1 per exception type per 5s).

### Modern Java upgrades (architectural)
- `record Vec3d(double x, y, z)` — immutable vectors
- `sealed interface EntityTrait` — compile-time exhaustiveness for entity filtering
- `ConcurrentHashMap` for `OcclusionCache` — safe MT reads
