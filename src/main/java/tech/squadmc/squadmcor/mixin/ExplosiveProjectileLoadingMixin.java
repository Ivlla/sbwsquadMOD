package tech.squadmc.squadmcor.mixin;

import com.atsuishio.superbwarfare.config.server.ProjectileConfig;
import com.atsuishio.superbwarfare.entity.projectile.FastThrowableProjectile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = FastThrowableProjectile.class, remap = false)
public abstract class ExplosiveProjectileLoadingMixin {
    // Opt explosive rounds inheriting the base default into SBW's bounded chunk queue.
    @Inject(method = "forceLoadChunk()Z", at = @At("RETURN"), cancellable = true)
    private void squadmc$loadExplosiveFlightPath(CallbackInfoReturnable<Boolean> result) {
        FastThrowableProjectile shot = (FastThrowableProjectile) (Object) this;
        if (!shot.level().isClientSide && !shot.isRemoved()
                && (shot.getExplosionRadius() > 0 || shot.getExplosionDamage() > 0)
                && ProjectileConfig.PROJECTILE_CHUNK_LOADING.get()) {
            result.setReturnValue(true);
        }
    }
}
