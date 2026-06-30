/**
 * TDD-003: Proves isForcedVisible() grace period works after removing || true.
 *
 * Design contract proof — no MC deps, no JUnit, runs with plain javac/java.
 *
 * Compile + run:
 *   javac -d /tmp IsForcedVisibleProof.java && java -cp /tmp IsForcedVisibleProof
 *
 * @see CullTask.cullEntities() — the `|| true` debug remnant fix
 * @see Entity.isForcedVisible() — 1-second grace period via lastTime
 */
public class IsForcedVisibleProof {
    static int failures = 0;

    static void check(boolean cond, String msg) {
        if (!cond) {
            System.err.println("FAIL: " + msg);
            failures++;
        }
    }

    static class GracePeriodTracker {
        private long lastTime = 0;

        void setTimeout() {
            this.lastTime = System.currentTimeMillis() + 1000;
        }

        boolean isForcedVisible() {
            return this.lastTime > System.currentTimeMillis();
        }
    }

    static boolean buggyCondition(GracePeriodTracker tracker) {
        return !tracker.isForcedVisible() || true;
    }

    static boolean correctCondition(GracePeriodTracker tracker) {
        return !tracker.isForcedVisible();
    }

    public static void main(String[] args) throws Exception {
        GracePeriodTracker tracker = new GracePeriodTracker();

        // Test 1: Buggy condition is ALWAYS true
        check(buggyCondition(tracker),
            "Buggy: no setTimeout, condition must be true (|| true always wins)");

        tracker.setTimeout();
        Thread.sleep(1100);
        check(!tracker.isForcedVisible(),
            "Grace period expired after 1.1s");
        check(buggyCondition(tracker),
            "Buggy: after grace expiry, condition STILL true (|| true) — BUG CONFIRMED");

        // Test 2: Correct condition respects grace period
        GracePeriodTracker t2 = new GracePeriodTracker();
        check(correctCondition(t2),
            "Correct: no grace → enters visibility check block");
        t2.setTimeout();
        check(t2.isForcedVisible(),
            "After setTimeout: isForcedVisible=true within 1s");
        check(!correctCondition(t2),
            "Correct: during grace → SKIPS visibility block (prevents flickering)");
        Thread.sleep(1100);
        check(!t2.isForcedVisible(), "After 1.1s: isForcedVisible=false");
        check(correctCondition(t2),
            "Correct: after grace expiry → enters block again");

        // Test 3: Grace period prevents flickering
        GracePeriodTracker t3 = new GracePeriodTracker();
        t3.setTimeout();
        Thread.sleep(100);
        check(t3.isForcedVisible(), "Within grace (100ms): isForcedVisible=true");
        check(!correctCondition(t3),
            "Correct: within grace → skip culling, no flickering");
        check(buggyCondition(t3),
            "Buggy: within grace → enters block anyway (|| true) — flickering bug");

        // Test 4: Multiple setTimeouts coalesce
        GracePeriodTracker t4 = new GracePeriodTracker();
        t4.setTimeout();
        Thread.sleep(500);
        t4.setTimeout();
        Thread.sleep(600);
        check(t4.isForcedVisible(), "Extended grace still active");
        check(!correctCondition(t4), "Correct: extended grace → still skip");

        if (failures == 0) {
            System.out.println("ALL TESTS PASSED: isForcedVisible dead code fix verified.");
        } else {
            System.err.println(failures + " TEST(S) FAILED");
            System.exit(1);
        }
    }
}
