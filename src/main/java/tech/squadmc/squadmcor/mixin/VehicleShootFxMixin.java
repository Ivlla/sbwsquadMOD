package tech.squadmc.squadmcor.mixin;

import com.atsuishio.superbwarfare.data.gun.GunData;
import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tech.squadmc.squadmcor.entity.UralGradEntity;
import tech.squadmc.squadmcor.fx.GunFx;

/** SBW вызывает этот метод один раз на выстрел любой техники. У Града эффекты свои. */
@Mixin(value = VehicleEntity.class, remap = false)
public abstract class VehicleShootFxMixin {

    @Inject(method = "playShootSound3p(Lnet/minecraft/world/entity/LivingEntity;Lcom/atsuishio/superbwarfare/data/gun/GunData;Lnet/minecraft/world/phys/Vec3;)V",
            at = @At("HEAD"))
    private void squadmc$shootFx(LivingEntity living, GunData data, Vec3 pos, CallbackInfo ci) {
        VehicleEntity vehicle = (VehicleEntity) (Object) this;
        if (data == null || pos == null || vehicle instanceof UralGradEntity) return;
        if (!(vehicle.level() instanceof ServerLevel level)) return;
        GunFx.Kind kind = GunFx.kindOf(data);
        if (kind != GunFx.Kind.NONE) GunFx.send(level, kind, pos, GunFx.direction(vehicle, living), vehicle.getId(), 0);
    }
}
