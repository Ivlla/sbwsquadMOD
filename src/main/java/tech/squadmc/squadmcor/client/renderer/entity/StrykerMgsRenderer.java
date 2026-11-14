package tech.squadmc.squadmcor.client.renderer.entity;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import tech.squadmc.squadmcor.client.model.StrykerMgsModel;
import tech.squadmc.squadmcor.client.performance.OptimizedVehicleRenderer;
import tech.squadmc.squadmcor.client.skin.VehicleSkinTexture;
import tech.squadmc.squadmcor.entity.StrykerMgsEntity;

public class StrykerMgsRenderer extends OptimizedVehicleRenderer<StrykerMgsEntity> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("squadmc", "textures/entity/stryker.png");

    public StrykerMgsRenderer(EntityRendererProvider.Context context) {
        super(context, new StrykerMgsModel());
    }

    @Override
    public ResourceLocation getTextureLocation(StrykerMgsEntity vehicle) {
        return VehicleSkinTexture.resolve(vehicle, TEXTURE);
    }

    @Override
    public RenderType getRenderType(StrykerMgsEntity vehicle, ResourceLocation texture,
                                  MultiBufferSource buffers, float partialTick) {
        return RenderType.entityCutout(getTextureLocation(vehicle));
    }
}
