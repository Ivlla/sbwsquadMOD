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
import tech.squadmc.squadmcor.entity.bradleyEntity;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

@Mod.EventBusSubscriber(modid = "squadmc", value = Dist.CLIENT)
public class BradleyReticleOverlay {

    private static final ResourceLocation TRIPLEX_TEXTURE = new ResourceLocation(squadmc.MODID, "textures/overlay/triplex.png");
    private static final ResourceLocation GUNNER_TEXTURE = new ResourceLocation(squadmc.MODID, "textures/overlay/gunner.png");

    private static final ResourceLocation BRADLEY_CANNON_SCOPE = new ResourceLocation(squadmc.MODID, "textures/overlay/bradley_cross.png");
    private static final ResourceLocation TOW_SCOPE = new ResourceLocation(squadmc.MODID, "textures/overlay/bradley_cross_tow.png");

    private static boolean searchedReflection = false;
    private static Method cachedMethod = null;
    private static Field cachedField = null;

    @SubscribeEvent
    public static void onRenderGuiPre(RenderGuiEvent.Pre event) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;

        if (player == null || mc.options.hideGui) {
            return;
        }

        Entity vehicle = player.getVehicle();
        if (!(vehicle instanceof bradleyEntity bradley)) {
            return;
        }

        int seatIndex = bradley.getSeatIndex(player);
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
                // Р’РѕРґРёС‚РµР»СЊ Bradley
                guiGraphics.blit(TRIPLEX_TEXTURE, 0, 0, 0, 0, screenWidth, screenHeight, screenWidth, screenHeight);
                drawDriverHud(guiGraphics, mc, bradley, screenWidth, screenHeight);
            }
            else if (seatIndex == 1) {
                // РќР°РІРѕРґС‡РёРє Bradley (РјР°СЃРєР° СЂР°СЃС‚СЏРіРёРІР°РµС‚СЃСЏ РЅР° РІРµСЃСЊ СЌРєСЂР°РЅ)
                guiGraphics.blit(GUNNER_TEXTURE, 0, 0, 0, 0, screenWidth, screenHeight, screenWidth, screenHeight);

                // Р’С‹С‡РёСЃР»РµРЅРёРµ РєРІР°РґСЂР°С‚РЅРѕР№ РѕР±Р»Р°СЃС‚Рё С‚РѕР»СЊРєРѕ РґР»СЏ СЃРµС‚РєРё РїСЂРёС†РµР»Р°
                int scopeSize = Math.min(screenWidth, screenHeight);
                int posX = (screenWidth - scopeSize) / 2;
                int posY = (screenHeight - scopeSize) / 2;

                int selectedWeapon = getSelectedWeaponIndex(bradley, seatIndex);
                ResourceLocation activeScope;
                if (selectedWeapon == 2) {
                    activeScope = TOW_SCOPE;
                } else {
                    activeScope = BRADLEY_CANNON_SCOPE;
                }

                // РћС‚СЂРёСЃРѕРІРєР° СЃРµС‚РєРё РїСЂРёС†РµР»Р° СЃ РїСЂРѕРїРѕСЂС†РёСЏРјРё 1:1 РІ С†РµРЅС‚СЂРµ СЌРєСЂР°РЅР°
                guiGraphics.blit(activeScope, posX, posY, 0, 0, scopeSize, scopeSize, scopeSize, scopeSize);

                drawGunnerHud(guiGraphics, mc, player, screenWidth, screenHeight);
            }

            RenderSystem.disableBlend();
        }
    }

    private static void drawDriverHud(GuiGraphics guiGraphics, Minecraft mc, bradleyEntity bradley, int width, int height) {
        double speed = bradley.getDeltaMovement().horizontalDistance() * 72.0D;
        String speedText = String.format("SPEED: %.0f km/h", speed);

        int healthPercent = (int) ((bradley.getHealth() / bradley.getMaxHealth()) * 100);
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

    private static int getSelectedWeaponIndex(bradleyEntity bradley, int seatIndex) {
        if (searchedReflection) {
            try {
                if (cachedMethod != null) {
                    return (int) cachedMethod.invoke(bradley, seatIndex);
                }
                if (cachedField != null) {
                    int[] arr = (int[]) cachedField.get(bradley);
                    return (arr != null && seatIndex < arr.length) ? arr[seatIndex] : 0;
                }
            } catch (Exception e) {
                return 0;
            }
        }

        searchedReflection = true;
        Class<?> clazz = bradley.getClass();

        try {
            cachedMethod = clazz.getMethod("getSelectedWeaponIndex", int.class);
            cachedMethod.setAccessible(true);
            return (int) cachedMethod.invoke(bradley, seatIndex);
        } catch (Exception e) {}

        try {
            cachedMethod = clazz.getMethod("getSelectedWeapon", int.class);
            cachedMethod.setAccessible(true);
            return (int) cachedMethod.invoke(bradley, seatIndex);
        } catch (Exception e) {}

        try {
            for (Method m : clazz.getMethods()) {
                String name = m.getName().toLowerCase();
                if (name.contains("weapon") && (name.contains("select") || name.contains("index") || name.contains("current"))) {
                    if (m.getParameterCount() == 1 && m.getParameterTypes()[0] == int.class && m.getReturnType() == int.class) {
                        m.setAccessible(true);
                        cachedMethod = m;
                        return (int) m.invoke(bradley, seatIndex);
                    }
                }
            }
        } catch (Exception e) {}

        try {
            Class<?> current = clazz;
            while (current != null && current != Object.class) {
                for (Field f : current.getDeclaredFields()) {
                    String name = f.getName().toLowerCase();
                    if (name.contains("weapon") && name.contains("select")) {
                        if (f.getType() == int[].class) {
                            f.setAccessible(true);
                            cachedField = f;
                            int[] arr = (int[]) f.get(bradley);
                            return (arr != null && seatIndex < arr.length) ? arr[seatIndex] : 0;
                        }
                    }
                }
                current = current.getSuperclass();
            }
        } catch (Exception e) {}

        return 0;
    }
}
