package tech.squadmc.squadmcor.client.renderer.entity;

import tech.squadmc.squadmcor.client.performance.OptimizedVehicleRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import tech.squadmc.squadmcor.client.model.T72AVModel;
import tech.squadmc.squadmcor.entity.T72AVEntity;


public class T72AVRenderer extends OptimizedVehicleRenderer<T72AVEntity> {
    private static final ResourceLocation CACHED_RESOURCE_0 = new ResourceLocation("squadmc", "textures/entity/t72av.png");

    public T72AVRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new T72AVModel());
    }

    @Override
    public RenderType getRenderType(T72AVEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityCutout(this.getTextureLocation(animatable));
    }

    @Override
    public ResourceLocation getTextureLocation(T72AVEntity entity) {
        return CACHED_RESOURCE_0;
    }
}
