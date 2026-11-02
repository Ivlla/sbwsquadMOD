package tech.squadmc.squadmcor.client.renderer.entity;

import tech.squadmc.squadmcor.client.performance.OptimizedVehicleRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import tech.squadmc.squadmcor.client.model.bradleyModel;
import tech.squadmc.squadmcor.entity.bradleyEntity;

public class bradleyRenderer extends OptimizedVehicleRenderer<bradleyEntity> {
    private static final ResourceLocation CACHED_RESOURCE_0 = new ResourceLocation("squadmc", "textures/entity/bradley.png");

    public bradleyRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new bradleyModel());
    }

    @Override
    public RenderType getRenderType(bradleyEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityCutout(this.getTextureLocation(animatable));
    }

    @Override
    public ResourceLocation getTextureLocation(bradleyEntity entity) {
        return CACHED_RESOURCE_0;
    }
}
