package tech.squadmc.squadmcor.client.thermal;

import net.minecraft.resources.ResourceLocation;
import tech.squadmc.squadmcor.squadmc;

import javax.annotation.Nullable;
import java.util.Map;

/** Тепловые приборы. Есть только у техники, которая несёт такой прибор в реальности. */
public enum ThermalSight {
    // НАТО: зелёный экран, чёткость зависит от поколения прибора.
    /** Современный прицел высокой чёткости: CITV и GPS Abrams, PERI Leopard 2A7V, Puma, FLIR MH-60. */
    NATO_HD("nato_hd", "FLIR"),
    /** Прибор предыдущего поколения: M2 Bradley, LAV-25A2. */
    NATO_GEN2("nato_gen2", "TIS"),
    /** Боевой модуль: M151 Protector, CROWS. Меньше разрешение. */
    NATO_RWS("nato_rws", "RWS"),
    /** ITAS комплекса TOW. */
    NATO_ITAS("nato_itas", "ITAS"),
    /** TADS Apache. */
    TADS("tads", "TADS FLIR"),
    /** Российские приборы: белый экран, заметный шум и строки. */
    RU("ru", "ТПВ");

    private static final Map<String, ThermalSight> VEHICLES = Map.ofEntries(
            Map.entry("m1a2", NATO_HD),
            Map.entry("m1a2_sand", NATO_HD),
            Map.entry("leopard_2a7v", NATO_HD),
            Map.entry("puma", NATO_HD),
            Map.entry("bradley", NATO_GEN2),
            Map.entry("bradley_sand", NATO_GEN2),
            Map.entry("lav25", NATO_GEN2),
            Map.entry("stryker_m2", NATO_RWS),
            Map.entry("rg_33", NATO_RWS),
            Map.entry("humvee_tow", NATO_ITAS),
            Map.entry("t72av", RU),
            Map.entry("t64", RU),
            Map.entry("t80", RU),
            Map.entry("gaz_tigr_rws", RU),
            Map.entry("sprut", RU),
            Map.entry("bmd4", RU),
            Map.entry("bmp2m", RU),
            Map.entry("mi8_amtsh", RU),
            Map.entry("ah_64", TADS),
            Map.entry("mh60l", NATO_HD),
            Map.entry("littlebird", NATO_HD),
            Map.entry("littlebird_heavy", NATO_HD),
            Map.entry("venom", NATO_HD),
            Map.entry("venom_door_guns", NATO_HD),
            Map.entry("venom_gunship", NATO_HD),
            Map.entry("nh_90", NATO_HD));

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
