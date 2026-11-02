package tech.squadmc.squadmcor.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import tech.squadmc.squadmcor.client.thermal.ThermalVision;

/** В тепловизоре свет сущности заменён её нагревом. */
@Mixin(LevelRenderer.class)
public abstract class ThermalEntityLightMixin {

    @ModifyArg(method = "renderEntity(Lnet/minecraft/world/entity/Entity;DDDFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;)V",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/entity/EntityRenderDispatcher;render(Lnet/minecraft/world/entity/Entity;DDDFFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"),
            index = 8)
    private int squadmc$heatAsLight(Entity entity, double x, double y, double z, float yaw, float partialTick,
                                    PoseStack pose, MultiBufferSource buffers, int light) {
        if (!ThermalVision.isActive()) return light;
        return LightTexture.pack(Math.round(ThermalVision.heatOf(entity) * 15F), 0);
    }
}
