package tech.squadmc.squadmcor.client.recoil;

import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import com.atsuishio.superbwarfare.event.ClientEventHandler;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import tech.squadmc.squadmcor.fx.GunFx;
import tech.squadmc.squadmcor.squadmc;

/**
 * Отдача прицела. Сдвигается только камера, а взгляд игрока не меняется, поэтому прицел всегда
 * возвращается туда, куда смотрел. Толчок идёт через пружину с затуханием: быстрый подброс и плавный
 * возврат, сверху короткая дрожь корпуса, которая гаснет сама.
 */
@Mod.EventBusSubscriber(modid = squadmc.MODID, value = Dist.CLIENT)
public final class VehicleRecoil {
    private static final float STIFFNESS = 90F;
    private static final float DAMPING = 13F;
    private static final float MAX_ANGLE = 14F;
    private static final float TRAUMA_DECAY_PER_SECOND = 1.7F;
    private static final float TRAUMA_DEGREES = 3.2F;
    private static final float ZOOM_SCALE = 0.8F;
    private static final float THIRD_PERSON_SCALE = 0.6F;
    private static final float MAX_FRAME = 0.05F;

    private static float pitch, pitchVelocity, yaw, yawVelocity, roll, rollVelocity, trauma;
    private static long lastFrame;

    private VehicleRecoil() {}

    /** { подброс, разброс вбок и по крену, дрожь } */
    private static float[] strength(GunFx.Kind kind) {
        return switch (kind) {
            case SMALL -> new float[]{7F, 4F, 0.12F};
            case MEDIUM -> new float[]{22F, 11F, 0.28F};
            case HEAVY -> new float[]{120F, 34F, 1F};
            case ROCKET -> new float[]{26F, 15F, 0.55F};
            case GRAD -> new float[]{30F, 18F, 0.6F};
            default -> new float[]{0F, 0F, 0F};
        };
    }

    public static void onShot(GunFx.Kind kind, int vehicleId) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null || player.getRootVehicle().getId() != vehicleId) return;
        // Лёгкое оружие чувствует только стрелок, тяжёлое трясёт всю машину.
        Entity root = player.getRootVehicle();
        float share = 1F;
        if (root instanceof VehicleEntity vehicle && vehicle.getGunData(vehicle.getSeatIndex(player)) == null) {
            if (kind == GunFx.Kind.SMALL || kind == GunFx.Kind.MEDIUM) return;
            share = 0.5F;
        }
        float[] s = strength(kind);
        var random = player.getRandom();
        pitchVelocity -= s[0] * share * (0.85F + 0.3F * random.nextFloat());
        yawVelocity += s[1] * share * (random.nextFloat() - 0.5F);
        rollVelocity += s[1] * share * (random.nextFloat() - 0.5F) * 1.4F;
        trauma = Math.min(1F, trauma + s[2] * share);
    }

    private static void step(float dt) {
        pitchVelocity += (-STIFFNESS * pitch - DAMPING * pitchVelocity) * dt;
        pitch = Mth.clamp(pitch + pitchVelocity * dt, -MAX_ANGLE, MAX_ANGLE);
        yawVelocity += (-STIFFNESS * yaw - DAMPING * yawVelocity) * dt;
        yaw = Mth.clamp(yaw + yawVelocity * dt, -MAX_ANGLE, MAX_ANGLE);
        rollVelocity += (-STIFFNESS * roll - DAMPING * rollVelocity) * dt;
        roll = Mth.clamp(roll + rollVelocity * dt, -MAX_ANGLE, MAX_ANGLE);
        trauma = Math.max(0F, trauma - TRAUMA_DECAY_PER_SECOND * dt);
    }

    @SubscribeEvent
    public static void onCamera(ViewportEvent.ComputeCameraAngles event) {
        Minecraft mc = Minecraft.getInstance();
        long now = System.nanoTime();
        float dt = lastFrame == 0L ? 0F : Math.min(MAX_FRAME, (now - lastFrame) / 1.0E9F);
        lastFrame = now;
        if (mc.player == null || mc.isPaused()) return;
        if (dt > 0F) step(dt);

        float scale = ClientEventHandler.zoomVehicle ? ZOOM_SCALE
                : mc.options.getCameraType() == CameraType.FIRST_PERSON ? 1F : THIRD_PERSON_SCALE;
        // Квадрат делает сильную дрожь резкой, а слабую почти незаметной.
        float shake = trauma * trauma * TRAUMA_DEGREES;
        float t = now / 1.0E9F;
        float shakePitch = shake * (Mth.sin(t * 61F) + 0.5F * Mth.sin(t * 37F + 1.3F));
        float shakeYaw = shake * (Mth.sin(t * 53F + 2.1F) + 0.5F * Mth.sin(t * 29F));
        float shakeRoll = shake * 0.7F * Mth.sin(t * 47F + 0.7F);

        event.setPitch(event.getPitch() + scale * (pitch + shakePitch));
        event.setYaw(event.getYaw() + scale * (yaw + shakeYaw));
        event.setRoll(event.getRoll() + scale * (roll + shakeRoll));
    }
}
