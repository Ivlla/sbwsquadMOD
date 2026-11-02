package tech.squadmc.squadmcor.client.renderer.entity;

import tech.squadmc.squadmcor.client.performance.OptimizedVehicleRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import tech.squadmc.squadmcor.client.model.HumveeModel;
import tech.squadmc.squadmcor.client.model.HumveeTowModel;
import tech.squadmc.squadmcor.entity.HumveeEntity;
import tech.squadmc.squadmcor.entity.HumveeTowEntity;


public class HumveeRender extends OptimizedVehicleRenderer<HumveeEntity> {
    private static final ResourceLocation CACHED_RESOURCE_0 = new ResourceLocation("squadmc", "textures/entity/humvees/humvee_m2_green.png");

    public HumveeRender(EntityRendererProvider.Context renderManager) {
        super(renderManager, new HumveeModel());
    }

    @Override
    public RenderType getRenderType(HumveeEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityCutout(this.getTextureLocation(animatable));
    }

    @Override
    public ResourceLocation getTextureLocation(HumveeEntity entity) {
        return CACHED_RESOURCE_0;
    }
}

