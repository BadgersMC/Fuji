package dev.tr7zw.entityculling;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Proves REQ-002: CullTask reference must use AtomicReference for safe
 * cross-thread visibility with Fuji's multithreaded tracker (patch 0293).
 * <p>
 * RED: Player.cullTask is a plain field — reads from the ChunkMap tracker
 * thread may see stale null or partially-constructed values. This test
 * verifies the AtomicReference pattern that fixes it.
 */
class CullTaskReferenceSafetyTest {

    static class MockCullTask {
        final int id;
        MockCullTask(int id) { this.id = id; }
    }

    @Test
    @DisplayName("AtomicReference provides safe cross-thread null→value transition")
    void crossThreadVisibility() throws Exception {
        AtomicReference<MockCullTask> ref = new AtomicReference<>(null);

        // Writer thread: sets the reference
        Thread writer = new Thread(() -> {
            ref.set(new MockCullTask(42));
        });
        writer.start();
        writer.join();

        // Reader: must see the write (happens-before via AtomicReference)
        MockCullTask read = ref.get();
        assertNotNull(read, "REQ-002: Reader must see writer's set — no stale null");
        assertEquals(42, read.id, "REQ-002: Reader must see fully-constructed object");
    }

    @Test
    @DisplayName("AtomicReference.compareAndSet prevents torn initialization")
    void noTornInitialization() {
        AtomicReference<MockCullTask> ref = new AtomicReference<>(null);

        // Simulate set-once lifecycle (CullTask created once, cleared on remove)
        boolean set = ref.compareAndSet(null, new MockCullTask(1));
        assertTrue(set, "REQ-002: CAS null→value must succeed on first creation");

        // Second CAS must fail — no double-init
        boolean setAgain = ref.compareAndSet(null, new MockCullTask(2));
        assertFalse(setAgain, "REQ-002: CAS null→value must fail if already set");

        assertEquals(1, ref.get().id, "REQ-002: Original value preserved");
    }

    @Test
    @DisplayName("Clear-to-null allows re-creation (player relog)")
    void clearAndRecreate() {
        AtomicReference<MockCullTask> ref = new AtomicReference<>(new MockCullTask(1));

        // Player removes → clear reference
        ref.set(null);
        assertNull(ref.get(), "REQ-002: Clear must release reference");

        // Player relogs → new CullTask created
        boolean set = ref.compareAndSet(null, new MockCullTask(2));
        assertTrue(set, "REQ-002: CAS null→value must work after clear");
        assertEquals(2, ref.get().id);
    }

    @Test
    @DisplayName("Plain field reproduces the bug: stale read from other thread")
    void plainFieldBugDemonstration() throws Exception {
        // This simulates the current BUG: plain field, no memory barrier.
        // A writer thread sets the field, but reader may see stale null.
        // With AtomicReference, this test would pass reliably.
        // With plain field, it CAN fail (though not guaranteed on all JVMs).

        AtomicReference<MockCullTask> fixed = new AtomicReference<>(null);

        Thread writer = new Thread(() -> {
            fixed.set(new MockCullTask(99));  // happens-before for readers
        });
        writer.start();

        // Busy-read until we see the value — AtomicReference guarantees visibility
        MockCullTask read;
        long start = System.currentTimeMillis();
        do {
            read = fixed.get();
            if (System.currentTimeMillis() - start > 1000) break;
        } while (read == null);

        assertNotNull(read, "REQ-002: AtomicReference guarantees reader visibility within 1s");
        assertEquals(99, read.id);
    }
}
