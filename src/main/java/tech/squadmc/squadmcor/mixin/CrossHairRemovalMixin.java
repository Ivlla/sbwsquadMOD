package tech.squadmc.squadmcor.mixin;

import com.atsuishio.superbwarfare.client.overlay.CrossHairOverlay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** No crosshair on hand-held guns. Hit and kill markers, and the repair tool's readout, stay. */
@Mixin(value = CrossHairOverlay.class, remap = false)
public abstract class CrossHairRemovalMixin {

    @Inject(method = {"renderGunDefaultCrosshair", "renderBocekCrosshair", "renderGrenadeCrosshair"},
            at = @At("HEAD"), cancellable = true)
    private void squadmc$noCrosshair(CallbackInfo ci) {
        ci.cancel();
    }
}
