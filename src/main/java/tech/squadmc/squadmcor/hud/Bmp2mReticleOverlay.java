package tech.squadmc.squadmcor.client.hud;

import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import com.atsuishio.superbwarfare.event.ClientEventHandler;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import tech.squadmc.squadmcor.heli.entity.vehicle.Bmp.Bmp2.BMP2MDEntity;
import tech.squadmc.squadmcor.heli.entity.vehicle.Bmp.Bmp2.BMP2MEntity;
import tech.squadmc.squadmcor.squadmc;

/**
 * The BMP-2's driver and gunner views on the BMP-2M and BMP-2MD.
 */
@Mod.EventBusSubscriber(modid = "squadmc", value = Dist.CLIENT)
public class Bmp2mReticleOverlay {

    private static final ResourceLocation TRIPLEX = new ResourceLocation(squadmc.MODID, "textures/overlay/triplex.png");
    private static final ResourceLocation GUNNER = new ResourceLocation(squadmc.MODID, "textures/overlay/gunner.png");
    private static final ResourceLocation CANNON_SCOPE = new ResourceLocation(squadmc.MODID, "textures/overlay/btr82_scope.png");
    private static final ResourceLocation ATGM_SCOPE = new ResourceLocation(squadmc.MODID, "textures/overlay/konkurs_scope.png");

    /** Position of the Konkurs in the BMP-2M gunner's weapon list. */
    private static final int BMP2M_ATGM_INDEX = 1;

    @SubscribeEvent
    public static void onRenderGuiPre(RenderGuiEvent.Pre event) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null || mc.options.hideGui) return;
        if (!(player.getVehicle() instanceof VehicleEntity vehicle)) return;
        if (!(vehicle instanceof BMP2MEntity || vehicle instanceof BMP2MDEntity)) return;
        if (!mc.options.getCameraType().isFirstPerson() && !ClientEventHandler.zoomVehicle) return;

        int seat = vehicle.getSeatIndex(player);
        boolean driver = seat == 0;
        boolean gunner = seat == vehicle.getTurretControllerIndex();
        boolean commander = vehicle instanceof BMP2MEntity && seat == 2;
        if (!driver && !gunner && !commander) return;

        int width = mc.getWindow().getGuiScaledWidth();
        int height = mc.getWindow().getGuiScaledHeight();
        GuiGraphics graphics = event.getGuiGraphics();

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        if (gunner) {
            boolean atgm = vehicle instanceof BMP2MEntity && vehicle.getWeaponIndex(seat) == BMP2M_ATGM_INDEX;
            graphics.blit(GUNNER, 0, 0, 0, 0, width, height, width, height);
            graphics.blit(atgm ? ATGM_SCOPE : CANNON_SCOPE, 0, 0, 0, 0, width, height, width, height);
            drawRange(graphics, mc, player, width, height);
        } else {
            graphics.blit(TRIPLEX, 0, 0, 0, 0, width, height, width, height);
            if (driver) drawDriverHud(graphics, mc, vehicle, width, height);
        }
        RenderSystem.disableBlend();
    }

    private static void drawDriverHud(GuiGraphics graphics, Minecraft mc, VehicleEntity vehicle, int width, int height) {
        double speed = vehicle.getDeltaMovement().horizontalDistance() * 72.0;
        int health = (int) (vehicle.getHealth() / vehicle.getMaxHealth() * 100);
        graphics.drawString(mc.font, String.format("SPEED: %.0f km/h", speed), 60, height - 40, 0x66FF00, false);
        graphics.drawString(mc.font, String.format("HEALTH: %d%%", health), width - 110, height - 40, 0x66FF00, false);
    }

    private static void drawRange(GuiGraphics graphics, Minecraft mc, Player player, int width, int height) {
        var eye = player.getEyePosition();
        var hit = player.level().clip(new ClipContext(eye, eye.add(player.getLookAngle().scale(512.0)),
                ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player));
        double distance = eye.distanceTo(hit.getLocation());
        String text = distance > 500 ? "LGR: >500m" : String.format("LGR: %.0fm", distance);
        graphics.drawCenteredString(mc.font, text, width / 2, height / 2 + 50, 0x66FF00);
    }
}
