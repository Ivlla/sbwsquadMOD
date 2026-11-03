package tech.squadmc.squadmcor.client.hud;

import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import com.atsuishio.superbwarfare.event.ClientEventHandler;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import tech.squadmc.squadmcor.squadmc;

import java.util.Map;
import java.util.Set;

/** Triplex view with speed and health, for vehicles that have no sight overlay of their own. */
@Mod.EventBusSubscriber(modid = "squadmc", value = Dist.CLIENT)
public class DriverHudOverlay {

    private static final ResourceLocation TRIPLEX = new ResourceLocation(squadmc.MODID, "textures/overlay/triplex.png");

    /** Vehicle id to the seats that get the HUD, counting from 0. */
    private static final Map<String, Set<Integer>> SEATS = Map.of(
            "mtlb", Set.of(0, 1),
            "mtlb_zu23", Set.of(0),
            "m113", Set.of(0));

    @SubscribeEvent
    public static void onRenderGuiPre(RenderGuiEvent.Pre event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.options.hideGui) return;
        if (!(mc.player.getVehicle() instanceof VehicleEntity vehicle)) return;
        Set<Integer> seats = SEATS.get(EntityType.getKey(vehicle.getType()).getPath());
        if (seats == null || !seats.contains(vehicle.getSeatIndex(mc.player))) return;
        if (!mc.options.getCameraType().isFirstPerson() && !ClientEventHandler.zoomVehicle) return;

        int width = mc.getWindow().getGuiScaledWidth();
        int height = mc.getWindow().getGuiScaledHeight();
        GuiGraphics graphics = event.getGuiGraphics();

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        graphics.blit(TRIPLEX, 0, 0, 0, 0, width, height, width, height);
        double speed = vehicle.getDeltaMovement().horizontalDistance() * 72.0;
        int health = (int) (vehicle.getHealth() / vehicle.getMaxHealth() * 100);
        graphics.drawString(mc.font, String.format("SPEED: %.0f km/h", speed), 60, height - 40, 0x66FF00, false);
        graphics.drawString(mc.font, String.format("HEALTH: %d%%", health), width - 110, height - 40, 0x66FF00, false);
        RenderSystem.disableBlend();
    }
}
