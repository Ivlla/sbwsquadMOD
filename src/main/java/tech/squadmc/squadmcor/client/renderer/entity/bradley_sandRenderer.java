package tech.squadmc.squadmcor.client.renderer.entity;

import tech.squadmc.squadmcor.client.performance.OptimizedVehicleRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import tech.squadmc.squadmcor.client.model.bradleyModel_sand;
import tech.squadmc.squadmcor.entity.bradley_sandEntity;

public class bradley_sandRenderer extends OptimizedVehicleRenderer<bradley_sandEntity> {
    private static final ResourceLocation CACHED_RESOURCE_0 = new ResourceLocation("squadmc", "textures/entity/m3a3_texture_sand.png");

    public bradley_sandRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new bradleyModel_sand());
    }

    @Override
    public RenderType getRenderType(bradley_sandEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityCutout(this.getTextureLocation(animatable));
    }

    @Override
    public ResourceLocation getTextureLocation(bradley_sandEntity entity) {
        return CACHED_RESOURCE_0;
    }
}
