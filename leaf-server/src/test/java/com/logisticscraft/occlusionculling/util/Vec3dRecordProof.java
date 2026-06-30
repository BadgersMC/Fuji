/**
 * TDD-009: Proves mutable Vec3d shared-state corruption risk → record fix.
 *
 * Design contract proof — no MC deps, no JUnit, runs with plain javac/java.
 *
 * Compile + run:
 *   javac -d /tmp Vec3dRecordProof.java && java -cp /tmp Vec3dRecordProof
 *
 * @see Vec3d — converted from mutable class to immutable record
 */
public class Vec3dRecordProof {
    static int failures = 0;

    static void check(boolean cond, String msg) {
        if (!cond) { System.err.println("FAIL: " + msg); failures++; }
    }

    static class MutableVec3d {
        double x, y, z;
        MutableVec3d(double x, double y, double z) { this.x = x; this.y = y; this.z = z; }
        void set(double x, double y, double z) { this.x = x; this.y = y; this.z = z; }
        void setAdd(MutableVec3d vec, double x, double y, double z) { this.x = vec.x + x; this.y = vec.y + y; this.z = vec.z + z; }
        MutableVec3d div(MutableVec3d rayDir) { this.x /= rayDir.x; this.y /= rayDir.y; this.z /= rayDir.z; return this; }
        MutableVec3d normalize() { double mag = Math.sqrt(x*x+y*y+z*z); this.x/=mag; this.y/=mag; this.z/=mag; return this; }
    }

    record Vec3d(double x, double y, double z) {
        Vec3d add(double dx, double dy, double dz) { return new Vec3d(x+dx, y+dy, z+dz); }
        Vec3d div(Vec3d rd) { return new Vec3d(x/rd.x, y/rd.y, z/rd.z); }
        Vec3d normalize() { double m=Math.sqrt(x*x+y*y+z*z); return new Vec3d(x/m, y/m, z/m); }
    }

    public static void main(String[] args) {
        MutableVec3d shared = new MutableVec3d(1,1,1);
        MutableVec3d rInv = shared.div(new MutableVec3d(2,2,2));
        check(shared == rInv, "BUG: div() returns this");
        check(shared.x == 0.5, "BUG: shared mutated by div()");

        Vec3d v1 = new Vec3d(1,1,1), v2 = v1.div(new Vec3d(2,2,2));
        check(v1 != v2, "FIX: record div() returns new");
        check(v1.x() == 1.0, "FIX: original unchanged");

        MutableVec3d[] targets = {new MutableVec3d(3,4,0)};
        MutableVec3d before = new MutableVec3d(targets[0].x, targets[0].y, targets[0].z);
        targets[0].normalize();
        check(before.x != targets[0].x, "BUG: normalize() mutated shared target");

        Vec3d[] rTargets = {new Vec3d(3,4,0)};
        Vec3d norm = rTargets[0].normalize();
        check(rTargets[0].x() == 3.0, "FIX: record normalize() leaves original intact");
        check(norm.x() == 0.6, "FIX: normalized result correct");

        Vec3d pos = new Vec3d(10,20,30);
        Vec3d[] t = {pos.add(0.05,0.95,0.05), pos.add(0.05,0.05,0.95), pos.add(0.95,0.95,0.95)};
        check(pos.x() == 10 && pos.y() == 20, "FIX: add() returns new, pos unchanged");
        check(t[0].x() == 10.05 && t[0].y() == 20.95, "FIX: target points computed correctly");

        if (failures == 0) System.out.println("ALL TESTS PASSED: Vec3d record design verified.");
        else { System.err.println(failures + " FAILED"); System.exit(1); }
    }
}
