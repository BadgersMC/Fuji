/**
 * TDD-006: Proves isSkippableArmorstand uses wrong guard (isInvisible, not isMarker).
 *
 * Design contract proof — no MC deps, no JUnit, runs with plain javac/java.
 *
 * Compile + run:
 *   javac -d /tmp ArmorStandCheckProof.java && java -cp /tmp ArmorStandCheckProof
 *
 * @see CullTask.isSkippableArmorstand() — isInvisible() → isMarker() fix
 */
public class ArmorStandCheckProof {
    static int failures = 0;

    static void check(boolean cond, String msg) {
        if (!cond) { System.err.println("FAIL: " + msg); failures++; }
    }

    static class ArmorStand {
        boolean marker, invisible;
        ArmorStand(boolean m, boolean i) { this.marker = m; this.invisible = i; }
        boolean isMarker() { return marker; }
        boolean isInvisible() { return invisible; }
    }

    static boolean buggy(Object e) {
        return e instanceof ArmorStand as && as.isInvisible();
    }

    static boolean fixed(Object e) {
        return e instanceof ArmorStand as && as.isMarker();
    }

    public static void main(String[] args) {
        check(buggy(new ArmorStand(false, true)),
            "BUGGY: invisible non-marker → wrongly skipped (isInvisible=true)");
        check(!fixed(new ArmorStand(false, true)),
            "FIXED: invisible non-marker → NOT skipped (isMarker=false)");

        check(!buggy(new ArmorStand(true, false)),
            "BUGGY: marker visible → NOT skipped (isInvisible=false)");
        check(fixed(new ArmorStand(true, false)),
            "FIXED: marker visible → correctly skipped (isMarker=true)");

        check(buggy(new ArmorStand(true, true)),
            "Both: marker+invisible → skipped (coincidental)");
        check(fixed(new ArmorStand(true, true)),
            "Both: marker+invisible → skipped");

        check(!buggy(new ArmorStand(false, false)),
            "Both: normal armor stand → NOT skipped");
        check(!fixed(new ArmorStand(false, false)),
            "Both: normal armor stand → NOT skipped");

        check(!buggy("not armorstand"), "Non-armorstand: buggy false");
        check(!fixed("not armorstand"), "Non-armorstand: fixed false");

        if (failures == 0) {
            System.out.println("ALL TESTS PASSED: armor stand marker check fix verified.");
        } else {
            System.err.println(failures + " TEST(S) FAILED");
            System.exit(1);
        }
    }
}
