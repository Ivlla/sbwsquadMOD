package tech.squadmc.squadmcor.client.smoke;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import java.io.IOException;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import tech.squadmc.squadmcor.squadmc;

/**
 * Перенесено из aasgranate (client/SmokeShaders). Ресурсы ищутся в домене
 * squadmc: (assets/squadmc/shaders/core/smoke_cloud.* и smoke_composite.*).
 */
@Mod.EventBusSubscriber(modid = squadmc.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class SmokeShaders {
    private static ShaderInstance cloud;
    private static ShaderInstance composite;

    private SmokeShaders() {}
    static ShaderInstance cloud() { return cloud; }
    static ShaderInstance composite() { return composite; }
    static boolean ready() { return cloud != null && composite != null; }

    @SubscribeEvent
    public static void setup(FMLClientSetupEvent event) {
        event.enqueueWork(SmokeTransparencyHook::install);
    }

    @SubscribeEvent
    public static void register(RegisterShadersEvent event) throws IOException {
        event.registerShader(new ShaderInstance(event.getResourceProvider(),
                new ResourceLocation(squadmc.MODID, "smoke_cloud"),
                DefaultVertexFormat.POSITION_COLOR_TEX), shader -> cloud = shader);
        event.registerShader(new ShaderInstance(event.getResourceProvider(),
                new ResourceLocation(squadmc.MODID, "smoke_composite"),
                DefaultVertexFormat.POSITION_TEX), shader -> composite = shader);
    }
}
