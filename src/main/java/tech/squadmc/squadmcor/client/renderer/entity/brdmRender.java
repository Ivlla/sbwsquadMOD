package tech.squadmc.squadmcor.client.renderer.entity;

import tech.squadmc.squadmcor.client.performance.OptimizedVehicleRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import tech.squadmc.squadmcor.client.model.brdmModel;
import tech.squadmc.squadmcor.entity.brdmEntity;

public class brdmRender extends OptimizedVehicleRenderer<brdmEntity> {
    private static final ResourceLocation CACHED_RESOURCE_0 = new ResourceLocation("squadmc", "textures/entity/brdm2.png");

    public brdmRender(EntityRendererProvider.Context renderManager) {
        super(renderManager, new brdmModel());
    }

    @Override
    public RenderType getRenderType(brdmEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityCutout(this.getTextureLocation(animatable));
    }

    @Override
    public ResourceLocation getTextureLocation(brdmEntity entity) {
        return CACHED_RESOURCE_0;
    }
}
