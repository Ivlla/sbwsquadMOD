// Based on VehicleHudEngineBarMixin from Frontline Combat Pack by Commissar_Mado (see FCP_LICENSE.txt).
// Modified for SquadMC: the key label is replaced by the engine icon.
package tech.squadmc.squadmcor.mixin;

import com.atsuishio.superbwarfare.client.RenderHelper;
import com.atsuishio.superbwarfare.client.overlay.RenderContext;
import com.atsuishio.superbwarfare.client.overlay.VehicleHudOverlay;
import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tech.squadmc.squadmcor.heli.client.EngineHud;
import tech.squadmc.squadmcor.heli.client.EngineKeyHandler;
import tech.squadmc.squadmcor.heli.entity.vehicle.engine.EngineControl;

/**
 * Иконка и шкала двигателя в строке здоровья HUD SBW, справа от шкалы корпуса, поэтому ничего не
 * перекрывают. Иконка красная при выключенном двигателе, жёлтая при запуске и белая при работе.
 */
@Mixin(value = VehicleHudOverlay.class, remap = false)
public abstract class VehicleHudEngineBarMixin {
    @Shadow @Final private static ResourceLocation VALUE_BAR;
    @Shadow @Final private static ResourceLocation VALUE_FRAME;

    @Shadow
    private int getArmorPlateCompatHeight(Player player) {
        throw new AssertionError();
    }

    @Inject(method = "render", at = @At(value = "INVOKE",
            target = "Lcom/atsuishio/superbwarfare/client/overlay/VehicleHudOverlay;renderWeaponInfo(Lnet/minecraft/client/gui/GuiGraphics;Lcom/atsuishio/superbwarfare/entity/vehicle/base/VehicleEntity;II)V"))
    private void squadmc$engineBar(RenderContext ctx, CallbackInfo ci) {
        Player player = ctx.getPlayer();
        if (!EngineHud.shown(player)) return;
        VehicleEntity vehicle = (VehicleEntity) player.getVehicle();

        GuiGraphics g = ctx.getGuiGraphics();
        int y = ctx.getScreenHeight() - getArmorPlateCompatHeight(player);
        int x = EngineHud.X;
        byte state = EngineControl.state(vehicle);
        float fill = switch (state) {
            case EngineControl.OFF -> 0F;
            case EngineControl.STARTING -> EngineKeyHandler.startProgress(vehicle, ctx.getPartialTick());
            default -> 1F;
        };

        if (state == EngineControl.OFF) RenderSystem.setShaderColor(1F, 0.15F, 0.15F, 1F);
        else if (state == EngineControl.STARTING) RenderSystem.setShaderColor(1F, 0.8F, 0.2F, 1F);
        RenderHelper.preciseBlit(g, EngineHud.ICON, x, y - 13, 100F, 0F, 0F, EngineHud.ICON_SIZE, EngineHud.ICON_SIZE,
                EngineHud.ICON_SIZE, EngineHud.ICON_SIZE);
        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);

        float barX = x + EngineHud.ICON_SIZE + 2;
        squadmc$blitShort(g, VALUE_FRAME, barX, y - 12, 1F);
        if (fill > 0F) squadmc$blitShort(g, VALUE_BAR, barX, y - 12, fill);
        RenderSystem.enableBlend();
    }

    /** Укороченная текстура шкалы 60x6: левая и правая половины, чтобы концы остались чёткими. */
    private static void squadmc$blitShort(GuiGraphics g, ResourceLocation texture, float x, float y, float fill) {
        float half = EngineHud.BAR_WIDTH / 2F;
        float width = (int) (EngineHud.BAR_WIDTH * fill);
        float left = Math.min(width, half);
        float right = width - left;
        if (left > 0F) RenderHelper.preciseBlit(g, texture, x, y, 100F, 0F, 0F, left, 6F, 60F, 6F);
        if (right > 0F) RenderHelper.preciseBlit(g, texture, x + half, y, 100F, 60F - half, 0F, right, 6F, 60F, 6F);
    }
}
