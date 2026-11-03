package tech.squadmc.squadmcor.fx;

/**
 * Наборы эффектов из MTS Official Pack. Каждому соответствует файл assets/squadmc/mts_fx/&lt;file&gt;.json.
 * Какой набор на какой взрыв SBW — и на какое попадание — смотри MtsFxServer.
 */
public enum MtsFxPreset {
    BOMBLET("bomblet"),
    HE_37MM("bullet3700he"),
    ROCKET("bulletrocket"),
    BASIC_BOMB("basicbomb"),   // есть debris
    HEAVY_BOMB("heavy_bomb"),  // есть debris и fire
    ROCKET_TRAIL("rocket_trail"),
    ROCKET_LAUNCH("rocket_launch"),
    HIT_762("bullet762"),
    HIT_AP_37MM("bullet3700solid"),
    MUZZLE_SMALL("muzzle_small"),
    MUZZLE_MEDIUM("muzzle_medium"),
    MUZZLE_HEAVY("muzzle_heavy"),
    GROUND_BLAST("ground_blast"),
    GRAD_BLAST("grad_blast"),
    VEHICLE_SMOKE("vehicle_smoke"),
    VEHICLE_SMOKE_THIN("vehicle_smoke_thin");

    public final String file;

    MtsFxPreset(String file) {
        this.file = file;
    }
}
