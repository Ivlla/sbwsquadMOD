package tech.squadmc.squadmcor.client.hud;

import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
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
import tech.squadmc.squadmcor.squadmc;
import tech.squadmc.squadmcor.entity.HumveeTowEntity;

@Mod.EventBusSubscriber(modid = "squadmc", value = Dist.CLIENT)
public class HumveeTowReticleOverlay {

    private static final ResourceLocation GUNNER_TEXTURE = new ResourceLocation(squadmc.MODID, "textures/overlay/gunner.png");
    private static final ResourceLocation TOW_SCOPE_TEXTURE = new ResourceLocation(squadmc.MODID, "textures/overlay/tow_cross.png");

    @SubscribeEvent
    public static void onRenderGuiPre(RenderGuiEvent.Pre event) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;

        if (player == null || mc.options.hideGui) {
            return;
        }

        Entity vehicle = player.getVehicle();
        if (!(vehicle instanceof HumveeTowEntity tow)) {
            return;
        }

        int seatIndex = tow.getSeatIndex(player);
        if (seatIndex < 0) {
            return;
        }

        boolean isFirstPerson = mc.options.getCameraType().isFirstPerson();
        boolean isZooming = ClientEventHandler.zoomVehicle;

        // Р’РѕРґРёС‚РµР»СЊ РІРёРґРёС‚ СЃРІРѕР№ HUD РІ РїРµСЂРІРѕРј Р»РёС†Рµ РІСЃРµРіРґР°.
        // РќР°РІРѕРґС‡РёРє (seat 1) РІРёРґРёС‚ РїСЂРёС†РµР» Рё РјР°СЃРєСѓ РўРћР›Р¬РљРћ РїСЂРё РїСЂРёС†РµР»РёРІР°РЅРёРё (Р·СѓРјРµ).
        if ((seatIndex == 0 && isFirstPerson) || (seatIndex == 1 && isZooming)) {
            int screenWidth = mc.getWindow().getGuiScaledWidth();
            int screenHeight = mc.getWindow().getGuiScaledHeight();
            GuiGraphics guiGraphics = event.getGuiGraphics();

            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

            if (seatIndex == 0) {
                drawDriverHud(guiGraphics, mc, tow, screenWidth, screenHeight);
            }
            else if (seatIndex == 1) {
                // РћС‚СЂРёСЃРѕРІРєР° С‡РµСЂРЅРѕР№ РјР°СЃРєРё РЅР°РІРѕРґС‡РёРєР° РЅР° РІРµСЃСЊ СЌРєСЂР°РЅ
                guiGraphics.blit(GUNNER_TEXTURE, 0, 0, 0, 0, screenWidth, screenHeight, screenWidth, screenHeight);

                // Р Р°СЃС‡РµС‚ СЂР°Р·РјРµСЂРѕРІ РїСЂРёС†РµР»Р° СЃ СЃРѕС…СЂР°РЅРµРЅРёРµРј РїСЂРѕРїРѕСЂС†РёР№ 16:9
                float aspectRatio = 16.0F / 9.0F;
                int scopeHeight = screenHeight;
                int scopeWidth = (int) (scopeHeight * aspectRatio);

                if (scopeWidth > screenWidth) {
                    scopeWidth = screenWidth;
                    scopeHeight = (int) (scopeWidth / aspectRatio);
                }

                // Р’С‹С‡РёСЃР»РµРЅРёРµ РєРѕРѕСЂРґРёРЅР°С‚ РґР»СЏ РѕС‚СЂРёСЃРѕРІРєРё СЃС‚СЂРѕРіРѕ РїРѕ С†РµРЅС‚СЂСѓ
                int x = (screenWidth - scopeWidth) / 2;
                int y = (screenHeight - scopeHeight) / 2;

                guiGraphics.blit(TOW_SCOPE_TEXTURE, x, y, 0, 0, scopeWidth, scopeHeight, scopeWidth, scopeHeight);

                // РћС‚СЂРёСЃРѕРІРєР° РґР°Р»СЊРЅРѕРјРµСЂР° РїРѕРґ РїРµСЂРµРєСЂРµСЃС‚РёРµРј
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
