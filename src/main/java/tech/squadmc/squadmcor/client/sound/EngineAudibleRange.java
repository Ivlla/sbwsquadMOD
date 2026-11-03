package tech.squadmc.squadmcor.client.sound;

import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import tech.squadmc.squadmcor.heli.entity.vehicle.engine.EngineControl;

/**
 * How far a vehicle's engine can be heard, in blocks.
 *
 * Vanilla fixes a sound's range when it starts playing, from its volume at that moment, and later
 * volume changes only affect loudness. Engine sounds start nearly silent to fade in, so their range
 * has to be set on the {@link Sound} itself rather than through volume.
 */
public final class EngineAudibleRange {

    private static final int LIGHT = 64;
    private static final int TRUCK = 80;
    private static final int APC = 128;
    private static final int HEAVY = 256;
    private static final float MOVING_MULTIPLIER = 1.5F;
    private static final double MOVING_SPEED = 0.07;

    private static final String[] LIGHT_IDS = {"humvee", "toyota", "uaz", "fenek", "gaz_tigr", "motuo", "motorboat", "rg_33"};
    private static final String[] TRUCK_IDS = {"fmtv", "kamaz", "m939", "man_hx", "ural", "grad"};
    private static final String[] TANK_IDS = {"m1a2", "leopard", "t64", "t72", "t80", "sprut"};

    private EngineAudibleRange() {
    }

    /** A sound whose audible range depends on its vehicle. */
    public interface Source {
        /** @return range in blocks, or 0 to leave the sound's own range alone. */
        int audibleRange();

        /** Extra gain applied on top of the sound's volume. */
        default float rangeGain() {
            return 1F;
        }
    }

    public static int idleRange(VehicleEntity vehicle) {
        if (EngineControl.isHelicopter(vehicle)) return HEAVY;
        String id = EntityType.getKey(vehicle.getType()).getPath();
        if (matches(id, TANK_IDS)) return HEAVY;
        if (matches(id, LIGHT_IDS)) return LIGHT;
        if (matches(id, TRUCK_IDS)) return TRUCK;
        return APC;
    }

    public static int movingRange(VehicleEntity vehicle) {
        return Math.round(idleRange(vehicle) * MOVING_MULTIPLIER);
    }

    public static int range(VehicleEntity vehicle, boolean idle) {
        return idle ? idleRange(vehicle) : movingRange(vehicle);
    }

    private static boolean matches(String id, String[] prefixes) {
        for (String prefix : prefixes) {
            if (id.startsWith(prefix)) return true;
        }
        return false;
    }

    /**
     * For one long-lived sound that plays at the moving range: gain that narrows it to the idle
     * range while the vehicle stands still, following the same linear falloff.
     */
    public static float idleGain(VehicleEntity vehicle) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return 1F;
        float moving = (float) Mth.clamp(vehicle.getDeltaMovement().length() / MOVING_SPEED, 0.0, 1.0);
        if (moving >= 1F) return 1F;
        float distance = (float) mc.gameRenderer.getMainCamera().getPosition().distanceTo(vehicle.position());
        float atMoving = 1F - distance / movingRange(vehicle);
        if (atMoving <= 0F) return 1F;
        float atIdle = Math.max(0F, 1F - distance / idleRange(vehicle));
        return Mth.lerp(moving, atIdle / atMoving, 1F);
    }

    /** Same sound file and settings, heard over {@code range} blocks. */
    public static Sound withRange(Sound sound, int range, float volume) {
        if (sound == null || sound == SoundManager.EMPTY_SOUND) return sound;
        // Vanilla multiplies the attenuation distance by volumes above 1.
        int distance = Math.max(1, Math.round(range / Math.max(volume, 1F)));
        return new Sound(sound.getLocation().toString(), sound.getVolume(), sound.getPitch(), sound.getWeight(),
                sound.getType(), sound.shouldStream(), sound.shouldPreload(), distance);
    }

    /** A one-shot engine sound, such as starting up, at a vehicle's idle range. */
    public static final class OneShot extends SimpleSoundInstance implements Source {
        private final int range;

        public OneShot(VehicleEntity vehicle, ResourceLocation sound, SoundSource source, float volume, RandomSource random) {
            super(sound, source, volume, 1F, random, false, 0, SoundInstance.Attenuation.LINEAR,
                    vehicle.getX(), vehicle.getY(), vehicle.getZ(), false);
            this.range = idleRange(vehicle);
        }

        @Override
        public int audibleRange() {
            return this.range;
        }
    }
}
