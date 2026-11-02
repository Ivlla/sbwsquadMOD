package tech.squadmc.squadmcor.client.sound;

import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import tech.squadmc.squadmcor.entity.MotorcycleEntity;
import tech.squadmc.squadmcor.init.ModSounds;

public class MotorcycleEngineSoundInstance extends AbstractTickableSoundInstance {
    private final MotorcycleEntity vehicle;
    private final boolean isFirstPerson;
    private final boolean isIdle;
    private boolean fadingOut = false;
    private float targetVolume = 0.8F;

    public MotorcycleEngineSoundInstance(MotorcycleEntity vehicle, boolean isFirstPerson, boolean isIdle) {
        super(isIdle ?
                        (isFirstPerson ? ModSounds.MOTORCYCLE_IDLE_1P.get() : ModSounds.MOTORCYCLE_IDLE_1P.get()) :
                        (isFirstPerson ? ModSounds.MOTORCYCLE_RUNNING_1P.get() : ModSounds.MOTORCYCLE_RUNNING_1P.get()),
                SoundSource.NEUTRAL, SoundInstance.createUnseededRandom());

        this.vehicle = vehicle;
        this.isFirstPerson = isFirstPerson;
        this.isIdle = isIdle;
        this.looping = true;
        this.delay = 0;
        this.volume = 0.01F;
        this.pitch = 1.0F;

        // РњРћРўРћР¦РРљР›Р« / Р›РћР”РљР: РЎС‚РѕРёС‚ = 10 (0.625F), Р•РґРµС‚ = 20 (1.25F)
        if (isFirstPerson) {
            this.targetVolume = 0.8F;
        } else {
            this.targetVolume = isIdle ? 0.625F : 1.25F;
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
            this.pitch = 0.96F + (float) Math.sin(this.vehicle.tickCount * 0.18D) * 0.02F;
        } else {
            float speedFactor = (float) (speed * 1.9D);
            this.pitch = Mth.clamp(0.85F + speedFactor, 0.85F, 1.45F);
        }
    }

    public void startFadeOut() {
        this.fadingOut = true;
    }

    public boolean isFirstPerson() { return this.isFirstPerson; }
    public boolean isIdle() { return this.isIdle; }
    public boolean isFadingOut() { return this.fadingOut; }
}
