package tech.squadmc.squadmcor.init;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import tech.squadmc.squadmcor.entity.*;
import tech.squadmc.squadmcor.entity.empl.*;
import tech.squadmc.squadmcor.entity.projectile.MalyutkaEntity;
import tech.squadmc.squadmcor.smoke.VehicleSmokeCloudEntity;
import tech.squadmc.squadmcor.squadmc;

public class ModEntities {

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, squadmc.MODID);

    public static final RegistryObject<EntityType<btr82Entity>> BTR82 =
            ENTITY_TYPES.register("btr82", () -> EntityType.Builder.of(btr82Entity::new, MobCategory.MISC)
                    .sized(3.0F, 2.5F)
                    .clientTrackingRange(360)
                    .build("btr82"));

    public static final RegistryObject<EntityType<TDADummyProjectile>> TDA_DUMMY =
            ENTITY_TYPES.register("tda_dummy", () -> EntityType.Builder.<TDADummyProjectile>of(TDADummyProjectile::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(360)
                    .build("tda_dummy"));

    // --- Объёмный дым техники (перенесено из aasgranate, полностью автономно) ---
    public static final RegistryObject<EntityType<VehicleSmokeCloudEntity>> VEHICLE_SMOKE_CLOUD =
            ENTITY_TYPES.register("vehicle_smoke_cloud", () -> EntityType.Builder.of(VehicleSmokeCloudEntity::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .clientTrackingRange(360)
                    .updateInterval(10)
                    .fireImmune()
                    .build("vehicle_smoke_cloud"));

    public static final RegistryObject<EntityType<btr80Entity>> BTR80 =
            ENTITY_TYPES.register("btr80", () -> EntityType.Builder.of(btr80Entity::new, MobCategory.MISC)
                    .sized(3.0F, 2.5F)
                    .clientTrackingRange(360)
                    .build("btr80"));


    public static final RegistryObject<EntityType<bmp2Entity>> BMP2 =
            ENTITY_TYPES.register("bmp2", () -> EntityType.Builder.of(bmp2Entity::new, MobCategory.MISC)
                    .sized(3.0F, 2.5F)
                    .clientTrackingRange(360)
                    .build("bmp2"));



    public static final RegistryObject<EntityType<KonkursDummyProjectile>> KONKURS_DUMMY =
            ENTITY_TYPES.register("konkurs_dummy", () -> EntityType.Builder.<KonkursDummyProjectile>of(KonkursDummyProjectile::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(360)
                    .build("konkurs_dummy"));

    public static final RegistryObject<EntityType<FenekEntity>> FENEK =
            ENTITY_TYPES.register("fenek", () -> EntityType.Builder.<FenekEntity>of(FenekEntity::new, MobCategory.MISC)
                    .sized(2.8F, 2.5F)
                    .clientTrackingRange(360)
                    .build("fenek"));


    public static final RegistryObject<EntityType<lav25Entity>> LAV25 =
            ENTITY_TYPES.register("lav25", () -> EntityType.Builder.of(lav25Entity::new, MobCategory.MISC)
                    .sized(3.0F, 2.5F)
                    .clientTrackingRange(360)
                    .build("lav25"));

    public static final RegistryObject<EntityType<bradleyEntity>> BRADLEY =
            ENTITY_TYPES.register("bradley", () -> EntityType.Builder.of(bradleyEntity::new, MobCategory.MISC)
                    .sized(3.2F, 2.8F)
                    .clientTrackingRange(360)
                    .build("bradley"));


    public static final RegistryObject<EntityType<TowDummyProjectile>> TOW_DUMMY =
            ENTITY_TYPES.register("tow_dummy", () -> EntityType.Builder.<TowDummyProjectile>of(TowDummyProjectile::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .build("tow_dummy"));

    public static final RegistryObject<EntityType<bmd4Entity>> BMD4 =
            ENTITY_TYPES.register("bmd4", () -> EntityType.Builder.<bmd4Entity>of(bmd4Entity::new, MobCategory.MISC)
                    .sized(3.875F, 2.8125F)
                    .clientTrackingRange(360)
                    .build("bmd4"));

    public static final RegistryObject<EntityType<T72AVEntity>> T72AV =
            ENTITY_TYPES.register("t72av", () -> EntityType.Builder.<T72AVEntity>of(T72AVEntity::new, MobCategory.MISC)
                    .sized(3.875F, 2.8125F)
                    .clientTrackingRange(360)
                    .build("t72av"));


    public static final RegistryObject<EntityType<Bmp1Entity>> BMP1 =
            ENTITY_TYPES.register("bmp1", () -> EntityType.Builder.<Bmp1Entity>of(Bmp1Entity::new, MobCategory.MISC)
                    .sized(3.2F, 2.5F)
                    .clientTrackingRange(360)
                    .build("bmp1"));

    public static final RegistryObject<EntityType<MalyutkaEntity>> MALYUTKA =
            ENTITY_TYPES.register("malyutka", () -> EntityType.Builder.<MalyutkaEntity>of(MalyutkaEntity::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .clientTrackingRange(360)
                    .updateInterval(20)
                    .build("malyutka"));

    public static final RegistryObject<EntityType<t80Entity>> T80 =
            ENTITY_TYPES.register("t80", () -> EntityType.Builder.<t80Entity>of(t80Entity::new, MobCategory.MISC)
                    .sized(3.2F, 2.5F)
                    .clientTrackingRange(360)
                    .build("t80"));

    public static final RegistryObject<EntityType<HumveeEntity>> HUMVEE =
            ENTITY_TYPES.register("humvee", () -> EntityType.Builder.<HumveeEntity>of(HumveeEntity::new, MobCategory.MISC)
                    .sized(2.8F, 2.1F)
                    .clientTrackingRange(360)
                    .build("humvee"));

    public static final RegistryObject<EntityType<T64Entity>> T64 =
            ENTITY_TYPES.register("t64", () -> EntityType.Builder.<T64Entity>of(T64Entity::new, MobCategory.MISC)
                    .sized(3.875F, 2.8125F)
                    .clientTrackingRange(360)
                    .build("t64"));

    public static final RegistryObject<EntityType<HumveeTowEntity>> HUMVEE_TOW =
            ENTITY_TYPES.register("humvee_tow", () -> EntityType.Builder.<HumveeTowEntity>of(HumveeTowEntity::new, MobCategory.MISC)
                    .sized(2.8F, 2.1F)
                    .clientTrackingRange(360)
                    .build("humvee_tow"));


    public static final RegistryObject<EntityType<HumveeM2Entity>> HUMVEE_M2 =
            ENTITY_TYPES.register("humvee_m2", () -> EntityType.Builder.<HumveeM2Entity>of(HumveeM2Entity::new, MobCategory.MISC)
                    .sized(2.8F, 2.1F)
                    .clientTrackingRange(360)
                    .build("humvee_m2"));

    public static final RegistryObject<EntityType<Gaz_tigrEntity>> GAZ_TIGR =
            ENTITY_TYPES.register("gaz_tigr", () -> EntityType.Builder.<Gaz_tigrEntity>of(Gaz_tigrEntity::new, MobCategory.MISC)
                    .sized(2.8F, 2.1F)
                    .clientTrackingRange(360)
                    .build("gaz_tigr"));


    public static final RegistryObject<EntityType<Gaz_tigrMGEntity>> GAZ_TIGR_MG =
            ENTITY_TYPES.register("gaz_tigr_mg", () -> EntityType.Builder.<Gaz_tigrMGEntity>of(Gaz_tigrMGEntity::new, MobCategory.MISC)
                    .sized(2.8F, 2.1F)
                    .clientTrackingRange(360)
                    .build("gaz_tigr_mg"));

    public static final RegistryObject<EntityType<Gaz_tigrRWSEntity>> GAZ_TIGR_RWS =
            ENTITY_TYPES.register("gaz_tigr_rws", () -> EntityType.Builder.<Gaz_tigrRWSEntity>of(Gaz_tigrRWSEntity::new, MobCategory.MISC)
                    .sized(2.8F, 2.1F)
                    .clientTrackingRange(360)
                    .build("gaz_tigr_rws"));

    public static final RegistryObject<EntityType<ToyotaEntity>> TOYOTA =
            ENTITY_TYPES.register("toyota", () -> EntityType.Builder.<ToyotaEntity>of(ToyotaEntity::new, MobCategory.MISC)
                    .sized(2.8F, 2.1F)
                    .clientTrackingRange(360)
                    .build("toyota"));

    public static final RegistryObject<EntityType<ToyotaZU23Entity>> TOYOTA_ZU23 =
            ENTITY_TYPES.register("toyota_hilux_zu23", () -> EntityType.Builder.<ToyotaZU23Entity>of(ToyotaZU23Entity::new, MobCategory.MISC)
                    .sized(2.8F, 2.1F)
                    .clientTrackingRange(360)
                    .build("toyota_hilux_zu23"));

    public static final RegistryObject<EntityType<UAZEntity>> UAZ =
            ENTITY_TYPES.register("uaz", () -> EntityType.Builder.<UAZEntity>of(UAZEntity::new, MobCategory.MISC)
                    .sized(2.8F, 2.1F)
                    .clientTrackingRange(360)
                    .build("uaz"));

    public static final RegistryObject<EntityType<UazDshkaEntity>> UAZ_DSHKA =
            ENTITY_TYPES.register("uaz_dshka", () -> EntityType.Builder.<UazDshkaEntity>of(UazDshkaEntity::new, MobCategory.MISC)
                    .sized(2.8F, 2.1F)
                    .clientTrackingRange(360)
                    .build("uaz_dshka"));

    public static final RegistryObject<EntityType<m1a2Entity>> M1A2 =
            ENTITY_TYPES.register("m1a2", () -> EntityType.Builder.<m1a2Entity>of(m1a2Entity::new, MobCategory.MISC)
                    .sized(2.8F, 2.1F)
                    .clientTrackingRange(360)
                    .build("m1a2"));

    public static final RegistryObject<EntityType<leopardEntity>> LEOPARD_2A7V =
            ENTITY_TYPES.register("leopard_2a7v", () -> EntityType.Builder.<leopardEntity>of(leopardEntity::new, MobCategory.MISC)
                    .sized(2.8F, 2.1F)
                    .clientTrackingRange(360)
                    .build("leopard_2a7v"));

    public static final RegistryObject<EntityType<sprutEntity>> SPRUT =
            ENTITY_TYPES.register("sprut", () -> EntityType.Builder.<sprutEntity>of(sprutEntity::new, MobCategory.MISC)
                    .sized(2.8F, 2.1F)
                    .clientTrackingRange(360)
                    .build("sprut"));

    public static final RegistryObject<EntityType<UralGradEntity>> GRAD =
            ENTITY_TYPES.register("grad", () -> EntityType.Builder.<UralGradEntity>of(UralGradEntity::new, MobCategory.MISC)
                    .sized(2.8F, 2.1F)
                    .clientTrackingRange(360)
                    .build("grad"));

    public static final RegistryObject<EntityType<MotorboatEntity>> MOTOR_BOAT =
            ENTITY_TYPES.register("motorboat", () -> EntityType.Builder.<MotorboatEntity>of(MotorboatEntity::new, MobCategory.MISC)
                    .sized(3F, 3F)
                    .clientTrackingRange(360)
                    .build("motorboat"));

    public static final RegistryObject<EntityType<MotorcycleEntity>> MOTOU =
            ENTITY_TYPES.register("motuo", () -> EntityType.Builder.<MotorcycleEntity>of(MotorcycleEntity::new, MobCategory.MISC)
                    .sized(1F, 1F)
                    .clientTrackingRange(360)
                    .build("motuo"));

    public static final RegistryObject<EntityType<UralEntity>> URAL =
            ENTITY_TYPES.register("ural_green", () -> EntityType.Builder.<UralEntity>of(UralEntity::new, MobCategory.MISC)
                    .sized(2.8F, 2.8F)
                    .clientTrackingRange(360)
                    .build("ural_green"));

    public static final RegistryObject<EntityType<FMTVEntity>> FMTV =
            ENTITY_TYPES.register("fmtv", () -> EntityType.Builder.<FMTVEntity>of(FMTVEntity::new, MobCategory.MISC)
                    .sized(2.8F, 2.8F)
                    .clientTrackingRange(360)
                    .build("fmtv"));

    public static final RegistryObject<EntityType<FMTVEntityGreen>> FMTV_GREEN =
            ENTITY_TYPES.register("fmtv_green", () -> EntityType.Builder.<FMTVEntityGreen>of(FMTVEntityGreen::new, MobCategory.MISC)
                    .sized(2.8F, 2.8F)
                    .clientTrackingRange(360)
                    .build("fmtv_green"));

    public static final RegistryObject<EntityType<StrykerMortalEntity>> STRYKER_MORTAR =
            ENTITY_TYPES.register("stryker_mortar", () -> EntityType.Builder.<StrykerMortalEntity>of(StrykerMortalEntity::new, MobCategory.MISC)
                    .sized(2.8F, 2.8F)
                    .clientTrackingRange(360)
                    .build("stryker_mortar"));

    public static final RegistryObject<EntityType<StrykerM2Entity>> STRYKER_M2 =
            ENTITY_TYPES.register("stryker_m2", () -> EntityType.Builder.<StrykerM2Entity>of(StrykerM2Entity::new, MobCategory.MISC)
                    .sized(2.8F, 2.8F)
                    .clientTrackingRange(360)
                    .build("stryker_m2"));

    public static final RegistryObject<EntityType<m1a2Entity_sand>> M1A2_SAND =
            ENTITY_TYPES.register("m1a2_sand", () -> EntityType.Builder.<m1a2Entity_sand>of(m1a2Entity_sand::new, MobCategory.MISC)
                    .sized(2.8F, 2.8F)
                    .clientTrackingRange(360)
                    .build("m1a2_sand"));

    public static final RegistryObject<EntityType<bradley_sandEntity>> BRADLEY_SAND =
            ENTITY_TYPES.register("bradley_sand", () -> EntityType.Builder.<bradley_sandEntity>of(bradley_sandEntity::new, MobCategory.MISC)
                    .sized(2.8F, 2.8F)
                    .clientTrackingRange(360)
                    .build("bradley_sand"));

    public static final RegistryObject<EntityType<PumaEntity>> PUMA =
            ENTITY_TYPES.register("puma", () -> EntityType.Builder.<PumaEntity>of(PumaEntity::new, MobCategory.MISC)
                    .sized(2.8F, 2.8F)
                    .clientTrackingRange(360)
                    .build("puma"));

    public static final RegistryObject<EntityType<mtlbEntity>> MTLB =
            ENTITY_TYPES.register("mtlb", () -> EntityType.Builder.<mtlbEntity>of(mtlbEntity::new, MobCategory.MISC)
                    .sized(2.8F, 2.8F)
                    .clientTrackingRange(360)
                    .build("mtlb"));

    public static final RegistryObject<EntityType<brdmEntity>> BRDM2 =
            ENTITY_TYPES.register("brdm2", () -> EntityType.Builder.<brdmEntity>of(brdmEntity::new, MobCategory.MISC)
                    .sized(2.8F, 2.8F)
                    .clientTrackingRange(360)
                    .build("brdm2"));

    public static final RegistryObject<EntityType<M2Entity>> M2 =
            ENTITY_TYPES.register("m2empl", () -> EntityType.Builder.<M2Entity>of(M2Entity::new, MobCategory.MISC)
                    .sized(0.8F, 0.8F)
                    .clientTrackingRange(360)
                    .build("m2empl"));

    public static final RegistryObject<EntityType<AGSEntity>> AGS17 =
            ENTITY_TYPES.register("emplags17", () -> EntityType.Builder.<AGSEntity>of(AGSEntity::new, MobCategory.MISC)
                    .sized(0.8F, 0.8F)
                    .clientTrackingRange(360)
                    .build("emplags17"));

    public static final RegistryObject<EntityType<ZISEntity>> ZIS3 =
            ENTITY_TYPES.register("emplzis3", () -> EntityType.Builder.<ZISEntity>of(ZISEntity::new, MobCategory.MISC)
                    .sized(2.1F, 1.5F)
                    .clientTrackingRange(360)
                    .build("emplzis3"));

    public static final RegistryObject<EntityType<TowEntity>> TOW =
            ENTITY_TYPES.register("empltow", () -> EntityType.Builder.<TowEntity>of(TowEntity::new, MobCategory.MISC)
                    .sized(0.8F, 1.8F)
                    .clientTrackingRange(360)
                    .build("empltow"));

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }


}
