package tech.squadmc.squadmcor.client.hud;

import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
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
import tech.squadmc.squadmcor.squadmc;
import tech.squadmc.squadmcor.entity.Gaz_tigrRWSEntity;

@Mod.EventBusSubscriber(modid = "squadmc", value = Dist.CLIENT)
public class Gaz_tigrRWSReticleOverlay {

    // РџСѓС‚Рё Рє С‚СЂРµРј С‚РµРєСЃС‚СѓСЂР°Рј РЅР°РІРѕРґС‡РёРєР° (РѕС‚ РЅРёР¶РЅРµРіРѕ СЃР»РѕСЏ Рє РІРµСЂС…РЅРµРјСѓ)
    private static final ResourceLocation RWS_HUD_TEXTURE = new ResourceLocation(squadmc.MODID, "textures/overlay/tv_frame.png");
    private static final ResourceLocation GUNNER_TEXTURE = new ResourceLocation(squadmc.MODID, "textures/overlay/outline_digital-4x3.png");
    private static final ResourceLocation RWS_SCOPE_TEXTURE = new ResourceLocation(squadmc.MODID, "textures/overlay/outline_kord-rws.png");

    @SubscribeEvent
    public static void onRenderGuiPre(RenderGuiEvent.Pre event) { // Р’РѕР·РІСЂР°С‰РµРЅРѕ СЃРѕР±С‹С‚РёРµ Pre
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;

        if (player == null || mc.options.hideGui) {
            return;
        }

        Entity vehicle = player.getVehicle();
        if (!(vehicle instanceof Gaz_tigrRWSEntity rwsVehicle)) {
            return;
        }

        int seatIndex = rwsVehicle.getSeatIndex(player);
        if (seatIndex < 0) {
            return;
        }

        boolean isFirstPerson = mc.options.getCameraType().isFirstPerson();

        // РћС‚РѕР±СЂР°Р¶Р°РµРј HUD РґР»СЏ РІРѕРґРёС‚РµР»СЏ (0) Рё РЅР°РІРѕРґС‡РёРєР° (1) РІСЃРµРіРґР° РїСЂРё РІРёРґРµ РѕС‚ РїРµСЂРІРѕРіРѕ Р»РёС†Р°
        if (isFirstPerson && (seatIndex == 0 || seatIndex == 1)) {
            int screenWidth = mc.getWindow().getGuiScaledWidth();
            int screenHeight = mc.getWindow().getGuiScaledHeight();
            GuiGraphics guiGraphics = event.getGuiGraphics();

            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

            if (seatIndex == 0) {
                drawDriverHud(guiGraphics, mc, rwsVehicle, screenWidth, screenHeight);
            }
            else if (seatIndex == 1) {
                // 1. РЎР»РѕР№ 1 (РЎР°РјС‹Р№ РЅРёР¶РЅРёР№): Р¤РѕРЅРѕРІС‹Р№ HUD-РёРЅС‚РµСЂС„РµР№СЃ РјРѕРґСѓР»СЏ
                guiGraphics.blit(RWS_HUD_TEXTURE, 0, 0, 0, 0, screenWidth, screenHeight, screenWidth, screenHeight);

                // 2. РЎР»РѕР№ 2 (РЎСЂРµРґРЅРёР№): РњР°СЃРєР°/РІРёРЅСЊРµС‚РєР° РЅР°РІРѕРґС‡РёРєР° РїРѕРІРµСЂС… С„РѕРЅР°
                guiGraphics.blit(GUNNER_TEXTURE, 0, 0, 0, 0, screenWidth, screenHeight, screenWidth, screenHeight);

                // Р Р°СЃС‡РµС‚ СЂР°Р·РјРµСЂРѕРІ Рё РїСЂРѕРїРѕСЂС†РёР№ РїСЂРёС†РµР»Р° (16:9)
                float aspectRatio = 16.0F / 9.0F;
                int scopeHeight = screenHeight;
                int scopeWidth = (int) (scopeHeight * aspectRatio);

                if (scopeWidth > screenWidth) {
                    scopeWidth = screenWidth;
                    scopeHeight = (int) (scopeWidth / aspectRatio);
                }

                int x = (screenWidth - scopeWidth) / 2;
                int y = (screenHeight - scopeHeight) / 2;

                // 3. РЎР»РѕР№ 3 (Р’РµСЂС…РЅРёР№): РџСЂРёС†РµР»СЊРЅР°СЏ СЃРµС‚РєР° TOW/RWS РїРѕРІРµСЂС… РјР°СЃРєРё Рё С„РѕРЅР°
                guiGraphics.blit(RWS_SCOPE_TEXTURE, x, y, 0, 0, scopeWidth, scopeHeight, scopeWidth, scopeHeight);

                // 4. РўРµРєСЃС‚ РґР°Р»СЊРЅРѕРјРµСЂР° РїРѕРІРµСЂС… РІСЃРµС… СЃР»РѕРµРІ
                drawGunnerHud(guiGraphics, mc, player, screenWidth, screenHeight);
            }

            RenderSystem.disableBlend();
        }
    }

    private static void drawDriverHud(GuiGraphics guiGraphics, Minecraft mc, VehicleEntity vehicle, int width, int height) {
        double speed = vehicle.getDeltaMovement().horizontalDistance() * 72.0D;
        String speedText = String.format("SPEED: %.0f km/h", speed);

        int healthPercent = (int) ((vehicle.getHealth() / vehicle.getMaxHealth()) * 100);
        String healthText = String.format("HEALTH: %d%%", healthPercent);

        int color = 0x66FF00;

        guiGraphics.drawString(mc.font, speedText, 60, height - 40, color, false);
        guiGraphics.drawString(mc.font, healthText, width - 110, height - 40, color, false);
    }

    private static void drawGunnerHud(GuiGraphics guiGraphics, Minecraft mc, Player player, int width, int height) {
        int color = 0x66FF00;

        double distance = calculateDistance(player);
        String distText = distance > 500 ? "LGR: >500m" : String.format("LGR: %.0fm", distance);

        guiGraphics.drawCenteredString(mc.font, distText, width / 2, height / 2 + 50, color);
    }

    private static double calculateDistance(Player player) {
        try {
            BlockHitResult result = player.level().clip(new ClipContext(
                    player.getEyePosition(),
                    player.getEyePosition().add(player.getLookAngle().scale(512.0D)),
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
