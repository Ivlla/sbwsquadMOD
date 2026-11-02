package tech.squadmc.squadmcor.mixin;

import com.atsuishio.superbwarfare.client.overlay.weapon.HelicopterHud;
import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Стрелкам вертолёта вместо приборной панели пилота только прицел. */
@Mixin(value = HelicopterHud.class, remap = false)
public abstract class HelicopterHudGunnerMixin {
    private static final ResourceLocation SQUADMC$CROSSHAIR =
            new ResourceLocation("superbwarfare", "textures/overlay/vehicle/crosshair/third_camera_2.png");

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void squadmc$gunnerHud(VehicleEntity vehicle, Player player, ForgeGui gui, GuiGraphics graphics,
                                   float partialTick, int width, int height, CallbackInfo ci) {
        int seat = vehicle.getSeatIndex(player);
        if (seat == 0 || (seat == vehicle.getTurretControllerIndex() && vehicle.hasTurret())) return;
        ci.cancel();
        if (vehicle.getGunData(seat) == null) return;
        RenderSystem.enableBlend();
        graphics.blit(SQUADMC$CROSSHAIR, width / 2 - 8, height / 2 - 8, 0, 0, 16, 16, 16, 16);
    }
}
