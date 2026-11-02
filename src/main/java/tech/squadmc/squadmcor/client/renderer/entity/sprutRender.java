package tech.squadmc.squadmcor.client.renderer.entity;

import tech.squadmc.squadmcor.client.performance.OptimizedVehicleRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import tech.squadmc.squadmcor.client.model.sprutModel;
import tech.squadmc.squadmcor.entity.sprutEntity;

public class sprutRender extends OptimizedVehicleRenderer<sprutEntity> {
    private static final ResourceLocation CACHED_RESOURCE_0 = new ResourceLocation("squadmc", "textures/entity/sprut.png");

    public sprutRender(EntityRendererProvider.Context renderManager) {
        super(renderManager, new sprutModel());
    }

    @Override
    public RenderType getRenderType(sprutEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityCutout(this.getTextureLocation(animatable));
    }

    @Override
    public ResourceLocation getTextureLocation(sprutEntity entity) {
        return CACHED_RESOURCE_0;
    }
}
