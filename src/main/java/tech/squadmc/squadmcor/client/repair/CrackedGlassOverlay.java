package tech.squadmc.squadmcor.client.repair;

import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import tech.squadmc.squadmcor.squadmc;

import java.io.IOException;

/** Разбитое стекло прицела у наводчика, пока башня выбита. */
public final class CrackedGlassOverlay {
    private static ShaderInstance shader;
    private static float intensity;

    private CrackedGlassOverlay() {}

    @Mod.EventBusSubscriber(modid = squadmc.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static final class Registration {
        private Registration() {}

        @SubscribeEvent
        public static void registerShaders(RegisterShadersEvent event) throws IOException {
            event.registerShader(new ShaderInstance(event.getResourceProvider(),
                    new ResourceLocation(squadmc.MODID, "cracked_glass"), DefaultVertexFormat.POSITION),
                    loaded -> shader = loaded);
        }
    }

    @Mod.EventBusSubscriber(modid = squadmc.MODID, value = Dist.CLIENT)
    public static final class Drawing {
        private Drawing() {}

        @SubscribeEvent
        public static void onRenderGui(RenderGuiEvent.Pre event) {
            Minecraft mc = Minecraft.getInstance();
            Player player = mc.player;
            VehicleEntity vehicle = player != null && player.getVehicle() instanceof VehicleEntity v ? v : null;
            boolean broken = vehicle != null && vehicle.hasTurret() && vehicle.getTurretDamaged()
                    && vehicle.getSeatIndex(player) == vehicle.getTurretControllerIndex()
                    && mc.options.getCameraType().isFirstPerson();
            intensity += ((broken ? 1F : 0F) - intensity) * 0.15F;
            if (intensity < 0.01F || shader == null || vehicle == null) return;
            draw(mc, vehicle.getId() * 0.137F, intensity);
        }
    }

    private static void draw(Minecraft mc, float seed, float amount) {
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(() -> shader);
        if (shader.getUniform("Seed") != null) shader.getUniform("Seed").set(seed);
        if (shader.getUniform("Amount") != null) shader.getUniform("Amount").set(amount);
        if (shader.getUniform("Aspect") != null) {
            shader.getUniform("Aspect").set((float) mc.getWindow().getWidth() / Math.max(1, mc.getWindow().getHeight()));
        }

        BufferBuilder buffer = Tesselator.getInstance().getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);
        buffer.vertex(-1.0, -1.0, 0.0).endVertex();
        buffer.vertex(1.0, -1.0, 0.0).endVertex();
        buffer.vertex(1.0, 1.0, 0.0).endVertex();
        buffer.vertex(-1.0, 1.0, 0.0).endVertex();
        BufferUploader.drawWithShader(buffer.end());

        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
    }
}
