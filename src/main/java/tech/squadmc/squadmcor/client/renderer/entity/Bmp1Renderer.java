package tech.squadmc.squadmcor.client.renderer.entity;

import tech.squadmc.squadmcor.client.performance.OptimizedVehicleRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import tech.squadmc.squadmcor.client.model.Bmp1Model;
import tech.squadmc.squadmcor.entity.Bmp1Entity;

public class Bmp1Renderer extends OptimizedVehicleRenderer<Bmp1Entity> {
    private static final ResourceLocation CACHED_RESOURCE_0 = new ResourceLocation("squadmc", "textures/entity/bmp1.png");

    public Bmp1Renderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new Bmp1Model());
    }

    @Override
    public RenderType getRenderType(Bmp1Entity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityCutout(this.getTextureLocation(animatable));
    }

    @Override
    public ResourceLocation getTextureLocation(Bmp1Entity entity) {
        return CACHED_RESOURCE_0;
    }
}
