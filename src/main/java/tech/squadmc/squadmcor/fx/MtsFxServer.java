package tech.squadmc.squadmcor.fx;

import com.atsuishio.superbwarfare.tools.ParticleTool;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;
import tech.squadmc.squadmcor.network.ModNetworking;
import tech.squadmc.squadmcor.network.MtsFxPacket;
import tech.squadmc.squadmcor.sound.HitCaliber;

import javax.annotation.Nullable;

/** Рассылка эффектов клиентам. СЕРВЕР. */
public final class MtsFxServer {
    /** Самый дальний звук взрыва в этих наборах слышен дальше, но 512 блоков хватает. */
    private static final double RANGE = 512.0;

    private MtsFxServer() {}

    /** Эффект взрыва SBW: частицы и звук из MTS. Тряску камеры по-прежнему делает сам SBW. */
    public static void explosion(ServerLevel level, Vec3 pos, @Nullable ParticleTool.ParticleType type) {
        ParticleTool.ParticleType actual = type != null ? type : ParticleTool.ParticleType.MINI;
        MtsFxPreset preset = switch (actual) {
            case MINI -> MtsFxPreset.BOMBLET;
            case SMALL -> MtsFxPreset.HE_37MM;
            case MEDIUM -> MtsFxPreset.ROCKET;
            case LARGE, HUGE -> MtsFxPreset.BASIC_BOMB;
            case GIANT, EPIC -> MtsFxPreset.HEAVY_BOMB;
        };
        send(level, pos, new MtsFxPacket(preset, false, pos, Direction.UP));
    }

    /** Гибель техники: тяжёлой — большой взрыв с обломками и огнём, лёгкой — поменьше. */
    public static void vehicleDeath(ServerLevel level, Vec3 pos, boolean heavy) {
        send(level, pos, new MtsFxPacket(heavy ? MtsFxPreset.HEAVY_BOMB : MtsFxPreset.BASIC_BOMB, false, pos, Direction.UP));
    }

    /** Вспышка и дым пуска ракеты из направляющей. */
    public static void rocketLaunch(ServerLevel level, Vec3 pos) {
        send(level, pos, new MtsFxPacket(MtsFxPreset.ROCKET_LAUNCH, false, pos, Direction.UP));
    }

    /** Попадание по технике. Частицы летят навстречу снаряду, как hitSide в MTS. */
    public static void vehicleHit(ServerLevel level, Vec3 hitPos, Entity projectile, @Nullable HitCaliber caliber) {
        MtsFxPreset preset = caliber == null || caliber == HitCaliber.SMALL ? MtsFxPreset.HIT_762 : MtsFxPreset.HIT_AP_37MM;
        Vec3 motion = projectile.getDeltaMovement();
        Direction normal = motion.lengthSqr() < 1.0E-6
                ? Direction.UP
                : Direction.getNearest(-motion.x, -motion.y, -motion.z);
        send(level, hitPos, new MtsFxPacket(preset, true, hitPos, normal));
    }

    private static void send(ServerLevel level, Vec3 pos, MtsFxPacket packet) {
        ModNetworking.CHANNEL.send(
                PacketDistributor.NEAR.with(() ->
                        new PacketDistributor.TargetPoint(pos.x, pos.y, pos.z, RANGE, level.dimension())),
                packet);
    }
}
