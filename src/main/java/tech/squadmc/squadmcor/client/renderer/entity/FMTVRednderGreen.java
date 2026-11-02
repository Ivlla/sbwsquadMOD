package tech.squadmc.squadmcor.client.renderer.entity;

import tech.squadmc.squadmcor.client.performance.OptimizedVehicleRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import tech.squadmc.squadmcor.client.model.FMTVModelGreen;
import tech.squadmc.squadmcor.entity.FMTVEntityGreen;

public class FMTVRednderGreen extends OptimizedVehicleRenderer<FMTVEntityGreen> {
    private static final ResourceLocation CACHED_RESOURCE_0 = new ResourceLocation("squadmc", "textures/entity/fmtv_green.png");


    public FMTVRednderGreen(EntityRendererProvider.Context renderManager) {
        super(renderManager, new FMTVModelGreen());
    }

    @Override
    public RenderType getRenderType(FMTVEntityGreen animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityCutoutNoCull(this.getTextureLocation(animatable));
    }

    @Override
    public ResourceLocation getTextureLocation(FMTVEntityGreen entity) {
        return CACHED_RESOURCE_0;
    }
}
