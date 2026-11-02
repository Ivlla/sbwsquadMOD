package tech.squadmc.squadmcor.client.renderer.entity;

import tech.squadmc.squadmcor.client.performance.OptimizedVehicleRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import tech.squadmc.squadmcor.client.model.Gaz_tigrModel;
import tech.squadmc.squadmcor.entity.Gaz_tigrEntity;



public class Gaz_tigrRender extends OptimizedVehicleRenderer<Gaz_tigrEntity> {
    private static final ResourceLocation CACHED_RESOURCE_0 = new ResourceLocation("squadmc", "textures/entity/gaz_tigrs/gaz_tigr.png");

    public Gaz_tigrRender(EntityRendererProvider.Context renderManager) {
        super(renderManager, new Gaz_tigrModel());
    }

    @Override
    public RenderType getRenderType(Gaz_tigrEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityCutout(this.getTextureLocation(animatable));
    }

    @Override
    public ResourceLocation getTextureLocation(Gaz_tigrEntity entity) {
        return CACHED_RESOURCE_0;
    }
}

