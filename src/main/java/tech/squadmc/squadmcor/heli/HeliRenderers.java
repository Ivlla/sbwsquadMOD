// Based on Frontline Combat Pack by Commissar_Mado (see FCP_LICENSE.txt). Modified for SquadMC.
package tech.squadmc.squadmcor.heli;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import tech.squadmc.squadmcor.squadmc;

import tech.squadmc.squadmcor.heli.client.renderer.Huey.HueyDoorGunnerM134Renderer;
import tech.squadmc.squadmcor.heli.client.renderer.Huey.HueyDoorGunnerM60Renderer;
import tech.squadmc.squadmcor.heli.client.renderer.Huey.HueyM134DoorGunsRenderer;
import tech.squadmc.squadmcor.heli.client.renderer.Huey.HueyM134GunshipRenderer;
import tech.squadmc.squadmcor.heli.client.renderer.Huey.HueyM60DoorGunsRenderer;
import tech.squadmc.squadmcor.heli.client.renderer.Huey.HueyM60GunshipRenderer;
import tech.squadmc.squadmcor.heli.client.renderer.Huey.HueyRenderer;
import tech.squadmc.squadmcor.heli.client.renderer.Huey.HueyRocketsRenderer;
import tech.squadmc.squadmcor.heli.client.renderer.Littlebird.LittlebirdArmedRenderer;
import tech.squadmc.squadmcor.heli.client.renderer.Littlebird.LittlebirdHeavyRenderer;
import tech.squadmc.squadmcor.heli.client.renderer.Littlebird.LittlebirdRenderer;
import tech.squadmc.squadmcor.heli.client.renderer.Projectile.Hellfire.LockOnHellfireRenderer;
import tech.squadmc.squadmcor.heli.client.renderer.Mi17.MI17DoorGunsRenderer;
import tech.squadmc.squadmcor.heli.client.renderer.Mi17.MI17Renderer;
import tech.squadmc.squadmcor.heli.client.renderer.Mi8.MI8AMTShRenderer;
import tech.squadmc.squadmcor.heli.client.renderer.Mi8.MI8DoorGunsRenderer;
import tech.squadmc.squadmcor.heli.client.renderer.Mi8.MI8MTVRenderer;
import tech.squadmc.squadmcor.heli.client.renderer.Mi8.MI8Renderer;
import tech.squadmc.squadmcor.heli.client.renderer.Ch53.Mh53Renderer;
import tech.squadmc.squadmcor.heli.client.renderer.Uh60.Mh60lRenderer;
import tech.squadmc.squadmcor.heli.client.renderer.Huey.VenomDoorGunsRenderer;
import tech.squadmc.squadmcor.heli.client.renderer.Huey.VenomGunshipRenderer;
import tech.squadmc.squadmcor.heli.client.renderer.Huey.VenomRenderer;
import tech.squadmc.squadmcor.heli.client.renderer.Projectile.Hellfire.WireGuidedHellfireRenderer;
import tech.squadmc.squadmcor.heli.client.renderer.Ch47.Ch47Renderer;
import tech.squadmc.squadmcor.heli.client.renderer.Kamaz.KamazRenderer;
import tech.squadmc.squadmcor.heli.client.renderer.ManHx60.ManHx60Renderer;
import tech.squadmc.squadmcor.heli.client.renderer.Uaz.UAZ452Renderer;
import tech.squadmc.squadmcor.heli.client.renderer.Rg33.RG33Renderer;
import tech.squadmc.squadmcor.heli.client.renderer.M939.M939Renderer;

@Mod.EventBusSubscriber(modid = squadmc.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class HeliRenderers {
    private HeliRenderers() {}

    @SubscribeEvent
    public static void register(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(HeliEntities.UAZ_452.get(), UAZ452Renderer::new);
        event.registerEntityRenderer(HeliEntities.KAMAZ.get(), KamazRenderer::new);
        event.registerEntityRenderer(HeliEntities.CH47.get(), Ch47Renderer::new);
        event.registerEntityRenderer(HeliEntities.RG_33.get(), RG33Renderer::new);
        event.registerEntityRenderer(HeliEntities.M939.get(), M939Renderer::new);
        event.registerEntityRenderer(HeliEntities.MAN_HX60.get(), ManHx60Renderer::new);
        event.registerEntityRenderer(HeliEntities.KAMAZ_NO_TENT.get(), KamazRenderer::new);
        event.registerEntityRenderer(HeliEntities.MAN_HX60_NO_TENT.get(), ManHx60Renderer::new);
        event.registerEntityRenderer(HeliEntities.LITTLEBIRD.get(), LittlebirdRenderer::new);
        event.registerEntityRenderer(HeliEntities.LITTLEBIRD_ARMED.get(), LittlebirdArmedRenderer::new);
        event.registerEntityRenderer(HeliEntities.LITTLEBIRD_HEAVY.get(), LittlebirdHeavyRenderer::new);
        event.registerEntityRenderer(HeliEntities.MH53.get(), Mh53Renderer::new);
        event.registerEntityRenderer(HeliEntities.MH60L.get(), Mh60lRenderer::new);
        event.registerEntityRenderer(HeliEntities.HUEY.get(), HueyRenderer::new);
        event.registerEntityRenderer(HeliEntities.HUEY_M60_DOOR_GUNS.get(), HueyM60DoorGunsRenderer::new);
        event.registerEntityRenderer(HeliEntities.HUEY_M60_GUNSHIP.get(), HueyM60GunshipRenderer::new);
        event.registerEntityRenderer(HeliEntities.HUEY_M134_DOOR_GUNS.get(), HueyM134DoorGunsRenderer::new);
        event.registerEntityRenderer(HeliEntities.HUEY_M134_GUNSHIP.get(), HueyM134GunshipRenderer::new);
        event.registerEntityRenderer(HeliEntities.VENOM.get(), VenomRenderer::new);
        event.registerEntityRenderer(HeliEntities.VENOM_GUNSHIP.get(), VenomGunshipRenderer::new);
        event.registerEntityRenderer(HeliEntities.VENOM_DOOR_GUNS.get(), VenomDoorGunsRenderer::new);
        event.registerEntityRenderer(HeliEntities.LOCK_ON_HELLFIRE.get(), LockOnHellfireRenderer::new);
        event.registerEntityRenderer(HeliEntities.WIRE_GUIDED_HELLFIRE.get(), WireGuidedHellfireRenderer::new);
        event.registerEntityRenderer(HeliEntities.MI17.get(), MI17Renderer::new);
        event.registerEntityRenderer(HeliEntities.MI8.get(), MI8Renderer::new);
        event.registerEntityRenderer(HeliEntities.MI17_DOOR_GUNS.get(), MI17DoorGunsRenderer::new);
        event.registerEntityRenderer(HeliEntities.MI8_DOOR_GUNS.get(), MI8DoorGunsRenderer::new);
        event.registerEntityRenderer(HeliEntities.MI8_MTV.get(), MI8MTVRenderer::new);
        event.registerEntityRenderer(HeliEntities.MI8_AMTSH.get(), MI8AMTShRenderer::new);
        event.registerEntityRenderer(HeliEntities.HUEY_ROCKETS.get(), HueyRocketsRenderer::new);
        event.registerEntityRenderer(HeliEntities.HUEY_DOOR_GUNNER_M60.get(), HueyDoorGunnerM60Renderer::new);
        event.registerEntityRenderer(HeliEntities.HUEY_DOOR_GUNNER_M134.get(), HueyDoorGunnerM134Renderer::new);
    }
}
