package tech.squadmc.squadmcor.client.renderer.entity;

import tech.squadmc.squadmcor.client.performance.OptimizedVehicleRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import tech.squadmc.squadmcor.client.model.t80Model;
import tech.squadmc.squadmcor.entity.t80Entity;


public class t80Render extends OptimizedVehicleRenderer<t80Entity> {
    private static final ResourceLocation CACHED_RESOURCE_0 = new ResourceLocation("squadmc", "textures/entity/t80.png");

    public t80Render(EntityRendererProvider.Context renderManager) {
        super(renderManager, new t80Model());
    }

    @Override
    public RenderType getRenderType(t80Entity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityCutout(this.getTextureLocation(animatable));
    }

    @Override
    public ResourceLocation getTextureLocation(t80Entity entity) {
        return CACHED_RESOURCE_0;
    }
}
