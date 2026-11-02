package tech.squadmc.squadmcor.client.hud;

import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import tech.squadmc.squadmcor.squadmc;
import tech.squadmc.squadmcor.entity.FMTVEntity;

@Mod.EventBusSubscriber(modid = "squadmc", value = Dist.CLIENT)
public class FMTVReticleOverlay {

    @SubscribeEvent
    public static void onRenderGuiPre(RenderGuiEvent.Pre event) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;

        if (player == null || mc.options.hideGui) {
            return;
        }

        Entity vehicle = player.getVehicle();

        if (!(vehicle instanceof FMTVEntity)) {
            return;
        }

        // РџСЂРёРІРѕРґРёРј СЃСѓС‰РЅРѕСЃС‚СЊ Рє Р±Р°Р·РѕРІРѕРјСѓ РєР»Р°СЃСЃСѓ С‚СЂР°РЅСЃРїРѕСЂС‚Р° SBW
        if (!(vehicle instanceof VehicleEntity vehicleEntity)) {
            return;
        }

        int seatIndex = vehicleEntity.getSeatIndex(player);
        if (seatIndex < 0) {
            return;
        }

        boolean isFirstPerson = mc.options.getCameraType().isFirstPerson();

        // РћС‚РѕР±СЂР°Р¶Р°РµРј HUD С‚РѕР»СЊРєРѕ РїСЂРё РІРёРґРµ РѕС‚ РїРµСЂРІРѕРіРѕ Р»РёС†Р°
        if (isFirstPerson) {
            int screenWidth = mc.getWindow().getGuiScaledWidth();
            int screenHeight = mc.getWindow().getGuiScaledHeight();
            GuiGraphics guiGraphics = event.getGuiGraphics();

            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

            if (seatIndex == 0) {
                // РћС‚СЂРёСЃРѕРІРєР° С‚РµРєСЃС‚РѕРІС‹С… РїРѕРєР°Р·Р°С‚РµР»РµР№ СЃРєРѕСЂРѕСЃС‚Рё Рё Р·РґРѕСЂРѕРІСЊСЏ
                drawDriverHud(guiGraphics, mc, vehicleEntity, screenWidth, screenHeight);
            }

            RenderSystem.disableBlend();
        }
    }

    private static void drawDriverHud(GuiGraphics guiGraphics, Minecraft mc, VehicleEntity vehicle, int width, int height) {
        // Р Р°СЃС‡РµС‚ СЃРєРѕСЂРѕСЃС‚Рё
        double speed = vehicle.getDeltaMovement().horizontalDistance() * 72.0D;
        String speedText = String.format("SPEED: %.0f km/h", speed);

        // Р Р°СЃС‡РµС‚ Р·РґРѕСЂРѕРІСЊСЏ РІ РїСЂРѕС†РµРЅС‚Р°С…
        int healthPercent = (int) ((vehicle.getHealth() / vehicle.getMaxHealth()) * 100);
        String healthText = String.format("HEALTH: %d%%", healthPercent);

        int color = 0x66FF00; // Р—РµР»РµРЅС‹Р№ С†РІРµС‚ С‚РµРєСЃС‚Р° HUD

        // Р’С‹РІРѕРґ РёРЅС„РѕСЂРјР°С†РёРё РЅР° СЌРєСЂР°РЅ (СЃРєРѕСЂРѕСЃС‚СЊ СЃР»РµРІР°, Р·РґРѕСЂРѕРІСЊРµ СЃРїСЂР°РІР°)
        guiGraphics.drawString(mc.font, speedText, 60, height - 40, color, false);
        guiGraphics.drawString(mc.font, healthText, width - 110, height - 40, color, false);
    }
}
