package tech.squadmc.squadmcor.client.renderer.entity;

import tech.squadmc.squadmcor.client.performance.OptimizedVehicleRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import tech.squadmc.squadmcor.client.model.Gaz_tigrMGModel;
import tech.squadmc.squadmcor.entity.Gaz_tigrMGEntity;



public class Gaz_tigrMGRender extends OptimizedVehicleRenderer<Gaz_tigrMGEntity> {
    private static final ResourceLocation CACHED_RESOURCE_0 = new ResourceLocation("squadmc", "textures/entity/gaz_tigrs/gaz_tigr.png");

    public Gaz_tigrMGRender(EntityRendererProvider.Context renderManager) {
        super(renderManager, new Gaz_tigrMGModel());
    }

    @Override
    public RenderType getRenderType(Gaz_tigrMGEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityCutout(this.getTextureLocation(animatable));
    }

    @Override
    public ResourceLocation getTextureLocation(Gaz_tigrMGEntity entity) {
        return CACHED_RESOURCE_0;
    }
}

