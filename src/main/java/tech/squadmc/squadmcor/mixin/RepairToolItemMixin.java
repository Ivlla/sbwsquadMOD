package tech.squadmc.squadmcor.mixin;

import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import com.atsuishio.superbwarfare.item.gun.special.RepairToolItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import tech.squadmc.squadmcor.repair.VehicleRepair;

/** Старый Repair Tool подчиняется тому же пределу, что и горелка. */
@Mixin(value = RepairToolItem.class, remap = false)
public abstract class RepairToolItemMixin {

    @Redirect(method = "onRayHitEntity", at = @At(value = "INVOKE",
            target = "Lcom/atsuishio/superbwarfare/entity/vehicle/base/VehicleEntity;heal(F)V"))
    private void squadmc$limitHeal(VehicleEntity vehicle, float amount) {
        float heal = VehicleRepair.torchHullHeal(vehicle, amount);
        if (heal > 0F) vehicle.heal(heal);
    }
}
