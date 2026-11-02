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
import tech.squadmc.squadmcor.entity.bmp2Entity;
import tech.squadmc.squadmcor.squadmc;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

@Mod.EventBusSubscriber(modid = "squadmc", value = Dist.CLIENT)
public class Bmp2ReticleOverlay {

    private static final ResourceLocation TRIPLEX_TEXTURE = new ResourceLocation(squadmc.MODID, "textures/overlay/triplex.png");
    private static final ResourceLocation GUNNER_TEXTURE = new ResourceLocation(squadmc.MODID, "textures/overlay/gunner.png");

    private static final ResourceLocation BMP2_CANNON_SCOPE = new ResourceLocation(squadmc.MODID, "textures/overlay/btr82_scope.png");
    private static final ResourceLocation BMP2_ATGM_SCOPE = new ResourceLocation(squadmc.MODID, "textures/overlay/konkurs_scope.png");

    private static Method cachedMethod = null;
    private static Field cachedField = null;
    private static boolean searchedReflection = false;

    @SubscribeEvent
    public static void onRenderGuiPre(RenderGuiEvent.Pre event) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;

        if (player == null || mc.options.hideGui) {
            return;
        }

        Entity vehicle = player.getVehicle();
        if (!(vehicle instanceof bmp2Entity bmp)) {
            return;
        }

        int seatIndex = bmp.getSeatIndex(player);
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
                drawDriverHud(guiGraphics, mc, bmp, screenWidth, screenHeight);
            }
            else if (seatIndex == 1) {
                guiGraphics.blit(GUNNER_TEXTURE, 0, 0, 0, 0, screenWidth, screenHeight, screenWidth, screenHeight);

                int selectedWeapon = getSelectedWeaponIndex(bmp, seatIndex);

                ResourceLocation activeScope;
                if (selectedWeapon == 2) {
                    activeScope = BMP2_ATGM_SCOPE;
                } else {
                    activeScope = BMP2_CANNON_SCOPE;
                }

                guiGraphics.blit(activeScope, 0, 0, 0, 0, screenWidth, screenHeight, screenWidth, screenHeight);
                drawGunnerHud(guiGraphics, mc, player, screenWidth, screenHeight);
            }

            RenderSystem.disableBlend();
        }
    }

    private static void drawDriverHud(GuiGraphics guiGraphics, Minecraft mc, bmp2Entity bmp, int width, int height) {
        double speed = bmp.getDeltaMovement().horizontalDistance() * 72.0D;
        String speedText = String.format("SPEED: %.0f km/h", speed);

        int healthPercent = (int) ((bmp.getHealth() / bmp.getMaxHealth()) * 100);
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

    private static int getSelectedWeaponIndex(bmp2Entity bmp, int seatIndex) {
        if (searchedReflection) {
            try {
                if (cachedMethod != null) {
                    return (int) cachedMethod.invoke(bmp, seatIndex);
                }
                if (cachedField != null) {
                    int[] arr = (int[]) cachedField.get(bmp);
                    return (arr != null && seatIndex < arr.length) ? arr[seatIndex] : 0;
                }
            } catch (Exception e) {
                return 0;
            }
        }

        searchedReflection = true;
        Class<?> clazz = bmp.getClass();

        try {
            cachedMethod = clazz.getMethod("getSelectedWeaponIndex", int.class);
            cachedMethod.setAccessible(true);
            return (int) cachedMethod.invoke(bmp, seatIndex);
        } catch (Exception e) {}

        try {
            cachedMethod = clazz.getMethod("getSelectedWeapon", int.class);
            cachedMethod.setAccessible(true);
            return (int) cachedMethod.invoke(bmp, seatIndex);
        } catch (Exception e) {}

        try {
            for (Method m : clazz.getMethods()) {
                String name = m.getName().toLowerCase();
                if (name.contains("weapon") && (name.contains("select") || name.contains("index") || name.contains("current"))) {
                    if (m.getParameterCount() == 1 && m.getParameterTypes()[0] == int.class && m.getReturnType() == int.class) {
                        m.setAccessible(true);
                        cachedMethod = m;
                        return (int) m.invoke(bmp, seatIndex);
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
                            int[] arr = (int[]) f.get(bmp);
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
