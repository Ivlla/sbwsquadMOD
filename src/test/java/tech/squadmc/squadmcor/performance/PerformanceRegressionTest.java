package tech.squadmc.squadmcor.performance;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

/** Standalone Java 17 tests; does not require Minecraft, a running server, or local mod JARs. */
public final class PerformanceRegressionTest {
    private static int checks;

    public static void main(String[] args) {
        paths();
        budgets();
        System.out.println("PASS: " + checks + " checks (paths, shared tickets, limits, expiry, FIFO and unload)");
    }

    private static void check(boolean condition, String message) {
        checks++;
        if (!condition) throw new AssertionError(message);
    }

    private static void paths() {
        check(ProjectileChunkPath.plan(1, 1, 1, 1, 128).size() == 9, "stationary halo");
        check(ProjectileChunkPath.plan(-0.0, -0.0, 0, 0, 128).size() == 9, "signed zero");
        check(ProjectileChunkPath.plan(Double.NaN, 0, 1, 1, 128).isEmpty(), "NaN rejected");
        check(ProjectileChunkPath.plan(0, 0, Double.POSITIVE_INFINITY, 1, 128).isEmpty(), "infinity rejected");
        check(ProjectileChunkPath.plan(0, 0, 1_000_000, 0, 128).isEmpty(), "overlong path is rejected, not truncated");
        Set<Long> fast = ProjectileChunkPath.plan(-50, 0, 150, 0, 128);
        for (int x = -4; x <= 9; x++) check(fast.contains(ProjectileChunkPath.pack(x, 0)), "intermediate fast chunk " + x);
        Random random = new Random(746352);
        for (int run = 0; run < 500; run++) {
            double x = random.nextDouble() * 1000 - 500, z = random.nextDouble() * 1000 - 500;
            double ex = x + random.nextDouble() * 300 - 150, ez = z + random.nextDouble() * 300 - 150;
            Set<Long> path = ProjectileChunkPath.plan(x, z, ex, ez, 256);
            check(!path.isEmpty(), "bounded route exists");
            check(path.equals(ProjectileChunkPath.plan(ex, ez, x, z, 256)), "route direction symmetry");
            // Independent dense sampling oracle: every point's collision halo must be covered.
            for (int step = 0; step <= 500; step++) {
                int cx = (int) Math.floor((x + (ex - x) * step / 500) / 16);
                int cz = (int) Math.floor((z + (ez - z) * step / 500) / 16);
                for (int ox = -1; ox <= 1; ox++) for (int oz = -1; oz <= 1; oz++)
                    check(path.contains(ProjectileChunkPath.pack(cx + ox, cz + oz)), "sampled halo covered");
            }
        }
    }

    private static void budgets() {
        ChunkLeaseBudget<String> book = new ChunkLeaseBudget<>();
        List<String> added = new ArrayList<>(), removed = new ArrayList<>();
        for (int shot = 0; shot < 10000; shot++) book.request("overworld:0", 0, false, 8);
        check(book.size() == 1, "10,000 projectiles share one request");
        book.request("player-loaded", 0, true, 8);
        check(book.size() == 1, "no ticket for naturally ticking terrain");
        for (int i = 1; i < 12; i++) book.request("overworld:" + i, 0, false, 8);
        check(book.size() == 8, "bounded queue including active entries");
        book.advance(1, 40, 3, 2, key -> false, added::add, removed::add);
        check(added.size() == 2 && book.activeCount() == 2, "new request rate limit");
        book.advance(2, 40, 3, 2, key -> false, added::add, removed::add);
        check(added.size() == 3 && book.activeCount() == 3, "active limit");
        check(added.equals(List.of("overworld:0", "overworld:1", "overworld:2")), "FIFO grants");
        book.request("overworld:0", 39, true, 8);
        book.advance(40, 40, 3, 2, key -> false, added::add, removed::add);
        check(book.size() == 1 && book.activeCount() == 1, "unused pending and active entries expire");
        check(removed.equals(List.of("overworld:1", "overworld:2")), "only owned active tickets released");
        book.advance(78, 40, 3, 2, key -> false, added::add, removed::add);
        check(book.size() == 1, "shared refresh retains ticket");
        book.advance(79, 40, 3, 2, key -> false, added::add, removed::add);
        check(book.size() == 0 && removed.size() == 3, "last user expiry releases ticket");

        book.request("overworld:1", 80, false, 8);
        book.request("nether:1", 80, false, 8);
        book.advance(81, 40, 3, 2, key -> key.startsWith("overworld"), added::add, removed::add);
        check(book.size() == 1 && book.activeCount() == 1, "queued chunk loaded by player needs no ticket");
        book.removeIf(key -> key.startsWith("overworld"), removed::add);
        check(book.size() == 1, "dimension isolation");
        book.removeIf(key -> key.startsWith("nether"), removed::add);
        check(book.size() == 0 && removed.get(removed.size() - 1).equals("nether:1"), "unload cleanup");
    }
}
