package tech.squadmc.squadmcor.mixin;

import com.atsuishio.superbwarfare.tools.ParticleTool;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tech.squadmc.squadmcor.fx.MtsFxServer;

/**
 * SBW шлёт этот пакет, чтобы показать взрыв на клиентах. Вместо него отправляем эффект MTS:
 * частицы и звук. Тряска камеры остаётся от SBW.
 */
@Mixin(targets = "com.atsuishio.superbwarfare.network.message.receive.ExplosionParticleMessage$Companion", remap = false)
public abstract class ExplosionParticleMessageMixin {

    @Inject(method = "sendToNearbyPlayers", at = @At("HEAD"), cancellable = true)
    private void squadmc$sendMtsFx(ServerLevel level, ParticleTool.ParticleType type, Vec3 pos, CallbackInfo ci) {
        MtsFxServer.explosion(level, pos, type);
        ci.cancel();
    }
}
