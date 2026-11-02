package tech.squadmc.squadmcor.client.renderer.entity;

import tech.squadmc.squadmcor.client.performance.OptimizedVehicleRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import tech.squadmc.squadmcor.client.model.Gaz_tigrRWSModel;
import tech.squadmc.squadmcor.entity.Gaz_tigrRWSEntity;

public class Gaz_tigrRWSRender extends OptimizedVehicleRenderer<Gaz_tigrRWSEntity> {
    private static final ResourceLocation CACHED_RESOURCE_0 = new ResourceLocation("squadmc", "textures/entity/gaz_tigrs/gaz_tigr.png");

    public Gaz_tigrRWSRender(EntityRendererProvider.Context renderManager) {
        super(renderManager, new Gaz_tigrRWSModel());
    }

    @Override
    public RenderType getRenderType(Gaz_tigrRWSEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityCutout(this.getTextureLocation(animatable));
    }

    @Override
    public ResourceLocation getTextureLocation(Gaz_tigrRWSEntity entity) {
        return CACHED_RESOURCE_0;
    }
}
