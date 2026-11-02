package tech.squadmc.squadmcor.client.renderer.entity.empl;

import tech.squadmc.squadmcor.client.performance.OptimizedVehicleRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import tech.squadmc.squadmcor.client.model.empl.TowModel;
import tech.squadmc.squadmcor.entity.empl.TowEntity;



public class TowRender extends OptimizedVehicleRenderer<TowEntity> {
    private static final ResourceLocation CACHED_RESOURCE_0 = new ResourceLocation("squadmc", "textures/entity/turrets/tow.png");

    public TowRender(EntityRendererProvider.Context renderManager) {
        super(renderManager, new TowModel());
    }

    @Override
    public RenderType getRenderType(TowEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityCutout(this.getTextureLocation(animatable));
    }

    @Override
    public ResourceLocation getTextureLocation(TowEntity entity) {
        return CACHED_RESOURCE_0;
    }
}

