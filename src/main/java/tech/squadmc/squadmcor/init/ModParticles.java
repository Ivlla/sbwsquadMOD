package tech.squadmc.squadmcor.init;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import tech.squadmc.squadmcor.squadmc;

public class ModParticles {
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES =
            DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, squadmc.MODID);

    // Р РµРіРёСЃС‚СЂРёСЂСѓРµРј СЃР°РјСѓ С‡Р°СЃС‚РёС†Сѓ РІСЃРїС‹С€РєРё РІС‹СЃС‚СЂРµР»Р°
    public static final RegistryObject<SimpleParticleType> MUZZLE_FLASH =
            PARTICLE_TYPES.register("muzzle_flash", () -> new SimpleParticleType(false));

    public static void register(IEventBus eventBus) {
        PARTICLE_TYPES.register(eventBus);
    }
}
