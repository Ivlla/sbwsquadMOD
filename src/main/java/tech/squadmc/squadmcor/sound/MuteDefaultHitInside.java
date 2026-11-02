package tech.squadmc.squadmcor.sound;

import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tech.squadmc.squadmcor.squadmc;

import java.util.Set;

/** КЛИЕНТ. Глушит стандартный звон попадания SBW, пока игрок сидит в технике. */
@Mod.EventBusSubscriber(modid = squadmc.MODID, value = Dist.CLIENT)
public class MuteDefaultHitInside {

    private static final Logger LOG = LogManager.getLogger("squadmc-mute");

    /** true = писать в лог все звуки внутри техники (с полным именем). Потом выключи. */
    private static final boolean DEBUG_LOG_ALL = true;

    // ВАЖНО: сравниваем полный ResourceLocation, а не e.getName().
    // e.getName() у Forge = только путь ("hit"), без "superbwarfare:".
    private static final Set<ResourceLocation> MUTED = Set.of(
            new ResourceLocation("superbwarfare", "hit"),
            new ResourceLocation("superbwarfare", "indication_vehicle")
    );

    @SubscribeEvent
    public static void onPlay(PlaySoundEvent e) {
        if (e.getSound() == null) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        if (!(mc.player.getRootVehicle() instanceof VehicleEntity)) return;

        ResourceLocation id = e.getSound().getLocation();

        if (DEBUG_LOG_ALL) {
            String p = id.getPath();
            if (!p.contains("engine") && !p.contains("step") && !p.contains("track")) {
                LOG.info("sound inside vehicle: {}", id);
            }
        }

        if (MUTED.contains(id)) {
            if (DEBUG_LOG_ALL) LOG.info("MUTED: {}", id);
            e.setSound(null);
        }
    }
}