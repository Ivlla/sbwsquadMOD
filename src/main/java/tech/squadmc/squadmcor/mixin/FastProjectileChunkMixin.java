package tech.squadmc.squadmcor.mixin;

import com.atsuishio.superbwarfare.entity.projectile.FastThrowableProjectile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tech.squadmc.squadmcor.performance.ProjectileChunkLoading;

/** Avoid SBW's second loader and its out-of-height manual ticks bypassing the tick gate. SBW 0.8.9.2. */
@Mixin(FastThrowableProjectile.class)
public abstract class FastProjectileChunkMixin {
    @Inject(method = "isAlwaysTicking", at = @At("HEAD"), cancellable = true)
    private void squadmc$useTicketTicking(CallbackInfoReturnable<Boolean> cir) {
        if (!((FastThrowableProjectile) (Object) this).level().isClientSide && ProjectileChunkLoading.enabled())
            cir.setReturnValue(false);
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", remap = false,
            target = "Lcom/atsuishio/superbwarfare/entity/projectile/FastThrowableProjectile;forceLoadChunk()Z"))
    private boolean squadmc$useSharedLoader(FastThrowableProjectile projectile) {
        return !ProjectileChunkLoading.enabled() && projectile.forceLoadChunk();
    }

    @Inject(method = "updateManualTickRegistration", remap = false, at = @At("HEAD"), cancellable = true)
    private void squadmc$useNormalEntityTicks(CallbackInfo ci) {
        if (!((FastThrowableProjectile) (Object) this).level().isClientSide && ProjectileChunkLoading.enabled()) ci.cancel();
    }
}
