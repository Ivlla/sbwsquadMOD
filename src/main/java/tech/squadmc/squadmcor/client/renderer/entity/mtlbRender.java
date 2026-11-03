package tech.squadmc.squadmcor.client.renderer.entity;

import tech.squadmc.squadmcor.client.performance.OptimizedVehicleRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import tech.squadmc.squadmcor.client.model.mtlbModel;
import tech.squadmc.squadmcor.entity.mtlbEntity;

public class mtlbRender extends OptimizedVehicleRenderer<mtlbEntity> {
    private static final ResourceLocation CACHED_RESOURCE_0 = new ResourceLocation("squadmc", "textures/entity/mtlb.png");

    public mtlbRender(EntityRendererProvider.Context renderManager) {
        super(renderManager, new mtlbModel());
    }

    @Override
    public RenderType getRenderType(mtlbEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityCutout(this.getTextureLocation(animatable));
    }


    @Override
    public ResourceLocation getTextureLocation(mtlbEntity entity) {
        return tech.squadmc.squadmcor.client.skin.VehicleSkinTexture.resolve(entity, CACHED_RESOURCE_0);
    }
}
