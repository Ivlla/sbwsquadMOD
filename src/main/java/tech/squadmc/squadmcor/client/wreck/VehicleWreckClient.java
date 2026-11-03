package tech.squadmc.squadmcor.client.wreck;

import net.minecraft.client.Minecraft;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;
import tech.squadmc.squadmcor.entity.VehicleWreckEntity;
import tech.squadmc.squadmcor.fx.MtsFxClient;
import tech.squadmc.squadmcor.fx.MtsFxPreset;

/** Burns a wreck with MTS's totaled-vehicle fire and smoke, then lets it smoulder and go out. */
public final class VehicleWreckClient {

    private static final double EFFECT_RANGE_SQR = 256 * 256;
    private static final int SMOKE_INTERVAL_BURNING = 2;
    private static final int SMOKE_INTERVAL_SMOULDERING = 6;
    private static final double HULL_SPREAD = 0.35;

    private VehicleWreckClient() {
    }

    public static void tick(VehicleWreckEntity wreck) {
        int age = wreck.age();
        if (age >= VehicleWreckEntity.SMOKE_TICKS) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.player.distanceToSqr(wreck) > EFFECT_RANGE_SQR) return;

        boolean burning = age < VehicleWreckEntity.FIRE_TICKS;
        if (burning) MtsFxClient.play(MtsFxPreset.VEHICLE_FIRE, false, point(wreck), Direction.UP);
        int interval = burning ? SMOKE_INTERVAL_BURNING : SMOKE_INTERVAL_SMOULDERING;
        if (wreck.tickCount % interval == 0) {
            MtsFxClient.play(MtsFxPreset.VEHICLE_SMOKE, false, point(wreck), Direction.UP);
        }
    }

    private static Vec3 point(VehicleWreckEntity wreck) {
        RandomSource random = wreck.level().random;
        double spread = wreck.getBbWidth() * HULL_SPREAD;
        return wreck.position().add((random.nextDouble() - 0.5) * 2 * spread, wreck.getBbHeight() * 0.7,
                (random.nextDouble() - 0.5) * 2 * spread);
    }
}
