package tech.squadmc.squadmcor.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import tech.squadmc.squadmcor.squadmc;

public class ModTabs {
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, squadmc.MODID);

    public static final RegistryObject<CreativeModeTab> SQUADMC_TAB = TABS.register("squadmc_tab", () ->
            CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.squadmc_tab"))
                    .icon(() -> new ItemStack(ModItems.BTR82_ICON.get())) // РСЃРїРѕР»СЊР·СѓРµРј РїСЂРµРґРјРµС‚ РєР°Рє РёРєРѕРЅРєСѓ
                    .displayItems((parameters, output) -> {
                        // Р”РѕР±Р°РІР»СЏРµРј СЃСѓС‰РЅРѕСЃС‚СЊ РІ СЃРїРёСЃРѕРє РїСЂРµРґРјРµС‚РѕРІ РІРєР»Р°РґРєРё
                        output.accept(ModItems.BTR82_ICON.get());
                        output.accept(ModItems.AT4.get());
                        output.accept(com.atsuishio.superbwarfare.item.container.ContainerBlockItem.createInstance(ModEntities.MTLB_ZU23.get()));
                        output.accept(com.atsuishio.superbwarfare.item.container.ContainerBlockItem.createInstance(ModEntities.ZU23.get()));
                        output.accept(ModItems.REPAIR_TORCH.get());
                        tech.squadmc.squadmcor.heli.HeliEntities.addToTab(output);
                        // РўСѓС‚ РјРѕР¶РЅРѕ РґРѕР±Р°РІР»СЏС‚СЊ РґСЂСѓРіРёРµ РїСЂРµРґРјРµС‚С‹
                    })
                    .build()
    );
}
