package tech.squadmc.squadmcor.client.renderer.entity;

import tech.squadmc.squadmcor.client.performance.OptimizedVehicleRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import tech.squadmc.squadmcor.client.model.UAZModel;
import tech.squadmc.squadmcor.entity.UAZEntity;

public class UAZRender extends OptimizedVehicleRenderer<UAZEntity> {
    private static final ResourceLocation CACHED_RESOURCE_0 = new ResourceLocation("squadmc", "textures/entity/uaz/uaz_ruined.png");

    public UAZRender(EntityRendererProvider.Context renderManager) {
        super(renderManager, new UAZModel());
    }

    @Override
    public RenderType getRenderType(UAZEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityCutout(this.getTextureLocation(animatable));
    }

    @Override
    public ResourceLocation getTextureLocation(UAZEntity entity) {
        return CACHED_RESOURCE_0;
    }
}
