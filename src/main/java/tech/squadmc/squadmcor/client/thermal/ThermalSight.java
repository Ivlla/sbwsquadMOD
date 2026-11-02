package tech.squadmc.squadmcor.client.thermal;

import net.minecraft.resources.ResourceLocation;
import tech.squadmc.squadmcor.squadmc;

import javax.annotation.Nullable;
import java.util.Map;

/** Тепловые приборы. Есть только у техники, которая несёт такой прибор в реальности. */
public enum ThermalSight {
    /** Современный прицел высокой чёткости: CITV и GPS Abrams, PERI Leopard 2A7V, Puma, FLIR MH-60 и UH-1Y. */
    WHITE_HOT_HD("whot_hd", "FLIR WHT"),
    /** Прибор предыдущего поколения: M2 Bradley, LAV-25A2. */
    WHITE_HOT_GEN2("whot_gen2", "TIS WHT"),
    /** Боевой модуль: M151 Protector, CROWS. Меньше разрешение. */
    RWS("rws", "RWS WHT"),
    /** ITAS комплекса TOW в режиме чёрно-горячий. */
    BLACK_HOT("bhot", "ITAS BLK"),
    /** Российские приборы: зелёный экран, заметный шум и строки. */
    GREEN_RU("green_ru", "ТПВ"),
    /** TADS Apache: монохромный зелёный, но чётче российских. */
    TADS("tads", "TADS FLIR");

    private static final Map<String, ThermalSight> VEHICLES = Map.ofEntries(
            Map.entry("m1a2", WHITE_HOT_HD),
            Map.entry("m1a2_sand", WHITE_HOT_HD),
            Map.entry("leopard_2a7v", WHITE_HOT_HD),
            Map.entry("puma", WHITE_HOT_HD),
            Map.entry("bradley", WHITE_HOT_GEN2),
            Map.entry("bradley_sand", WHITE_HOT_GEN2),
            Map.entry("lav25", WHITE_HOT_GEN2),
            Map.entry("stryker_m2", RWS),
            Map.entry("rg_33", RWS),
            Map.entry("humvee_tow", BLACK_HOT),
            Map.entry("gaz_tigr_rws", GREEN_RU),
            Map.entry("sprut", GREEN_RU),
            Map.entry("bmd4", GREEN_RU),
            Map.entry("mi8_amtsh", GREEN_RU),
            Map.entry("ah_64", TADS),
            Map.entry("mh60l", WHITE_HOT_HD),
            Map.entry("littlebird", WHITE_HOT_HD),
            Map.entry("littlebird_heavy", WHITE_HOT_HD),
            Map.entry("venom", WHITE_HOT_HD),
            Map.entry("venom_door_guns", WHITE_HOT_HD),
            Map.entry("venom_gunship", WHITE_HOT_HD),
            Map.entry("nh_90", WHITE_HOT_HD));

    private final ResourceLocation effect;
    private final String label;

    ThermalSight(String file, String label) {
        this.effect = new ResourceLocation(squadmc.MODID, "shaders/post/thermal_" + file + ".json");
        this.label = label;
    }

    @Nullable
    public static ThermalSight of(String vehicleId) {
        return VEHICLES.get(vehicleId);
    }

    public ResourceLocation effect() {
        return effect;
    }

    public String label() {
        return label;
    }
}
