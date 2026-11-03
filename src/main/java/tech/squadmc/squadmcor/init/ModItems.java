package tech.squadmc.squadmcor.init;

import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import tech.squadmc.squadmcor.item.launcher.FclAmmoItem;
import tech.squadmc.squadmcor.item.launcher.FclLauncherItem;
import tech.squadmc.squadmcor.item.launcher.FclLauncherSpec;
import tech.squadmc.squadmcor.squadmc;

import java.util.ArrayList;
import java.util.List;

public class ModItems {
    /** Launchers and rounds, in creative tab order. */
    public static final List<RegistryObject<Item>> FCL_ITEMS = new ArrayList<>();

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, squadmc.MODID);

    // РРєРѕРЅРєРё С‚РµС…РЅРёРєРё
    public static final RegistryObject<Item> BTR80_ICON = ITEMS.register("btr80_icon", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> BTR82_ICON = ITEMS.register("btr82_icon", () -> new Item(new Item.Properties()));

    // === РћР”РќРћР РђР—РћР’Р«Р™ Р“Р РђРќРђРўРћРњР•Рў AT4 ===
    public static final RegistryObject<Item> AT4 = launcher(FclLauncherSpec.AT4);
    public static final RegistryObject<Item> REPAIR_TORCH = ITEMS.register("repair_torch", tech.squadmc.squadmcor.repair.RepairTorchItem::new);

    // РџСѓСЃС‚РѕР№ РѕС‚СЃС‚СЂРµР»СЏРЅРЅС‹Р№ С‚СѓР±СѓСЃ
    public static final RegistryObject<Item> AT4_USED = ITEMS.register("at4_used", () ->
            new Item(new Item.Properties().stacksTo(1)));

    // Frontline Combat Launchers
    public static final RegistryObject<Item> FCL_M72 = launcher(FclLauncherSpec.FCL_M72);
    public static final RegistryObject<Item> FCL_RPG26 = launcher(FclLauncherSpec.FCL_RPG26);
    public static final RegistryObject<Item> FCL_RPG28 = launcher(FclLauncherSpec.FCL_RPG28);
    public static final RegistryObject<Item> FCL_DZJ08 = launcher(FclLauncherSpec.FCL_DZJ08);
    public static final RegistryObject<Item> FCL_PANZERFAUST3 = launcher(FclLauncherSpec.FCL_PANZERFAUST3);
    public static final RegistryObject<Item> FCL_PF98 = launcher(FclLauncherSpec.FCL_PF98);
    public static final RegistryObject<Item> FCL_CARLGUSTAF = launcher(FclLauncherSpec.FCL_CARLGUSTAF);
    public static final RegistryObject<Item> FCL_CARLGUSTAF_SCOPE = launcher(FclLauncherSpec.FCL_CARLGUSTAF_SCOPE);
    public static final RegistryObject<Item> FCL_CARLGUSTAFM4 = launcher(FclLauncherSpec.FCL_CARLGUSTAFM4);
    public static final RegistryObject<Item> FCL_CARLGUSTAFM4_SCOPE = launcher(FclLauncherSpec.FCL_CARLGUSTAFM4_SCOPE);
    public static final RegistryObject<Item> FCL_RPG29 = launcher(FclLauncherSpec.FCL_RPG29);
    public static final RegistryObject<Item> FCL_RPG29_SCOPE = launcher(FclLauncherSpec.FCL_RPG29_SCOPE);
    public static final RegistryObject<Item> FCL_RPG29M = launcher(FclLauncherSpec.FCL_RPG29M);
    public static final RegistryObject<Item> FCL_RPG29M_SCOPE = launcher(FclLauncherSpec.FCL_RPG29M_SCOPE);
    public static final RegistryObject<Item> FCL_RPG7V2 = launcher(FclLauncherSpec.FCL_RPG7V2);
    public static final RegistryObject<Item> FCL_RPG7V2_SCOPE = launcher(FclLauncherSpec.FCL_RPG7V2_SCOPE);
    public static final RegistryObject<Item> FCL_SMAW = launcher(FclLauncherSpec.FCL_SMAW);
    public static final RegistryObject<Item> FCL_SMAW_SCOPE = launcher(FclLauncherSpec.FCL_SMAW_SCOPE);

    static {
        for (String id : new String[]{
                "fcl_ammo9x51",
                "fcl_at4_rocket",
                "fcl_carlgustaf_he448",
                "fcl_carlgustaf_heat551crs",
                "fcl_carlgustaf_heat758",
                "fcl_carlgustaf_hedp502",
                "fcl_dzj08_round",
                "fcl_m72_rocket",
                "fcl_panzerfaust3_bunkerfaust",
                "fcl_panzerfaust3_heat",
                "fcl_panzerfaust3_tandem",
                "fcl_pf98_he",
                "fcl_pf98_heat",
                "fcl_pf98_hei",
                "fcl_rpg26_rocket",
                "fcl_rpg28_rocket",
                "fcl_rpg29_mg29",
                "fcl_rpg29_og29",
                "fcl_rpg29_pg29v",
                "fcl_rpg7v2_og7v",
                "fcl_rpg7v2_pg7vm",
                "fcl_rpg7v2_pg7vr",
                "fcl_smaw_heaa",
                "fcl_smaw_hedm",
        }) {
            FCL_ITEMS.add(ITEMS.register(id, () -> new FclAmmoItem(id)));
        }
    }

    private static RegistryObject<Item> launcher(FclLauncherSpec spec) {
        RegistryObject<Item> item = ITEMS.register(spec.id, () -> new FclLauncherItem(spec));
        FCL_ITEMS.add(item);
        return item;
    }
}
