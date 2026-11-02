package tech.squadmc.squadmcor.client.hud;

import com.atsuishio.superbwarfare.data.gun.GunData;
import com.atsuishio.superbwarfare.tools.FormatTool;
import com.atsuishio.superbwarfare.tools.RangeTool;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import tech.squadmc.squadmcor.entity.UralGradEntity;

@Mod.EventBusSubscriber(modid = "squadmc", value = Dist.CLIENT)
public class UralGradReticleOverlay {

    private static final ResourceLocation GUNNER = new ResourceLocation("squadmc", "textures/overlay/gunner.png");

    @SubscribeEvent
    public static void onRenderGuiPre(RenderGuiEvent.Pre event) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;

        if (player == null || mc.options.hideGui) {
            return;
        }

        Entity vehicle = player.getVehicle();
        if (!(vehicle instanceof UralGradEntity grad)) {
            return;
        }

        int seatIndex = grad.getSeatIndex(player);
        if (seatIndex < 0) {
            return;
        }

        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int screenHeight = mc.getWindow().getGuiScaledHeight();
        GuiGraphics guiGraphics = event.getGuiGraphics();

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        if (seatIndex == 0) {
            // Р’РѕРґРёС‚РµР»СЊ (1 РјРµСЃС‚Рѕ, РёРЅРґРµРєСЃ 0) вЂ” РїРѕРєР°Р·С‹РІР°РµРј СЃРєРѕСЂРѕСЃС‚СЊ Рё РїСЂРѕС‡РЅРѕСЃС‚СЊ
            drawDriverHud(guiGraphics, mc, grad, screenWidth, screenHeight);
        } else if (seatIndex == 1) {
            // РќР°РІРѕРґС‡РёРє (2 РјРµСЃС‚Рѕ, РёРЅРґРµРєСЃ 1) вЂ” РїРѕРєР°Р·С‹РІР°РµРј РїСЂРёС†РµР» Рё Р±Р°Р»Р»РёСЃС‚РёС‡РµСЃРєРёР№ РєР°Р»СЊРєСѓР»СЏС‚РѕСЂ
            guiGraphics.blit(GUNNER, 0, 0, 0, 0, screenWidth, screenHeight, screenWidth, screenHeight);
            drawGradHud(guiGraphics, mc, grad, player, screenWidth, screenHeight);
        }
        RenderSystem.disableBlend();
    }

    private static void drawDriverHud(GuiGraphics guiGraphics, Minecraft mc, UralGradEntity grad, int width, int height) {
        double speed = grad.getDeltaMovement().horizontalDistance() * 72.0D;
        String speedText = String.format("SPEED: %.0f km/h", speed);

        int healthPercent = (int) ((grad.getHealth() / grad.getMaxHealth()) * 100);
        String healthText = String.format("HEALTH: %d%%", healthPercent);

        int color = 0xFF3300; // РљСЂР°СЃРЅС‹Р№ С†РІРµС‚ РёРЅС‚РµСЂС„РµР№СЃР° "Р“СЂР°РґР°"

        guiGraphics.drawString(mc.font, speedText, 60, height - 40, color, false);
        guiGraphics.drawString(mc.font, healthText, width - 110, height - 40, color, false);
    }

    private static void drawGradHud(GuiGraphics guiGraphics, Minecraft mc, UralGradEntity grad, Player player, int width, int height) {
        int color = 0xFF3300; // РљСЂР°СЃРЅС‹Р№ С†РІРµС‚ РёРЅС‚РµСЂС„РµР№СЃР° "Р“СЂР°РґР°"

        // РџРѕР»СѓС‡Р°РµРј СѓРіР»С‹ РЅР°РєР»РѕРЅР° Рё РїРѕРІРѕСЂРѕС‚Р° С‚СѓСЂРµР»Рё РёР· СЃСѓС‰РЅРѕСЃС‚Рё UralGradEntity
        float pitch = grad.getTurretXRot();
        float yaw = grad.getTurretYRot();

        // Р”РёРЅР°РјРёС‡РµСЃРєРё РїРѕР»СѓС‡Р°РµРј С„РёР·РёС‡РµСЃРєРёРµ С…Р°СЂР°РєС‚РµСЂРёСЃС‚РёРєРё С‚РµРєСѓС‰РµРіРѕ РІРѕРѕСЂСѓР¶РµРЅРёСЏ РёР· grad.json
        GunData gunData = grad.getGunData(player);
        double velocity = gunData != null ? gunData.compute().velocity : 16.0D;
        double gravity = gunData != null ? gunData.compute().getGravity() : 0.15D;

        // Р Р°СЃСЃС‡РёС‚С‹РІР°РµРј РґРёСЃС‚Р°РЅС†РёСЋ РїРѕР»РµС‚Р° РІСЃС‚СЂРѕРµРЅРЅС‹РјРё СЃСЂРµРґСЃС‚РІР°РјРё РјРѕРґР°
        double range = RangeTool.getRange((double)(-pitch), velocity, gravity);

        String angleText = String.format("ELEVATION: %s", FormatTool.format2D((double)(-pitch), "В°"));
        String yawText = String.format("AZIMUTH: %s", FormatTool.format2D((double)(-yaw), "В°"));
        String rangeText = range <= 0 ? "EST. RANGE: ---" : String.format("EST. RANGE: %s", FormatTool.format1D((double)Math.max((int)range, 0), "m"));

        int centerX = width / 2;
        int centerY = height / 2;

        guiGraphics.drawString(mc.font, angleText, centerX - 140, centerY + 80, color, true);
        guiGraphics.drawString(mc.font, yawText, centerX - 140, centerY + 92, color, true);
        guiGraphics.drawString(mc.font, rangeText, centerX + 40, centerY + 80, color, true);
    }
}
