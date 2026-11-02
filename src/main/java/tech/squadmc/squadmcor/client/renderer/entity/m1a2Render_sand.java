package tech.squadmc.squadmcor.client.renderer.entity;

import tech.squadmc.squadmcor.client.performance.OptimizedVehicleRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import tech.squadmc.squadmcor.client.model.m1a2Model_sand;
import tech.squadmc.squadmcor.entity.m1a2Entity_sand;

public class m1a2Render_sand extends OptimizedVehicleRenderer<m1a2Entity_sand> {
    private static final ResourceLocation CACHED_RESOURCE_0 = new ResourceLocation("squadmc", "textures/entity/m1a2_sand.png");

    public m1a2Render_sand(EntityRendererProvider.Context renderManager) {
        super(renderManager, new m1a2Model_sand());
    }

    @Override
    public RenderType getRenderType(m1a2Entity_sand animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityCutout(this.getTextureLocation(animatable));
    }

    @Override
    public ResourceLocation getTextureLocation(m1a2Entity_sand entity) {
        return CACHED_RESOURCE_0;
    }
}
