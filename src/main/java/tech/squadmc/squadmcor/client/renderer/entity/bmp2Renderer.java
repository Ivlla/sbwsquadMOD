package tech.squadmc.squadmcor.client.renderer.entity;

import tech.squadmc.squadmcor.client.performance.OptimizedVehicleRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import tech.squadmc.squadmcor.client.model.bmp2Model;
import tech.squadmc.squadmcor.entity.bmp2Entity;

public class bmp2Renderer extends OptimizedVehicleRenderer<bmp2Entity> {
    private static final ResourceLocation CACHED_RESOURCE_0 = new ResourceLocation("squadmc", "textures/entity/bmp2.png");

    public bmp2Renderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new bmp2Model());
    }

    @Override
    public RenderType getRenderType(bmp2Entity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityCutout(this.getTextureLocation(animatable));
    }

    @Override
    public ResourceLocation getTextureLocation(bmp2Entity entity) {
        return CACHED_RESOURCE_0;
    }
}

