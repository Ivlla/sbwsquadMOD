package tech.squadmc.squadmcor.repair;

import com.atsuishio.superbwarfare.data.vehicle.subdata.OBBInfo;
import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import com.google.gson.JsonObject;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import tech.squadmc.squadmcor.squadmc;

/**
 * Vehicles that cannot float take damage while fully under water. A vehicle can float when its
 * data gives it buoyancy ("Buoyancy" in EngineInfo).
 */
@Mod.EventBusSubscriber(modid = squadmc.MODID)
public final class VehicleWaterDamage {

    private static final int INTERVAL_TICKS = 20;
    /** Share of maximum health lost per interval, so any vehicle drowns in 20 seconds. */
    private static final float DAMAGE_FRACTION = 0.05F;

    private VehicleWaterDamage() {
    }

    @SubscribeEvent
    public static void onLevelTick(TickEvent.LevelTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !(event.level instanceof ServerLevel level)) return;
        if (level.getGameTime() % INTERVAL_TICKS != 0) return;
        for (Entity entity : level.getAllEntities()) {
            if (!(entity instanceof VehicleEntity vehicle) || vehicle.isWreck()) continue;
            if (canFloat(vehicle) || !isFullySubmerged(level, vehicle)) continue;
            vehicle.setHealth(vehicle.getHealth() - vehicle.getMaxHealth() * DAMAGE_FRACTION);
        }
    }

    private static boolean canFloat(VehicleEntity vehicle) {
        JsonObject engine = vehicle.computed().getEngineInfo();
        return engine != null && engine.has("Buoyancy") && engine.get("Buoyancy").getAsDouble() > 0;
    }

    private static boolean isFullySubmerged(ServerLevel level, VehicleEntity vehicle) {
        double top = vehicle.getBoundingBox().maxY;
        for (OBBInfo obb : vehicle.computed().getObb()) {
            top = Math.max(top, vehicle.getY() + obb.getPosition().y + obb.getSize().y);
        }
        BlockPos pos = BlockPos.containing(vehicle.getX(), top, vehicle.getZ());
        FluidState fluid = level.getFluidState(pos);
        return fluid.is(FluidTags.WATER) && top <= pos.getY() + fluid.getHeight(level, pos);
    }
}
