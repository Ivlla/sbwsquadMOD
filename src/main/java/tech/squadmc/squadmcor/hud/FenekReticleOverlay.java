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
import tech.squadmc.squadmcor.entity.FenekEntity;
import tech.squadmc.squadmcor.squadmc;

@Mod.EventBusSubscriber(modid = squadmc.MODID, value = Dist.CLIENT)
public class FenekReticleOverlay {

    private static final ResourceLocation TRIPLEX_TEXTURE = new ResourceLocation(squadmc.MODID, "textures/overlay/triplex.png");
    private static final ResourceLocation RWS_FRAME = new ResourceLocation(squadmc.MODID, "textures/overlay/tv_frame.png");
    private static final ResourceLocation RWS_DIGITAL = new ResourceLocation(squadmc.MODID, "textures/overlay/outline_digital-4x3.png");
    private static final ResourceLocation RWS_RETICLE = new ResourceLocation(squadmc.MODID, "textures/overlay/outline_kord-rws.png");

    @SubscribeEvent
    public static void onRenderGuiPre(RenderGuiEvent.Pre event) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;

        if (player == null || mc.options.hideGui) {
            return;
        }

        Entity vehicle = player.getVehicle();
        if (!(vehicle instanceof FenekEntity fenek)) {
            return;
        }

        int seatIndex = fenek.getSeatIndex(player);
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
                // [1] Р’РѕРґРёС‚РµР»СЊ
                guiGraphics.blit(TRIPLEX_TEXTURE, 0, 0, 0, 0, screenWidth, screenHeight, screenWidth, screenHeight);
                drawDriverHud(guiGraphics, mc, fenek, screenWidth, screenHeight);
            } else if (seatIndex == 1) {
                // [2] РЎС‚СЂРµР»РѕРє (РџСѓР»РµРјРµС‚РЅР°СЏ С‚СѓСЂРµР»СЊ)
                guiGraphics.blit(RWS_FRAME, 0, 0, 0, 0, screenWidth, screenHeight, screenWidth, screenHeight);
                guiGraphics.blit(RWS_DIGITAL, 0, 0, 0, 0, screenWidth, screenHeight, screenWidth, screenHeight);

                float aspectRatio = 16.0F / 9.0F;
                int scopeHeight = screenHeight;
                int scopeWidth = (int) (scopeHeight * aspectRatio);
                if (scopeWidth > screenWidth) {
                    scopeWidth = screenWidth;
                    scopeHeight = (int) (scopeWidth / aspectRatio);
                }
                int x = (screenWidth - scopeWidth) / 2;
                int y = (screenHeight - scopeHeight) / 2;
                guiGraphics.blit(RWS_RETICLE, x, y, 0, 0, scopeWidth, scopeHeight, scopeWidth, scopeHeight);

                drawGunnerHud(guiGraphics, mc, player, screenWidth, screenHeight);
            } else if (seatIndex == 2) {
                // [3] РљРѕРјР°РЅРґРёСЂ (РћР±Р·РѕСЂРЅС‹Р№ РїРµСЂРёСЃРєРѕРї / РјР°С‡С‚Р°)
                guiGraphics.blit(RWS_FRAME, 0, 0, 0, 0, screenWidth, screenHeight, screenWidth, screenHeight);
                guiGraphics.blit(RWS_DIGITAL, 0, 0, 0, 0, screenWidth, screenHeight, screenWidth, screenHeight);

                float aspectRatio = 16.0F / 9.0F;
                int scopeHeight = screenHeight;
                int scopeWidth = (int) (scopeHeight * aspectRatio);
                if (scopeWidth > screenWidth) {
                    scopeWidth = screenWidth;
                    scopeHeight = (int) (scopeWidth / aspectRatio);
                }
                int x = (screenWidth - scopeWidth) / 2;
                int y = (screenHeight - scopeHeight) / 2;

                guiGraphics.blit(RWS_RETICLE, x, y, 0, 0, scopeWidth, scopeHeight, scopeWidth, scopeHeight);
                drawCommanderHud(guiGraphics, mc, player, screenWidth, screenHeight);
            }

            RenderSystem.disableBlend();
        }
    }

    private static void drawDriverHud(GuiGraphics guiGraphics, Minecraft mc, FenekEntity fenek, int width, int height) {
        double speed = fenek.getDeltaMovement().horizontalDistance() * 72.0D;
        String speedText = String.format("SPEED: %.0f km/h", speed);

        int healthPercent = (int) ((fenek.getHealth() / fenek.getMaxHealth()) * 100);
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

    private static void drawCommanderHud(GuiGraphics guiGraphics, Minecraft mc, Player player, int width, int height) {
        int color = 0x00FFCC;
        double distance = calculateDistance(player);
        String distText = distance > 1000 ? "LRF: >1000m" : String.format("LRF: %.0fm", distance);
        guiGraphics.drawCenteredString(mc.font, distText, width / 2, height / 2 + 50, color);
    }

    private static double calculateDistance(Player player) {
        try {
            BlockHitResult result = player.level().clip(new ClipContext(
                    player.getEyePosition(),
                    player.getEyePosition().add(player.getLookAngle().scale(1000.0D)),
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
