package tech.squadmc.squadmcor.mixin;

import com.atsuishio.superbwarfare.client.model.entity.VehicleModel;
import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import tech.squadmc.squadmcor.client.repair.DamageVisuals;

/** Прячет выбитые гусеницы и колёса. */
@Mixin(value = VehicleModel.class, remap = false)
public abstract class VehicleModelDamageMixin {

    @Inject(method = "setCustomAnimations(Lcom/atsuishio/superbwarfare/entity/vehicle/base/VehicleEntity;JLsoftware/bernie/geckolib/core/animation/AnimationState;)V",
            at = @At("TAIL"))
    private void squadmc$hideBroken(VehicleEntity vehicle, long instanceId, AnimationState<?> state, CallbackInfo ci) {
        DamageVisuals.apply((GeoModel<?>) (Object) this, vehicle);
    }
}
