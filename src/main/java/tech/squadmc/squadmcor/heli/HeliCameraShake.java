package tech.squadmc.squadmcor.heli;

import com.atsuishio.superbwarfare.event.ClientEventHandler;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import tech.squadmc.squadmcor.squadmc;

/**
 * Тряска камеры в вертолёте, по ощущениям как в Squad. Значения подобраны вручную, все в градусах:
 * гул винта, тряска на переходе в горизонтальный полёт и на большой скорости, нагрузка от ручки шага,
 * вибрация при повреждениях, толчки от посадки и стрельбы.
 */
@Mod.EventBusSubscriber(modid = squadmc.MODID, value = Dist.CLIENT)
public final class HeliCameraShake {
    private static final float ROTOR_AMP = 0.10F;
    private static final float TRANSITION_AMP = 0.38F;
    private static final float HIGH_SPEED_AMP = 0.30F;
    private static final float COLLECTIVE_AMP = 0.14F;
    private static final float DAMAGE_AMP = 0.55F;
    private static final float ROCKET_PITCH_KICK = 1.3F;
    private static final float KICK_DECAY = 0.78F;
    private static final float ZOOM_SCALE = 0.35F;
    private static final float THIRD_PERSON_SCALE = 0.4F;

    private static float intensityRotor, intensityBuffet, intensityDamage;
    private static float kick, kickRollSign = 1F;
    private static double lastVerticalSpeed;

    private HeliCameraShake() {}

    /** Вызывается из пакета выстрела. */
    public static void onFire(int vehicleId, float strength) {
        // Отдачу выстрела теперь считает VehicleRecoil.
        if (strength >= 0F) return;
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null || player.getRootVehicle().getId() != vehicleId) return;
        kick = Math.min(2.5F, kick + strength);
        kickRollSign = player.getRandom().nextBoolean() ? 1F : -1F;
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Minecraft mc = Minecraft.getInstance();
        kick *= KICK_DECAY;
        if (mc.player == null || !(mc.player.getRootVehicle() instanceof HeliVehicleBase heli)
                || !tech.squadmc.squadmcor.heli.entity.vehicle.engine.EngineControl.isHelicopter(heli) || mc.isPaused()) {
            intensityRotor = intensityBuffet = intensityDamage = 0F;
            return;
        }

        float rotor = Mth.clamp(Math.abs(heli.getPower()) / 0.6F, 0F, 1F);
        Vec3 motion = heli.getDeltaMovement();
        // Скорость в блоках в секунду.
        float speed = (float) motion.horizontalDistance() * 20F;

        // Самая сильная тряска при наборе скорости из висения: примерно 12–25 б/с.
        float transition = (float) Math.exp(-Math.pow((speed - 18F) / 8F, 2)) * rotor;
        float highSpeed = Mth.clamp((speed - 45F) / 35F, 0F, 1F);
        float collective = heli.upInputDown() ? rotor : 0F;

        intensityRotor = Mth.lerp(0.1F, intensityRotor, ROTOR_AMP * rotor);
        intensityBuffet = Mth.lerp(0.08F, intensityBuffet,
                TRANSITION_AMP * transition + HIGH_SPEED_AMP * highSpeed + COLLECTIVE_AMP * collective);
        float health = heli.getMaxHealth() > 0 ? heli.getHealth() / heli.getMaxHealth() : 1F;
        intensityDamage = Mth.lerp(0.05F, intensityDamage, DAMAGE_AMP * Mth.clamp((0.5F - health) / 0.5F, 0F, 1F) * rotor);

        // Жёсткая посадка.
        if (heli.onGround() && lastVerticalSpeed < -0.2) {
            kick = Math.min(2.5F, kick + (float) Math.min(1.5, -lastVerticalSpeed * 2.5));
        }
        lastVerticalSpeed = motion.y;
    }

    @SubscribeEvent
    public static void onCamera(ViewportEvent.ComputeCameraAngles event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || !(mc.player.getRootVehicle() instanceof HeliVehicleBase heli)
                || !tech.squadmc.squadmcor.heli.entity.vehicle.engine.EngineControl.isHelicopter(heli)) return;
        if (intensityRotor <= 0F && intensityBuffet <= 0F && intensityDamage <= 0F && kick < 0.01F) return;

        float t = (mc.player.tickCount + (float) event.getPartialTick());
        float scale = ClientEventHandler.zoomVehicle ? ZOOM_SCALE
                : mc.options.getCameraType() == CameraType.FIRST_PERSON ? 1F : THIRD_PERSON_SCALE;

        // Частый гул винта.
        float rotorPitch = intensityRotor * Mth.sin(t * 2.6F);
        float rotorRoll = intensityRotor * Mth.sin(t * 2.1F + 1.3F);
        // Медленная неровная тряска: сумма несоотносимых частот, чтобы не было заметного ритма.
        float buffetPitch = intensityBuffet * (Mth.sin(t * 0.93F) + 0.6F * Mth.sin(t * 1.71F + 0.7F)) * 0.6F;
        float buffetYaw = intensityBuffet * Mth.sin(t * 1.17F + 2.1F) * 0.4F;
        float buffetRoll = intensityBuffet * (Mth.sin(t * 0.77F + 1.1F) + 0.5F * Mth.sin(t * 1.93F)) * 0.7F;
        float damage = intensityDamage * (Mth.sin(t * 1.9F) + 0.5F * Mth.sin(t * 3.7F + 0.4F));

        float kickPitch = -ROCKET_PITCH_KICK * kick;
        float kickRoll = 0.55F * kick * kickRollSign * Mth.sin(t * 2.3F);

        event.setPitch(event.getPitch() + scale * (rotorPitch + buffetPitch + damage + kickPitch));
        event.setYaw(event.getYaw() + scale * (buffetYaw + 0.5F * damage));
        event.setRoll(event.getRoll() + scale * (rotorRoll + buffetRoll + 0.7F * damage + kickRoll));
    }
}
