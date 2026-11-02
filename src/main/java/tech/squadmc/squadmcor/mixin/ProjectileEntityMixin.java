package tech.squadmc.squadmcor.mixin;

import com.atsuishio.superbwarfare.entity.projectile.ProjectileEntity;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/** Убирает искры и дым SBW при попадании пули в OBB техники: вместо них теперь эффект MTS. */
@Mixin(value = ProjectileEntity.class, remap = false)
public abstract class ProjectileEntityMixin {

    @Redirect(method = "getHitResult", at = @At(value = "INVOKE",
            target = "Lcom/atsuishio/superbwarfare/tools/ParticleTool;sendParticle(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/particles/ParticleOptions;DDDIDDDDZ)V"))
    private void squadmc$noObbParticles(ServerLevel level, ParticleOptions particle, double x, double y, double z,
                                        int count, double xOffset, double yOffset, double zOffset, double speed, boolean force) {
    }
}
