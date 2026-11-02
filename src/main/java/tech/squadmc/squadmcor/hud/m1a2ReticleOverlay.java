package tech.squadmc.squadmcor.hud;

import com.atsuishio.superbwarfare.event.ClientEventHandler;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import tech.squadmc.squadmcor.entity.m1a2Entity;

@Mod.EventBusSubscriber(modid = "squadmc", value = Dist.CLIENT)
public class m1a2ReticleOverlay {

    private static final ResourceLocation TRIPLEX_TEXTURE = new ResourceLocation("squadmc", "textures/overlay/triplex.png");
    private static final ResourceLocation GUNNER_TEXTURE = new ResourceLocation("squadmc", "textures/overlay/gunner.png");
    private static final ResourceLocation ABRAMS_SCOPE = new ResourceLocation("squadmc", "textures/overlay/abrams_main.png");

    private static final ResourceLocation CROWS_HUD_TEXTURE = new ResourceLocation("squadmc", "textures/overlay/outline_stryker-rws.png");
    private static final ResourceLocation RWS_TEXTURE = new ResourceLocation("squadmc", "textures/overlay/outline_digital-4x3.png");
    private static final ResourceLocation RWS_HUD = new ResourceLocation("squadmc", "textures/overlay/tv_frame.png");     // РЎР»РѕР№ 3: РџСЂРёС†РµР»СЊРЅР°СЏ СЃРµС‚РєР°

    @SubscribeEvent
    public static void onRenderGuiPre(RenderGuiEvent.Pre event) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;

        if (player == null || mc.options.hideGui) {
            return;
        }

        Entity vehicle = player.getVehicle();
        if (!(vehicle instanceof m1a2Entity m1a2)) {
            return;
        }

        int seatIndex = m1a2.getSeatIndex(player);
        if (seatIndex < 0) {
            return;
        }

        boolean isFirstPerson = mc.options.getCameraType().isFirstPerson();
        boolean isZooming = ClientEventHandler.zoomVehicle;

        if (isFirstPerson || isZooming) {
            int screenWidth = mc.getWindow().getGuiScaledWidth();
            int screenHeight = mc.getWindow().getGuiScaledHeight();
            GuiGraphics guiGraphics = event.getGuiGraphics();

            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

            if (seatIndex == 0) {
                guiGraphics.blit(TRIPLEX_TEXTURE, 0, 0, 0, 0, screenWidth, screenHeight, screenWidth, screenHeight);
                drawDriverHud(guiGraphics, mc, m1a2, screenWidth, screenHeight);
            } else if (seatIndex == 1) {
                guiGraphics.blit(GUNNER_TEXTURE, 0, 0, 0, 0, screenWidth, screenHeight, screenWidth, screenHeight);
                guiGraphics.blit(ABRAMS_SCOPE, 0, 0, 0, 0, screenWidth, screenHeight, screenWidth, screenHeight);
                drawGunnerHud(guiGraphics, mc, player, screenWidth, screenHeight);
            } else if (seatIndex == 2) {

                guiGraphics.blit(RWS_HUD, 0, 0, 0, 0, screenWidth, screenHeight, screenWidth, screenHeight);

                guiGraphics.blit(RWS_TEXTURE, 0, 0, 0, 0, screenWidth, screenHeight, screenWidth, screenHeight);

                // 1. РЎР»РѕР№ 1 (РЎР°РјС‹Р№ РЅРёР¶РЅРёР№): Р¤РѕРЅРѕРІС‹Р№ РёРЅС‚РµСЂС„РµР№СЃ РјРѕРЅРёС‚РѕСЂР° CROWS
                guiGraphics.blit(CROWS_HUD_TEXTURE, 0, 0, 0, 0, screenWidth, screenHeight, screenWidth, screenHeight);

                // 2. РЎР»РѕР№ 2 (РЎСЂРµРґРЅРёР№): РњР°СЃРєР°/РІРёРЅСЊРµС‚РєР° РїРѕРІРµСЂС… С„РѕРЅР°



                // Р Р°СЃС‡РµС‚ СЂР°Р·РјРµСЂРѕРІ Рё РїСЂРѕРїРѕСЂС†РёР№ РїСЂРёС†РµР»СЊРЅРѕР№ СЃРµС‚РєРё (16:9)
                float aspectRatio = 16.0F / 9.0F;
                int scopeHeight = screenHeight;
                int scopeWidth = (int) (scopeHeight * aspectRatio);

                if (scopeWidth > screenWidth) {
                    scopeWidth = screenWidth;
                    scopeHeight = (int) (scopeWidth / aspectRatio);
                }

                int x = (screenWidth - scopeWidth) / 2;
                int y = (screenHeight - scopeHeight) / 2;



                // 4. РўРµРєСЃС‚ РґР°Р»СЊРЅРѕРјРµСЂР° РїРѕРІРµСЂС… РІСЃРµС… СЃР»РѕРµРІ РїСЂРёС†РµР»Р°
                drawCommanderHud(guiGraphics, mc, m1a2, player, screenWidth, screenHeight);
            }

            RenderSystem.disableBlend();
        }
    }

    private static void drawDriverHud(GuiGraphics guiGraphics, Minecraft mc, m1a2Entity m1a2, int width, int height) {
        double speed = m1a2.getDeltaMovement().horizontalDistance() * 72.0D;
        String speedText = String.format("SPD: %.0f mph", speed * 0.621371D); // Р’С‹РІРѕРґ РІ РјРёР»СЏС… РґР»СЏ Р°РјРµСЂРёРєР°РЅСЃРєРѕР№ С‚РµС…РЅРёРєРё

        int healthPercent = (int) ((m1a2.getHealth() / m1a2.getMaxHealth()) * 100);
        String healthText = String.format("SYS: %d%%", healthPercent);

        int color = 0x00FFCC; // Р“РѕР»СѓР±РѕРІР°С‚С‹Р№ С†РІРµС‚ HUD

        guiGraphics.drawString(mc.font, speedText, 60, height - 40, color, false);
        guiGraphics.drawString(mc.font, healthText, width - 110, height - 40, color, false);
    }

    private static void drawCommanderHud(GuiGraphics guiGraphics, Minecraft mc, m1a2Entity m1a2, Player player, int width, int height) {
        int color = 0x00FFCC;
        double distance = calculateDistance(player);
        String distText = distance > 1200 ? "LRF: >1200m" : String.format("LRF: %.0fm", distance);
        guiGraphics.drawCenteredString(mc.font, distText, width / 2, height / 2 + 50, color);
    }

    private static void drawGunnerHud(GuiGraphics guiGraphics, Minecraft mc, Player player, int width, int height) {
        int color = 0x00FFCC;
        double distance = calculateDistance(player);
        String distText = distance > 1200 ? "LRF: >1200m" : String.format("LRF: %.0fm", distance);
        guiGraphics.drawCenteredString(mc.font, distText, width / 2, height / 2 + 50, color);
    }

    private static double calculateDistance(Player player) {
        try {
            BlockHitResult result = player.level().clip(new ClipContext(
                    player.getEyePosition(),
                    player.getEyePosition().add(player.getLookAngle().scale(1200.0D)),
                    ClipContext.Block.OUTLINE,
                    ClipContext.Fluid.NONE,
                    player
            ));
            return player.getEyePosition().distanceTo(result.getLocation());
        } catch (Exception e) {
            return 999.0D;
        }
    }
}
