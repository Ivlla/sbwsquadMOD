package tech.squadmc.squadmcor.client.wreck;

import com.atsuishio.superbwarfare.init.ModParticleTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import tech.squadmc.squadmcor.client.sound.EngineAudibleRange;
import tech.squadmc.squadmcor.entity.VehicleWreckEntity;
import tech.squadmc.squadmcor.fx.MtsFxClient;
import tech.squadmc.squadmcor.fx.MtsFxPreset;
import tech.squadmc.squadmcor.init.ModSounds;

import java.util.Map;
import java.util.WeakHashMap;

/** A wreck's fire, smoke and crackling, plus ammunition cooking off soon after it is destroyed. */
public final class VehicleWreckClient {

    private static final double EFFECT_RANGE_SQR = 256 * 256;
    private static final int SOUND_RANGE = 48;
    private static final int BLACK_SMOKE_INTERVAL = 6;
    private static final int THIN_SMOKE_INTERVAL = 6;
    /** The fire dies down over this many ticks before going out. */
    private static final int FIRE_FADE_TICKS = 600;
    private static final int COOK_OFF_TICKS = 800;
    private static final float COOK_OFF_CHANCE = 0.008F;
    private static final double HULL_SPREAD = 0.7;
    private static final float FIRE_STAR_CHANCE = 0.3F;
    private static final float LAVA_CHANCE = 0.05F;
    private static final Map<VehicleWreckEntity, BurnSound> BURN_SOUNDS = new WeakHashMap<>();

    private VehicleWreckClient() {
    }

    public static void tick(VehicleWreckEntity wreck) {
        int age = wreck.age();
        if (age >= VehicleWreckEntity.SMOKE_TICKS) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.player.distanceToSqr(wreck) > EFFECT_RANGE_SQR) return;

        RandomSource random = wreck.level().random;
        if (age >= VehicleWreckEntity.FIRE_TICKS) {
            if (wreck.tickCount % THIN_SMOKE_INTERVAL == 0) play(MtsFxPreset.VEHICLE_SMOKE_THIN, wreck, random);
            return;
        }

        float intensity = Mth.clamp((VehicleWreckEntity.FIRE_TICKS - age) / (float) FIRE_FADE_TICKS, 0F, 1F);
        if (random.nextFloat() < intensity) spawnFire(wreck, random);
        if (wreck.tickCount % BLACK_SMOKE_INTERVAL == 0) play(MtsFxPreset.VEHICLE_SMOKE, wreck, random);
        if (age < COOK_OFF_TICKS && random.nextFloat() < COOK_OFF_CHANCE) play(MtsFxPreset.BOMBLET, wreck, random);

        if (!BURN_SOUNDS.containsKey(wreck) && mc.player.distanceToSqr(wreck) < SOUND_RANGE * SOUND_RANGE) {
            BurnSound sound = new BurnSound(wreck);
            BURN_SOUNDS.put(wreck, sound);
            mc.getSoundManager().play(sound);
        }
    }

    /** The same particles SBW burns its vehicles with. */
    private static void spawnFire(VehicleWreckEntity wreck, RandomSource random) {
        var level = wreck.level();
        Vec3 flame = point(wreck, random);
        level.addParticle(ParticleTypes.FLAME, flame.x, flame.y, flame.z, 0, 0.03 + random.nextDouble() * 0.04, 0);
        Vec3 flame2 = point(wreck, random);
        level.addParticle(ParticleTypes.FLAME, flame2.x, flame2.y, flame2.z, 0, 0.03 + random.nextDouble() * 0.04, 0);
        if (random.nextFloat() < FIRE_STAR_CHANCE) {
            Vec3 star = point(wreck, random);
            level.addParticle(ModParticleTypes.FIRE_STAR.get(), star.x, star.y, star.z,
                    (random.nextDouble() - 0.5) * 0.2, 0.1 + random.nextDouble() * 0.2, (random.nextDouble() - 0.5) * 0.2);
        }
        if (random.nextFloat() < LAVA_CHANCE) {
            Vec3 lava = point(wreck, random);
            level.addParticle(ParticleTypes.LAVA, lava.x, lava.y, lava.z, 0, 0, 0);
        }
    }

    private static void play(MtsFxPreset preset, VehicleWreckEntity wreck, RandomSource random) {
        MtsFxClient.play(preset, false, point(wreck, random), Direction.UP);
    }

    private static Vec3 point(VehicleWreckEntity wreck, RandomSource random) {
        AABB box = wreck.getBoundingBox();
        double x = wreck.getX() + (random.nextDouble() - 0.5) * box.getXsize() * HULL_SPREAD;
        double z = wreck.getZ() + (random.nextDouble() - 0.5) * box.getZsize() * HULL_SPREAD;
        return new Vec3(x, wreck.getY() + box.getYsize() * 0.6, z);
    }

    /** Loops while the fire lasts. Sound file: assets/squadmc/sounds/vehicle_burn.ogg */
    private static final class BurnSound extends AbstractTickableSoundInstance implements EngineAudibleRange.Source {
        private final VehicleWreckEntity wreck;

        BurnSound(VehicleWreckEntity wreck) {
            super(ModSounds.VEHICLE_BURN.get(), SoundSource.BLOCKS, SoundInstance.createUnseededRandom());
            this.wreck = wreck;
            this.looping = true;
            this.delay = 0;
            this.volume = 1F;
            this.x = wreck.getX();
            this.y = wreck.getY();
            this.z = wreck.getZ();
        }

        @Override
        public void tick() {
            int age = this.wreck.age();
            if (this.wreck.isRemoved() || age >= VehicleWreckEntity.FIRE_TICKS) {
                BURN_SOUNDS.remove(this.wreck);
                stop();
                return;
            }
            this.volume = Mth.clamp((VehicleWreckEntity.FIRE_TICKS - age) / (float) FIRE_FADE_TICKS, 0F, 1F);
        }

        @Override
        public int audibleRange() {
            return SOUND_RANGE;
        }
    }
}
