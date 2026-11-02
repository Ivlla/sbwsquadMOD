package tech.squadmc.squadmcor.client;

import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import com.atsuishio.superbwarfare.entity.vehicle.utils.VehicleVecUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector4d;
import tech.squadmc.squadmcor.client.model.UralGradModel;
import tech.squadmc.squadmcor.fx.GunFx;
import tech.squadmc.squadmcor.fx.MtsFxClient;
import tech.squadmc.squadmcor.fx.MtsFxPreset;

/** Клиент: дым из ствола, пыль от тяжёлых выстрелов и пуски Града из конкретной ракеты. */
public final class GunFxClient {
    private static final double HEAVY_DUST_HEIGHT = 5.0;
    private static final double GRAD_DUST_BEHIND = 4.0;
    private static final double GRAD_DUST_HEIGHT = 8.0;

    private GunFxClient() {}

    public static void play(GunFx.Kind kind, Vec3 pos, Vec3 direction, int vehicleId, int rocket) {
        tech.squadmc.squadmcor.client.recoil.VehicleRecoil.onShot(kind, vehicleId);
        switch (kind) {
            case SMALL -> MtsFxClient.playDirected(MtsFxPreset.MUZZLE_SMALL, pos, direction);
            case MEDIUM -> MtsFxClient.playDirected(MtsFxPreset.MUZZLE_MEDIUM, pos, direction);
            case HEAVY -> {
                MtsFxClient.playDirected(MtsFxPreset.MUZZLE_HEAVY, pos, direction);
                MtsFxClient.playGroundBlast(pos, HEAVY_DUST_HEIGHT);
            }
            case ROCKET -> MtsFxClient.playDirected(MtsFxPreset.ROCKET_LAUNCH, pos, direction);
            case GRAD -> grad(pos, direction, vehicleId, rocket);
            default -> {
            }
        }
    }

    private static void grad(Vec3 fallback, Vec3 direction, int vehicleId, int rocket) {
        Vec3 pos = fallback;
        Minecraft mc = Minecraft.getInstance();
        Entity entity = mc.level == null ? null : mc.level.getEntity(vehicleId);
        if (entity instanceof VehicleEntity vehicle) {
            Vec3 rocketPos = rocketWorldPos(vehicle, rocket);
            if (rocketPos != null) pos = rocketPos;
        }
        // Пламя и дым бьют назад из сопла, а снаряд уходит вперёд.
        Vec3 back = direction.scale(-1);
        MtsFxClient.play(MtsFxPreset.ROCKET_LAUNCH, false, pos, Direction.UP);
        MtsFxClient.playDirected(MtsFxPreset.GRAD_BLAST, pos, back);
        MtsFxClient.playGroundBlast(pos.add(back.scale(GRAD_DUST_BEHIND)), GRAD_DUST_HEIGHT);
    }

    /** Точка кости rocketN в мире: положение кости в координатах машины переводится в систему барабана. */
    private static Vec3 rocketWorldPos(VehicleEntity vehicle, int rocket) {
        float[] pivot = UralGradModel.rocketPivot(rocket);
        Vec3 turret = vehicle.getTurretPos();
        Vec3 barrel = vehicle.getBarrelPosition();
        if (pivot == null || turret == null || barrel == null) return null;
        // GeckoLib меняет знак X при загрузке, а перед модели смотрит в сторону -Z.
        double x = -pivot[0] / 16.0 - turret.x - barrel.x;
        double y = pivot[1] / 16.0 - turret.y - barrel.y;
        double z = -pivot[2] / 16.0 - turret.z - barrel.z;
        Vector4d world = VehicleVecUtils.transformPosition(vehicle.getBarrelTransform(1F), x, y, z);
        return new Vec3(world.x, world.y, world.z);
    }
}
