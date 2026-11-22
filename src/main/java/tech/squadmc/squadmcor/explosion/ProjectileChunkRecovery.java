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
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Mod.EventBusSubscriber(modid = "squadmc")
public final class ProjectileChunkRecovery {
    private static final Map<ServerLevel, Set<FastThrowableProjectile>> SHOTS = new HashMap<>();

    @SubscribeEvent
    public static void join(EntityJoinLevelEvent event) {
        if (event.getLevel() instanceof ServerLevel level
                && event.getEntity() instanceof FastThrowableProjectile shot && shot.forceLoadChunk()) {
            SHOTS.computeIfAbsent(level, ignored -> new HashSet<>()).add(shot);
        }
    }

    @SubscribeEvent
    public static void leave(EntityLeaveLevelEvent event) {
        if (event.getLevel() instanceof ServerLevel level && event.getEntity() instanceof FastThrowableProjectile shot) {
            var shots = SHOTS.get(level);
            if (shots != null) shots.remove(shot);
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
        var shots = SHOTS.get(level);
        if (shots == null) return;
        // A suspended entity cannot refresh its own ticket. Retry from the level tick.
        for (var shot : shots) {
            if (!shot.isRemoved() && shot.getY() <= level.getMaxBuildHeight()
                    && !level.isPositionEntityTicking(shot.blockPosition())) {
                ProjectileChunkSavedData.queueForceLoad(level, shot.chunkPosition());
            }
        }
    }
}
