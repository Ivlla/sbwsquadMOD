package tech.squadmc.squadmcor.performance;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

/** Server-thread-only, bounded FIFO of shared, temporary chunk tickets. No Minecraft dependencies. */
public final class ChunkLeaseBudget<K> {
    private static final class Lease {
        long touched;
        boolean active;
        Lease(long now) { touched = now; }
    }

    private final Map<K, Lease> leases = new LinkedHashMap<>();

    public void request(K key, long now, boolean alreadyTicking, int maxEntries) {
        Lease lease = leases.get(key);
        if (lease != null) {
            lease.touched = now;
        } else if (!alreadyTicking && leases.size() < maxEntries) {
            leases.put(key, new Lease(now));
        }
    }

    public void advance(long now, int ttl, int maxActive, int maxStarts,
                        Predicate<K> ready, Consumer<K> acquire, Consumer<K> release) {
        int active = 0;
        Iterator<Map.Entry<K, Lease>> iterator = leases.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<K, Lease> entry = iterator.next();
            if (now - entry.getValue().touched >= ttl) {
                if (entry.getValue().active) release.accept(entry.getKey());
                iterator.remove();
            } else if (entry.getValue().active) active++;
        }
        iterator = leases.entrySet().iterator();
        int started = 0;
        while (iterator.hasNext()) {
            Map.Entry<K, Lease> entry = iterator.next();
            Lease lease = entry.getValue();
            if (lease.active) continue;
            // A player may have loaded a queued chunk in the meantime.
            if (ready.test(entry.getKey())) {
                iterator.remove();
            } else if (active < maxActive && started < maxStarts) {
                acquire.accept(entry.getKey());
                lease.active = true;
                active++;
                started++;
            }
        }
    }

    public void removeIf(Predicate<K> predicate, Consumer<K> release) {
        Iterator<Map.Entry<K, Lease>> iterator = leases.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<K, Lease> entry = iterator.next();
            if (predicate.test(entry.getKey())) {
                if (entry.getValue().active) release.accept(entry.getKey());
                iterator.remove();
            }
        }
    }

    public int size() { return leases.size(); }
    public int activeCount() {
        int count = 0;
        for (Lease lease : leases.values()) if (lease.active) count++;
        return count;
    }
}
