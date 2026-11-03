package tech.squadmc.squadmcor.mixin;

import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tech.squadmc.squadmcor.repair.VehicleWaterDamage;

/**
 * SBW only lets a vehicle step up blocks while it stands on the ground, so one that is afloat
 * could never climb out onto a bank. Afloat counts as standing for the step check.
 */
@Mixin(value = VehicleEntity.class, remap = false)
public abstract class VehicleWaterStepMixin {

    @Inject(method = "vCollide", at = @At("HEAD"))
    private void squadmc$stepWhileFloating(Vec3 movement, CallbackInfoReturnable<Vec3> cir) {
        VehicleEntity vehicle = (VehicleEntity) (Object) this;
        if (vehicle.isInFluidType() && VehicleWaterDamage.canFloat(vehicle)) {
            VehicleEntity.ignoreEntityGroundCheckStepping = true;
        }
    }
}
