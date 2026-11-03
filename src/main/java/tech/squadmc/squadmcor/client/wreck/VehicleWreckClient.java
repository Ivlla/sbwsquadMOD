package tech.squadmc.squadmcor.client.wreck;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import tech.squadmc.squadmcor.client.sound.EngineAudibleRange;
import tech.squadmc.squadmcor.entity.VehicleWreckEntity;
import tech.squadmc.squadmcor.fx.MtsFxClient;
import tech.squadmc.squadmcor.fx.MtsFxPreset;

/** A wreck's fire, smoke and crackling, plus ammunition cooking off soon after it is destroyed. */
public final class VehicleWreckClient {

    private static final double EFFECT_RANGE_SQR = 256 * 256;
    private static final int SOUND_RANGE = 48;
    private static final ResourceLocation BURN_SOUND = new ResourceLocation("superbwarfare", "turret_burn");
    private static final int SOUND_INTERVAL = 18;
    private static final int BLACK_SMOKE_INTERVAL = 2;
    private static final int THIN_SMOKE_INTERVAL = 6;
    /** The fire dies down over this many ticks before going out. */
    private static final int FIRE_FADE_TICKS = 600;
    private static final int COOK_OFF_TICKS = 800;
    private static final float COOK_OFF_CHANCE = 0.015F;
    private static final double HULL_SPREAD = 0.7;

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
        if (random.nextFloat() < intensity) play(MtsFxPreset.VEHICLE_FIRE, wreck, random);
        if (random.nextFloat() < intensity) play(MtsFxPreset.VEHICLE_FIRE, wreck, random);
        if (wreck.tickCount % BLACK_SMOKE_INTERVAL == 0) play(MtsFxPreset.VEHICLE_SMOKE, wreck, random);
        if (age < COOK_OFF_TICKS && random.nextFloat() < COOK_OFF_CHANCE) play(MtsFxPreset.BOMBLET, wreck, random);

        if (wreck.tickCount % SOUND_INTERVAL == 0) {
            mc.getSoundManager().play(new BurnSound(wreck, intensity, random));
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

    private static final class BurnSound extends SimpleSoundInstance implements EngineAudibleRange.Source {
        BurnSound(VehicleWreckEntity wreck, float volume, RandomSource random) {
            super(BURN_SOUND, SoundSource.BLOCKS, volume, 0.85F + random.nextFloat() * 0.3F, random, false, 0,
                    SoundInstance.Attenuation.LINEAR, wreck.getX(), wreck.getY(), wreck.getZ(), false);
        }

        @Override
        public int audibleRange() {
            return SOUND_RANGE;
        }
    }
}
