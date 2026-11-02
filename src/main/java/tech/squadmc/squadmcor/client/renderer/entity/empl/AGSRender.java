package tech.squadmc.squadmcor.client.renderer.entity.empl;

import tech.squadmc.squadmcor.client.performance.OptimizedVehicleRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import tech.squadmc.squadmcor.client.model.empl.AGSModel;
import tech.squadmc.squadmcor.entity.empl.AGSEntity;



public class AGSRender extends OptimizedVehicleRenderer<AGSEntity> {
    private static final ResourceLocation CACHED_RESOURCE_0 = new ResourceLocation("squadmc", "textures/entity/turrets/ags17.png");

    public AGSRender(EntityRendererProvider.Context renderManager) {
        super(renderManager, new AGSModel());
    }

    @Override
    public RenderType getRenderType(AGSEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityCutout(this.getTextureLocation(animatable));
    }

    @Override
    public ResourceLocation getTextureLocation(AGSEntity entity) {
        return CACHED_RESOURCE_0;
    }
}

