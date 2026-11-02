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
import tech.squadmc.squadmcor.entity.T64Entity;
import tech.squadmc.squadmcor.squadmc;

@Mod.EventBusSubscriber(modid = squadmc.MODID, value = Dist.CLIENT)
public class T64ReticleOverlay {

    private static final ResourceLocation TRIPLEX_TEXTURE = new ResourceLocation(squadmc.MODID, "textures/overlay/triplex.png");
    private static final ResourceLocation GUNNER_TEXTURE = new ResourceLocation(squadmc.MODID, "textures/overlay/gunner.png");
    private static final ResourceLocation T64_SCOPE = new ResourceLocation(squadmc.MODID, "textures/overlay/t72_scope.png");
    private static final ResourceLocation CORD_SCOPE = new ResourceLocation(squadmc.MODID, "textures/overlay/pkt_scope.png");

    @SubscribeEvent
    public static void onRenderGuiPre(RenderGuiEvent.Pre event) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;

        if (player == null || mc.options.hideGui) {
            return;
        }

        Entity vehicle = player.getVehicle();
        if (!(vehicle instanceof T64Entity t64)) {
            return;
        }

        int seatIndex = t64.getSeatIndex(player);
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
                drawDriverHud(guiGraphics, mc, t64, screenWidth, screenHeight);
            } else if (seatIndex == 1) {
                guiGraphics.blit(GUNNER_TEXTURE, 0, 0, 0, 0, screenWidth, screenHeight, screenWidth, screenHeight);
                guiGraphics.blit(T64_SCOPE, 0, 0, 0, 0, screenWidth, screenHeight, screenWidth, screenHeight);
                drawGunnerHud(guiGraphics, mc, player, screenWidth, screenHeight);
            } else if (seatIndex == 2) {
                guiGraphics.blit(GUNNER_TEXTURE, 0, 0, 0, 0, screenWidth, screenHeight, screenWidth, screenHeight);
                guiGraphics.blit(CORD_SCOPE, 0, 0, 0, 0, screenWidth, screenHeight, screenWidth, screenHeight);
                drawCommanderHud(guiGraphics, mc, t64, player, screenWidth, screenHeight);
            }

            RenderSystem.disableBlend();
        }
    }

    private static void drawDriverHud(GuiGraphics guiGraphics, Minecraft mc, T64Entity t64, int width, int height) {
        double speed = t64.getDeltaMovement().horizontalDistance() * 72.0D;
        String speedText = String.format("SPEED: %.0f km/h", speed);

        int healthPercent = (int) ((t64.getHealth() / t64.getMaxHealth()) * 100);
        String healthText = String.format("HEALTH: %d%%", healthPercent);

        int color = 0x66FF00;

        guiGraphics.drawString(mc.font, speedText, 60, height - 40, color, false);
        guiGraphics.drawString(mc.font, healthText, width - 110, height - 40, color, false);
    }

    private static void drawCommanderHud(GuiGraphics guiGraphics, Minecraft mc, T64Entity t64, Player player, int width, int height) {
        int color = 0x66FF00;

        double distance = calculateDistance(player);
        String distText = distance > 800 ? "LGR: >800m" : String.format("LGR: %.0fm", distance);
        guiGraphics.drawCenteredString(mc.font, distText, width / 2, height / 2 + 50, color);
    }

    private static void drawGunnerHud(GuiGraphics guiGraphics, Minecraft mc, Player player, int width, int height) {
        int color = 0x66FF00;

        double distance = calculateDistance(player);
        String distText = distance > 800 ? "LGR: >800m" : String.format("LGR: %.0fm", distance);

        guiGraphics.drawCenteredString(mc.font, distText, width / 2, height / 2 + 50, color);
    }

    private static double calculateDistance(Player player) {
        try {
            BlockHitResult result = player.level().clip(new ClipContext(
                    player.getEyePosition(),
                    player.getEyePosition().add(player.getLookAngle().scale(812.0D)),
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
