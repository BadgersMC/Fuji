package com.logisticscraft.occlusionculling.util;

/**
 * Immutable 3D double-precision vector — replaced mutable class with Java record.
 *
 * All operations return new instances; no mutation.
 * Java records auto-generate equals(), hashCode(), toString(), and accessor methods.
 */
public record Vec3d(double x, double y, double z) {

    public Vec3d add(double dx, double dy, double dz) {
        return new Vec3d(x + dx, y + dy, z + dz);
    }

    public Vec3d div(Vec3d rayDir) {
        return new Vec3d(x / rayDir.x, y / rayDir.y, z / rayDir.z);
    }

    public Vec3d normalize() {
        double mag = Math.sqrt(x * x + y * y + z * z);
        return new Vec3d(x / mag, y / mag, z / mag);
    }
}
