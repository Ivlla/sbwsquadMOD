package tech.squadmc.squadmcor.fx;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Обычный ванильный лист пишет глубину, и огромная прозрачная вспышка закрывала бы дым за собой.
 * Поэтому здесь запись глубины выключена, а isBlended рисуется с аддитивным смешиванием, как в MTS.
 */
@OnlyIn(Dist.CLIENT)
public final class MtsFxRenderTypes {
    private MtsFxRenderTypes() {}

    public static final ParticleRenderType TRANSLUCENT = new Sheet("SQUADMC_MTS_TRANSLUCENT", false);
    public static final ParticleRenderType ADDITIVE = new Sheet("SQUADMC_MTS_ADDITIVE", true);

    private record Sheet(String name, boolean additive) implements ParticleRenderType {
        @Override
        public void begin(BufferBuilder builder, TextureManager textures) {
            RenderSystem.depthMask(false);
            RenderSystem.disableCull();
            RenderSystem.setShader(GameRenderer::getParticleShader);
            RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_PARTICLES);
            RenderSystem.enableBlend();
            if (additive) {
                RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
            } else {
                RenderSystem.defaultBlendFunc();
            }
            builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
        }

        @Override
        public void end(Tesselator tesselator) {
            tesselator.end();
            RenderSystem.defaultBlendFunc();
            RenderSystem.enableCull();
            RenderSystem.depthMask(true);
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
