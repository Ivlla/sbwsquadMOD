package tech.squadmc.squadmcor.init;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import tech.squadmc.squadmcor.entity.projectile.SpikeMissileEntity;
import tech.squadmc.squadmcor.item.SpikeItem;
import tech.squadmc.squadmcor.network.SpikeNetwork;
import tech.squadmc.squadmcor.squadmc;

@Mod.EventBusSubscriber(modid = squadmc.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class SpikeContent {
    private static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, squadmc.MODID);
    private static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, squadmc.MODID);

    public static final RegistryObject<Item> SPIKE = ITEMS.register("spike", SpikeItem::new);
    public static final RegistryObject<EntityType<SpikeMissileEntity>> SPIKE_MISSILE =
            ENTITIES.register("spike_missile", () ->
                    EntityType.Builder.<SpikeMissileEntity>of(SpikeMissileEntity::new, MobCategory.MISC)
                            .sized(0.35F, 0.35F).clientTrackingRange(256).updateInterval(1)
                            .build(squadmc.MODID + ":spike_missile"));

    private SpikeContent() {}

    public static void register(IEventBus bus) {
        ITEMS.register(bus);
        ENTITIES.register(bus);
        bus.addListener(SpikeContent::commonSetup);
    }

    private static void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(SpikeNetwork::register);
    }

    @SubscribeEvent
    public static void creativeTab(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey().location().equals(new ResourceLocation(squadmc.MODID, "squadmc_tab"))) {
            event.accept(SPIKE.get());
        }
    }
}
