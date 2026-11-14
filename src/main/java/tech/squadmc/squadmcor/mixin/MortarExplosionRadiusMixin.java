package tech.squadmc.squadmcor.mixin;

import com.atsuishio.superbwarfare.entity.projectile.FastThrowableProjectile;
import com.atsuishio.superbwarfare.entity.projectile.MortarShellEntity;
import com.atsuishio.superbwarfare.tools.CustomExplosion;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tech.squadmc.squadmcor.explosion.ExplosionSettings;

/** Scale at detonation, after weapon data and saved projectile data have been applied. */
@Mixin(value = FastThrowableProjectile.class, remap = false)
public abstract class MortarExplosionRadiusMixin {
    @Inject(method = "buildExplosion", at = @At("RETURN"))
    private void squadmc$mortarRadius(Vec3 position,
                                     CallbackInfoReturnable<CustomExplosion.Builder> cir) {
        if ((Object) this instanceof MortarShellEntity mortar) {
            cir.getReturnValue().radius(mortar.getExplosionRadius()
                    * ExplosionSettings.MORTAR_RADIUS_MULTIPLIER.get().floatValue());
        }
    }
}
