package tech.squadmc.squadmcor.client.hud;

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
import tech.squadmc.squadmcor.entity.btr80Entity;

@Mod.EventBusSubscriber(modid = "squadmc", value = Dist.CLIENT)
public class Btr80ReticleOverlay {

    private static final ResourceLocation TRIPLEX_TEXTURE = new ResourceLocation(squadmc.MODID, "textures/overlay/triplex.png");
    private static final ResourceLocation GUNNER_TEXTURE = new ResourceLocation(squadmc.MODID, "textures/overlay/gunner.png");
    private static final ResourceLocation BTR80_SCOPE_TEXTURE = new ResourceLocation(squadmc.MODID, "textures/overlay/btr82_scope.png");

    @SubscribeEvent
    public static void onRenderGuiPre(RenderGuiEvent.Pre event) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;

        if (player == null || mc.options.hideGui) {
            return;
        }

        Entity vehicle = player.getVehicle();
        if (!(vehicle instanceof btr80Entity btr)) {
            return;
        }

        int seatIndex = btr.getSeatIndex(player);
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
                // Р’РѕРґРёС‚РµР»СЊ: СЂРёСЃСѓРµРј РїРѕР»РЅРѕСЌРєСЂР°РЅРЅС‹Р№ С‚СЂРёРїР»РµРєСЃ
                guiGraphics.blit(TRIPLEX_TEXTURE, 0, 0, 0, 0, screenWidth, screenHeight, screenWidth, screenHeight);
                drawDriverHud(guiGraphics, mc, btr, screenWidth, screenHeight);
            }
            else if (seatIndex == 1) {
                // РќР°РІРѕРґС‡РёРє: РјР°СЃРєР° СЌРєСЂР°РЅР° (gunner.png) РІРѕ РІРµСЃСЊ СЌРєСЂР°РЅ
                guiGraphics.blit(GUNNER_TEXTURE, 0, 0, 0, 0, screenWidth, screenHeight, screenWidth, screenHeight);

                // РџСЂРёС†РµР»СЊРЅР°СЏ СЃРµС‚РєР° Р‘РўР -80
                guiGraphics.blit(BTR80_SCOPE_TEXTURE, 0, 0, 0, 0, screenWidth, screenHeight, screenWidth, screenHeight);

                // HUD РЅР°РІРѕРґС‡РёРєР° (РґР°Р»СЊРЅРѕРјРµСЂ)
                drawGunnerHud(guiGraphics, mc, player, screenWidth, screenHeight);
            }

            RenderSystem.disableBlend();
        }
    }

    private static void drawDriverHud(GuiGraphics guiGraphics, Minecraft mc, btr80Entity btr, int width, int height) {
        double speed = btr.getDeltaMovement().horizontalDistance() * 72.0D;
        String speedText = String.format("SPEED: %.0f km/h", speed);

        int healthPercent = (int) ((btr.getHealth() / btr.getMaxHealth()) * 100);
        String healthText = String.format("HEALTH: %d%%", healthPercent);

        int color = 0x66FF00; // Р—РµР»РµРЅС‹Р№ С†РІРµС‚ HUD

        guiGraphics.drawString(mc.font, speedText, 60, height - 40, color, false);
        guiGraphics.drawString(mc.font, healthText, width - 110, height - 40, color, false);
    }

    private static void drawGunnerHud(GuiGraphics guiGraphics, Minecraft mc, Player player, int width, int height) {
        int color = 0x66FF00;

        // Р Р°СЃС‡РµС‚ РґРёСЃС‚Р°РЅС†РёРё Р»Р°Р·РµСЂРЅРѕРіРѕ РґР°Р»СЊРЅРѕРјРµСЂР°
        double distance = calculateDistance(player);
        String distText = distance > 500 ? "LGR: >500m" : String.format("LGR: %.0fm", distance);

        // Р”Р°Р»СЊРЅРѕРјРµСЂ РІС‹РІРѕРґРёС‚СЃСЏ РїРѕРґ С†РµРЅС‚СЂР°Р»СЊРЅС‹Рј РїРµСЂРµРєСЂРµСЃС‚РёРµРј РїСЂРёС†РµР»Р°
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
