package tech.squadmc.squadmcor.heli.client.sound;

import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import tech.squadmc.squadmcor.heli.entity.vehicle.engine.EngineControl;
import tech.squadmc.squadmcor.squadmc;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.WeakHashMap;

/** Звук запуска для техники без многослойного профиля Squad. Прерванный запуск обрывает звук. */
@Mod.EventBusSubscriber(modid = squadmc.MODID, value = Dist.CLIENT)
public final class EngineStartSounds {
    private static final Map<String, String> SOUNDS = new LinkedHashMap<>();
    private static final String DEFAULT_WHEEL = "squadmc:engine_start.us_truck";
    private static final String DEFAULT_TRACK = "squadmc:engine_start.t72";
    private static final String DEFAULT_HELICOPTER = "superbwarfare:mi_28_engine_start";

    static {
        SOUNDS.put("m1a2", "squadmc:engine_start.abrams");
        SOUNDS.put("leopard", "squadmc:engine_start.abrams");
        SOUNDS.put("t72", "squadmc:engine_start.t72");
        SOUNDS.put("t64", "squadmc:engine_start.t72");
        SOUNDS.put("t80", "squadmc:engine_start.t90");
        SOUNDS.put("sprut", "squadmc:engine_start.t90");
        SOUNDS.put("bmp2", "squadmc:engine_start.bmp2");
        SOUNDS.put("bmd4", "squadmc:engine_start.bmp3");
        SOUNDS.put("bmp1", "squadmc:engine_start.bmp1");
        SOUNDS.put("mtlb", "squadmc:engine_start.bmp1");
        SOUNDS.put("bradley", "squadmc:engine_start.bradley");
        SOUNDS.put("puma", "squadmc:engine_start.warrior");
        SOUNDS.put("btr", "squadmc:engine_start.btr80");
        SOUNDS.put("brdm", "squadmc:engine_start.brdm2");
        SOUNDS.put("lav25", "squadmc:engine_start.lav25");
        SOUNDS.put("stryker", "squadmc:engine_start.stryker");
        SOUNDS.put("gaz_tigr", "squadmc:engine_start.tigr");
        SOUNDS.put("humvee", "squadmc:engine_start.m1151");
        SOUNDS.put("toyota", "squadmc:engine_start.technical");
        SOUNDS.put("uaz", "squadmc:engine_start.technical");
        SOUNDS.put("motuo", "squadmc:engine_start.technical");
        SOUNDS.put("motorboat", "squadmc:engine_start.technical");
        SOUNDS.put("ural", "squadmc:engine_start.ural375");
        SOUNDS.put("grad", "squadmc:engine_start.ural375");
        SOUNDS.put("fmtv", "squadmc:engine_start.us_truck");
        SOUNDS.put("fenek", "squadmc:engine_start.gwagon");
    }

    private static final Map<VehicleEntity, Byte> LAST_STATE = new WeakHashMap<>();
    private static final Map<VehicleEntity, SoundInstance> PLAYING = new WeakHashMap<>();

    private EngineStartSounds() {}

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.isPaused()) return;
        for (Entity entity : mc.level.entitiesForRendering()) {
            if (entity instanceof VehicleEntity vehicle && EngineControl.appliesTo(vehicle)) update(mc, vehicle);
        }
    }

    private static void update(Minecraft mc, VehicleEntity vehicle) {
        byte state = EngineControl.state(vehicle);
        Byte last = LAST_STATE.put(vehicle, state);
        if (last == null || last == state || EngineControl.usesSquadSound(vehicle)) return;
        if (state == EngineControl.STARTING) {
            SoundInstance sound = new SimpleSoundInstance(SoundEvent.createVariableRangeEvent(new ResourceLocation(soundFor(vehicle))),
                    SoundSource.NEUTRAL, 1.5F, 1F, vehicle.getRandom(), vehicle.getX(), vehicle.getY(), vehicle.getZ());
            mc.getSoundManager().play(sound);
            PLAYING.put(vehicle, sound);
        } else if (state == EngineControl.OFF) {
            SoundInstance sound = PLAYING.remove(vehicle);
            if (sound != null) mc.getSoundManager().stop(sound);
        }
    }

    private static String soundFor(VehicleEntity vehicle) {
        String id = EntityType.getKey(vehicle.getType()).getPath();
        for (Map.Entry<String, String> entry : SOUNDS.entrySet()) {
            if (id.contains(entry.getKey())) return entry.getValue();
        }
        if (EngineControl.isHelicopter(vehicle)) return DEFAULT_HELICOPTER;
        return tech.squadmc.squadmcor.repair.VehicleRepair.isTracked(vehicle) ? DEFAULT_TRACK : DEFAULT_WHEEL;
    }
}
