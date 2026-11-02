package tech.squadmc.squadmcor.mixin;

import com.atsuishio.superbwarfare.data.vehicle.subdata.EngineType;
import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Без авторегена корпуса. С выбитой гусеницей или колесом машина не едет, но поворачивает. */
@Mixin(value = VehicleEntity.class, remap = false)
public abstract class VehicleEntityRepairMixin {

    @Inject(method = "repairAmount", at = @At("HEAD"), cancellable = true)
    private void squadmc$noAutoRepair(CallbackInfoReturnable<Float> cir) {
        cir.setReturnValue(0F);
    }

    @Inject(method = "forwardInputDown", at = @At("HEAD"), cancellable = true)
    private void squadmc$noForward(CallbackInfoReturnable<Boolean> cir) {
        if (squadmc$running()) cir.setReturnValue(false);
    }

    @Inject(method = "backInputDown", at = @At("HEAD"), cancellable = true)
    private void squadmc$noBack(CallbackInfoReturnable<Boolean> cir) {
        if (squadmc$running()) cir.setReturnValue(false);
    }

    /** true, если ходовая выбита. */
    private boolean squadmc$running() {
        VehicleEntity vehicle = (VehicleEntity) (Object) this;
        if (!vehicle.getLeftWheelDamaged() && !vehicle.getRightWheelDamaged()) return false;
        EngineType type = vehicle.computed().getEngineType();
        return type == EngineType.TRACK || type == EngineType.WHEEL;
    }
}
