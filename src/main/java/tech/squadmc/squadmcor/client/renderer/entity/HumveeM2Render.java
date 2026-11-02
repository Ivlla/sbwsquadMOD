package tech.squadmc.squadmcor.client.renderer.entity;

import tech.squadmc.squadmcor.client.performance.OptimizedVehicleRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import tech.squadmc.squadmcor.client.model.HumveeM2Model;
import tech.squadmc.squadmcor.entity.HumveeM2Entity;
import tech.squadmc.squadmcor.entity.HumveeTowEntity;


public class HumveeM2Render extends OptimizedVehicleRenderer<HumveeM2Entity> {
    private static final ResourceLocation CACHED_RESOURCE_0 = new ResourceLocation("squadmc", "textures/entity/humvees/humvee_m2_green.png");

    public HumveeM2Render(EntityRendererProvider.Context renderManager) {
        super(renderManager, new HumveeM2Model());
    }

    @Override
    public RenderType getRenderType(HumveeM2Entity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityCutout(this.getTextureLocation(animatable));
    }

    @Override
    public ResourceLocation getTextureLocation(HumveeM2Entity entity) {
        return CACHED_RESOURCE_0;
    }
}

