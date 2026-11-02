package tech.squadmc.squadmcor.client.renderer.entity;

import tech.squadmc.squadmcor.client.performance.OptimizedVehicleRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import tech.squadmc.squadmcor.client.model.ToyotaModel;
import tech.squadmc.squadmcor.entity.ToyotaEntity;

public class ToyotaRender extends OptimizedVehicleRenderer<ToyotaEntity> {
    private static final ResourceLocation CACHED_RESOURCE_0 = new ResourceLocation("squadmc", "textures/entity/toyotas/toyota_hilux.png");

    public ToyotaRender(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ToyotaModel());
    }

    @Override
    public RenderType getRenderType(ToyotaEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityCutout(this.getTextureLocation(animatable));
    }

    @Override
    public ResourceLocation getTextureLocation(ToyotaEntity entity) {
        return CACHED_RESOURCE_0;
    }
}
