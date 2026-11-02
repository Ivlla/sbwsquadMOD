package tech.squadmc.squadmcor.client.renderer.entity;

import tech.squadmc.squadmcor.client.performance.OptimizedVehicleRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import tech.squadmc.squadmcor.client.model.UazDshkaModel;
import tech.squadmc.squadmcor.entity.UazDshkaEntity;

public class UazDshkaRender extends OptimizedVehicleRenderer<UazDshkaEntity> {
    private static final ResourceLocation CACHED_RESOURCE_0 = new ResourceLocation("squadmc", "textures/entity/uaz/uaz_dshka.png");

    public UazDshkaRender(EntityRendererProvider.Context renderManager) {
        super(renderManager, new UazDshkaModel());
    }

    @Override
    public RenderType getRenderType(UazDshkaEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityCutout(this.getTextureLocation(animatable));
    }

    @Override
    public ResourceLocation getTextureLocation(UazDshkaEntity entity) {
        return CACHED_RESOURCE_0;
    }
}
