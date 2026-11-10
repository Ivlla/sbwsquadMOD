package tech.squadmc.squadmcor.repair;

import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import tech.squadmc.squadmcor.entity.VehicleWreckEntity;
import tech.squadmc.squadmcor.heli.entity.vehicle.engine.EngineControl;
import tech.squadmc.squadmcor.squadmc;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Set;

/** Swaps this mod's destroyed vehicles for wrecks; helicopters first fall to the ground under SBW. */
@Mod.EventBusSubscriber(modid = squadmc.MODID)
public final class VehicleWreckConverter {
    private static final Map<ServerLevel, Set<VehicleEntity>> VEHICLES = new HashMap<>();

    private VehicleWreckConverter() {
    }

    @SubscribeEvent
    public static void onJoin(EntityJoinLevelEvent event) {
        if (event.getLevel() instanceof ServerLevel level && event.getEntity() instanceof VehicleEntity vehicle && isOurs(vehicle)) {
            VEHICLES.computeIfAbsent(level, ignored -> new LinkedHashSet<>()).add(vehicle);
        }
    }

    @SubscribeEvent
    public static void onLeave(EntityLeaveLevelEvent event) {
        if (event.getLevel() instanceof ServerLevel level) {
            Set<VehicleEntity> vehicles = VEHICLES.get(level);
            if (vehicles != null) vehicles.remove(event.getEntity());
        }
    }

    @SubscribeEvent
    public static void onUnload(LevelEvent.Unload event) { VEHICLES.remove(event.getLevel()); }

    @SubscribeEvent
    public static void onStopped(ServerStoppedEvent event) { VEHICLES.clear(); }

    @SubscribeEvent
    public static void onLevelTick(TickEvent.LevelTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !(event.level instanceof ServerLevel level)) return;
        Set<VehicleEntity> vehicles = VEHICLES.get(level);
        if (vehicles == null || vehicles.isEmpty()) return;
        List<VehicleEntity> destroyed = new ArrayList<>();
        vehicles.removeIf(entity -> entity.isRemoved() || !entity.isAddedToWorld());
        for (VehicleEntity vehicle : vehicles) {
            if (vehicle.isWreck() && hasLanded(vehicle)) {
                destroyed.add(vehicle);
            }
        }
        destroyed.forEach(vehicle -> VehicleWreckEntity.replace(level, vehicle));
    }

    private static boolean isOurs(VehicleEntity vehicle) {
        return squadmc.MODID.equals(EntityType.getKey(vehicle.getType()).getNamespace());
    }

    private static boolean hasLanded(VehicleEntity vehicle) {
        return !EngineControl.isHelicopter(vehicle) || vehicle.onGround() || vehicle.isInWater();
    }
}
