package tech.squadmc.squadmcor.repair;

import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import tech.squadmc.squadmcor.entity.VehicleWreckEntity;
import tech.squadmc.squadmcor.heli.entity.vehicle.engine.EngineControl;
import tech.squadmc.squadmcor.squadmc;

import java.util.ArrayList;
import java.util.List;

/** Swaps this mod's destroyed vehicles for wrecks; helicopters first fall to the ground under SBW. */
@Mod.EventBusSubscriber(modid = squadmc.MODID)
public final class VehicleWreckConverter {

    private VehicleWreckConverter() {
    }

    @SubscribeEvent
    public static void onLevelTick(TickEvent.LevelTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !(event.level instanceof ServerLevel level)) return;
        List<VehicleEntity> destroyed = new ArrayList<>();
        for (Entity entity : level.getAllEntities()) {
            if (entity instanceof VehicleEntity vehicle && vehicle.isWreck() && isOurs(vehicle) && hasLanded(vehicle)) {
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
