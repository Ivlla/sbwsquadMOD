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
import tech.squadmc.squadmcor.squadmc;
import tech.squadmc.squadmcor.entity.empl.TowEntity;

@Mod.EventBusSubscriber(modid = "squadmc", value = Dist.CLIENT)
public class towReticleOverlay {

    // РџСѓС‚СЊ Рє С‚РµРєСЃС‚СѓСЂРµ Р·Р°С‚РµРјРЅСЏСЋС‰РµР№ РјР°СЃРєРё РїРѕ РєСЂР°СЏРј (РєР°Рє Сѓ РѕСЃС‚Р°Р»СЊРЅС‹С… РЅР°РІРѕРґС‡РёРєРѕРІ)
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
        if (!(vehicle instanceof TowEntity zis)) {
            return;
        }

        // РўР°Рє РєР°Рє РїСѓС€РєР° СЃС‚Р°С†РёРѕРЅР°СЂРЅР°СЏ, СЃРёРґРµРЅСЊРµ РЅР°РІРѕРґС‡РёРєР°/СЃС‚СЂРµР»РєР° РЅР°С…РѕРґРёС‚СЃСЏ РїРѕРґ РёРЅРґРµРєСЃРѕРј 0
        int seatIndex = zis.getSeatIndex(player);
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
                guiGraphics.blit(GUNNER_TEXTURE, 0, 0, 0, 0, screenWidth, screenHeight, screenWidth, screenHeight);
                guiGraphics.blit(TOW_SCOPE_TEXTURE, 0, 0, 0, 0, screenWidth, screenHeight, screenWidth, screenHeight);
                drawGunnerHud(guiGraphics, mc, player, screenWidth, screenHeight);
            }
            RenderSystem.disableBlend();
        }
    }

    private static void drawGunnerHud(GuiGraphics guiGraphics, Minecraft mc, Player player, int width, int height) {
        int color = 0x66FF00; // Р—РµР»РµРЅС‹Р№ С†РІРµС‚ РёРЅС‚РµСЂС„РµР№СЃР°

        // РР·РјРµСЂРµРЅРёРµ РґРёСЃС‚Р°РЅС†РёРё СЃ РїРѕРјРѕС‰СЊСЋ Р»Р°Р·РµСЂРЅРѕРіРѕ РґР°Р»СЊРЅРѕРјРµСЂР°
        double distance = calculateDistance(player);
        String distText = distance > 1000 ? "LGR: >1000m" : String.format("LGR: %.0fm", distance);

        // РўРµРєСЃС‚ РІС‹РІРѕРґРёС‚СЃСЏ РїРѕ С†РµРЅС‚СЂСѓ СЌРєСЂР°РЅР° С‡СѓС‚СЊ РЅРёР¶Рµ РїРµСЂРµРєСЂРµСЃС‚РёСЏ
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
