package tech.squadmc.squadmcor.performance;

import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.TicketType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import tech.squadmc.squadmcor.squadmc;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.LinkedHashSet;
import java.util.ArrayList;

/** No synchronous getChunk/join calls, persistent forced chunks, or per-projectile tickets. */
@Mod.EventBusSubscriber(modid = squadmc.MODID)
public final class ProjectileChunkLoading {
    private static final TicketType<ChunkPos> TICKET = TicketType.create("squadmc_projectile",
            Comparator.comparingLong(ChunkPos::toLong));
    // DistanceManager uses level 33 - radius. 31 keeps entities ticking in the centre chunk.
    private static final int TICKET_RADIUS = 2;
    private static final String WAIT_STARTED = "SquadmcChunkWaitStarted";
    private static final ChunkLeaseBudget<Key> BUDGET = new ChunkLeaseBudget<>();
    private static final Map<Key, Boolean> READY_THIS_TICK = new HashMap<>();
    private static final Set<Entity> PROJECTILES = new LinkedHashSet<>();
    private static MinecraftServer server;
    private static long clock;
    private static long lastWarning = -1200;

    private record Key(ResourceKey<Level> dimension, long chunk) {}
    private ProjectileChunkLoading() {}

    public static boolean enabled() { return PerformanceConfig.PROJECTILE_CHUNKS.get(); }

    private static boolean supported(Entity entity) {
        if (!(entity instanceof Projectile)) return false;
        String namespace = EntityType.getKey(entity.getType()).getNamespace();
        return namespace.equals("superbwarfare") || namespace.equals(squadmc.MODID);
    }

    @SubscribeEvent
    public static void onJoin(EntityJoinLevelEvent event) {
        if (event.getLevel() instanceof ServerLevel level && supported(event.getEntity())) {
            server = level.getServer();
            event.getEntity().getPersistentData().remove(WAIT_STARTED);
            PROJECTILES.add(event.getEntity());
        }
    }

    @SubscribeEvent
    public static void onLeave(EntityLeaveLevelEvent event) { PROJECTILES.remove(event.getEntity()); }

    public static boolean beforeTick(Entity entity) {
        if (!supported(entity) || !(entity.level() instanceof ServerLevel level) || !enabled()) return true;
        server = level.getServer();
        Vec3 motion = entity.getDeltaMovement();
        Set<Long> required = ProjectileChunkPath.plan(entity.getX(), entity.getZ(),
                entity.getX() + motion.x, entity.getZ() + motion.z, PerformanceConfig.PATH_LIMIT.get());
        boolean ready = !required.isEmpty();
        for (long chunk : required) {
            Key key = new Key(level.dimension(), chunk);
            boolean ticking = isReady(key);
            BUDGET.request(key, clock, ticking, PerformanceConfig.MAX_TICKETS.get() * 2);
            ready &= ticking;
        }
        // Best-effort look-ahead. Its readiness must not delay an already safe current step.
        if (ready) {
            for (long chunk : ProjectileChunkPath.plan(entity.getX() + motion.x, entity.getZ() + motion.z,
                    entity.getX() + motion.x * 2, entity.getZ() + motion.z * 2, PerformanceConfig.PATH_LIMIT.get())) {
                if (required.contains(chunk)) continue;
                Key key = new Key(level.dimension(), chunk);
                BUDGET.request(key, clock, isReady(key), PerformanceConfig.MAX_TICKETS.get() * 2);
            }
            entity.getPersistentData().remove(WAIT_STARTED);
            return true;
        }
        // The full entity tick is gated, so velocity, fuse, damage and trajectory are not partially advanced.
        var data = entity.getPersistentData();
        if (!data.contains(WAIT_STARTED) || data.getLong(WAIT_STARTED) > clock) data.putLong(WAIT_STARTED, clock);
        if (clock - data.getLong(WAIT_STARTED) >= PerformanceConfig.WAIT_TICKS.get()) {
            entity.discard();
            if (clock - lastWarning >= 1200) {
                LogUtils.getLogger().warn("Projectile chunk loading timed out: {} in {}. Tickets: {}, queued/active: {}. "
                                + "Pre-generate the combat area or review squadmc-performance-server.toml limits.",
                        EntityType.getKey(entity.getType()), level.dimension().location(), BUDGET.activeCount(), BUDGET.size());
                lastWarning = clock;
            }
        }
        return false;
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.START) return;
        clock++;
        READY_THIS_TICK.clear();
        if (server == null) return;
        if (!enabled()) {
            BUDGET.removeIf(key -> true, ProjectileChunkLoading::release);
            return;
        }
        BUDGET.advance(clock, PerformanceConfig.RETAIN_TICKS.get(), PerformanceConfig.MAX_TICKETS.get(),
                PerformanceConfig.STARTS_PER_TICK.get(), ProjectileChunkLoading::isReady,
                ProjectileChunkLoading::acquire, ProjectileChunkLoading::release);
        // A player can leave after a projectile enters a naturally ticking chunk. Restore tickets
        // even if vanilla stopped ticking that entity; do not manually simulate it a second time.
        // Snapshot only stalled entities, because discard() posts a leave event that mutates the set.
        ArrayList<Entity> stalled = null;
        PROJECTILES.removeIf(entity -> entity.isRemoved() || !entity.isAddedToWorld());
        for (Entity entity : PROJECTILES) {
            if (!isReady(new Key(entity.level().dimension(), entity.chunkPosition().toLong()))) {
                if (stalled == null) stalled = new ArrayList<>();
                stalled.add(entity);
            }
        }
        if (stalled != null) for (Entity entity : stalled) beforeTick(entity);
    }

    private static boolean isReady(Key key) {
        Boolean cached = READY_THIS_TICK.get(key);
        if (cached != null) return cached;
        ServerLevel level = server.getLevel(key.dimension());
        ChunkPos pos = new ChunkPos(key.chunk());
        boolean ready = level != null && level.getChunkSource().getChunkNow(pos.x, pos.z) != null
                && level.isPositionEntityTicking(pos.getWorldPosition());
        if (READY_THIS_TICK.size() < 16384) READY_THIS_TICK.put(key, ready);
        return ready;
    }

    private static void acquire(Key key) {
        ServerLevel level = server.getLevel(key.dimension());
        if (level != null) {
            ChunkPos pos = new ChunkPos(key.chunk());
            level.getChunkSource().addRegionTicket(TICKET, pos, TICKET_RADIUS, pos);
        }
    }

    private static void release(Key key) {
        ServerLevel level = server.getLevel(key.dimension());
        if (level != null) {
            ChunkPos pos = new ChunkPos(key.chunk());
            level.getChunkSource().removeRegionTicket(TICKET, pos, TICKET_RADIUS, pos);
        }
    }

    @SubscribeEvent
    public static void onUnload(LevelEvent.Unload event) {
        if (event.getLevel() instanceof ServerLevel level && server != null) {
            READY_THIS_TICK.clear();
            PROJECTILES.removeIf(entity -> entity.level() == level);
            BUDGET.removeIf(key -> key.dimension().equals(level.dimension()), ProjectileChunkLoading::release);
        }
    }

    @SubscribeEvent
    public static void onStopped(ServerStoppedEvent event) {
        BUDGET.removeIf(key -> true, key -> {});
        READY_THIS_TICK.clear();
        PROJECTILES.clear();
        server = null;
        clock = 0;
        lastWarning = -1200;
    }
}
