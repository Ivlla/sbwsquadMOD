package tech.squadmc.squadmcor.client.renderer.entity;

import tech.squadmc.squadmcor.client.performance.OptimizedVehicleRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import tech.squadmc.squadmcor.client.model.marder1a3Model;
import tech.squadmc.squadmcor.entity.marder1a3Entity;

public class marder1a3Render extends OptimizedVehicleRenderer<marder1a3Entity> {
    private static final ResourceLocation CACHED_RESOURCE_0 = new ResourceLocation("squadmc", "textures/entity/marder1a3_1.png");

    public marder1a3Render(EntityRendererProvider.Context renderManager) {
        super(renderManager, new marder1a3Model());
    }

    @Override
    public RenderType getRenderType(marder1a3Entity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityCutout(this.getTextureLocation(animatable));
    }

    @Override
    public ResourceLocation getTextureLocation(marder1a3Entity entity) {
        return tech.squadmc.squadmcor.client.skin.VehicleSkinTexture.resolve(entity, CACHED_RESOURCE_0);
    }
}

