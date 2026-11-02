package tech.squadmc.squadmcor.client.hud;

import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.MovementInputUpdateEvent;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;
import tech.squadmc.squadmcor.entity.SquadBaseVehicleEntity;
import tech.squadmc.squadmcor.network.ModNetworking;
import tech.squadmc.squadmcor.network.ToggleEnginePacket;
import tech.squadmc.squadmcor.squadmc;

@Mod.EventBusSubscriber(modid = squadmc.MODID, value = Dist.CLIENT)
public class VehicleInteractionOverlay {

    // РўРµРєСЃС‚СѓСЂР° РёРєРѕРЅРєРё СЃРѕСЃС‚РѕСЏРЅРёСЏ РґРІРёРіР°С‚РµР»СЏ
    private static final ResourceLocation ENGINE_ICON = new ResourceLocation(squadmc.MODID, "textures/overlay/engine_icon.png");

    /**
     * Р‘Р»РѕРєРёСЂСѓРµС‚ С„РёР·РёС‡РµСЃРєРѕРµ СѓСЃРєРѕСЂРµРЅРёРµ W/A/S/D РїСЂРё Р·Р°РіР»СѓС€РµРЅРЅРѕРј РґРІРёРіР°С‚РµР»Рµ
     */
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onMovementInput(MovementInputUpdateEvent event) {
        Player player = Minecraft.getInstance().player;
        if (player != null && player.getVehicle() instanceof VehicleEntity vehicle) {
            if (vehicle instanceof SquadBaseVehicleEntity squadVehicle && !squadVehicle.isEngineStarted()) {
                event.getInput().forwardImpulse = 0.0F;
                event.getInput().leftImpulse = 0.0F;
                event.getInput().up = false;
                event.getInput().down = false;
                event.getInput().left = false;
                event.getInput().right = false;
                event.getInput().jumping = false;
            }
        }
    }

    /**
     * РћР±СЂР°Р±РѕС‚РєР° РєР»Р°РІРёС€ (Р—Р°РїСѓСЃРє РґРІРёРіР°С‚РµР»СЏ РЅР° 'R', РёРЅРІРµРЅС‚Р°СЂСЊ РЅР° 'E')
     */
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onKeyInput(InputEvent.Key event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null && mc.player.getVehicle() instanceof VehicleEntity) {
            int key = event.getKey();
            int action = event.getAction();

            // 1. РћС‚РєСЂС‹С‚РёРµ РёРЅРІРµРЅС‚Р°СЂСЏ
            if (action == GLFW.GLFW_PRESS && mc.options.keyInventory.matches(key, event.getScanCode())) {
                if (mc.screen == null) {
                    mc.setScreen(new InventoryScreen(mc.player));
                    while (mc.options.keyInventory.consumeClick()) {}
                    return;
                }
            }

            // 2. Р—Р°РїСѓСЃРє / РћСЃС‚Р°РЅРѕРІРєР° РґРІРёРіР°С‚РµР»СЏ РїРѕ РЅР°Р¶Р°С‚РёСЋ 'R'
            if (key == GLFW.GLFW_KEY_R && action == GLFW.GLFW_PRESS) {
                ModNetworking.CHANNEL.sendToServer(new ToggleEnginePacket());
            }
        }
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null || mc.level == null) return;

        if (player.getVehicle() instanceof VehicleEntity vehicle) {
            // Р‘Р»РѕРєРёСЂСѓРµРј РєР»Р°РІРёС€Рё РґРІРёР¶РµРЅРёСЏ РґР»СЏ РІРѕРґРёС‚РµР»СЏ, РµСЃР»Рё РґРІРёРіР°С‚РµР»СЊ РІС‹РєР»СЋС‡РµРЅ
            if (vehicle instanceof SquadBaseVehicleEntity squadVehicle && !squadVehicle.isEngineStarted()) {
                mc.options.keyUp.setDown(false);
                mc.options.keyDown.setDown(false);
                mc.options.keyLeft.setDown(false);
                mc.options.keyRight.setDown(false);
            }

            // РџРѕРІРѕСЂРѕС‚ Р±Р°С€РЅРё РЅР° A Рё D РґР»СЏ РЅР°РІРѕРґС‡РёРєР°
            if (vehicle instanceof SquadBaseVehicleEntity squadVehicle && squadVehicle.hasKeyboardTurretTraverse()) {
                int seatIndex = squadVehicle.getSeatIndex(player);
                if (seatIndex == 1) {
                    boolean leftDown = mc.options.keyLeft.isDown();
                    boolean rightDown = mc.options.keyRight.isDown();

                    if (leftDown != rightDown) {
                        float traverseSpeed = squadVehicle.getTurretTraverseSpeed();
                        float deltaYaw = leftDown ? -traverseSpeed : traverseSpeed;

                        player.setYRot(player.getYRot() + deltaYaw);
                        player.yHeadRot = player.getYRot();
                        player.yBodyRot = player.getYRot();
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onRenderGui(RenderGuiEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null || mc.options.hideGui) return;

        int screenHeight = mc.getWindow().getGuiScaledHeight();
        GuiGraphics gui = event.getGuiGraphics();

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        if (player.getVehicle() instanceof VehicleEntity vehicle) {
            // РћС‚РѕР±СЂР°Р¶РµРЅРёРµ РёРЅРґРёРєР°С‚РѕСЂР° РґРІРёРіР°С‚РµР»СЏ
            drawEngineStatusIndicator(gui, mc, vehicle, screenHeight);
        }

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableBlend();
    }

    /**
     * РћС‚СЂРёСЃРѕРІРєР° РёРЅРґРёРєР°С‚РѕСЂР° СЃРѕСЃС‚РѕСЏРЅРёСЏ РґРІРёРіР°С‚РµР»СЏ
     */
    private static void drawEngineStatusIndicator(GuiGraphics gui, Minecraft mc, VehicleEntity vehicle, int screenHeight) {
        boolean engineStarted = true;
        if (vehicle instanceof SquadBaseVehicleEntity squadVehicle) {
            engineStarted = squadVehicle.isEngineStarted();
        }

        int iconX = 90;
        int iconY = screenHeight - 20;
        int iconSize = 16;

        if (engineStarted) {
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        } else {
            RenderSystem.setShaderColor(1.0F, 0.15F, 0.15F, 1.0F);
        }

        gui.blit(ENGINE_ICON, iconX, iconY, 0, 0, iconSize, iconSize, iconSize, iconSize);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }
}
