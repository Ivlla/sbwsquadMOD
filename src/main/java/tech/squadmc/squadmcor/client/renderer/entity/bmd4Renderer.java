package tech.squadmc.squadmcor.client.renderer.entity;

import tech.squadmc.squadmcor.client.performance.OptimizedVehicleRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import tech.squadmc.squadmcor.client.model.bmd4Model;
import tech.squadmc.squadmcor.entity.bmd4Entity;

public class bmd4Renderer extends OptimizedVehicleRenderer<bmd4Entity> {
    private static final ResourceLocation CACHED_RESOURCE_0 = new ResourceLocation("squadmc", "textures/entity/bmd4.png");

    public bmd4Renderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new bmd4Model());
    }

    @Override
    public RenderType getRenderType(bmd4Entity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityCutout(this.getTextureLocation(animatable));
    }

    @Override
    public ResourceLocation getTextureLocation(bmd4Entity entity) {
        return CACHED_RESOURCE_0;
    }
}
