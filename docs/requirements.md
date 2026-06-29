# Fuji — Requirements

## Raytrace Entity Tracker Stabilization

### REQ-001 — Bounded block-change processing
**Unwanted.** IF the raytrace entity tracker is enabled AND block state changes occur in loaded chunks THEN THE SYSTEM SHALL process block-change notifications in O(players) time per tick without spawning unbounded asynchronous tasks.

### REQ-002 — Thread-safe CullTask access
**State-driven.** WHILE the multithreaded entity tracker runs THE SYSTEM SHALL provide safe cross-thread visibility of the CullTask reference via volatile or AtomicReference semantics.

### REQ-003 — Forced-visible grace period
**Event-driven.** WHEN an entity transitions from culled to visible THE SYSTEM SHALL prevent re-culling for a configurable grace period.

### REQ-004 — Corrected ray-AABB intersection
**Ubiquitous.** THE SYSTEM SHALL correctly determine whether a ray intersects an axis-aligned bounding box, returning true when the far intersection distance is positive and the near distance does not exceed the far distance.

### REQ-005 — Config value bounds
**Event-driven.** WHEN the raytrace-entity-tracker configuration section is loaded THE SYSTEM SHALL validate and clamp all numeric parameters to defined safe ranges, logging a warning when clamping occurs.

### REQ-006 — Correct armor-stand skipping
**Event-driven.** WHEN the skip-marker-armor-stand configuration option is enabled THE SYSTEM SHALL skip only armor stands where ArmorStand.isMarker() returns true.

### REQ-007 — Rate-limited error logging
**Unwanted.** IF an exception occurs during a visibility check THEN THE SYSTEM SHALL emit a rate-limited log message through the standard logging framework.

### REQ-008 — License compliance
**Ubiquitous.** THE SYSTEM SHALL include the tr7zw/EntityCulling custom license text in the distributed binary.
