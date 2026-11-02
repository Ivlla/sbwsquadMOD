package tech.squadmc.squadmcor.client.smoke;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderType;

/**
 * Перенесено из aasgranate (client/SmokeRenderTypes). Состояния blend/depth
 * идентичны; переименованы только внутренние имена типов ("squadmc_*") и
 * домен ресурса текстуры — при одновременно установленных двух модах
 * конфликтов быть не может.
 */
public class SmokeRenderTypes extends RenderType {

    private SmokeRenderTypes(String name, VertexFormat format, VertexFormat.Mode mode, int bufferSize,
                             boolean affectsCrumbling, boolean sortOnUpload,
                             Runnable setupState, Runnable clearState) {
        super(name, format, mode, bufferSize, affectsCrumbling, sortOnUpload, setupState, clearState);
    }

    /** Дым без шейдерпака: свой шейдер smoke_cloud. */
    public static final RenderType SMOKE = RenderType.create(
            "squadmc_smoke",
            DefaultVertexFormat.POSITION_COLOR_TEX,
            VertexFormat.Mode.QUADS,
            512 * 1024,
            false,
            true,
            CompositeState.builder()
                    .setShaderState(new ShaderStateShard(SmokeShaders::cloud))
                    .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                    .setCullState(NO_CULL)
                    .setWriteMaskState(COLOR_WRITE)
                    .setDepthTestState(LEQUAL_DEPTH_TEST)
                    .setLightmapState(NO_LIGHTMAP)
                    .createCompositeState(false));

    /** Дым с шейдерпаком: ванильный шейдер частиц + мягкая текстура, пак обрабатывает как частицы. */
    public static final RenderType SMOKE_SHADERS = RenderType.create(
            "squadmc_smoke_shaders",
            DefaultVertexFormat.PARTICLE,
            VertexFormat.Mode.QUADS,
            512 * 1024,
            false,
            true,
            CompositeState.builder()
                    .setShaderState(new ShaderStateShard(GameRenderer::getParticleShader))
                    .setTextureState(new TextureStateShard(SmokePuffTexture.ID, true, false))
                    .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                    .setCullState(NO_CULL)
                    .setWriteMaskState(COLOR_WRITE)
                    .setDepthTestState(LEQUAL_DEPTH_TEST)
                    .setLightmapState(LIGHTMAP)
                    .createCompositeState(false));

    /** Искры: аддитивные штрихи (оставлен для симметрии с исходной системой). */
    public static final RenderType SPARKS = RenderType.create(
            "squadmc_sparks",
            DefaultVertexFormat.POSITION_COLOR,
            VertexFormat.Mode.QUADS,
            64 * 1024,
            false,
            false,
            CompositeState.builder()
                    .setShaderState(new ShaderStateShard(GameRenderer::getPositionColorShader))
                    .setTransparencyState(ADDITIVE_TRANSPARENCY)
                    .setCullState(NO_CULL)
                    .setWriteMaskState(COLOR_WRITE)
                    .setDepthTestState(LEQUAL_DEPTH_TEST)
                    .setLightmapState(NO_LIGHTMAP)
                    .createCompositeState(false));
}
