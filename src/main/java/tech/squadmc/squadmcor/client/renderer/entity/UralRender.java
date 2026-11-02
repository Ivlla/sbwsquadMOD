package tech.squadmc.squadmcor.client.renderer.entity;

import tech.squadmc.squadmcor.client.performance.OptimizedVehicleRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import tech.squadmc.squadmcor.client.model.UralModel;
import tech.squadmc.squadmcor.entity.UralEntity;

public class UralRender extends OptimizedVehicleRenderer<UralEntity> {
    private static final ResourceLocation CACHED_RESOURCE_0 = new ResourceLocation("squadmc", "textures/entity/ural_green.png");


    public UralRender(EntityRendererProvider.Context renderManager) {
        super(renderManager, new UralModel());
    }

    @Override
    public RenderType getRenderType(UralEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityCutout(this.getTextureLocation(animatable));
    }

    @Override
    public ResourceLocation getTextureLocation(UralEntity entity) {
        return CACHED_RESOURCE_0;
    }
}
