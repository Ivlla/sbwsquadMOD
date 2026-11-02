package tech.squadmc.squadmcor.client.renderer.entity.empl;

import tech.squadmc.squadmcor.client.performance.OptimizedVehicleRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import tech.squadmc.squadmcor.client.model.empl.ZISModel;
import tech.squadmc.squadmcor.entity.empl.ZISEntity;



public class ZISRender extends OptimizedVehicleRenderer<ZISEntity> {
    private static final ResourceLocation CACHED_RESOURCE_0 = new ResourceLocation("squadmc", "textures/entity/turrets/zis3.png");

    public ZISRender(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ZISModel());
    }

    @Override
    public RenderType getRenderType(ZISEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityCutout(this.getTextureLocation(animatable));
    }

    @Override
    public ResourceLocation getTextureLocation(ZISEntity entity) {
        return CACHED_RESOURCE_0;
    }
}

