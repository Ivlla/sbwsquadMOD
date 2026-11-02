package tech.squadmc.squadmcor.mixin;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.renderer.LightTexture;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tech.squadmc.squadmcor.client.thermal.ThermalVision;

/**
 * Лайтмапа прибора. Мир серый и ровный. Столбец без неба (x — блок, y — небо) отдан теплу: красный
 * полный, зелёный равен нагреву, синего нет. Клетка полного освещения тоже горячая, в ней яркие
 * частицы вроде вспышек.
 */
@Mixin(LightTexture.class)
public abstract class ThermalLightTextureMixin {
    @Shadow @Final private NativeImage lightPixels;

    private static final int SQUADMC$COLD = 0xFF8C8C8C;

    @Inject(method = "updateLightTexture", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/texture/DynamicTexture;upload()V"))
    private void squadmc$thermalLight(float partialTicks, CallbackInfo ci) {
        if (!ThermalVision.isActive()) return;
        for (int sky = 1; sky < 16; sky++) {
            for (int block = 0; block < 16; block++) {
                this.lightPixels.setPixelRGBA(block, sky, SQUADMC$COLD);
            }
        }
        for (int block = 0; block < 16; block++) {
            this.lightPixels.setPixelRGBA(block, 0, squadmc$heatPixel(block));
        }
        this.lightPixels.setPixelRGBA(15, 15, squadmc$heatPixel(15));
    }

    /** Формат пикселя ABGR. */
    private static int squadmc$heatPixel(int level) {
        return 0xFF000000 | (level * 17) << 8 | 0xFF;
    }
}
