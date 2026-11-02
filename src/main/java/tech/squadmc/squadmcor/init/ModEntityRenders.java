package tech.squadmc.squadmcor.init;

import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import tech.squadmc.squadmcor.client.renderer.entity.*;
import tech.squadmc.squadmcor.client.renderer.entity.empl.*;
import tech.squadmc.squadmcor.squadmc;

@EventBusSubscriber(modid = squadmc.MODID, bus = Bus.MOD, value = {Dist.CLIENT})
public class ModEntityRenders {

    // 1. Р РµРіРёСЃС‚СЂР°С†РёСЏ СЂРµРЅРґРµСЂРѕРІ СЃСѓС‰РЅРѕСЃС‚РµР№
    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.BTR82.get(), btr82Renderer::new);
        event.registerEntityRenderer(ModEntities.BTR80.get(), btr80Renderer::new);
        event.registerEntityRenderer(ModEntities.TDA_DUMMY.get(), NoopRenderer::new);
        event.registerEntityRenderer(ModEntities.BMP2.get(), bmp2Renderer::new);
        event.registerEntityRenderer(ModEntities.KONKURS_DUMMY.get(), NoopRenderer::new);

        // Облако объёмного дыма рисуется stage-проходом (VehicleSmokeStageRenderer) —
        // штатный рендерер сущности не нужен, ровно как у TDA/KONKURS/TOW.
        event.registerEntityRenderer(ModEntities.VEHICLE_SMOKE_CLOUD.get(), NoopRenderer::new);
        event.registerEntityRenderer(ModEntities.LAV25.get(), lav25Renderer::new);
        event.registerEntityRenderer(ModEntities.BRADLEY.get(), bradleyRenderer::new);
        event.registerEntityRenderer(ModEntities.TOW_DUMMY.get(), NoopRenderer::new);
        event.registerEntityRenderer(ModEntities.BMD4.get(), bmd4Renderer::new);
        event.registerEntityRenderer(ModEntities.T72AV.get(), T72AVRenderer::new);
        event.registerEntityRenderer(ModEntities.BMP1.get(), Bmp1Renderer::new);
        event.registerEntityRenderer(ModEntities.MALYUTKA.get(), MalyutkaRenderer::new);
        event.registerEntityRenderer(ModEntities.T80.get(), t80Render::new);
        event.registerEntityRenderer(ModEntities.HUMVEE.get(), HumveeRender::new);
        event.registerEntityRenderer(ModEntities.HUMVEE_TOW.get(), HumveeTowRender::new);
        event.registerEntityRenderer(ModEntities.HUMVEE_M2.get(), HumveeM2Render::new);
        event.registerEntityRenderer(ModEntities.GAZ_TIGR.get(), Gaz_tigrRender::new);
        event.registerEntityRenderer(ModEntities.GAZ_TIGR_MG.get(), Gaz_tigrMGRender::new);
        event.registerEntityRenderer(ModEntities.GAZ_TIGR_RWS.get(), Gaz_tigrRWSRender::new);
        event.registerEntityRenderer(ModEntities.TOYOTA.get(), ToyotaRender::new);
        event.registerEntityRenderer(ModEntities.UAZ.get(), UAZRender::new);
        event.registerEntityRenderer(ModEntities.UAZ_DSHKA.get(), UazDshkaRender::new);
        event.registerEntityRenderer(ModEntities.M1A2.get(), m1a2Render::new);
        event.registerEntityRenderer(ModEntities.SPRUT.get(), sprutRender::new);
        event.registerEntityRenderer(ModEntities.GRAD.get(), UralGradRender::new);
        event.registerEntityRenderer(ModEntities.MOTOR_BOAT.get(), MotorboatRender::new);
        event.registerEntityRenderer(ModEntities.MOTOU.get(), MotorcycleRender::new);
        event.registerEntityRenderer(ModEntities.URAL.get(), UralRender::new);
        event.registerEntityRenderer(ModEntities.FMTV.get(), FMTVRender::new);
        event.registerEntityRenderer(ModEntities.STRYKER_MORTAR.get(), StrykerMortarRender::new);
        event.registerEntityRenderer(ModEntities.STRYKER_M2.get(), StrykerM2Render::new);
        event.registerEntityRenderer(ModEntities.M1A2_SAND.get(), m1a2Render_sand::new);
        event.registerEntityRenderer(ModEntities.BRADLEY_SAND.get(), bradley_sandRenderer::new);
        event.registerEntityRenderer(ModEntities.MTLB.get(), mtlbRender::new);
        event.registerEntityRenderer(ModEntities.BRDM2.get(), brdmRender::new);
        event.registerEntityRenderer(ModEntities.TOYOTA_ZU23.get(), ToyotaZU23Render::new);
        event.registerEntityRenderer(ModEntities.M2.get(), m2Render::new);
        event.registerEntityRenderer(ModEntities.AGS17.get(), AGSRender::new);
        event.registerEntityRenderer(ModEntities.ZIS3.get(), ZISRender::new);
        event.registerEntityRenderer(ModEntities.TOW.get(), TowRender::new);
        event.registerEntityRenderer(ModEntities.FMTV_GREEN.get(), FMTVRednderGreen::new);
        event.registerEntityRenderer(ModEntities.LEOPARD_2A7V.get(), leopardRender::new);
        event.registerEntityRenderer(ModEntities.PUMA.get(), PumaRenderer::new);
        event.registerEntityRenderer(ModEntities.T64.get(), T64Renderer::new);
        event.registerEntityRenderer(ModEntities.FENEK.get(), FenekRender::new);
    }

}
