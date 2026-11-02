package tech.squadmc.squadmcor.client.renderer.entity;

import tech.squadmc.squadmcor.client.performance.OptimizedVehicleRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import tech.squadmc.squadmcor.client.model.MotorcycleModel;
import tech.squadmc.squadmcor.entity.MotorcycleEntity;

public class MotorcycleRender extends OptimizedVehicleRenderer<MotorcycleEntity> {
    private static final ResourceLocation CACHED_RESOURCE_0 = new ResourceLocation("squadmc", "textures/entity/motuo.png");


    public MotorcycleRender(EntityRendererProvider.Context renderManager) {
        super(renderManager, new MotorcycleModel());
    }

    @Override
    public RenderType getRenderType(MotorcycleEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityCutout(this.getTextureLocation(animatable));
    }

    @Override
    public ResourceLocation getTextureLocation(MotorcycleEntity entity) {
        return CACHED_RESOURCE_0;
    }

}
