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
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

@Mod.EventBusSubscriber(modid = "squadmc")
public final class ProjectileChunkRecovery {
    // Insertion order forms a round-robin queue, including during sustained fire.
    private static final Map<ServerLevel, LinkedHashSet<FastThrowableProjectile>> SHOTS = new HashMap<>();

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void join(EntityJoinLevelEvent event) {
        if (event.getLevel() instanceof ServerLevel level
                && event.getEntity() instanceof FastThrowableProjectile shot && shot.forceLoadChunk()) {
            SHOTS.computeIfAbsent(level, ignored -> new LinkedHashSet<>()).add(shot);
        }
    }

    @SubscribeEvent
    public static void leave(EntityLeaveLevelEvent event) {
        if (event.getLevel() instanceof ServerLevel level && event.getEntity() instanceof FastThrowableProjectile shot) {
            var flights = SHOTS.get(level);
            if (flights != null) flights.remove(shot);
        }
    }

    @SubscribeEvent
    public static void unload(LevelEvent.Unload event) {
        if (event.getLevel() instanceof ServerLevel level) SHOTS.remove(level);
    }

    @SubscribeEvent
    public static void tick(TickEvent.LevelTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !(event.level instanceof ServerLevel level)
                || !ProjectileConfig.PROJECTILE_CHUNK_LOADING.get()) return;
        var flights = SHOTS.get(level);
        if (flights == null || flights.isEmpty()) return;

        int checks = Math.min(flights.size(), TerrainRules.recoveryChecksPerTick);
        Set<Long> checkedChunks = new HashSet<>();
        for (int i = 0; i < checks; i++) {
            var it = flights.iterator();
            var shot = it.next();
            it.remove();
            if (shot.isRemoved()) continue;
            flights.add(shot);
            if (shot.getY() > level.getMaxBuildHeight()) continue;
            var chunk = shot.chunkPosition();
            if (checkedChunks.add(chunk.toLong()) && !level.isPositionEntityTicking(shot.blockPosition())) {
                ProjectileChunkSavedData.queueForceLoad(level, chunk);
            }
        }
    }
}
