package tech.squadmc.squadmcor.client.renderer.entity;

import tech.squadmc.squadmcor.client.performance.OptimizedVehicleRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import tech.squadmc.squadmcor.client.model.PumaModel;
import tech.squadmc.squadmcor.entity.PumaEntity;

public class PumaRenderer extends OptimizedVehicleRenderer<PumaEntity> {
    private static final ResourceLocation CACHED_RESOURCE_0 = new ResourceLocation("squadmc", "textures/entity/puma.png");

    public PumaRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new PumaModel());
    }

    @Override
    public RenderType getRenderType(PumaEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityCutout(this.getTextureLocation(animatable));
    }

    @Override
    public ResourceLocation getTextureLocation(PumaEntity entity) {
        return CACHED_RESOURCE_0;
    }
}
