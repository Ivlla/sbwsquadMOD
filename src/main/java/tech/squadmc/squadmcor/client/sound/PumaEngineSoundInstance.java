package tech.squadmc.squadmcor.client.sound;

import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import tech.squadmc.squadmcor.entity.PumaEntity;
import tech.squadmc.squadmcor.init.ModSounds;

public class PumaEngineSoundInstance extends AbstractTickableSoundInstance {
    private final PumaEntity vehicle;
    private final boolean isFirstPerson;
    private final boolean isIdle;
    private boolean fadingOut = false;
    private float targetVolume = 1.0F;

    public PumaEngineSoundInstance(PumaEntity vehicle, boolean isFirstPerson, boolean isIdle) {
        super(isIdle ?
                        (isFirstPerson ? ModSounds.BRADLEY_ENGINE_IDLE_1P.get() : ModSounds.BRADLEY_ENGINE_IDLE_3P.get()) :
                        (isFirstPerson ? ModSounds.BRADLEY_ENGINE_RUNNING_1P.get() : ModSounds.BRADLEY_ENGINE_RUNNING_3P.get()),
                SoundSource.NEUTRAL, SoundInstance.createUnseededRandom());

        this.vehicle = vehicle;
        this.isFirstPerson = isFirstPerson;
        this.isIdle = isIdle;
        this.looping = true;
        this.delay = 0;
        this.volume = 0.01F;
        this.pitch = 1.0F;

        if (isFirstPerson) {
            this.targetVolume = 1.0F;
        } else {
            this.targetVolume = isIdle ? 2.8F : 5.5F;
        }

        this.x = (float) vehicle.getX();
        this.y = (float) vehicle.getY();
        this.z = (float) vehicle.getZ();
    }

    @Override
    public void tick() {
        if (this.vehicle.isRemoved() || !this.vehicle.isAlive()) {
            this.stop();
            return;
        }

        this.x = (float) this.vehicle.getX();
        this.y = (float) this.vehicle.getY();
        this.z = (float) this.vehicle.getZ();

        if (this.fadingOut) {
            this.volume = Math.max(0.0F, this.volume - (this.targetVolume * 0.05F));
            if (this.volume <= 0.0F) {
                this.stop();
            }
            return;
        }

        if (this.volume < this.targetVolume) {
            this.volume = Mth.lerp(0.1F, this.volume, this.targetVolume);
        }

        double speed = this.vehicle.getDeltaMovement().horizontalDistance();
        if (this.isIdle) {
            this.pitch = 0.98F + (float) Math.sin(this.vehicle.tickCount * 0.1D) * 0.02F;
        } else {
            float speedFactor = (float) (speed * 1.5D);
            this.pitch = Mth.clamp(0.85F + speedFactor, 0.85F, 1.3F);
        }
    }

    public void startFadeOut() {
        this.fadingOut = true;
    }

    public boolean isFirstPerson() { return this.isFirstPerson; }
    public boolean isIdle() { return this.isIdle; }
    public boolean isFadingOut() { return this.fadingOut; }
}
