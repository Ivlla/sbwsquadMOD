// Based on EngineHud from Frontline Combat Pack by Commissar_Mado (see FCP_LICENSE.txt). Modified for SquadMC.
package tech.squadmc.squadmcor.heli.client;

import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import tech.squadmc.squadmcor.heli.entity.vehicle.engine.EngineControl;
import tech.squadmc.squadmcor.squadmc;

/** Расположение иконки и шкалы двигателя в HUD техники SBW. */
public final class EngineHud {
    public static final ResourceLocation ICON = new ResourceLocation(squadmc.MODID, "textures/overlay/engine_icon.png");
    /** Шкала здоровья SBW заканчивается на x=80. */
    public static final int X = 84;
    public static final int ICON_SIZE = 8;
    public static final int BAR_WIDTH = 30;

    private EngineHud() {}

    public static boolean shown(Player player) {
        return player != null && player.getVehicle() instanceof VehicleEntity vehicle
                && EngineControl.appliesTo(vehicle) && vehicle.getFirstPassenger() == player;
    }

    public static int hoverTextX(int sbwX) {
        Minecraft mc = Minecraft.getInstance();
        if (!shown(mc.player)) return sbwX;
        return Math.max(sbwX, X + ICON_SIZE + 2 + BAR_WIDTH + 4);
    }
}
