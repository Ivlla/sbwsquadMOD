package tech.squadmc.squadmcor.mixin;

import com.atsuishio.superbwarfare.tools.ParticleTool;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Отключает частицы и звуки взрыва SBW: их заменяют эффекты MTS. Тряска камеры остаётся от SBW. */
@Mixin(value = ParticleTool.class, remap = false)
public abstract class ParticleToolMixin {

    @Inject(method = "spawnExplosionParticlesClient", at = @At("HEAD"), cancellable = true)
    private static void squadmc$noSbwParticles(ParticleTool.ParticleType type, Level level, Vec3 pos, CallbackInfo ci) {
        ci.cancel();
    }

    // В Kotlin-объекте private fun без @JvmStatic компилируется в метод экземпляра, поэтому обработчик не static.
    @Inject(method = "playExplosionSounds", at = @At("HEAD"), cancellable = true)
    private void squadmc$noSbwSounds(ParticleTool.ParticleType type, ServerLevel level, Vec3 pos, CallbackInfo ci) {
        ci.cancel();
    }
}
