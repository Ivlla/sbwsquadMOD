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
import tech.squadmc.squadmcor.entity.StrykerMortalEntity;

import java.lang.reflect.Field;

@Mod.EventBusSubscriber(modid = "squadmc", value = Dist.CLIENT)
public class StrykerMortarReticleOverlay {

    private static final ResourceLocation GUNNER_TEXTURE = new ResourceLocation("squadmc", "textures/overlay/gunner.png");
    private static final ResourceLocation TRIPLEX_TEXTURE = new ResourceLocation("squadmc", "textures/overlay/triplex.png");

    private static Field turretXRotField = null;
    private static Field turretYRotField = null;
    private static boolean reflectionSearched = false;

    @SubscribeEvent
    public static void onRenderGuiPre(RenderGuiEvent.Pre event) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;

        if (player == null || mc.options.hideGui) {
            return;
        }

        Entity vehicle = player.getVehicle();
        if (!(vehicle instanceof StrykerMortalEntity strykerMortal)) {
            return;
        }

        int seatIndex = strykerMortal.getSeatIndex(player);
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
            // Р’РѕРґРёС‚РµР»СЊ (1 РјРµСЃС‚Рѕ) вЂ” РїРѕРєР°Р·С‹РІР°РµРј С‚СЂРёРїР»РµРєСЃ, СЃРєРѕСЂРѕСЃС‚СЊ Рё Р·РґРѕСЂРѕРІСЊРµ
            guiGraphics.blit(TRIPLEX_TEXTURE, 0, 0, 0, 0, screenWidth, screenHeight, screenWidth, screenHeight);
            drawDriverHud(guiGraphics, mc, strykerMortal, screenWidth, screenHeight);
        } else if (seatIndex == 1) {
            // РќР°РІРѕРґС‡РёРє (2 РјРµСЃС‚Рѕ) вЂ” РїРѕРєР°Р·С‹РІР°РµРј РїСЂРёС†РµР» Рё Р±Р°Р»Р»РёСЃС‚РёС‡РµСЃРєРёР№ РєР°Р»СЊРєСѓР»СЏС‚РѕСЂ
            guiGraphics.blit(GUNNER_TEXTURE, 0, 0, 0, 0, screenWidth, screenHeight, screenWidth, screenHeight);
            drawStrykerHud(guiGraphics, mc, strykerMortal, player, screenWidth, screenHeight);
        }
        RenderSystem.disableBlend();
    }

    private static void drawDriverHud(GuiGraphics guiGraphics, Minecraft mc, StrykerMortalEntity stryker_mortar, int width, int height) {
        double speed = stryker_mortar.getDeltaMovement().horizontalDistance() * 72.0D;
        String speedText = String.format("SPEED: %.0f km/h", speed);

        int healthPercent = (int) ((stryker_mortar.getHealth() / stryker_mortar.getMaxHealth()) * 100);
        String healthText = String.format("HEALTH: %d%%", healthPercent);

        int color = 0x1B8C31;

        guiGraphics.drawString(mc.font, speedText, 60, height - 40, color, false);
        guiGraphics.drawString(mc.font, healthText, width - 110, height - 40, color, false);
    }

    private static void drawStrykerHud(GuiGraphics guiGraphics, Minecraft mc, StrykerMortalEntity stryker_mortar, Player player, int width, int height) {
        int color = 0x1B8C31;

        // РџРѕР»СѓС‡Р°РµРј СѓРіР»С‹ РЅР°РєР»РѕРЅР° Рё РїРѕРІРѕСЂРѕС‚Р° С‚СѓСЂРµР»Рё С‡РµСЂРµР· СЂРµС„Р»РµРєСЃРёСЋ
        float pitch = getTurretXRot(stryker_mortar);
        float yaw = getTurretYRot(stryker_mortar);

        // Р”РёРЅР°РјРёС‡РµСЃРєРё РїРѕР»СѓС‡Р°РµРј С„РёР·РёС‡РµСЃРєРёРµ С…Р°СЂР°РєС‚РµСЂРёСЃС‚РёРєРё С‚РµРєСѓС‰РµРіРѕ РІРѕРѕСЂСѓР¶РµРЅРёСЏ
        GunData gunData = stryker_mortar.getGunData(player);
        double velocity = gunData != null ? gunData.compute().velocity : 16.0D;
        double gravity = gunData != null ? gunData.compute().getGravity() : 0.15D;

        // Р Р°СЃСЃС‡РёС‚С‹РІР°РµРј С‚РѕС‡РЅСѓСЋ РґРёСЃС‚Р°РЅС†РёСЋ РїРѕР»РµС‚Р° РІСЃС‚СЂРѕРµРЅРЅС‹РјРё СЃСЂРµРґСЃС‚РІР°РјРё РјРѕРґР°
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

    private static float getTurretXRot(StrykerMortalEntity vehicle) {
        ensureReflectionInit();
        try {
            if (turretXRotField != null) {
                return turretXRotField.getFloat(vehicle);
            }
        } catch (Exception ignored) {}
        return vehicle.getXRot();
    }

    private static float getTurretYRot(StrykerMortalEntity vehicle) {
        ensureReflectionInit();
        try {
            if (turretYRotField != null) {
                return turretYRotField.getFloat(vehicle);
            }
        } catch (Exception ignored) {}
        return vehicle.getYRot();
    }

    private static void ensureReflectionInit() {
        if (reflectionSearched) {
            return;
        }
        reflectionSearched = true;
        try {
            Class<?> vehicleClass = com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity.class;

            turretXRotField = vehicleClass.getDeclaredField("turretXRot");
            turretXRotField.setAccessible(true);

            turretYRotField = vehicleClass.getDeclaredField("turretYRot");
            turretYRotField.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
