package tech.squadmc.squadmcor.client.hud;

import com.atsuishio.superbwarfare.data.vehicle.subdata.EngineType;
import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.MovementInputUpdateEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;
import tech.squadmc.squadmcor.entity.SquadBaseVehicleEntity;
import tech.squadmc.squadmcor.squadmc;

@Mod.EventBusSubscriber(modid = squadmc.MODID, value = Dist.CLIENT)
public class VehicleInteractionOverlay {


    /**
     * Р‘Р»РѕРєРёСЂСѓРµС‚ С„РёР·РёС‡РµСЃРєРѕРµ СѓСЃРєРѕСЂРµРЅРёРµ W/A/S/D РїСЂРё Р·Р°РіР»СѓС€РµРЅРЅРѕРј РґРІРёРіР°С‚РµР»Рµ
     */
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onMovementInput(MovementInputUpdateEvent event) {
        Player player = Minecraft.getInstance().player;
        if (player != null && player.getVehicle() instanceof VehicleEntity vehicle) {
            if (vehicle instanceof SquadBaseVehicleEntity squadVehicle && !squadVehicle.isEngineStarted() && vehicle.getFirstPassenger() == player) {
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
            if (vehicle instanceof SquadBaseVehicleEntity squadVehicle && !squadVehicle.isEngineStarted() && vehicle.getFirstPassenger() == player) {
                mc.options.keyUp.setDown(false);
                mc.options.keyDown.setDown(false);
                mc.options.keyLeft.setDown(false);
                mc.options.keyRight.setDown(false);
            }

            // РџРѕРІРѕСЂРѕС‚ Р±Р°С€РЅРё РЅР° A Рё D РґР»СЏ РЅР°РІРѕРґС‡РёРєР°
            if (isGunnerWithTurret(vehicle, player)) {
                boolean leftDown = mc.options.keyLeft.isDown();
                boolean rightDown = mc.options.keyRight.isDown();

                if (leftDown != rightDown) {
                    float traverseSpeed = vehicle instanceof SquadBaseVehicleEntity squadVehicle ? squadVehicle.getTurretTraverseSpeed() : 2.2F;
                    float deltaYaw = leftDown ? -traverseSpeed : traverseSpeed;

                    player.setYRot(player.getYRot() + deltaYaw);
                    player.yHeadRot = player.getYRot();
                    player.yBodyRot = player.getYRot();
                }
            }
        }
    }

    /** Наводчик наземной техники с башней поворачивает её на A и D. */
    private static boolean isGunnerWithTurret(VehicleEntity vehicle, Player player) {
        if (!vehicle.hasTurret() || vehicle.getFirstPassenger() == player
                || vehicle.getSeatIndex(player) != vehicle.getTurretControllerIndex()) return false;
        EngineType type = vehicle.computed().getEngineType();
        return type == EngineType.TRACK || type == EngineType.WHEEL || type == EngineType.SHIP;
    }
}
