package tech.squadmc.squadmcor.client.renderer.entity;

import tech.squadmc.squadmcor.client.performance.OptimizedVehicleRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import tech.squadmc.squadmcor.client.model.HumveeTowModel;
import tech.squadmc.squadmcor.entity.HumveeTowEntity;

public class HumveeTowRender extends OptimizedVehicleRenderer<HumveeTowEntity> {
    private static final ResourceLocation CACHED_RESOURCE_0 = new ResourceLocation("squadmc", "textures/entity/humvees/humvee_tow_green.png");

    public HumveeTowRender(EntityRendererProvider.Context renderManager) {
        super(renderManager, new HumveeTowModel());
    }

    @Override
    public RenderType getRenderType(HumveeTowEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityCutout(this.getTextureLocation(animatable));
    }

    @Override
    public ResourceLocation getTextureLocation(HumveeTowEntity entity) {
        return CACHED_RESOURCE_0;
    }
}


