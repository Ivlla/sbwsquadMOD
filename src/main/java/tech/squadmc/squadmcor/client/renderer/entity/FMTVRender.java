package tech.squadmc.squadmcor.client.renderer.entity;

import tech.squadmc.squadmcor.client.performance.OptimizedVehicleRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import tech.squadmc.squadmcor.client.model.FMTVModel;
import tech.squadmc.squadmcor.entity.FMTVEntity;

public class FMTVRender extends OptimizedVehicleRenderer<FMTVEntity> {
    private static final ResourceLocation CACHED_RESOURCE_0 = new ResourceLocation("squadmc", "textures/entity/fmtv_iraq.png");


    public FMTVRender(EntityRendererProvider.Context renderManager) {
        super(renderManager, new FMTVModel());
    }

    @Override
    public RenderType getRenderType(FMTVEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityCutoutNoCull(this.getTextureLocation(animatable));
    }

    @Override
    public ResourceLocation getTextureLocation(FMTVEntity entity) {
        return CACHED_RESOURCE_0;
    }
}
