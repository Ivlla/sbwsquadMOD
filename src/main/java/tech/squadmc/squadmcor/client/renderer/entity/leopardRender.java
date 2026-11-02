package tech.squadmc.squadmcor.client.renderer.entity;

import tech.squadmc.squadmcor.client.performance.OptimizedVehicleRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import tech.squadmc.squadmcor.client.model.leopardModel;
import tech.squadmc.squadmcor.entity.leopardEntity;

public class leopardRender extends OptimizedVehicleRenderer<leopardEntity> {
    private static final ResourceLocation CACHED_RESOURCE_0 = new ResourceLocation("squadmc", "textures/entity/leopard_2a7v_camo.png");

    public leopardRender(EntityRendererProvider.Context renderManager) {
        super(renderManager, new leopardModel());
    }

    @Override
    public RenderType getRenderType(leopardEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityCutout(this.getTextureLocation(animatable));
    }

    @Override
    public ResourceLocation getTextureLocation(leopardEntity entity) {
        return CACHED_RESOURCE_0;
    }
}
