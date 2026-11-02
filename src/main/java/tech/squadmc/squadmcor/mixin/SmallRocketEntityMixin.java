package tech.squadmc.squadmcor.mixin;

import com.atsuishio.superbwarfare.entity.projectile.SmallRocketEntity;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tech.squadmc.squadmcor.heli.HeliClientFx;

/**
 * След НУРСа из MTS вместо следа SBW. Это только визуал, поэтому require = 0:
 * если SBW поменяет метод, игра запустится, просто вернётся след SBW.
 */
@Mixin(SmallRocketEntity.class)
public abstract class SmallRocketEntityMixin {

    @Redirect(method = "tick", require = 0, at = @At(value = "INVOKE", remap = false,
            target = "Lcom/atsuishio/superbwarfare/entity/projectile/SmallRocketEntity;smallTrail()V"))
    private void squadmc$noSbwTrail(SmallRocketEntity self) {
    }

    @Redirect(method = "tick", require = 0, at = @At(value = "INVOKE", remap = false,
            target = "Lcom/atsuishio/superbwarfare/tools/ParticleTool;sendParticle(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/particles/ParticleOptions;DDDIDDDDZ)V"))
    private void squadmc$noSbwPuff(ServerLevel level, ParticleOptions particle, double x, double y, double z,
                                   int count, double xOffset, double yOffset, double zOffset, double speed, boolean force) {
    }

    @Inject(method = "tick", require = 0, at = @At("TAIL"))
    private void squadmc$mtsTrail(CallbackInfo ci) {
        SmallRocketEntity self = (SmallRocketEntity) (Object) this;
        if (self.level().isClientSide() && !self.isRemoved()) {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> HeliClientFx.rocketTrail(self));
        }
    }
}
