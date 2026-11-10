package tech.squadmc.squadmcor;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import tech.squadmc.squadmcor.init.*;
import tech.squadmc.squadmcor.network.ModNetworking;

@Mod(squadmc.MODID)
public class squadmc {
    public static final String MODID = "squadmc";

    public squadmc() {
        net.minecraftforge.fml.ModLoadingContext.get().registerConfig(
                net.minecraftforge.fml.config.ModConfig.Type.SERVER,
                tech.squadmc.squadmcor.performance.PerformanceConfig.SPEC, "squadmc-performance-server.toml");
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        ModItems.ITEMS.register(bus);
        SpikeContent.register(bus);
        tech.squadmc.squadmcor.heli.HeliEntities.init();
        ModEntities.register(bus);
        ModSounds.register(bus);
        ModTabs.TABS.register(bus);
        ModNetworking.register();

        MinecraftForge.EVENT_BUS.register(this);
    }
}
