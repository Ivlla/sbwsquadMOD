package tech.squadmc.squadmcor.mixin;

import com.atsuishio.superbwarfare.client.sound.VehicleSoundInstance;
import net.minecraft.client.resources.sounds.AbstractSoundInstance;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.client.sounds.WeighedSoundEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tech.squadmc.squadmcor.client.sound.EngineAudibleRange;

/** Applies vehicle engine hearing ranges, including SBW's own engine loop. */
@Mixin(AbstractSoundInstance.class)
public abstract class EngineSoundRangeMixin {

    @Shadow
    protected Sound sound;

    @Shadow
    protected float volume;

    @Inject(method = "resolve", at = @At("RETURN"))
    private void squadmc$applyRange(SoundManager manager, CallbackInfoReturnable<WeighedSoundEvents> cir) {
        Object self = this;
        int range = 0;
        if (self instanceof EngineAudibleRange.Source source) {
            range = source.audibleRange();
        } else if (self instanceof VehicleSoundInstance.EngineSound) {
            range = EngineAudibleRange.movingRange(((VehicleSoundInstanceAccessor) self).squadmc$getVehicle());
        }
        if (range > 0) {
            this.sound = EngineAudibleRange.withRange(this.sound, range, this.volume);
        }
    }

    @Inject(method = "getVolume", at = @At("RETURN"), cancellable = true)
    private void squadmc$applyGain(CallbackInfoReturnable<Float> cir) {
        Object self = this;
        float gain = 1F;
        if (self instanceof EngineAudibleRange.Source source) {
            gain = source.rangeGain();
        } else if (self instanceof VehicleSoundInstance.EngineSound) {
            gain = EngineAudibleRange.idleGain(((VehicleSoundInstanceAccessor) self).squadmc$getVehicle());
        }
        if (gain != 1F) {
            cir.setReturnValue(cir.getReturnValueF() * gain);
        }
    }
}
