package tech.squadmc.squadmcor.client.sound;

import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import tech.squadmc.squadmcor.entity.UralGradEntity;
import tech.squadmc.squadmcor.init.ModSounds;

public class UralGradEngineSoundInstance extends AbstractTickableSoundInstance {
    private final UralGradEntity vehicle;
    private final boolean isFirstPerson;
    private final boolean isIdle;
    private boolean fadingOut = false;
    private float targetVolume = 1.0F;

    public UralGradEngineSoundInstance(UralGradEntity vehicle, boolean isFirstPerson, boolean isIdle) {
        super(isIdle ?
                        (isFirstPerson ? ModSounds.URAL_ENGINE_IDLE_1P.get() : ModSounds.URAL_ENGINE_IDLE_3P.get()) :
                        (isFirstPerson ? ModSounds.URAL_RUNNING_1P.get() : ModSounds.URAL_RUNNING_3P.get()),
                SoundSource.NEUTRAL, SoundInstance.createUnseededRandom());

        this.vehicle = vehicle;
        this.isFirstPerson = isFirstPerson;
        this.isIdle = isIdle;
        this.looping = true;
        this.delay = 0;
        this.volume = 0.01F;
        this.pitch = 1.0F;

        // Р“Р РЈР—РћР’РРљР: РЎС‚РѕРёС‚ = 20 (1.25F), Р•РґРµС‚ = 40 (2.5F)
        if (isFirstPerson) {
            this.targetVolume = 1.0F;
        } else {
            this.targetVolume = isIdle ? 1.25F : 2.5F;
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
            this.pitch = 0.95F + (float) Math.sin(this.vehicle.tickCount * 0.1D) * 0.02F;
        } else {
            float speedFactor = (float) (speed * 1.4D);
            this.pitch = Mth.clamp(0.80F + speedFactor, 0.80F, 1.20F);
        }
    }

    public void startFadeOut() {
        this.fadingOut = true;
    }

    public boolean isFirstPerson() { return this.isFirstPerson; }
    public boolean isIdle() { return this.isIdle; }
    public boolean isFadingOut() { return this.fadingOut; }
}
