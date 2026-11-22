package tech.squadmc.squadmcor.explosion;

import com.atsuishio.superbwarfare.config.server.ProjectileConfig;
import com.atsuishio.superbwarfare.entity.projectile.FastThrowableProjectile;
import com.atsuishio.superbwarfare.world.saveddata.ProjectileChunkSavedData;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Mod.EventBusSubscriber(modid = "squadmc")
public final class ProjectileChunkRecovery {
    private static final class Flights {
        final Set<FastThrowableProjectile> shots = new HashSet<>();
        long tick = Long.MIN_VALUE;
        int launches;
    }

    private static final Map<ServerLevel, Flights> SHOTS = new HashMap<>();

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void join(EntityJoinLevelEvent event) {
        if (event.getLevel() instanceof ServerLevel level
                && event.getEntity() instanceof FastThrowableProjectile shot && shot.forceLoadChunk()) {
            var flights = SHOTS.computeIfAbsent(level, ignored -> new Flights());
            if (flights.shots.contains(shot)) return;
            long now = level.getGameTime();
            if (flights.tick != now) {
                flights.tick = now;
                flights.launches = 0;
            }
            var limits = TerrainRules.flightLimits;
            if (flights.shots.size() >= limits.active()
                    || (!event.loadedFromDisk() && flights.launches >= limits.perTick())) {
                event.setCanceled(true);
                shot.discard();
                return;
            }
            flights.shots.add(shot);
            if (!event.loadedFromDisk()) flights.launches++;
        }
    }

    @SubscribeEvent
    public static void leave(EntityLeaveLevelEvent event) {
        if (event.getLevel() instanceof ServerLevel level && event.getEntity() instanceof FastThrowableProjectile shot) {
            var flights = SHOTS.get(level);
            if (flights != null) flights.shots.remove(shot);
        }
    }

    @SubscribeEvent
    public static void unload(LevelEvent.Unload event) {
        if (event.getLevel() instanceof ServerLevel level) SHOTS.remove(level);
    }

    @SubscribeEvent
    public static void tick(TickEvent.LevelTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !(event.level instanceof ServerLevel level)
                || level.getGameTime() % 20 != 0 || !ProjectileConfig.PROJECTILE_CHUNK_LOADING.get()) return;
        var flights = SHOTS.get(level);
        if (flights == null) return;
        // A suspended entity cannot refresh its own ticket. Retry from the level tick.
        for (var shot : flights.shots) {
            if (!shot.isRemoved() && shot.getY() <= level.getMaxBuildHeight()
                    && !level.isPositionEntityTicking(shot.blockPosition())) {
                ProjectileChunkSavedData.queueForceLoad(level, shot.chunkPosition());
            }
        }
    }
}
