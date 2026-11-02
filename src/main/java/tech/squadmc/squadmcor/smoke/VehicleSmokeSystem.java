package tech.squadmc.squadmcor.smoke;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import tech.squadmc.squadmcor.entity.SquadBaseVehicleEntity;
import tech.squadmc.squadmcor.init.ModEntities;

/**
 * Серверная обвязка дыма техники: залп дымовых гранатомётов и постоянный
 * дымогенератор. Использует только свои классы (VehicleSmokeCloudEntity) —
 * зависимости от aasgranate нет.
 */
public final class VehicleSmokeSystem {

    // --- залп дымовых гранатомётов ---
    public static final int VOLLEY_COOLDOWN_TICKS = 240; // 12 сек
    public static final int DEFAULT_CHARGES = 4;

    /** Облако залпа ДГ — подгоняется под размер машины в одном месте. */
    public static final float LAUNCHER_RADIUS = 5.5F;
    public static final float LAUNCHER_HEIGHT = 2.6F;
    public static final int LAUNCHER_GROW_TICKS = 140;
    public static final int LAUNCHER_HOLD_TICKS = 700;
    public static final int LAUNCHER_FADE_TICKS = 160;

    // --- дымогенератор (выхлоп) ---
    // Уменьшено в 1.5 раза относительно исходных 4.2F / 2.4F по просьбе:
    // визуально клубки меньше, форма (соотношение радиус/высота) та же.
    public static final float GENERATOR_RADIUS = 2.8F;
    public static final float GENERATOR_HEIGHT = 1.6F;
    public static final int GENERATOR_GROW_TICKS = 60;
    public static final int GENERATOR_HOLD_TICKS = 100;
    public static final int GENERATOR_FADE_TICKS = 200;
    public static final float GENERATOR_INTENSITY = 0.5F;   // 1.0 = как раньше

    /** Раз в сколько тиков дымогенератор оставляет позади новый клубок (шлейф). */
    public static final int TRAIL_INTERVAL_TICKS = 8; // ~0.4 сек при 20 tps

    private VehicleSmokeSystem() {}

    /** Локальный offset → мировые координаты с учётом yaw техники (то же вращение, что в tickServer облака). */
    public static Vec3 localToWorld(Entity vehicle, Vec3 local) {
        float yawRad = -vehicle.getYRot() * ((float) Math.PI / 180F);
        double ox = local.x * Math.cos(yawRad) - local.z * Math.sin(yawRad);
        double oz = local.x * Math.sin(yawRad) + local.z * Math.cos(yawRad);
        return new Vec3(vehicle.getX() + ox, vehicle.getY() + local.y, vehicle.getZ() + oz);
    }

    /** Залп дымовых гранатомётов: по облаку на каждую точку крепления. Только сервер. */
    public static void fireVolley(SquadBaseVehicleEntity vehicle, ServerPlayer player) {
        Level level = vehicle.level();
        if (level.isClientSide) return;

        if (vehicle.getSmokeVolleyCooldown() > 0 || vehicle.getSmokeCharges() <= 0) {
            player.displayClientMessage(Component.translatable("message.squadmc.smoke_reloading"), true);
            return;
        }

        for (Vec3 local : vehicle.getSmokeLauncherPoints()) {
            VehicleSmokeCloudEntity cloud =
                    new VehicleSmokeCloudEntity(ModEntities.VEHICLE_SMOKE_CLOUD.get(), level);
            cloud.setPos(localToWorld(vehicle, local).add(0.0, 0.35, 0.0));
            cloud.configure(LAUNCHER_RADIUS, LAUNCHER_HEIGHT,
                    LAUNCHER_GROW_TICKS, LAUNCHER_HOLD_TICKS, LAUNCHER_FADE_TICKS);
            level.addFreshEntity(cloud);
        }

        vehicle.setSmokeCharges(vehicle.getSmokeCharges() - 1);
        vehicle.setSmokeVolleyCooldown(VOLLEY_COOLDOWN_TICKS);
        level.playSound(null, vehicle.getX(), vehicle.getY() + 1.5, vehicle.getZ(),
                SoundEvents.FIRECHARGE_USE, SoundSource.PLAYERS, 1.6F, 0.9F);
        player.displayClientMessage(
                Component.translatable("message.squadmc.smoke_volley", vehicle.getSmokeCharges()), true);
    }

    /**
     * Дымогенератор: вызывать каждый тик, пока включён. В отличие от старой
     * версии, теперь оставляет позади СВОБОДНЫЕ (не привязанные к технике)
     * клубки дыма каждые TRAIL_INTERVAL_TICKS тиков — получается настоящий
     * шлейф, а не одна точка, которая просто едет вместе с машиной.
     * Каждый клубок живёт своей жизнью (grow/hold/fade) и угасает сам.
     */
    public static void tickGenerator(SquadBaseVehicleEntity vehicle) {
        Level level = vehicle.level();
        if (level.isClientSide) return;

        if (vehicle.tickCount % TRAIL_INTERVAL_TICKS != 0) return;

        Vec3 offset = vehicle.getSmokeGeneratorOffset();
        // усредняем текущий и предыдущий yRot, чтобы точка спавна лучше
        // совпадала с тем, что в этот момент реально видно на клиенте
        // (клиент интерполирует поворот, и "чистый" текущий yRot всегда
        // немного забегает вперёд относительно отрисованной модели)
        float blendedYRot = (vehicle.yRotO + vehicle.getYRot()) / 2.0F;
        Vec3 puffPos = localToWorldWithYaw(vehicle, offset, blendedYRot);

        VehicleSmokeCloudEntity puff =
                new VehicleSmokeCloudEntity(ModEntities.VEHICLE_SMOKE_CLOUD.get(), level);
        puff.setPos(puffPos);
        puff.configure(GENERATOR_RADIUS, GENERATOR_HEIGHT,
                GENERATOR_GROW_TICKS, GENERATOR_HOLD_TICKS, GENERATOR_FADE_TICKS);
        puff.setIntensity(GENERATOR_INTENSITY);
        level.addFreshEntity(puff);
    }
    public static Vec3 localToWorldWithYaw(Entity vehicle, Vec3 local, float yRotDeg) {
        float yawRad = -yRotDeg * ((float) Math.PI / 180F);
        double ox = local.x * Math.cos(yawRad) - local.z * Math.sin(yawRad);
        double oz = local.x * Math.sin(yawRad) + local.z * Math.cos(yawRad);
        return new Vec3(vehicle.getX() + ox, vehicle.getY() + local.y, vehicle.getZ() + oz);
    }
}