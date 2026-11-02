// Based on VehicleHudHoverShiftMixin from Frontline Combat Pack by Commissar_Mado (see FCP_LICENSE.txt).
package tech.squadmc.squadmcor.mixin;

import com.atsuishio.superbwarfare.client.overlay.VehicleHudOverlay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import tech.squadmc.squadmcor.heli.client.EngineHud;

/** Текст режима висения SBW стоит там же, где шкала двигателя, поэтому сдвигается правее неё. */
@Mixin(value = VehicleHudOverlay.class, remap = false)
public abstract class VehicleHudHoverShiftMixin {

    @ModifyArg(method = "renderHoverInfo", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/gui/GuiGraphics;drawString(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;IIIZ)I",
            remap = true), index = 2)
    private int squadmc$shiftHoverText(int x) {
        return EngineHud.hoverTextX(x);
    }
}
