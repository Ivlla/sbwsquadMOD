package tech.squadmc.squadmcor.client.renderer.entity;

import tech.squadmc.squadmcor.client.performance.OptimizedVehicleRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import tech.squadmc.squadmcor.client.model.btr80Model;
import tech.squadmc.squadmcor.entity.btr80Entity;

public class btr80Renderer extends OptimizedVehicleRenderer<btr80Entity> {
    private static final ResourceLocation CACHED_RESOURCE_0 = new ResourceLocation("squadmc", "textures/entity/btr_82.png");

    public btr80Renderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new btr80Model());
    }

    @Override
    public RenderType getRenderType(btr80Entity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityCutout(this.getTextureLocation(animatable));
    }

    @Override
    public ResourceLocation getTextureLocation(btr80Entity entity) {
        return CACHED_RESOURCE_0;
    }
}
