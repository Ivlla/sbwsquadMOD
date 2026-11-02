package tech.squadmc.squadmcor.client.renderer.entity;

import tech.squadmc.squadmcor.client.performance.OptimizedVehicleRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import tech.squadmc.squadmcor.client.model.FenekModel;
import tech.squadmc.squadmcor.entity.FenekEntity;
import tech.squadmc.squadmcor.squadmc;

public class FenekRender extends OptimizedVehicleRenderer<FenekEntity> {
    private static final ResourceLocation CACHED_RESOURCE_0 = new ResourceLocation(squadmc.MODID, "textures/entity/fennek.png");

    public FenekRender(EntityRendererProvider.Context renderManager) {
        super(renderManager, new FenekModel());
    }

    @Override
    public RenderType getRenderType(FenekEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityCutout(this.getTextureLocation(animatable));
    }

    @Override
    public ResourceLocation getTextureLocation(FenekEntity entity) {
        return CACHED_RESOURCE_0;
    }
}
