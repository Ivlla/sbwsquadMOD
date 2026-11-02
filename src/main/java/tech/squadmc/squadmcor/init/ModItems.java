package tech.squadmc.squadmcor.init;

import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import tech.squadmc.squadmcor.item.AT4Item;
import tech.squadmc.squadmcor.squadmc;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, squadmc.MODID);

    // РРєРѕРЅРєРё С‚РµС…РЅРёРєРё
    public static final RegistryObject<Item> BTR80_ICON = ITEMS.register("btr80_icon", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> BTR82_ICON = ITEMS.register("btr82_icon", () -> new Item(new Item.Properties()));

    // === РћР”РќРћР РђР—РћР’Р«Р™ Р“Р РђРќРђРўРћРњР•Рў AT4 ===
    public static final RegistryObject<Item> AT4 = ITEMS.register("at4", AT4Item::new);

    // РџСѓСЃС‚РѕР№ РѕС‚СЃС‚СЂРµР»СЏРЅРЅС‹Р№ С‚СѓР±СѓСЃ
    public static final RegistryObject<Item> AT4_USED = ITEMS.register("at4_used", () ->
            new Item(new Item.Properties().stacksTo(1)));
}
