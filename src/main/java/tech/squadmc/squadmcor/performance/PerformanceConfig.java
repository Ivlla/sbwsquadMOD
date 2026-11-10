package tech.squadmc.squadmcor.performance;

import net.minecraftforge.common.ForgeConfigSpec;

public final class PerformanceConfig {
    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.BooleanValue PROJECTILE_CHUNKS;
    public static final ForgeConfigSpec.IntValue MAX_TICKETS, STARTS_PER_TICK, RETAIN_TICKS, WAIT_TICKS, PATH_LIMIT;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.push("projectileChunks");
        PROJECTILE_CHUNKS = builder.comment("Shared asynchronous chunk tickets for all SBW and squadmc projectiles.",
                "Restart the server after changing this setting; replaces SBW's projectile loader while enabled.")
                .worldRestart().define("enabled", true);
        MAX_TICKETS = builder.comment("Global ticket-centre limit across ALL dimensions. Each ticket also loads neighbouring chunks.")
                .defineInRange("maxTickets", 64, 16, 1024);
        STARTS_PER_TICK = builder.comment("Maximum new tickets per server tick, shared by all projectiles and dimensions.")
                .defineInRange("startsPerTick", 2, 1, 32);
        RETAIN_TICKS = builder.comment("Release tickets this many ticks after their last request.")
                .defineInRange("retainTicks", 40, 5, 200);
        WAIT_TICKS = builder.comment("Pause a projectile at an unloaded boundary; discard it without an explosion after this timeout.",
                "This overload safeguard prevents indefinite accumulation of stalled projectiles.")
                .defineInRange("maxWaitTicks", 200, 20, 1200);
        PATH_LIMIT = builder.comment("Maximum chunks in one projectile path, including collision halo. Longer paths wait, then time out.")
                .defineInRange("pathLimit", 128, 16, 1024);
        builder.pop();
        SPEC = builder.build();
    }

    private PerformanceConfig() {}
}
