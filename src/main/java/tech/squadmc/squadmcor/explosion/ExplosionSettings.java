package tech.squadmc.squadmcor.explosion;

import net.minecraftforge.common.ForgeConfigSpec;

public final class ExplosionSettings {
    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.DoubleValue MORTAR_RADIUS_MULTIPLIER;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        MORTAR_RADIUS_MULTIPLIER = builder
                .comment("SBW mortar explosion radius multiplier. 1.0 = original, 1.5 = 50% larger.",
                        "Applied when the explosion is built; does not change saved projectile data.")
                .defineInRange("mortarRadiusMultiplier", 1.5, 0.1, 4.0);
        SPEC = builder.build();
    }

    private ExplosionSettings() {}
}
