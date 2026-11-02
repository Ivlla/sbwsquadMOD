package tech.squadmc.squadmcor.init;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import tech.squadmc.squadmcor.squadmc;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> REGISTRY =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, squadmc.MODID);

    // Р—РІСѓРєРѕРІРѕРµ СЃРѕР±С‹С‚РёРµ РІС‹СЃС‚СЂРµР»Р° РїСѓС€РєРё Р‘РўР -82
    public static final RegistryObject<SoundEvent> BTR82_CANNON_FIRE = register("btr82_cannon_fire");
    public static final RegistryObject<SoundEvent> BTR82_CANNON_FIRE_3P = register("btr82_cannon_fire_3p");
    public static final RegistryObject<SoundEvent> BTR82_CANNON_FIRE_3P_FAR = register("btr82_cannon_fire_3p_far");
    public static final RegistryObject<SoundEvent> BTR82_CANNON_FIRE_3P_FAR_VERY = register("btr82_cannon_fire_3p_far_very");


    // Р—РІСѓРєРѕРІРѕРµ СЃРѕР±С‹С‚РёРµ РІС‹СЃС‚СЂРµР»Р° РїСѓС€РєРё Р‘РўР -80
    public static final RegistryObject<SoundEvent> BTR80_CANNON_FIRE = register("btr80_kpvt_1p");
    public static final RegistryObject<SoundEvent> BTR80_CANNON_FIRE_3P = register("btr80_kpvt_3p");
    public static final RegistryObject<SoundEvent> BTR80_CANNON_RELOAD = register("reload_kpvt");


    // Р—РІСѓРєРѕРІРѕРµ СЃРѕР±С‹С‚РёРµ РІС‹СЃС‚СЂРµР»Р° РїСѓС€РєРё Р‘РњРџ-2
    public static final RegistryObject<SoundEvent> BMP2_CANNON_FIRE = register("bmp2_cannon_fire_1p");
    public static final RegistryObject<SoundEvent> BMP2_CANNON_FIRE_3P = register("bmp2_cannon_fire_3p");

    // Р—РІСѓРєРё РїРѕРґРіРѕС‚РѕРІРєРё РџРўРЈР 
    public static final RegistryObject<SoundEvent> KONKURS_PREFIRE_1P = register("konkurs_prefire_1p");
    public static final RegistryObject<SoundEvent> KONKURS_PREFIRE_3P = register("konkurs_prefire_3p");
    public static final RegistryObject<SoundEvent> KONKURS_LAUNCH_BLAST = register("konkurs_launch_blast");


    // Р”РІРёРіР°С‚РµР»СЊ Р‘РўР -82
    public static final RegistryObject<SoundEvent> BTR82_ENGINE_IDLE_1P = register("btr82_engine_idle_1p");
    public static final RegistryObject<SoundEvent> BTR82_ENGINE_IDLE_3P = register("btr82_engine_idle_3p");
    public static final RegistryObject<SoundEvent> BTR82_ENGINE_RUNNING_1P = register("btr82_engine_running_1p");
    public static final RegistryObject<SoundEvent> BTR82_ENGINE_RUNNING_3P = register("btr82_engine_running_3p");

    // Р”РІРёРіР°С‚РµР»СЊ Р‘РњРџ-2
    public static final RegistryObject<SoundEvent> BMP2_ENGINE_IDLE_1P = register("bmp2_engine_idle_1p");
    public static final RegistryObject<SoundEvent> BMP2_ENGINE_IDLE_3P = register("bmp2_engine_idle_3p");
    public static final RegistryObject<SoundEvent> BMP2_ENGINE_RUNNING_1P = register("bmp2_engine_running_1p");
    public static final RegistryObject<SoundEvent> BMP2_ENGINE_RUNNING_3P = register("bmp2_engine_running_3p");

    // Р”РІРёРіР°С‚РµР»СЊ LAV-25
    public static final RegistryObject<SoundEvent> LAV25_ENGINE_IDLE_1P = register("lav25_engine_idle_1p");
    public static final RegistryObject<SoundEvent> LAV25_ENGINE_IDLE_3P = register("lav25_engine_idle_3p");
    public static final RegistryObject<SoundEvent> LAV25_ENGINE_RUNNING_1P = register("lav25_engine_running_1p");
    public static final RegistryObject<SoundEvent> LAV25_ENGINE_RUNNING_3P = register("lav25_engine_running_3p");

    // Р”РІРёРіР°С‚РµР»СЊ Bradley
    public static final RegistryObject<SoundEvent> BRADLEY_ENGINE_IDLE_1P = register("bradley_engine_idle_1p");
    public static final RegistryObject<SoundEvent> BRADLEY_ENGINE_IDLE_3P = register("bradley_engine_idle_3p");
    public static final RegistryObject<SoundEvent> BRADLEY_ENGINE_RUNNING_1P = register("bradley_engine_running_1p");
    public static final RegistryObject<SoundEvent> BRADLEY_ENGINE_RUNNING_3P = register("bradley_engine_running_3p");

    // Р’РѕРѕСЂСѓР¶РµРЅРёРµ Bradley
    public static final RegistryObject<SoundEvent> BRADLEY_CANNON_FIRE = register("bradley_cannon_fire_1p");
    public static final RegistryObject<SoundEvent> BRADLEY_CANNON_FIRE_3P = register("bradley_cannon_fire_3p");
    public static final RegistryObject<SoundEvent> TOW_PREFIRE_1P = register("tow_prefire_1p");
    public static final RegistryObject<SoundEvent> TOW_PREFIRE_3P = register("tow_prefire_3p");
    public static final RegistryObject<SoundEvent> TOW_LAUNCH_BLAST = register("tow_launch_blast");

    // Р”РІРёРіР°С‚РµР»СЊ BMD4
    public static final RegistryObject<SoundEvent> BMD4_ENGINE_IDLE_1P = register("bmd4_engine_idle_1p");
    public static final RegistryObject<SoundEvent> BMD4_ENGINE_IDLE_3P = register("bmd4_engine_idle_3p");
    public static final RegistryObject<SoundEvent> BMD4_ENGINE_RUNNING_1P = register("bmd4_engine_running_1p");
    public static final RegistryObject<SoundEvent> BMD4_ENGINE_RUNNING_3P = register("bmd4_engine_running_3p");

    //Bmd4 Cannon
    public static final RegistryObject<SoundEvent> BMD4_CANNON_FIRE_1P = register("120mm_fire_1p");
    public static final RegistryObject<SoundEvent> BMD4_CANNON_FIRE_3P = register("120mm_fire_3p");
    public static final RegistryObject<SoundEvent> BMD4_CANNON_FIRE_3P_FAR = register("120mm_fire_3p_far");
    public static final RegistryObject<SoundEvent> BMD4_CANNON_FIRE_3P_FAR_VERY = register("120mm_fire_3p_far_very");


    //Р”РІРёРіР°С‚РµР»СЊ T72Av
    public static final RegistryObject<SoundEvent> T72AV_ENGINE_IDLE_1P = register("t72av_engine_idle_1p");
    public static final RegistryObject<SoundEvent> T72AV_ENGINE_IDLE_3P = register("t72av_engine_idle_3p");
    public static final RegistryObject<SoundEvent> T72AV_ENGINE_RUNNING_3P = register("t72av_engine_running_3p");
    public static final RegistryObject<SoundEvent> T72AV_ENGINE_RUNNING_1P = register("t72av_engine_running_1p");

    //Р”РІРёРіР°С‚РµР»СЊ Bmp1
    public static final RegistryObject<SoundEvent> BMP1_ENGINE_IDLE_1P = register("bmp1_engine_idle_1p");
    public static final RegistryObject<SoundEvent> BMP1_ENGINE_IDLE_3P = register("bmp1_engine_idle_3p");
    public static final RegistryObject<SoundEvent> BMP1_ENGINE_RUNNING_3P = register("bmp1_engine_running_3p");
    public static final RegistryObject<SoundEvent> BMP1_ENGINE_RUNNING_1P = register("bmp1_engine_running_1p");

    //at3 malytka
    public static final RegistryObject<SoundEvent> BMP1_AT3_FIRE_1P = register("at3_fire_1p");
    public static final RegistryObject<SoundEvent> BMP1_AT3_FIRE_3P = register("at3_fire_3p");
    public static final RegistryObject<SoundEvent> BMP1_AT3_RELOAD = register("at3_reload");

    //bmp1 cannon
    public static final RegistryObject<SoundEvent> BMP1_FIRE_1P = register("bmp1_fire_1p");
    public static final RegistryObject<SoundEvent> BMP1_FIRE_3P = register("bmp1_fire_3p");
    public static final RegistryObject<SoundEvent> BMP1_FIRE_3P_FAR = register("bmp1_fire_3p_far");
    public static final RegistryObject<SoundEvent> BMP1_FIRE_3P_FAR_VERY = register("bmp1_fire_3p_far_very");

    // РџРµСЂРµР·Р°СЂСЏРґРєР° СЂСѓС‡РєР°РјРё 120РјРј
    public static final RegistryObject<SoundEvent> RELOAD_120MM_MECHAN= register("reload_120mm_mechan");
    // РџРµСЂРµР·Р°СЂСЏРґРєР° Р°РІС‚РѕРјР°С‚РѕРј 120РјРј
    public static final RegistryObject<SoundEvent> RELOAD_120MM_AUTO= register("reload_120mm_auto");

    //Р”РІРёРіР°С‚РµР»СЊ Humvee
    public static final RegistryObject<SoundEvent> HUMVEE_ENGINE_IDLE_1P = register("humvee_idle_1p");
    public static final RegistryObject<SoundEvent> HUMVEE_ENGINE_IDLE_3P = register("humvee_idle_3p");
    public static final RegistryObject<SoundEvent> HUMVEE_RUNNING_1P_3P = register("humvee_running_1p_3p");

    //M2 РѕРіРѕРЅСЊ
    public static final RegistryObject<SoundEvent> M2_FIRE = register("m2_fire");
    //M2 РїРµСЂРµР·Р°СЂСЏРґРєР°
    public static final RegistryObject<SoundEvent> M2_RELOAD = register("m2_reload");


    public static final RegistryObject<SoundEvent> EMPTY = register("empty");


    // РџСѓР»РёРє PKT
    public static final RegistryObject<SoundEvent> PKT_COAX = register("pkt_coax");


    //Р”РІРёРіР°С‚РµР»СЊ M1A2
    public static final RegistryObject<SoundEvent> M1A2_ENGINE_IDLE_1P = register("m1a2_idle_1p");
    public static final RegistryObject<SoundEvent> M1A2_ENGINE_IDLE_3P = register("m1a2_idle_3p");
    public static final RegistryObject<SoundEvent> M1A2_RUNNING_3P = register("m1a2_running_3p");
    public static final RegistryObject<SoundEvent> M1A2_RUNNING_1P = register("m1a2_running_1p");

    //РЎРјРѕРєРё

    public static final RegistryObject<SoundEvent> BTR82_SMOKE_LAUNCH = register("btr82_smoke_launch");
    public static final RegistryObject<SoundEvent> BTR82_SMOKE_EXPLODE = register("btr82_smoke_explode");

    //Ural РґРІРјРіР°С‚РµР»СЊ
    public static final RegistryObject<SoundEvent> URAL_ENGINE_IDLE_1P = register("ural_idle_1p");
    public static final RegistryObject<SoundEvent> URAL_ENGINE_IDLE_3P = register("ural_idle_3p");
    public static final RegistryObject<SoundEvent> URAL_RUNNING_3P = register("ural_running_3p");
    public static final RegistryObject<SoundEvent> URAL_RUNNING_1P = register("ural_running_1p");

    //Р”РІРёРіР°С‚РµР»СЊ РјРѕС‚РѕС†РёРєР»
    public static final RegistryObject<SoundEvent> MOTORCYCLE_IDLE_1P = register("motou_idle");
    public static final RegistryObject<SoundEvent> MOTORCYCLE_RUNNING_1P = register("motou_running");

    public static final RegistryObject<SoundEvent> MTLB_ENGINE_IDLE_1P = register("mtlb_idle_1p");
    public static final RegistryObject<SoundEvent> MTLB_ENGINE_IDLE_3P = register("mtlb_idle_3p");
    public static final RegistryObject<SoundEvent> MTLB_ENGINE_RUNNING_1P = register("mtlb_running_1p");
    public static final RegistryObject<SoundEvent> MTLB_ENGINE_RUNNING_3P = register("mtlb_running_3p");

    public static final RegistryObject<SoundEvent> BRDM2_ENGINE_IDLE_1P = register("brdm2_idle_1p");
    public static final RegistryObject<SoundEvent> BRDM2_ENGINE_IDLE_3P = register("brdm2_idle_3p");
    public static final RegistryObject<SoundEvent> BRDM2_ENGINE_RUNNING_1P = register("brdm2_running_1p");
    public static final RegistryObject<SoundEvent> BRDM2_ENGINE_RUNNING_3P = register("brdm2_running_3p");

    public static final RegistryObject<SoundEvent> HIT_HEAVY  = register("hit_heavy");
    public static final RegistryObject<SoundEvent> HIT_MEDIUM = register("hit_medium");
    public static final RegistryObject<SoundEvent> HIT_SMALL  = register("hit_small");

    private static RegistryObject<SoundEvent> register(String name) {
        return REGISTRY.register(name, () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(squadmc.MODID, name)));
    }

    public static void register(IEventBus eventBus) {
        REGISTRY.register(eventBus);
    }
}
