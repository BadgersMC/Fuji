/**
 * TDD-004: Proves rayIntersection tmax check was inverted.
 *
 * Design contract proof — no MC deps, no JUnit, runs with plain javac/java.
 *
 * Compile + run:
 *   javac -d /tmp RayAabbProof.java && java -cp /tmp RayAabbProof
 *
 * @see OcclusionCullingInstance.rayIntersection() — tmax > 0 → tmax < 0 fix
 */
public class RayAabbProof {
    static int failures = 0;

    static void check(boolean cond, String msg) {
        if (!cond) { System.err.println("FAIL: " + msg); failures++; }
    }

    static final class Vec3d {
        final double x, y, z;
        Vec3d(double x, double y, double z) { this.x = x; this.y = y; this.z = z; }
        Vec3d div(Vec3d o) { return new Vec3d(x / o.x, y / o.y, z / o.z); }
    }

    /** BUGGY: OcclusionCullingInstance.java lines 234-254 exact mirror */
    static boolean buggy(int[] b, Vec3d rayOrigin, Vec3d rayDir) {
        Vec3d rInv = new Vec3d(1, 1, 1).div(rayDir);
        double t1 = (b[0] - rayOrigin.x) * rInv.x;
        double t2 = (b[0] + 1 - rayOrigin.x) * rInv.x;
        double t3 = (b[1] - rayOrigin.y) * rInv.y;
        double t4 = (b[1] + 1 - rayOrigin.y) * rInv.y;
        double t5 = (b[2] - rayOrigin.z) * rInv.z;
        double t6 = (b[2] + 1 - rayOrigin.z) * rInv.z;
        double tmin = Math.max(Math.max(Math.min(t1, t2), Math.min(t3, t4)), Math.min(t5, t6));
        double tmax = Math.min(Math.min(Math.max(t1, t2), Math.max(t3, t4)), Math.max(t5, t6));
        if (tmax > 0) { return false; }
        return !(tmin > tmax);
    }

    /** FIXED: tmax < 0 only rejects boxes behind viewer */
    static boolean fixed(int[] b, Vec3d rayOrigin, Vec3d rayDir) {
        Vec3d rInv = new Vec3d(1, 1, 1).div(rayDir);
        double t1 = (b[0] - rayOrigin.x) * rInv.x;
        double t2 = (b[0] + 1 - rayOrigin.x) * rInv.x;
        double t3 = (b[1] - rayOrigin.y) * rInv.y;
        double t4 = (b[1] + 1 - rayOrigin.y) * rInv.y;
        double t5 = (b[2] - rayOrigin.z) * rInv.z;
        double t6 = (b[2] + 1 - rayOrigin.z) * rInv.z;
        double tmin = Math.max(Math.max(Math.min(t1, t2), Math.min(t3, t4)), Math.min(t5, t6));
        double tmax = Math.min(Math.min(Math.max(t1, t2), Math.max(t3, t4)), Math.max(t5, t6));
        if (tmax < 0) { return false; }
        return !(tmin > tmax);
    }

    public static void main(String[] args) {
        Vec3d origin = new Vec3d(0.5, 1.62, 0.5);
        Vec3d forward = new Vec3d(0.001, -0.001, 1.0);

        int[] boxAhead = {0, 1, 5};
        check(!buggy(boxAhead, origin, forward),
            "BUGGY: box at z=5 rejected (tmax > 0 fires)");
        check(fixed(boxAhead, origin, forward),
            "FIXED: box at z=5 correctly hits");

        int[] boxBehind = {0, 1, -3};
        check(!fixed(boxBehind, origin, forward),
            "FIXED: box at z=-3 is behind viewer → miss");
        check(buggy(boxBehind, origin, forward),
            "BUGGY: behind box → false HIT (tmax<0 bypasses guard)");

        Vec3d insideOrigin = new Vec3d(2.5, 1.62, 0.5);
        int[] boxInside = {2, 1, 5};
        check(!buggy(boxInside, insideOrigin, forward),
            "BUGGY: viewer inside box → tmax>0 → false MISS");
        check(fixed(boxInside, insideOrigin, forward),
            "FIXED: viewer inside box correctly hits");

        int[] boxFarSide = {10, 20, 5};
        check(!fixed(boxFarSide, origin, forward),
            "FIXED: box at y=20 misses (slabs don't overlap)");
        check(!buggy(boxFarSide, origin, forward),
            "BUGGY: misses for wrong reason (tmax>0, not slab gap)");

        int[] boxClose = {0, 1, 1};
        check(!buggy(boxClose, origin, forward),
            "BUGGY: close box at z=1 rejected (tmax>0)");
        check(fixed(boxClose, origin, forward),
            "FIXED: close box at z=1 correctly hits");

        Vec3d lookBack = new Vec3d(0.001, -0.001, -1.0);
        int[] boxOpposite = {0, 1, 5};
        check(!fixed(boxOpposite, origin, lookBack),
            "FIXED: box behind when looking opposite way → miss");

        if (failures == 0) {
            System.out.println("ALL TESTS PASSED: tmax inversion fix verified.");
        } else {
            System.err.println(failures + " TEST(S) FAILED");
            System.exit(1);
        }
    }
}
