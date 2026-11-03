package tech.squadmc.squadmcor.item.launcher;

import java.util.HashMap;
import java.util.Map;

/**
 * Per-launcher data ported from the Frontline Combat Launchers pack.
 * Index 0 of the scope arrays is the launcher itself, index 1 is its optic module. A launcher with
 * an optic exists as two separate items, one for each.
 * Aim offsets are generated from each model's "scope" bone and its first-person transform.
 */
public final class FclLauncherSpec {
    public static final FclLauncherSpec AT4 = new FclLauncherSpec("at4", 6.0f,
            new String[]{"reload"},
            new String[]{null},
            new String[]{"fire"},
            new String[][]{new String[]{}},
            new String[]{},
            new String[]{},
            new float[][]{{4.724f,0.449f,3.62f}},
            new double[]{0},
            new String[]{null},
            2.0f);
    public static final FclLauncherSpec FCL_M72 = new FclLauncherSpec("fcl_m72", 3.5f,
            new String[]{"reload"},
            new String[]{null},
            new String[]{"fire"},
            new String[][]{new String[]{}},
            new String[]{},
            new String[]{},
            new float[][]{{6.543f,0.334f,6.52f}},
            new double[]{0},
            new String[]{null},
            2.0f);
    public static final FclLauncherSpec FCL_RPG26 = new FclLauncherSpec("fcl_rpg26", 3.0f,
            new String[]{"reload"},
            new String[]{null},
            new String[]{"fire"},
            new String[][]{new String[]{}},
            new String[]{},
            new String[]{},
            new float[][]{{9.598f,-1.65f,16.02f}},
            new double[]{0},
            new String[]{null},
            2.0f);
    public static final FclLauncherSpec FCL_RPG28 = new FclLauncherSpec("fcl_rpg28", 9.0f,
            new String[]{"reload"},
            new String[]{null},
            new String[]{"fire"},
            new String[][]{new String[]{}},
            new String[]{},
            new String[]{},
            new float[][]{{6.747f,5.045f,10.82f}},
            new double[]{1.75},
            new String[]{"fcl_rpg28_pip"},
            2.0f);
    public static final FclLauncherSpec FCL_DZJ08 = new FclLauncherSpec("fcl_dzj08", 3.5f,
            new String[]{"reload"},
            new String[]{null},
            new String[]{"fire"},
            new String[][]{new String[]{}},
            new String[]{},
            new String[]{},
            new float[][]{{4.481f,2.877f,9.82f}},
            new double[]{1.75},
            new String[]{"fcl_dzj08_pip"},
            2.0f);
    public static final FclLauncherSpec FCL_PANZERFAUST3 = new FclLauncherSpec("fcl_panzerfaust3", 2.0f,
            new String[]{"reload","reload","reload"},
            new String[]{null,null,null},
            new String[]{"fire","fire","fire"},
            new String[][]{new String[]{"tandem"},new String[]{"heat"},new String[]{"bunkerfaust"}},
            new String[]{},
            new String[]{},
            new float[][]{{5.285f,0.895f,3.32f}},
            new double[]{1.75},
            new String[]{"fcl_panzerfaust3_pip"},
            2.0f);
    public static final FclLauncherSpec FCL_PF98 = new FclLauncherSpec("fcl_pf98", 4.0f,
            new String[]{"reloadheat","reloadhei","reloadhei"},
            new String[]{null,null,null},
            new String[]{"fire","fire","fire"},
            new String[][]{new String[]{"heat"},new String[]{"hei"},new String[]{"hei"}},
            new String[]{},
            new String[]{},
            new float[][]{{4.369f,6.642f,11.47f}},
            new double[]{2.75},
            new String[]{"fcl_pf98_pip"},
            2.0f);
    public static final FclLauncherSpec FCL_CARLGUSTAF = new FclLauncherSpec("fcl_carlgustaf", 6.6f,
            new String[]{"reloadheat551crs","reloadheat758","reloadhedp502","reloadhe448"},
            new String[]{null,null,null,null},
            new String[]{"fire","fire","fire","fire"},
            new String[][]{new String[]{"heat551crs"},new String[]{"heat758"},new String[]{"hedp502"},new String[]{"he448"}},
            new String[]{"rearsightunflipped","foresightunflipped"},
            new String[]{"rearsightflipped","foresightflipped","Optic"},
            new float[][]{{1.874f,10.606f,21.07f},{3.438f,6.888f,12.07f}},
            new double[]{0,1.75},
            new String[]{null,"fcl_carlgustaf_pip"},
            2.0f);
    public static final FclLauncherSpec FCL_CARLGUSTAFM4 = new FclLauncherSpec("fcl_carlgustafm4", 6.6f,
            new String[]{"reloadheat551crs","reloadheat758","reloadhedp502","reloadhe448"},
            new String[]{null,null,null,null},
            new String[]{"fire","fire","fire","fire"},
            new String[][]{new String[]{"heat551crs"},new String[]{"heat758"},new String[]{"hedp502"},new String[]{"he448"}},
            new String[]{},
            new String[]{"sightwire","Optic"},
            new float[][]{{3.717f,5.369f,30.07f},{2.448f,4.593f,23.07f}},
            new double[]{0,2.25},
            new String[]{null,"fcl_carlgustafm4_pip"},
            2.0f);
    public static final FclLauncherSpec FCL_RPG29 = new FclLauncherSpec("fcl_rpg29", 2.5f,
            new String[]{"reloadpg","reloadog","reloadmg"},
            new String[]{null,null,null},
            new String[]{"fire","fire","fire"},
            new String[][]{new String[]{"pg"},new String[]{"og"},new String[]{"mg"}},
            new String[]{"rearsightunflipped","foresightunflipped"},
            new String[]{"rearsightflipped","foresightflipped","Optic"},
            new float[][]{{8.292f,2.784f,19.82f},{4.19f,3.806f,12.57f}},
            new double[]{0,1.45},
            new String[]{null,"fcl_1p38_pip"},
            2.0f);
    public static final FclLauncherSpec FCL_RPG29M = new FclLauncherSpec("fcl_rpg29m", 2.5f,
            new String[]{"reloadpg","reloadog","reloadmg"},
            new String[]{null,null,null},
            new String[]{"fire","fire","fire"},
            new String[][]{new String[]{"pg"},new String[]{"og"},new String[]{"mg"}},
            new String[]{"rearsightunflipped","foresightunflipped"},
            new String[]{"rearsightflipped","foresightflipped","Optic"},
            new float[][]{{11.363f,3.511f,13.32f},{6.457f,3.887f,5.02f}},
            new double[]{0,2.75},
            new String[]{null,"fcl_1p38_pip"},
            2.0f);
    public static final FclLauncherSpec FCL_RPG7V2 = new FclLauncherSpec("fcl_rpg7v2", 4.5f,
            new String[]{"reload","reload","reload"},
            new String[]{null,null,null},
            new String[]{"fire","fire","fire"},
            new String[][]{new String[]{"pg7vm"},new String[]{"pg7vr"},new String[]{"og7v"}},
            new String[]{"sight"},
            new String[]{"sightflipped","Optic"},
            new float[][]{{10.532f,-0.334f,3.82f},{7.881f,0.535f,3.82f}},
            new double[]{0,1.45},
            new String[]{null,"fcl_pgo7_pip"},
            2.0f);
    public static final FclLauncherSpec FCL_SMAW = new FclLauncherSpec("fcl_smaw", 2.5f,
            new String[]{"reload","reload","reloadrifleempty"},
            new String[]{null,null,"reloadrifle"},
            new String[]{"fire","fire","firerifle"},
            new String[][]{new String[]{},new String[]{},new String[]{}},
            new String[]{"rearsight"},
            new String[]{"Optic"},
            new float[][]{{10.507f,-0.811f,5.82f},{7.817f,-1.104f,-2.18f}},
            new double[]{0,2.55},
            new String[]{null,"fcl_smaw_pip"},
            2.0f);

    public static final FclLauncherSpec FCL_CARLGUSTAF_SCOPE = FCL_CARLGUSTAF.withOptic();
    public static final FclLauncherSpec FCL_CARLGUSTAFM4_SCOPE = FCL_CARLGUSTAFM4.withOptic();
    public static final FclLauncherSpec FCL_RPG29_SCOPE = FCL_RPG29.withOptic();
    public static final FclLauncherSpec FCL_RPG29M_SCOPE = FCL_RPG29M.withOptic();
    public static final FclLauncherSpec FCL_RPG7V2_SCOPE = FCL_RPG7V2.withOptic();
    public static final FclLauncherSpec FCL_SMAW_SCOPE = FCL_SMAW.withOptic();

    public final String id;
    /** 0: the launcher's own sights, 1: its optic module. */
    public final int scope;
    public final float displayRoll;
    public final String[] reload;
    public final String[] reloadNormal;
    public final String[] fire;
    public final Map<String, Integer> ammoParts = new HashMap<>();
    public final Map<String, Integer> scopeParts = new HashMap<>();
    public final float[][] aim;
    public final double[] zoom;
    public final String[] pip;
    public final float armScale;

    private FclLauncherSpec(String id, float displayRoll, String[] reload, String[] reloadNormal, String[] fire,
                            String[][] ammoBones, String[] ironOnly, String[] opticOnly,
                            float[][] aim, double[] zoom, String[] pip, float armScale) {
        this.id = id;
        this.scope = 0;
        this.displayRoll = displayRoll;
        this.reload = reload;
        this.reloadNormal = reloadNormal;
        this.fire = fire;
        this.aim = aim;
        this.zoom = zoom;
        this.pip = pip;
        this.armScale = armScale;
        for (int ammo = 0; ammo < ammoBones.length; ammo++) {
            for (String bone : ammoBones[ammo]) {
                this.ammoParts.merge(bone, 1 << ammo, (a, b) -> a | b);
            }
        }
        for (String bone : ironOnly) this.scopeParts.put(bone, 0);
        for (String bone : opticOnly) this.scopeParts.put(bone, 1);
    }

    private FclLauncherSpec(FclLauncherSpec base, String id, int scope) {
        this.id = id;
        this.scope = scope;
        this.displayRoll = base.displayRoll;
        this.reload = base.reload;
        this.reloadNormal = base.reloadNormal;
        this.fire = base.fire;
        this.ammoParts.putAll(base.ammoParts);
        this.scopeParts.putAll(base.scopeParts);
        this.aim = base.aim;
        this.zoom = base.zoom;
        this.pip = base.pip;
        this.armScale = base.armScale;
    }

    private FclLauncherSpec withOptic() {
        return new FclLauncherSpec(this, this.id + "_scope", 1);
    }

    public float[] aimOffset() {
        return this.aim[this.scope];
    }

    public double customZoom() {
        return this.zoom[this.scope];
    }

    public String reticle() {
        return this.pip[this.scope];
    }

    private static int clampIndex(int index, int length) {
        return Math.max(0, Math.min(index, length - 1));
    }

    public String reloadAnimation(int ammo, boolean normal) {
        int i = clampIndex(ammo, this.reload.length);
        String name = normal && this.reloadNormal[i] != null ? this.reloadNormal[i] : this.reload[i];
        return "animation.model." + name;
    }

    public String fireAnimation(int ammo) {
        return "animation.model." + this.fire[clampIndex(ammo, this.fire.length)];
    }
}
