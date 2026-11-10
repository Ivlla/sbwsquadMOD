package tech.squadmc.squadmcor.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tech.squadmc.squadmcor.performance.ProjectileChunkLoading;

/** Wrap the complete virtual tick, including derived rocket guidance and collision logic. */
@Mixin(ServerLevel.class)
public abstract class ProjectileTickGateMixin {
    @Inject(method = "tickNonPassenger", at = @At("HEAD"), cancellable = true)
    private void squadmc$waitForProjectileChunks(Entity entity, CallbackInfo ci) {
        if (!ProjectileChunkLoading.beforeTick(entity)) ci.cancel();
    }
}
