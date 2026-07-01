/**
 * TDD-007: Proves error handling was unbounded printStackTrace spam.
 *
 * Design contract proof — no MC deps, no JUnit, runs with plain javac/java.
 *
 * Compile + run:
 *   javac -d /tmp ErrorLoggingProof.java && java -cp /tmp ErrorLoggingProof
 *
 * @see OcclusionCullingInstance.isAABBVisible() — Throwable → Exception, rate-limited logging
 */
public class ErrorLoggingProof {
    static int failures = 0;

    static void check(boolean cond, String msg) {
        if (!cond) { System.err.println("FAIL: " + msg); failures++; }
    }

    static final java.util.Map<String, Long> lastLogged = new java.util.HashMap<>();
    static final long COOLDOWN_MS = 5000;
    static final StringBuilder log = new StringBuilder();

    static void warnRateLimited(String key, String fmt, Object... args) {
        long now = System.currentTimeMillis();
        Long last = lastLogged.get(key);
        if (last != null && now - last < COOLDOWN_MS) return;
        lastLogged.put(key, now);
        log.append(String.format(fmt, args)).append("\n");
    }

    static class BuggyHandler {
        int stderrCount = 0;
        boolean runWithBuggy(Runnable body) {
            try { body.run(); return true; }
            catch (Throwable t) { t.printStackTrace(); stderrCount++; return false; }
        }
    }

    static class FixedHandler {
        boolean runWithFixed(Runnable body) {
            try { body.run(); return true; }
            catch (Exception e) {
                warnRateLimited(e.getClass().getSimpleName(),
                    "[OcclusionCulling] %s: %s",
                    e.getClass().getSimpleName(), e.getMessage());
                return false;
            }
        }
    }

    public static void main(String[] args) throws Exception {
        // Test 1: Rate limiting on rapid failures
        log.setLength(0);
        FixedHandler fh = new FixedHandler();
        RuntimeException ex = new RuntimeException("test");
        for (int i = 0; i < 100; i++) fh.runWithFixed(() -> { throw ex; });
        check(log.toString().lines().count() <= 2,
            "FIXED: 100 failures → rate-limited to ~1 log line");

        // Test 2: Cooldown allows re-log after delay
        Thread.sleep(5100);
        int before = (int) log.toString().lines().count();
        fh.runWithFixed(() -> { throw ex; });
        check(log.toString().lines().count() > before,
            "FIXED: after cooldown, new failure logged");

        // Test 3: Buggy catches OutOfMemoryError (shouldn't)
        BuggyHandler bh = new BuggyHandler();
        bh.runWithBuggy(() -> { throw new OutOfMemoryError("simulated"); });
        check(bh.stderrCount == 1,
            "BUGGY: catches OutOfMemoryError (should be uncatchable Error)");

        // Test 4: Different exception types independent
        lastLogged.clear(); log.setLength(0);
        FixedHandler fh2 = new FixedHandler();
        fh2.runWithFixed(() -> { throw new RuntimeException("a"); });
        fh2.runWithFixed(() -> { throw new IllegalStateException("b"); });
        check(log.toString().lines().count() == 2,
            "FIXED: different types → independent rate limits");

        // Test 5: Fixed does NOT catch Error
        boolean errorCaught = false;
        try { new FixedHandler().runWithFixed(() -> { throw new StackOverflowError("x"); }); }
        catch (StackOverflowError e) { errorCaught = true; }
        check(errorCaught,
            "FIXED: StackOverflowError passes through (Exception only)");

        if (failures == 0) {
            System.out.println("ALL TESTS PASSED: rate-limited error logging design verified.");
        } else {
            System.err.println(failures + " TEST(S) FAILED");
            System.exit(1);
        }
    }
}
