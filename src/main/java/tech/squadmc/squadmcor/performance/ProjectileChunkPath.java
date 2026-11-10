package tech.squadmc.squadmcor.performance;

import java.util.LinkedHashSet;
import java.util.Set;

/** Supercover traversal: includes crossed chunks, corner neighbours and a one-chunk collision halo. */
public final class ProjectileChunkPath {
    private ProjectileChunkPath() {}

    public static Set<Long> plan(double x, double z, double endX, double endZ, int limit) {
        Set<Long> result = new LinkedHashSet<>();
        if (!valid(x) || !valid(z) || !valid(endX) || !valid(endZ)) return result;
        int cx = (int) Math.floor(x / 16.0), cz = (int) Math.floor(z / 16.0);
        int tx = (int) Math.floor(endX / 16.0), tz = (int) Math.floor(endZ / 16.0);
        double dx = endX - x, dz = endZ - z;
        int sx = dx > 0 ? 1 : dx < 0 ? -1 : 0, sz = dz > 0 ? 1 : dz < 0 ? -1 : 0;
        double stepX = sx == 0 ? Double.POSITIVE_INFINITY : 16.0 / Math.abs(dx);
        double stepZ = sz == 0 ? Double.POSITIVE_INFINITY : 16.0 / Math.abs(dz);
        double nextX = sx == 0 ? Double.POSITIVE_INFINITY : ((cx + (sx > 0 ? 1 : 0)) * 16.0 - x) / dx;
        double nextZ = sz == 0 ? Double.POSITIVE_INFINITY : ((cz + (sz > 0 ? 1 : 0)) * 16.0 - z) / dz;
        for (int traversed = 0; traversed <= limit; traversed++) {
            for (int ox = -1; ox <= 1; ox++) {
                for (int oz = -1; oz <= 1; oz++) {
                    result.add(pack(cx + ox, cz + oz));
                    if (result.size() > limit) return Set.of();
                }
            }
            if (cx == tx && cz == tz) return result;
            if (nextX < nextZ) { cx += sx; nextX += stepX; }
            else if (nextZ < nextX) { cz += sz; nextZ += stepZ; }
            else { cx += sx; cz += sz; nextX += stepX; nextZ += stepZ; }
        }
        return Set.of(); // Never silently truncate a fast projectile's collision path.
    }

    private static boolean valid(double value) { return Double.isFinite(value) && Math.abs(value) <= 30_000_000; }
    public static long pack(int x, int z) { return (x & 0xffffffffL) | ((z & 0xffffffffL) << 32); }
}
