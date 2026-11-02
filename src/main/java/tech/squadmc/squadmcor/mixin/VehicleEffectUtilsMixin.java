package tech.squadmc.squadmcor.mixin;

import com.atsuishio.superbwarfare.data.vehicle.subdata.VehicleType;
import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import com.atsuishio.superbwarfare.entity.vehicle.utils.VehicleEffectUtils;
import com.atsuishio.superbwarfare.entity.OBBEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tech.squadmc.squadmcor.repair.WheelLoss;

/** Логика SBW для частей без их авторегена: чинить теперь можно только горелкой или на станции. */
@Mixin(value = VehicleEffectUtils.class, remap = false)
public abstract class VehicleEffectUtilsMixin {

    /** Без дыма и искр от сломанных деталей. */
    @Inject(method = "handlePartDamaged", at = @At("HEAD"), cancellable = true)
    private static void squadmc$noPartEffects(VehicleEntity vehicle, OBBEntity obbEntity, CallbackInfo ci) {
        ci.cancel();
    }

    @Inject(method = "handlePartHealth", at = @At("HEAD"), cancellable = true)
    private static void squadmc$noPartRegen(VehicleEntity vehicle, CallbackInfo ci) {
        ci.cancel();
        VehicleType type = vehicle.getVehicleType();
        boolean aircraft = type == VehicleType.HELICOPTER || type == VehicleType.AIRPLANE || type == VehicleType.AIRSHIP;
        if (aircraft && vehicle.getHealth() < 0.05F * vehicle.getMaxHealth()) {
            vehicle.setMainEngineHealth(0F);
            vehicle.setSubEngineHealth(0F);
        }

        if (vehicle.getTurretHealth() <= 0F) vehicle.setTurretDamaged(true);
        else if (vehicle.getTurretHealth() > 0.95F * vehicle.getTurretMaxHealth()) vehicle.setTurretDamaged(false);

        if (WheelLoss.applies(vehicle)) {
            WheelLoss.update(vehicle);
        } else {
            if (vehicle.getLeftWheelHealth() <= 0F) vehicle.setLeftWheelDamaged(true);
            else if (vehicle.getLeftWheelHealth() > 0.95F * vehicle.getLeftWheelMaxHealth()) vehicle.setLeftWheelDamaged(false);

            if (vehicle.getRightWheelHealth() <= 0F) vehicle.setRightWheelDamaged(true);
            else if (vehicle.getRightWheelHealth() > 0.95F * vehicle.getRightWheelMaxHealth()) vehicle.setRightWheelDamaged(false);
        }

        if (vehicle.getMainEngineHealth() <= 0F) vehicle.setMainEngineDamaged(true);
        else if (vehicle.getMainEngineHealth() > 0.95F * vehicle.getMainEngineMaxHealth()) vehicle.setMainEngineDamaged(false);

        if (vehicle.getSubEngineHealth() <= 0F) vehicle.setSubEngineDamaged(true);
        else if (vehicle.getSubEngineHealth() > 0.95F * vehicle.getSubEngineMaxHealth()) vehicle.setSubEngineDamaged(false);
    }
}
