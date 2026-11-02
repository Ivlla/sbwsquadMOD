package tech.squadmc.squadmcor.sound;

import com.atsuishio.superbwarfare.api.event.ProjectileHitEvent;
import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import tech.squadmc.squadmcor.fx.MtsFxServer;
import tech.squadmc.squadmcor.squadmc;

/** Ловит попадание снаряда SBW в технику. Шина Forge, сервер. */
@Mod.EventBusSubscriber(modid = squadmc.MODID)
public class VehicleHitEvents {

    // LOWEST + receiveCanceled=false: если кто-то отменил попадание (щит и т.п.) — звука нет
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onHitEntity(ProjectileHitEvent.HitEntity event) {
        if (!(event.getTarget() instanceof VehicleEntity vehicle)) return;
        VehicleHitSounds.onVehicleHit(vehicle, event.getProjectile());
        if (vehicle.level() instanceof ServerLevel level) {
            MtsFxServer.vehicleHit(level, event.getHitVec(), event.getProjectile(), VehicleHitSounds.classify(event.getProjectile()));
        }
    }
}
