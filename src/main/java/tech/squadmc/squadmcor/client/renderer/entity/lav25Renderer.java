package tech.squadmc.squadmcor.client.renderer.entity;

import tech.squadmc.squadmcor.client.performance.OptimizedVehicleRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import tech.squadmc.squadmcor.client.model.lav25Model;
import tech.squadmc.squadmcor.entity.lav25Entity;

public class lav25Renderer extends OptimizedVehicleRenderer<lav25Entity> {
    private static final ResourceLocation CACHED_RESOURCE_0 = new ResourceLocation("squadmc", "textures/entity/lav25.png");

    public lav25Renderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new lav25Model());
    }

    @Override
    public RenderType getRenderType(lav25Entity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityCutout(this.getTextureLocation(animatable));
    }

    @Override
    public ResourceLocation getTextureLocation(lav25Entity entity) {
        return CACHED_RESOURCE_0;
    }
}
