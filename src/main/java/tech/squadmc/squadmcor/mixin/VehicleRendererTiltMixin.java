package tech.squadmc.squadmcor.mixin;

import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tech.squadmc.squadmcor.client.repair.DamageVisuals;

/** Крен в сторону выбитой гусеницы или колеса. */
@Mixin(value = VehicleRenderer.class, remap = false)
public abstract class VehicleRendererTiltMixin {

    @Inject(method = "vehicleAxis", at = @At("RETURN"))
    private void squadmc$tilt(VehicleEntity vehicle, PoseStack pose, float yaw, float partialTicks, CallbackInfo ci) {
        float[] tilt = DamageVisuals.tilt(vehicle);
        if (tilt == null) return;
        pose.mulPose(Axis.ZP.rotationDegrees(tilt[0]));
        pose.mulPose(Axis.XP.rotationDegrees(tilt[1]));
    }
}
