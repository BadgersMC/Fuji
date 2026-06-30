/**
 * TDD-005: Proves RaytraceTracker config fields need bounds validation.
 *
 * Design contract proof — no MC deps, no JUnit, runs with plain javac/java.
 *
 * Compile + run:
 *   javac -d /tmp ConfigBoundsProof.java && java -cp /tmp ConfigBoundsProof
 *
 * @see RaytraceTracker.onLoaded() — bounds checks for traceInterval,
 *      maxTraceDistance, boundingBoxLimit, boundingBoxExpansion
 */
public class ConfigBoundsProof {
    static int failures = 0;
    static final StringBuilder log = new StringBuilder();

    static void check(boolean cond, String msg) {
        if (!cond) { System.err.println("FAIL: " + msg); failures++; }
    }

    static void warn(String msg) {
        log.append("WARN: ").append(msg).append("\n");
    }

    static class UnvalidatedConfig {
        int traceInterval, maxTraceDistance, boundingBoxLimit;
        double boundingBoxExpansion;

        void load(int ti, int mtd, int bbl, double bbe) {
            this.traceInterval = ti;
            this.maxTraceDistance = mtd;
            this.boundingBoxLimit = bbl;
            this.boundingBoxExpansion = bbe;
        }
    }

    static class ValidatedConfig {
        int traceInterval, maxTraceDistance, boundingBoxLimit;
        double boundingBoxExpansion;

        void load(int ti, int mtd, int bbl, double bbe) {
            if (ti < 0) { warn("traceInterval " + ti + " < 0, clamping to 0"); this.traceInterval = 0; }
            else { this.traceInterval = ti; }
            if (mtd <= 0) { warn("maxTraceDistance " + mtd + " <= 0, clamping to 1"); this.maxTraceDistance = 1; }
            else { this.maxTraceDistance = mtd; }
            if (bbl < 0) { warn("boundingBoxLimit " + bbl + " < 0, clamping to 0"); this.boundingBoxLimit = 0; }
            else { this.boundingBoxLimit = bbl; }
            if (bbe < 0) { warn("boundingBoxExpansion " + bbe + " < 0, clamping to 0"); this.boundingBoxExpansion = 0; }
            else { this.boundingBoxExpansion = bbe; }
        }
    }

    public static void main(String[] args) {
        UnvalidatedConfig bad = new UnvalidatedConfig();
        bad.load(-50, 0, -10, -1.5);
        check(bad.traceInterval == -50,
            "BUG: traceInterval=-50 passed through");
        check(bad.maxTraceDistance == 0,
            "BUG: maxTraceDistance=0 passed through");
        check(bad.boundingBoxLimit == -10,
            "BUG: boundingBoxLimit=-10 passed through");
        check(bad.boundingBoxExpansion == -1.5,
            "BUG: boundingBoxExpansion=-1.5 passed through");

        log.setLength(0);
        ValidatedConfig good = new ValidatedConfig();
        good.load(-50, 0, -10, -1.5);
        check(good.traceInterval == 0, "FIX: traceInterval clamped to 0");
        check(good.maxTraceDistance == 1, "FIX: maxTraceDistance clamped to 1");
        check(good.boundingBoxLimit == 0, "FIX: boundingBoxLimit clamped to 0");
        check(good.boundingBoxExpansion == 0.0, "FIX: boundingBoxExpansion clamped to 0.0");
        check(log.toString().contains("traceInterval"), "FIX: warning logged");
        check(log.toString().contains("maxTraceDistance"), "FIX: warning logged");

        log.setLength(0);
        ValidatedConfig normal = new ValidatedConfig();
        normal.load(50, 64, 20, 0.5);
        check(normal.traceInterval == 50, "Normal traceInterval=50 preserved");
        check(normal.maxTraceDistance == 64, "Normal maxTraceDistance=64 preserved");
        check(normal.boundingBoxLimit == 20, "Normal boundingBoxLimit=20 preserved");
        check(normal.boundingBoxExpansion == 0.5, "Normal boundingBoxExpansion=0.5 preserved");
        check(log.toString().isEmpty(), "No warnings for in-range values");

        log.setLength(0);
        ValidatedConfig edge = new ValidatedConfig();
        edge.load(0, 1, 0, 0.0);
        check(edge.traceInterval == 0, "Edge: traceInterval=0 preserved");
        check(edge.maxTraceDistance == 1, "Edge: maxTraceDistance=1 preserved");
        check(edge.boundingBoxLimit == 0, "Edge: boundingBoxLimit=0 preserved");
        check(edge.boundingBoxExpansion == 0.0, "Edge: boundingBoxExpansion=0.0 preserved");
        check(log.toString().isEmpty(), "No warnings for edge values");

        if (failures == 0) {
            System.out.println("ALL TESTS PASSED: config bounds validation design verified.");
        } else {
            System.err.println(failures + " TEST(S) FAILED");
            System.exit(1);
        }
    }
}
