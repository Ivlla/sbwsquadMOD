package tech.squadmc.squadmcor.client.renderer.entity;

import tech.squadmc.squadmcor.client.performance.OptimizedVehicleRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import tech.squadmc.squadmcor.client.model.m1a2Model;
import tech.squadmc.squadmcor.entity.m1a2Entity;

public class m1a2Render extends OptimizedVehicleRenderer<m1a2Entity> {
    private static final ResourceLocation CACHED_RESOURCE_0 = new ResourceLocation("squadmc", "textures/entity/m1a2.png");

    public m1a2Render(EntityRendererProvider.Context renderManager) {
        super(renderManager, new m1a2Model());
    }

    @Override
    public RenderType getRenderType(m1a2Entity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityCutout(this.getTextureLocation(animatable));
    }

    @Override
    public ResourceLocation getTextureLocation(m1a2Entity entity) {
        return CACHED_RESOURCE_0;
    }
}
