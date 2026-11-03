// Based on Frontline Combat Pack by Commissar_Mado (see FCP_LICENSE.txt). Modified for SquadMC.
package tech.squadmc.squadmcor.heli;

import com.atsuishio.superbwarfare.item.container.ContainerBlockItem;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.registries.RegistryObject;
import tech.squadmc.squadmcor.init.ModEntities;

import tech.squadmc.squadmcor.heli.entity.vehicle.Littlebird.LittlebirdEntity;
import tech.squadmc.squadmcor.heli.entity.vehicle.Littlebird.LittlebirdArmedEntity;
import tech.squadmc.squadmcor.heli.entity.vehicle.Littlebird.LittlebirdHeavyEntity;
import tech.squadmc.squadmcor.heli.entity.vehicle.Ch53.Mh53Entity;
import tech.squadmc.squadmcor.heli.entity.vehicle.Uh60.Mh60lEntity;
import tech.squadmc.squadmcor.heli.entity.vehicle.Huey.HueyEntity;
import tech.squadmc.squadmcor.heli.entity.vehicle.Huey.HueyM60DoorGunsEntity;
import tech.squadmc.squadmcor.heli.entity.vehicle.Huey.HueyM60GunshipEntity;
import tech.squadmc.squadmcor.heli.entity.vehicle.Huey.HueyM134DoorGunsEntity;
import tech.squadmc.squadmcor.heli.entity.vehicle.Huey.HueyM134GunshipEntity;
import tech.squadmc.squadmcor.heli.entity.vehicle.Huey.VenomEntity;
import tech.squadmc.squadmcor.heli.entity.vehicle.Huey.VenomGunshipEntity;
import tech.squadmc.squadmcor.heli.entity.vehicle.Huey.VenomDoorGunsEntity;
import tech.squadmc.squadmcor.heli.entity.projectile.Hellfire.LockOnHellfireEntity;
import tech.squadmc.squadmcor.heli.entity.projectile.Hellfire.WireGuidedHellfireEntity;
import tech.squadmc.squadmcor.heli.entity.vehicle.Mi17.MI17Entity;
import tech.squadmc.squadmcor.heli.entity.vehicle.Mi8.MI8Entity;
import tech.squadmc.squadmcor.heli.entity.vehicle.Mi17.MI17DoorGunsEntity;
import tech.squadmc.squadmcor.heli.entity.vehicle.Mi8.MI8DoorGunsEntity;
import tech.squadmc.squadmcor.heli.entity.vehicle.Mi8.MI8MTVEntity;
import tech.squadmc.squadmcor.heli.entity.vehicle.Mi8.MI8AMTShEntity;
import tech.squadmc.squadmcor.heli.entity.vehicle.Huey.HueyRocketsEntity;
import tech.squadmc.squadmcor.heli.entity.vehicle.Huey.HueyDoorGunnerM60Entity;
import tech.squadmc.squadmcor.heli.entity.vehicle.Huey.HueyDoorGunnerM134Entity;
import tech.squadmc.squadmcor.heli.entity.vehicle.Uaz.UAZ452Entity;
import tech.squadmc.squadmcor.heli.entity.vehicle.Kamaz.KamazEntity;
import tech.squadmc.squadmcor.heli.entity.vehicle.Ch47.Ch47Entity;
import tech.squadmc.squadmcor.heli.entity.vehicle.Rg33.RG33Entity;
import tech.squadmc.squadmcor.heli.entity.vehicle.M939.M939Entity;
import tech.squadmc.squadmcor.heli.entity.vehicle.ManHx60.ManHx60Entity;
import tech.squadmc.squadmcor.heli.entity.vehicle.Kamaz.KamazNoTentEntity;
import tech.squadmc.squadmcor.heli.entity.vehicle.ManHx60.ManHx60NoTentEntity;
import tech.squadmc.squadmcor.heli.entity.vehicle.Nh90.Nh90Entity;
import tech.squadmc.squadmcor.heli.entity.vehicle.Ah64.Ah64Entity;
import tech.squadmc.squadmcor.heli.entity.vehicle.Bmp.Bmp2.BMP2MEntity;
import tech.squadmc.squadmcor.heli.entity.vehicle.Bmp.Bmp2.BMP2MDEntity;
import tech.squadmc.squadmcor.heli.entity.vehicle.M113.M113Entity;

/** Вертолёты и ракеты Hellfire из Frontline Combat Pack. */
public final class HeliEntities {
    private HeliEntities() {}

    public static final RegistryObject<EntityType<LittlebirdEntity>> LITTLEBIRD = ModEntities.ENTITY_TYPES.register("littlebird",
            () -> EntityType.Builder.of(LittlebirdEntity::new, MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).fireImmune().sized(2f,2f).build("littlebird"));
    public static final RegistryObject<EntityType<LittlebirdArmedEntity>> LITTLEBIRD_ARMED = ModEntities.ENTITY_TYPES.register("littlebird_armed",
            () -> EntityType.Builder.of(LittlebirdArmedEntity::new, MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).fireImmune().sized(2f,2f).build("littlebird_armed"));
    public static final RegistryObject<EntityType<LittlebirdHeavyEntity>> LITTLEBIRD_HEAVY = ModEntities.ENTITY_TYPES.register("littlebird_heavy",
            () -> EntityType.Builder.of(LittlebirdHeavyEntity::new, MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).fireImmune().sized(2f,2f).build("littlebird_heavy"));
    public static final RegistryObject<EntityType<Mh53Entity>> MH53 = ModEntities.ENTITY_TYPES.register("mh53",
            () -> EntityType.Builder.of(Mh53Entity::new, MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).fireImmune().sized(3f,2f).build("mh53"));
    public static final RegistryObject<EntityType<Mh60lEntity>> MH60L = ModEntities.ENTITY_TYPES.register("mh60l",
            () -> EntityType.Builder.of(Mh60lEntity::new, MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).fireImmune().sized(3f,2f).build("mh60l"));
    public static final RegistryObject<EntityType<HueyEntity>> HUEY = ModEntities.ENTITY_TYPES.register("huey",
            () -> EntityType.Builder.of(HueyEntity::new, MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).fireImmune().sized(3f,2f).build("huey"));
    public static final RegistryObject<EntityType<HueyM60DoorGunsEntity>> HUEY_M60_DOOR_GUNS = ModEntities.ENTITY_TYPES.register("huey_m60_door_guns",
            () -> EntityType.Builder.of(HueyM60DoorGunsEntity::new, MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).fireImmune().sized(3f,2f).build("huey_m60_door_guns"));
    public static final RegistryObject<EntityType<HueyM60GunshipEntity>> HUEY_M60_GUNSHIP = ModEntities.ENTITY_TYPES.register("huey_m60_gunship",
            () -> EntityType.Builder.of(HueyM60GunshipEntity::new, MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).fireImmune().sized(3f,2f).build("huey_m60_gunship"));
    public static final RegistryObject<EntityType<HueyM134DoorGunsEntity>> HUEY_M134_DOOR_GUNS = ModEntities.ENTITY_TYPES.register("huey_m134_door_guns",
            () -> EntityType.Builder.of(HueyM134DoorGunsEntity::new, MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).fireImmune().sized(3f,2f).build("huey_m134_door_guns"));
    public static final RegistryObject<EntityType<HueyM134GunshipEntity>> HUEY_M134_GUNSHIP = ModEntities.ENTITY_TYPES.register("huey_m134_gunship",
            () -> EntityType.Builder.of(HueyM134GunshipEntity::new, MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).fireImmune().sized(3f,2f).build("huey_m134_gunship"));
    public static final RegistryObject<EntityType<VenomEntity>> VENOM = ModEntities.ENTITY_TYPES.register("venom",
            () -> EntityType.Builder.of(VenomEntity::new, MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).fireImmune().sized(3f,2f).build("venom"));
    public static final RegistryObject<EntityType<VenomGunshipEntity>> VENOM_GUNSHIP = ModEntities.ENTITY_TYPES.register("venom_gunship",
            () -> EntityType.Builder.of(VenomGunshipEntity::new, MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).fireImmune().sized(3f,2f).build("venom_gunship"));
    public static final RegistryObject<EntityType<VenomDoorGunsEntity>> VENOM_DOOR_GUNS = ModEntities.ENTITY_TYPES.register("venom_door_guns",
            () -> EntityType.Builder.of(VenomDoorGunsEntity::new, MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).fireImmune().sized(3f,2f).build("venom_door_guns"));
    public static final RegistryObject<EntityType<LockOnHellfireEntity>> LOCK_ON_HELLFIRE = ModEntities.ENTITY_TYPES.register("lock_on_hellfire",
            () -> EntityType.Builder.<LockOnHellfireEntity>of(LockOnHellfireEntity::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(false).setTrackingRange(256).setUpdateInterval(1).noSave().fireImmune().sized(0.5f, 0.5f).build("lock_on_hellfire"));
    public static final RegistryObject<EntityType<WireGuidedHellfireEntity>> WIRE_GUIDED_HELLFIRE = ModEntities.ENTITY_TYPES.register("wire_guided_hellfire",
            () -> EntityType.Builder.<WireGuidedHellfireEntity>of(WireGuidedHellfireEntity::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(false).setTrackingRange(256).setUpdateInterval(1).noSave().fireImmune().sized(0.5f, 0.5f).build("wire_guided_hellfire"));
    public static final RegistryObject<EntityType<MI17Entity>> MI17 = ModEntities.ENTITY_TYPES.register("mi17",
            () -> EntityType.Builder.of(MI17Entity::new, MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).fireImmune().sized(3f,2f).build("mi17"));
    public static final RegistryObject<EntityType<MI8Entity>> MI8 = ModEntities.ENTITY_TYPES.register("mi8",
            () -> EntityType.Builder.of(MI8Entity::new, MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).fireImmune().sized(3f,2f).build("mi8"));
    public static final RegistryObject<EntityType<MI17DoorGunsEntity>> MI17_DOOR_GUNS = ModEntities.ENTITY_TYPES.register("mi17_door_guns",
            () -> EntityType.Builder.of(MI17DoorGunsEntity::new, MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).fireImmune().sized(3f,2f).build("mi17_door_guns"));
    public static final RegistryObject<EntityType<MI8DoorGunsEntity>> MI8_DOOR_GUNS = ModEntities.ENTITY_TYPES.register("mi8_door_guns",
            () -> EntityType.Builder.of(MI8DoorGunsEntity::new, MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).fireImmune().sized(3f,2f).build("mi8_door_guns"));
    public static final RegistryObject<EntityType<MI8MTVEntity>> MI8_MTV = ModEntities.ENTITY_TYPES.register("mi8_mtv",
            () -> EntityType.Builder.of(MI8MTVEntity::new, MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).fireImmune().sized(3f,2f).build("mi8_mtv"));
    public static final RegistryObject<EntityType<MI8AMTShEntity>> MI8_AMTSH = ModEntities.ENTITY_TYPES.register("mi8_amtsh",
            () -> EntityType.Builder.of(MI8AMTShEntity::new, MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).fireImmune().sized(3f,2f).build("mi8_amtsh"));
    public static final RegistryObject<EntityType<HueyRocketsEntity>> HUEY_ROCKETS = ModEntities.ENTITY_TYPES.register("huey_rockets",
            () -> EntityType.Builder.of(HueyRocketsEntity::new, MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).fireImmune().sized(3f,2f).build("huey_rockets"));
    public static final RegistryObject<EntityType<HueyDoorGunnerM60Entity>> HUEY_DOOR_GUNNER_M60 = ModEntities.ENTITY_TYPES.register("huey_door_gunner_m60",
            () -> EntityType.Builder.of(HueyDoorGunnerM60Entity::new, MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).fireImmune().sized(3f,2f).build("huey_door_gunner_m60"));
    public static final RegistryObject<EntityType<HueyDoorGunnerM134Entity>> HUEY_DOOR_GUNNER_M134 = ModEntities.ENTITY_TYPES.register("huey_door_gunner_m134",
            () -> EntityType.Builder.of(HueyDoorGunnerM134Entity::new, MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).fireImmune().sized(3f,2f).build("huey_door_gunner_m134"));

    public static final RegistryObject<EntityType<UAZ452Entity>> UAZ_452 = ModEntities.ENTITY_TYPES.register("uaz_452",
            () -> EntityType.Builder.of(UAZ452Entity::new, MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).fireImmune().sized(2f,2f).build("uaz_452"));
    public static final RegistryObject<EntityType<KamazEntity>> KAMAZ = ModEntities.ENTITY_TYPES.register("kamaz",
            () -> EntityType.Builder.of(KamazEntity::new, MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).fireImmune().sized(3f,2f).build("kamaz"));
    public static final RegistryObject<EntityType<Ch47Entity>> CH47 = ModEntities.ENTITY_TYPES.register("ch47",
            () -> EntityType.Builder.of(Ch47Entity::new, MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).fireImmune().sized(3f,2f).build("ch47"));
    public static final RegistryObject<EntityType<BMP2MEntity>> BMP2M = ModEntities.ENTITY_TYPES.register("bmp2m",
            () -> EntityType.Builder.of(BMP2MEntity::new, MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).fireImmune().sized(3f,2f).build("bmp2m"));
    public static final RegistryObject<EntityType<BMP2MDEntity>> BMP2MD = ModEntities.ENTITY_TYPES.register("bmp2md",
            () -> EntityType.Builder.of(BMP2MDEntity::new, MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).fireImmune().sized(3f,2f).build("bmp2md"));
    public static final RegistryObject<EntityType<M113Entity>> M113 = ModEntities.ENTITY_TYPES.register("m113",
            () -> EntityType.Builder.of(M113Entity::new, MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).fireImmune().sized(3f,2f).build("m113"));
    public static final RegistryObject<EntityType<RG33Entity>> RG_33 = ModEntities.ENTITY_TYPES.register("rg_33",
            () -> EntityType.Builder.of(RG33Entity::new, MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).fireImmune().sized(3f,2f).build("rg_33"));
    public static final RegistryObject<EntityType<M939Entity>> M939 = ModEntities.ENTITY_TYPES.register("m939",
            () -> EntityType.Builder.of(M939Entity::new, MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).fireImmune().sized(3f,3f).build("m939"));
    public static final RegistryObject<EntityType<ManHx60Entity>> MAN_HX60 = ModEntities.ENTITY_TYPES.register("man_hx60",
            () -> EntityType.Builder.of(ManHx60Entity::new, MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).fireImmune().sized(3f,3f).build("man_hx60"));
    public static final RegistryObject<EntityType<KamazNoTentEntity>> KAMAZ_NO_TENT = ModEntities.ENTITY_TYPES.register("kamaz_no_tent",
            () -> EntityType.Builder.of(KamazNoTentEntity::new, MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).fireImmune().sized(3f,2f).build("kamaz_no_tent"));
    public static final RegistryObject<EntityType<ManHx60NoTentEntity>> MAN_HX60_NO_TENT = ModEntities.ENTITY_TYPES.register("man_hx60_no_tent",
            () -> EntityType.Builder.of(ManHx60NoTentEntity::new, MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).fireImmune().sized(3f,3f).build("man_hx60_no_tent"));

    public static final RegistryObject<EntityType<Nh90Entity>> NH_90 = ModEntities.ENTITY_TYPES.register("nh_90",
            () -> EntityType.Builder.of(Nh90Entity::new, MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).fireImmune().sized(3.375f,2.5f).build("nh_90"));
    public static final RegistryObject<EntityType<Ah64Entity>> AH_64 = ModEntities.ENTITY_TYPES.register("ah_64",
            () -> EntityType.Builder.of(Ah64Entity::new, MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).fireImmune().sized(3.375f,2.5f).build("ah_64"));

    /** Вызывается из конструктора мода, чтобы регистрации попали в DeferredRegister вовремя. */
    public static void init() {}

    public static void addToTab(CreativeModeTab.Output output) {
        output.accept(ContainerBlockItem.createInstance(BMP2M.get()));
        output.accept(ContainerBlockItem.createInstance(BMP2MD.get()));
        output.accept(ContainerBlockItem.createInstance(M113.get()));
        output.accept(ContainerBlockItem.createInstance(NH_90.get()));
        output.accept(ContainerBlockItem.createInstance(AH_64.get()));
        output.accept(ContainerBlockItem.createInstance(UAZ_452.get()));
        output.accept(ContainerBlockItem.createInstance(KAMAZ.get()));
        output.accept(ContainerBlockItem.createInstance(CH47.get()));
        output.accept(ContainerBlockItem.createInstance(RG_33.get()));
        output.accept(ContainerBlockItem.createInstance(M939.get()));
        output.accept(ContainerBlockItem.createInstance(MAN_HX60.get()));
        output.accept(ContainerBlockItem.createInstance(KAMAZ_NO_TENT.get()));
        output.accept(ContainerBlockItem.createInstance(MAN_HX60_NO_TENT.get()));
        output.accept(ContainerBlockItem.createInstance(LITTLEBIRD.get()));
        output.accept(ContainerBlockItem.createInstance(LITTLEBIRD_ARMED.get()));
        output.accept(ContainerBlockItem.createInstance(LITTLEBIRD_HEAVY.get()));
        output.accept(ContainerBlockItem.createInstance(MH53.get()));
        output.accept(ContainerBlockItem.createInstance(MH60L.get()));
        output.accept(ContainerBlockItem.createInstance(HUEY.get()));
        output.accept(ContainerBlockItem.createInstance(HUEY_M60_DOOR_GUNS.get()));
        output.accept(ContainerBlockItem.createInstance(HUEY_M60_GUNSHIP.get()));
        output.accept(ContainerBlockItem.createInstance(HUEY_M134_DOOR_GUNS.get()));
        output.accept(ContainerBlockItem.createInstance(HUEY_M134_GUNSHIP.get()));
        output.accept(ContainerBlockItem.createInstance(VENOM.get()));
        output.accept(ContainerBlockItem.createInstance(VENOM_GUNSHIP.get()));
        output.accept(ContainerBlockItem.createInstance(VENOM_DOOR_GUNS.get()));
        output.accept(ContainerBlockItem.createInstance(MI17.get()));
        output.accept(ContainerBlockItem.createInstance(MI8.get()));
        output.accept(ContainerBlockItem.createInstance(MI17_DOOR_GUNS.get()));
        output.accept(ContainerBlockItem.createInstance(MI8_DOOR_GUNS.get()));
        output.accept(ContainerBlockItem.createInstance(MI8_MTV.get()));
        output.accept(ContainerBlockItem.createInstance(MI8_AMTSH.get()));
        output.accept(ContainerBlockItem.createInstance(HUEY_ROCKETS.get()));
        output.accept(ContainerBlockItem.createInstance(HUEY_DOOR_GUNNER_M60.get()));
        output.accept(ContainerBlockItem.createInstance(HUEY_DOOR_GUNNER_M134.get()));
    }
}
